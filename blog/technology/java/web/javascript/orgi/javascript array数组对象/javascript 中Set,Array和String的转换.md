# javascript 中Set,Array和String的转换

类型转换

```js
// array -> set
var set = new Set([1,2,3,4,5]); // 用Set(arr)构造方法
console.log(set);   // Set(5) {1, 2, 3, 4, 5}

// set -> array
var arr = [...set]; // 用...rest运算符
console.log(arr);   // (5) [1, 2, 3, 4, 5]

// Set(str)构造方法传入字符串，返回单个字符的set(字符去重)
var mySet = new Set("Hello");
console.log(mySet); // Set(4) {"H", "e", "l", "o"}

```

数组去重

```js
var arr = [1,2,3,3,3,4];
arr = [...new Set(arr)];
console.log(arr);   // (4) [1, 2, 3, 4]

```





https://blog.csdn.net/wuyujin1997/article/details/89066191