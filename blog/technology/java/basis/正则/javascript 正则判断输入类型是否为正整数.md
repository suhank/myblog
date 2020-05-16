# javascript 正则判断输入类型是否为正整数



需要用到正则表达式：

```
"/^+?[1-9][0-9]*$/"
```

例子如下:

```html
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>检测正整数</title>
</head>
<script>
	<!-- 判断是否为正整数-->
	function fun() {
		var value = document.getElementById("value").value;
		var ele = document.getElementById("message");
		ele.style.color = "red";
		if (value == "") {
			ele.innerHTML = "请输入内容";
		} else {
			var r = /^\+?[1-9][0-9]*$/;
			if (r.test(value)) {
				ele.style.color = "green";
				ele.innerHTML = "是一个数字";
			} else {
				ele.innerHTML = "请输入一个正整数";
			}
		}

	}
</script>
<body>
<center>
	<input type="text" id="value" style="width: 50px">
	<input type="button" onclick="fun()" value="检测"><br>
	<div id="message"></div>
</center>
</body>
</html>
```



原文链接：https://blog.csdn.net/flyawayl/article/details/77240812