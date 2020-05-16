[TOC]



# tomcat servlet3的异步化原理理解

之所以想起来扒这个servlet 3的异化步原理，原因是前几天写了一篇关于[消息队列的pull与push模式理解](http://blog.csdn.net/zhurhyme/article/details/75670200).从对spring jms消息队列的客户端实现来看activemq服务端并没有真正的实现push.所以想搞清楚tomcat是如何实现servlet3的异步化。

## tomcat如何处理请求

从客户端发出一次请求至tomcat容器大致经过如下过程：

1. 客户端发出http请求至tomcat的连接监听端口；
2. tomcat connector接收线程接收请求，并根据http协议解析该次请求；
3. tomcat 通过解析的http报文，初始化org.apache.coyote.Request，并实例化org.apache.coyote.Response;
4. 经装饰模式转化为servlet api对应的HttpServletRequest与HttpServletReponse;
5. 经tomcat的层层容器engine,host,context最终到过我们所写的业务servlet的service方法；
6. 业务方法service,处理相关的业务逻辑,写入相应的响应的至response，并返回tomat的容器组件；
7. tomcat该处理线程关闭响应流Response并将响应内容返回客户端;
8. tomcat该处理线程被释放，然后用于下次请求的处理;

上面的过程只是粗略的描述,如果需要更详细的则采用如下方法：

- 阅读tomcat源代码；
- 阅读How tomcat work,再结合上面的方法；
- 自己思考，实验，再结合上面两方法；
- 度娘，再结合上面所有方法；
- 上面的方法难度依次递减；

## servlet3异步化

从上面tomcat处理请求过程可知， tomcat处理线程一直被占用，直至业务方法处理完毕;那么servlet3是如何进行异步化的呢？使用servlet3的异步的一般姿势：

```
    AsyncContext context = request.startAsync(request, response); //第一行
    //省略相关的业务处理代码
    context.complete(); //第三行
```

当然在使用上面的代码之前，不要忘了开启servlet的异步化(使用注解也可以):

```
<filter>
    <filter-name>requestFilter</filter-name>
    <filter-class>***</filter-class>
    <async-supported>true</async-supported>
</filter> 
```

我们在业务方法中，使用如上的“第一行”代码达到如下目的：

1. 将tomcat的该次的处理线程进行提前释放；
2. 也正是由于启动了该AsyncContext，使得tomcat的处理线程在释放的过程中没有关闭响应(response)流;使得我们在业务代码中才能进行后续处理；

提前释放tomcat的处理线程，是为了让tomcat的线程使用率更高，提高吞吐量；不关闭响应流为的是我们在业务中处理了占用长时间的业务操作之后，自己进行响应流的返回并进行关闭，这正是上面“第三行”代码作的事情。

所以tomat结合servlet3异步化的整体请求处理过程大致如下：

1. 客户端发出http请求至tomcat的连接监听端口；
2. tomcat connector接收线程接收请求，并根据http协议解析该次请求；
3. tomcat 通过解析的http报文，实例化org.apache.coyote.Request，并实例化org.apache.coyote.Response;
4. 经装饰模式转化为servlet api对应的HttpServletRequest与HttpServletReponse;
5. 经tomcat的层层容器engine,host,context最终到过我们所写的业务servlet的service方法；
6. 业务方法开启异步化上下文AsynContext;释放tomcat当前处理线程；
7. tomcat判断当前请求是否开启了异步化，如果开启则不关闭响应流Response，也不进行用户响应的返回;
8. tomcat该线程被释放，然后用于下次请求的处理，提高其吞吐量;
9. 业务方法在AsynContext环境中完成业务方法的处理，调用其complete方法，将响应写回响应流，并关闭响应流，完成此次请求处理.

所以用一句话总结servlet3的基本原理就是：网络连接依旧在，提前释放tomcat处理线程用于提高吞吐量，响应流不关闭，由业务方法自己处理。从这个角度来看基于servlet3的异步化完全有可能实现真正的服务端push。



https://blog.csdn.net/zhurhyme/article/details/76228836