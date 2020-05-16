[TOC]



# 对ES5中apply、call和bind三个方法的解读

JavaScript中，apply、call 和 bind 都是为了改变某个函数**运行时的上下文**而存在的，其实就是为了改变所调用的函数体内部 **this 的指向**。

### Function.prototype.apply()方法

**Example**

```
var nodeList = document.querySelectorAll("div");
Array.prototype.slice.apply(nodeList).forEach((node)=>{
  console.log(node);    // 输出每一个div节点对象
});
```

**代码解读：**
上面是一段JS中常用的代码，这段代码表示的意义是，当我们获取到一个**类数组**的对象的时候如何让它可以调用所有**数组**所拥有的方法。我们都知道，所有的获取dom元素的方法，返回的不是dom节点对象，就是包含节点对象的**类数组**，而类数组虽然从输出来看和数组是没什么区别的，但是在原型链上是有很大的差异的。像NodeList这个类数组就是不包含基本上所有数组的常用方法的，所以这个时候就需要 **借** 数组的**slice**方法来将NodeList这个歌类数组转成真正的数组对象，这样就可以直接调用push方法了。

### Function.prototype.call()方法

**Example**

```
var nodeList = document.querySelectorAll("div");
Array.prototype.slice.call(nodeList).forEach((node)=>{
  console.log(node);    // 输出每一个div节点对象
});
```

**代码解读：**上面这段代码和apply方法中的例子是一模一样的，所以，其实他们两个方法的作用都是一致的，用法呢就是类似的，而为什么说是类似的呢，看下面这个例子：
**Example：**

```
var max1 = Math.max.call(null, 1,2,3); 
var max2 = Math.max.apply(null, [1,2,3]);
console.log(max1);      // 结果是3
console.log(max2)       // 结果是3
```

**代码解读：**从上面这个例子中可以看出，call和apply的用法不同之处在于，如果所调用的方法方法需要传入参数，那么call需要从第二个入参开始传入需要的**值**，而apply是在第二入参用**数组**来传递参数，这里要注意的是哪怕是参数只需要传入一个，也全都按照这种语法规则，不然如上面的**Math.max**就会报**TypeError**，所以建议如下：**当所调用的方法是0个参数的，那随便哪个都可以，如果是1~2个参数的建议使用call方法，如果是3个及以上的用apply方法**。

### Function.prototype.bind()方法

**Example**

```
var nodeList = document.querySelectorAll("div");
var nodeArray = Array.prototype.slice.bind(nodeList);
nodeArray().forEach((node)=>{
  console.log(node);    // 输出每一个div节点对象
});
```

**代码解读：**从上面的例子看一看出bind方法只是**替换了所调用方法的this指向**，并不会**主动去执行**这个方法，而apply和call方法是**即改变了this指向，又立即执行的**，所以bind一般用于不需要立即执行，只要求更改this指向的场景，如：click事件的回调函数一般就会用bind去改变回调函数的this指向，而在click事件触发的时候执行。最后说明一下，bind的参数和call的参数传递是一致的，例子如下：

**Example**

```
var maxBind = Math.max.bind(null, 1,2,3); 
var max = maxBind();
console.log(max);      // 结果是3
```

**结束语：**上面就是javaScript对于如何改变运行时上下文的三个方法了，下面是我自己实现的一个类似运行时改变上下文的方法，不适合在实际场景中使用，仅供大家参考：

```
var Person = {
  context:null,
  name:'小明',
  say:function(greeting){
    var me = this.context || this;
    return greeting + me.name
  }
}
Person.say.__proto__.callCopy = function(_context,greeting){
  Person.context = _context;
  return Person.say(greeting);
}
var world1 =Person.say('你好 ');
var world2 = Person.say.callCopy({name:'xiaoMing'},'hello ');
console.log(world1);    // 你好 小明  
console.log(world2);    // hello xiaoMing
```





https://segmentfault.com/a/1190000010263750