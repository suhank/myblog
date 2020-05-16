[TOC]



# javascript eval 函数 最好不要用



`eval()` 函数会将传入的字符串当做 JavaScript 代码进行执行。

 

## 语法

```
eval(string)
```

### 参数

- `string`

  表示JavaScript表达式，语句或一系列语句的字符串。表达式可以包含变量以及已存在对象的属性。

### 返回值

执行指定代码之后的返回值。如果返回值为空，返回[`undefined`](https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/undefined)

## 描述

`eval()` 是全局对象的一个函数属性。

`eval()` 的参数是一个字符串。如果字符串表示的是表达式，`eval()` 会对表达式进行求值。如果参数表示一个或多个 JavaScript 语句， 那么 `eval()` 就会执行这些语句。注意不要用 `eval()` 来执行一个算术表达式；因为 JavaScript 可以自动为算术表达式求值。

如果你以字符串的形式构造算术表达式，则可以用 `eval()` 在随后对其求值。例如，假设你有一个变量 `x` ，您可以通过将表达式的字符串值（例如 `3 * x + 2` ）赋值给一个变量，然后在你的代码后面的其他地方调用 `eval()` ，来推迟涉及 `x` 的表达式的求值。

如果 `eval()` 的参数不是字符串， `eval()` 将会将参数原封不动的返回。在下面的例子中，`String` 构造器被指定， 而 `eval()` 返回了 `String` 对象而不是执行字符串。

```
eval(new String("2 + 2")); // 返回了包含"2 + 2"的字符串对象
eval("2 + 2");             // returns 4
```

你可以使用通用的的方法来绕过这个限制，如使用`toString()`

```
var expression = new String("2 + 2");
eval(expression.toString());
```

