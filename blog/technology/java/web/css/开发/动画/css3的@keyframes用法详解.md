# css3的@keyframes用法详解

CSS3的@keyframes用法详解:
此属性与animation属性是密切相关的，关于animation属性可以参阅CSS3的animation属性用法详解一章节。

## 一.基本知识:

keyframes翻译成中文，是"关键帧"的意思，如果用过flash应该对这个比较好理解，当然不会flash也没有任何问题。
使用transition属性也能够实现过渡动画效果，但是略显粗糙，因为不能够更为精细的控制动画过程，比如只能够在指定的时间段内总体控制某一属性的过渡，而animation属性则可以利用@keyframes将指定时间段内的动画划分的更为精细一些。

语法结构:

```
@keyframes animationname {keyframes-selector {css-styles;}}
```

参数解析:
1.animationname:声明动画的名称。
2.keyframes-selector:用来划分动画的时长，可以使用百分比形式，也可以使用 "from" 和 "to"的形式。
"from" 和 "to"的形式等价于 0% 和 100%。
建议始终使用百分比形式。

## 二.代码实例:

### 实例一: 

```html
<!DOCTYPE html>   
<html>   
<head>   
<meta charset=" utf-8">   
<meta name="author" content="http://www.softwhy.com/" />   
<title>蚂蚁部落</title>  
<style type="text/css">  
div{ 
  width:100px; 
  height:100px; 
  background:red; 
  position:relative; 
     
  animation:theanimation 5s infinite alternate; 
  -webkit-animation:theanimation 5s infinite alternate ; 
  -moz-animation:theanimation 5s infinite alternate ; 
  -o-animation:theanimation 5s infinite alternate ; 
  -ms-animation:theanimation 5s infinite alternate ; 
} 
@keyframes theanimation{ 
  from {left:0px;} 
  to {left:200px;} 
} 
@-webkit-keyframes theanimation{ 
  from {left:0px;} 
  to {left:200px;} 
} 
@-moz-keyframes theanimation{ 
  from {left:0px;} 
  to {left:200px;}  
} 
@-o-keyframes theanimation{ 
  from {left:0px;} 
  to {left:200px;}  
} 
@-ms-keyframes theanimation{ 
  from {left:0px;} 
  to {left:200px;} 
} 
</style> 
</head> 
<body> 
<div></div> 
</body> 
</html>
```

上面代码实现了简单的动画，下面简单做一下分析:
1.使用@keyframes定义了一个名为theanimation的动画。
2.@keyframes声明的动画名称要和animation配合使用。
3.from to等价于0%-100%，所以就是规定5s内做了一件事情。

### 实例二: 

```html
<!DOCTYPE html>   
<html>   
<head>   
<meta charset=" utf-8">   
<meta name="author" content="http://www.softwhy.com/" />   
<title>蚂蚁部落</title>  
<style type="text/css">  
div{ 
  width:100px; 
  height:100px; 
  background:red; 
  position:relative; 
     
  animation:theanimation 4s infinite alternate; 
  -webkit-animation:theanimation 4s infinite alternate ; 
  -moz-animation:theanimation 4s infinite alternate ; 
  -o-animation:theanimation 4s infinite alternate ; 
  -ms-animation:theanimation 4s infinite alternate ; 
} 
@keyframes theanimation{ 
  0%{top:0px;left:0px;background:red;} 
  25%{top:0px;left:100px;background:blue;} 
  50%{top:100px;left:100px;background:yellow;} 
  75%{top:100px;left:0px;background:green;} 
  100%{top:0px;left:0px;background:red;} 
} 
@-moz-keyframes theanimation{ 
  0% {top:0px;left:0px;background:red;} 
  25%{top:0px;left:100px;background:blue;} 
  50%{top:100px;left:100px;background:yellow;} 
  75%{top:100px;left:0px;background:green;} 
  100%{top:0px;left:0px;background:red;} 
} 
@-webkit-keyframes theanimation{ 
  0%{top:0px;left:0px;background:red;} 
  25%{top:0px;left:100px;background:blue;} 
  50%{top:100px;left:100px;background:yellow;} 
  75%{top:100px;left:0px;background:green;} 
  100%{top:0px;left:0px;background:red;} 
} 
@-o-keyframes theanimation{ 
  0%{top:0px;left:0px;background:red;} 
  25%{top:0px;left:100px;background:blue;} 
  50%{top:100px;left:100px;background:yellow;} 
  75%{top:100px;left:0px;background:green;} 
  100%{top:0px;left:0px;background:red;} 
} 
</style> 
</head> 
<body> 
<div></div> 
</body> 
</html>
```

在以上代码中，使用百分比形式将动画时长进行了划分，规定了在指定区间内做指定的事情。



http://www.voidcn.com/article/p-rzorfrgx-bnu.html