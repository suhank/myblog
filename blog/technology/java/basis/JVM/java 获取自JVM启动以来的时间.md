[TOC]



# java 获取自JVM启动以来的时间

时间: 2017-10-29 02:47:49.0标签: [java](https://codeday.me/tag/java) [time](https://codeday.me/tag/time) [jvm](https://codeday.me/tag/jvm) 
译文: [来源](http://stackoverflow.com/questions/817801/time-since-jvm-started)[翻译纠错](https://publish.codeday.me/post/publist?site=cn&id=90411)



有没有办法找出自从JVM开始的时间？



当然，除了在main的开始附近启动一个定时器，因为在我的场景中，我正在编写库代码，并且在启动后立即调用某个东西是一个太重的麻烦。

最佳答案

使用此代码段：

```
long jvmUpTime = ManagementFactory.getRuntimeMXBean().getUptime();
```

要么：

```
long jvmStartTime = ManagementFactory.getRuntimeMXBean().getStartTime();
```

这是检索JVM运行时间的正确方法。

详情请参阅<http://java.sun.com/j2se/1.5.0/docs/api/java/lang/management/RuntimeMXBean.html>







<https://codeday.me/bug/20171029/90411.html>