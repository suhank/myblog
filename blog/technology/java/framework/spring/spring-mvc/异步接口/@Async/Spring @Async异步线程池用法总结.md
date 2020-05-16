[TOC]



# Spring @Async异步线程池用法总结

## 1. TaskExecutor

Spring异步线程池的接口类，其实质是java.util.concurrent.Executor

Spring 已经实现的异常线程池： 
\1. SimpleAsyncTaskExecutor：不是真的线程池，这个类不重用线程，每次调用都会创建一个新的线程。 
\2. SyncTaskExecutor：这个类没有实现异步调用，只是一个同步操作。只适用于不需要多线程的地方 
\3. ConcurrentTaskExecutor：Executor的适配类，不推荐使用。如果ThreadPoolTaskExecutor不满足要求时，才用考虑使用这个类 
\4. SimpleThreadPoolTaskExecutor：是Quartz的SimpleThreadPool的类。线程池同时被quartz和非quartz使用，才需要使用此类 
\5. ThreadPoolTaskExecutor ：最常使用，推荐。 其实质是对java.util.concurrent.ThreadPoolExecutor的包装

## 2. @Async

spring对过@Async定义异步任务

异步的方法有3种 

1. 最简单的异步调用，返回值为void 
2. 带参数的异步调用 异步方法可以传入参数 
3. 异常调用返回Future

详细见代码：

```java
@Component
public class AsyncDemo {
    private static final Logger log = LoggerFactory.getLogger(AsyncDemo.class);

    /**
     * 最简单的异步调用，返回值为void
     */
    @Async
    public void asyncInvokeSimplest() {
        log.info("asyncSimplest");
    }

    /**
     * 带参数的异步调用 异步方法可以传入参数
     * 
     * @param s
     */
    @Async
    public void asyncInvokeWithParameter(String s) {
        log.info("asyncInvokeWithParameter, parementer={}", s);
    }

    /**
     * 异常调用返回Future
     * 
     * @param i
     * @return
     */
    @Async
    public Future<String> asyncInvokeReturnFuture(int i) {
        log.info("asyncInvokeReturnFuture, parementer={}", i);
        Future<String> future;
        try {
            Thread.sleep(1000 * 1);
            future = new AsyncResult<String>("success:" + i);
        } catch (InterruptedException e) {
            future = new AsyncResult<String>("error");
        }
        return future;
    }

} 
```

以上的异步方法和普通的方法调用相同

```java
asyncDemo.asyncInvokeSimplest();
asyncDemo.asyncInvokeWithException("test");
Future<String> future = asyncDemo.asyncInvokeReturnFuture(100);
System.out.println(future.get()); 
```

## 3. Spring 开启异步配置

Spring有两种方法启动配置 
\1. 注解 
\2. XML

### 3.1 通过注解实现

要启动异常方法还需要以下配置 
\1. @EnableAsync 此注解开户异步调用功能 
\2. public AsyncTaskExecutor taskExecutor() 方法自定义自己的线程池，线程池前缀”Anno-Executor”。如果不定义，则使用系统默认的线程池。

```java
@SpringBootApplication
@EnableAsync // 启动异步调用
public class AsyncApplicationWithAnnotation {
    private static final Logger log = LoggerFactory.getLogger(AsyncApplicationWithAnnotation.class);

    /**
     * 自定义异步线程池
     * @return
     */
    @Bean
    public AsyncTaskExecutor taskExecutor() {  
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor(); 
        executor.setThreadNamePrefix("Anno-Executor");
        executor.setMaxPoolSize(10);  

        // 设置拒绝策略
        executor.setRejectedExecutionHandler(new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                // .....
            }
        });
        // 使用预定义的异常处理类
        // executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        return executor;  
    } 

    public static void main(String[] args) {
        log.info("Start AsyncApplication.. ");
        SpringApplication.run(AsyncApplicationWithAnnotation.class, args);
    }
}
 
```

以上的异常方法和普通的方法调用相同

