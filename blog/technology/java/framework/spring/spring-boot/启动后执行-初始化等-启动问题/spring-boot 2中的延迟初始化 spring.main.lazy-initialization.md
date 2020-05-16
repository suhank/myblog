[TOC]



# spring-boot 2中的延迟初始化 spring.main.lazy-initialization

上次修改时间：2020年3月21日

 

**[>>检查课程](https://www.baeldung.com/ls-course-start)**

## 1.概述

在本教程中，我们将看到如何从[Spring Boot 2.2](https://www.baeldung.com/new-spring-boot-2)开始在应用程序级别配置延迟初始化。

## 2.延迟初始化

在Spring中，默认情况下，所有定义的Bean及其依赖项都是在创建应用程序上下文时创建的。

**相反，当我们使用延迟初始化配置bean时，****仅在需要bean时才创建该bean，并注入其依赖项。**

## 3. Maven依赖

为了在我们的应用程序中获得Spring Boot 2.2，我们需要将其包含在类路径中。

使用Maven，我们只需要添加 [the *spring-boot-starter* dependency](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter):

```
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter</artifactId>
        <version>2.2.2.RELEASE</version>
    </dependency>
</dependencies>
```

## 4.启用延迟初始化

Spring Boot 2.2引入了*spring.main.lazy-initialization*属性，使在整个应用程序中配置延迟初始化变得更加容易。

**将属性值设置为\*true\*意味着应用程序中的所有bean将使用延迟初始化。**

让我们在*application.yml*配置文件中配置属性：

```
spring:
  main:
    lazy-initialization: true
```

或者，如果是这种情况，请在我们的*application.properties*文件中：

```
spring.main.lazy-initialization=true
```

此配置影响上下文中的所有bean。因此，如果我们想为特定bean配置延迟初始化，可以通过[*@Lazy*方法来完成](https://www.baeldung.com/spring-lazy-annotation)。

甚至，我们可以将new属性与*@Lazy*批注结合使用，设置为*false*。

换句话说，**除了我们使用\*@Lazy（false）\*****显式配置的那些bean外**，**所有定义的bean将使用延迟初始化***。*

## 5.运行

让我们创建一个简单的服务，使我们能够测试刚刚描述的内容。

通过向构造函数添加一条消息，我们将确切地知道何时创建bean。

```
public class Writer {
 
    private final String writerId;
 
    public Writer(String writerId) {
        this.writerId = writerId;
        System.out.println(writerId + " initialized!!!");
    }
 
    public void write(String message) {
        System.out.println(writerId + ": " + message);
    }
     
}
```

另外，让我们创建*SpringApplication*并注入之前创建的服务。

```
@SpringBootApplication
public class Application {
 
    @Bean("writer1")
    public Writer getWriter1() {
        return new Writer("Writer 1");
    }
 
    @Bean("writer2")
    public Writer getWriter2() {
        return new Writer("Writer 2");
    }
 
    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(Application.class, args);
        System.out.println("Application context initialized!!!");
 
        Writer writer1 = ctx.getBean("writer1", Writer.class);
        writer1.write("First message");
 
        Writer writer2 = ctx.getBean("writer2", Writer.class);
        writer2.write("Second message");
    }
}
```

让我们将*spring.main.lazy-initialization*属性值设置为*false*，然后运行我们的应用程序。

```
Writer 1 initialized!!!
Writer 2 initialized!!!
Application context initialized!!!
Writer 1: First message
Writer 2: Second message
```

如我们所见，bean是在应用程序上下文启动时创建的。

现在，将*spring.main.lazy-initialization*的值更改为*true*，然后再次运行我们的应用程序。

```
Application context initialized!!!
Writer 1 initialized!!!
Writer 1: First message
Writer 2 initialized!!!
Writer 2: Second message
```

**结果，该应用程序没有在启动时创建bean，而是仅在需要它们时才创建。**

## 6.延迟初始化的影响

在整个应用程序中启用延迟初始化可能会产生正面和负面影响。

让我们来谈谈其中的一些，正如新功能[的正式公告](https://spring.io/blog/2019/03/14/lazy-initialization-in-spring-boot-2-2)中所述：

1. 延迟初始化可以减少应用程序启动时创建的bean的数量–因此，**我们可以缩短**应用程序**的启动时间**
2. 由于在需要它们之前都不创建任何Bean，因此**我们可以掩盖问题，使其在运行时而不是启动时运行。**
3. 这些问题可能包括内存不足错误，配置错误或发现类定义的错误
4. 另外，当我们处于Web上下文中时，**按需触发Bean创建将增加HTTP请求的延迟** -Bean创建将仅影响第一个请求，但这**可能会对负载平衡和自动扩展产生负面影响**。

## 7.结论

在本教程中，我们使用Spring Boot 2.2中引入的新属性*spring.main.lazy-initialization*配置了惰性初始化。

与往常一样，可以[在GitHub上](https://github.com/eugenp/tutorials/tree/master/spring-boot-modules/spring-boot-performance)获得本教程的源代码。



https://www.baeldung.com/spring-boot-lazy-initialization