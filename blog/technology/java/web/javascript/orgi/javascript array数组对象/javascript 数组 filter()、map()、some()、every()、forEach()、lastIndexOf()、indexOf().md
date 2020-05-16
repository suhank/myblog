[TOC]



# javascript 数组 filter()、map()、some()、every()、forEach()、lastIndexOf()、indexOf()

## filter():

 语法：

```
var filteredArray = array.filter(callback[, thisObject]);
```

参数说明：

callback： 要对每个数组元素执行的回调函数。
thisObject ： 在执行回调函数时定义的this对象。

```js
//过滤掉小于 10 的数组元素：

//代码：
function isBigEnough(element, index, array) {
    return (element >= 10);
}
var filtered = [12, 5, 8, 130, 44].filter(isBigEnough);
// 12, 130, 44
//结果：[12, 5, 8, 130, 44].filter(isBigEnough) ： 12, 130, 44 
```



功能说明：

**对数组中的每个元素都执行一次指定的函数（callback），并且创建一个新的数组，该数组元素是所有回调函数执行时返回值为 true 的原数组元素。它只对数组中的非空元素执行指定的函数，没有赋值或者已经删除的元素将被忽略，同时，新创建的数组也不会包含这些元素。**

回调函数可以有三个参数：当前元素，当前元素的索引和当前的数组对象。

如参数 **thisObject** 被传递进来，它将被当做回调函数（callback）内部的 this 对象，如果没有传递或者为null，那么将会使用全局对象。

filter 不会改变原有数组，记住：只有在回调函数执行前传入的数组元素才有效，在回调函数开始执行后才添加的元素将被**忽略**，而在回调函数开始执行到最后一个元素这一期间，数组元素被删除或者被更改的，将以回调函数访问到该元素的时间为准，被删除的元素将被忽略。

## **map():**



```
//将所有的数组元素转换为大写：

var strings = ["hello", "Array", "WORLD"];
function makeUpperCase(v)
{
    return v.toUpperCase();
}
var uppers = strings.map(makeUpperCase);
// uppers is now ["HELLO", "ARRAY", "WORLD"]
// strings is unchanged
//结果：["hello", "Array", "WORLD"].map(makeUpperCase) ： HELLO, ARRAY, WORLD 
```



## **some():**

对数组中的每个元素都执行一次指定的函数（callback），直到此函数返回 true，如果发现这个元素，some 将返回 true，如果回调函数对每个元素执行后都返回 false ，some 将返回 false。它只对数组中的非空元素执行指定的函数，没有赋值或者已经删除的元素将被忽略。

```js
//检查是否有数组元素大于等于10：
function isBigEnough(element, index, array) {
    return (element >= 10);
}
var passed = [2, 5, 8, 1, 4].some(isBigEnough);
// passed is false
passed = [12, 5, 8, 1, 4].some(isBigEnough);
// passed is true
//结果：
//[2, 5, 8, 1, 4].some(isBigEnough) ： false 
//[12, 5, 8, 1, 4].some(isBigEnough) ： true
```



## **every():**

**对数组中的每个元素都执行一次指定的函数（callback），直到此函数返回 false，如果发现这个元素，every 将返回 false，如果回调函数对每个元素执行后都返回 true ，every 将返回 true。它只对数组中的非空元素执行指定的函数，没有赋值或者已经删除的元素将被忽略**



```
//测试是否所有数组元素都大于等于10：

function isBigEnough(element, index, array) {
    return (element >= 10);
}
var passed = [12, 5, 8, 130, 44].every(isBigEnough);
// passed is false
passed = [12, 54, 18, 130, 44].every(isBigEnough);
// passed is true
//结果：
//[12, 5, 8, 130, 44].every(isBigEnough) 返回 ： false 
//[12, 54, 18, 130, 44].every(isBigEnough) 返回 ： true 
```



## **forEach():**



```
//打印数组内容：

function printElt(element, index, array) {
    document.writeln("[" + index + "] is " + element + "<br />");
}
[2, 5, 9].forEach(printElt);
// Prints:
// [0] is 2
// [1] is 5
// [2] is 9
//结果：
//[0] is 2
//[1] is 5
//[2] is 9
```



## **lastIndexOf():**

#### 语法

```
var index = array.lastIndexOf(searchElement[, fromIndex]);
```

参数说明

searchElement： 要搜索的元素

fromIndex ： 开始搜索的位置，默认为数组的长度（length），在这样的情况下，将搜索所有的数组元素。**搜索是反方向进行的。**

功能说明

比较 **searchElement** 和数组的每个元素是否绝对一致（===），当有元素符合条件时，返回当前元素的索引。如果没有发现，就直接返回 -1 。



```
//查找符合条件的元素：

var array = [2, 5, 9, 2];
var index = array.lastIndexOf(2);
// index is 3
index = array.lastIndexOf(7);
// index is -1
index = array.lastIndexOf(2, 3);
// index is 3
index = array.lastIndexOf(2, 2);
// index is 0
index = array.lastIndexOf(2, -2);
// index is 0
index = array.lastIndexOf(2, -1);
// index is 3
//结果：
//[2, 5, 9, 2].lastIndexOf(2) ： 3 
//[2, 5, 9, 2].lastIndexOf(7) ： -1 
//[2, 5, 9, 2].lastIndexOf(2, 3) ： 3 
//[2, 5, 9, 2].lastIndexOf(2, 2) ： 0 
//[2, 5, 9, 2].lastIndexOf(2, -2) ： 0 
//[2, 5, 9, 2].lastIndexOf(2, -1) ： 3 
```



## **indexOf():**

功能与lastIndexOf()一样，**搜索是正向进行的**



```
//查找符合条件的元素：

var array = [2, 5, 9];
var index = array.indexOf(2);
// index is 0
index = array.indexOf(7);
// index is -1
//结果：
//[2, 5, 9].indexOf(2) ： 0 
//[2, 5, 9].indexOf(7) ： -1 
```







<https://www.cnblogs.com/xiao-hong/p/3194027.html>