```java
@RunWith(SpringRunner.class)
@SpringBootTest(classes=AsyncApplicationWithAnnotation.class)
public class AsyncApplicationWithAnnotationTests {
    @Autowired
    private AsyncDemo asyncDemo;

    @Test
    public void contextLoads() throws InterruptedException, ExecutionException {
        asyncDemo.asyncInvokeSimplest();
        asyncDemo.asyncInvokeWithParameter("test");
        Future<String> future = asyncDemo.asyncInvokeReturnFuture(100);
        System.out.println(future.get());
    }
}
 
```

执行测试用例，输出内容如下： 
可以看出主线程的名称为main; 异步方法则使用 Anno-Executor1，可见异常线程池起作用了

```
2017-03-28 20:00:07.731  INFO 5144 --- [ Anno-Executor1] c.hry.spring.async.annotation.AsyncDemo  : asyncSimplest
2017-03-28 20:00:07.732  INFO 5144 --- [ Anno-Executor1] c.hry.spring.async.annotation.AsyncDemo  : asyncInvokeWithParameter, parementer=test
2017-03-28 20:00:07.751  INFO 5144 --- [ Anno-Executor1] c.hry.spring.async.annotation.AsyncDemo  : asyncInvokeReturnFuture, parementer=100
success:100
2017-03-28 20:00:08.757  INFO 5144 --- [       Thread-2] s.c.a.AnnotationConfigApplicationContext : Closing org.springframework.context.annotation.AnnotationConfigApplicationContext@47af7f3d: startup date [Tue Mar 28 20:00:06 CST 2017]; root of context hierarchy
 
```

### 3.2 通过XML实现

Bean文件配置: spring_async.xml 
\1. 线程的前缀为xmlExecutor 
\2. 启动异步线程池配置

```
    <!-- 等价于 @EnableAsync， executor指定线程池 -->
    <task:annotation-driven executor="xmlExecutor"/>
    <!-- id指定线程池产生线程名称的前缀 -->
    <task:executor
        id="xmlExecutor"
        pool-size="5-25"
        queue-capacity="100"
        keep-alive="120"
        rejection-policy="CALLER_RUNS"/>123456789
```

线程池参数说明 
\1. ‘id’ ： 线程的名称的前缀 
\2. ‘pool-size’：线程池的大小。支持范围”min-max”和固定值（此时线程池core和max sizes相同） 
\3. ‘queue-capacity’ ：排队队列长度 
○ The main idea is that when a task is submitted, the executor will first try to use a free thread if the number of active threads is currently less than the core size. 
○ If the core size has been reached, then the task will be added to the queue as long as its capacity has not yet been reached. 
○ Only then, if the queue’s capacity has been reached, will the executor create a new thread beyond the core size. 
○ If the max size has also been reached, then the executor will reject the task. 
○ By default, the queue is unbounded, but this is rarely the desired configuration because it can lead to OutOfMemoryErrors if enough tasks are added to that queue while all pool threads are busy. 
\4. ‘rejection-policy’: 对拒绝的任务处理策略 
○ In the default ThreadPoolExecutor.AbortPolicy, the handler throws a runtime RejectedExecutionException upon rejection. 
○ In ThreadPoolExecutor.CallerRunsPolicy, the thread that invokes execute itself runs the task. This provides a simple feedback control mechanism that will slow down the rate that new tasks are submitted. 
○ In ThreadPoolExecutor.DiscardPolicy, a task that cannot be executed is simply dropped. 
○ In ThreadPoolExecutor.DiscardOldestPolicy, if the executor is not shut down, the task at the head of the work queue is dropped, and then execution is retried (which can fail again, causing this to be repeated.) 
\5. ‘keep-alive’ ： 线程保活时间（单位秒） 
setting determines the time limit (in seconds) for which threads may remain idle before being terminated. If there are more than the core number of threads currently in the pool, after waiting this amount of time without processing a task, excess threads will get terminated. A time value of zero will cause excess threads to terminate immediately after executing a task without remaining follow-up work in the task queue()

异步线程池

```java
@SpringBootApplication
@ImportResource("classpath:/async/spring_async.xml")
public class AsyncApplicationWithXML {
    private static final Logger log = LoggerFactory.getLogger(AsyncApplicationWithXML.class);

    public static void main(String[] args) {
        log.info("Start AsyncApplication.. ");
        SpringApplication.run(AsyncApplicationWithXML.class, args);
    }
} 
```

