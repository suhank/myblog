 

# javascript的“闭包”是什么？

## 说明

闭包是指函数和其词法环境的组合——

闭包就是能够读取其他函数内部变量的函数。例如在javascript中，只有函数内部的子函数才能读取局部变量，所以闭包可以理解成“定义在一个函数内部的函数“。在本质上，闭包是将函数内部和函数外部连接起来的桥梁。

简单来说，形成闭包有两个条件

1.函数中嵌套函数

2.内部函数引用了外部函数的参数

## 解释

假设函数A包含了函数B，B里面又包含了函数C，

```js
A(){
    B(){
        C(){}
    }
}
```

假设每个函数都内部定义了自己的局部变量，那么函数C的词法环境(它可以调用的变量范围)是A和B以及C自己内部所有的变量环境；

函数B的话，它没法调用子函数C的内部环境，可以用A和B自己本身的环境；A的话只能用自己内部词法环境，连B的也拿不到。

这就是闭包的基本含义，再举个例子来说明应用：

假设我现在想用for循环和console.log来输出循环的值：

```javascript
for (var i = 0; i < 5; i++) {
    console.log(i)
}
```

运行结果是输出0，1，2，3，4；

现在我想1秒（1000毫秒）后输出一次这个结果，即输出时设置个时间间隔

即在for中添加setTimeout(funcname,1000);来实现每隔1000毫秒执行一次这个funcname函数，代码如下：

```javascript
for (var i = 0; i < 5; i++) {
    setTimeout(function timer() {
        console, log(i);
    }, 1000);
}
```

运行结果试一下，结果却是输出了5个5，不是预期的01234；

回到ABC模型，for的环境看作A，环境中i值从0变到4；setTimeout看作函数B，console.log看作函数C，由于没有定义局部变量，BC和A的词法环境都是一样的；

当A中执行完循环时，i最终为5，并设置了5个setTimeout函数B，在1秒后，B函数执行内部的C函数来输出i，由于C函数词法环境和AB一样，1秒后for循环已结束i值为4，执行完i++值为5，此时连续输出5个5；

下面我们设置个闭包来把每种i值在取值后通过函数将其限定为局部变量：

```js
for (var i = 0; i < 5; i++) {
    (function (i) {
        setTimeout(function timer() {
            console.log(i);
        }, 1000);
    })(i);
}
```

即在for中，通过(function(i){原函数内容})(i);的形式，将i值传递到该函数中成为该函数的词法环境，设该函数为D，此时变成了：

```
A{D{设定i值{B{C{}}}}};
```

BCD的词法环境都是A的基础上外加传入的i值，闭包也就发挥了它限定词法环境的作用，代码也就可以按预期输出了～



https://www.wukong.com/answer/6595724331681054990/?iid=45816013889&app=news_article&app_id=13&tt_from=android_share&utm_medium=toutiao_android&utm_campaign=client_share