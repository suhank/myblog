[TOC]



# javascript Window setTimeout() 方法

 

## 实例

3 秒（3000 毫秒）后弹出 "Hello" :

setTimeout(function(){ alert("Hello"); }, 3000);

尝试一下 »

------

## 定义和用法

setTimeout() 方法用于在指定的毫秒数后调用函数或计算表达式。

**提示：** 1000 毫秒= 1 秒。

**提示：** 如果你只想重复执行可以使用 [setInterval()](http://www.runoob.com/jsref/met-win-setInterval.html) 方法。

**提示：** 使用 [clearTimeout()](http://www.runoob.com/jsref/met-win-cleartimeout.html) 方法来阻止函数的执行。

------

## 浏览器支持

表格中的数字表示支持该属性的第一个浏览器版本号。

| 方法         |      |      |      |      |      |
| ------------ | ---- | ---- | ---- | ---- | ---- |
| setTimeout() | 1.0  | 4.0  | 1.0  | 1.0  | 4.0  |

------

## 语法

```
setTimeout(code, milliseconds, paam1, param2, ...)
setTimeout(function, milliseconds, param1, param2, ...)
```

| 参数                | 描述                                                         |
| ------------------- | ------------------------------------------------------------ |
| code/function       | 必需。要调用一个代码串，也可以是一个函数。                   |
| milliseconds        | 可选。执行或调用 code/function 需要等待的时间，以毫秒计。默认为 0。 |
| param1, param2, ... | 可选。 传给执行函数的其他参数（IE9 及其更早版本不支持该参数）。 |

------

## 技术细节

返回值:返回一个 ID（数字），可以将这个ID传递给 clearTimeout() 来取消执行。

## 更多实例

### 实例

3 秒（3000 毫秒）后弹出 "Hello" :

```
var myVar;
 
function myFunction() {
    myVar = setTimeout(alertFunc, 3000);
}
 
function alertFunc() {
    alert("Hello!");
}
```

尝试一下 »

### 实例

在第 2、4、6 秒修改输入框中的文本：

```
var x = document.getElementById("txt");
setTimeout(function(){ x.value = "2 秒" }, 2000);
setTimeout(function(){ x.value = "4 秒" }, 4000);
setTimeout(function(){ x.value = "6 秒" }, 6000);
```

尝试一下 »

### 实例

打开一个新窗口，3 秒后将该窗口关闭:

```
var myWindow = window.open("", "", "width=200, height=100");
myWindow.document.write("<p>这是一个新窗口'</p>");
setTimeout(function(){ myWindow.close() }, 3000);
```

尝试一下 »

### 实例

使用 clearTimeout() 来阻止函数的执行：

```
var myVar;
 
function myFunction() {
    myVar = setTimeout(function(){ alert("Hello") }, 3000);
}
 
function myStopFunction() {
    clearTimeout(myVar);
}
```

尝试一下 »

### 实例

计数器 -- 可以通过点击按钮停止：

```
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>菜鸟教程(runoob.com)</title>
</head>
<body>

<button onclick="startCount()">开始计数!</button>
<input type="text" id="txt">
<button onclick="stopCount()">停止计数!</button>

<p>
点击 "开始计数!" 按钮开始执行计数程序。输入框从 0 开始计算。 点击 "停止计数!" 按钮停止后，可以再次点击点击 "开始计数!" 按钮会重新开始计数。
</p>

<script>
var c = 0;
var t;
var timer_is_on = 0;

function timedCount() {
    document.getElementById("txt").value = c;
    c = c + 1;
    t = setTimeout(function(){ timedCount() }, 1000);
}

function startCount() {
    if (!timer_is_on) {
        timer_is_on = 1;
        timedCount();
    }
}

function stopCount() {
    clearTimeout(t);
    timer_is_on = 0;
}
</script>

</body>
</html>
```

尝试一下 »

### 实例

显示当前时间：

```
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>菜鸟教程(runoob.com)</title>
</head>
<body onload="startTime()">

<div id="txt"></div>

<script>
function startTime() {
    var today = new Date();
    var h = today.getHours();
    var m = today.getMinutes();
    var s = today.getSeconds();
    // 在 numbers<10 的数字前加上 0
    m = checkTime(m);
    s = checkTime(s);
    document.getElementById("txt").innerHTML = h + ":" + m + ":" + s;
    var t = setTimeout(function(){ startTime() }, 500);
}

function checkTime(i) {
    if (i < 10) {
        i = "0" + i;
    }
    return i;
}
</script>

</body>
</html>
```

尝试一下 »

### 实例

传递参数给 alertFunc 函数 ( IE9 及其更早版本不支持):

```
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>菜鸟教程(runoob.com)</title>
</head>
<body>

<p>点击按钮 2 秒后输出 "Hello"。</p>

<p>实例中，我们也会输出传递给 alertFunc() 函数的参数 ( IE9 及更早版本不支持 )。</p>

<button onclick="myStartFunction()">开始</button>

<p id="demo"></p>

<p id="demo2" style="color:red;"></p>

<script>
var myVar;

function myStartFunction() {
    myVar = setTimeout(alertFunc, 2000, "Runoob", "Google");
}

function alertFunc(param1, param2) {
    document.getElementById("demo").innerHTML += "Hello ";

    document.getElementById("demo2").innerHTML = "传递给 alertFunc() 的参数: <br>" 
    + param1 + "<br>" + param2 + "<br>";
}
</script>

</body>
</html>
```

尝试一下 »

 

但是，如果使用匿名函数，则所有浏览器都支持：

```
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>菜鸟教程(runoob.com)</title>
</head>
<body>

<p>点击按钮 2 秒后输出 "Hello"。</p>

<p>实例中，我们也会输出传递给 alertFunc() 函数的参数 ( 兼容所有浏览器 )。</p>

<button onclick="myStartFunction()">开始</button>

<p id="demo"></p>

<p id="demo2" style="color:red;"></p>

<script>
var myVar;

function myStartFunction() {
    myVar = setTimeout(function(){ alertFunc("Runoob", "Google"); }, 2000);
}

function alertFunc(param1, param2) {
    document.getElementById("demo").innerHTML += "Hello ";

    document.getElementById("demo2").innerHTML = "传递给 alertFunc() 的参数: <br>" 
    + param1 + "<br>" + param2 + "<br>";
}
</script>

</body>
</html>
```

尝试一下 »

------

## 相关页面

Window 对象: [setInterval() 方法](http://www.runoob.com/jsref/met-win-setinterval.html)

Window 对象: [setTimeout() 方法](http://www.runoob.com/jsref/met-win-settimeout.html)

Window 对象: [clearTimeout() 方法](http://www.runoob.com/jsref/met-win-cleartimeout.html)





http://www.runoob.com/jsref/met-win-settimeout.html