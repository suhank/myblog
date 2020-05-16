[TOC]



# maven 单独部署pom或者jar文件

有时候我们会需要单独部署一个文件到maven的本地库或者远程库中，一般来说会是一个比较高层的pom文件，可以使用如下命令： 

## deploy语法

```
mvn deploy:deploy-file -Dfile=[your file] -DgroupId=[xxxx] -DartifactId=[xxxx] -Dversion=[xxxx] -Dpackaging=[pom|jar|other] -DrepositoryId=[id] -Durl=[repo url]  
```

## 示例

### 部署单个pom.xml文件

```sh
mvn deploy:deploy-file -Dfile=pom.xml -DgroupId=com.red.wms.debuglog -DartifactId=debuglog-parent -Dversion=1.0.0-SNAPSHOT -Dpackaging=pom -DrepositoryId=snapshots -Durl=http://115.159.115.190:8081/repository/maven-snapshots/
```

### 部署 第三方jar

```sh
mvn -X deploy:deploy-file -DgroupId=com.baiwang -DartifactId=sdk -Dversion=20190102 -Dpackaging=jar -Dfile=/Users/morrissss/Downloads/baiwang-bopsdk-1.3.4.jar -Durl=http://mvn.devops.xiaohongshu.com/repository/maven-releases -DrepositoryId=releases
```



对于deploy命令来说，file、repositoryId和url是必选的。其中repositoryId是指远程maven库的id，一般会配置在setting.xml文件里面，是在\<server>标签下的那个id。url比较重要，如果错误的话，是无法上传的，一般来说我们可以在setting.xml文件的\<server>标签下找到，但是这个\<server>下的这个url不一定就是真正的maven库地址，如果这个url不行，那么可以尝试把这个url中的最后一个路径替换成\<server>标签下\<id>自标签的内容，一般来说是可以行的。如果再不行，就要文人了。 
<http://maven.apache.org/plugins/maven-deploy-plugin/>

<https://maven.apache.org/guides/mini/guide-3rd-party-jars-remote.html>

<http://maven.apache.org/ref/3.6.0/maven-embedder/cli.html>



<https://mark-ztw.iteye.com/blog/1823677>