[TOC]

# 理解spread运算符与rest参数

spread运算符与rest参数 是ES6的新语法。
它们的作用是什么？能做什么事情？

## **1. rest运算符用于获取函数调用时传入的参数。**



```
function testFunc(...args) {
   console.log(args);  // ['aa', 'bb', 'cc']
   console.log(args.length); // 3
}
 // 调用函数
 testFunc('aa', 'bb', 'cc'); 
```



## **2. spread运算符用于数组的构造，析构，以及在函数调用时使用数组填充参数列表。**



```
let arrs1 = ['aa', 'bb'];
let arrs2 = ['cc', 'dd'];

// 合并数组
let arrs = [...arrs1, ...arrs2];
console.log(arrs); // ['aa', 'bb', 'cc', 'dd']

// 析构数组
let param1, param2;
[param1, ...param2] = arrs1;

console.log(param1); // aa
console.log(param2); // ['bb']
```



## **3. 类数组的对象转变成数组。**

比如我们常见的是arguments对象，它是类数组，它有长度属性，但是没有数组的方法，比如如下代码：



```
function testFunc() {
   console.log(arguments); // ['a', 'b']
   console.log(typeof arguments); // object
   console.log(arguments.length); // 2
   console.log(arguments.push('aa')); // 报错  arguments.push is not a function
    };
 // 函数调用
 testFunc('a', 'b');
```



把类数组对象转换成数组，代码如下：



```
function testFunc() {
   // 转换成数组
   var toArray = [...arguments];
   console.log(toArray); // ['a', 'b']
   toArray.push('11');   // ['a', 'b', '11']
   console.log(toArray);
 };
 // 函数调用
 testFunc('a', 'b');
```



## **4. 数组的深度拷贝**

浅拷贝如下demo：

```
var arr1 = [1, 2];
var arr2 = arr1;
arr1.push(3);
console.log(arr1); // [1, 2, 3]
console.log(arr2); // [1, 2, 3]
```

如上代码，arr1是一个数组有2个值 [1, 2], 然后把 arr1 赋值个 arr2, 接着往arr1中添加一个元素3，然后就会影响arr2中的数组。
因为我们知道浅拷贝是：拷贝的是该对象的引用，所以引用值改变，其他值也会跟着改变。
所以引用值也跟着改变。

深度拷贝对象如下代码：

```
var arr1 = [1, 2];
var arr2 = [...arr1];
arr1.push(3);
console.log(arr1); // [1, 2, 3]
console.log(arr2); // [1, 2]
```

## **5. 字符串转数组**

如下代码：

```
var str = 'kongzhi';
var arr = [...str];
console.log(arr); // ["k", "o", "n", "g", "z", "h", "i"]
```

如果一个函数最后一个形参以 ...为前缀的，则在函数调用时候，该形参会成为一个数组，数组中的元素都是传递给这个函数多出来的实参的值。
比如如下代码：

```
function test(a, ...b) {
   console.log(a); // 11
   console.log(b); // ['22', '33']
}
test('11', '22', '33');
```

## **6. 解构赋值**

解构赋值允许你使用类似数组或对象字面量的语法将数组和对象的属性赋给各种变量。



```
// 解构数组
var arr = ['aa', 'bb', 'cc'];
let [a1, a2, a3] = arr;
console.log(a1); // aa
console.log(a2); // bb
console.log(a3); // cc

// 对象解构
var o = {a: 1, b: 2};
var {a, b} = o;
console.log(a);  // 1
console.log(b);  // 2
```



## **7. 交换变量**

```
var a = 1, b = 2;
[a, b] = [b, a];
console.log(a); // 2
console.log(b); // 1
```

## **8. 从函数中返回多个值**



```
function test() {
   return {
      aa: 1,
      bb: 2
   }
}
let { aa, bb } = test();
console.log(aa); // 1
console.log(bb); // 2
```

