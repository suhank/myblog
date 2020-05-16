 

[TOC]



# javascript 判断一个 object 对象是否为空

判断一个对象是否为空对象，本文给出三种判断方法：

## 1.最常见的思路，`for...in...` 遍历属性，为真则为“非空数组”；否则为“空数组”

```js
for (var i in obj) { // 如果不为空，则会执行到这一步，返回true
    return true
}
return false // 如果为空,返回false 
```

## 2.通过 `JSON` 自带的 `stringify()` 方法来判断:

`JSON.stringify()` 方法用于将 `JavaScript` 值转换为 `JSON` 字符串。

```js
if (JSON.stringify(data) === '{}') {
    return false // 如果为空,返回false
}
return true // 如果不为空，则会执行到这一步，返回true 
```

这里需要注意为什么不用 `toString()`，因为它返回的不是我们需要的。

```js
var a = {}
a.toString() // "[object Object]" 
```

## 3.`ES6` 新增的方法 `Object.keys()`:  这个时间对象判断无效

`Object.keys()` 方法会返回一个由一个给定对象的自身可枚举属性组成的数组。

如果我们的对象为空，他会返回一个空数组，如下：

```js
var a = {}
Object.keys(a) // [] 
```

我们可以依靠Object.keys()这个方法通过判断它的长度来知道它是否为空。

```js
if (Object.keys(object).length === 0) {
    return false // 如果为空,返回false
}
return true // 如果不为空，则会执行到这一步，返回true 
```

作者：言墨儿 
链接：<http://www.jianshu.com/p/972d0f277d45>

## 实际使用

```js

  /**
   * 校验对象是否为空(包括数组)
   */
  checkIsNullObj(obj) {
    if (!obj){
      return true
    }
    //数组是否为空
    if (Array.isArray(obj) && obj.length == 0){
      return true
    }
    return JSON.stringify(obj) === '{}'
  },
  /**
   * 校验对象是否不为空
   */
  checkNonNullObj(obj) {
    return !this.checkIsNullObj(obj)
  }
```

 



https://blog.csdn.net/FungLeo/article/details/78113661