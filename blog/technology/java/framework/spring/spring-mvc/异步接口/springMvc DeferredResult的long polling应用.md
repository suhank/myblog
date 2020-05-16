[TOC]



# springMvc DeferredResult的long polling应用

## **1.了解servlet以及spring mvc中的异步？**

Spring MVC 3.2开始引入了基于Servlet 3的异步请求处理。相比以前，控制器方法已经不一定需要返回一个值，而是可以返回一个java.util.concurrent.Callable的对象，并通过Spring MVC所管理的线程来产生返回值。与此同时，Servlet容器的主线程则可以退出并释放其资源了，同时也允许容器去处理其他的请求。通过一个TaskExecutor，Spring MVC可以在另外的线程中调用Callable。当Callable返回时，请求再携带Callable返回的值，再次被分配到Servlet容器中恢复处理流程。以下代码给出了一个这样的控制器方法作为例子：

```js
@RequestMapping(method=RequestMethod.POST)
public CallableprocessUpload(final MultipartFile file) {

    return new Callable() {
        public String call() throws Exception {
            // ...
            return "someView";
        }
    };
}
```

另一个选择，是让控制器方法返回一个DeferredResult的实例。这种场景下，返回值可以由任何一个线程产生，也包括那些不是由Spring MVC管理的线程。举个例子，返回值可能是为了响应某些外部事件所产生的，比如一条JMS的消息，一个计划任务，等等。以下代码给出了一个这样的控制器作为例子：

```js
@RequestMapping("/quotes")
@ResponseBody
public DeferredResultquotes() {
    DeferredResultdeferredResult = new DeferredResult();
    // Save the deferredResult somewhere..
    return deferredResult;
}

// In some other thread...
deferredResult.setResult(data);
```

如果对Servlet 3.0的异步请求处理特性没有了解，理解这个特性可能会有点困难。因此，阅读一下前者的文档将会很有帮助。

以下给出了这个机制运作背后的一些原理：

一个servlet请求ServletRequest可以通过调用request.startAsync()方法而进入异步模式。这样做的主要结果就是该servlet以及所有的过滤器都可以结束，但其响应（response）会留待异步处理结束后再返回调用request.startAsync()方法会返回一个AsyncContext对象，可用它对异步处理进行进一步的控制和操作。比如说它也提供了一个与转向（forward）很相似的dispatch方法，只不过它允许应用恢复Servlet容器的请求处理进程ServletRequest提供了获取当前DispatherType的方式，后者可以用来区别当前处理的是原始请求、异步分发请求、转向，或是其他类型的请求分发类型。

有了上面的知识，下面可以来看一下Callable的异步请求被处理时所依次发生的事件：

1. 控制器先返回一个Callable对象
2. Spring MVC开始进行异步处理，并把该Callable对象提交给另一个独立线程的执行器TaskExecutor处理
3. DispatcherServlet和所有过滤器都退出Servlet容器线程，但此时方法的响应对象仍未返回
4. Callable对象最终产生一个返回结果，此时Spring MVC会重新把请求分派回Servlet容器，恢复处理
5. DispatcherServlet再次被调用，恢复对Callable异步处理所返回结果的处理
6. 对DeferredResult异步请求的处理顺序也非常类似，区别仅在于应用可以通过任何线程来计算返回一个结果：
7. 控制器先返回一个DeferredResult对象，并把它存取在内存（队列或列表等）中以便存取
8. Spring MVC开始进行异步处理
9. DispatcherServlet和所有过滤器都退出Servlet容器线程，但此时方法的响应对象仍未返回
10. 由处理该请求的线程对 DeferredResult进行设值，然后Spring MVC会重新把请求分派回Servlet容器，恢复处理
11. DispatcherServlet再次被调用，恢复对该异步返回结果的处理

## 2.简述polling和long polling的区别？

这里暂抛开某些场景webSocket的解决方案。

举一个生活中的列子来说明长轮询比轮询好在哪里：电商云集的时代，大家肯定都有查询快递的经历，怎么最快知道快递的进度呢？polling和long polling的方式分别如下：

- polling：如果我想在两分钟内看到快递的变化，那么，轮询会每隔两分钟去像服务器发起一次快递变更的查询请求，如果快递其实是一个小时变更一次，那么polling的方式在获取一次真实有效信息时需要发起30次
- long polling：首先发起查询请求，服务端没有更新的话就不回复，直到一个小时变更时才将结果返回给客户，然后客户发起下次查询请求。长轮询保证了每次发起的查询请求都是有效的，极大的减少了与服务端的交互，基于web异步处理技术，大大的提升了服务性能

