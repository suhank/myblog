[TOC]



# Spring基础学习-SpringMVC异步处理模式分析(DeferredResult/SseEmitter等）

# 1. 背景

Tomcat等应用服务器的连接线程池实际上是有限制的；每一个连接请求都会耗掉线程池的一个连接数；如果某些耗时很长的操作，如对大量数据的查询操作、调用外部系统提供的服务以及一些IO密集型操作等，会占用连接很长时间，这个时候这个连接就无法被释放而被其它请求重用。如果连接占用过多，服务器就很可能无法及时响应每个请求；极端情况下如果将线程池中的所有连接耗尽，服务器将长时间无法向外提供服务！

在常规场景中，客户端需要等待服务器处理完毕后返回才能继续进行其它操作，这个场景下每一步都是同步调用，如客户端调用Servlet后需要等待其处理返回，Servlet调用具体的Controller后也需要等待其返回。这种情况是在服务器端开发中最常见的场景，适合于服务器端处理时间不是很长的情况；默认情况下Spring的Controller提供的就是这样的服务。

当某项服务处理时间过长时，如邮件发送，需要调用到外部接口，处理时间不受调用方的控制，因此如果耗时过长会有两个比较严重的后果：一是如上文所说的会长时间的占用请求连接数，严重时有可能导致服务器失去响应； 二是客户端等待时间过长，导致前端应用的用户友好性下降，而且客户很有可能因为长时间得不到服务器响应而重复操作，从而加重服务器的负担，使得应用崩溃的机率变大！ 
为应对这种场景，一般会启用一个后台的线程池，处理请求的Controller会先提交一个耗时长操作如邮件发送到线程池中，然后立即返回到前台。因此处理响应的主线程耗时变短，客户感受到的就是在点击某个发送按钮后很快就得到服务器反馈结果，然后就放心的继续处理其它工作。实际上邮件发送这种事情延迟几秒对于客户来说根本感受不到。当然应用需要保证提交到线程池中的任务执行成功，或者是执行失败后在前端某个地方能够看到失败的具体情况。

