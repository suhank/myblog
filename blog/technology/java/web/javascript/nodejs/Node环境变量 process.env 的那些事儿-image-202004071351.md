[TOC]



# Node环境变量 process.env 的那些事儿

 更新于 2017-10-25  约 6 分钟

## 前言

这两天在和运维GG搞部署项目的事儿。

碰到一个问题就是，咱们的dev，uat，product环境的问题。

因为是前后端分离，所以在开发和部署的过程中会有对后端接口的域名的切换问题。折腾了一下午，查询了各种资料这才把这`Node`环境变量`process.env`给弄明白。

下面这就做个问题解决的记录。希望能对这个不明白的人有所帮助。

## Node环境变量

首先，咱们在做`react`、`vue`的单页应用开发的时候，相信大家对配置文件里的`process.env`并不眼生。
想不起来？ 黑人问号.jpg。

就是下面这些玩意儿。

![clipboard.png](image-未命名/bVXbr0.png)

从字面上看，就是这个 `env`属性，在 `development`和`production`不同环境上，配置会有些不同。

行，那下面我们开始看看这个所谓的 `process`到底是个什么东西。

> 文档：[http://nodejs.cn/api/process....](http://nodejs.cn/api/process.html)
> **官方解释**：`process` 对象是一个 `global` （全局变量），提供有关信息，控制当前 `Node.js` 进程。作为一个对象，它对于 `Node.js` 应用程序始终是可用的，故无需使用 `require()`。

process（进程）其实就是存在nodejs中的一个全局变量。
然后呢，咱们可以通过这个所谓的进程能拿到一些有意思的东西。

不过我们今天主要是讲讲 `process.env`。

## process.env

这是啥?

> **官方**: `process.env`属性返回一个包含用户环境信息的对象。
>
> 文档：[http://nodejs.cn/api/process....](http://nodejs.cn/api/process.html#process_process_env)

噢噢噢，原来着个属性能返回**项目运行所在环境**的一些信息。

**有啥用呢？**

很明显的一个使用场景，依靠这个我们就可以给服务器上打上一个标签。这样的话，我们就能根据不同的环境，做一些配置上的处理。比如开启 sourceMap，后端接口的域名切换等等。

```
你是 dev 环境
他是 uat 环境
她是 product 环境。
```

## 如何配置环境变量

下面讲讲如何配置各个环境的环境变量。

### Windows配置

#### 临时配置

直接在cmd环境配置即可，查看环境变量，添加环境变量，删除环境变量。

```
#node中常用的到的环境变量是NODE_ENV，首先查看是否存在 
set NODE_ENV 
#如果不存在则添加环境变量 
set NODE_ENV=production 
#环境变量追加值 set 变量名=%变量名%;变量内容 
set path=%path%;C:\web;C:\Tools 
#某些时候需要删除环境变量 
set NODE_ENV=
```

#### 永久配置

右键(此电脑) -> 属性(R) -> 高级系统设置 -> 环境变量(N)...

### Linux配置

#### 临时

查看环境变量，添加环境变量，删除环境变量

```sh
#node中常用的到的环境变量是NODE_ENV，首先查看是否存在
echo $NODE_ENV
#如果不存在则添加环境变量
export NODE_ENV=production
#环境变量追加值
export path=$path:/home/download:/usr/local/
#某些时候需要删除环境变量
unset NODE_ENV
#某些时候需要显示所有的环境变量
env
```

#### 永久

打开配置文件所在

```
# 所有用户都生效
vim /etc/profile
# 当前用户生效
vim ~/.bash_profile
```

在文件末尾添加类似如下语句进行环境变量的设置或修改

```
# 在文件末尾添加如下格式的环境变量
export path=$path:/home/download:/usr/local/
export NODE_ENV = product
```

最后修改完成后需要运行如下语句令系统重新加载

```
# 修改/etc/profile文件后
source /etc/profile
# 修改~/.bash_profile文件后
source ~/.bash_profile
```

## 解决环境导致后端接口变换问题

搞清楚这个问题后，我们就可以在不同环境的机器上设置不同的 `NODE_ENV`，当然这个字段也不一定。
你也可以换成其他的`NODE_ENV_NIZUISHUAI`等等，反正是自定义的。

### 解决步骤

**1.修改代码里的后端地址配置**

很简单，就是利用 `process.env.NODE_ENV`这个字段来判断。（`process`是`node`全局属性，直接用就行了）

![clipboard.png](image-未命名/bVXbBe.png)

**2.在linux上设置环境变量**

```
export NODE_ENV=dev
```

然后你就可以去愉快的启动项目玩了。

## 说在最后

因为我现在这个项目 React 服务端渲染。所以后端的请求转发就没交给`nginx`进行处理。
像平常的纯单页应用，一般是用`nginx`进行请求转发的。

本篇成文比较快，哈哈，如果文内有任何的纰漏，还请指点，我也就学习学习啦。

另外，如果这篇小小的文章对你带来帮助，不妨给我点个赞吧，这将是我继续下的一大动力。

谢谢~~

## 参考文献

> 环境变量-JasperXu的博客 ：[http://sorex.cnblogs.com/p/62...](http://sorex.cnblogs.com/p/6200940.html)
> 如何查看并设置NODE_ENV的值? :[http://cnodejs.org/topic/587d...](http://cnodejs.org/topic/587dc8a62967eeb01aafe87b)
> Node.js的process模块 : [http://www.css88.com/archives...](http://www.css88.com/archives/4548)

注：【如何配置环境变量】这一块的内容，是直接引用的 JasperXu的博客 的环境变量。

感谢上面三位的好文。

阅读 74.1k更新于 2017-10-25







https://segmentfault.com/a/1190000011683741