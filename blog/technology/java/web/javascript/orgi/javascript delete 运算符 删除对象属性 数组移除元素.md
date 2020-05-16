[TOC]



# javascript delete 运算符 删除对象属性 数组移除元素

从对象中删除一个属性，或从数组中移除一个元素。

## [语法]()

```
delete expression
```

## [备注]()

*expression* 参数是有效的 JavaScript 表达式，它通常生成属性名或数组元素。

如果 *expression* 的结果是一个对象，且在 *expression* 中指定的属性存在，同时该对象不允许此属性被删除，则将返回 **false**。

在所有其他情况下，将返回 **true**。

## 示例

下面的示例演示如何从数组中移除元素。

[JavaScript]()

```
// Create an array.
var ar = new Array (10, 11, 12, 13, 14);

// Remove an element from the array.
delete ar[1];

// Print the results.
document.write ("element 1: " + ar[1]);
document.write ("<br />");
document.write ("array: " + ar);
// Output:
//  element 1: undefined
//  array: 10,,12,13,14
```

## 示例

下面的示例演示如何从某一对象删除属性。

[JavaScript]()

```
// Create an object and add expando properties.
var myObj = new Object();
myObj.name = "Fred";
myObj.count = 42;

// Delete the properties from the object.
delete myObj.name;
delete myObj["count"];

// Print the results.
document.write ("name: " + myObj.name);
document.write ("<br />");
document.write ("count: " + myObj.count);
// Output:
//  name: undefined
//  count: undefined
```

## 要求

在以下文档模式中受支持：Quirks、Internet Explorer 6 标准模式、Internet Explorer 7 标准模式、Internet Explorer 8 标准模式、Internet Explorer 9 标准模式、Internet Explorer 10 标准模式和 Internet Explorer 11 标准模式。 此外，也在应用商店应用（Windows 8 和 Windows Phone 8.1）中受支持。 请参阅[版本信息](https://msdn.microsoft.com/zh-cn/library/s4esdbwz(v=vs.94).aspx)。

## [另请参阅]()

[运算符优先级 (JavaScript)](https://msdn.microsoft.com/zh-cn/library/z3ks45k7(v=vs.94).aspx)
[运算符摘要 (JavaScript)](https://msdn.microsoft.com/zh-cn/library/ms364089(v=vs.94).aspx)



https://msdn.microsoft.com/zh-cn/library/2b2z052x(v=vs.94).aspx