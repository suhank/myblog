[TOC]



# javascript 中实现sleep暂停睡眠功能的多种方式

 

由于很多语言都有sleep函数，但是js中没有，下面介绍JavaScript实现类似sleep的多种方式。



## 1、利用循环 

```
function sleep(d){
  for(var t = Date.now();Date.now() - t <= d;);
}
sleep(5000); //当前方法暂停5秒
```

优点：简单粗暴，通俗易懂。

缺点：这是最简单粗暴的实现，确实 sleep 了，也确实卡死了，CPU 会飙升，无论你的服务器 CPU 有多么 Niubility。



## 2、Promise版本  

```
function sleep(ms) {
  return new Promise(resolve => 
      setTimeout(resolve, ms)
  )
}
sleep(3000).then(()=>{
   //code
})
```

优点：这种方式实际上是用了 setTimeout，没有形成进程阻塞，不会造成性能和负载问题。

缺点：虽然不像 callback 套那么多层，但仍不怎么美观，而且当我们需要在某过程中需要停止执行（或者在中途返回了错误的值），还必须得层层判断后跳出，非常麻烦，而且这种异步并不是那么彻底，还是看起来别扭。



## 3、通过generate来实现

```
function* sleep(ms){
   yield new Promise(function(resolve,reject){
             console.log(111);
             setTimeout(resolve,ms);
        })  
}
sleep(500).next().value.then(()=>{
  console.log(11111)
})
```

优点：同 Promise 优点，另外代码就变得非常简单干净，没有 then 那么生硬和恶心。

缺点：但不足也很明显，就是每次都要执行 next() 显得很麻烦，虽然有co（第三方包）可以解决，但就多包了一层不好看，错误也必须按co的逻辑来处理不爽。



## 4、通过 Async/Await 封装

```
function sleep(ms){
  return new Promise((resolve)=>setTimeout(resolve,ms));
}
async function test(){
  var temple=await sleep(1000);
  console.log(1111)
  return temple
}
test();
//延迟1000ms输出了1111
```

优点：同 Promise 和 Generator 优点。 Async/Await 可以看做是 Generator 的语法糖，Async 和 Await 相较于 * 和 yield 更加语义，另外各个函数都是扁平的，不会产生多余的嵌套，代码更加清爽易读。

缺点： ES7 语法存在兼容性问题，有 babel 一切兼容性都不是问题



## 5、使用node-sleep

```
var sleep = require('sleep');
var n=10;

sleep.sleep(n) //sleep for n seconds
sleep.msleep(n) //sleep for n miliseconds
sleep.usleep(n) //sleep for n microseconds (1 second is 1000000 microseconds)
```

优点：能够实现更加精细的时间精确度，而且看起来就是真的 sleep 函数，清晰直白。

缺点：缺点需要安装这个模块，这也许算不上什么缺点。

地址：<https://github.com/ErikDubbelboer/node-sleep>









<http://www.fly63.com/article/detial/925>