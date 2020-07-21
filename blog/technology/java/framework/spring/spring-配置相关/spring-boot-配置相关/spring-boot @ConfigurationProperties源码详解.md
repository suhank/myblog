[TOC]



# springboot @ConfigurationProperties源码详解 

## **概述**

之前介绍过Spring中的配置类@Configuration，在配置类中通过注入Environment或者@Value，我们可以拿到外部配置数据。在Spring boot中，框架默认提供了application,properties配置文件来提供系统配置，那么有没有更好的办法来获取外部配置呢？ 那就是@ConfigurationProperties。

## 应用示例 

有时候有这样子的情景，我们想把配置文件的信息，读取并自动封装成实体类，这样子，我们在代码里面使用就轻松方便多了，这时候，我们就可以使用@ConfigurationProperties，它可以把同类的配置信息自动封装成实体类

首先在配置文件里面，这些信息是这样子滴

```
connection.username=admin
connection.password=kyjufskifas2jsfs
connection.remoteAddress=192.168.1.1
```

这时候我们可以定义一个实体类在装载配置文件信息

```java
@Component
@ConfigurationProperties(prefix="connection")
public class ConnectionSettings {

    private String username;
    private String remoteAddress;
    private String password ;

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getRemoteAddress() {
        return remoteAddress;
    }
    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

} 
```

我们还可以把@ConfigurationProperties还可以直接定义在@bean的注解上，这是bean实体类就不用@Component和@ConfigurationProperties了

```java
@SpringBootApplication
public class DemoApplication{

    //...

    @Bean
    @ConfigurationProperties(prefix = "connection")
    public ConnectionSettings connectionSettings(){
        return new ConnectionSettings();

    }

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
} 
```

然后我们需要使用的时候就直接这样子注入

```java
@RestController
@RequestMapping("/task")
public class TaskController {

@Autowired ConnectionSettings conn;

@RequestMapping(value = {"/",""})
public String hellTask(){
    String userName = conn.getUsername();     
    return "hello task !!";
}

} 
```

如果发现@ConfigurationPropertie不生效，有可能是项目的目录结构问题，你可以通过@EnableConfigurationProperties(ConnectionSettings.class)来明确指定需要用哪个实体类来装载配置信息。

 

## 工作原理 

前面我们已经分析过了，spring的starter再启动的时候回加载configure

那么我们去看看spring-boot-autoconfigure的配置文件spring.factories 
可以看到org.springframework.boot.autoconfigure.EnableAutoConfiguration的配置中有一个org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration继续看进去，calss上有个注解EnableConfigurationProperties，再进去发现是一个EnableConfigurationPropertiesImportSelector 
这时候我们就知道了，在启动的过程中 spring会去EnableConfigurationPropertiesImportSelector来注册bean 并且处理







https://blog.csdn.net/yingxiake/article/details/51263071