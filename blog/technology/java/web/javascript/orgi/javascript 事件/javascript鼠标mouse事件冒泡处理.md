# javascript鼠标mouse事件冒泡处理

简单的鼠标移动事件：

进入

```
mouseenter：不冒泡
mouseover: 冒泡
不论鼠标指针穿过被选元素或其子元素，都会触发 mouseover 事件
只有在鼠标指针穿过被选元素时，才会触发 mouseenter 事件
```

移出

```
mouseleave: 不冒泡
mouseout：冒泡
不论鼠标指针离开被选元素还是任何子元素，都会触发 mouseout 事件
只有在鼠标指针离开被选元素时，才会触发 mouseleave 事件
```

我们通过一个案例观察下问题：

给一个嵌套的层级绑定mouseout事件，会发现mouseout事件与想象的不一样

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
<div class="out overout" style="width:40%;height:120px; margin:0 15px;background-color:#D6EDFC;float:left;"
     data-mce-style="width: 40%; height: 120px; margin: 0 15px; background-color: #d6edfc; float: left;"><p
        style="border:1px solid red" data-mce-style="border: 1px solid red;">外部子元素</p>
    <div class="in overout" style="width:60%;background-color:#FFCC00;margin:10px auto;"
         data-mce-style="width: 60%; background-color: #ffcc00; margin: 10px auto;"><p style="border:1px solid red"
                                                                                       data-mce-style="border: 1px solid red;">
        内部子元素</p>
        <p id="inshow">0</p>
    </div>
    <p id="outshow">0</p>
</div>
<script type="text/javascript">

    var i = 0;
    var k = 0;

    document.querySelectorAll('.out')[0].addEventListener('mouseout', function (e) {
        document.querySelectorAll("#inshow")[0].textContent = (++i)
        e.stopPropagation();
    }, false)

    document.querySelectorAll('.in')[0].addEventListener('mouseout', function () {
        document.querySelectorAll("#outshow")[0].textContent = (++k)
    }, false)

</script>
</body>
</html>

```

我们发现一个问题mouseout事件：

1. 无法阻止冒泡
2. 在内部的子元素上也会触发

 

------

 **同样的问题还有mouseover事件，那么在stopPropagation方法失效的情况下我们要如何停止冒泡呢？**

- 为了阻止mouseover和mouseout的反复触发，这里要用到event对象的一个属性relatedTarget，这个属性就是用来判断 mouseover和mouseout事件目标节点的相关节点的属性。简单的来说就是当触发mouseover事件时，relatedTarget属性代表的就是鼠标刚刚离开的那个节点，当触发mouseout事件时它代表的是鼠标移向的那个对象。由于MSIE不支持这个属性，不过它有代替的属性，分别是 fromElement和toElement。
-     有了这个属性，我们就能够清楚的知道我们的鼠标是从哪个对象移过来，又是要移动到哪里去了。这样我们就能够通过判断这个相关联的对象是否在我们要触发事件的对象的内部，或者是不是就是这个对象本身。通过这个判断我们就能够合理的选择是否真的要触发事件。
-     这里我们还用到了一个用于检查一个对象是否包含在另外一个对象中的方法，contains方法。MSIE和FireFox分别提供了检查的方法，这里封装了一个函数。

jQuery的处理也是如出一辙

```js
jQuery.each({
        mouseenter: "mouseover",
        mouseleave: "mouseout",
        pointerenter: "pointerover",
        pointerleave: "pointerout"
    }, function(orig, fix) {
        jQuery.event.special[orig] = {
            delegateType: fix,
            bindType: fix,

            handle: function(event) {
                var ret,
                    target = this,
                    related = event.relatedTarget,
                    handleObj = event.handleObj;

                // For mousenter/leave call the handler if related is outside the target.
                // NB: No relatedTarget if the mouse left/entered the browser window
                if (!related || (related !== target && !jQuery.contains(target, related))) {
                    event.type = handleObj.origType;
                    ret = handleObj.handler.apply(this, arguments);
                    event.type = fix;
                }
                return ret;
            }
        };
    });
```



 



https://www.cnblogs.com/aaronjs/p/4241531.html