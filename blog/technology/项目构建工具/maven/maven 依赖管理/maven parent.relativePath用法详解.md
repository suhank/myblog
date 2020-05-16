# Maven parent.relativePath用法详解

springboot框架搭建的时候，采用官方文档

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>1.5.6.RELEASE</version>
    <relativePath /> <!-- lookup parent from repository -->
</parent>
```

设定一个空值将始终从仓库中获取，不从本地路径获取，如`<relativePath />`

 

**Maven parent.relativePath**

默认值为../pom.xml

查找顺序：relativePath元素中的地址–本地仓库–远程仓库





<https://www.cnblogs.com/liaojie970/p/8806843.html>