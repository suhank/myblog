# @PropertySource注解根据不同环境动态加载自定义配置文件

2019-08-15 17:57:04

### 1、自定义配置文件common-as-dev.properties

```properties
stu.name=zhangsan
stu.age=18
stu.sex=man
```

### 2、pom里配置环境

```xml
<profile>

    <id>as-dev</id>
    <properties>
        <port>8081</port>
        <ctx>/dev</ctx>
        <env>as-dev</env>
    </properties>

    <activation>
        <activeByDefault>true</activeByDefault>
    </activation>

</profile>
```

### 3、application.properties里引用环境

```properties
spring.profiles.active=@env@
server.port=@port@
server.servlet.context-path=@ctx@
```

### 4、@PropertySource加载

```java
@PropertySource("classpath:/common-${spring.profiles.active}.properties")
@Configuration
public class LoadProperties {
}
```

### 5、@Value注解引用

```java
@Value("${stu.name}")
private String stuName;
```

 



https://blog.csdn.net/xl_1803/article/details/99647366