[TOC]



# rabbitMQ windows 安装

## 1、下载,其实erlang不装也是可以的

下载 rabbitMQ ：<http://www.rabbitmq.com/download.html>，安装rabbitmq需要erlang，下载erlang：<http://www.erlang.org/download.html>

 

## 2、安装RABBITMQ

rabbitMQ安装，查看安装文档：<http://www.rabbitmq.com/install-windows.html>

## 3、安装erlang

安装erlang，下载完成erlang后，直接打开文件下一步就可以安装完成了，安装完成erlang后再回过来安装RABBITMQ。

## 4、启动RABBITMQ

如果是安装版的直接在开始那里找到安装目录点击启动即可

![img](image-201710201624/0.7192562390118837.png)

 

启动的时候有可能碰到这个问题。

![img](image-201710201624/0.3965545289684087.png)

 

碰到这种情况的时候就先执行停止，执行完后再启动即可。

 

![img](image-201710201624/0.9116235561668873.png)

## 5、安装管理工具

参考官方文档：<http://www.rabbitmq.com/management.html>

操作起来很简单，只需要在DOS下面，进入安装目录（C：\RabbitMQ Server\rabbitmq_server-3.2.2\sbin）执行如下命令就可以成功安装。

 

```
rabbitmq-plugins enable rabbitmq_management
```

可以通过访问 http://localhost:15672  进行测试，默认的登陆账号为：guest，密码为：guest。

 

 

如果访问成功了，恭喜，整个rabbitMQ安装完成了。

 

 

 

来源： <http://www.cnblogs.com/junrong624/p/4121656.html>