# JQuery的函数“\$(function() {})”的含义及与“$(document).ready(function(){})”的区别

常能在页面中看到如下语句：

```Html
<script type="text/javascript">
 $(function(){
  $("#titleDiv").load("content.do?type=list");
 });
</script> 
```

那么

```
$(function() {})
```

是什么意思呢？这其实是一个jQuery函数，是当文档载入完成的时候执行的，也就是说文档载入完成后，执行：

```
$("#titleDiv").load("content.do?type=list");
```

这里执行相当于：

```
 $(document).ready(function(){ 
    $("#titleDiv").load("content.do?type=list");
 }) 
```

当然，如果两种方式都有的话：

```
 $(document).ready(function(){ 
    $("#titleDiv").load("content.do?type=list");
 })

$(function(){
  $("#titleDiv").load("content.do?type=list");
 }); 
```

那么

```
$(document).ready(function(){})
```

先被执行，而：

```
$(function(){})
```

后被执行。





转载请注明出处：<http://blog.csdn.net/dongdong9223/article/details/50504518>  本文出自【[我是干勾鱼的博客](http://blog.csdn.net/dongdong9223)】