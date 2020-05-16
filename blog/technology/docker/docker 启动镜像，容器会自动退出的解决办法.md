# docker 启动镜像，容器会自动退出的解决办法



## 背景

近期在进行Dockerfile实践时，运行了一个简单的容器后,然后docker ps -a 进行查看, 会发现容器已经退出。

## 原因

Docker容器后台运行,就必须有一个前台进程.容器运行的命令如果不是那些一直挂起的命令（比如运行top，tail），就是会自动退出的。

## 解决办法

在启动脚本里面增加一个执行进程：

```
tail -f /dev/null
```


如果是别人的镜像你不想修改，可以用-dit参数

```
docker run -dit --name ubuntu2 ubuntu
```


或 

```
docker run -d --name ubuntu ubuntu /bin/bash -c "tail -f /dev/null"
```

 





原文链接：https://blog.csdn.net/h106140873/java/article/details/94577383