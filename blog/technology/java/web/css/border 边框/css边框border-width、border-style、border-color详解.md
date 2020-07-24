[TOC]



# css边框border-width、border-style、border-color详解

 

css设置边框可以使用border，border是border-width、border-style和border-color的缩写形式，本文章向大家接受css 边框的相关知识，需要的朋友可以参考一下。

在网页中，边框随处可见，任何块元素和行内元素都可以设置边框属性。例如，[div元素](http://www.manongjc.com/html/html_div.html)可以设置边框，[img元素](http://www.manongjc.com/html/html_img.html)也可以设置边框，[table元素](http://www.manongjc.com/html/html_table.html)也可以设置边框，[span元素](http://www.manongjc.com/html/html_span.html)同样也可以设置边框等等。下面向大家介绍边框border的相关知识：

css border分为：

1. border-width
2. border-style
3. border-color

 

## border-width

border-width属性设置一个元素的四个边框的宽度。此属性可以有一到四个值。

实例：

```
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
    <style>
        p.one {
            border-style: solid;
            border-width: 5px;
        }

        p.two {
            border-style: solid;
            border-width: medium;
        }

        p.three {
            border-style: solid;
            border-width: 1px;
        }
    </style>
</head>
<body>

<p class="one">Some text.</p>
<p class="two">Some text.</p>
<p class="three">Some text.</p>
<p><b>Note:</b> The "border-width" property does not work if it is used alone. Use the "border-style" property to set
    the borders first.</p>

</body>
</html>
```

[在线运行](http://www.manongjc.com/runcode/585.html)

## border-style

border-style属性设置一个元素的四个边框的样式。此属性可以有一到四个值。

实例：

```
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
    <style>
        p.none {
            border-style: none;
        }

        p.dotted {
            border-style: dotted;
        }

        p.dashed {
            border-style: dashed;
        }

        p.solid {
            border-style: solid;
        }

        p.double {
            border-style: double;
        }

        p.groove {
            border-style: groove;
        }

        p.ridge {
            border-style: ridge;
        }

        p.inset {
            border-style: inset;
        }

        p.outset {
            border-style: outset;
        }

        p.hidden {
            border-style: hidden;
        }
    </style>
</head>
<body>

<p class="none">No border.</p>
<p class="dotted">A dotted border.</p>
<p class="dashed">A dashed border.</p>
<p class="solid">A solid border.</p>
<p class="double">A double border.</p>
<p class="groove">A groove border.</p>
<p class="ridge">A ridge border.</p>
<p class="inset">An inset border.</p>
<p class="outset">An outset border.</p>
<p class="hidden">A hidden border.</p>

</body>
</html>
```

[在线运行](http://www.manongjc.com/runcode/578.html)

 

## border-color

border-color属性设置一个元素的四个边框颜色。此属性可以有一到四个值。

实例：

```
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
    <style>
        p.one {
            border-style: solid;
            border-color: #0000ff;
        }

        p.two {
            border-style: solid;
            border-color: #ff0000 #0000ff;
        }

        p.three {
            border-style: solid;
            border-color: #ff0000 #00ff00 #0000ff;
        }

        p.four {
            border-style: solid;
            border-color: #ff0000 #00ff00 #0000ff rgb(250, 0, 255);
        }
    </style>
</head>
<body>

<p class="one">One-colored border!</p>
<p class="two">Two-colored border!</p>
<p class="three">Three-colored border!</p>
<p class="four">Four-colored border!</p>
<p><b>Note:</b> The "border-color" property does not work if it is used alone. Use the "border-style" property to set
    the borders first.</p>

</body>
</html>
```

[在线运行](http://www.manongjc.com/runcode/563.html)

原文地址：<http://www.manongjc.com/article/1177.html>