如果在发散的触类旁通一下，long polling的方式和发布订阅的模式有点类似之处，只是每次拿到了发布的结果之后需要再次发起消息订阅

## 3.因为DeferredResult，所以long polling？

因为DeferredResult技术，所以使得long polling不会一直占用容器资源，使得长轮询成为可能。长轮询的应用有很多，简述下就是：需要及时知道某些消息的变更的场景都可以用长轮询来解决，当然，你可能又想起了发布订阅了，哈哈

- 比如：在线聊天？一个服务端，多个客户端，服务端管理所有的人的消息，客户端向服务端发起给自己的消息的请求，服务端处理后给返回，然后客户端再次发起？
- 在比如类发布订阅的例子：配置中心服务，当配置中心的配置变更好，相关的客户端程序需要及时更新最新的配置。disconf就是基于zookeeper的发布订阅来做的，apollo就是采用的DeferredResult的long polling来做的，客户端发起长轮询，配置中心监听器监听到配置变更后，将结果响应给客户端。apollo的具体做法可见服务端：[com/ctrip/framework/apollo/configservice/controller/NotificationControllerV2.java](https://github.com/ctripcorp/apollo/blob/c62f807e7e487ffc9c713a4eb751123e331a9902/apollo-configservice/src/main/java/com/ctrip/framework/apollo/configservice/controller/NotificationControllerV2.java) 客户端：[com/ctrip/framework/apollo/internals/RemoteConfigLongPollService.java](https://github.com/ctripcorp/apollo/blob/c62f807e7e487ffc9c713a4eb751123e331a9902/apollo-client/src/main/java/com/ctrip/framework/apollo/internals/RemoteConfigLongPollService.java)



## **4.简单的测试用例？**

多个请求的结果，使用另一个请求控制他的响应返回。本实例构建在spring boot 1.5.7上。

**1.定义异步接口**

```js
/**
 * Created by kl on 2017/9/27.
 * Content :
 */
@RestController
@RequestMapping("/async")
public class AsyncController {
    final Map deferredResultMap=new ConcurrentReferenceHashMap<>();
    @GetMapping("/longPolling")
    public DeferredResultlongPolling(){
        DeferredResultdeferredResult=new DeferredResult(0L);
        deferredResultMap.put(deferredResult.hashCode(),deferredResult);
        deferredResult.onCompletion(()->{
            deferredResultMap.remove(deferredResult.hashCode());
            System.err.println("还剩"+deferredResultMap.size()+"个deferredResult未响应");
        });
        return deferredResult;
    }
    @GetMapping("/returnLongPollingValue")
    public void returnLongPollingValue(){
        for (Map.Entry entry:deferredResultMap.entrySet()){
            entry.getValue().setResult("kl");
        }
    }
}
```

**2.定义接口访问实例，使用fegin**

```js
/**
 * Created by kl on 2017/9/27.
 * Content :
 */
@FeignClient(url = "localhost:8976",name = "async")
public interface AsyncFeginService {

    @GetMapping("/async/longPolling")
    String longPolling();

    @GetMapping("/async/returnLongPollingValue")
     void returnLongPollingValue();
}
```

3.测试用例

```js
@RunWith(SpringRunner.class)
@SpringBootTest
public class LongPollingdemoApplicationTests {
	@Autowired
	AsyncFeginService asyncFeginService;
	/**
	 * 模拟多个浏览器客户端发起长轮询请求，等待testLongPolling测试用例请求通知服务端返回各浏览器的请求结果
	 * @throws Exception
	 */
	@Test
	public void contextLoads() throws Exception{
		ExecutorService executorService=Executors.newFixedThreadPool(4);
		for (int i=0;i<=3;i++){
			executorService.execute(()->{
				String kl=asyncFeginService.longPolling();
				System.err.println("收到响应："+kl);
			});
		}
		System.in.read();
	}
	/**
	 * 通知服务端返回上个测试的长轮询结果
	 */
	@Test
	public void testLongPolling(){
		asyncFeginService.returnLongPollingValue();
	}
}
```

测试时，先启动contextLoads会发起四个异步请求，一直等待请求结果响应，直到testLongPolling通知服务端返回deferredResult的值





http://www.kailing.pub/article/index/arcid/163.html