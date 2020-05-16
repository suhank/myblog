[TOC]



# jquery移除或禁用html元素的点击事件

移除或禁用html元素的点击事件可以通过css实现也可以通过js或jQuery实现。

### 一、CSS方法

```
.disabled { pointer-events: none; }  
```

### 二、jQuery方法

方法一

```
$(this).click(function (event) {  
event.preventDefault();  
}  
```

方法二

```
$('a').live('click', function(event) {  
       alert("抱歉,已停用！");    
      event.preventDefault();     
    }); 
```

注：此方法中的live亦可以为on，bind等方法

方法三

```
$('.disableCss').removeAttr('onclick');//去掉标签中的onclick事件  
```

通过removeAttr方法来控制html标签的属性已达到启用或禁用事件。另，使用这种方式也可以控制其他事件或其他效果。

方法四

```
$('#button').attr('disabled',"true");//添加disabled属性  
$('#button').removeAttr("disabled"); //移除disabled属性  
```

注：和方法三是一样的，不过disabled属性一般用在类型为button或submit的input上





https://blog.csdn.net/wangzl1163/article/details/53666883