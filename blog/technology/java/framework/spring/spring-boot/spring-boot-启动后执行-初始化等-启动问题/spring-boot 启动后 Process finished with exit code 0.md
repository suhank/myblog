# spring-boot 启动后 Process finished with exit code 0

原创Emptor 发布于2018-09-12 22:21:23 阅读数 2434  收藏
展开

在研究Spring cloud用zookeeper做注册中心的时候，消费端，启动后就立即关闭了，最后报 Process finished with exit code 0
后来发现没有加上依赖

```xml
<dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

加上以后再启动就不会直接退出了。

我想大概的原因就我们平时做一个小的main方练习一样，启动后JVM就停止了。
而加上web依赖后，所有的对象都被装载进了web容器中，紧接着就是处理各种请求了，不会启动后就直接停止了。



 





https://blog.csdn.net/qq_30243515/article/details/82669542