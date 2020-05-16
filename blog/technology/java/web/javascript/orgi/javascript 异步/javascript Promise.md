[TOC]



# javascript Promise

## 简介

古人云：“君子一诺千金”，这种“承诺将来会执行”的对象在JavaScript中称为Promise对象。

Promise有各种开源实现，在ES6中被统一规范，由浏览器直接支持。先测试一下你的浏览器是否支持Promise：

```Js
 'use strict';
new Promise(function () {});
// 直接运行测试:
console.log('支持Promise!');
```

## 简单Promise示例

```js
function test(resolve, reject) {
    if (true) {
        //处理逻辑成功的情况执行resolve
        resolve('resolve执行了');
    } else {
        //处理逻辑失败的情况执行reject
        reject('reject执行了');
    }
}

//没有promise写法
test(function (resolve) {
    console.log('成功：' + resolve);
}, function (reject) {
    console.log('失败：' + reject);
});

//promise写法1
var p1 = new Promise(test);
p1.then((resolve) => {
    console.log('成功：' + resolve);
}, (reject) => {
    console.log('失败：' + reject);
});

//promise写法2
var p1 = new Promise(test);
p1.then((resolve) => {
    console.log('成功：' + resolve);
});
p1.catch((reject) => {
    console.log('失败：' + reject);
});

//promise写法3
new Promise(test).then((resolve) => {
    console.log('成功：' + resolve);
}).catch((reject) => {
        console.log('失败：' + reject)
    }
);
```

 

## Promise.prototype.finally()

 `finally()` 方法返回一个[`Promise`](https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Promise)，在promise执行结束时，无论结果是fulfilled或者是rejected，在执行[`then()`](https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Promise/then)和[`catch()`](https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Promise/catch)后，都会执行**finally**指定的回调函数。这为指定执行完promise后，无论结果是fulfilled还是rejected都需要执行的代码提供了一种方式，避免同样的语句需要在[`then()`](https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Promise/then)和[`catch()`](https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Promise/catch)中各写一次的情况。

### 语法

```js
p.finally(onFinally);

p.finally(function() {
   // 返回状态为(resolved 或 rejected)
});参数
```

- `onFinally`

  `Promise` 状态改变后执行的回调函数。

#### 返回值

返回一个设置了 `finally` 回调函数的[`Promise`](https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Promise)对象。 

#### 描述

如果你想在 promise 执行完毕后无论其结果怎样都做一些处理或清理时，`finally()` 方法可能是有用的。

`finally()` 虽然与 `.then(onFinally, onFinally)` 类似，它们不同的是：

- 调用内联函数时，不需要多次声明该函数或为该函数创建一个变量保存它。
- 由于无法知道`promise`的最终状态，所以`finally`的回调函数中不接收任何参数，它仅用于无论最终结果如何都要执行的情况。
- 与`Promise.resolve(2).then(() => {}, () => {})` （resolved的结果为`undefined`）不同，`Promise.resolve(2).finally(() => {})` resolved的结果为 `2`。
- 同样，`Promise.reject(3).then(() => {}, () => {})` (resolved 的结果为`undefined`), `Promise.reject(3).finally(() => {})` rejected 的结果为 `3`。

**注意:** 在`finally`回调中 `throw`（或返回被拒绝的promise）将以 `throw()` 指定的原因拒绝新的promise.

### 示例

```js
function test(resolve, reject) {
    if (true) {
        //处理逻辑成功的情况执行resolve
        resolve('resolve执行了');
    } else {
        //处理逻辑失败的情况执行reject
        reject('reject执行了');
    }
}

new Promise(test).then((resolve) => {
    console.log('成功：' + resolve);
}).catch((reject) => {
    console.log('失败：' + reject)
}).finally(() => {
    console.log('finally执行了.....')
});
```

### 浏览器兼容性

Update compatibility data on GitHub

|               | Desktop        | Mobile         | Server         |                   |                |                  |                 |                    |              |                     |                   |                  |                  |                    |
| ------------- | -------------- | -------------- | -------------- | ----------------- | -------------- | ---------------- | --------------- | ------------------ | ------------ | ------------------- | ----------------- | ---------------- | ---------------- | ------------------ |
|               | Chrome         | Edge           | Firefox        | Internet Explorer | Opera          | Safari           | Android webview | Chrome for Android | Edge Mobile  | Firefox for Android | Opera for Android | Safari on iOS    | Samsung Internet | Node.js            |
| Basic support | Full support63 | Full support18 | Full support58 | No supportNo      | Full support50 | Full support11.1 | Full support63  | Full support63     | No supportNo | Full support58      | Full support50    | Full support11.1 | No supportNo     | Full support10.0.0 |

 

