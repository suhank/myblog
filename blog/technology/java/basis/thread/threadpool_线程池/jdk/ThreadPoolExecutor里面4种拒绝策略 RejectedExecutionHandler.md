[TOC]



# ThreadPoolExecutor里面4种拒绝策略 RejectedExecutionHandler

## 简介

ThreadPoolExecutor类实现了ExecutorService接口和Executor接口，可以设置线程池corePoolSize，最大线程池大小，AliveTime，拒绝策略等。常用构造方法：

```java
    /**
     * Creates a new {@code ThreadPoolExecutor} with the given initial
     * parameters.
     *
     * @param corePoolSize the number of threads to keep in the pool, even
     *        if they are idle, unless {@code allowCoreThreadTimeOut} is set
     * @param maximumPoolSize the maximum number of threads to allow in the
     *        pool
     * @param keepAliveTime when the number of threads is greater than
     *        the core, this is the maximum time that excess idle threads
     *        will wait for new tasks before terminating.
     * @param unit the time unit for the {@code keepAliveTime} argument
     * @param workQueue the queue to use for holding tasks before they are
     *        executed.  This queue will hold only the {@code Runnable}
     *        tasks submitted by the {@code execute} method.
     * @param threadFactory the factory to use when the executor
     *        creates a new thread
     * @param handler the handler to use when execution is blocked
     *        because the thread bounds and queue capacities are reached
     * @throws IllegalArgumentException if one of the following holds:<br>
     *         {@code corePoolSize < 0}<br>
     *         {@code keepAliveTime < 0}<br>
     *         {@code maximumPoolSize <= 0}<br>
     *         {@code maximumPoolSize < corePoolSize}
     * @throws NullPointerException if {@code workQueue}
     *         or {@code threadFactory} or {@code handler} is null
     */
    public ThreadPoolExecutor(int corePoolSize,
                              int maximumPoolSize,
                              long keepAliveTime,
                              TimeUnit unit,
                              BlockingQueue<Runnable> workQueue,
                              ThreadFactory threadFactory,
                              RejectedExecutionHandler handler) {
```

 corePoolSize： 线程池维护线程的最少数量

maximumPoolSize：线程池维护线程的最大数量

keepAliveTime： 线程池维护线程所允许的空闲时间

unit： 线程池维护线程所允许的空闲时间的单位

workQueue： 线程池所使用的缓冲队列

handler： 线程池对拒绝任务的处理策略

 

当一个任务通过execute(Runnable)方法欲添加到线程池时：

1. 如果此时线程池中的数量小于corePoolSize，即使线程池中的线程都处于空闲状态，也要创建新的线程来处理被添加的任务。
2. 如果此时线程池中的数量等于 corePoolSize，但是缓冲队列 workQueue未满，那么任务被放入缓冲队列。
3. 如果此时线程池中的数量大于corePoolSize，缓冲队列workQueue满，并且线程池中的数量小于maximumPoolSize，建新的线程来处理被添加的任务。
4. 如果此时线程池中的数量大于corePoolSize，缓冲队列workQueue满，并且线程池中的数量等于maximumPoolSize，那么通过 handler所指定的策略来处理此任务。也就是：处理任务的优先级为：核心线程corePoolSize、任务队列workQueue、最大线程maximumPoolSize，如果三者都满了，使用handler处理被拒绝的任务。  

当线程池中的线程数量大于 corePoolSize时，如果某线程空闲时间超过keepAliveTime，线程将被终止。这样，线程池可以动态的调整池中的线程数。

## RejectedExecutionHandler拒绝任务的处理策略有四个选择：

### 注意

**这个拒绝任务策略生效的前提是 queueCapacity (就是存储线程的缓冲队列的大小) 缓冲队列满了**

### 策略1: AbortPolicy

对拒绝任务抛弃处理，并且抛出异常。

抛出java.util.concurrent.RejectedExecutionException异常 ，示例如下：

```java
private static class Worker implements Runnable {
    public void run() {
        System.out.println(Thread.currentThread().getName() + " is running");
    }
}

    public static void main(String[] args) {

        int corePoolSize = 5;
        int maxPoolSize = 10;
        long keepAliveTime = 5;
        BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>(10);

        //拒绝策略1：将抛出 RejectedExecutionException.
        RejectedExecutionHandler handler =
                new ThreadPoolExecutor.AbortPolicy();

        ThreadPoolExecutor executor = new ThreadPoolExecutor
                (corePoolSize, maxPoolSize,
                        keepAliveTime, TimeUnit.SECONDS,
                        queue, handler);

        for (int i = 0; i < 100; i++) {
            executor.execute(new Worker());
        }
        executor.shutdown();
    }
```

