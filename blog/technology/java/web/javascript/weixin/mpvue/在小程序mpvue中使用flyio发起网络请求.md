[TOC]



# 在小程序mpvue中使用flyio发起网络请求

## 前言

Fly.js 一个基于Promise的、强大的、支持多种JavaScript运行时的http请求库. 有了它，您可以使用一份http请求代码在浏览器、微信小程序、Weex、Node、React Native、快应用中都能正常运行。同时可以方便配合主流前端框架 ，最大可能的实现 Write Once Run Everywhere。上一篇文章介绍了在快应用中使用flyio，本文主要介绍一下如何在微信小程序中使用flyio。
Flyio Github: https://github.com/wendux/fly

## 问题

随着 Weex 、mpvue 的发布，他们都是支持Vue.js语法。目前vue已经你能够运行在浏览器、小程序和Native了。尽管各个平台仍有差异，但已经基本能实现 Write Once Run Everywhere 。这使得我们可以在多个端上实现尽可能大限度在代码复用。但是无论是 vue 还是Weex 、mpvue，它们本质上都只是一个View层，也就说最好的情况，也只能实现UI复用。但对于一个应用程序来说，除了UI，最重要的就是数据了，而数据来源一般都是来自网络请求（大多数都是http）。在使用这些框架时，您的网络请求，都需要使用平台特定的API！这很糟糕，意味着您网络请求的代码不能复用，所以尽管UI可以复用，但我们还需要去适配网络请求部分的代码。

## Flyio简介

要上述问题，就需要一个能支持多个平台网络库，用户层提供统一的API，将平台差异在底层屏蔽。而 Fly.js就是这酱紫的一个网络库，为了方便axios使用者迁移，fly.js API设计风格和axios相似（但不完全相同）！

Fly.js 通过在不同 JavaScript 运行时通过在底层切换不同的 Http Engine来实现多环境支持，但同时对用户层提供统一、标准的Promise API。不仅如此，Fly.js还支持请求/响应拦截器、自动转化JSON、请求转发等功能，详情请参考：<https://github.com/wendux/fly> 。下面我们看看在微信小程序、mpvue中和中如何使用fly.

## 微信小程序

微信小程序采用web开发技术栈，使用JavaScript语言开发，但是JavaScript运行时和浏览器又有所不同，导致axios、jQuery等库无法在微信小程序中使用，而flyio可以。下面给出具体使用方法

## 引入fly

Flyio在各个平台下的标准API是一致的，只是入口文件不同，在微信小程序中引入：

Npm安装：npm install flyio --save.

```
var Fly=require("flyio/dist/npm/wx") 
var fly=new Fly
```

如果您的微信小程序项目没有使用npm来管理依赖，您可以直接下载源码到您的小程序工程，下载链接wx.js 或 wx.umd.min.js .下载任意一个，保存到本地工程目录，假设在“lib”目录，接下来引入：

```
var Fly=require("../lib/wx") //wx.js为您下载的源码文件
var fly=new Fly; //创建fly实例
```

引入之后，您就可以对fly实例进行全局配置、添加拦截器、发起网络请求了。

使用

Fly基于Promise提供了Restful API，你可以方便的使用它们，具体请参考fly 文档 。下面给出一个简单的示例

```js
//添加拦截器
fly.interceptors.request.use((config,promise)=>{
    //给所有请求添加自定义header
    config.headers["X-Tag"]="flyio";
    return config;
})
//配置请求基地址
fly.config.baseURL='http://www.dtworkroom.com/doris/1/2.0.0/'
...
Page({
  //事件处理函数
  bindViewTap: function() {
    //发起get请求
    fly.get("/test",{xx:6}).then((d)=>{
      //输出请求数据
      console.log(d.data)
      //输出响应头
      console.log(d.header)
    }).catch(err=>{
      console.log(err.status,err.message)
    })
    ...
  })
})
```

## 在mpvue中使用

在mpvue 中您也可以将fly实例挂在vue原型上，这样就可以在任何组件中通过this方便的调用：

```js
var Fly=require("flyio/dist/npm/wx") 
var fly=new Fly
... //添加全局配置、拦截器等
Vue.prototype.$http=fly //将fly实例挂在vue原型上
在组件中您可以方便的使用：
this.$http.get("/test",{xx:6}).then((d)=>{
      //输出请求数据
      console.log(d.data)
      //输出响应头
      console.log(d.header)
    }).catch(err=>{
      console.log(err.status,err.message)
    })
```

反馈

如果您有问题欢迎在 在github 提issue . fly.js github: github.com/fly





http://www.wxapp-union.com/article-3854-1.html