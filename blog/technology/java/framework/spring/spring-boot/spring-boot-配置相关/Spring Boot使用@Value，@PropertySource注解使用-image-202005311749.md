# Spring Boot使用@Value，@PropertySource注解使用

2017-05-29 18:28:26

# 一 @Value

在Spring Boot中，有些变量根据需求配置在application.properties中，

在应用程序中使用@Value注解获取值。比如：

在配置application.properteis配置一个键值对:

```html
test.value=I am Chinese!我是中国人!
```

程序中获取方式：

```java
/**
 * 使用@value注解，从配置文件读取值
 */
@Value("${test.value}")
private String testValueAnno;
```

将变量testValueAnno值初始化为：test.value=I am Chinese!我是中国人!

# 二 @PropertySource

@PropertySource注解用于指定目录读取properties文件，如果将“test.value”在配置文件中对应的值加上中文，

通过@Value读取到的值会出现中文乱码。

# 三 @Value读取值中文乱码解决方案

首先，要保证properties文件是utf-8编码的，idea工具可以在File-Encoding设置，

设置为utf-8编码后还是乱码，可以试试以下几种方式处理乱码问题。

## 方案1

通过@PropertySource注解指定文件路径，通过utf-8的编码读取文件。

```java
@PropertySource(value = {"classpath:application.properties"}, encoding = "utf-8")
```

## **方案2**

把中文转成unicode编码作为值，因为Spring Boot加载application.properties采用的是unicode编码形式。

```java
test.value=\u0049\u0020\u0061\u006d\u0020\u0043\u0068\u0069\u006e\u0065\u0073\u0065\u0021\u6211\u662f\u4e2d\u56fd\u4eba\u0021
```

## **方案3**

改用application.yml方式配置，yml支持中文直接编写而不乱码。

```html
test:
  value: I am Chinese!我是中国人!
```

建议：Spring Boot项目尽量使用application.yml方式，不要用properties方式，更不要使用yml和properties混合方式。 

# 四 @Value和@PropertySource项目实例

**1、ValueController.java**

```java
package com.lanhuigu.springboot.controller;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
 
/**
 * @RestController这个注解等价于spring mvc用法中的@Controller+@ResponseBody
 * @auther: yihonglei
 * @date: 2019-06-04 17:50
 */
@RestController
@PropertySource(value = {"classpath:application.properties"}, encoding = "utf-8")
public class ValueController {
    /**
     * 使用@value注解，从配置文件读取值
     */
    @Value("${test.value}")
    private String testValueAnno;
 
    @RequestMapping(value = "myValue")
    @ResponseBody
    private String testValue() {
        System.out.println("测试:" + testValueAnno);
 
        return testValueAnno;
    }
}
```

**2、properties配置（github项目在application-dev.propeties）**

```html
test.value=I am Chinese!我是中国人!
```

**3、启动类**

```java
package com.lanhuigu.springboot;
 
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
 
/**
 * Spring Boot启动类
 *
 * @author yihonglei
 */
@SpringBootApplication
@EnableScheduling
public class BootApplication {
    public static void main(String[] args) {
        SpringApplication.run(BootApplication.class, args);
    }
}
```

**4、访问项目**

http://localhost:7000/myValue

![img](image-202005311749/20190604182119836.png)https://blog.csdn.net/yhl_jxy/article/details/72803278