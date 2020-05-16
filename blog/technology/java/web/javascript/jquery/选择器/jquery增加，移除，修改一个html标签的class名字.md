# jquery增加，移除，修改一个html标签的class名字

**1. 增加一个class：**

```
$(".default").addClass("hover_s");
```

**2. 移除一个class：**

```
$(".default").removeClass("default ");
```

**3. 修改一个class：** 
3.1 可以分两步走： 
1 先增加一个你要增加的class

```
$(".default").addClass("hover_s");
```

2 再删除一个你想要删除的class

```
$(".default").removeClass("default ");1
```

或者反过来，先删除，后增加也行。

3.2 可以直接设置成你想要的class

```
$(". default ").attr("class", " hover_s fl fv lv ");1
```

1. 当鼠标移到，离开指定标签时修改class 
   移到时改成hover_s，离开时改成default。

```
$(".default").hover(function () {
    $(this).addClass("hover_s");
    $(this).removeClass("default");
    }, function () {
    $(this).addClass("default");
    $(this).removeClass("hover_s");
});1234567
```

1. 获取标签的所有class

```
var classname_module = $(".lv").attr("class");
```





https://blog.csdn.net/zengyonglan/article/details/53995053