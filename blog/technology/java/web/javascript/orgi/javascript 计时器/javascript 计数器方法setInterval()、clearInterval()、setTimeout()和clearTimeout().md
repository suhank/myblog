[TOC]



# javascript计数器方法setInterval()、clearInterval()、setTimeout()和clearTimeout()

| 方法          | 描述                                                   |
| ------------- | ------------------------------------------------------ |
| setInterval   | 周期性地调用一个函数(function)或者执行一段代码。       |
| clearInterval | 取消掉用setInterval设置的重复执行动作。                |
| setTimeout    | 在指定的延迟时间之后调用一个函数或者执行一个代码片段。 |
| clearTimeout  | 方法可取消由 setTimeout() 方法设置的 timeout。         |

## setInterval()

window.setInterval()方法，周期性地调用一个函数(function)或者执行一段代码。

### 语法

> var intervalID = window.setInterval(func, delay[, param1, param2, ...]);
> var intervalID = window.setInterval(code, delay);

### 详解

```
intervalID 是此重复操作的唯一辨识符，可以作为参数传给clearInterval()。  
func 是你想要重复调用的函数。  
code 是另一种语法的应用，是指你想要重复执行的一段字符串构成的代码(使用该语法是不推荐的，不推荐的原因和eval()一样)。  
delay 是每次延迟的毫秒数 (一秒等于1000毫秒)，函数的每次调用会在该延迟之后发生。和setTimeout一样，实际的延迟时间可能会稍长一点。  
```

需要注意的是,IE不支持第一种语法中向延迟函数传递额外参数的功能.如果你想要在IE中达到同样的功能,你必须使用一种兼容代码 (查看[callback arguments](https://developer.mozilla.org/zh-CN/docs/DOM/window.setTimeout#Callback_arguments) 一段)。

### 例子

```js
setInterval(function(){
    console.log("log")
},1000)
```

## clearInterval()

`window.clearInterval()` 取消掉用setInterval设置的重复执行动作.

### 语法

> window.clearInterval(intervalID)

### 详解

```
在指定的延迟时间之后调用一个函数或者执行一个代码片段。  
intervalID是你想要取消的重复动作的ID,这个ID是个整数,是由setInterval()返回的。 
```

### 例子

```js
var pageTimer = {} ; //定义计算器全局变量
//赋值模拟
pageTimer["timer1"] = setInterval(function(){},2000);
pageTimer["timer2"] = setInterval(function(){},2000);
//全部清除方法
for(var each in pageTimer){
    clearInterval(pageTimer[each]);
}
// 暴力清除
for(var i = 1; i < 1000; i++) {
    clearInterval(i);
}
```

分析：实际上暴力清除的方式是不可取的，在不得已情况下才使用，在IE下，定时器返回值在IE下面是8位数字如：248147094，并且起始值不能确定，而Chrome和firefox下是从1开始的个位数字，一般项目还是建议第一种，并且第一种的扩展性也好，比如可以做个方法，清除除了指定定时器之外的所有定时器。

## setTimeout()

在指定的延迟时间之后调用一个函数或者执行一个代码片段。
`window.setTimeout()`

### 语法

> var timeoutID = window.setTimeout(func, delay, [param1, param2, ...]);
> var timeoutID = window.setTimeout(code, delay);

### 详解

* timeoutID 是该延时操作的数字ID, 此ID随后可以用来作为window.clearTimeout方法的参数。
* func 是你想要在delay毫秒之后执行的函数。
* code 在第二种语法,是指你想要在delay毫秒之后执行的代码 (使用该语法是不推荐的, 不推荐的原因和eval()一样)。 
* delay 是延迟的毫秒数 (一秒等于1000毫秒),函数的调用会在该延迟之后发生.但是实际的延迟时间可能会稍长一点,查看下面的备注。

需要注意的是，IE不支持第一种语法中向延迟函数传递额外参数的功能。如果你想要在IE中达到同样的功能，你必须使用一种兼容代码 (查看[callback arguments](https://developer.mozilla.org/zh-CN/docs/DOM/window.setTimeout#Callback_arguments) 一段)。

### 备注

你可以使用 window.clearTimeout()来取消延迟操作。
如果你希望你的代码被重复的调用 (比如每 N 毫秒一次),考虑使用window.setInterval()。

### 例子

```js
// 推荐
window.setTimeout(function() {
    alert("Hello World!");
}, 500);

// 不推荐
window.setTimeout("alert("Hello World!");", 500);
```

## clearTimeout()

window.clearTimeout() 方法可取消由 setTimeout() 方法设置的 timeout。

### 语法

> clearTimeout(id_of_settimeout)

### 详解

| 参数             | 描述                                                         |
| ---------------- | ------------------------------------------------------------ |
| id_of_settimeout | 由 setTimeout() 返回的 ID 值。该值标识要取消的延迟执行代码块。 |

### 实例

```js
var c=0
var t
function timedCount(){
    document.getElementById('txt').value=c
    c=c+1
    t=setTimeout("timedCount()",1000)
}
function stopCount(){
    clearTimeout(t)
}
```





https://segmentfault.com/a/1190000002475127