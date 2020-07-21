[TOC]



# spring-boot中读取config配置文件的两种方式

2016-10-17 18:37:04

 了解过spring-boot这个技术的，应该知道Spring-Boot的核心配置文件application.properties，当然也可以通过注解自定义配置文件的信息。

Spring-Boot读取配置文件的方式：

## 一.读取核心配置文件信息application.properties的内容

​     核心配置文件是指在resources根目录下的application.properties或application.yml配置文件，读取这两个配置文件的方法有两种，都比较简单。

核心配置文件application.properties内容如下：

```java
test.msg=Hello World SpringBoot
```

### 方式一：使用@Value方式（常用）

```java
package Solin.controller;
 
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
 
@RestController
public class WebController {
	@Value("${test.msg}")
	private String msg;
	
	@RequestMapping("/index1") 
	public String index1(){
		return "方式一:"+msg;
	}
}
```

注意：在@Value的${}中包含的是核心配置文件中的键名。在Controller类上加@RestController表示将此类中的所有视图都以JSON方式显示，类似于在视图方法上加@ResponseBody。
访问：http://localhost:8088/index1时得到："方式一:Hello World SpringBoot"

### 方式二：使用Environment方式

```java
package Solin.controller;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
 
@RestController
public class WebController {
	@Autowired
	private Environment env;
	
	@RequestMapping("/index2") 
	public String index2(){
		return "方式二:"+env.getProperty("test.msg");
	}
}
```

注意：这种方式是依赖注入Evnironment来完成，在创建的成员变量private Environment env上加上@Autowired注解即可完成依赖注入，然后使用env.getProperty("键名")即可读取出对应的值。
访问：http://localhost:8088/index2时得到："方式二:Hello World SpringBoot"





## 二.读取自定义配置文件信息，例如：**author**.properties

为了不破坏核心文件的原生态，但又需要有自定义的配置信息存在，一般情况下会选择自定义配置文件来放这些自定义信息，这里在`resources`目录下创建配置文件author.properties

`resources/author.properties`内容如下：

```java
author.name=Solin
author.age=22
```

创建管理配置的实体类：

```java
package Solin.controller;
 
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
 
//加上注释@Component，可以直接在其他地方使用@Autowired来创建其实例对象
@Component
@ConfigurationProperties(prefix = "author",locations = "classpath:author.properties")   
public class MyWebConfig{
	private String name;
	private int age;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
}
```

注意：
​    

在@ConfigurationProperties注释中有两个属性：
locations：指定配置文件的所在位置
prefix：指定配置文件中键名称的前缀（我这里配置文件中所有键名都是以author.开头）
​    

使用@Component是让该类能够在其他地方被依赖使用，即使用@Autowired注释来创建实例。

创建测试Controller

```java
package Solin.controller;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
 
@Controller  
public class ConfigController {
	@Autowired
	private MyWebConfig conf;
	
	@RequestMapping("/test") 
	public @ResponseBody String test() {
		return "Name:"+conf.getName()+"---"+"Age:"+conf.getAge(); 
	}
}
```



注意：由于在Conf类上加了注释@Component，所以可以直接在这里使用@Autowired来创建其实例对象。

访问：http://localhost:8088/test时得到："Name:Solin---Age:22"







<https://blog.csdn.net/qq_32786873/article/details/52840745>