[TOC]



# EsLint入门学习整理

  	这两天因为公司要求，就对ESLint进行了初步的了解，网上的内容基本上都差不多，但是内容有些乱，我这呢，就跟着大部分的文章，以及官方文档整理出了一篇入门学习的文字，技术点不算特别全，但是对于新手的我来说是够的，文章的篇幅很长，内容可能有些累赘，但是按着步骤一步一步来，基本上看完也就可以对ESLint有个初步的了解了，此外一些相关文档网上还是有很多的，一些单个比较重要的技术点，网上也都会有相关的文章做了详细的描述。

​	 我这篇文章针对的就是对eslint完全不了解的小伙伴们。比如像我就是一窍不通的，最近算是转型吧，写了一段时间的移动端，技术没咋练好，就被要求做前端的东西，脑子里也是一团浆糊，公司又要求研究些东西，刚接触的时候完全懵逼，现在慢慢地有点喜欢上前端了，最近也在开始补习web基础，发现需要学习的东西好多啊。希望大家要是有兴趣的话可以跟我做个朋友，一起讨论讨论技术的事情，互相学习一下下。

## 介绍

​    **ESLint** 是由 **Nicholas C. Zakas** 编写的一个可扩展、每条规则独立、不内置编码风格为理念的 **Lint** 工具。

​    在团队协作中，为避免低级 Bug、产出风格统一的代码，会预先制定编码规范。使用 Lint 工具和代码风格检测工具，则可以辅助编码规范执行，有效控制代码质量。EsLint帮助我们检查**Javascript**编程时的语法错误。比如：在**Javascript**应用中，你很难找到你漏泄的变量或者方法。**EsLint**能够帮助我们分析JS代码，找到bug并确保一定程度的JS语法书写的正确性。

​    **EsLint**是建立在**Esprima**(**ECMAScript**解析架构)的基础上的。**Esprima**支持ES5.1,本身也是用**ECMAScript**编写的，用于多用途分析。**EsLint**不但提供一些默认的规则（可扩展），也提供用户自定义规则来约束我们写的**Javascript**代码。

​    ESLint是确定和报告模式的工具中发现**ECMAScript** / **JavaScript**代码,使代码更一致的目标和避免错误。在许多方面,它类似于**JSLint**和**JSHint**，但是也有部分不同。

特定：

- ESLint使用Espree JavaScript解析。
- ESLint使用AST评估模式的代码。
- ESLint完全可插入式的,每一个规则是一个插件,支持插件扩展、自定义规则。
- 默认规则包含所有 JSLint、JSHint 中存在的规则，易迁移；
- 规则可配置性高：可设置「警告」、「错误」两个 error 等级，或者直接禁用；
- 包含代码风格检测的规则（可以丢掉 JSCS 了）；

EsLint提供以下支持：

- ES6
- AngularJS
- JSX
- Style检查
- 自定义错误和提示

EsLint提供以下几种校验：

- 语法错误校验
- 不重要或丢失的标点符号，如分号
- 没法运行到的代码块（使用过WebStorm的童鞋应该了解）
- 未被使用的参数提醒
- 漏掉的结束符，如}
- 确保样式的统一规则，如sass或者less
- 检查变量的命名

## 使用

​    有两种方法来安装**ESLint**:全局安装和本地安装。

#### 1.本地安装

如果你想包括ESLint作为你的项目构建系统的一部分,我们建议在本地安装。你可以使用npm:

```
$ npm install eslint --save-dev
```

你应该设置一个配置文件:

```
$ ./node_modules/.bin/eslint --init
```

之后,您可以运行ESLint在任何文件或目录如下:

```
$ ./node_modules/.bin/eslint yourfile.js
```

yourfile.js是你需要测试的js文件。你使用的任何插件或共享配置必须安装在本地来与安装在本地的ESLint一起工作。

#### 2.全局安装

如果你想让ESLint可用到所有的项目,我们建议安装ESLint全局安装。你可以使用npm:

```
$ npm install -g eslint
```

你应该设置一个配置文件:

```
$ eslint --init
```

之后,您可以在任何文件或目录运行ESLint:

```
$ eslint yourfile.js
```

##### PS：eslint --init是用于每一个项目设置和配置eslint,并将执行本地安装的ESLint及其插件的目录。如果你喜欢使用全局安装的ESLint，在你配置中使用的任何插件都必须是全局安装的。

#### 3.使用

