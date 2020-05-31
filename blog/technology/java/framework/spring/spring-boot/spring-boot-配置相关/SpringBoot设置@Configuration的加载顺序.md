# SpringBoot设置@Configuration的加载顺序

0.3922018.12.27 09:12:56字数 277阅读 29,032

------

#### 1、简介

Spring Boot会检查你发布的jar中是否存在`META-INF/spring.factories`文件，该文件中以`EnableAutoConfiguration`为key的属性应该列出你的配置类：

```undefined
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
com.mycorp.libx.autoconfigure.LibXAutoConfiguration,\
com.mycorp.libx.autoconfigure.LibXWebAutoConfiguration
```

你可以使用[`@AutoConfigureAfter`](http://github.com/spring-projects/spring-boot/tree/master/spring-boot-autoconfigure/src/main/java/org/springframework/boot/autoconfigure/AutoConfigureAfter.java)或[`@AutoConfigureBefore`](http://github.com/spring-projects/spring-boot/tree/master/spring-boot-autoconfigure/src/main/java/org/springframework/boot/autoconfigure/AutoConfigureBefore.java)注解为配置类指定特定的顺序。例如，如果你提供web-specific配置，你的类就需要应用在`WebMvcAutoConfiguration`后面。

你也可以使用`@AutoconfigureOrder`注解为那些相互不知道存在的自动配置类提供排序，该注解语义跟常规的`@Order`注解相同，但专为自动配置类提供顺序。

> 注：自动配置类只能通过这种方式加载，确保它们定义在一个特殊的package中，特别是不能成为组件扫描的目标。

#### 2、例子

例如下面的项目，想要保证加载`BananaConf.java`后再加载`AppleConf.java`

![img](https://upload-images.jianshu.io/upload_images/7378160-7dbab31c3d909ab6.png?imageMogr2/auto-orient/strip|imageView2/2/w/234/format/webp)

目录结构


1）添加文件`spring.factories`（若没有`META-INF`文件夹，则需要先添加）
2）在`spring.factories`中加入





```undefined
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
com.example.demo.conf.AppleConf,\
com.example.demo.conf.BananaConf
```

3）`AppleConf`中添加 `@AutoConfigureAfter(BananaConf.class)`



```kotlin
@Configuration
@AutoConfigureAfter(BananaConf.class)
public class AppleConf
{
    xxx
}
```

`BananaConf`正常配置即可



```java
@Configuration
public class BananaConf
{
    xxx
}
```

注意：在spring.factories注册了的配置类，可以不用再该配置类写@Configuration



只会对META-INF/spring.factories里面的配置类进行排序，如果配置类被@SpringBootApplication扫描到，那么这个@AutoConfigureAfter也会失效



https://www.jianshu.com/p/217ccb986290