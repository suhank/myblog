# Spring Boot 2.0.4.RELEASE集成 Activiti 6.0 详解

2018年09月10日 12:29:33 [xiaozaq](https://me.csdn.net/xiaozaq) 阅读数 1647



尝试直接使用activiti-spring-boot-starter-basic来与Spring Boot进行集成

用过Spring Boot的同学都知道Spring Boot Stater是个好东西，基于这个东东开发的组件基本上是零配置就能集成进Spring Boot里面，非常的方便，即官方所说的“开箱即用"，现在有不少插件都是有这个开箱即用的版本，activiti也不例外。

然而，集成之后稍微一试，控制台就无情的报错了。仔细一查控制台才发现，这个插件的最新版本是基于Activiti 6.0来做的，并不支持Spring Boot 2.0这个版本。

然后找了一下Activiti 7，当然这不是Release的版本，只存在于Github里，这一个版本是基于Spring Boot 2.0来开发的，但是它并没有开箱即用的版本。最后通过网上查找资料和自己摸索，一下方法能是项目正常运行起来。

1. 在 pom.xml里引入以下依赖

   ```
           <dependency>
               <groupId>org.activiti</groupId>
               <artifactId>activiti-spring-boot-starter-basic</artifactId>
               <version>6.0.0</version>
           </dependency>
   ```

2. 在配置文件application.properties中，设置

```
spring.activiti.check-process-definitions=false
```

3.在启动类上排除SecurityAutoConfiguration类（org.activiti.spring.boot.SecurityAutoConfiguration）

```
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
```

然后选择项目右键-》Run as -》Spring Boot App  项目启动成功，能正常访问http://localhost:8080

 

附项目所有的依赖：

```
<dependencies>
<!-- 		spring boot 2.0 不支持 activiti-spring-boot-starter-basic 6.0.0 以及之前的版本。需要做些修改
			主要是org.activiti.spring.boot.SecurityAutoConfiguration和spring自带的SecurityAutoConfiguration冲突了。
			所以需要在在启动类上排除activiti的SecurityAutoConfiguration类，就能启动成功。但不确定是否会有其他问题。需要等
			activiti-spring-boot-starter-basic 7.0.0
 -->
		<dependency>
		    <groupId>org.activiti</groupId>
		    <artifactId>activiti-spring-boot-starter-basic</artifactId>
		    <version>6.0.0</version>
		</dependency>
 
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
 
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-tomcat</artifactId>
			<scope>provided</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
		
		<dependency>
			<groupId>org.mybatis.spring.boot</groupId>
			<artifactId>mybatis-spring-boot-starter</artifactId>
			<version>1.3.2</version>
		</dependency>
 
		<dependency>
			<groupId>mysql</groupId>
			<artifactId>mysql-connector-java</artifactId>
			<scope>runtime</scope>
		</dependency>
		
 
		
	</dependencies> 
```

 



<https://blog.csdn.net/xiaozaq/article/details/82587841>