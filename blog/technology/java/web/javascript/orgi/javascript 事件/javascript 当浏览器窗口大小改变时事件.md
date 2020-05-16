# javascript 当浏览器窗口大小改变时事件

```js
 window.onload=function(){
 	 changeDivHeight();
 }
 //当浏览器窗口大小改变时，设置显示内容的高度
 window.onresize=function(){
 	 changeDivHeight();
 }
 function changeDivHeight(){				
 	var h = document.documentElement.clientHeight;//获取页面可见高度
 	document.getElementById("div_ov_y").style.height=h-140+"px";
 }

```





https://blog.csdn.net/luoyejie/article/details/11045407