测试用例

```java
@RunWith(SpringRunner.class)
@SpringBootTest(classes=AsyncApplicationWithXML.class)
public class AsyncApplicationWithXMLTest {
    @Autowired
    private AsyncDemo asyncDemo;

    @Test
    public void contextLoads() throws InterruptedException, ExecutionException {
        asyncDemo.asyncInvokeSimplest();
        asyncDemo.asyncInvokeWithParameter("test");
        Future<String> future = asyncDemo.asyncInvokeReturnFuture(100);
        System.out.println(future.get());
    }
}
 
```

运行测试用例，输出内容如下： 
可以看出主线程的名称为main; 异步方法则使用 xmlExecutor-x，可见异常线程池起作用了

```
2017-03-28 20:12:10.540  INFO 12948 --- [           main] c.h.s.a.xml.AsyncApplicationWithXMLTest  : Started AsyncApplicationWithXMLTest in 1.441 seconds (JVM running for 2.201)
2017-03-28 20:12:10.718  INFO 12948 --- [  xmlExecutor-2] com.hry.spring.async.xml.AsyncDemo       : asyncInvokeWithParameter, parementer=test
2017-03-28 20:12:10.721  INFO 12948 --- [  xmlExecutor-1] com.hry.spring.async.xml.AsyncDemo       : asyncSimplest
2017-03-28 20:12:10.722  INFO 12948 --- [  xmlExecutor-3] com.hry.spring.async.xml.AsyncDemo       : asyncInvokeReturnFuture, parementer=100
success:100
2017-03-28 20:12:11.729  INFO 12948 --- [       Thread-2] s.c.a.AnnotationConfigApplicationContext : Closing org.springframework.context.annotation.AnnotationConfigApplicationContext@71809907: startup date [Tue Mar 28 20:12:09 CST 2017]; root of context hierarchy
1234567
```

## 4. 对异步方法的异常处理

在调用方法时，可能出现方法中抛出异常的情况。在异步中主要有有两种异常处理方法： 
\1. 对于方法返回值是Futrue的异步方法: a) 一种是在调用future的get时捕获异常; b) 在异常方法中直接捕获异常 
\2. 对于返回值是void的异步方法：通过AsyncUncaughtExceptionHandler处理异常

AsyncExceptionDemo：

```java
@Component
public class AsyncExceptionDemo {
    private static final Logger log = LoggerFactory.getLogger(AsyncExceptionDemo.class);

    /**
     * 最简单的异步调用，返回值为void
     */
    @Async
    public void asyncInvokeSimplest() {
        log.info("asyncSimplest");
    }

    /**
     * 带参数的异步调用 异步方法可以传入参数
     *  对于返回值是void，异常会被AsyncUncaughtExceptionHandler处理掉
     * @param s
     */
    @Async
    public void asyncInvokeWithException(String s) {
        log.info("asyncInvokeWithParameter, parementer={}", s);
        throw new IllegalArgumentException(s);
    }

    /**
     * 异常调用返回Future
     *  对于返回值是Future，不会被AsyncUncaughtExceptionHandler处理，需要我们在方法中捕获异常并处理
     *  或者在调用方在调用Futrue.get时捕获异常进行处理
     * 
     * @param i
     * @return
     */
    @Async
    public Future<String> asyncInvokeReturnFuture(int i) {
        log.info("asyncInvokeReturnFuture, parementer={}", i);
        Future<String> future;
        try {
            Thread.sleep(1000 * 1);
            future = new AsyncResult<String>("success:" + i);
            throw new IllegalArgumentException("a");
        } catch (InterruptedException e) {
            future = new AsyncResult<String>("error");
        } catch(IllegalArgumentException e){
            future = new AsyncResult<String>("error-IllegalArgumentException");
        }
        return future;
    }

} 
```

实现AsyncConfigurer接口对异常线程池更加细粒度的控制 
a) 创建线程自己的线程池 
b) 对void方法抛出的异常处理的类AsyncUncaughtExceptionHandler

