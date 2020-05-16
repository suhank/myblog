[TOC]



# Spring线程池与JDK线程池配置

  在web开发项目中，处理任务的线程池或多或少会用到。如果项目中使用到了spring，使用线程池时就可以直接使用spring自带的线程池了。下面是Spring线程池与JDK线程池的使用实例，做个参考吧。

```java
package com.practice.threadpool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.*;

/**
 * Spring线程池与JDK线程池配置
 * 在web开发项目中，处理任务的线程池或多或少会用到。如果项目中使用到了spring，
 * 使用线程池时就可以直接使用spring自带的线程池了。下面是Spring线程池与JDK线程池的使用实例，做个参考吧。
 */
@Slf4j
public class SpringThreadPoolUseDemo {

    //直接在代码中使用
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        /*------------------------------------------------------------------------------------
                 JDK线程池示例
        ------------------------------------------------------------------------------------*/
        /*ExecutorService threadPool = Executors.newFixedThreadPool(5);
        CompletionService<String> executor = new ExecutorCompletionService<String>(threadPool);
        Future<String> future = executor.submit(new TaskHandle());
        System.out.println(future.get());
        threadPool.shutdown();*/

        /*------------------------------------------------------------------------------------
                 Spring线程池示例
        ------------------------------------------------------------------------------------*/
        ThreadPoolTaskExecutor threadPoolTaskExecutor = getThreadPoolTaskExecutor();

        for (int i = 0; i < 100; i++) {
            FutureTask<String> ft = new FutureTask<String>(new TaskHandle(i));
            threadPoolTaskExecutor.submit(ft);
//            System.out.println(ft.get());
        }

        log.info("主线程执行完成....");
//        threadPoolTaskExecutor.shutdown();
        //如果启用了spring的注入功能，则可以在被spring管理的bean方法上添加“@Async”即可。
    }

    private static ThreadPoolTaskExecutor getThreadPoolTaskExecutor() {
        //==== 非容器管理方式
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setQueueCapacity(10);
        threadPoolTaskExecutor.setCorePoolSize(5);
        threadPoolTaskExecutor.setMaxPoolSize(10);
        threadPoolTaskExecutor.setKeepAliveSeconds(60);
//        threadPoolTaskExecutor.setThreadNamePrefix("sparkle-main-");//配置线程池中的线程的名称前缀
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());//CallerRunsPolicy策略是排队等待执行
        threadPoolTaskExecutor.initialize();

        //==== 容器管理方式
        /*  把以下配置加到spring的配置文件中：
         <!-- 配置线程池 -->
         <bean id ="taskExecutor"  class ="org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor" >
         <!-- 线程池维护线程的最少数量 -->
         <property name ="corePoolSize" value ="5" />
         <!-- 线程池维护线程所允许的空闲时间 -->
         <property name ="keepAliveSeconds" value ="5" />
         <!-- 线程池维护线程的最大数量 -->
         <property name ="maxPoolSize" value ="10" />
         <!-- 线程池所使用的缓冲队列 -->
         <property name ="queueCapacity" value ="10" />
         </bean>*/
        //在程序中这样调用方法
        /*ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
        ThreadPoolTaskExecutor threadPoolTaskExecutor = ctx
                .getBean(ThreadPoolTaskExecutor.class);*/
        return threadPoolTaskExecutor;
    }

    /**
     * 处理任务的类,为了方便大家观看，我把这个类写到当前类中了。
     *
     * @author mengfeiyang
     */
    private static class TaskHandle implements Callable<String> {

        private  int i ;

        public TaskHandle(int i) {
            this.i = i;
        }

        public String call() throws Exception {
            log.info(Thread.currentThread().getName()+"线程执行了,现在执行到"+i+"。");
            Thread.sleep(1000);
            return Thread.currentThread().getName();
        }
    }
}

```

实际项目中见到的配置：

```xml
	<!-- 线程池 -->
	<bean id="threadPoolTaskExecutor"
		class="org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor">
		<property name="corePoolSize" value="2" />
		<property name="maxPoolSize" value="30" />
		<property name="queueCapacity" value="100" />
	</bean>
```