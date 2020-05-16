[TOC]



# maven可选依赖（Optional Dependencies）和依赖排除（Dependency Exclusions）

## 前言

​    我们知道，maven的依赖关系是有传递性的。如：A-->B，B-->C。但有时候，项目A可能不是必需依赖C，因此需要在项目A中排除对A的依赖。在maven的依赖管理中，有两种方式可以对依赖关系进行，分别是可选依赖（Optional Dependencies）以及依赖排除（Dependency Exclusions）。

## 一、可选依赖

​    当一个项目A依赖另一个项目B时，项目A可能很少一部分功能用到了项目B，此时就可以在A中配置对B的可选依赖。举例来说，一个类似hibernate的项目，它支持对mysql、oracle等各种数据库的支持，但是在引用这个项目时，我们可能只用到其对mysql的支持，此时就可以在这个项目中配置可选依赖。

​    配置可选依赖的原因：1、节约磁盘、内存等空间；2、避免license许可问题；3、避免类路径问题，等等。

​    示例：

```html
<project>
  ...
  <dependencies>
    <!-- declare the dependency to be set as optional -->
    <dependency>
      <groupId>sample.ProjectB</groupId>
      <artifactId>Project-B</artifactId>
      <version>1.0</version>
      <scope>compile</scope>
      <optional>true</optional> <!-- value will be true or false only -->
    </dependency>
  </dependencies>
</project>

```

​    假设以上配置是项目A的配置，即：Project-A --> Project-B。在编译项目A时，是可以正常通过的。

​    如果有一个新的项目X依赖A，即：Project-X -> Project-A。此时项目X就不会依赖项目B了。如果项目X用到了涉及项目B的功能，那么就需要在pom.xml中重新配置对项目B的依赖。

**例如**: spring-boot-autoconfigure-2.0.0.RELEASE.jar 全部jar都是\<optional>true\</optional>

## 二、依赖排除

​    当一个项目A依赖项目B，而项目B同时依赖项目C，如果项目A中因为各种原因不想引用项目C，在配置项目B的依赖时，可以排除对C的依赖。

​    示例（假设配置的是A的pom.xml，依赖关系为：A --> B; B --> C）：

```html
<project>
  ...
  <dependencies>
    <dependency>
      <groupId>sample.ProjectB</groupId>
      <artifactId>Project-B</artifactId>
      <version>1.0</version>
      <scope>compile</scope>
      <exclusions>
        <exclusion>  <!-- declare the exclusion here -->
          <groupId>sample.ProjectC</groupId>
          <artifactId>Project-C</artifactId>
        </exclusion>
      </exclusions> 
    </dependency>
  </dependencies>
</project>
```

​    当然，对于多重依赖，配置也很简单，参考如下示例：

```html
Project-A
   -> Project-B
        -> Project-D 
              -> Project-E <! -- This dependency should be excluded -->
              -> Project-F
   -> Project C 
```

​    A对于E相当于有多重依赖，我们在排除对E的依赖时，只需要在配置B的依赖中进行即可：

```html
<project>
  <modelVersion>4.0.0</modelVersion>
  <groupId>sample.ProjectA</groupId>
  <artifactId>Project-A</artifactId>
  <version>1.0-SNAPSHOT</version>
  <packaging>jar</packaging>
  ...
  <dependencies>
    <dependency>
      <groupId>sample.ProjectB</groupId>
      <artifactId>Project-B</artifactId>
      <version>1.0-SNAPSHOT</version>
      <exclusions>
        <exclusion>
          <groupId>sample.ProjectE</groupId> <!-- Exclude Project-E from Project-B -->
          <artifactId>Project-E</artifactId>
        </exclusion>
      </exclusions>
    </dependency>
  </dependencies>
</project> 
```



参考资料：

1、maven官网：<http://maven.apache.org/guides/introduction/introduction-to-optional-and-excludes-dependencies.html>





https://blog.csdn.net/ado1986/article/details/39547839