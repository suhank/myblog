[TOC]



# java多线程公平锁与非公平锁

2016年11月25日 15:10:51

## 结论

公平锁（Fair）：加锁前检查是否有排队等待的线程，优先排队等待的线程，先来先得 

非公平锁（Nonfair）：加锁时不考虑排队等待问题，直接尝试获取锁，获取不到自动到队尾等待



**非公平锁性能比公平锁高5~10倍，因为公平锁需要在多核的情况下维护一个队列**

**首先Java中的ReentrantLock 默认的lock()方法采用的是非公平锁。**



## 公平锁实例

```java
package com.practice.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class MyService {
    private ReentrantLock lock;

    public MyService(boolean isFair) {
        super();
        lock = new ReentrantLock(isFair);
    }

    public void serviceMethod() {
        try {
            lock.lock();
            System.out.println("ThreadName="
                    + Thread.currentThread().getName() + "获得锁定");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }


    public static void main(String[] args) {
        final MyService service = new MyService(true);
        Thread thread = new Thread() {
            @Override
            public void run() {
                System.out.println("我进来了" + Thread.currentThread().getName());
                service.serviceMethod();
            }
        };

        ExecutorService exec = Executors.newCachedThreadPool();
        for (int i = 0; i < 5; i++) {
            exec.execute(thread);
        }
        exec.shutdown();
    }


}


```

执行结果

```
我进来了pool-1-thread-2
我进来了pool-1-thread-5
我进来了pool-1-thread-4
我进来了pool-1-thread-1
我进来了pool-1-thread-3
ThreadName=pool-1-thread-2获得锁定
ThreadName=pool-1-thread-5获得锁定
ThreadName=pool-1-thread-4获得锁定
ThreadName=pool-1-thread-1获得锁定
ThreadName=pool-1-thread-3获得锁定
```

这个时候你会发现打印是有序的，排队在前面的线程直接获取锁。这就是公平锁





## 非公平锁实例

```java
package com.practice.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class MyService {
    private ReentrantLock lock;

    public MyService(boolean isFair) {
        super();
        lock = new ReentrantLock(isFair);
    }

    public void serviceMethod() {
        try {
            lock.lock();
            System.out.println("ThreadName="
                    + Thread.currentThread().getName() + "获得锁定");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }


    public static void main(String[] args) {
        final MyService service = new MyService(false);
        Thread thread = new Thread() {
            @Override
            public void run() {
                System.out.println("我进来了" + Thread.currentThread().getName());
                service.serviceMethod();
            }
        };

        ExecutorService exec = Executors.newCachedThreadPool();
        for (int i = 0; i < 5; i++) {
            exec.execute(thread);
        }
        exec.shutdown();
    }


}


```

执行结果

```
我进来了pool-1-thread-1
我进来了pool-1-thread-3
我进来了pool-1-thread-2
我进来了pool-1-thread-5
ThreadName=pool-1-thread-1获得锁定
我进来了pool-1-thread-4
ThreadName=pool-1-thread-4获得锁定
ThreadName=pool-1-thread-2获得锁定
ThreadName=pool-1-thread-3获得锁定
ThreadName=pool-1-thread-5获得锁定
```

当把true改成false之后，线程3先进来，却发现被线程4获得锁了，这就是非公平锁













版权声明：本文为博主原创文章，遵循[ CC 4.0 BY-SA ](http://creativecommons.org/licenses/by-sa/4.0/)版权协议，转载请附上原文出处链接和本声明。

本文链接：<https://blog.csdn.net/IsResultXaL/article/details/53334750>