# win10下挂载文件夹到docker容器内

在win10环境下使用docker挂载时路径比较特殊如：

你的目录是

```text
C:\docker.image.data\redis\data
```

在进行目录挂载的时候应该写成：

```text
/c/docker.image.data/redis/data
```

以docker的语法来看的话就是：

```text
docker run -p 6379:6379 -v /c/docker.image.data/redis/data:/data -d redis:5.0.5 redis-server --appendonly yes
```

此时运行时还会出现报错：

**docker: Error response from daemon: D: drive is not shared. Please share it in Docker for Windows Settings.**

这个问题是你的docker的相关使用权限未开！默认使用的时候就是没有开的状态！

你需要在Docker of windows的Setting中进行配置，将D盘符进行共享。这时需要密码，即为你windows用户的开机密码。

然后就安排上了！！！





https://zhuanlan.zhihu.com/p/87593614