这种场景在Spring中可使用TaskExecutor或者是Async来处理，关于它们的用法请参考：[Spring基础学习-任务执行（TaskExecutor及Async）](http://blog.csdn.net/icarusliu/article/details/79528810)

通过以上两种场景，很容易就会想到，如果某个操作既耗时很长，客户端又必须要等待其返回才能进一步处理时，应该通过什么方式来处理？Servlet3.0中引入异步请求处理来处理这种场景，相应的，Spring在3.2版本中就引入相关机制来使用Servlet的该特性。

# 2. SpringMVC异步处理概述

为满足耗时任务占用应用服务器连接数，而客户端又必须等待这些耗时长任务返回才能处理下一步工作的场景，Spring引入了以下机制来处理：

- 使用Callable或者DeferredResult当成Controller的返回值，能够处理异步返回单个结果的场景
- 使用ResponseBodyEmitter/SseEmitter或者StreamingResponseBody来流式处理多个返回值
- 在Controller中使用响应式客户端调用服务并返回响应式的数据对象

## 2.1 Callable

Callable直接使用在Controller中被RequestMapping所注解的方法上，做为其返回对象。 
使用示例：

```java
@RequestMapping("/testCallable")
public Callable<String> testCallable() {
    logger.info("Controller开始执行！");
    Callable<String> callable = () -> {
        Thread.sleep(5000);

        logger.info("实际工作执行完成！");

        return "succeed!";
    };
    logger.info("Controller执行结束！");
    return callable;
}
```

使用浏览器访问<http://localhost/test/testCallable>, 结果如下：

```java
2018-03-12 22:38:05.547  INFO 4980 --- [p-nio-80-exec-2] c.l.t.b.e.controllers.TestController     : Controller开始执行！
2018-03-12 22:38:05.553  INFO 4980 --- [p-nio-80-exec-2] c.l.t.b.e.controllers.TestController     : Controller执行结束！
2018-03-12 22:38:10.560  INFO 4980 --- [      MvcAsync1] c.l.t.b.e.controllers.TestController     : 实际工作执行完成！123
```

可以看到以下结果：

- 浏览器等待了大约5秒后返回结果
- 打印日志中，Controller在6ms就执行结束
- 打印日志中，实际的任务执行在一个名称为MvcAsync1的线程中执行，并且在Controller执行完5s后才执行结束

因此可以得到结论：

> 返回Callable对象时，实际工作线程会在后台处理，Controller无需等待工作线程处理完成，但Spring会在工作线程处理完毕后才返回客户端。 
> 它的执行流程是这样的：　

- 客户端请求服务
- SpringMVC调用Controller，Controller返回一个Callback对象
- SpringMVC调用ruquest.startAsync并且将Callback提交到TaskExecutor中去执行
- DispatcherServlet以及Filters等从应用服务器线程中结束，但Response仍旧是打开状态，也就是说暂时还不返回给客户端
- TaskExecutor调用Callback返回一个结果，SpringMVC将请求发送给应用服务器继续处理
- DispatcherServlet再次被调用并且继续处理Callback返回的对象，最终将其返回给客户端

## 2.2 DeferredResult

DeferredResult使用方式与Callable类似，但在返回结果上不一样，它返回的时候实际结果可能没有生成，实际的结果可能会在另外的线程里面设置到DeferredResult中去。 
该类包含以下日常使用相关的特性：

- 超时配置：通过构造函数可以传入超时时间，单位为毫秒；因为需要等待设置结果后才能继续处理并返回客户端，如果一直等待会导致客户端一直无响应，因此必须有相应的超时机制来避免这个问题；实际上就算不设置这个超时时间，应用服务器或者Spring也会有一些默认的超时机制来处理这个问题。
- 结果设置：它的结果存储在一个名称为result的属性中；可以通过调用setResult的方法来设置属性；由于这个DeferredResult天生就是使用在多线程环境中的，因此对这个result属性的读写是有加锁的。

接下来将对DeferredResult的处理流程进行说明，并实现一个较为简单的示例。

### 2.2.1 DeferredResult处理流程

DeferredResult的处理过程与Callback类似，不一样的地方在于它的结果不是DeferredResult直接返回的，而是由其它线程通过同步的方式设置到该对象中。它的执行过程如下所示：

- 客户端请求服务
- SpringMVC调用Controller，Controller返回一个DeferredResult对象
- SpringMVC调用ruquest.startAsync
- DispatcherServlet以及Filters等从应用服务器线程中结束，但Response仍旧是打开状态，也就是说暂时还不返回给客户端
- 某些其它线程将结果设置到DeferredResult中，SpringMVC将请求发送给应用服务器继续处理
- DispatcherServlet再次被调用并且继续处理DeferredResult中的结果，最终将其返回给客户端

### 2.2.2 DeferredResult使用示例

本示例将在一个Controller中添加两个RequestMapping注解的方法。其中一个返回的是DeferredResult的对象，另外一个设置这个对象的值。

```java
@RestController
@RequestMapping("/test")
public class TestController {
    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    @Autowired
    private AsyncService asyncService;

    private DeferredResult<String> deferredResult = new DeferredResult<>();

     /**
     * 返回DeferredResult对象
     *
     * @return
     */
    @RequestMapping("/testDeferredResult")
    public DeferredResult<String> testDeferredResult() {
        return deferredResult;
    }

    /**
     * 对DeferredResult的结果进行设置
     * @return
     */
    @RequestMapping("/setDeferredResult")
    public String setDeferredResult() {
        deferredResult.setResult("Test result!");
        return "succeed";
    }
} 
```

第一步先访问：<http://localhost/test/testDeferredResult> 
此时客户端将会一直等待，直到一定时长后会超时 
第二步再新开页面访问：<http://localhost/test/setDeferredResult> 
此时第一个页面会返回结果。

## 2.3 SseEmitter

Callback和DeferredResult用于设置单个结果，如果有多个结果需要返回给客户端时，可以使用SseEmitter以及ResponseBodyEmitter等； 
下面直接看示例，与DeferredResult的示例类似：

```java
@RestController
@RequestMapping("/test")
public class TestController {
    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    @Autowired
    private AsyncService asyncService;

    private DeferredResult<String> deferredResult = new DeferredResult<>();

    private SseEmitter sseEmitter = new SseEmitter();

    /**
     * 返回SseEmitter对象  
     * 
     * @return
     */
    @RequestMapping("/testSseEmitter")
    public SseEmitter testSseEmitter() {
        return sseEmitter;
    }

    /**
     * 向SseEmitter对象发送数据  
     * 
     * @return
     */
    @RequestMapping("/setSseEmitter")
    public String setSseEmitter() {
        try {
            sseEmitter.send(System.currentTimeMillis());
        } catch (IOException e) {
            logger.error("IOException!", e);
            return "error";  
        }

        return "Succeed!"; 
    }

     /**
     * 将SseEmitter对象设置成完成
     *
     * @return
     */
    @RequestMapping("/completeSseEmitter")
    public String completeSseEmitter() {
        sseEmitter.complete();

        return "Succeed!";
    }
} 
```

第一步访问：<http://localhost/test/testSseEmitter> 
第二步连续访问：<http://localhost/test/setSseEmitter> 
第三步访问：<http://localhost/test/completeSseEmitter> 
可以看到结果，只有当第三步执行后，第一步的访问才算结束。

## 2.4 StreamingResponseBody

用于直接将结果写出到Response的OutputStream中； 如文件下载等，示例：

```java
@GetMapping("/download")
public StreamingResponseBody handle() {
    return new StreamingResponseBody() {
        @Override
        public void writeTo(OutputStream outputStream) throws IOException {
            // write...
        }
    };
}
```

# 3 异步处理拦截器

在进行异步处理时，可以使用CallableProcessingInterceptor来对Callback返回参数的情况进行拦截，也可以使用DeferredResultProcessingInterceptor来对DeferredResult的情况进行拦截。 也可以直接使用AsyncHandlerInterceptor 。 
拦截器的使用与普通拦截器并无不一样的，因此此处不再展开。具体可以参考： [Spring Boot拦截器示例及源码原理分析](http://blog.csdn.net/icarusliu/article/details/78833520)

# 参考资料

> 1. <https://spring.io/blog/2012/05/07/spring-mvc-3-2-preview-introducing-servlet-3-async-support>



https://blog.csdn.net/icarusliu/article/details/79539105