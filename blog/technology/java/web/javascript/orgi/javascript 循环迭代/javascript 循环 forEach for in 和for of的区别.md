[TOC]



# javascript 循环 forEach for in 和for of的区别

对数组的遍历大家最常用的就是for循环，ES5的话也可以使用forEach，ES5具有遍历数组功能的还有map、filter、some、every、reduce、reduceRight等，只不过他们的返回结果不一样。但是使用foreach遍历数组的话，使用break不能中断循环，使用return也不能返回到外层函数。

那么接下来我们一起看一下for in 和for of 的区别吧。

## for in 循环对象

### 简介

看一个简单的例子

```js
//for in 应用于数组
Array.prototype.sayHello = function(){
    console.log("Hello")
}
Array.prototype.str = 'world';
var myArray = [1,2,10,30,100];
myArray.name='数组';

for(let index in myArray){
    console.log(index);
}
//输出结果如下
0,1,2,3,4,name,str,sayHello

//for in  应用于对象中
Object.prototype.sayHello = function(){
    console.log('Hello');
}
Obeject.prototype.str = 'World';
var myObject = {name:'zhangsan',age:100};

for(let index in myObject){
    console.log(index);
}
//输出结果
name,age,str,sayHello
//首先输出的是对象的属性名，再是对象原型中的属性和方法，
//如果不想让其输出原型中的属性和方法，可以使用hasOwnProperty方法进行过滤
for(let index in myObject){
    if(myObject.hasOwnProperty(index)){
        console.log(index)
    }
}
//输出结果为
name,age
//你也可以用Object.keys()方法获取所有的自身可枚举属性组成的数组。
Object.keys(myObject)
 
```

可以看出for in 应用于数组循环返回的是数组的下标和数组的属性和原型上的方法和属性，而for in应用于对象循环返回的是对象的属性名和原型中的方法和属性。

使用for in 也可以遍历数组，但是会存在以下问题：

1.index索引为字符串型数字，不能直接进行几何运算

2.遍历顺序有可能不是按照实际数组的内部顺序

3.使用for in会遍历数组所有的可枚举属性，包括原型。例如上栗的原型方法method和name属性







### **for..in 不应该被用来迭代一个下标顺序很重要的 Array** .

数组索引仅是可枚举的整数名，其他方面和别的普通对象属性没有什么区别。**for...in 并不能够保证返回的是按一定顺序的索引**，但是它会返回所有可枚举属性，包括非整数名称的和继承的。

因为迭代的顺序是依赖于执行环境的，所以数组遍历不一定按次序访问元素。 因此当迭代那些访问次序重要的 arrays 时用整数索引去进行 [`for`](https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/for) 循环 (或者使用 [`Array.prototype.forEach()`](https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Array/forEach) 或 [`for...of`](https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Statements/for...of) 循环) 。

### **仅迭代自身的属性**

如果你只要考虑对象本身的属性，而不是它的原型，那么使用 [`getOwnPropertyNames()`](https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Object/getOwnPropertyNames) 或执行  [`hasOwnProperty()`](https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Object/hasOwnProperty) 来确定某属性是否是对象本身的属性 (也能使用[`propertyIsEnumerable`](https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Object/propertyIsEnumerable))。另外，如果你知道外部不存在任何的干扰代码，你可以扩展内置原型与检查方法。

### 例子

```js
var obj = {a:1, b:2, c:3};
    
for (var prop in obj) {
  console.log("obj." + prop + " = " + obj[prop]);
}

// Output:
// "obj.a = 1"
// "obj.b = 2"
// "obj.c = 3"
```



```js
var triangle = {a:1, b:2, c:3};

function ColoredTriangle() {
  this.color = "red";
}

ColoredTriangle.prototype = triangle;

var obj = new ColoredTriangle();

for (var prop in obj) {
  if( obj.hasOwnProperty( prop ) ) {
    console.log("o." + prop + " = " + obj[prop]);
  } 
}

// Output:
// "o.color = red"
```



摘自<https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Statements/for...in#Description>

## for of 循环数组

```js
Object.prototype.sayHello = function(){
    console.log('Hello');
}
var myObject = {
    name:'zhangsan',
    age:10
}

for(let key of myObject){
    consoloe.log(key);
}
//输出结果
//typeError

Array.prototype.sayHello = function(){
    console.log("Hello");
}
var myArray = [1,200,3,400,100];
for(let key of myArray){
    console.log(key);
}
//输出结果
1,200,3,400,100
 
```

for in遍历的是数组的索引（即键名），而for of遍历的是数组元素值。 所以for in更适合遍历对象，不要使用for in遍历数组。

## forEach ie浏览器有缺陷

自从JavaScript5起，我们开始可以使用内置的forEach 方法：，forEach方法中的function回调有三个参数：第一个参数是遍历的数组内容，第二个参数是对应的数组索引，第三个参数是数组本身。比如如下例子：

```
var arr = [1,2,3,4];
arr.forEach(function(value,index,array){
    array[index] == value;    //结果为true
    sum+=value;  
    });
console.log(sum);    //结果为 8 
```



```
myArray.forEach(function (value) {
console.log(value);
});
```

但是以上，代码在IE中却无法正常工作。因为IE的Array 没有这个方法。

```
alert(Array.prototype.forEach);
```

执行以上这句得到的是 “undefined”， 也就是说在**IE 中 Array 没有forEach的方法。**

如果想要在IE中使用这个方法，就需要给它手动添加这个原型方法。

```
if (!Array.prototype.forEach) {  
    Array.prototype.forEach = function(callback, thisArg) {  
        var T, k;  
        if (this == null) {  
            throw new TypeError(" this is null or not defined");  
        }  
        var O = Object(this);  
        var len = O.length >>> 0; // Hack to convert O.length to a UInt32  
        if ({}.toString.call(callback) != "[object Function]") {  
            throw new TypeError(callback + " is not a function");  
        }  
        if (thisArg) {  
            T = thisArg;  
        }  
        k = 0;  
        while (k < len) {  
            var kValue;  
            if (k in O) {  
                kValue = O[k];  
                callback.call(T, kValue, k, O);  
            }  
            k++;  
        }  
    };  
}   
```


相比于传统的写法，forEach写法简单了许多，但也有短处：你不能中断循环(使用语句或使用语句。可以使用如下两种方式：

```
if 语句控制
return . (return true, false)
```

以下例子是取出数组中2的倍数和3的倍数的数：

```
arryAll.forEach(function(e){  
    if(e%2==0)  
    {  
        arrySpecial.push(e);  
        return;  
    }  
    if(e%3==0)  
    {      
        arrySpecial.push(e);  
        return;  
    }  
})  
```



原文：https://blog.csdn.net/WKY_CSDN/article/details/74391905 




https://juejin.im/post/5aea83c86fb9a07aae15013b