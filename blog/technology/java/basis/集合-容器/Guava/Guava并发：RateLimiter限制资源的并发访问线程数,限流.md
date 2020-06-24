# Guava并发：RateLimiter限制资源的并发访问线程数

RateLimiter类似于JDK的信号量Semphore，他用来限制对资源并发访问的线程数，本文介绍RateLimiter使用

[Guava](http://outofmemory.cn/tag/Guava) [并发](http://outofmemory.cn/tag/%E5%B9%B6%E5%8F%91) [Java](http://outofmemory.cn/tag/Java)

RateLimiter类似于JDK的信号量Semphore，他用来限制对资源并发访问的线程数。

```java
RateLimiter limiter = RateLimiter.create(4.0); //每秒不超过4个任务被提交
limiter.acquire();  //请求RateLimiter, 超过permits会被阻塞
executor.submit(runnable); //提交任务
```

也可以以非阻塞的形式来使用：

```java
If(limiter.tryAcquire()){ //未请求到limiter则立即返回false
    doSomething();
}else{
    doSomethingElse();
}
```





http://outofmemory.cn/java/guava/concurrent/RateLimiter