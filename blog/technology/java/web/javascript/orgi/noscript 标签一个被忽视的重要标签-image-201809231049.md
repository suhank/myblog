# noscript 标签一个被忽视的重要标签

打开 Drupal 的新后台，发现显示大面积空白。本以为是 CSS 的问题，后来折腾好久才发现是我之前因为某些站的安全问题把浏览器的 Javascript 给禁用了。Javascript 的日益强大使我们的网页丰富多彩，交互越来越强大，功能越来越炫。但是有的网页效果完全依赖 Javascript 来实现，一旦离开了 Javascript 的支持，可能连基本的内容都显示不完全，这时候，我们老当益壮的`noscript`标签就该出场了.

`noscript`标签是一个相当古老的标签，其被引入的最初目的是帮助老旧浏览器的平滑升级更替，因为早期的浏览器并不能支持 JavaScript。noscript 标签在不支持JavaScript 的浏览器中显示替代的内容。这个元素可以包含任何 HTML 元素。这个标签的用法也非常简单：

```
<noscript>
  <p>本页面需要浏览器支持（启用）JavaScript</p>
</noscript>
```

不过到了现在，浏览器不支持 Javascript 的事情应该已经不会出现了，但是用户也可能因为各种原因而禁用了 Javascript。如节省流量，延长电池使用时间，或者是不希望自己的隐私被各类统计/追踪脚本泄露。

也有相当一部分用户安装了类似`noscript`

的浏览器扩展来禁止浏览器运行 Javascript。

网站虽然不能强制用户启用浏览器的 Javascript，但是可以提示用户的浏览器已经禁用脚本，来达到更好的用户体验。例如 Fackbook 这样的提示：

![img](/var/folders/f1/bv046xq17hb29l881ch_5db80000gn/T/abnerworks.Typora/image-20180923104837521.png)

noscript 标签中的元素中的内容只有在下列情况下才会显示出来：

- 浏览器不支持脚本
- 浏览器支持脚本，但脚本被禁用

符合上述任何一个条件，浏览器都会显示 noscript 中的内容。而在除此之外的其他情况下，浏览器不会呈现 noscript 中的内容。

## 写在最后

使用 noscript 标签只能给网站用户传达一个信息，即如果不启用 Javascript，网页内容和效果可能不能完全被呈现。但如果有些用户并不懂得如何去开启 Javascript的话，这样的提示信息对他也并没有什么实际的帮助。所以我们还是应该在网站设计之初多多考虑在没有 Javascript(或 HTML5，或其他依赖)的支持的情况下，如何使这样的非常规状况尽可能少的影响到用户的浏览体验。





https://www.jianshu.com/p/b075aaf1c6ba