https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Promise/finally





## Promise.prototype.then()

`**then()**` 方法返回一个  [`Promise`](https://developer.mozilla.org/zh-CN/docs/Web/API/Promise) 。它最多需要有两个参数：Promise 的成功和失败情况的回调函数。

<iframe src="https://interactive-examples.mdn.mozilla.net/pages/js/promise-then.html" height="250" class="interactive interactive-js" frameborder="0" width="100%" style="font-style: normal !important; margin: 0px; padding: 10px; border: 1px solid rgb(234, 242, 244); max-width: 100%; box-sizing: border-box; background-color: rgb(245, 249, 250); color: rgb(51, 51, 51); height: 490px; width: 948.172px;"></iframe>

注意：如果忽略针对某个状态的回调函数参数，或者提供非函数 (nonfunction) 参数，那么 `then` 方法将会丢失关于该状态的回调函数信息，但是并不会产生错误。如果调用 `then`的 `Promise` 的状态（fulfillment 或 rejection）发生改变，但是 `then` 中并没有关于这种状态的回调函数，那么 `then` 将创建一个没有经过回调函数处理的新 `Promise` 对象，这个新 `Promise` 只是简单地接受调用这个 `then` 的原 `Promise` 的终态作为它的终态。

### 语法

```
p.then(onFulfilled, onRejected);

p.then(function(value) {
   // fulfillment
  }, function(reason) {
  // rejection
});
```

#### 参数

- onFulfilled

  当Promise变成接受状态（fulfillment）时，该参数作为回调函数被调用（参考： [`Function`](https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Function)）。该函数有一个参数，即接受的最终结果（the fulfillment  value）。如果传入的 `onFulfilled `参数类型不是函数，则会在内部被替换为`(x) => x `，即原样返回 promise 最终结果的函数

- onRejected

  当Promise变成拒绝状态（rejection ）时，该参数作为回调函数被调用（参考： [`Function`](https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Function)）。该函数有一个参数,，即拒绝的`原因（the rejection reason）`。

```js
var p = new Promise((resolve, reject) => {
    resolve('foo')
})

// 'bar' 不是函数，会在内部被替换为 (x) => x
p.then('bar').then((value) => {
    console.log(value) // 'foo'
})
```

- 返回值[节](https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Promise/then#%E8%BF%94%E5%9B%9E%E5%80%BC)then方法返回一个[`Promise`](https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Promise)，而它的行为与then中的回调函数的返回值有关：如果then中的回调函数返回一个值，那么then返回的Promise将会成为接受状态，并且将返回的值作为接受状态的回调函数的参数值。如果then中的回调函数抛出一个错误，那么then返回的Promise将会成为拒绝状态，并且将抛出的错误作为拒绝状态的回调函数的参数值。如果then中的回调函数返回一个已经是接受状态的Promise，那么then返回的Promise也会成为接受状态，并且将那个Promise的接受状态的回调函数的参数值作为该被返回的Promise的接受状态回调函数的参数值。如果then中的回调函数返回一个已经是拒绝状态的Promise，那么then返回的Promise也会成为拒绝状态，并且将那个Promise的拒绝状态的回调函数的参数值作为该被返回的Promise的拒绝状态回调函数的参数值。如果then中的回调函数返回一个未定状态（pending）的Promise，那么then返回Promise的状态也是未定的，并且它的终态与那个Promise的终态相同；同时，它变为终态时调用的回调函数参数与那个Promise变为终态时的回调函数的参数是相同的。

#### 描述

由于 `then` 和 [`Promise.prototype.catch()`](https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Promise/catch) 方法都会返回 promise，它们可以被链式调用 — 一种称为**复合**（ *composition）* 的操作.

### 示例

#### `使用then方法`

```js
let p1 = new Promise(function(resolve, reject) {
  resolve("Success!");
  // or
  // reject ("Error!");
});

p1.then(function(value) {
  console.log(value); // Success!
}, function(reason) {
  console.log(reason); // Error!
});
```

#### 链式调用

then 方法返回一个Promise 对象，其允许方法链。

你可以传递一个 lambda 给 then 并且如果它返回一个 promise，一个等价的 Promise 将暴露给后续的方法链。下面的代码片段使用 setTimout 函数来模拟异步代码操作。

```js
Promise.resolve("foo")
  // 1. 接收 "foo" 并与 "bar" 拼接，并将其结果做为下一个resolve返回。
  .then(function(string) {
    return new Promise(function(resolve, reject) {
      setTimeout(function() {
        string += 'bar';
        resolve(string);
      }, 1);
    });
  })
  // 2. 接收 "foobar", 放入一个异步函数中处理该字符串
  // 并将其打印到控制台中, 但是不将处理后的字符串返回到下一个。
  .then(function(string) {
    setTimeout(function() {
      string += 'baz';
      console.log(string);
    }, 1)
    return string;
  })
  // 3. 打印本节中代码将如何运行的帮助消息，
  // 字符串实际上是由上一个回调函数之前的那块异步代码处理的。
  .then(function(string) {
    console.log("Last Then:  oops... didn't bother to instantiate and return " +
                "a promise in the prior then so the sequence may be a bit " +
                "surprising");

    // 注意 `string` 这时不会存在 'baz'。
    // 因为这是发生在我们通过setTimeout模拟的异步函数中。
    console.log(string);
});
```

当一个值只是从一个 lambda 内部返回时，它将有效地返回 Promise.resolve（<由被调用的处理程序返回的值>）。

```js
var p2 = new Promise(function(resolve, reject) {
  resolve(1);
});

p2.then(function(value) {
  console.log(value); // 1
  return value + 1;
}).then(function(value) {
  console.log(value + "- This synchronous usage is virtually pointless"); // 2- This synchronous usage is virtually pointless
});

p2.then(function(value) {
  console.log(value); // 1
});
```

##### 如果函数抛出错误或返回一个拒绝的Promise，则调用将返回一个拒绝的Promise。

```js
Promise.resolve()
  .then( () => {
    // 使 .then() 返回一个 rejected promise
    throw 'Oh no!';
  })
  .then( () => {
    console.log( 'Not called.' );
  }, reason => {
    console.error( 'onRejected function called: ', reason );
});
```

在其他情况下，一个 resolving Promise 会被返回。在下面的例子里，第一个 then() 会返回一个用 resolving Promise 包装的 42，即使之前的 Promise 是 rejected 的。

```js
Promise.reject()
  .then( () => 99, () => 42 ) // onRejected returns 42 which is wrapped in a resolving Promise
  .then( solution => console.log( 'Resolved with ' + solution ) ); // Resolved with 42
```

实际上，捕获 rejected promise 的需求经常大于使用 then 的两种情况语法，比如下面这样的：

```js
Promise.resolve()
  .then( () => {
    // 使 .then() 返回一个 rejected promise
    throw 'Oh no!';
  })
  .catch( reason => {
    console.error( 'onRejected function called: ', reason );
  })
  .then( () => {
    console.log( "I am always called even if the prior then's promise rejects" );
  });
```

你也可以在另一个顶层函数上使用链式去实现带有 Promise-based API 的函数。

```js
function fetch_current_data() {
  // fetch() API 返回了一个 Promise.
  // 这个函数提供了类似的API，
  // 这个函数除了实现 Promise，它还能够完成更多的工作。
  return fetch('current-data.json').then((response) => {
    if (response.headers.get('content-type') != 'application/json') {
      throw new TypeError();
    }
    var j = response.json();
    // maybe do something with j
    return j; // fulfillment value given to user of
              // fetch_current_data().then()
  });
}
```

如果 `onFulfilled` 返回了一个 promise，`then` 的返回值就会被 Promise resolved或者rejected。

```js
function resolveLater(resolve, reject) {
  setTimeout(function () {
    resolve(10);
  }, 1000);
}
function rejectLater(resolve, reject) {
  setTimeout(function () {
    reject(20);
  }, 1000);
}

var p1 = Promise.resolve('foo');
var p2 = p1.then(function() {
  // Return promise here, that will be resolved to 10 after 1 second
  return new Promise(resolveLater);
});
p2.then(function(v) {
  console.log('resolved', v);  // "resolved", 10
}, function(e) {
  // not called
  console.log('rejected', e);
});

var p3 = p1.then(function() {
  // Return promise here, that will be rejected with 20 after 1 second
  return new Promise(rejectLater);
});
p3.then(function(v) {
  // not called
  console.log('resolved', v);
}, function(e) {
  console.log('rejected', e); // "rejected", 20
});
```

https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Promise/then





## Promise.resolve()

### 简介

new Promise的快捷方式

静态方法[`Promise.resolve(value)`](http://liubin.github.io/promises-book/#Promise.resolve) 可以认为是 `new Promise()` 方法的快捷方式。

比如 `Promise.resolve(42);` 可以认为是以下代码的语法糖。

```
new Promise(function(resolve){
    resolve(42);
});
```

在这段代码中的 `resolve(42);` 会让这个promise对象立即进入确定（即resolved）状态，并将 `42` 传递给后面then里所指定的 `onFulfilled` 函数。

方法 `Promise.resolve(value);` 的返回值也是一个promise对象，所以我们可以像下面那样接着对其返回值进行 `.then` 调用。

```
Promise.resolve(42).then(function(value){
    console.log(value);
});
```

[Promise.resolve](http://liubin.github.io/promises-book/#Promise.resolve)作为 `new Promise()` 的快捷方式，在进行promise对象的初始化或者编写测试代码的时候都非常方便。



https://www.kancloud.cn/kancloud/promises-book/44227





`**Promise.resolve(value)**`方法返回一个以给定值解析后的[`Promise`](https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Promise) 对象。但如果这个值是个thenable（即带有then方法），返回的promise会“跟随”这个thenable的对象，采用它的最终状态（指resolved/rejected/pending/settled）；如果传入的value本身就是promise对象，则该对象作为Promise.resolve方法的返回值返回；否则以该值为成功状态返回promise对象。

 

<iframe src="https://interactive-examples.mdn.mozilla.net/pages/js/promise-resolve.html" height="250" class="interactive interactive-js" frameborder="0" width="100%" style="font-style: normal !important; margin: 0px; padding: 10px; border: 1px solid rgb(234, 242, 244); max-width: 100%; box-sizing: border-box; background-color: rgb(245, 249, 250); color: rgb(51, 51, 51); height: 490px; width: 948.172px;"></iframe>

### 语法[节](https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Promise/resolve#Syntax)

```
Promise.resolve(value);
Promise.resolve(promise);
Promise.resolve(thenable);
```

#### 参数[节](https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Promise/resolve#%E5%8F%82%E6%95%B0)

- value

  将被`Promise`对象解析的参数。也可以是一个`Promise`对象，或者是一个thenable。

#### 返回值[节](https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Promise/resolve#%E8%BF%94%E5%9B%9E%E5%80%BC)

返回一个解析过带着给定值的`Promise`对象，如果返回值是一个promise对象，则直接返回这个Promise对象。

### 描述[节](https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Promise/resolve#Description)

静态方法 `Promise.resolve`返回一个解析过的`Promise`对象.



### 示例[节](https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Promise/resolve#%E7%A4%BA%E4%BE%8B)

#### 使用静态方法`Promise.resolve`[节](https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Promise/resolve#%E4%BD%BF%E7%94%A8%E9%9D%99%E6%80%81%E6%96%B9%E6%B3%95Promise.resolve)

```js
Promise.resolve("Success").then(function(value) {
  console.log(value); // "Success"
}, function(value) {
  // 不会被调用
});
```

#### 对一个数组进行resolve[节](https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Promise/resolve#%E5%AF%B9%E4%B8%80%E4%B8%AA%E6%95%B0%E7%BB%84%E8%BF%9B%E8%A1%8Cresolve)

```js
var p = Promise.resolve([1,2,3]);
p.then(function(v) {
  console.log(v[0]); // 1
});
```

#### Resolve另一个promise对象[节](https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Promise/resolve#Resolve%E5%8F%A6%E4%B8%80%E4%B8%AApromise%E5%AF%B9%E8%B1%A1)

```js
var original = Promise.resolve('我在第二行');
var cast = Promise.resolve(original);
cast.then(function(value) {
  console.log('value: ' + value);
});
console.log('original === cast ? ' + (original === cast));

/*
*  打印顺序如下，这里有一个同步异步先后执行的区别
*  original === cast ? true
*  value: 我在第二行
*/
```

#### resolve thenable的对象们并抛出错误[节](https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Promise/resolve#resolve_thenable%E7%9A%84%E5%AF%B9%E8%B1%A1%E4%BB%AC%E5%B9%B6%E6%8A%9B%E5%87%BA%E9%94%99%E8%AF%AF)

```js
// Resolve一个thenable对象
var p1 = Promise.resolve({ 
  then: function(onFulfill, onReject) { onFulfill("fulfilled!"); }
});
console.log(p1 instanceof Promise) // true, 这是一个Promise对象

p1.then(function(v) {
    console.log(v); // 输出"fulfilled!"
  }, function(e) {
    // 不会被调用
});

// Thenable在callback之前抛出异常
// Promise rejects
var thenable = { then: function(resolve) {
  throw new TypeError("Throwing");
  resolve("Resolving");
}};

var p2 = Promise.resolve(thenable);
p2.then(function(v) {
  // 不会被调用
}, function(e) {
  console.log(e); // TypeError: Throwing
});

// Thenable在callback之后抛出异常
// Promise resolves
var thenable = { then: function(resolve) {
  resolve("Resolving");
  throw new TypeError("Throwing");
}};

var p3 = Promise.resolve(thenable);
p3.then(function(v) {
  console.log(v); // 输出"Resolving"
}, function(e) {
  // 不会被调用
});
```

## Promise.resolve









一般情况下我们都会使用 `new Promise()` 来创建promise对象，但是除此之外我们也可以使用其他方法。

在这里，我们将会学习如何使用 [`Promise.resolve`](http://liubin.github.io/promises-book/#Promise.resolve) 和 [`Promise.reject`](http://liubin.github.io/promises-book/#Promise.reject)这两个方法。



## 并行执行异步任务

除了串行执行若干异步任务外，Promise还可以并行执行异步任务。

试想一个页面聊天系统，我们需要从两个不同的URL分别获得用户的个人信息和好友列表，这两个任务是可以并行执行的，用`Promise.all()`实现如下：

```js
var p1 = new Promise(function (resolve, reject) {
    setTimeout(resolve, 500, 'P1');
});
var p2 = new Promise(function (resolve, reject) {
    setTimeout(resolve, 600, 'P2');
});
// 同时执行p1和p2，并在它们都完成后执行then:
Promise.all([p1, p2]).then(function (results) {
    console.log(results); // 获得一个Array: ['P1', 'P2']
});
```

有些时候，多个异步任务是为了容错。比如，同时向两个URL读取用户的个人信息，只需要获得先返回的结果即可。这种情况下，用`Promise.race()`实现：

```js
var p1 = new Promise(function (resolve, reject) {
    setTimeout(resolve, 500, 'P1');
});
var p2 = new Promise(function (resolve, reject) {
    setTimeout(resolve, 600, 'P2');
});
Promise.race([p1, p2]).then(function (result) {
    console.log(result); // 'P1'
});
```

由于`p1`执行较快，Promise的`then()`将获得结果`'P1'`。`p2`仍在继续执行，但执行结果将被丢弃。

如果我们组合使用Promise，就可以把很多异步任务以并行和串行的方式组合起来执行。

## 实际使用例子

### 封装

```Js
/**
 * Description: 校验sellerId是否有效
 * Create on: 2018/10/27 下午3:45
 *
 * @param reject sellerId有效则执行resolve
 * @param reject sellerId无效则执行reject
 * @author xiangyu.ye
 */
checkSellerIdIsValid (resolve, reject) {
  let that = this
  return new Promise(function (resolve, reject) {
    let userInfo = wx.getStorageSync('userInfo')
    let postData = { sellerId: userInfo.userNo }
    that.$http.post(api.ShopBIListSeller,
      postData)
      .then((res) => {
        if (res.data.code === '200') {
          let sellers = res.data.data.sellers
          if (sellers != null) {
            that.sellerIdIsValid = true
            resolve()
          } else {
            that.sellerIdIsValid = false
            reject(res)
          }
        } else {
          wx.showToast({
            title: res.data.msg,
            duration: 2000,
            icon: 'none'
          })
          reject(res)
        }
      }).catch(err => {
        console.log(err)
        reject(err)
      })
  })
},
```

### 使用

```js
this.checkSellerIdIsValid().then(() => {
  let userInfo = wx.getStorageSync('userInfo')
  this.filterData.sellerId = userInfo.userNo
  this.getShopList(this.filterData)
}).catch((res) => {
  console.error(res)
  this.getShopList(this.filterData)
})
```





https://www.liaoxuefeng.com/wiki/001434446689867b27157e896e74d51a89c25cc8b43bdb3000/0014345008539155e93fc16046d4bb7854943814c4f9dc2000