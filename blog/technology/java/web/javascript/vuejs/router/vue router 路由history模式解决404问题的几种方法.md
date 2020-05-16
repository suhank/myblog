[TOC]



# vue router 路由history模式解决404问题的几种方法

时间:2018-09-29

## 简介

这篇文章主要介绍了Vue路由history模式解决404问题的几种方法，小编觉得挺不错的，现在分享给大家，也给大家做个参考。一起跟随小编过来看看吧

## **问题背景：**

vue-router 默认是hash模式，使用url的hash来模拟一个完整的url，当url改变的时候，页面不会重新加载。但是如果我们不想hash这种以#号结尾的路径时候的话，我们可以使用路由的history的模式。比如如下网址：使用hash模式的话，那么访问变成 http://localhost:8080/bank/page/count/#/ 这样的访问，如果路由使用 history的话，那么访问的路径变成 如下：http://localhost:8080/bank/page/count 这样的了；

不过history的这种模式需要后台配置支持。比如：当我们进行项目的主页的时候，一切正常，可以访问，但是当我们刷新页面或者直接访问路径的时候就会返回404，那是因为在history模式下，只是动态的通过js操作window.history来改变浏览器地址栏里的路径，并没有发起http请求，但是当我直接在浏览器里输入这个地址的时候，就一定要对服务器发起http请求，但是这个目标在服务器上又不存在，所以会返回404

怎么解决呢？我们现在可以把所有请求都转发到 http://localhost:8080/bank/page/index.html上就可以了。

## **解决方案：**

对于VUE的router[mode: history]模式在开发的时候，一般都不出问题。是因为开发时用的服务器为node，Dev环境中自然已配置好了。

但对于放到nginx下运行的时候，自然还会有其他注意的地方。总结如下：

在nginx里配置了以下配置后， 可能首页没有问题，但链接其他会出现（404）

```nginx
location / {
      root  D:Testexpricedist;
      index index.html index.htm;
      try_files $uri $uri/ /index.html;
      add_header 'Access-Control-Allow-Origin' '*';
      add_header 'Access-Control-Allow-Credentials' 'true';
      add_header 'Access-Control-Allow-Methods' 'GET, POST, OPTIONS';
      add_header 'Access-Control-Allow-Headers' 'DNT,X-CustomHeader,Keep-Alive,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type';
    }
    
    location ^~/api/ {
      proxy_pass  http://39.105.109.245:8080/;
    }
```

**为了解决404，需要通过以下两种方式：**

## 1、官网推荐

（vue.js官方教程里提到的<https://router.vuejs.org/zh-cn/essentials/history-mode.html>）

```nginx
location / {
　　root  D:Testexpricedist;
　　index index.html index.htm;
　　try_files $uri $uri/ /index.html;
```

## 2、匹配errpr_page

```nginx
location /{
　　root  /data/nginx/html;
　　index index.html index.htm;
　　error_page 404 /index.html;
}
```

以上就是本文的全部内容，希望对大家的学习有所帮助，也希望大家多多支持脚本之家。

 

原文地址：<http://www.manongjc.com/article/13683.html>