[TOC]



# javascript 布尔值(Boolean)转换规则



## 语法

众所周知, JavaScript有五个基本的值类型：**number**、**string**、**boolean**、**null**和**undefined。**除了null和undefined以外，其他三个具有所谓的基本包装对象。可以使用内置构造函数Number()、String()、Boolean()创建包装对象。

> boolean是JS的6种数据类型(number,string,object,boolean,null,undefined)之一,有且只有两种值:true和false



### 1.使用Boolean(value)方法可以强制转换任意值为boolean类型,除了以下六个值，其他都是自动转为true：

- undefined
- null
- -0
- +0
- NaN
- ‘’（空字符串）

```js
Boolean(undefined) // false
Boolean(null) // false
Boolean(0) // false
Boolean(NaN) // false
Boolean('') // false 
```

### 2.对象的转换规则

> 所有对象的布尔值都是true，甚至连false对应的布尔对象也是true。
>
> 请注意，空对象{}和空数组[]也会被转成true。

```
Boolean(new Boolean(false))// Boolean对象会转成true
Boolean([]) // 空数组会转成true
Boolean({}) // 空对象会转成true 
```

### 3.其中有一些让人困惑的地方

> **&&** 表达式从第一个开始,遇到值为false的表达式,则返回表达式本身,否则返回最后一个表达式
>
> **||** 和 **!** 逻辑运算符原理类似

```js
var obj = new Boolean(false);
console.log(obj && true);//true
console.log(true && obj);//false 
```

因此,第二个console实际上打印的是obj对象的值,即false

------

本文就讨论这么多内容,大家有什么问题或好的想法欢迎在下方参与[留言和评论](http://louiszhai.github.io/2015/12/11/js.boolean/#respond).

本文作者: [louis](https://github.com/Louiszhai)

本文链接: <http://louiszhai.github.io/2015/12/11/js.boolean/>