[TOC]



# maven传递依赖无法引入问题（The POM for xxx is invalid）

阅读 864

收藏 4

2017-09-18

原文链接：[blog.csdn.net](https://link.juejin.im/?target=http%3A%2F%2Fblog.csdn.net%2Fxktxoo%2Farticle%2Fdetails%2F78005817)

WebGL 入门与实践小册，提前预售，限时优惠速抢！0x7.me

#### 一、背景

　　应用A直接应用B，应用B依赖二方包C1、C2、C3，应用A传递依赖C1、C2、C3。现应用B升级版本，应用更新B依赖包后发现可正常引入依赖B，但传递依赖的C1、C2、C3不能引入。 　　

#### 二、问题排查

　　应用根目录打印依赖树：

```
mvn dependency:tree>tree.txt
```

　　应用依赖树中出现如下警告。警告显示：应用引入的依赖包无效，依赖包中传递依赖项不可用，可以通过开启debug获取更多信息。

```
...
[WARNING] The POM for com.xxx.yyy:zzz:jar:1.0.1-SNAPSHOT is invalid,
 transitive dependencies (if any) will not be available, enable debug
logging for more 
...
```

　　开启debug功能，重新打印依赖树：

```
mvn -X dependency:tree>tree.txt
```

　　开启maven debug功能后，警告后紧跟了一条错误信息，如下。错误原因：传递依赖项中有依赖项版本缺失。

```
...
[WARNING] The POM for com.xxx.yyy:zzz:jar:1.0.1-SNAPSHOT is invalid, 
transitive dependencies (if any) will not be available: 1 problem was 
encountered while building the effective model for com.xxx.yyy:zzz:jar:1.0.1-SNAPSHOT
[ERROR] 'dependencies.dependency.version' for com.xxx.mmm.nnn:jar is missing. @ 
...
```

#### 三、解决方案

　　重新提交应用B父POM到远程仓库，删除本地maven仓库中应用B相关依赖包，在应用A中更新maven依赖，一切OK。



<https://juejin.im/entry/59bd40a2f265da06633d1529>