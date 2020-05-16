# javascript根据日期判断星期几

 

```Jsp
<SCRIPT> 
var   s   =  '2011-11-17'; 
alert( "今天星期 "+"天一二三四五六 ".charAt(new   Date(s).getDay())); 
alert("星期 "   +   new   Date(s).getDay());
</SCRIPT>
```

！————————————————————————————————————————

刚发现在IE中不能实现~解决办法如下：

```Jsp
<SCRIPT> 
var   s   =  '2011-11-17'; 
//alert(new Date());
//alert( "今天星期 "+"天一二三四五六 ".charAt(new  Date(s).getDay())); 
//alert(new Date().getDay());
var b = new Date(Date.parse(s.replace(/\-/g,"/")));
alert(b.getDay());
</SCRIPT>
```

现在都能实现

大同小异，另一种方法：

```js
var weekDay = ["星期天", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"];
        var dateStr = "2008-08-08";
        var myDate = new Date(Date.parse(dateStr.replace(/-/g, "/")));
        alert(weekDay[myDate.getDay()]); 

```





https://blog.csdn.net/tmaic/article/details/6982722