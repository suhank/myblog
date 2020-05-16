## 用原生 JavaScript 替换 jQuery 的 ready 方法

原文链接： [www.sitepoint.com](https://www.sitepoint.com/jquery-document-ready-plain-javascript/?utm_source=javascriptweekly&utm_medium=email)

`ready` 方法是 jQuery 实现的在 html 页面在 DOM(Document Object Model, 文档对象模型) 树完全加载完成后触发的一个方法. 因为它接收的方法在页面中所有的 DOM 都可访问时才执行, 所以此时你完全可以访问和操作 html 中的元素.

在 jQuery 3.0 之前, 典型的匿名函数方式的用法如下:

```
$(document).ready(function() {
  // 在 .ready() 被触发时执行.
}); 
```

## 在 jQuery 3.0 中 ready() 的变化

在 jQuery 3.0 发布之前, 有以下几种 `ready` 方法的使用方式:

- 在 document 对象上: `$(document).ready(handler);`
- 在一个空元素上: `$().ready(handler);`
- 或直接使用 (即: 不在一个指定的元素上): `$(handler);`

以上的几种方式是等价的. 传入的 handler 会在页面所有的 DOM 都加载完成后执行, 不管它被哪个指定元素执行. 也就是, 在 image 元素 `$("img")` 与 document 对象上调用 `ready` 方法不表明要等待这些元素加载完成后就触发 handler, 而是在整个 DOM 树加载完成后才触发.

在 jQuery 3.0 中, 除了 `$(handler);` 方法其它的都被弃用了. 官方的理由是:

> 因为这个选择与 `.ready()` 方法的行为没有关系, 它是低效的并且会误导用户猜测这个方法的行为.

## Ready 和 Load 事件的不同点

`ready` 事件在页面 DOM 完全加载后触发并能正确的访问到元素. 而 `load` 事件在页面 DOM 及资源文件(图片,视频等)都加载完成后才触发.

load 事件可以像下面这样使用:

```
$(window).on("load", function(){
  // 当页面所有资源(图片,视频等)全加载完成后才加载执行
}); 
```

这会等待 DOM 加载完成以及图片加载完成(根据图片的大小, 需要加载一定的时间).

对于常规的 DOM 操作你多半不需要 `load` 事件, 但如果你想做一个等待页面所有资源加载的一个加载效果或者是计算图片的大小时这应该是一个不错的选择.

## 你可能并不需要 jQuery.ready()

`ready` 方法确保了其内部的代码都能正确的操作 DOM 元素. 这是什么意思? 当你把 JavaScript 代码放到 HTML 文档中时它会确保回调函数里面的代码在浏览器在已经加载页面中所有的元素时执行:

```
<!doctype html>
<html>
  <head>
    <meta charset="utf-8">
    <title>.ready() 教程</title>
    <script src="https://cdn.jsdelivr.net/jquery/latest/jquery.min.js"></script>
    <script>
      $(function(){ // .ready() 的回调方法, 在 DOM 完全加载完后执行
        var length = $("p").length;
        // 下面会在console控制台中输出 1, 表示有段落 p 存在.
        // 这就证明了这个回调方法在 DOM 完全加载完后执行.
        console.log(length);
      });
    </script>
  </head>
  <body>
    <p>I'm the content of this website</p>
  </body>
</html> 
```

如果你把要**执行的 JavaScript 放到 `body` 元素里面的最后位置, 你就不需要用 `ready()`方法把代码包裹在里面了**, 因为在 JavaScript 代码执行时页面中所有的元素都已经加载完成, 所以此时你就可以访问或操作元素了:

```
<!doctype html>
<html>
  <head>
    <meta charset="utf-8">
    <title>.ready() 教程</title>
  </head>
  <body>
    <p>I'm the content of this website</p>
    <script src="https://cdn.jsdelivr.net/jquery/latest/jquery.min.js"></script>
    <script>
      var length = $("p").length;
      // 下面会在console控制台中输出 1, 表示有段落 p 存在.
      console.log(length);
    </script>
  </body>
</html> 
```

## 使用原生 JavaScript 替换 ready()

对于现代浏览器, 以及 IE9+, 你可以监听 [`DOMContentLoaded`](https://developer.mozilla.org/en/docs/Web/Events/DOMContentLoaded) 事件:

```
document.addEventListener("DOMContentLoaded", function(){
  // 在 DOM 完全加载完后执行
}); 
```

在这里要记住当事件已经触发后回调方法不会执行(页面触发事件后才添加的这个事件监听). 为了确保回调函数始终能执行, jQuery 检测了document 的 `readyState` 属性([参考](https://github.com/jquery/jquery/blob/ad6a94c3f1747829082b85fd53ee2efbae879707/src/core/ready.js#L80-L93)), 如果检测出的属性值是 `complete` 就立即执行回调函数:

```
var callback = function(){
  // 在 DOM 完全加载完后执行
};

if (
    document.readyState === "complete" ||
    (document.readyState !== "loading" && !document.documentElement.doScroll)
) {
  callback();
} else {
  document.addEventListener("DOMContentLoaded", callback);
} 
```

你应该始终记得引入 [domReady](https://github.com/ded/domready) 库, 它已经实现了这个解决方案.

### 老版本的 IE

对于 IE8 及以下版本, 你可以使用 `onreadystatechange` 事件来检测 document 的 `readyState` 属性:

```
document.attachEvent("onreadystatechange", function(){
  // 检测 DOM 是否加载完全
  if(document.readyState === "complete"){
    // 为了确保在之后不会再触发 移除事件监听
    document.detachEvent("onreadystatechange", arguments.callee);
    // 实际处理程序...
  }
}); 
```

另外你可以使用 load 事件, 像 jQuery 那样, 这样就可以在所有的浏览器中正确的执行了. 这也导致有一定的时间延迟, 因为它会等所有的资源都加载完成. 记住在这个解决方案中你还是得去检测 `readyState`, 如上所述, 这是为了确保当事件已经触发后也能执行回调函数.

## 结论

如果你正在寻找一个原生 JavaScript 来代替 `ready` 方法你可以通过 `DOMContentLoaded`事件来解决. 如果你的系统需要支持 IE 那么你就要确保 DOM 已经加载完全!



https://www.zcfy.cc/article/quick-tip-replace-jquery-039-s-ready-with-plain-javascript