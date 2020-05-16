[TOC]



# 真正的让iframe自适应高度 兼容多种浏览器随着窗口大小改变

2016-05-19 18:37:44 [alex8046](https://me.csdn.net/alex8046) 阅读数 193296 文章标签： [iframe](https://so.csdn.net/so/search/s.do?q=iframe&t=blog)[html5](https://so.csdn.net/so/search/s.do?q=html5&t=blog)

 

今天有朋友问到我关于“iframe自适应高度”的问题，原本以为是很简单的问题，没想到折腾了20分钟才搞定。期间遇到几个问题，要么是高度自适应了，但是当窗口改变时会出现滚动条。也就是当窗口放大时iframe没有自动跟随变大显得很小，或是当窗口缩小时iframe还是之前那么大就出现了滚动条。还有或是高度不准确，那么就达不到想要的效果了。

 

为什么需要使用iframe自适应高度呢？其实就是为了美观，要不然iframe和窗口长短大小不一，看起来总是不那么舒服，特别是对于我们这些编程的来说，如鲠在喉的感觉。

首先设置样式

> body{margin:0; padding:0;}

如果不设置body的margin和padding为0的话，页面上下左右会出现空白。

html代码如下

```
<iframe src="http://www.fulibac.com" id="myiframe" scrolling="no" frameborder="0"></iframe>
```

下面就是今天小编写的时候遇到的问题，考虑到有些朋友可能没怎么用jquery就直接用js吧。

## 方法一

> var ifm= document.getElementById("myiframe");
>
> ifm.height=document.documentElement.clientHeight;

这个方法可以达到让iframe自适应高度的效果，但是如果你将窗口放大或缩小效果就不出来了，也就是本文开头讲的。需要再次刷新，那就不属于自适应了。

那么问题来了，需要解决当窗口改变大小的时候执行js事件，以让iframe自适就高度。那么就需要将相关的代码写成函数，并且给iframe加上onLoad="changeFrameHeight()"，也就是下面的方法二了。

## 方法二

```
<iframe src="http://www.fulibac.com" id="myiframe" scrolling="no" οnlοad="changeFrameHeight()" frameborder="0"></iframe>
```

js代码也得跟着改

```js
function changeFrameHeight(){
    var ifm= document.getElementById("iframepage"); 
    ifm.height=document.documentElement.clientHeight;
}

window.οnresize=function(){  
     changeFrameHeight();  
} 

```

window.onresize的作用就是当窗口大小改变的时候会触发这个事件。

所以，使用方法二就可以完美的、真正的让iframe自适应高度了，试试看吧，并且兼容多种浏览器。



源引：http://www.fulibac.com/993.html



<https://blog.csdn.net/alex8046/article/details/51456131>