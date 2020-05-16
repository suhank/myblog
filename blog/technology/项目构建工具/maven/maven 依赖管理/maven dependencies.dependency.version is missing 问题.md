[TOC]



# Maven dependencies.dependency.version is missing 问题

2016-12-30 10:10:00

   

子模块为web项目，在写依赖时，没有写version的信息。在父pom中使用dependencyManagement中写明了version 
于是pom报错：**dependencies.dependency.version is missing** 
原因及解决办法： 

1、 maven 对于父项目定义的dependencyManagement 中的 非 jar 类型的 type节点不会继承，也就是说子项目中必须再次声明非 jar 类型的type即可。

2、

- 在子项目中对于缺少版本号的依赖写上版本号；
- 删除C:\Users\server.m2\repository目录下的.cache目录；
- 增加一个relativePath项到parent节点中

```xml
 <parent>
        <artifactId>modeling-parent</artifactId>
        <groupId>whu.lmars.modolingtool</groupId>
        <version>1.0-SNAPSHOT<ersion>
        <relativePath>../pom.xml</relativePath>
</parent> 
```

再重新运行mvn命令，删去子项目中的版本号





<https://blog.csdn.net/u013177446/article/details/53939805>