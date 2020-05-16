[TOC]



# 彻底弄懂 Java 线程池原理

2019年01月07日 

## 概述

这篇文章是我在阅读源码时整理的一些笔记，对源码的关键点进行了比较详细的注释，然后加上一些自己对线程池机制的理解。最终目的是要弄清楚下面这些问题：

- 线程池有 execute() 和 submit() 方法，执行机制分别是什么？
- 如何新建线程？
- 任务如何执行？
- 线程如何销毁？超时机制如何实现？

首先需要介绍一下线程池的两个重要成员：

## ctl

AtomicInteger 类型。高3位存储线程池状态，低29位存储当前线程数量。workerCountOf(c) 返回当前线程数量。runStateOf(c) 返回当前线程池状态。 线程池有如下状态：

- RUNNING：接收新任务，处理队列任务。
- SHUTDOWN：不接收新任务，但处理队列任务。
- STOP：不接收新任务，也不处理队列任务，并且中断所有处理中的任务。
- TIDYING：所有任务都被终结，有效线程为0。会触发terminated()方法。
- TERMINATED：当terminated()方法执行结束。

## Worker

这个线程在线程池中的包装类。一个 Worker 代表一个线程。线程池用一个 HashSet 管理这些线程。

**需要注意的是，Worker 本身并不区分核心线程和非核心线程，核心线程只是概念模型上的叫法，特性是依靠对线程数量的判断来实现的** Worker 特性如下：

- 继承自 AQS，本身实现了一个最简单的不公平的不可重入锁。
- 构造方法传入 Runnable，代表第一个执行的任务，可以为空。构造方法中新建一个线程。
- 实现了 Runnable 接口，在新建线程时传入 this。因此线程启动时，会执行 Worker 本身的 run 方法。
- run 方法调用了 ThreadPoolExecutor 的 runWorker 方法，负责实际执行任务。

## submit() 方法的执行机制

submit 返回一个 Future 对象，我们可以调用其 get 方法获取任务执行的结果。代码很简单，就是将 Runnable 包装成 FutureTask 而已。可以看到，最终还是调用 Execute 方法：

```
public Future<?> submit(Runnable task) {
    if (task == null) throw new NullPointerException();
    RunnableFuture<Void> ftask = newTaskFor(task, null);
    execute(ftask);
    return ftask;
}

```

FutureTask 的代码就不贴了，简述一下原理：

- FutureTask 实现了 RunnableFuture 接口，RunnableFuture 继承自Runnable。执行任务时会调用 FutureTask 的 run 方法，run 方法中执行真正的任务代码，执行完后调用 set 方法设置结果。
- 如果任务执行完毕，get 方法会直接返回结果，如果没有，get 方法会阻塞并等待结果。
- set 方法中设置结果后会取消阻塞，使 get 方法返回结果。

## execute() 方法的执行机制

这个机制大家应该都很熟了，再简述一遍：

1. 工作线程数小于核心线程数时，直接新建核心线程执行任务；
2. 大于核心线程数时，将任务添加进等待队列；
3. 队列满时，创建非核心线程执行任务；
4. 工作线程数大于最大线程数时，拒绝任务

具体的代码分析如下：

```java
int c = ctl.get();
if (workerCountOf(c) < corePoolSize) { //小于核心线程数
    if (addWorker(command, true)) //启动核心线程并执行任务
        return;
    c = ctl.get(); //执行失败时重新获取值
}
if (isRunning(c) && workQueue.offer(command)) { //检查运行状态并将任务添加到队列
    int recheck = ctl.get();
    if (! isRunning(recheck) && remove(command)) //重新检查，防止状态有变化。如果有，移出队列并拒绝任务
        reject(command);
    else if (workerCountOf(recheck) == 0) //如果线程数为0，创建非核心线程，第一个参数为空时会从队列中取任务执行
        addWorker(null, false);
}
else if (!addWorker(command, false)) //添加到队列失败，说明队列已满，创建非核心线程执行任务
    reject(command); //执行失败说明达到最大线程数，拒绝任务

```

## 新任务如何添加进队列？

