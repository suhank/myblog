[TOC]



# jquery原理的简单分析,让你扒开jquery的小外套 

### 引言

　　最近LZ还在消化系统原理的第三章，因此这部分内容LZ打算再沉淀一下再写。本次LZ和各位来讨论一点前端的内容，其实有关jquery，在很久之前，LZ就写过一篇简单的源码分析。只不过当时刚开始写博客，写的相对来讲比较随意，直接就把源码给贴上来了，尽管加了很多注释，但还是会略显粗糙。

　　这次LZ再次执笔，准备稍微规范一点的探讨一下jquery的相关内容。

### jquery的外衣

　　jquery是一个轻量级的JS框架，这点相信大部分人都听过，而jquery之所以有这样一个称呼，就是因为它悄悄披了一件外衣，将自己给隐藏了起来。

```js
//以下截取自jquery源码片段
(function( window, undefined ) {
   /*    源码内容    */
})( window );
```

　　上面这一小段代码来自于1.9.0当中jquery的源码，它是一个无污染的JS插件的标准写法，专业名词叫闭包。可以把它简单的看做是一个函数，与普通函数不同的是，这个函数没有名字，而且会立即执行，就像下面这样，会直接弹出字符串。

```
    (function( window, undefined ) {
       alert("Hello World!");
    })( window );
```

　　可以看出来这样写的直接效果，就相当于我们直接弹出一个字符串。但是不同的是，我们将里面的变量变成了局域变量，这不仅可以提高运行速度，更重要的是我们在引用jquery的JS文件时，不会因为jquery当中的变量太多，而与其它的JS框架的变量命名产生冲突。对于这一点，我们拿以下这一小段代码来说明。

```
var temp = "Hello World!";
(function( window, undefined ) {
   var temp = "ByeBye World!";
})( window );
alert(temp);
```

　　这段代码的运行结果是Hello而不是ByeBye，也就是说闭包中的变量声明没有污染到外面的全局变量，倘若我们去掉闭包，则最终的结果会是ByeBye，就像下面这样。

```
    var temp = "Hello World!";
//    (function( window, undefined ) {
       var temp = "ByeBye World!";
//    })( window );
    alert(temp);
```

　　由此就可以看出来，jquery的外衣就是这一层闭包，它是很重要的一个内容，是编写JS框架必须知道的知识，它可以帮助我们隐藏我们的临时变量，降低污染。

### jquery的背心

　　刚才我们说了，jquery将自己声明的变量全部都用外衣遮盖起来了，而我们平时使用的Jquery和$，却是真真实实的全局变量，这个是从何而来，谜底就在jquery的某一行代码，一般是在文件的末尾。

```
window.jQuery = window.$ = jQuery;
```

　　这一句话将我们在闭包当中定义的jQuery对象导出为全局变量jQuery和\$，因此我们才可以在外部直接使用jQuery和$。window是默认的JS上下文环境，因此将对象绑定到window上面，就相当于变成了传统意义上的全局变量，就像下面这一小段代码的效果一样。

```
    var temp = "Hello World!";
    (function( window, undefined ) {
       var temp = "ByeBye World!";
       window.temp = temp;
    })( window );
    alert(temp);
```

　　很明显，它的结果应该是ByeBye，而不是Hello。因为我们在闭包中导出了temp局部变量为全局变量，从而覆盖了第一行声明的全局变量temp。可以看出，就是通过导出的方式，jquery露出了自己的小背心。

 

### jquery的内裤

　　内裤保护的是我们的核心器官，因此非常重要。那么jquery的内裤也一样，也是最核心的功能，就是选择器。而选择器简单理解的话，其实就是在DOM文档中，寻找一个DOM对象的工具。

　　首先我们进入jquery源码中，可以很容易的找到jquery对象的声明，看过以后会发现，原来我们的jquery对象就是init对象。

```js
    jQuery = function( selector, context ) {
        return new jQuery.fn.init( selector, context, rootjQuery );
    }
```

　　这里出现了jQuery.fn这样一个东西，它的由来可以在jquery的源码中找到，它其实代表的就是jQuery对象的原型。

```js
jQuery.fn = jQuery.prototype;
jQuery.fn.init.prototype = jQuery.fn;
```

　　这两句话，第一句把jQuery对象的原型赋给了fn属性，第二句把jQuery对象的原型又赋给了init对象的原型。也就是说，init对象和jQuery具有相同的原型，因此我们在上面返回的init对象，就与jQuery对象有一样的属性和方法。

　　我们不打算深究init这个方法的逻辑以及实现，但是我们需要知道的是，jQuery其实就是将DOM对象加了一层包裹，而寻找某个或者若干个DOM对象是由sizzle选择器负责的，它的官方地址是<http://sizzlejs.com/>，有兴趣的猿友可以去仔细研究下这个基于CSS的选择器。

　　下面是LZ截取的一个jQuery对象的属性和方法截图，方法这里就不提了，对于属性来说，我们最需要关注的只有一个属性，就是[0]属性，[0]其实就是原生的DOM对象。

![img](image-201806121504/10232250-76e5662e08f84b8d8d98f6b61ced7749.jpg)

