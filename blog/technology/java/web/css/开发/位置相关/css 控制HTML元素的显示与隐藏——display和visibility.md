# 控制HTML元素的显示与隐藏——display和visibility

 /Users/jerryye/backup/studio/AvailableCode/web/css/css-demo/layout 页面布局相关/元素的显示与隐藏-display和visibility/display和visibility.html





有些时候我们需要根据某些条件来控制Web页面中的HTML元素显示还是隐藏，可以通过display或visibility来实现。通过下面的例子了解display和visibility的区别，简单的例子代码如下：

```html
<!DOCTYPE html>
<html lang="en">
<head>
<!--
    说明:
    把 display 设置成 none 不会保留元素本该显示的空间，但是 visibility: hidden 还会保留。
-->

    <meta charset="UTF-8">
    <title>HTML元素的显示与隐藏控制</title>
    <script type="text/javascript">
        function showAndHidden1() {
            var div1 = document.getElementById("div1");
            var div2 = document.getElementById("div2");
            if (div1.style.display == 'block') div1.style.display = 'none';
            else div1.style.display = 'block';
            if (div2.style.display == 'block') div2.style.display = 'none';
            else div2.style.display = 'block';
        }

        function showAndHidden2() {
            var div3 = document.getElementById("div3");
            var div4 = document.getElementById("div4");
            if (div3.style.visibility == 'visible') div3.style.visibility = 'hidden';
            else div3.style.visibility = 'visible';
            if (div4.style.visibility == 'visible') div4.style.visibility = 'hidden';
            else div4.style.visibility = 'visible';
        }
    </script>
</head>
<body>
<div>display：元素的位置不被占用</div>
<div id="div1" style="display:block;">DIV 1</div>
<div id="div2" style="display:none;">DIV 2</div>
<input type="button" onclick="showAndHidden1();" value="DIV切换"/>
<hr>
<div>visibility：元素的位置仍被占用</div>
<div id="div3" style="visibility:visible;">DIV 3</div>
<div id="div4" style="visibility:hidden;">DIV 4</div>
<input type="button" onclick="showAndHidden2();" value="DIV切换"/>
</body>
</html>
```



https://blog.csdn.net/lzwglory/article/details/17242871