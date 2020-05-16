# Dockerfile中的COPY和ADD指令详解与比较 推荐使用COPY指令

## 前言

Dockerfile中的COPY指令和ADD指令都可以将主机上的资源复制或加入到容器镜像中，都是在构建镜像的过程中完成的。

COPY指令和ADD指令的唯一区别在于是否支持从远程URL获取资源。COPY指令只能从执行docker build所在的主机上读取资源并复制到镜像中。而ADD指令还支持通过URL从远程服务器读取资源并复制到镜像中。

满足同等功能的情况下，**推荐使用COPY指令**。ADD指令更擅长读取本地tar文件并解压缩。

## 1.COPY指令

COPY指令能够将构建命令所在的主机本地的文件或目录，复制到镜像文件系统。

exec格式用法（推荐）：

```
COPY ["<src>",... "<dest>"]，推荐，特别适合路径中带有空格的情况
```

shell格式用法：

```
COPY <src>... <dest>
```



## 2.ADD指令

ADD指令不仅能够将构建命令所在的主机本地的文件或目录，而且能够将远程URL所对应的文件或目录，作为资源复制到镜像文件系统。
所以，可以认为ADD是增强版的COPY，支持将远程URL的资源加入到镜像的文件系统。

exec格式用法（推荐）：

```
ADD ["<src>",... "<dest>"]，特别适合路径中带有空格的情况
```

shell格式用法：

```
ADD <src>... <dest>
```

说明，对于从远程URL获取资源的情况，由于ADD指令不支持认证，如果从远程获取资源需要认证，则只能使用RUN wget或RUN curl替代。
另外，如果源路径的资源发生变化，则该ADD指令将使Docker Cache失效，Dockerfile中后续的所有指令都不能使用缓存。因此尽量将ADD指令放在Dockerfile的后面。

## 3.COPY指令和ADD指令的用法非常相似，具体注意事项如下：

- 源路径可以有多个
- 源路径是相对于执行build的相对路径
- 源路径如果是本地路径，必须是build上下文中的路径
- 源路径如果是一个目录，则该目录下的所有内容都将被加入到容器，但是该目录本身不会
- 目标路径必须是绝对路径，或相对于WORKDIR的相对路径
- 目标路径如果不存在，则会创建相应的完整路径
- 目标路径如果不是一个文件，则必须使用**/**结束
- 路径中可以使用通配符

## 4.读取URL远程资源

```
RUN mkdir -p /usr/src/things \  
    && curl -SL http://example.com/big.tar.xz \  
    | tar -xJC /usr/src/things \  
    && make -C /usr/src/things all  
```

事实上，当要读取URL远程资源的时候，并不推荐使用ADD指令，而是建议使用RUN指令，在RUN指令中执行wget或curl命令。

参考链接：

https://docs.docker.com/engine/reference/builder/





https://blog.csdn.net/taiyangdao/article/details/73222601