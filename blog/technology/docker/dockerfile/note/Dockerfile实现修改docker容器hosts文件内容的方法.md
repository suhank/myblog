# Dockerfile实现修改docker容器hosts文件内容的方法

## 场景

今天突然遇到一个问题，需要向容器的/etc/hosts文件追加自定义的内容，直接的做法的是，进入容器，直接修改/etc/hosts文件，但是，这种做法在容器重新启动后就失效，而且容器启动实例一多，就会带来繁琐的手动操作。

为了能让同一个镜像启动的容器每次启动的时候都能自动更新成我们需要的/etc/hosts文件，现有以下几种方法：

## 1. 在docker run的时候增加参数--add-host进行添加（官方给的方法）

官方文档地址: https://docs.docker.com/engine/reference/commandline/run/#usage

```
# 添加单个hosts
docker run -it nginx --add-host=localhost:127.0.0.1
# 添加多个hosts
docker run -it nginx --add-host=localhost:127.0.0.1 --add-host=example.com:127.0.0.1 
# 一个ip对应多个hosts
docker run -it nginx --add-host="localhost example.com":127.0.0.1
```



## 2. 在dockerfile中，使用脚本作为镜像入口，再利用脚本运行修改hosts文件的命令以及真正的应用程序入口

文件说明
\- myhosts：需要追加到/etc/hosts中的内容
\- run.sh：容器的入口执行脚本
\- dockerfile：构建镜像的dockerfile文件

dockerfile示例如下：

```
FROM centos:6
MAINTAINER chenjiefeng
RUN mkdir /data
COPY run.sh /data/
COPY myhosts /data/
RUN chmod +x /data/run.sh
ENTRYPOINT /bin/sh -c /data/run.sh
```



run.sh示例如下：

```
#!/bin/bash
# 向hosts文件追加内容
cat /data/myhosts >> /etc/hosts
# 其他命令
# 保留终端，防止容器自动退出
/bin/bash 

```

myhosts示例如下：

```
127.0.0.1 localhost example.com
```

镜像构建完成后，执行docker run 指令运行容器，查看/etc/hosts配置

```
$ cat /etc/hosts
127.0.0.1   localhost
::1 localhost ip6-localhost ip6-loopback
fe00::0 ip6-localnet
ff00::0 ip6-mcastprefix
ff02::1 ip6-allnodes
ff02::2 ip6-allrouters
172.17.0.9  f7e886276ba3
127.0.0.1 localhost example.com
```

bingo! 已经修改成功



http://blog.realjf.com/archives/62