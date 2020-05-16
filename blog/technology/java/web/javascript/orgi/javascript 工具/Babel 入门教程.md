[TOC]



# Babel 入门教程

作者： [阮一峰](http://www.ruanyifeng.com/)

日期： [2016年1月25日](http://www.ruanyifeng.com/blog/2016/01/)

（说明：本文选自我的新书[《ES6 标准入门（第二版）》](http://www.ruanyifeng.com/blog/2016/01/ecmascript-6-primer.html)的第一章[《ECMAScript 6简介》](http://es6.ruanyifeng.com/#docs/intro)）

[Babel](https://babeljs.io/)是一个广泛使用的转码器，可以将ES6代码转为ES5代码，从而在现有环境执行。

这意味着，你可以现在就用 ES6 编写程序，而不用担心现有环境是否支持。下面是一个例子。

> ```javascript
> // 转码前
> input.map(item => item + 1);
> 
> // 转码后
> input.map(function (item) {
>   return item + 1;
> });
> ```



```
// 转码前
let fun = () => console.log('babel')
fun()

// 转码后
var fun = function fun() {
  return console.log('babel');
};
fun();
```

上面的原始代码用了箭头函数，这个特性还没有得到广泛支持，Babel将其转为普通函数，就能在现有的JavaScript环境执行了。

## 一、配置文件`.babelrc`

Babel的配置文件是`.babelrc`，存放在项目的根目录下。使用Babel的第一步，就是配置这个文件。

该文件用来设置转码规则和插件，基本格式如下。

> ```javascript
> {
>   "presets": [],
>   "plugins": []
> }
> ```

`presets`字段设定转码规则，官方提供以下的规则集，你可以根据需要安装。

> ```bash
> # ES2015转码规则
> $ npm install --save-dev babel-preset-es2015
> 
> # react转码规则
> $ npm install --save-dev babel-preset-react
> 
> # ES7不同阶段语法提案的转码规则（共有4个阶段），选装一个
> $ npm install --save-dev babel-preset-stage-0
> $ npm install --save-dev babel-preset-stage-1
> $ npm install --save-dev babel-preset-stage-2
> $ npm install --save-dev babel-preset-stage-3
> ```

然后，将这些规则加入`.babelrc`。

> ```javascript
>   {
>     "presets": [
>       "es2015",
>       "react",
>       "stage-2"
>     ],
>     "plugins": []
>   }
> ```

注意，以下所有Babel工具和模块的使用，都必须先写好`.babelrc`。

## 二、命令行转码`babel-cli`

Babel提供`babel-cli`工具，用于命令行转码。

它的安装命令如下。

> ```bash
> $ npm install --global babel-cli
> ```

基本用法如下。

> ```bash
> # 转码结果输出到标准输出
> $ babel example.js
> 
> # 转码结果写入一个文件
> # --out-file 或 -o 参数指定输出文件
> $ babel example.js --out-file compiled.js
> # 或者
> $ babel example.js -o compiled.js
> 
> # 整个目录转码
> # --out-dir 或 -d 参数指定输出目录
> $ babel src --out-dir lib
> # 或者
> $ babel src -d lib
> 
> # -s 参数生成source map文件
> $ babel src -d lib -s
> ```

上面代码是在全局环境下，进行Babel转码。这意味着，如果项目要运行，全局环境必须有Babel，也就是说项目产生了对环境的依赖。另一方面，这样做也无法支持不同项目使用不同版本的Babel。

一个解决办法是将`babel-cli`安装在项目之中。

> ```bash
> # 安装
> $ npm install --save-dev babel-cli
> ```

然后，改写`package.json`。

> ```javascript
> {
>   // ...
>   "devDependencies": {
>     "babel-cli": "^6.0.0"
>   },
>   "scripts": {
>     "build": "babel src -d lib"
>   },
> }
> ```

转码的时候，就执行下面的命令。

> ```javascript
> $ npm run build
> ```

## 三、babel-node

`babel-cli`工具自带一个`babel-node`命令，提供一个支持ES6的REPL环境。它支持Node的REPL环境的所有功能，而且可以直接运行ES6代码。

它不用单独安装，而是随`babel-cli`一起安装。然后，执行`babel-node`就进入PEPL环境。

> ```bash
> $ babel-node
> > (x => x * 2)(1)
> 2
> ```

`babel-node`命令可以直接运行ES6脚本。将上面的代码放入脚本文件`es6.js`，然后直接运行。

> ```bash
> $ babel-node es6.js
> 2
> ```

`babel-node`也可以安装在项目中。

> ```bash
> $ npm install --save-dev babel-cli
> ```

然后，改写`package.json`。

> ```javascript
> {
>   "scripts": {
>     "script-name": "babel-node script.js"
>   }
> }
> ```

上面代码中，使用`babel-node`替代`node`，这样`script.js`本身就不用做任何转码处理。

## 四、babel-register

`babel-register`模块改写`require`命令，为它加上一个钩子。此后，每当使用`require`加载`.js`、`.jsx`、`.es`和`.es6`后缀名的文件，就会先用Babel进行转码。

> ```bash
> $ npm install --save-dev babel-register
> ```

使用时，必须首先加载`babel-register`。

> ```bash
> require("babel-register");
> require("./index.js");
> ```

然后，就不需要手动对`index.js`转码了。

需要注意的是，`babel-register`只会对`require`命令加载的文件转码，而不会对当前文件转码。另外，由于它是实时转码，所以只适合在开发环境使用。

## 五、babel-core

如果某些代码需要调用Babel的API进行转码，就要使用`babel-core`模块。

安装命令如下。

> ```bash
> $ npm install babel-core --save
> ```

然后，在项目中就可以调用`babel-core`。

> ```javascript
> var babel = require('babel-core');
> 
> // 字符串转码
> babel.transform('code();', options);
> // => { code, map, ast }
> 
> // 文件转码（异步）
> babel.transformFile('filename.js', options, function(err, result) {
>   result; // => { code, map, ast }
> });
> 
> // 文件转码（同步）
> babel.transformFileSync('filename.js', options);
> // => { code, map, ast }
> 
> // Babel AST转码
> babel.transformFromAst(ast, code, options);
> // => { code, map, ast }
> ```

配置对象`options`，可以参看官方文档<http://babeljs.io/docs/usage/options/>。

下面是一个例子。

> ```javascript
> var es6Code = 'let x = n => n + 1';
> var es5Code = require('babel-core')
>   .transform(es6Code, {
>     presets: ['es2015']
>   })
>   .code;
> // '"use strict";\n\nvar x = function x(n) {\n  return n + 1;\n};'
> ```

上面代码中，`transform`方法的第一个参数是一个字符串，表示需要转换的ES6代码，第二个参数是转换的配置对象。

## 六、babel-polyfill

Babel默认只转换新的JavaScript句法（syntax），而不转换新的API，比如Iterator、Generator、Set、Maps、Proxy、Reflect、Symbol、Promise等全局对象，以及一些定义在全局对象上的方法（比如`Object.assign`）都不会转码。

举例来说，ES6在`Array`对象上新增了`Array.from`方法。Babel就不会转码这个方法。如果想让这个方法运行，必须使用`babel-polyfill`，为当前环境提供一个垫片。

安装命令如下。

> ```bash
> $ npm install --save babel-polyfill
> ```

然后，在脚本头部，加入如下一行代码。

> ```javascript
> import 'babel-polyfill';
> // 或者
> require('babel-polyfill');
> ```

Babel默认不转码的API非常多，详细清单可以查看`babel-plugin-transform-runtime`模块的[definitions.js](https://github.com/babel/babel/blob/master/packages/babel-plugin-transform-runtime/src/definitions.js)文件。

## 七、浏览器环境

Babel也可以用于浏览器环境。但是，从Babel 6.0开始，不再直接提供浏览器版本，而是要用构建工具构建出来。如果你没有或不想使用构建工具，可以通过安装5.x版本的`babel-core`模块获取。

> ```bash
> $ npm install babel-core@old
> ```

运行上面的命令以后，就可以在当前目录的`node_modules/babel-core/`子目录里面，找到`babel`的浏览器版本`browser.js`（未精简）和`browser.min.js`（已精简）。

然后，将下面的代码插入网页。

> ```markup
> <script src="node_modules/babel-core/browser.js"></script>
> <script type="text/babel">
> // Your ES6 code
> </script>
> ```

上面代码中，`browser.js`是Babel提供的转换器脚本，可以在浏览器运行。用户的ES6脚本放在`script`标签之中，但是要注明`type="text/babel"`。

另一种方法是使用[babel-standalone](https://github.com/Daniel15/babel-standalone)模块提供的浏览器版本，将其插入网页。

> ```markup
> <script src="https://cdnjs.cloudflare.com/ajax/libs/babel-standalone/6.4.4/babel.min.js"></script>
> <script type="text/babel">
> // Your ES6 code
> </script>
> ```

注意，网页中实时将ES6代码转为ES5，对性能会有影响。生产环境需要加载已经转码完成的脚本。

下面是如何将代码打包成浏览器可以使用的脚本，以`Babel`配合`Browserify`为例。首先，安装`babelify`模块。

> ```bash
> $ npm install --save-dev babelify babel-preset-es2015
> ```

然后，再用命令行转换ES6脚本。

> ```bash
> $  browserify script.js -o bundle.js \
>   -t [ babelify --presets [ es2015 react ] ]
> ```

上面代码将ES6脚本`script.js`，转为`bundle.js`，浏览器直接加载后者就可以了。

在`package.json`设置下面的代码，就不用每次命令行都输入参数了。

> ```javascript
> {
>   "browserify": {
>     "transform": [["babelify", { "presets": ["es2015"] }]]
>   }
> }
> ```

## 八、在线转换

Babel提供一个[REPL在线编译器](https://babeljs.io/repl/)，可以在线将ES6代码转为ES5代码。转换后的代码，可以直接作为ES5代码插入网页运行。

## 九、与其他工具的配合

许多工具需要Babel进行前置转码，这里举两个例子：ESLint和Mocha。

[ESLint](http://eslint.org/) 用于静态检查代码的语法和风格，安装命令如下。

> ```bash
> $ npm install --save-dev eslint babel-eslint
> ```

然后，在项目根目录下，新建一个配置文件`.eslint`，在其中加入`parser`字段。

> ```javascript
> {
>   "parser": "babel-eslint",
>   "rules": {
>     ...
>   }
> }
> ```

再在`package.json`之中，加入相应的`scripts`脚本。

> ```javascript
>   {
>     "name": "my-module",
>     "scripts": {
>       "lint": "eslint my-files.js"
>     },
>     "devDependencies": {
>       "babel-eslint": "...",
>       "eslint": "..."
>     }
>   }
> ```

[Mocha](http://www.ruanyifeng.com/blog/2015/12/a-mocha-tutorial-of-examples.html) 则是一个测试框架，如果需要执行使用ES6语法的测试脚本，可以修改`package.json`的`scripts.test`。

> ```javascript
> "scripts": {
>   "test": "mocha --ui qunit --compilers js:babel-core/register"
> }
> ```

上面命令中，`--compilers`参数指定脚本的转码器，规定后缀名为`js`的文件，都需要使用`babel-core/register`先转码。

（完）

### 文档信息

- 版权声明：自由转载-非商用-非衍生-保持署名（[创意共享3.0许可证](http://creativecommons.org/licenses/by-nc-nd/3.0/deed.zh)）
- 发表日期： 2016年1月25日
- 更多内容： [档案](http://www.ruanyifeng.com/blog/archives.html) » [JavaScript](http://www.ruanyifeng.com/blog/javascript/)
- 文集：[《前方的路》](http://www.ruanyifeng.com/road)，[《未来世界的幸存者》](http://www.ruanyifeng.com/survivor)
- 社交媒体：[![img](data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABQAAAAUCAYAAACNiR0NAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAk1JREFUeNqsVc9rE0EU/mZ3dhPNJl2bVIk/Km09aFBE8GLvnrwJXvwLxIsgCP4Fnit6E8Gb0IMHwUsL9SSNgiAoooJIpE1Joo1h82uT7IxvxmRN3Q32kAdvdrI773vfe+/bDTu9WioDcDAda3Ja8piepQ1avCkCeny0Y+R9KenKwOmHHLvPaKn7AtVOoO8dOWDCMpg+41gMQv5FNEJACgoE8MsPNLDBhgfoutMOsJDmWLmU037C4TApciljwQ/kHoohQxV06+wMXNvE7WINuaSpvdYVKLgWnl3O4zAxU3Z1IYVGT+B5qYWV9z2kbQaT/cOwTsxOUUYF+mB5Dkmi8HG3h2q9hxsFNwRTdvQgxxnXRrMvIdkEhllis/qtiWuLDm4WZnBlPoWnXz28LHdw0uGR7m9Wu3j0qYGEubfn4UlBfXO4gTZltRNMg9w9f0i7kNFxllsBlS0x75gYfxyWTBUjT2W5CSMSPBrQuG21+hiMDS8CeCzF8eSLp13uQ3Ab1IqMFZN8tFHPmgOBF99bYP8Be1Xp6t7OJszJgKpPSiZvaj7uf2hguz2IBQuozDvFH6RDBjuK9wdQDvt0nMpW+8efGyh5UcAeZb2+UcHbnz5Jx4wdlp4yJYU3kLiQtfBwOYeLc8nIwfXtNu69q2Oz4mMxbemYOOOjKarhvq51KUjg3GwiFPIuvcNF6pnSnWK0lOEabNLgQh1aJFAhlGB9rG110B2+oyRNPc0sZbRNFltmLKDKqj4Qrm1ojzOxDz2pyPQ0P7CK4c40/wJ+CzAAGYrXsvfFXR4AAAAASUVORK5CYII=) twitter](https://twitter.com/ruanyf)，[![img](data:image/png;base64,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) weibo](http://weibo.com/ruanyf)

[![QCon](https://www.wangbase.com/blogimg/asset/201809/bg2018091702.jpg)](https://2018.qconshanghai.com/?utm_source=ryf&utm_campaign=9&utm_medium=banner&utm_term=0815)

[![腾讯课堂](https://www.wangbase.com/blogimg/asset/201806/bg2018062701.jpg)](https://ke.qq.com/next/index.html?from=175763)



## 相关文章

- 2018.07.08: [Web Worker 使用教程](http://www.ruanyifeng.com/blog/2018/07/web-worker.html)

  一、概述

- 2018.07.04: [浏览器数据库 IndexedDB 入门教程](http://www.ruanyifeng.com/blog/2018/07/indexeddb.html)

  一、概述

- 2018.06.18: [JavaScript 的 this 原理](http://www.ruanyifeng.com/blog/2018/06/javascript-this.html)

  一、问题的由来 学懂 JavaScript 语言，一个标志就是理解下面两种写法，可能有不一样的结果。

- 2018.03.20: [Node 调试工具入门教程](http://www.ruanyifeng.com/blog/2018/03/node-debugger.html)

  JavaScript 程序越来越复杂，调试工具的重要性日益凸显。客户端脚本有浏览器，Node 脚本怎么调试呢？





http://www.ruanyifeng.com/blog/2016/01/babel.html