# css font-size: 0;解决inline元素间的空白间隙

## 标签之间的间隙

1. 比如我在写如下代码的时候：

```html
<ul>
    <li>我是第一项</li>
    <li>我是第二项</li>
    <li>我是第三项</li>
    <li>我是第四项</li>
</ul>
```

设置css如下：

```html
<style>
    ul {
        list-style: none;
    }
    li {
        width: 25%;
        display: inline-block;
        background: green;
        text-align: center;
        height: 40px;
        line-height: 40px;
    }
</style>
```

表面上看应该是出于同一行，没什么问题，但是效果其实是下面这样的：

![img](image-201810171253/image-20181102110348837.png)

内容4掉了下来

我们为了页面代码的整洁可读性，往往会设置一些适当的缩进、换行，但当元素的display为inline或者inline-block的时候，这些缩进、换行就会产生空白，所以出现上述问题。虽然还有其他方法能解决我们因为缩进、换行而产生的问题，但此时，最合适的方法就是给li的父级ul设置： **font-size: 0;** 给li设置：**font-size: 16px;** 如此就达到了所需效果。

![img](image-201810171253/image-20181102110402162.png)

达到效果

## 图片间的间隙问题。

*其实图片的间隙问题也是因为我们的换行、缩进。*

```
<div>
     ![](pic1.jpg)
     ![](pic2.jpg)
</div>
```

两张图片之间就会出现这样的间隙：

![img](image-201810171253/image-20181102110412024.png)

给这个div设置

```
div {
    font-size: 0;
}
```

图片之间的间隙就没有了：

![img](image-201810171253/image-20181102110417069.png)

木有间隙





https://www.jianshu.com/p/48f9805f1b06