线程池使用 addWorker 方法新建线程，第一个参数代表要执行的任务，线程会将这个任务执行完毕后再从队列取任务执行。第二参数是核心线程的标志，它并不是 Worker 本身的属性，在这里只用来判断工作线程数量是否超标。

这个方法可以分成两部分，第一部分进行一些前置判断，并使用循环 CAS 结构将线程数量加1。代码如下：

```java
private boolean addWorker(Runnable firstTask, boolean core) {
    retry: //这个语法不常用，用于给外层 for 循环命名。方便嵌套 for 循环中，break 和 continue 指定是外层还是内层循环
    for (;;) {
        int c = ctl.get();
        int rs = runStateOf(c);
        
        // firstTask 不为空代表这个方法用于添加任务，为空代表新建线程。SHUTDOWN 状态下不接受新任务，但处理队列中的任务。这就是第二个判断的逻辑。
        if (rs >= SHUTDOWN &&
        ! (rs == SHUTDOWN &&
           firstTask == null &&
           ! workQueue.isEmpty()))
        return false;
        
        // 使用循环 CAS 自旋，增加线程数量直到成功为止
        for (;;) {
        int wc = workerCountOf(c);
        //判断是否超过线程容量
        if (wc >= CAPACITY ||
            wc >= (core ? corePoolSize : maximumPoolSize))
            return false;
        //使用 CAS 将线程数量加1
        if (compareAndIncrementWorkerCount(c))
            break retry;
        //修改不成功说明线程数量有变化
        //重新判断线程池状态，有变化时跳到外层循环重新获取线程池状态
        c = ctl.get();  // Re-read ctl
        if (runStateOf(c) != rs)
            continue retry;
        //到这里说明状态没有变化，重新尝试增加线程数量
        }
    }
    ... ...
}

```

第二部分负责新建并启动线程，并将 Worker 添加至 Hashset 中。代码很简单，没什么好注释的，用了 ReentrantLock 确保线程安全。

```java
boolean workerStarted = false;
boolean workerAdded = false;
Worker w = null;
try {
    w = new Worker(firstTask);
    final Thread t = w.thread;
    if (t != null) {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            int rs = runStateOf(ctl.get());

            if (rs < SHUTDOWN ||
                (rs == SHUTDOWN && firstTask == null)) {
                if (t.isAlive()) // precheck that t is startable
                    throw new IllegalThreadStateException();
                workers.add(w);
                int s = workers.size();
                if (s > largestPoolSize)
                    largestPoolSize = s; //这个参数是测试用的，不用管它
                workerAdded = true;
            }
        } finally {
            mainLock.unlock();
        }
        if (workerAdded) {
            t.start();
            workerStarted = true;
        }
    }
} finally {
    if (! workerStarted)
        addWorkerFailed(w); //添加失败时移除 Worker 并将线程数量减 1
}
return workerStarted;
}

```

## 任务如何执行？

在 addWorker 方法中，线程会被启动。新建线程时，Worker 将自身传入，所以线程启动后会执行 Worker 的 run 方法，这个方法调用了 ThreadPoolExecutor 的 runWorker 方法执行任务，runWorker 中会循环取任务执行，执行逻辑如下：

- 如果 firstTask 不为空，先执行 firstTask，执行完毕后置空；
- firstTask 为空后调用 getTask() 从队列中取任务执行；
- 一直执行到没有任务后，退出 while 循环
- 调用 processWorkerExit() 方法，将 Worker 移除出 HashSet，此时线程执行完毕，也不再被引用，会自动销毁。

具体代码分析如下：

