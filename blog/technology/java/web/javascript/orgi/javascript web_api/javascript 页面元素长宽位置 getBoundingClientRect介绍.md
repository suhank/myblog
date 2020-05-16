[TOC]



# javascript 页面元素长宽位置 getBoundingClientRect介绍

### getBoundingClientRect获取元素位置

getBoundingClientRect用于获得页面中某个元素的左，上，右和下分别相对浏览器视窗的位置。getBoundingClientRect是DOM元素到浏览器可视范围的距离（不包含文档卷起的部分）。

该函数返回一个Object对象，该对象有6个属性：`top,lef,right,bottom,width,height`；这里的top、left和css中的理解很相似，width、height是元素自身的宽高，但是right，bottom和css中的理解有点不一样。right是指元素右边界距窗口最左边的距离，bottom是指元素下边界距窗口最上面的距离。

getBoundingClientRect()最先是IE的私有属性，现在已经是一个W3C标准。所以你不用当心浏览器兼容问题，不过还是有区别的：`IE只返回top,lef,right,bottom四个值`，不过可以通过以下方法来获取width,height的值

```Js
var ro = object.getBoundingClientRect();
var Width = ro.right - ro.left;
var Height = ro.bottom - ro.top;

//兼容所有浏览器写法：

var ro = object.getBoundingClientRect();
var Top = ro.top;
var Bottom = ro.bottom;
var Left = ro.left;
var Right = ro.right;
var Width = ro.width||Right - Left;
var Height = ro.height||Bottom - Top;

//有了这个方法，获取页面元素的位置就简单多了:

var X= this.getBoundingClientRect().left+document.documentElement.scrollLeft;
var Y =this.getBoundingClientRect().top+document.documentElement.scrollTop;
```

### getBoundingClientRect判断元素是否在可视区域

以前的办法是通过各种offset判断元素是否可见，网上很多教程，大家可以自己去查找。 `getBoundingClientRect`是获取可视区域相关位置信息的，使用这个属性来判断更加方便：

```
function isElementInViewport (el) {
    var rect = el.getBoundingClientRect();
    return (
        rect.top >= 0 &&
        rect.left >= 0 &&
        rect.bottom <= (window.innerHeight || document.documentElement.clientHeight) && /*or $(window).height() */
        rect.right <= (window.innerWidth || document.documentElement.clientWidth) /*or $(window).width() */
    );
}
```

### getBoundingClientRect兼容性

目前来说兼容性还是不错的，但是使用前还是查看一下caniuse比较好。

[caniuse](http://caniuse.com/#search=getBoundingClientRect)





https://div.io/topic/1400?utm_source=tuicool&utm_medium=referral