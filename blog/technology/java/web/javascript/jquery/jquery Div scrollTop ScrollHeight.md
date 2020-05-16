# JQuery Div scrollTop ScrollHeight

jQuery 里和滚动条有关的概念很多，但是有三个属性和滚动条的拖动有关，就是：scrollTop、scrollLeft、scrollHeight。其中 scrollHeight 属性，互联网上几乎搜素不到关于它的应用技巧，而我正好需要用到它。

我们现在只探讨和垂直滚动有关的 scrollTop、scrollHeight 属性。

## 一、滚动条有关属性的正确理解：

假设有以下Html代码：

```html
<div id="div1" style="overflow-y:auto; overflow-x:hidden; height:500px;">
      <div style="height:750px;">
      </div>
</div> 
```

由于内部的div标签高度比外部的长，并且外部的div允许自动出现垂直滚动条，所以用浏览器打开后，可以看到垂直滚动条。滚动条向下拖动一段距离，看到的页面效果如下（右部的a、b是我抓图后，用PS标出来的）： 
那么，这里的外部div 的scrollTop、scrollHeight 属性到底是什么呢？ 
有人说，scrollTop等于图中标出的a。scrollHeight 等于外部div的高度500px。其实，都不对。 
其实，图中标出的a、b，对我们编程写js代码没有任何具体意义，它仅仅具有象征意义。 
实际上，在js代码里，滚动条是被抽象为一个“点”来对待的。scrollHeight其实不是“滚动条的高度”（b），而是表示滚动条需要滚动的高度，即内部div的高度750px。而scrollTop表示滚动条（一个点）当前的位置在750px里占了多少，不是图中标出的a。 
这时，我们很叹服Windows的设计者，滚动条设计的如此形象美妙，欺骗了多少头脑简单的鼠标操作员。a和b的距离分别标识滚动条滚动了和需要滚动的距离，它们之间分别有一个对应的关系，但这些不是我们这些开发应用程序的程序员考虑的，是设计操作系统GUI图形接口的程序员考虑的。

## 二、判断垂直滚动条是否到达底部 

弄明白了以上的概念，编码其实就比较简单了， 以下是示例代码：

```html
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
    <html xmlns="http://www.w3.org/1999/xhtml">
    <head>
      <title>下拉滚动条滚到底部了吗？</title>
      <script language="javascript" src="jquery-1.10.2.js"></script>
      <script language="javascript">
      $(document).ready(function (){
        var nScrollHight = 0; //滚动距离总长(注意不是滚动条的长度)
        var nScrollTop = 0;   //滚动到的当前位置
        var nDivHight = $("#div1").height();

        $("#div1").scroll(function(){
          nScrollHight = $(this)[0].scrollHeight;
          nScrollTop = $(this)[0].scrollTop;
          if(nScrollTop + nDivHight >= nScrollHight)
            alert("滚动条到底部了");
          });
      });
      </script>
    <body>
    <div id="div1" style="overflow-y:auto; overflow-x:hidden; height:500px;">
      <div style="background-color:#ccc; height:750px;">IE 和 FF 下测试通过</div>
    </div>
    </body>
    </html> 
```

代码解说： 
内部div高度为750，外部div高度为500，所以垂直滚动条需要滚动750-500=250的距离，就会到达底部，参见语句nScrollTop + nDivHight >= nScrollHight。 
程序中，在外部div的scroll（滚动）事件中侦测和执行if判断语句，是非常消耗CPU资源的。用鼠标拖拉滚动条，只要有一个像素的变动就会触发该事件。但点击滚动条两头的箭头，事件触发的频率会低得多。所以滚动条的scroll事件要谨慎使用。 
本示例判断的是没有水平滚动条的情况，在有水平滚动条时，情况会有细小的变化，所以nScrollTop + nDivHight >= nScrollHight语句中，需要用“>=”比较运算符，而没有水平滚动条的时候，等号“=”就足够了。大家可以实际测试一下。还可以判断水平滚动条是否滚动到头了。



https://blog.csdn.net/male09/article/details/68962049