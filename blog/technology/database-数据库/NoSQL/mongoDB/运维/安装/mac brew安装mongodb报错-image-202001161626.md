[TOC]



# mac brew安装mongodb报错 

 

今天在使用`brew install mongodb` 安装 mongodb 时

提示：Error: No available formula with the name 'mongodb'

经查找原因如下：

![clipboard.png](image-未命名/bVbxK9J.png)

MongoDB不再是开源的了，并且已经从Homebrew中移除 #43770

新的安装方式可以参考github主页给的提示：[https://github.com/mongodb/ho...](https://github.com/mongodb/homebrew-brew)

## 设定

```
brew tap mongodb/brew
```

## 安装

安装MongoDB社区服务器的最新可用生产版本（包括所有命令行工具）。这将安装MongoDB 4.2.x：

```
$ brew install mongodb-community
```

安装MongoDB社区服务器和命令行工具的最新4.2.x生产版本：

```
$ brew install mongodb-community@4.2
```

安装MongoDB社区服务器和命令行工具的最新4.0.x生产版本：

```
$ brew install mongodb-community@4.0
```

安装MongoDB社区服务器和命令行工具的最新3.6.x生产版本：

```
$ brew install mongodb-community@3.6
```

仅安装最新的mongoshell以连接到远程MongoDB实例：

```
$ brew install mongodb-community-shell
```

如果报这个错⬇️

```
An exception occurred within a child process
  An exception occurred within a child process:
  DownloadError: Failed to download resource "mongodb-community"
Download failed: https://fastdl.mongodb.org/osx/mongodb-macos-x86_64-4.2.0.tgz
```

重新执行一下上面的命令

```
brew install mongodb-community@4.2 (我装的这个)
```

## 文件路径

```
配置文件：/usr/local/etc/mongod.conf
日志目录路径：/usr/local/var/log/mongodb
数据目录路径：/usr/local/var/mongodb
```

## 启动 && 停止 mongodb-community服务器

**mongod作为服务运行**

若要launchd启动mongod立即重启也登录时，使用

```
brew services start mongodb-community
```

如果您mongod作为服务进行管理，它将使用上面列出的默认路径。要停止服务器实例，请使用：

```
brew services stop mongodb-community
```

## 手动启动 mongod

如果您不想要或不需要后台MongoDB服务，您可以运行：

```
mongod --config /usr/local/etc/mongod.conf
```

注意：如果您不包含--config带有配置文件路径的选项，则MongoDB服务器没有默认配置文件或日志目录路径，并将使用数据目录路径/data/db。

要mongod手动关闭，请使用admin数据库并运行db.shutdownServer()：

```
mongo admin --eval "db.shutdownServer()"
```



阅读 3.1k更新于 2019-09-16





https://segmentfault.com/a/1190000020400235