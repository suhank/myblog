# FeignClient与RestTemplate的区别比较简单研究

题外：个人觉得可能还没达到那种境界，还体会不到真正的实质性区别，就好比用HttpClient可以实现的用FeignClient同样可以实现，反之也是。

JAVA 项目中接口调用怎么做 ？

- Httpclient
- Okhttp
- Httpurlconnection
- RestTemplate

上面是最常见的几种用法，我们今天要介绍的用法比上面的更简单，方便，它就是[Feign](https://github.com/OpenFeign/feign)

Feign是一个声明式的REST客户端，它的目的就是让REST调用更加简单。

Feign提供了HTTP请求的模板，通过编写简单的接口和插入注解，就可以定义好HTTP请求的参数、格式、地址等信息。

而Feign则会完全代理HTTP请求，我们只需要像调用方法一样调用它就可以完成服务请求及相关处理。

SpringCloud对Feign进行了封装，使其支持SpringMVC标准注解和HttpMessageConverters。

Feign可以与Eureka和Ribbon组合使用以支持负载均衡。

区别：

在预订微服务中，有一个同步呼叫票价（Fare）。`RestTemplate`是用来制作的同步呼叫。使用`RestTemplate时`，URL参数是以编程方式构造的，数据被发送到其他服务。在更复杂的情况下，我们将不得不`RestTemplate`深入到更低级别的API提供的甚至是API 的细节。

Feign是Spring Cloud Netflix库，用于在基于REST的服务调用上提供更高级别的抽象。Spring Cloud Feign在声明性原则上工作。使用Feign时，我们在客户端编写声明式REST服务接口，并使用这些接口来编写客户端程序。开发人员不用担心这个接口的实现。这将在运行时由Spring动态配置。通过这种声明性的方法，开发人员不需要深入了解由HTTP提供的HTTP级别API的细节的`RestTemplate`。

还有一点返回异常处理默认Feign能否返回原始的异常,RestTemplate默认只返回null

总结：

也就是说FeignClient简化了请求的编写，且通过动态负载进行选择要使用哪个服务进行消费，而这一切都由Spring动态配置实现，我们不用关心这些，只管使用方法即可。再说，就是简化了编写，RestTemplate还需要写上服务器IP这些信息等等，而FeignClient则不用。

不过话又再说回来，其实RestTemplate同样可以简化到使用FeignClient那样简单，无非就是自己封装多一层去实现而已，所以，我个人觉得没有太多绝对，只是看你的业务需求怎么定位这个选择而已。



 

参考：

<http://blog.csdn.net/u010889990/article/details/78673273>

<https://segmentfault.com/a/1190000009229438>

<https://www.packtpub.com/mapt/book/application_development/9781786466686/5/ch05lvl1sec57/feign-as-a-declarative-rest-client>

<https://www.jianshu.com/p/3d597e9d2d67>

<http://www.cnblogs.com/duanxz/p/7516676.html>

<https://www.cnblogs.com/duanxz/p/3510622.html>

<https://www.jianshu.com/p/270f3cd658f1>

<https://zhuanlan.zhihu.com/p/26400247>







https://www.cnblogs.com/EasonJim/p/8321355.html