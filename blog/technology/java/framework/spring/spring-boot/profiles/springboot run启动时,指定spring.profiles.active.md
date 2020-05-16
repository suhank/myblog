[TOC]



# spring-boot:run启动时，指定spring.profiles.active

## maven的profile

Maven启动指定Profile通过-P，如`mvn spring-boot:run -Ptest`，但这是Maven的Profile。



## maven启动spring-boot的profile

可以使用profiles参数指定用于特定应用程序的活动配置文件。以下配置启用foo和bar配置文件：

```
<project>
  ...
  <build>
    ...
    <plugins>
      ...
      <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
        <version>2.0.2.RELEASE</version>
        <configuration>
          <profiles>
            <profile>foo</profile>
            <profile>bar</profile>
          </profiles>
        </configuration>
        ...
      </plugin>
      ...
    </plugins>
    ...
  </build>
  ...
</project>
```

要启用的配置文件也可以在命令行中指定，请确保使用逗号分隔它们，即：

```
mvn spring-boot:run -Dspring-boot.run.profiles=foo,bar
```



## 直接java运行jar方式指定profile

如果使用命令行直接运行jar文件，则使用`java -jar -Dspring.profiles.active=test demo-0.0.1-SNAPSHOT.jar`



## ide指定profile

如果使用开发工具，运行Application.java文件启动，则增加参数`--spring.profiles.active=test`





http://www.jspxcms.com/knowledge/341.html