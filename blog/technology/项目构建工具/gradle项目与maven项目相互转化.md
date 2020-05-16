[TOC]



# gradle项目与maven项目相互转化

gradle这几年发展迅猛，github越来越多的项目都开始采用gradle来构建了，但是并不是所有人都对gradle很熟悉，下面的方法可以把gradle转成maven项目，前提gradle项目目录结构保持跟maven一样的约定，即/src/main/java这一套。

## 一、gradle --> maven

在build.gradle中增加以下内容(group,version可自行修改，artifactId默认为目录名称)

```
apply plugin: 'java'
apply plugin: 'maven'

group = 'com.101tec'
version = '0.7-dev'
sourceCompatibility = 1.8
```

然后./gradlew build ，成功后将在build\poms目录下生成pom-default.xml文件，把它复制到根目录下，改名成pom.xml即可

当然，通过修改build.gradle 也可以直接在根目录下生成pom.xml

```
task writeNewPom {
    pom {
        project {
//            inceptionYear '2018'
//            licenses {
//                license {
//                    name 'The Apache Software License, Version 2.0'
//                    url 'http://www.apache.org/licenses/LICENSE-2.0.txt'
//                    distribution 'repo'
//                }
//            }
        }
    }.writeTo("pom.xml")
}
```

## 二、maven --> gradle

先保证本机安装了gradle 2.0以上的版本

然后在maven根目录下运行

gradle init --type pom





http://www.cnblogs.com/yjmyzz/p/gradle-to-maven.html