- 新建一个项目：



  ![img](https://upload-images.jianshu.io/upload_images/1062695-c187438b213fec23.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1000/format/webp)

  创建项目

- 创建package.json文件

  ```
    $ npm init
  ```



  ![img](https://upload-images.jianshu.io/upload_images/1062695-2400e7ee232eff77.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1000/format/webp)

  npm init

- 安装ESLint

  ```
    $ npm install -g eslint
  ```



  ![img](https://upload-images.jianshu.io/upload_images/1062695-6fa26dffea88fa40.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1000/format/webp)

  npm install -g eslint

- 创建和编写简单的js文件
  ​    创建index.js文件，里面写一个函数，就直接用别人写的一个简单的函数用用吧。

  ```
    function merge () {
    var ret = {};
    for (var i in arguments) {
        var m = arguments[i];
        for (var j in m) ret[j] = m[j];
    }
    return ret;
    }
  
    console.log(merge({a: 123}, {b: 456}));
  ```

- 执行`node index.js`，输出结果为{ a: 123, b: 456 }

  ```
     appledeMacBook-Pro:testEslint apple$ node index.js
    { a: 123, b: 456 }
  ```

- 使用eslint检查

  ```
    eslint index.js
  ```



  ![img](https://upload-images.jianshu.io/upload_images/1062695-35360d2dd0822ea2.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1000/format/webp)

  eslint index.js

​    执行结果是失败，因为没有找到相应的配置文件，个人认为这个eslint最重要的就是配置问题。

- 新建配置文件

  ```
    $ eslint --init
  ```

  ​    不过这个生成的额文件里面已经有一些配置了，把里面的内容大部分删除。留下个extends，剩下的自己填就可以了

  ```
    module.exports = {
        "extends": "eslint:recommended"
    };
  ```

  ​    eslint:recommended配置，它包含了一系列核心规则，能报告一些常见的问题。

- 重新执行eslint index.js





  ![img](https://upload-images.jianshu.io/upload_images/1062695-826297c1dbc87c93.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/938/format/webp)

  eslint index.js

  Unexpected console statement no-console - 不能使用console

   

  ‘console’ is not defined no-undef - console变量未定义，不能使用未定义的变量

  ​    一条一条解决，不能使用console的提示，那我们就禁用no-console就好了，在配置文件中添加rules



  ```
    module.exports = {
        extends: 'eslint:recommended',
        rules: {
            'no-console': 'off',
         },
    };
  ```

  ​    配置规则写在rules对象里面，key表示规则名称，value表示规则的配置。
  ​    然后就是解决**no-undef**:出错的原因是因为JavaScript有很多种运行环境，比如常见的有浏览器和Node.js，另外还有很多软件系统使用JavaScript作为其脚本引擎，比如PostgreSQL就支持使用JavaScript来编写存储引擎，而这些运行环境可能并不存在console这个对象。另外在浏览器环境下会有window对象，而Node.js下没有；在Node.js下会有process对象，而浏览器环境下没有。
  所以在配置文件中我们还需要指定程序的目标环境：

  ```
    module.exports = {
        extends: 'eslint:recommended',
        env: {
            node: true,
         },
        rules: {
         'no-console': 'off',
        }
    };
  ```

  ​    再重新执行检查时，就没有任何提示输出了，说明index.js已经完全通过了检查。

## 配置

​    ESLint设计出来就是可以配置的，挺自由的，你可以关闭任何一条规则，只运行基本语法验证。有两种主要的方式来配置：

- **Configuration Comments** - 使用 JavaScript 注释把配置信息直接嵌入到一个文件。
- **Configuration Files** - 使用 JavaScript、JSON 或者 YAML 文件为整个目录和它的子目录指定配置信息。可以用 .eslintrc.* 文件或者在 package.json 文件里的 eslintConfig 字段这两种方式进行配置，ESLint 会查找和自动读取它们，再者，你可以在命令行指定一个配置文件。

​    有很多信息可以配置：

- **Environments** - 指定脚本的运行环境。每种环境都有一组特定的预定义全局变量。
- **Globals** - 脚本在执行期间访问的额外的全局变量
- **Rules** - 启用的规则及各自的错误级别

​    在配置文件。**eslintrc.js**中写配置内容，可以将**module.exports**内的内容直接写到**package.json**里用字段**eslintConfig**括起来就可以了。也可以在执行**eslint**命令是通过命令行参数来指定。

​    配置的详细说明文档可以参考这里：[Configuring ESLint](https://link.jianshu.com/?t=http://eslint.cn/docs/user-guide/configuring)

## 规则

​    我们**eslintrc.js**中的ruls中不仅仅是只有诸如**'no-console': 'off'**的规则，更多的是像下面这样的规则。

```
{
    "rules": {
        "semi": ["error", "always"],
        "quotes": ["error", "double"]
    }
}
```

​    这里的**"semi"** 和** "quotes" **是 ESLint 中 规则 的名称。中括号中第一个是错误级别。每条规则又三个取值：

- "off" or 0 - 关闭(禁用)规则
- "warn" or 1 - 将规则视为一个警告（并不会导致检查不通过）
- "error" or 2 - 将规则视为一个错误 (退出码为1，检查不通过)

​    有些规则还带有可选的参数，比如comma-dangle可以写成[ "error", "always-multiline" ]；no-multi-spaces可以写成[ "error", { exceptions: { "ImportDeclaration": true }}]。

​    配置和规则的内容有不少，将会另出一篇文介绍。

​    规则的详细说明文档可以参考这里：[Rules](https://link.jianshu.com/?t=http://eslint.cn/docs/rules/)

## 使用共享的配置文件

​    我们使用配置js文件是以**extends: 'eslint:recommended'**为基础配置，但是大多数时候我们需要制定很多规则，在一个文件中写入会变得很臃肿，管理起来会很麻烦。

​    我们可以将定义好规则的**.eslintrc.js**文件存储到一个公共的位置。改个名字比如**public-eslintrc.js**。在文件内容添加一两个规则。

```
module.exports = {
extends: 'eslint:recommended',
env: {
    node: true,
 },
rules: {
    'no-console': 'off',
    'indent': [ 'error', 4 ],
    'quotes': [ 'error', 'single' ],
    },
};
```

​    然后原来的**.eslintrc.js**文件内容稍微变化下，删掉规则啥的，留下一个**extends**。

```
module.exports = {
    extends: './public-eslintrc.js',
};
```

​    这个要测试的是啥呢，就是看看限定缩进是4个空格和使用单引号的字符串等，然后测试下，运行**eslint index.js**，得到的结果是没有问题的，但是如果在`index.js`中的`var ret = {};`前面加个空格啥的，结果就立马不一样了。



![img](https://upload-images.jianshu.io/upload_images/1062695-7abd726abecfca07.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1000/format/webp)

eslint index.js



​    这时候提示第7行的是缩进应该是8个空格，而文件的第7行却发现了9个空格，说明公共配置文件public-eslintrc.js已经生效了。

​    除了这些基本的配置以外，在npm上有很多已经发布的ESLint配置，也可以通过安装使用。配置名字一般都是**eslint-config-**为前缀，一般我们用的eslint是全局安装的，那用的**eslint-config-**模块也必须是全局安装，不然没法载入。

​    下面是官网的一些资料：
​    [使用共享的模块](https://link.jianshu.com/?t=http://eslint.cn/docs/user-guide/configuring#using-a-shareable-configuration-package) 
​    [使用插件](https://link.jianshu.com/?t=http://eslint.cn/docs/user-guide/configuring#using-the-configuration-from-a-plugin) 
​    [使用配置文件](https://link.jianshu.com/?t=http://eslint.cn/docs/user-guide/configuring#using-a-configuration-file)

​    在这提一下，[Rules](https://link.jianshu.com/?t=http://eslint.cn/docs/rules/)页面的很多规则后面都有一个橙色的小扳手标识，这个标识在执行

```
eslint index.js --fix
```

的时候，--fix参数可以自动修复该问题。
​    比如我们在规则中添加一条no-extra-semi: 禁止不必要的分号。

```
'no-extra-semi':'error'
```

​    然后，我们在index.js最后多添加一个分号



![img](https://upload-images.jianshu.io/upload_images/1062695-f9dc5ca53e9a1498.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/896/format/webp)

不必要的分号

​    执行eslint index.js，得到结果如下：



![img](https://upload-images.jianshu.io/upload_images/1062695-499a2c6182d6692d.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/934/format/webp)

不必要的分号

​    我们再执行

```
eslint index.js --fix
```

就会自动修复，index.js那个多余的分号也就被修复消失不见了。

## 发布自己的配置

​    共享的配置文件那一节里面已经说了，因为项目中需要配置的内容太多，所以可以在extends中指定一个文件名，或者一个eslint-config-开头的模块名。为了便于共享，一般推荐将其发布成一个NPM模块。

​    其原理就是在载入模块时输出原来.eslintrc.js的数据。比如我们可以创建一个模块 `eslint-config-my` 用于测试。

​    创建文件夹和文件：



![img](https://upload-images.jianshu.io/upload_images/1062695-a61613cb99752918.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/664/format/webp)

创建文件

​    my-config.js:

```
module.exports = {
    extends: 'eslint:recommended',
    env: {
        node: true,
        es6: true
    },
    rules: {
        'no-console': 'off',
        'indent': [ 'error', 4 ],
        'quotes': [ 'error', 'single' ]
    }
};
```

​    package.json:

```
{
    "name": "application-name1",
    "version": "0.0.1",
    "main":"my-config.js"
}
```

​    为了能让 eslint 正确载入这个模块，我们需要执行 npm link 将这个模块链接到本地全局位置：

```
npm link eslint-config-my
```

​    然后将文件 .eslintrc.js 改成：

```
module.exports = {
    extends: 'my',
}; 
```

​    提醒：在 extends 中， eslint-config-my 可简写为 **my** 。

​    在执行 `eslint merge.js` 检查，可看到没有任何错误提示信息，说明 eslint 已经成功载入了 `eslint-config-my` 的配置。如果我们使用 `npm publish` 将其发布到 NPM 上，那么其他人通过 `npm install eslint-config-my` 即可使用我们共享的这个配置。

​    在`eslint --init`初始化文件的时候，有一些默认的数据项，我就简单的说一下。

- parserOptions

  ​    EsLint通过parserOptions，允许指定校验的ecma的版本，及ecma的一些特性

  ```
    {
        "parserOptions": {
            "ecmaVersion": 6, //指定ECMAScript支持的版本，6为ES6
            "sourceType": "module", //指定来源的类型，有两种”script”或”module”
            "ecmaFeatures": {
                "jsx": true//启动JSX
            },
        }
    }
  ```

- Parser

  ​    EsLint默认使用esprima做脚本解析，当然你也切换他，比如切换成 babel-eslint解析

  ```
    {
        "parser": "esprima" //默认，可以设置成babel-eslint，支持jsx
    }
  ```

- Environments

  ​    Environment可以预设好的其他环境的全局变量，如brower、node环境变量、es6环境变量、mocha环境变量等

  ```
    {
        "env": {
            "browser": true,
            "node": true
        }
    }
  ```

  ​    如果你想使用插件中的环境变量，你可以使用plugins指定，如下

  ```
    {
        "plugins": ["example"],
        "env": {
            "example/custom": true
        }
    }
  ```

- Globals

  ​    指定你所要使用的全局变量，true代表允许重写、false代表不允许重写

  ```
    {
        "globals": {
            "var1": true,
            "var2": false
        }
    }
  ```

- Plugins

  ​    EsLint允许使用第三方插件

  ```
    {
        "plugins": [
            "react"    
        ]
    }
  ```

- Rules

  ​    这个就是上面说的规则。

## 自定义规则

​    ESLint自带的规则一般都不会很全面，在实际的项目中，我们要根据自己的需求来创建自己的规则。这也算的上是ESLint最有特色的地方了。

​    在我看来，发布自己的配置，其实就是要先自定义规则，自己的配置中加上自定义的规则，应该会比较适合实际项目中的使用。

​    以 eslint-plugin-react 为例，安装以后，需要在 ESLint 配置中开启插件，其中 eslint-plugin- 前缀可以省略：

```
{
    "plugins": [
        "react"
    ]
}
```

接下来开启 ESLint JSX 支持（ESLint 内置选项）：

```
{
    "ecmaFeatures": {
        "jsx": true
    }
}
```

然后就可以配置插件提供的规则了：

```
{
    "rules": {
        "react/display-name": 1,
        "react/jsx-boolean-value": 1
    }
}
```

自定义规则都是以插件名称为命名空间的。

## 工作流集成

​    ESLint 可以集成到主流的编辑器和构建工具中，以便我们在编写的代码的同时进行 lint。

##### 编辑器集成

​    以 WebStorm 为例，只要全局安装 ESLint 或者在项目中依赖中添加 ESLint ，然后在设置里开启 ESLint 即可。其他编辑可以从官方文档中获得获得具体信息。

##### 构建系统集成

​    在 Gulp 中使用：

```
var gulp = require('gulp');  
var eslint = require('gulp-eslint');

gulp.task('lint', function() {  
    return gulp.src('client/app/**/*.js')
    .pipe(eslint())
    .pipe(eslint.format());
});
```

其他构建工具参考官方文档。

## 总结

#####     以上呢，就是我花了两天整理的一些资料，不能算很全，但是对于像我这样的新手进行的初步了解应该是够了，之后可能有时间的话会陆续的整理一些相关的资料发布出来。

## 参考：

​    [ESLint-官方文档](https://link.jianshu.com/?t=http://eslint.org/)

​    [利用ESLint检查代码质量](https://link.jianshu.com/?t=http://cnodejs.org/topic/57c68052b4a3bca66bbddbdd)

​    [Eslint 规则说明](https://link.jianshu.com/?t=http://blog.csdn.net/helpzp2008/article/details/51507428)

​    [ESLint 使用入门](https://link.jianshu.com/?t=https://csspod.com/getting-started-with-eslint/)





https://www.jianshu.com/p/f2f06a0e154b