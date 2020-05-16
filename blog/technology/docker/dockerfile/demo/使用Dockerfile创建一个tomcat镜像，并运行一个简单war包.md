# 使用Dockerfile创建一个tomcat镜像，并运行一个简单war包

/Users/jerryye/backup/studio/AvailableCode/docker/dockerfile_demo/dockerfile_test



docker已经看了有一段时间了，对镜像和容器也有了一个大致了解，参考书上的例子制作一个tomcat镜像，并简单运行一个HelloWorld.war



## 运行tomcat



1.首先下载linux环境的tomcat和jdk，并分别解压至helloworld目录

![img](https://images2018.cnblogs.com/blog/1158674/201803/1158674-20180310192932109-298170324.png)

2.新建Dockerfile文件

```
touch Dockerfile
```

Dockerfile文件的内容如下： 

```dockerfile
FROM daocloud.io/centos:7
MAINTAINER yxy
ENV REFRESHED_AT 2018-03-10

#切换镜像目录，进入/data/myworkspace目录
WORKDIR /data/myworkspace

#copy命令如果目的文件不存在会自动创建
#将宿主机的jdk目录下的文件拷至镜像的/data/myworkspace/jdk目录下
COPY "/software/jdk1.8.0_144" "/data/myworkspace/jdk/"
#将宿主机的tomcat目录下的文件拷至镜像的/data/myworkspace/tomcat目录下
COPY "/software/apache-tomcat-8.5.30" "/data/myworkspace/tomcat/"
COPY "entrypoint.sh" "/data/myworkspace/"

#设置目录权限 只要本地是有权限的cp进去的文件夹都有权限
#RUN  cd /data/myworkspace/tomcat \
#&& chmod 755 -R * \
#&& cd /data/myworkspace/jdk \
#&& chmod 755 -R *

#设置环境变量
ENV JAVA_HOME=/data/myworkspace/jdk
ENV JRE_HOME=$JAVA_HOME/jre
ENV CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar:$JRE_HOME/lib:$CLASSPATH
ENV PATH=/sbin:$JAVA_HOME/bin:$PATH

#公开端口 这个其实还是要创建容器的时候指定才有作用
EXPOSE 8080

#设置启动命令
#ENTRYPOINT ["/data/myworkspace/tomcat/bin/startup.sh","&&","tail -f /dev/null"]
ENTRYPOINT ["/data/myworkspace/entrypoint.sh"]
```

entrypoint.sh

```
#!/usr/bin/env sh
#docker启动后指定的脚本
/usr/tomcat/bin/startup.sh
tail -f /dev/null
```

3.构建镜像

```
[root@localhost helloword]# docker build -t yxy01/tomcat:0.0.1 .
```

![img](https://images2018.cnblogs.com/blog/1158674/201803/1158674-20180310194604998-1581821338.png)

成功的话，会有镜像id显示

![img](https://images2018.cnblogs.com/blog/1158674/201803/1158674-20180310194656834-1604313109.png)

4.使用docker images查看创建好的镜像

![img](https://images2018.cnblogs.com/blog/1158674/201803/1158674-20180310194754939-593124121.png)

5.通过创建好的镜像，启动一个容器

```
[root@localhost helloword]# docker run  -d -p 8080:8080 --name yxy_tomcat yxy01/tomcat:0.0.1
```

![img](https://images2018.cnblogs.com/blog/1158674/201803/1158674-20180310195123572-1746142728.png)

访问一下

![img](https://images2018.cnblogs.com/blog/1158674/201803/1158674-20180310195203375-1959044701.png)

6.进入容器，看下之前在Dockerfile中写好命令要创建的tomcat和jdk目录

```
[root@localhost helloword]# docker exec -it 480f45dc4c00284690b378c063daf7371719c1cddf0efc2032223bfb318b2076 /bin/bash
```

![img](https://images2018.cnblogs.com/blog/1158674/201803/1158674-20180310195343152-1607287037.png)

 

## 发布war

<============分割线===============>*

下面部署一个HelloWorld.war包

1.在helloworld目录下新建一个webapps目录，把war包放进去

![img](https://images2018.cnblogs.com/blog/1158674/201803/1158674-20180310200657053-865779449.png)

2.使用-v参数将war包挂载至容器内的 tomcat/webapps目录

```
[root@localhost helloword]# docker run -d -p 8080:8080 -v /HMK/helloword/webapps/HelloWorld.war:/usr/tomcat/webapps/HelloWorld.war --name hmk_tomcat jamtur01/tomcat
```

```
注意：-v /HMK/helloword/webapps/HelloWorld.war:/usr/tomcat/webapps/HelloWorld.war，这里是挂载的单个文件
```

![img](https://images2018.cnblogs.com/blog/1158674/201803/1158674-20180310201043378-122925204.png)

 

3.说明下我尝试的几种挂载方法

第一次是将宿主机webapps目录挂载至容器的webapps

```
[root@localhost helloword]# docker run -d -p 8080:8080 -v /HMK/helloword/webapps:/usr/tomcat/webapps --name hmk_tomcat jamtur01/tomcat
```

但是后来发现，启动容器后，容器内的webapps目录和宿主机一样了，也就是容器中tomcat/webapps原本的内容被置换为宿主机的webapps内容了（这和我预期的不符）

![img](https://images2018.cnblogs.com/blog/1158674/201803/1158674-20180310201441612-731493374.png)

![img](https://images2018.cnblogs.com/blog/1158674/201803/1158674-20180310201504926-733813331.png)

通过单个文件挂载的话，则只是把这个war包丢进容器中，并没有影响容器中原本的内容（虽然我知道一般不建议挂载单个文件，但是如何通过挂载目录来读取容器外的程序包、配置文件等的方法还没有找到。。。）

![img](https://images2018.cnblogs.com/blog/1158674/201803/1158674-20180310201956182-1747731441.png)

另外挂载单个文件时注意宿主机的路径是绝对路径，容器中也是绝对路径+文件名（也就是说虽然容器中没有这个war文件，但是我们要假设有，然后映射到宿主机的文件，然后容器就能运行宿主机的war文件了）





https://www.cnblogs.com/hanmk/p/8541814.html