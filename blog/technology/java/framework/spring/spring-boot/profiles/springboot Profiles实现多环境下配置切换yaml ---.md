[TOC]



# Spring Boot Profiles实现多环境下配置切换yaml ---

在后端开发中,应用程序在不同的环境可能会有不同的配置，例如数据库连接、日志级别等，开发，测试，生产每个环境可能配置都不一致。

使用Spring Boot的Profile可以实现多场景下的配置切换，方便开发中进行测试和部署生产环境。 下面就大致介绍一下yml配置文件跟properties配置文件怎么使用profile配置不同环境的配置文件。

## 开发环境

- JDK 1.8
- Maven 3.x
- Spring Boot 1.5.8
- Intellij Idea 2017

## 一、使用Spring Boot Profiles

### 1. 使用yml文件

首先,我们先创建一个名为 application.yml的属性文件,如下:

```Yaml
#公共部分
server:
  port: 8080

my:
  name: demo

spring:
  profiles:
    active: dev #默认的profile 

---
#development environment
spring:
  profiles: dev

server:
  port: 8160

my:
  name: ricky

---
#test environment
spring:
  profiles: test

server:
  port: 8180

my:
  name: test

---
#production environment
spring:
  profiles: prod

server:
  port: 8190

my:
  name: prod
```

application.yml文件分为四部分,使用 `---` 来作为分隔符，第一部分通用配置部分，表示三个环境都通用的属性， 后面三段分别为：开发，测试，生产，用spring.profiles指定了一个值(开发为dev，测试为test，生产为prod)，这个值表示该段配置应该用在哪个profile里面。

如果我们是本地启动，在通用配置里面可以设置调用哪个环境的profil，也就是第一段的spring.profiles.active=XXX， 其中XXX是后面3段中spring.profiles对应的value,通过这个就可以控制本地启动调用哪个环境的配置文件，例如:

```
spring:
    profiles:
        active: dev
```

表示默认 加载的就是开发环境的配置，如果dev换成test，则会加载测试环境的属性，以此类推。

**注意：如果spring.profiles.active没有指定值，那么只会使用没有指定spring.profiles文件的值，也就是只会加载通用的配置。**

#### 启动参数

如果是部署到服务器的话,我们正常打成jar包，启动时通过 `--spring.profiles.active=xxx` 来控制加载哪个环境的配置，完整命令如下:

```
java -jar xxx.jar --spring.profiles.active=test 表示使用测试环境的配置

java -jar xxx.jar --spring.profiles.active=prod 表示使用生产环境的配置
```

#### 使用多个yml配置文件进行配置属性文件

我们也可以使用多个yml来配置属性，将于环境无关的属性放置到application.yml文件里面；通过与配置文件相同的命名规范，创建application-{profile}.yml文件 存放不同环境特有的配置，例如 application-test.yml 存放测试环境特有的配置属性，application-prod.yml 存放生产环境特有的配置属性。

通过这种形式来配置多个环境的属性文件，在application.yml文件里面`spring.profiles.active=xxx`来指定加载不同环境的配置,如果不指定，则默认只使用application.yml属性文件，不会加载其他的profiles的配置。

### 2. 使用properties文件

如果使用application.properties进行多个环境的配置，原理跟使用多个yml配置文件一致，创建application-{profile}.properties文件 存放不同环境特有的配置，将于环境无关的属性放置到application.properties文件里面，并在application.properties文件中通过`spring.profiles.active=xxx` 指定加载不同环境的配置。如果不指定，则默认加载application.properties的配置，不会加载带有profile的配置。

## 二、Maven Profile

如果我们使用的是构建工具是Maven，也可以通过Maven的profile特性来实现多环境配置打包。

pom.xml配置如下：

```xml
<profiles>
        <!--开发环境-->
        <profile>
            <id>dev</id>
            <properties>
                <build.profile.id>dev</build.profile.id>
            </properties>
            <activation>
                <activeByDefault>true</activeByDefault>
            </activation>
        </profile>
        <!--测试环境-->
        <profile>
            <id>test</id>
            <properties>
                <build.profile.id>test</build.profile.id>
            </properties>
        </profile>
        <!--生产环境-->
        <profile>
            <id>prod</id>
            <properties>
                <build.profile.id>prod</build.profile.id>
            </properties>
        </profile>
    </profiles>

    <build>
        <finalName>${project.artifactId}</finalName>
        <resources>
            <resource>
                <directory>src/main/resources</directory>
                <filtering>false</filtering>
            </resource>
            <resource>
                <directory>src/main/resources.${build.profile.id}</directory>
                <filtering>false</filtering>
            </resource>
        </resources>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <classifier>exec</classifier>
                </configuration>
            </plugin>
        </plugins>
    </build>
```

通过执行 `mvn clean package -P ${profile}` 来指定使用哪个profile。

## 三、获取profile环境

环境工具类

```java
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

/**
 * <B>Description:</B> 环境工具类  <br>
 * <B>Create on:</B> 2016/10/31 18:48 <br>
 * 使用说明: 参考 ProjectConfig
 * @author xiangyu.ye
 * @version 1.0
 */
public class EnvironmentUtil {

    @Autowired
    private Environment env;

    public String       LOCAL = "local";//本地
    public String       TEST  = "test"; //测试环境
    public String       PROD  = "prod"; //生产环境

    /**
     * <B>Description:</B> 获取环境 <br>
     * <B>Create on:</B> 2016/10/31 18:49 <br>
     *
     * @author xiangyu.ye
     */
    public String getEnvironment() {
        String environment = "";
        String[] activeProfiles = env.getActiveProfiles();
        if (activeProfiles != null && activeProfiles.length > 0) {
            environment = activeProfiles[0];
        }
        AssertUtil.notNullOrEmpty(environment, "获取不到环境参数！");
        return environment;
    }

    /**
     * <B>Description:</B> 是否是local环境 <br>
     * <B>Create on:</B> 2016/10/31 18:49 <br>
     *
     * @author xiangyu.ye
     */
    public boolean isLocal() {
        String environment = getEnvironment();
        if (LOCAL.equals(environment)) {
            return true;
        }
        return false;
    }

    /**
     * <B>Description:</B> 是否是 test环境 <br>
     * <B>Create on:</B> 2016/10/31 18:49 <br>
     *
     * @author xiangyu.ye
     */
    public boolean isTest() {
        String environment = getEnvironment();
        if (TEST.equals(environment)) {
            return true;
        }
        return false;
    }

    /**
     * <B>Description:</B> 是否是 prod 环境 <br>
     * <B>Create on:</B> 2016/10/31 18:49 <br>
     *
     * @author xiangyu.ye
     */
    public boolean isProd() {
        String environment = getEnvironment();
        if (PROD.equals(environment)) {
            return true;
        }
        return false;
    }

}

```



配置类

```java
/**
 * <B>Description:</B> 项目配置 <br>
 * <B>Create on:</B> 2018/4/19 下午10:16 <br>
 *
 * @author xiangyu.ye
 * @version 1.0
 */
@Configuration
@Import(EnvironmentUtil.class)
public class ProjectConfig {


}

```



## 参考资料

[Spring Boot Reference Guide - Profiles](https://docs.spring.io/spring-boot/docs/1.5.8.RELEASE/reference/htmlsingle/#boot-features-profiles) 
[Maven profiles](http://maven.apache.org/guides/introduction/introduction-to-profiles.html)