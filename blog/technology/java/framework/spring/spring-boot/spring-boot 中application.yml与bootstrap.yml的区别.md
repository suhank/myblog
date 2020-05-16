# Spring Boot 中application.yml与bootstrap.yml的区别

2017年12月27日 16:58:40

阅读数：3409

# yml与properties

其实yml和properties文件是一样的原理，且一个项目上要么yml或者properties，二选一的存在。

推荐使用yml，更简洁。

# bootstrap与application

## 1.加载顺序

这里主要是说明application和bootstrap的加载顺序。

- bootstrap.yml（bootstrap.properties）先加载
- application.yml（application.properties）后加载

bootstrap.yml 用于应用程序上下文的引导阶段。

bootstrap.yml 由父Spring ApplicationContext加载。

父ApplicationContext 被加载到使用 application.yml 的之前。

## 2.配置区别

bootstrap.yml 和application.yml 都可以用来配置参数。

- bootstrap.yml 可以理解成系统级别的一些参数配置，这些参数一般是不会变动的。
- application.yml 可以用来定义应用级别的，如果搭配 spring-cloud-config 使用 application.yml 里面定义的文件可以实现动态替换。

使用Spring Cloud Config Server时，应在 bootstrap.yml 中指定：

1. spring.application.name
2. spring.cloud.config.server.git.uri
3. 一些加密/解密信息

实例：

bootstrap.yml

```
spring:
  application:
    name: service-a
  cloud:
    config:
      uri: http://127.0.0.1:8888
      fail-fast: true
      username: user
      password: ${CONFIG_SERVER_PASSWORD:password}
      retry:
        initial-interval: 2000
        max-interval: 10000
        multiplier: 2
        max-attempts: 10
```

当使用Spring Cloud时，通常从服务器加载“real”配置数据。为了获取URL（和其他连接配置，如密码等），您需要一个较早的或“bootstrap”配置。因此，您将配置服务器属性放在bootstrap.yml中，该属性用于加载实际配置数据（通常覆盖application.yml [如果存在]中的内容）。

当然，在一些情况上不用那么区分这两个文件，你只需要使用application文件即可，把全部选项都写在这里，效果基本是一致的，在不考虑上面的加载顺序覆盖的问题上。





https://blog.csdn.net/jeikerxiao/article/details/78914132