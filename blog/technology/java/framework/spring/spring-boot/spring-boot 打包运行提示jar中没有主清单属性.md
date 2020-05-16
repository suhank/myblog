[TOC]



# spring-boot 打包运行提示:jar中没有主清单属性

原创我是康小小 最后发布于2018-01-17 14:56:44 阅读数 66751  收藏
展开

## 问题分析

使用Spring Boot微服务搭建框架，在eclipse和Idea下能正常运行，但是在打成jar包部署或者直接使用java -jar命令的时候，提示了**xxxxxx.jar中没有主清单属性：**

```
D:\hu-git\spring-xxx-xxx\target>java -jar spring-cloud-eureka-0.0.1-SNAPS
HOT.jar
spring-xxx-xxx-0.0.1-SNAPSHOT.jar中没有主清单属性
```

右击选择Run as -> maven install。
在这里有一个问题就是主清单属性是什么?

以SpringBoot为例，jar包中包含了三个文件夹：BOOT-INF，META-INF，org，可以把jar包解压到文件夹下查看，其中META-INF文件夹下有一个MANIFEST.MF文件，该文件指明了程序的入口以及版本信息等内容，如下

```
Manifest-Version: 1.0
Implementation-Title: spring-xxx-xxx
Implementation-Version: 0.0.1-SNAPSHOT
Archiver-Version: Plexus Archiver
Built-By: XXXX
Implementation-Vendor-Id: com.huyikang.practice
Spring-Boot-Version: 1.5.9.RELEASE
Implementation-Vendor: Pivotal Software, Inc.
Main-Class: org.springframework.boot.loader.JarLauncher
Start-Class: com.huyikang.practice.eureka.Application
Spring-Boot-Classes: BOOT-INF/classes/
Spring-Boot-Lib: BOOT-INF/lib/
Created-By: Apache Maven 3.5.2
Build-Jdk: 1.8.0_151
Implementation-URL: http://maven.apache.org
```

- Main-Class:代表了Spring Boot中启动jar包的程序
- Start-Class:属性就代表了Spring Boot程序的入口类，这个类中应该有一个main方法
- Spring-Boot-Classes:代表了类的路径，所有编译后的class文件，以及配置文件，都存储在该路径下
- Spring-Boot-Lib:表示依赖的jar包存储的位置
  这些值都是SpringBoot打包插件会默认生成的，如果没有这些属性，SpringBoot程序自然不能运行，就会报错：jar中没有主清单属性，也就是说没有按照SpringBoot的要求，生成这些必须的属性。

## 解决办法

在pom中添加一个SpringBoot的构建的插件，然后重新运行 mvn install即可。

```xml
<build>
  <plugins>
  	<plugin>
  		<groupId>org.springframework.boot</groupId>
 			<artifactId>spring-boot-maven-plugin</artifactId>
  	</plugin>
  </plugins>
</build>
```

在运行mvn install的时候，自动生成这些主清单属性，运行java -jar xxx.jar时会根据主清单属性找到启动类，从而启动程序。

 



原文链接：https://blog.csdn.net/u010429286/article/details/79085212