如果你间接的使用 eval()，比如通过一个引用来调用它，而不是直接的调用 `eval` 。 从 [ECMAScript 5](http://www.ecma-international.org/ecma-262/5.1/#sec-10.4.2) 起，它工作在全局作用域下，而不是局部作用域中。这就意味着，例如，下面的代码的作用声明创建一个全局函数，并且geval中的这些代码在执行期间不能在被调用的作用域中访问局部变量。

```
function test() {
  var x = 2, y = 4;
  console.log(eval("x + y"));  // 直接调用，使用本地作用域，结果是 6
  var geval = eval; // 等价于在全局作用域调用
  console.log(geval("x + y")); // 间接调用，使用全局作用域，throws ReferenceError 因为`x`未定义
  (0, eval)('x + y'); // 另一间接调用的例子
}
```

## 避免在不必要的情况下使用 `eval`

`eval()` 是一个危险的函数， 他执行的代码拥有着执行者的权利。如果你用 `eval()` 运行的字符串代码被恶意方（不怀好意的人）操控修改，您最终可能会在您的网页/扩展程序的权限下，在用户计算机上运行恶意代码。更重要的是，第三方代码可以看到某一个eval()被调用时的作用域，这也有可能导致一些不同方式的攻击。相似的 [`Function`](https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/Function) 就不容易被攻击。

`eval()` 通常比替代方法慢，因为它必须调用 JS 解释器，而许多其他结构则由现代 JS 引擎进行优化。

在常见的案例中我们都会找更安全或者更快的方案去替换 `eval()`

### 访问成员属性

你不应该去使用 `eval()` 来将属性名字转化为属性。考虑下面的这个例子，被访问对象的属性在它被执行之前都会未知的。这里可以用 eval 处理：

```
var obj = { a: 20, b: 30 };
var propName = getPropName(); // 返回 "a" 或 "b"

eval( 'var result = obj.' + propsName )
```

但是，这里并不是必须得使用 `eval()` 。事实上，这里并不建议这样使用。可以使用 [属性访问器](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Operators/Property_Accessors) 进行代替，它更快而且更安全：

```
var obj = { a: 20, b: 30 }
var propName = getPropName(); // 返回 "a" 或 "b"
var result = obj[ propName ]; //  obj[ "a" ] 与 obj.a 等价
```

你还可以使用这个方法去访问子代的属性。如下：

```
var obj = {a: {b: {c: 0}}};
var propPath = getPropPath(); // 例如返回 "a.b.c"

eval( 'var result = obj.' + propPath )
```

在这里避免 `eval()` 可以通过分割属性路径和循环遍历不同的属性来完成：

```
function getDescendantantProp(obj, desc) {
    var arr = desc.split('.');
    while(arr.length) {
        obj = obj[arr.shift()];
    }
    return obj;
}

var obj = {a: {b: {c: 0}}};
var propPath = getPropPath(); // 例如返回 "a.b.c"
var result = getDescendantantProp(obj, propPath);
```

同样的方法也可实现设置子代的属性值:

```
function setDescendantProp(obj, desc, value) {
  var arr = desc.split('.');
  while (arr.length > 1) {
    obj = obj[arr.shift()];
  }
  obj[arr[0]] = value;
}

var obj = {a: {b: {c: 0}}};
var propPath = getPropPath();  // 例如返回 "a.b.c"
var result = setDescendantProp(obj, propPath, 1);  // test.a.b.c 值为 1
```

### 使用函数而非代码段

JavaScript拥有 [first-class functions](https://developer.mozilla.org/zh-CN/docs/Glossary/First-class_Function)，这意味着你可以将函数直接作为参数传递给其他接口，将他们保存在变量中或者对象的属性中，等等。很多DOM的API都用这种思路进行设计，你也可以（或者应该）这样子设计你的代码：

```
// 代替 setTimeout(" ... ", 1000) 写法:
setTimeout(function() { ... }, 1000); 

// 代替 elt.setAttribute("onclick", "...") 写法:
elt.addEventListener('click', function() { ... } , false);
```

[闭包](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Closures) 也有助于创建参数化函数而不用连接字符串。

### 解析 JSON（将字符串转化为JavaScript对象）

如果你在调用 `eval()` 传入的字符串参数中包含数据（如：一个数组“[1,2,3]”）而不是代码，你应该考虑将其转换为 [JSON](https://developer.mozilla.org/en-US/docs/Glossary/JSON) 对象，这允许你用JavaScript语法的子集来表示数据。[在扩展中下载JSON和JavaScript](https://developer.mozilla.org/zh-CN/docs/Downloading_JSON_and_JavaScript_in_extensions)

提示：因为 JSON 语法子集相对于 JavaScript 语法子集比较有局限性，很多在 JavaScript 中可用的特性在 JSON 中就不起作用了。比如，后缀逗号在 JSON 中不支持，并且对象中的属性名在 JSON 中必须用引号括起来。请务必使用 JSON 序列化方法来生成稍后将被解析为 JSON 的字符串。

### 尽量传递数据而非代码

例如，设计为抓取网页内容的扩展，可能会在XPath中定义抓取规则，而不是在 JavaScript 代码中。

### 以有限权限运行代码

如果你必须执行这段代码, 应考虑以更低的权限运行。此建议主要适用于扩展和XUL应用程序，可以使用[Components.utils.evalInSandbox](https://developer.mozilla.org/en-US/docs/Components.utils.evalInSandbox) 。

## 例子

### 使用 `eval`

在下面的代码中，两种包含了 `eval()` 的声明都返回了42。第一种是对字符串 "`x + y + 1`" 求值；第二种是对字符串 "`42`"求值。

```
var x = 2;
var y = 39;
var z = "42";
eval("x + y + 1"); // returns 42
eval(z);           // returns 42
```

### 使用 `eval` 执行一串 JavaScript 语句

下面的例子使用 `eval()` 来执行 `str` 字符串。这个字符串包含了如果 `x` 等于5，就打开一个Alert 对话框并对 `z` 赋值 42，否则就对 `z` 赋值 0 的 JavaScript 语句。 当第二个声明被执行，`eval()` 将会令字符串被执行，并最终返回赋值给 `z` 的 42。

```
var x = 5;
var str = "if (x == 5) {alert('z is 42'); z = 42;} else z = 0; ";
console.log('z is ', eval(str));
```

如果您定义了多个值，则会返回最后一个值。

```
var x = 5;
var str = "if (x == 5) {console.log('z is 42'); z = 42; x = 420; } else z = 0;"; 

console.log('x is ', eval(str)); // z is 42  x is 420
```

### 返回值

`eval` 返回最后一个表达式的值。

```
var str = "if ( a ) { 1+1; } else { 1+2; }";
var a = true;
var b = eval(str);  // returns 2 

console.log('b is : ' + b);

a = false;
b = eval(str);  // returns 3

console.log('b is : ' + b);
```

### `eval` 中函数作为字符串被定义需要“（”和“）”作为前缀和后缀

```
var fctStr1 = 'function a() {}'
var fctStr2 = '(function a() {})'
var fct1 = eval(fctStr1)  // return undefined
var fct2 = eval(fctStr2)  // return a function
```

## 规范

| Specification                                                | Status   | Comment             |
| ------------------------------------------------------------ | -------- | ------------------- |
| [ECMAScript 1st Edition (ECMA-262)](https://www.ecma-international.org/publications/files/ECMA-ST-ARCH/ECMA-262,%201st%20edition,%20June%201997.pdf) | Standard | Initial definition. |
| [ECMAScript 5.1 (ECMA-262)eval](https://www.ecma-international.org/ecma-262/5.1/#sec-15.1.2.1) | Standard |                     |
| [ECMAScript 2015 (6th Edition, ECMA-262)eval](https://www.ecma-international.org/ecma-262/6.0/#sec-eval-x) | Standard |                     |
| [ECMAScript Latest Draft (ECMA-262)eval](https://tc39.github.io/ecma262/#sec-eval-x) | Draft    |                     |

## 浏览器兼容性

[新的兼容性表格正在测试中 ](https://developer.mozilla.org/docs/New_Compatibility_Tables_Beta)

|               | Desktop         | Mobile          | Server        |                   |                 |                 |                 |                    |                 |                     |                   |                 |                  |                 |
| ------------- | --------------- | --------------- | ------------- | ----------------- | --------------- | --------------- | --------------- | ------------------ | --------------- | ------------------- | ----------------- | --------------- | ---------------- | --------------- |
|               | Chrome          | Edge            | Firefox       | Internet Explorer | Opera           | Safari          | Android webview | Chrome for Android | Edge Mobile     | Firefox for Android | Opera for Android | iOS Safari      | Samsung Internet | Node.js         |
| Basic support | Full supportYes | Full supportYes | Full support1 | Full supportYes   | Full supportYes | Full supportYes | Full supportYes | Full supportYes    | Full supportYes | Full support4       | Full supportYes   | Full supportYes | Full supportYes  | Full supportYes |

### Legend

- Full support 

  Full support

## 火狐相关

- 从历史上看，`eval()` 有一个可选的第二个参数，指定上下文执行对象。 这个参数是非标准的，并且明确地从 Firefox 4 中删除。请参阅 [bug 531675](https://bugzilla.mozilla.org/show_bug.cgi?id=531675) 。

## 其他内容

- [`uneval()`](https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/uneval)
- [Property accessors](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Operators/Property_Accessors)
- [WebExtensions: Using eval in content scripts](https://developer.mozilla.org/en-US/Add-ons/WebExtensions/Content_scripts#Using_eval()_in_content_scripts)

## 文档标签和贡献者

 

标签：

 

- [eval](https://developer.mozilla.org/zh-CN/docs/tag/eval)

-  

- [JavaScript](https://developer.mozilla.org/zh-CN/docs/tag/JavaScript)

 **此页面的贡献者：** [Akiq2016](https://developer.mozilla.org/zh-CN/profiles/Akiq2016), [extending](https://developer.mozilla.org/zh-CN/profiles/extending), [icepro](https://developer.mozilla.org/zh-CN/profiles/icepro), [eeeeeeeason](https://developer.mozilla.org/zh-CN/profiles/eeeeeeeason), [JX-Zhuang](https://developer.mozilla.org/zh-CN/profiles/JX-Zhuang), [yanpengxiang](https://developer.mozilla.org/zh-CN/profiles/yanpengxiang), [SiberianMark](https://developer.mozilla.org/zh-CN/profiles/SiberianMark), [Jiang-Xuan](https://developer.mozilla.org/zh-CN/profiles/Jiang-Xuan), [Hugh](https://developer.mozilla.org/zh-CN/profiles/Hugh), [Binly42](https://developer.mozilla.org/zh-CN/profiles/Binly42), [ziyunfei](https://developer.mozilla.org/zh-CN/profiles/ziyunfei), [fscholz](https://developer.mozilla.org/zh-CN/profiles/fscholz), [qianjiahao](https://developer.mozilla.org/zh-CN/profiles/qianjiahao),[teoli](https://developer.mozilla.org/zh-CN/profiles/teoli), [huguowei](https://developer.mozilla.org/zh-CN/profiles/huguowei), [Mgjbot](https://developer.mozilla.org/zh-CN/profiles/Mgjbot), [Laser](https://developer.mozilla.org/zh-CN/profiles/Laser)

 **最后编辑者:** [Akiq2016](https://developer.mozilla.org/zh-CN/profiles/Akiq2016), Feb 10, 2018, 5:26:09 AM





https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Global_Objects/eval