运行结果如下：

```
pool-1-thread-2 is running
pool-1-thread-3 is running
Exception in thread "main" java.util.concurrent.RejectedExecutionException
pool-1-thread-1 is running
pool-1-thread-7 is running
pool-1-thread-6 is running
pool-1-thread-4 is running
pool-1-thread-9 is running
pool-1-thread-8 is running
pool-1-thread-5 is running
at java.util.concurrent.ThreadPoolExecutor$AbortPolicy.rejectedExecution(ThreadPoolExecutor.java:1760)
at java.util.concurrent.ThreadPoolExecutor.reject(ThreadPoolExecutor.java:767)
at java.util.concurrent.ThreadPoolExecutor.execute(ThreadPoolExecutor.java:658)
at concurrent.ThreadPoolDemo.main(ThreadPoolDemo.java:33)
pool-1-thread-10 is running
```

处理源码如下：

```
public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
    throw new RejectedExecutionException();
}
```



### 策略2: CallerRunsPolicy

CallerRunsPolicy指的是当线程池拒绝该任务的时候，线程在本地线程直接execute。这样就限制了本地线程的循环提交流程。  所以会在主线程直接执行该线程。也就是说，在本程序中最多会有11个线程在执行，10个线程在等待。由此限制了线程池的等待线程数与执行线程数

用于被拒绝任务的处理程序，它直接在 execute 方法的调用线程中运行被拒绝的任务；如果执行程序已关闭，则会丢弃该任务。如下：

```java
RejectedExecutionHandler handler = new ThreadPoolExecutor.CallerRunsPolicy();
```

运行如下：

```
pool-1-thread-7 is running
pool-1-thread-7 is running
pool-1-thread-7 is running
pool-1-thread-7 is running
pool-1-thread-7 is running
pool-1-thread-7 is running
pool-1-thread-2 is running
pool-1-thread-3 is running
pool-1-thread-1 is running
pool-1-thread-8 is running
main is running
main is running
main is running
pool-1-thread-4 is running
pool-1-thread-7 is running
pool-1-thread-7 is running
pool-1-thread-7 is running
```

处理源码如下：

```java
public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
    if (!e.isShutdown()) {
        r.run();
    }
}
```

### 策略3: DiscardOldestPolicy

在线程池的等待队列中，将队首任务抛弃，使用当前任务来替换。

```
RejectedExecutionHandler handler = new ThreadPoolExecutor.DiscardOldestPolicy();
```

这样运行结果就不会有100个线程全部被执行。处理源码如下：

```java
       public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            if (!e.isShutdown()) {
                e.getQueue().poll();
                e.execute(r);
            }
        }
```

 

### 策略4:DiscardPolicy 

什么也不做

用于被拒绝任务的处理程序，默认情况下它将丢弃被拒绝的任务。

运行结果也不会全部执行100个线程。

源码如下，实际就是对线程不执行操作：

```
    public static class DiscardPolicy implements RejectedExecutionHandler {

        /**

         * Creates a <tt>DiscardPolicy</tt>.

         */

        public DiscardPolicy() { }

 

        /**

         * Does nothing, which has the effect of discarding task r.

         * @param r the runnable task requested to be executed

         * @param e the executor attempting to execute this task

         */

        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {

        }

    }
```

 

这四种策略是独立无关的，是对任务拒绝处理的四中表现形式。最简单的方式就是直接丢弃任务。但是却有两种方式，到底是该丢弃哪一个任务，比如可以丢弃当前将要加入队列的任务本身（DiscardPolicy）或者丢弃任务队列中最旧任务（DiscardOldestPolicy）。丢弃最旧任务也不是简单的丢弃最旧的任务，而是有一些额外的处理。除了丢弃任务还可以直接抛出一个异常（RejectedExecutionException），这是比较简单的方式。抛出异常的方式（AbortPolicy）尽管实现方式比较简单，但是由于抛出一个RuntimeException，因此会中断调用者的处理过程。除了抛出异常以外还可以不进入线程池执行，在这种方式（CallerRunsPolicy）中任务将有调用者线程去执行。 





https://blog.csdn.net/pozmckaoddb/article/details/51478017