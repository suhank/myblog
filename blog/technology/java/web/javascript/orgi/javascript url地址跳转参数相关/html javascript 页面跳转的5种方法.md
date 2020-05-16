[TOC]



# HTML页面跳转的5种方法

## 简介

下面列了五个例子来详细说明，这几个例子的主要功能是：在5秒后，自动跳转到同目录下的hello.html（根据自己需要自行修改）文件。

## **1) html的实现**

```html
<head>
<!-- 以下方式只是刷新不跳转到其他页面 -->
<meta http-equiv="refresh" content="10">
<!-- 以下方式定时转到其他页面 -->
<meta http-equiv="refresh" content="5;url=hello.html"> 
</head>
```

优点：简单
缺点：Struts Tiles中无法使用

 

## **2) javascript的实现**

```html
<script language="javascript" type="text/javascript"> 
// 以下方式直接跳转
window.location.href='hello.html';
// 以下方式定时跳转
setTimeout("javascript:location.href='hello.html'", 5000); 
//在另外新建窗口中打开窗口
window.open("http://www.w3schools.com","_blank");    
</script>
```

优点：灵活，可以结合更多的其他功能
缺点：受到不同浏览器的影响

## **3) 结合了倒数的javascript实现（IE）**

```html
<span id="totalSecond">5</span>
<script language="javascript" type="text/javascript"> 
var second = totalSecond.innerText; 
setInterval("redirect()", 1000); 
function redirect(){ 
totalSecond.innerText=--second; 
if(second<0) location.href='hello.html'; 
} 
</script>
```

优点：更人性化
缺点：firefox不支持（firefox不支持span、div等的innerText属性）

## **3') 结合了倒数的javascript实现（firefox）**

```
<script language="javascript" type="text/javascript"> 
var second = document.getElementById('totalSecond').textContent; 
setInterval("redirect()", 1000); 
function redirect() 
{ 
document.getElementById('totalSecond').textContent = --second; 
if (second < 0) location.href = 'hello.html'; 
} 
</script>
```

## **4) 解决Firefox不支持innerText的问题**

```
<span id="totalSecond">5</span>
<script language="javascript" type="text/javascript"> 
if(navigator.appName.indexOf("Explorer") > -1){ 
document.getElementById('totalSecond').innerText = "my text innerText"; 
} else{ 
document.getElementById('totalSecond').textContent = "my text textContent"; 
} 
</script>
```

## 5) 整合3)和3')

```
<span id="totalSecond">5</span>
 
<script language="javascript" type="text/javascript"> 
var second = document.getElementById('totalSecond').textContent; 
 
if (navigator.appName.indexOf("Explorer") > -1)  { 
    second = document.getElementById('totalSecond').innerText; 
} else { 
    second = document.getElementById('totalSecond').textContent; 
} 
 
setInterval("redirect()", 1000); 
function redirect() { 
if (second < 0) { 
    location.href = 'hello.html'; 
} else { 
    if (navigator.appName.indexOf("Explorer") > -1) { 
        document.getElementById('totalSecond').innerText = second--; 
    } else { 
        document.getElementById('totalSecond').textContent = second--; 
    } 
} 
} 
</script>
```

 

 

https://www.cnblogs.com/aszx0413/articles/1886819.html