```java
final void runWorker(Worker w) {
    Thread wt = Thread.currentThread();
    Runnable task = w.firstTask;
    w.firstTask = null;
    w.unlock(); // allow interrupts
    boolean completedAbruptly = true;
    //task 为我们传给 execute 的任务。task 为空时从队列中取任务执行
    try {
        while (task != null || (task = getTask()) != null) {
            w.lock();
            //这段逻辑非常绕。实际上它实现了以下逻辑：
            //1.如果线程池已停止且线程未中断，条件成立，中断线程
            //2.如果线程池未停止，线程为中断状态，将线程状态重置，并重新进行1的判断
            //3.如果线程池未停止，线程不为中断状态，条件不成立
            //Thread.interrupted() 会重置中断状态，保证
            if ((runStateAtLeast(ctl.get(), STOP) ||
                 (Thread.interrupted() &&
                  runStateAtLeast(ctl.get(), STOP))) &&
                !wt.isInterrupted())
                wt.interrupt();
            //beforeExecute 和 afterExecute 为空方法，交给子类实现
            try {
                beforeExecute(wt, task);
                Throwable thrown = null;
                try {
                    task.run(); //执行任务
                } catch (RuntimeException x) {
                    thrown = x; throw x;
                } catch (Error x) {
                    thrown = x; throw x;
                } catch (Throwable x) {
                    thrown = x; throw new Error(x);
                } finally {
                    afterExecute(task, thrown);
                }
            } finally {
                task = null;
                w.completedTasks++;
                w.unlock();
            }
        }
        completedAbruptly = false;
    } finally {
        //执行到这里时说明线程执行完毕，此方法将线程从 HashSet 中移出。线程终止且没有引用，会被自动回收。
        processWorkerExit(w, completedAbruptly);
    }
}

```

## 线程如何销毁？超时机制如何实现？

在 runWorker 方法中 getTask 方法返回 null 之后会导致线程执行完毕，被移除出 HashSet，从而被系统销毁。 线程的超时机制也是在这个方法实现的，借助于 BlockingQueue 的 poll 和 take 方法。

- poll 方法可以设置一个超时时间，当队列为空时，在此时间内阻塞等待，超时后返回 null
- take 方法在队列为空时直接抛出异常

超时机制实现原理如下：

- 当 allowCoreThreadTimeOut 为 true，所有线程都会超时，全部调用 poll 方法，传入 keepAliveTime 参数。
- 当 allowCoreThreadTimeOut 为 false 时，如果工作线程数量大于核心线程数，将此线程当作非核心线程处理，调用 poll 方法
- 当 allowCoreThreadTimeOut 为 false 且工作线程数量小于等于核心线程数时，将此线程当作核心线程处理，调用 take 方法，队列为空时抛出异常，进入下一次循环。如果队列一直为空，核心线程会一直在此循环等待任务进行处理。

具体代码如下：

```java
private Runnable getTask() {
    boolean timedOut = false; // Did the last poll() time out?
    
    for (;;) {
        int c = ctl.get();
        int rs = runStateOf(c);
    
        // Check if queue empty only if necessary.
        if (rs >= SHUTDOWN && (rs >= STOP || workQueue.isEmpty())) {
            decrementWorkerCount();
            return null;
        }
    
        int wc = workerCountOf(c);
    
        // 允许核心线程超时或者线程数大于核心线程
        boolean timed = allowCoreThreadTimeOut || wc > corePoolSize;
    
        // timed && timedOut 这两个参数结合起来控制超时机制
        if ((wc > maximumPoolSize || (timed && timedOut))
            && (wc > 1 || workQueue.isEmpty())) {
            if (compareAndDecrementWorkerCount(c))
                return null;
            continue;
        }
    
        try {
            // 队列为空时，poll 方法会阻塞等待，超过 keepAliveTime 时返回空值。take 方法会直接返回异常。
            // 当 allowCoreThreadTimeOut 为 true 时，核心线程和非核心线程没有区别，一律调用poll方法
            // 当 allowCoreThreadTimeOut 为 false 时，线程数量超过核心线程数才会进入超时机制，如果不超过，则将当前线程当作核心线程处理，调用 take，抛出异常后进入下一次循环。如果队列为空，此处会一直循环。
            Runnable r = timed ?
                workQueue.poll(keepAliveTime, TimeUnit.NANOSECONDS) :
                workQueue.take();
            if (r != null)
                return r;
            timedOut = true;
        } catch (InterruptedException retry) {
            timedOut = false;
        }
    }
}

```

 





https://juejin.im/post/5c33400c6fb9a049fe35503b#heading-6