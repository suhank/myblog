[TOC]



# css :after 选择器

 

## 实例

在每个 <p> 元素的内容之后插入新内容：

```
p:after
{ 
content:"台词：";
}
```

[亲自试一试](http://www.w3school.com.cn/tiy/t.asp?f=css_sel_after)

## 浏览器支持

| IE   | Firefox | Chrome | Safari | Opera |
| ---- | ------- | ------ | ------ | ----- |
|      |         |        |        |       |

所有主流浏览器都支持 :after 选择器。

注释：对于 IE8 及更早版本中的 :after，必须声明 [](http://www.w3school.com.cn/tags/tag_doctype.asp)。

## 定义和用法

:after 选择器在被选元素的内容后面插入内容。

请使用 content 属性来指定要插入的内容。

## 亲自试一试 - 实例

在每个 <p> 元素后面插入内容，并设置所插入内容的样式：

```
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
    <style>
        p:after {
            content:"台词：-";
            background-color:yellow;
            color:red;
            font-weight:bold;
        }
    </style>
</head>
<body>

<p>我是唐老鸭。</p>
<p>我住在 Duckburg。</p>

<p><b>注释：</b>对于在 IE8 中工作的 :after，必须声明 DOCTYPE。</p>


</body>
</html>
```

 