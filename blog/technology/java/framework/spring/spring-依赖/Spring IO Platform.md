# Spring IO Platform

转自：[spring io 平台介绍](https://link.jianshu.com/?t=http://www.wangmin.me/2015/01/16/spring_io_platform.html)

Spring IO Platform reference对Spring IO的介绍如下：

> Spring IO Platform is primarily intended to be used with a dependency management system. It works well with both Maven and Gradle.

具体如何理解Spring IO Platform 的作用了？

以前在升级Spring项目的时候是手动的一个一个升级Spring模块的版本，并且一个模块与另一个模块之间的依赖适不适合你并不知道，你还需要测试或者找资料，所以比较麻烦。Spring IO Platform它能够结合Maven (或Gradle)管理每个模块的依赖，使得开发者不再花心思研究各个Java库相互依赖的版本，只需要引入Spring IO Platform即可，因为这些库的依赖关系Spring IO Platform已经帮你验证过了。

在Maven中的使用也比较简单，只需要在pom.xml文件中加入依赖管理就可：

```
<dependencyManagement>
    <dependencies>
        <dependency> 
            <groupId>io.spring.platform</groupId>
            <artifactId>platform-bom</artifactId>
            <version>2.0.1. RELEASE </version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

我的观点：Spring IO Platform只是一个pom文件，记录了spring与其他开源项目对应的版本。省去了版本号，也就省去了处理依赖时的问题，因为Spring IO Platform中有最优的版本配置。

最经典的BOM有三种：spring-framework-bom、spring-boot-dependencies、platform-bom，当然，还有其他的BOM，例如spring-integration-bom和spring-security-bom。

参考资料：[Spring IO platform](https://link.jianshu.com/?t=http://platform.spring.io/platform/)



https://www.jianshu.com/p/ce29422e3d1a