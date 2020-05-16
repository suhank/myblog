[TOC]



# 如何发布第一个属于自己的npm包



## 什么是NPM？

NPM是随同NodeJS一起安装的javascript包管理工具，能解决NodeJS代码部署上的很多问题，常见的使用场景有以下几种：

1. 允许用户从NPM服务器下载别人编写的第三方包到本地使用。
2. 允许用户从NPM服务器下载并安装别人编写的命令行程序到本地使用。
3. 允许用户将自己编写的包或命令行程序上传到NPM服务器供别人使用。

## 发布前的准备

### 1. 注册一个npm账号

前往[NPM官网](http://npmjs.org/)进行注册

### 2. 创建一个简单的包

在本地创建一个项目文件夹sugars_demo (名字自己取，不要和NPM上已有的包名重复冲突就好)
然后通过终端进入文件夹（路径每个人不一样）

```
cd sugars_demo
```

接着可以通过命令创建一个包信息管理文件package.json

```
npm init
```

一路回车或根据包的内容来填写相关信息后，package.json内容大概如下

```
{
  "name": "sugars_demo",
  "version": "1.0.0",
  "description": "A demo",
  "main": "index.js",
  "scripts": {
    "test": "echo \"Error: no test specified\" && exit 1"
  },
  "repository": {
    "type": "git",
    "url": ""
  },
  "keywords": [
    "sugars",
    "demo"
  ],
  "author": {
    "name": "sugars",
    "email": "343166031@qq.com"
  },
  "license": "MIT"
}
```

接着在sugars_demo文件夹里创建一个index.js文件，然后简单敲几行代码

```
;(function (global, factory) {
  typeof exports === 'object' && typeof module !== 'undefined' ? module.exports = factory() :
    typeof define === 'function' && define.amd ? define(factory) :
      global.moduleName = factory()
}(this, (function () {
  var test = {
    sayHi: function () {
      console.log('hi');
    }
  };

  return test
})))
```

到这里一个简单的包就创建好了。
如果想再完善一下的话，还可以在包根目录下创建README.md文件，里面可以写一些关于包的介绍信息，最后发布后会展示在NPM官网上。

## 开始发布创建好的包

使用终端命令行
如果是第一次发布包，执行以下命令，然后输入前面注册好的NPM账号，密码和邮箱，将提示创建成功

```
npm adduser
```

如果不是第一次发布包，执行以下命令进行登录，同样输入NPM账号，密码和邮箱

```
npm login
```

**注意：npm adduser成功的时候默认你已经登陆了，所以不需要再进行npm login了**

接着先进入项目文件夹下，然后输入以下命令进行发布

```
npm publish
```

当终端显示如下面的信息时，就代表版本号为1.0.0的包发布成功啦！前往NPM官网就可以查到你的包了

```
myMacBook-Pro:sugars_demo sugars$ npm publish
+ sugars_demo@1.0.0
```

如果遇到显示以下信息，比如

```
npm ERR publish 403

You do not have permission to publish 'bootstrap'.Are you logged in as
the corrent user?:bootstrap
```

意思就是你没有权限发布名为“bootstrap”的包，显然这个鼎鼎有名的包已经有人发布了，所以你只能另取它名。

## 更新已经发布的包

更新包的操作和发布包的操作其实是一样的

```
npm publish
```

但要注意的是，每次更新时，必须修改版本号后才能更新，比如将1.0.0修改为1.0.1后就能进行更新发布了。
这里的包版本号有一套规则，采用的是[semver](https://semver.org/lang/zh-CN/)（语义化版本），通俗点意思就是版本号：大改.中改.小改

阅读 4k更新于 2018-04-06

 



https://segmentfault.com/a/1190000013940567