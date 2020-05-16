[TOC]



# springboot 非web应用程序实例

## SpringBootApplication配置

### 方式一: 推荐

```java
 
@SpringBootApplication
public class SpringBootConsoleApplication   {
    public static void main(String[] args) throws Exception {
    	new SpringApplicationBuilder(SpringBootConsoleApplication.class)
            .run(args);
        //SpringApplication.run(SpringBootConsoleApplication.class, args);
    }
}
```



### 方式二:

在Spring Boot中，要创建一个非Web应用程序，实现`CommandLineRunner`并覆盖`run()`方法，例如：

```java
import org.springframework.boot.CommandLineRunner;

@SpringBootApplication
public class SpringBootConsoleApplication implements CommandLineRunner {

    public static void main(String[] args) throws Exception {

        SpringApplication.run(SpringBootConsoleApplication.class, args);

    }

    //access command line arguments
    @Override
    public void run(String... args) throws Exception {
        //do something
    }
}
 
```

## 项目依赖

只有依赖 `spring-boot-starter` 库，参考如下 `pom.xml` -

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.yiibai</groupId>
    <artifactId>spring-boot-non-web</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <packaging>jar</packaging>

    <name>spring-boot-non-web</name>
    <url>http://maven.apache.org</url>

    <properties>
        <java.version>1.8</java.version>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencies>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>3.8.1</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
            <exclusions>
                <exclusion>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-starter-tomcat</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
    </dependencies>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>1.5.1.RELEASE</version>
    </parent>

    <build>
        <plugins>
            <!-- Package as an executable jar/war -->
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>


XML
```

## HelloMessageService测试类

返回消息的服务，如下 *HelloMessageService.java* 代码所示 -

```java
package com.yiibai.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class HelloMessageService {

    @Value("${name:unknown}")
    private String name;

    public String getMessage() {
        return getMessage(name);
    }

    public String getMessage(String name) {
        return "Hello " + name;
    }

}
 
```

属性文件配置文件： *application.properties* 如下所示 -

```Shell
name=yiibai
```

下面是`CommandLineRunner`示例,如果运行这个Spring Boot，那么`run`方法将是入口点。
*SpringBootConsoleApplication.java* 代码内容如下所示 -

```Java
package com.yiibai;

import com.yiibai.service.HelloMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static java.lang.System.exit;

@SpringBootApplication
public class SpringBootConsoleApplication implements CommandLineRunner {

    @Autowired
    private HelloMessageService helloService;

    public static void main(String[] args) throws Exception {

        //disabled banner, don't want to see the spring logo
        SpringApplication app = new SpringApplication(SpringBootConsoleApplication.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);

    }

    // Put your logic here.
    @Override
    public void run(String... args) throws Exception {

        if (args.length > 0) {
            System.out.println(helloService.getMessage(args[0].toString()));
        } else {
            System.out.println(helloService.getMessage());
        }

        exit(0);
    }
}

```

## 实例运行演示

打包上面的项目并运行它，如下命令 -

```shell
## Go to project directory
## package it
$ mvn package

$ java -jar target/spring-boot-non-web-0.0.1-SNAPSHOT.jar
Hello yiibai

$ java -jar target/spring-boot-non-web-0.0.1-SNAPSHOT.jar "Max su"
Hello Max su
```





https://www.yiibai.com/spring-boot/non-web-application-example.html