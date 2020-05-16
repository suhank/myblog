[TOC]



# springboot异步调用@Async

这个还是自己实现比较靠谱

## 1.使用背景

在项目中，当访问其他人的接口较慢或者做耗时任务时，不想程序一直卡在耗时任务上，想程序能够并行执行，我们可以使用多线程来并行的处理任务，也可以使用spring提供的异步处理方式@Async。

## 2.异步处理方式

1. 调用之后，不返回任何数据。
2. 调用之后，返回数据，通过Future来获取返回数据

## 3.@Async不返回数据

使用@EnableAsync启用异步注解

```java
@Configuration
@EnableAsync
@Slf4j
public class AsyncConfig{
}
```

在异步处理的方法dealNoReturnTask上添加注解@Async

```java
@Component
@Slf4j
public class AsyncTask {

    @Async
    public void dealNoReturnTask(){
        log.info("Thread {} deal No Return Task start", Thread.currentThread().getName());
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("Thread {} deal No Return Task end at {}", Thread.currentThread().getName(), System.currentTimeMillis());
    }
}
```

Test测试类：

```java
@SpringBootTest(classes = SpringbootApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
@Slf4j
public class AsyncTest {

    @Autowired
    private AsyncTask asyncTask;

    @Test
    public void testDealNoReturnTask(){
        asyncTask.dealNoReturnTask();
        try {
            log.info("begin to deal other Task!");
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
```

日志打印结果为：

```
begin to deal other Task!
AsyncExecutorThread-1 deal No Return Task start
AsyncExecutorThread-1 deal No Return Task end at 1499751227034
```

从日志中我们可以看出，方法dealNoReturnTask()是异步执行完成的。
dealNoReturnTask()设置sleep 3s是为了模拟耗时任务
testDealNoReturnTask()设置sleep 10s是为了确认异步是否执行完成

## 4.@Async返回数据

异步调用返回数据，Future表示在未来某个点获取执行结果，返回数据类型可以自定义

```java
    @Async
    public Future<String> dealHaveReturnTask() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("thread", Thread.currentThread().getName());
        jsonObject.put("time", System.currentTimeMillis());
        return new AsyncResult<String>(jsonObject.toJSONString());
    }
```

测试类用isCancelled判断异步任务是否取消，isDone判断任务是否执行结束

```java
	@Test
    public void testDealHaveReturnTask() throws Exception {
        Future<String> future = asyncTask.dealHaveReturnTask();
        log.info("begin to deal other Task!");
        while (true) {
            if(future.isCancelled()){
                log.info("deal async task is Cancelled");
                break;
            }
            if (future.isDone() ) {
                log.info("deal async task is Done");
                log.info("return result is " + future.get());
                break;
            }
            log.info("wait async task to end ...");
            Thread.sleep(1000);
        }
    }
```

日志打印如下，我们可以看出任务一直在等待异步任务执行完毕，用future.get()来获取异步任务的返回结果

```
begin to deal other Task!
wait async task to end ...
wait async task to end ...
wait async task to end ...
wait async task to end ...
deal async task is Done
return result is {"thread":"AsyncExecutorThread-1","time":1499752617330}
```

## 4.异常处理

我们可以实现AsyncConfigurer接口，也可以继承AsyncConfigurerSupport类来实现
在方法getAsyncExecutor()中创建线程池的时候，必须使用 executor.initialize()，
不然在调用时会报线程池未初始化的异常。
如果使用threadPoolTaskExecutor()来定义bean，则不需要初始化

```java
@Configuration
@EnableAsync
@Slf4j
public class AsyncConfig implements AsyncConfigurer {

//    @Bean
//    public ThreadPoolTaskExecutor threadPoolTaskExecutor(){
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        executor.setCorePoolSize(10);
//        executor.setMaxPoolSize(100);
//        executor.setQueueCapacity(100);
//        return executor;
//    }

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(100);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("AsyncExecutorThread-");
        executor.initialize(); //如果不初始化，导致找到不到执行器
        return executor;
    }
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new AsyncExceptionHandler();
    }
}
```

异步异常处理类：

```java
@Slf4j
public class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler {
    @Override
    public void handleUncaughtException(Throwable ex, Method method, Object... params) {
        log.info("Async method: {} has uncaught exception,params:{}", method.getName(), JSON.toJSONString(params));

        if (ex instanceof AsyncException) {
            AsyncException asyncException = (AsyncException) ex;
            log.info("asyncException:{}",asyncException.getErrorMessage());
        }

        log.info("Exception :");
        ex.printStackTrace();
    }
}
```

异步处理异常类：

```java
@Data
@AllArgsConstructor
public class AsyncException extends Exception {
    private int code;
    private String errorMessage;
}
```

1. 在无返回值的异步调用中，异步处理抛出异常，AsyncExceptionHandler的handleUncaughtException()会捕获指定异常，原有任务还会继续运行，直到结束。
2. 在有返回值的异步调用中，异步处理抛出异常，会直接抛出异常，异步任务结束，原有处理结束执行。

大家可以关注我的公众号：不知风在何处，相互沟通，共同进步。



http://www.voidcn.com/article/p-vuszdodk-e.html