　　很多时候，我们在jQuery和DOM对象之间切换时需要用到[0]这个属性。从截图也可以看出，jQuery对象其实主要就是把原生的DOM对象存在了[0]的位置，并给它加了一系列简便的方法。这个索引0的属性我们可以从一小段代码简单的看一下它的由来，下面是init方法中的一小段对DOMElement对象作为选择器的源码。

```
    // Handle $(DOMElement)
    if ( selector.nodeType ) {
        /*     可以看到，这里将DOM对象赋给了jQuery对象的[0]这个位置  */
        this.context = this[0] = selector;
        this.length = 1;
        return this;
    }
```

　　这一小段代码可以在jquery源码中找到，它是处理传入的选择参数是一个DOM对象的情况。可以看到，里面很明显的将jQuery对象索引0的位置以及context属性，都赋予了DOM对象。代码不仅说明了这一点，也同时说明了，我们使用$(DOMElement)可以将一个DOM对象转换为jQuery对象，从而通过转换获得jQuery对象的简便方法。

### jquery的大腿

　　大腿是非常性感令男人垂涎的地方，要说jquery最性感最令我们向往的，便是它的ready方法了，千万不要告诉LZ你使用jquery却从未用过$(function(){})或者是ready方法。这里LZ不打算带各位去看jquery的实现原理，因为比较复杂，而且这里我们的主旨不是为了一点一点的剖析源码，而是简介一下jquery的实现原理。

　　实现类似jquery的ready方法的效果我们是可以简单做到的，它的实现原理就是，维护一个函数数组，然后不停的判断DOM是否加载完毕，倘若加载完毕就触发所有数组中的函数。遵循着这一思想，LZ拿出很久之前写的一个小例子，来给各位看一下。 

```js
(function( window, undefined ) {
var 
    jQuery = {
        isReady:false,//文档加载是否完成的标识
        readyList:[],//函数序列
        //onload事件实现
        ready : function(fn){
                //如果是函数，加入到函数序列
                if(fn && typeof fn == 'function' ){
                    jQuery.readyList.push(fn);
                }
                //文档加载完成，执行函数序列。
                if(jQuery.isReady){
                    for(var i = 0;i < jQuery.readyList.length ;i++){
                        fn = jQuery.readyList[i];
                        jQuery.callback(fn);
                    }
                    return jQuery;
                }
            },
        //回调
        callback : function(fn){
            fn.call(document,jQuery);
        }
    };
    //导出对象
    window.$ = window.jQuery = jQuery;
    //判断加载是否完成
    var top = false;
    try {
        top = window.frameElement == null && document.documentElement;
    } catch(e) {}
    if ( top && top.doScroll ) {
        (function doScrollCheck() {
            try {
                top.doScroll("left");
                jQuery.isReady = true;
                jQuery.ready();
            } catch(e) {
                setTimeout( doScrollCheck, 50 );
            }
        })();
    }
}(window));
```

　　这段代码是LZ从之前的例子摘出来的，它的实现逻辑非常简单，但是可以达到jQuery的ready方法的效果，各位有兴趣的可以加入这个JS文件测试一下效果。需要注意的是，上面没有考虑浏览器兼容性，那段判断文档加载是否完成的代码是针对IE写的，因此只能在IE下测试。

　　代码当中已经嵌入了简单的注释，因此LZ这里就不多做解释了，全部的源码可以在LZ的另一篇文章[jquery源码分析](http://www.cnblogs.com/zuoxiaolong/p/jquery_src.html)找到，有兴趣的猿友也可以看下，那里模拟了一个非常简陋的jquery。

### jquery的胳膊

　　我们缺了胳膊依旧可以生活，甚至可以用脚写程序，但是不得不承认，有了胳膊的我们会更加如虎添翼。而对于jquery来说，extend方法便是它的胳膊，没有它我们依然可以很好的使用jquery，但是有了它，我们会更加畅快。

　　这里LZ不再详细分析extend方法，有兴趣的朋友可以参考LZ很久之前的一篇文章[jquery扩展函数详解](http://www.cnblogs.com/zuoxiaolong/p/jquery_extend.html)，那里有较为详细的分析和解释。这里LZ只简单说两个extend方法的常用方式。

　　1、使用jQuery.fn.extend可以扩展jQuery对象，使用jQuery.extend可以扩展jQuery，前者类似于给类添加普通方法，后者类似于给类添加静态方法。

　　2、两个extend方法如果有两个object类型的参数，则会将后面的参数对象属性扩展到第一个参数对象上面，扩展时可以再添加一个boolean参数控制是否深度拷贝。

 

### 小结

　　本次对于jquery的简单分析就到此为止了，由于LZ并不专注于前段开发，所以对于jquery一直是采取着适可而止的研究方式。不过只要还在做Web开发，就离不开前端，因此我们也不能放弃前端。

 

  

版权声明作者：zuoxiaolong（左潇龙）出处：[博客园左潇龙的技术博客--http://www.cnblogs.com/zuoxiaolong](http://www.cnblogs.com/zuoxiaolong)您的支持是对博主最大的鼓励，感谢您的认真阅读。本文版权归作者所有，欢迎转载，但未经作者同意必须保留此段声明，且在文章页面明显位置给出原文连接，否则保留追究法律责任的权利。



https://www.cnblogs.com/zuoxiaolong/p/jquery1.html