```
/**
 * 通过实现AsyncConfigurer自定义异常线程池，包含异常处理
 * 
 * @author hry
 *
 */
@Service
public class MyAsyncConfigurer implements AsyncConfigurer{
    private static final Logger log = LoggerFactory.getLogger(MyAsyncConfigurer.class);

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor threadPool = new ThreadPoolTaskExecutor();  
        threadPool.setCorePoolSize(1);  
        threadPool.setMaxPoolSize(1);  
        threadPool.setWaitForTasksToCompleteOnShutdown(true);  
        threadPool.setAwaitTerminationSeconds(60 * 15);  
        threadPool.setThreadNamePrefix("MyAsync-");
        threadPool.initialize();
        return threadPool;  
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
         return new MyAsyncExceptionHandler();  
    }

    /**
     * 自定义异常处理类
     * @author hry
     *
     */
    class MyAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {  

        @Override  
        public void handleUncaughtException(Throwable throwable, Method method, Object... obj) {  
            log.info("Exception message - " + throwable.getMessage());  
            log.info("Method name - " + method.getName());  
            for (Object param : obj) {  
                log.info("Parameter value - " + param);  
            }  
        }  

    } 

}12345678910111213141516171819202122232425262728293031323334353637383940414243444546
```

```
@SpringBootApplication
@EnableAsync // 启动异步调用
public class AsyncApplicationWithAsyncConfigurer {
    private static final Logger log = LoggerFactory.getLogger(AsyncApplicationWithAsyncConfigurer.class);

    public static void main(String[] args) {
        log.info("Start AsyncApplication.. ");
        SpringApplication.run(AsyncApplicationWithAsyncConfigurer.class, args);
    }


}123456789101112
```

测试代码

```java
@RunWith(SpringRunner.class)
@SpringBootTest(classes=AsyncApplicationWithAsyncConfigurer.class)
public class AsyncApplicationWithAsyncConfigurerTests {
    @Autowired
    private AsyncExceptionDemo asyncDemo;

    @Test
    public void contextLoads() throws InterruptedException, ExecutionException {
        asyncDemo.asyncInvokeSimplest();
        asyncDemo.asyncInvokeWithException("test");
        Future<String> future = asyncDemo.asyncInvokeReturnFuture(100);
        System.out.println(future.get());
    }
} 
```

运行测试用例 
MyAsyncConfigurer 捕获AsyncExceptionDemo 对象在调用asyncInvokeWithException的异常

```
2017-04-02 16:01:45.591  INFO 11152 --- [      MyAsync-1] c.h.s.a.exception.AsyncExceptionDemo     : asyncSimplest
2017-04-02 16:01:45.605  INFO 11152 --- [      MyAsync-1] c.h.s.a.exception.AsyncExceptionDemo     : asyncInvokeWithParameter, parementer=test
2017-04-02 16:01:45.608  INFO 11152 --- [      MyAsync-1] c.h.s.async.exception.MyAsyncConfigurer  : Exception message - test
2017-04-02 16:01:45.608  INFO 11152 --- [      MyAsync-1] c.h.s.async.exception.MyAsyncConfigurer  : Method name - asyncInvokeWithException
2017-04-02 16:01:45.608  INFO 11152 --- [      MyAsync-1] c.h.s.async.exception.MyAsyncConfigurer  : Parameter value - test
2017-04-02 16:01:45.608  INFO 11152 --- [      MyAsync-1] c.h.s.a.exception.AsyncExceptionDemo     : asyncInvokeReturnFuture, parementer=100
error-IllegalArgumentException
2017-04-02 16:01:46.656  INFO 11152 --- [       Thread-2] s.c.a.AnnotationConfigApplicationContext : Closing org.springframework.context.annotation.AnnotationConfigApplicationContext@47af7f3d: startup date [Sun Apr 02 16:01:44 CST 2017]; root of context hierarchy
123456789
```

## 5. 源码地址

[代码的GITHUB地址](https://github.com/hryou0922/spring_boot/tree/master/taskExecutionAndScheduling/src/main/java/com/hry/spring/async)







https://blog.csdn.net/hry2015/article/details/67640534