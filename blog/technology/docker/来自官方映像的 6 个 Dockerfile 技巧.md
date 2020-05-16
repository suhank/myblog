[TOC]



# 来自官方映像的 6 个 Dockerfile 技巧 *【已翻译100%】*

英文原文：[6 Dockerfile Tips from the Official Images](http://container-solutions.com/2014/11/6-dockerfile-tips-official-images/)

 

[前篇文章](http://container-solutions.com/2014/10/docker-official-images-good-bad-ugly/)是关于Docker官方镜像的，本文将根据我从官方镜像学到的经验，讲解编写Dockerfile的技巧。

### 1. 选择Debian

官方镜像的大多数Dockerfile，不管是直接还是通过其他镜像，都是基于Debian的。Dockerfile版本通常跟特定的发行版挂钩，正常是使用稳定版(wheezy)，有些是测试版(jessie)，还有是不稳定版(sid)。Debian镜像的主要好处是文件小，加起来才85.1MB，而Ubuntu要200MB。指定准确的发行版可以预防一些问题，比如，即使标上latest的发行版升级了，构建也不会崩溃。

### 2. 确定来源

如果要用户信赖你的镜像，你就要考虑如果验证此镜像上的所有软件的真实性。如果通过apt-get方式从Debian的仓库获取，这个验证过程已经被解决了。如果从网上下载文件，或者从第三方仓库安装软件，你就应该通过校验和、数字签名等方式验证这些文件。比如，为了验证nginx包，nginx的Dockerfile会做如下操作：

```
RUN apt-key adv --keyserver pgp.mit.edu --recv-keys 573BFD6B3D8FBC641079A6ABABF5BD827BD9BF62
RUN echo "deb http://nginx.org/packages/mainline/debian/ wheezy nginx" >> /etc/apt/sources.list

ENV NGINX_VERSION 1.7.7-1~wheezy

RUN apt-get update && apt-get install -y nginx=${NGINX_VERSION}
```

注意nginx也是跟一个特定的版本关联的。这种做法可以保证，维护者测试的镜像跟构建工具构建的是相同的。但是，这个假设也不是绝对的，因为nginx本身的依赖也会随时间变化(比如有些依赖会标明>=某个版本)。

你自己也可以对下载的文件做相似的操作，计算文件校验和，然后跟本地版本(stored version)比较。这些操作都由[Redis Dockerfile](https://github.com/docker-library/redis/blob/master/2.8/Dockerfile)做过了。另外，有些下载的文件可能包含签名文件，你可以通过gpg来验证，通常这些操作都由官方镜像做过了。

不幸的是，一些官方镜像也没能定期正确地进行这些验证操作，或者只验证了部分文件，所以查看官方Dockerfile时要注意下。

 

### 3. 移除构建依赖

如果通过源码编译构建，你的镜像通常比需要的大很多。可能的话，在同一条RUN指令中，安装构建工具、构建软件，然后移除构建工具。虽然这样做又诡异又恼人，但是可以省下几百MB空间。在不同的指令中删除文件是没有意义的，因为这些文件已经被打包进镜像了。我们看下Redis Dockerfile是怎么做的：

```
RUN buildDeps='gcc libc6-dev make'; \
    set -x \
    && apt-get update && apt-get install -y $buildDeps --no-install-recommends \
    && rm -rf /var/lib/apt/lists/* \
    && mkdir -p /usr/src/redis \
    && curl -sSL "$REDIS_DOWNLOAD_URL" -o redis.tar.gz \
    && echo "$REDIS_DOWNLOAD_SHA1 *redis.tar.gz" | sha1sum -c - \
    && tar -xzf redis.tar.gz -C /usr/src/redis --strip-components=1 \
    && rm redis.tar.gz \
    && make -C /usr/src/redis \
    && make -C /usr/src/redis install \
    && ln -s redis-server "$(dirname "$(which redis-server)")/redis-sentinel" \
    && rm -r /usr/src/redis \
    && apt-get purge -y --auto-remove $buildDeps
```

gcc, libc和make在同一条指令中先被安装，使用，然后被删掉。另外注意下，作者还删除了不会被使用的tar.gz源文件夹。顺带提下，这些代码也演示了如何使用sha1sum来验证Redis下载文件的校验和。

### 4. 选择gosu

[gosu](https://github.com/tianon/gosu)实用工具，通常用在ENTRYPOINT指令调用的脚本中，这些ENTRYPOINT指令位于官方镜像的Dockerfile中。它是个类sudo的简单工具，接受并运行特定用户的特定指令。但是gosu可以避免sudo怪异恼人的TTY和信号转发(signal-forwarding)行为。

看下这篇[编写入口点脚本的官方建议](https://docs.docker.com/articles/dockerfile_best-practices/)，大多数官方镜像都遵循该建议。 

### 5. 选择buildpack-deps基础镜像

很多Docker的"language-stack"镜像都是基于 [buildpack-deps](https://registry.hub.docker.com/u/library/buildpack-deps/) 基础镜像，该镜像包含了通常开发所必须的头文件和工具（比如源码管理工具）。如果你想构建一个language-stack镜像，使用这个基础镜像可以省下不少时间。但是向镜像添加非必须的东西也遭受了很多批评，这导致了一些仓库，如Node提供了直接基于Debian的可选slim包(完整的Node镜像有728MB，slim只有291.4MB)。但是记住用户可能需要某些开发库，同时也通过某种途径下载了基础镜像。

 

### 6. 使用描述性标签

所有的官方镜像都提供了很多标签。像latest标签一样，提供一个版本标签是种好做法，这样用户就不用担心基础镜像变更而破坏容器。官方镜像做了更多， 提供了上文提及的精简slim镜像，以及自动导入和编译的onbuild镜像。镜像打上onbuild标签，即使转移代码再编译，来新建子镜像，用户也不会太意外。





https://www.oschina.net/translate/6-dockerfile-tips-official-images