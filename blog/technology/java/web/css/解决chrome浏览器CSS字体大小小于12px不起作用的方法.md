



# 解决chrome浏览器CSS字体大小小于12px不起作用的方法

 2015-10-16 14:06:00

谷歌浏览器是不支持12px以下小字体的，可能是考虑到用户体验吧。

网上也有一些文章介绍，说可以使用： 

```css
-webkit-text-size-adjust:none; 
```


但是，自chrome 27之后，就取消了对这个属性的支持。

我们还有其它办法解决这个问题吗？

谷歌浏览器支持CSS缩放，于是，我们可以在这方面做文章： 

```css
 -webkit-transform: scale(0.5); 
```

既然最小支持到12px，那么就以12px为基点进行缩放，

同时可以使用-webkit-transform-origin-x: 0; 来解决缩放后margin-left的位移问题：

```css
.test_tag{ 
	font-size:12px; 
	-webkit-transform-origin-x: 0; 
	-webkit-transform: scale(0.5833333333333334); 
}
```


scale值的计算方法为： 7 / 12 = 0.5833333333333334







https://blog.csdn.net/djjx5227jj/article/details/49178005