[TOC]



# springboot对多线程的支持详解 @Async

> 这两天看阿里的JAVA开发手册，到多线程的时候说永远不要用 new Thread()这种方式来使用多线程。确实是这样的，我一直在用线程池，到了springboot才发现他已经给我们提供了很方便的线程池机制。
> 本博客代码托管在github上[https://github.com/gxz0422042...](https://github.com/gxz04220427/springboot-learn/tree/master)

## 一、介绍

`Spring`是通过任务执行器(`TaskExecutor`)来实现多线程和并发编程，使用`ThreadPoolTaskExecutor`来创建一个基于线城池的`TaskExecutor`。在使用线程池的大多数情况下都是异步非阻塞的。我们配置注解`@EnableAsync`可以开启异步任务。然后在实际执行的方法上配置注解`@Async`上声明是异步任务。

## 二、配置类

配置类代码如下：

```
package com.spartajet.springbootlearn.thread;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * @description
 * @create 2017-02-22 下午11:53
 * @email gxz04220427@163.com
 */
@Configuration
@EnableAsync
public class ThreadConfig implements AsyncConfigurer {

    /**
     * The {@link Executor} instance to be used when processing async
     * method invocations.
     */
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(15);
        executor.setQueueCapacity(25);
        executor.initialize();
        return executor;
    }

    /**
     * The {@link AsyncUncaughtExceptionHandler} instance to be used
     * when an exception is thrown during an asynchronous method execution
     * with {@code void} return type.
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return null;
    }
}
```

解读：

1. 利用`EnableAsync`来开启`Springboot`对于异步任务的支持
2. 配置类实现接口`AsyncConfigurator`，返回一个`ThreadPoolTaskExecutor`线程池对象。

## 三、任务执行

任务执行代码：

```
package com.spartajet.springbootlearn.thread;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @description
 * @create 2017-02-23 上午12:00
 * @email gxz04220427@163.com
 */
@Service
public class AsyncTaskService {
    @Async
    public void executeAsyncTask(int i) {
        System.out.println("线程" + Thread.currentThread().getName() + " 执行异步任务：" + i);
    }
}
```

代码解读：

1. 通过`@Async`注解表明该方法是异步方法，如果注解在类上，那表明这个类里面的所有方法都是异步的。

## 四、测试代码

```
package com.spartajet.springbootlearn;

import com.spartajet.springbootlearn.thread.AsyncTaskService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith (SpringRunner.class)
@SpringBootTest
public class SpringbootLearnApplicationTests {
    @Autowired
    private AsyncTaskService asyncTaskService;

    @Test
    public void contextLoads() {
    }

    @Test
    public void threadTest() {
        for (int i = 0; i < 20; i++) {
            asyncTaskService.executeAsyncTask(i);
        }
    }

}
```

测试结果：

```
线程ThreadPoolTaskExecutor-4 执行异步任务：3
线程ThreadPoolTaskExecutor-2 执行异步任务：1
线程ThreadPoolTaskExecutor-1 执行异步任务：0
线程ThreadPoolTaskExecutor-1 执行异步任务：7
线程ThreadPoolTaskExecutor-1 执行异步任务：8
线程ThreadPoolTaskExecutor-1 执行异步任务：9
线程ThreadPoolTaskExecutor-1 执行异步任务：10
线程ThreadPoolTaskExecutor-5 执行异步任务：4
线程ThreadPoolTaskExecutor-3 执行异步任务：2
线程ThreadPoolTaskExecutor-5 执行异步任务：12
线程ThreadPoolTaskExecutor-1 执行异步任务：11
线程ThreadPoolTaskExecutor-2 执行异步任务：6
线程ThreadPoolTaskExecutor-4 执行异步任务：5
线程ThreadPoolTaskExecutor-2 执行异步任务：16
线程ThreadPoolTaskExecutor-1 执行异步任务：15
线程ThreadPoolTaskExecutor-5 执行异步任务：14
线程ThreadPoolTaskExecutor-3 执行异步任务：13
线程ThreadPoolTaskExecutor-1 执行异步任务：19
线程ThreadPoolTaskExecutor-2 执行异步任务：18
线程ThreadPoolTaskExecutor-4 执行异步任务：17
```

https://segmentfault.com/a/1190000015766938