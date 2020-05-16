[TOC]



# 三十分钟学会TypeScript

今天我们来看一看 TypeScript，它是一种可以编译成 JavaScript 的编程语言，是为构建大型复杂应用程序的开发者们而设计的。它继承了许多语言的编程思想，比如 C# 和 Java，并且相对于宽松自由式的 JavaScript，它添加了更多的规则和约束。

这个教程适用于相当精通 JavaScript，但刚接触 TypeScript 的开发者们。本教程涵盖了大部分的基础知识和主要特性，同时包含了许多带有注释的例子，来帮助你学习这门语言。

让我们开始吧！

### 使用 TypeScript 的好处

JavaScript 已经很棒了，你或许会怀疑，我真的需要学习 TypeScript 吗？从技术层面上来说，成为一位出色的开发者确实不需要学习 TypeScript，大多数人没有学习 TypeScript 也做的很好。但是，工作中使用 TypeScript 确实有许多好处：

- 基于静态类型，用 TypeScript 编辑代码有更高的预测性，更易纠错。
- 由于模块，命名空间和强大的面向对象编程支持，使构建大型复杂应用程序的代码库更加容易。
- TypeScript在编译为JavaScript的过程中，在它到达运行时间前可以捕获所有类型的错误，并中断它们的执行。
- 即将到来的 Angular 2 框架就是用 TypeScript 编写的，同时推荐开发人员在项目中也使用这种语言。

（上述原因中的）最后一点，实际上是对于很多开发者们来说最重要的一点，也是使他们对 TypeScript 产生兴趣的主要原因。Angular 2是现在最热门的框架之一，尽管开发者们可以一起使用常规的 JavaScript，但是大部分的教程和例子都是用 TypeScript 写的。随着Angular 2逐渐扩展自己的社区，越来越多的人将会选择 TypeScript。 

最近 TypeScript 的人气上升趋势，数据来源于Google Trends。

### 安装TypeScript

该教程需要Node.js 和 Npm。如果没有安装请[点击这里](https://docs.npmjs.com/getting-started/installing-node)。

安装 TypeScript 最简单的方式就是通过 npm。使用以下命令行，可以全局安装 TypeScript 包，然后就可以在所有项目中使用TypeScript编译器了:

```
npm install -g typescript
```

打开终端然后运行 tsc -v 命令来查看是否正确安装了 TypeScript.

```
tsc -v
 
Version 1.8.10
```



### 支持 TypeScript 的文本编辑器

TypeScript是开源的项目，由微软开发和维护，因此最初只有微软的 [Visual Studio](https://www.visualstudio.com/) 支持。现在，出现了更多本身支持或者通过插件支持 TypeScript 语法、智能提示、纠错、甚至是内置编译器的文本编辑器和IDE。

- [Visual Studio Code](https://code.visualstudio.com/)– 微软另外一个轻量级的开源代码编辑器。内置支持TypeScript。
- Sublime Text的官方[免费插件](https://github.com/Microsoft/TypeScript-Sublime-Plugin)。
- [WebStorm](https://www.jetbrains.com/webstorm/)的最新版本带有内置支持。
- [其他](https://github.com/Microsoft/TypeScript/wiki/TypeScript-Editor-Support)包括Vim，Atom，Emacs等。

### 编译成 JavaScript

TypeScript 是 写在 **.ts** 文件（或者 JSX的.tsx）里，不能直接在浏览器端运行，需要首先转译为vanilla.js。这个编译的过程可以有多种实现方式：

- 在终端上运行前面提到的命令行工具 `tsc`。
- 直接在 Visual Studio 或者其他 IDE 和文本编辑器上（操作）。
- 使用自动化构建工具，例如 [gulp](http://gulpjs.com/)。

我们发现第一个方法是最简单的，对于初学者也是最容易接受的，因此我们将在教程中使用该方法。

下面的命令行把 TypeScript 文件 main.ts编译为 JavaScript 版本的 main.js。如果 main.js 已经存在的话会被覆盖。

```
tsc main.ts
```

也可以通过列出所有的文件或者使用通配符来一次编译多个文件：

```
#Will result in separate .js files: main.js worker.js.
 
tsc main.ts worker.ts
 
#Compiles all .ts files in the current folder. Does NOT work recursively.
 
tsc *.ts
```

有更改的时候也可以使用 –watch 来自动编译成 TypeScript 文件：

```
# Initializes a watcher process that will keep main.js up to date. 
tsc main.ts --watch
```

更高级的 TypeScript 用户也可以创建一个 tsconfig.json 文件，包含多种构建设置。因为配置文件在某种程度上是可以自动化进程的，所以在有许多.ts文件的大型项目中有配置文件是很方便的。可以在[这里](http://www.typescriptlang.org/docs/handbook/tsconfig-json.html)阅读到更多关于 tsconfig.json 的TypeScript 文档。

### 静态类型

TypeScript 一个很独特的特征是支持静态类型。意思就是可以声明变量的类型，（因此）编译器就可以确保赋值时不会产生类型错误。如果省略了类型声明，TypeScript 将会从代码中自动推测出正确的类型。

这有个例子。任意变量，函数自变量或者返回值在初始化时都可以定义自己的类型。

```
var burger: string = 'hamburger',     // String 
    calories: number = 300,           // Numeric
    tasty: boolean = true;            // Boolean

// Alternatively, you can omit the type declaration:
// var burger = 'hamburger';

// The function expects a string and an integer.
// It doesn't return anything so the type of the function itself is void.

function speak(food: string, energy: number): void {
  console.log("Our " + food + " has " + energy + " calories.");
}

speak(burger, calories);
```

因为 JavaScript 是弱类型语言（即不声明变量类型），因此 TypeScript 编译为 JavaScript 时，（变量类型的声明）全部被移除：

```
// JavaScript code from the above TS example.

var burger = 'hamburger',
    calories = 300, 
    tasty = true; 

function speak(food, energy) {
    console.log("Our " + food + " has " + energy + " calories.");
}

speak(burger, calories);
```

然而，如果我们试着输入非法的语句，tsc 会警告代码里有错误。例如：

```
// The given type is boolean, the provided value is a string.
var tasty: boolean = "I haven't tried it yet";
```

```
main.ts(1,5): error TS2322: Type 'string' is not assignable to type 'boolean'.
```

如果传入错误的函数自变量也会发出警告：

```
function speak(food: string, energy: number): void{
  console.log("Our " + food + " has " + energy + " calories.");
}

// Arguments don't match the function parameters.
speak("tripple cheesburger", "a ton of");
```

```
main.ts(5,30): error TS2345: Argument of type 'string' is not assignable to parameter of type 'number'.
```

以下是一些最常用的数据类型：

- Number (数值类型)  –  所有数字都是数值类型的，无论是整数、浮点型或者其他数值类型都相同。
- String（字符串类型） –  文本类型，就如 vanilla JS 字符串一样可以使用单引号或者双引号。
- Boolean（布尔类型） –   true 或者 false，用 0 和 1 会造成编译错误。
- Any（任意类型） – 该类型的变量可以设定为字符串类型，数值类型或者任何其他类型。
- Arrays（数组类型） – 有两种语法：my_arr: number[];或者my_arr: Array<number>
- Void （空类型）- 用在不返回任何值的函数中。

可以到官方的TypeScript文档查看所有变量类型列表 –  点击[这里](http://www.typescriptlang.org/docs/handbook/basic-types.html)。

### Interfaces 接口

接口通常会根据一个对象是否符合某种特定结构来进行类型检查。通过定义一个接口我们可以命名一个特殊的组合变量，确保它们会一直一起运行。当转译成 JavaScript 时，接口会消失 – 它们唯一的目的是在开发阶段里起到辅助的作用。

在下面的例子中我们定义了一个简单的接口来对一个函数自变量进行类型检查：

```
// Here we define our Food interface, its properties, and their types.
interface Food {
    name: string;
    calories: number;
}

// We tell our function to expect an object that fulfills the Food interface. 
// This way we know that the properties we need will always be available.
function speak(food: Food): void{
  console.log("Our " + food.name + " has " + food.calories + " calories.");
}

// We define an object that has all of the properties the Food interface expects.
// Notice that types will be inferred automatically.
var ice_cream = {
  name: "ice cream", 
  calories: 200
}

speak(ice_cream);
```

属性的顺序并不重要。我们只需必要的属性存在并且是正确的类型。如果哪里有遗漏，类型错误，或者命名不同的话，编译器都会报警告信息。

```
interface Food {
    name: string;
    calories: number;
}
 
function speak(food: Food): void{
  console.log("Our " + food.name + " has " + food.calories + " grams.");
}
 
// We've made a deliberate mistake and name is misspelled as nmae.
var ice_cream = {
  nmae: "ice cream", 
  calories: 200
}
 
speak(ice_cream);
```

```
main.ts(16,7): error TS2345: Argument of type '{ nmae: string; calories: number; } 
is not assignable to parameter of type 'Food'. 
Property 'name' is missing in type '{ nmae: string; calories: number; }'.
```

这只是入门指南，所以我们并不准备介绍更多关于接口的细节。不过，有很多没有提及到的地方，我们推荐查看TypeScript文档 – 点击[这里](http://www.typescriptlang.org/docs/handbook/interfaces.html)。

### 类

在搭建大型规模的应用程序时，尤其是在 Java 或 C# 当中，许多开发者会优先选择面向对象编程。TypeScript 提供一个类系统，和 Java、C# 中的非常相似，包括了继承，抽象类，接口实现，setters/getters 方法等。

值得一提的是由于最新的 JavaScript 更新（ECMAScript 2015），这些类对于 vanilla JS 来说是原生的，并且在没有 TypeScript 的情况下也可以使用。这两种实现方式非常相似但是也有不同的地方，TypeScript 更加严格一些。

继续上面的 food的 例子，这里有一个简单的TypeScript类：

```
class Menu {
  // Our properties:
  // By default they are public, but can also be private or protected.
  items: Array<string>;  // The items in the menu, an array of strings.
  pages: number;         // How many pages will the menu be, a number.

  // A straightforward constructor. 
  constructor(item_list: Array<string>, total_pages: number) {
    // The this keyword is mandatory.
    this.items = item_list;    
    this.pages = total_pages;
  }

  // Methods
  list(): void {
    console.log("Our menu for today:");
    for(var i=0; i<this.items.length; i++) {
      console.log(this.items[i]);
    }
  }

} 

// Create a new instance of the Menu class.
var sundayMenu = new Menu(["pancakes","waffles","orange juice"], 1);

// Call the list method.
sundayMenu.list();
```

只要写过一点 Java 或者 C# ，就会发现TypeScript和它们在语法上非常相似。继承也是一样：

```
class HappyMeal extends Menu {
  // Properties are inherited

  // A new constructor has to be defined.
  constructor(item_list: Array<string>, total_pages: number) {
    // In this case we want the exact same constructor as the parent class (Menu), 
    // To automatically copy it we can call super() - a reference to the parent's constructor.
    super(item_list, total_pages);
  }

  // Just like the properties, methods are inherited from the parent.
  // However, we want to override the list() function so we redefine it.
  list(): void{
    console.log("Our special menu for children:");
    for(var i=0; i<this.items.length; i++) {
      console.log(this.items[i]);
    }

  }
}

// Create a new instance of the HappyMeal class.
var menu_for_children = new HappyMeal(["candy","drink","toy"], 1);

// This time the log message will begin with the special introduction.
menu_for_children.list();
```

更深入了解类，可以阅读 TypeScript 文档 – 点击[这里](http://www.typescriptlang.org/docs/handbook/classes.html)。

### 泛型

泛型（Generics）是允许同一个函数接受不同类型参数的一种模板。相比于使用 any 类型，使用泛型来创建可复用的组件要更好，因为泛型会保留参数类型。

一段简单的脚本例子，传入一个参数，返回一个包含了同样参数的数组。

```
// The <T> after the function name symbolizes that it's a generic function.
// When we call the function, every instance of T will be replaced with the actual provided type.

// Receives one argument of type T,
// Returns an array of type T.

function genericFunc<T>(argument: T): T[] {    
  var arrayOfT: T[] = [];    // Create empty array of type T.
  arrayOfT.push(argument);   // Push, now arrayOfT = [argument].
  return arrayOfT;
}

var arrayFromString = genericFunc<string>("beep");
console.log(arrayFromString[0]);         // "beep"
console.log(typeof arrayFromString[0])   // String

var arrayFromNumber = genericFunc(42);
console.log(arrayFromNumber[0]);         // 42
console.log(typeof arrayFromNumber[0])   // number
```

第一次调用函数的时候，我们将类型手动设置成字符串。第二次及以后再次调用的时候就不必这样做了，因为编译器会判断传递过什么参数并且自动决定哪种类型最适合。虽然不是强制性的，但是由于编译器在众多复杂环境中确定正确类型的时候可能会失败，所以每次都传入类型是好的做法。

TypeScript 文档里包含了一些比较新的例子，包括泛型类，泛型类与接口绑定等等，更多请点击[这里](http://www.typescriptlang.org/docs/handbook/generics.html)。

在开发大型应用时，另一个重要的概念是模块化。与一个有 10000 行代码的文件相比，把代码分成多个可复用组件，这样可以帮助项目保持条理性和易懂性。

TypeScript 介绍了导入和导出模块的语句，但是并不能解决文件间的真正连接。TypeScript 依赖于第三方函数库来加载外部模块：用于浏览器应用程序的 [require.js](http://requirejs.org/) 和用于 Node.js 的 [CommonJS](https://en.wikipedia.org/wiki/CommonJS)。我们来看一个简单的带有 require.js 的TypeScript 模块例子：

我们会有两个文件。一个是导出函数，另一个是导入并调用函数。

#### exporter.ts

```
var sayHi = function(): void {
    console.log("Hello!");
}
 
export = sayHi;
```



#### importer.ts

```
import sayHi = require('./exporter');
sayHi();
```

现在我们需要下载 require.js，包含在一个script标签里 – 如何设置请点击[这里](http://requirejs.org/docs/start.html)。最后一步是编译这两个 .ts 文件。需要添加一个额外的参数来告诉 TypeScript，我们是为 require.js 创建模块的（也被称为AMD），而不是 CommonJS。

```
tsc --module amd *.ts
```

模块是相当复杂的而且也超出了本教程的范围。如果想继续学习更多关于模块的知识，可以查看 TypeScript 文档 – 点击[这里](http://www.typescriptlang.org/docs/handbook/modules.html)。

### 第三方声明文件

在使用一个常规 JavaScript 库时，我们需要用到一个声明文件来使这个库是否和 TypeScript 兼容。一个声明文件包含 .d.ts 扩展名和关于该库的多种信息，还有API。

TypeScript的声明文件通常是手写的，但是极有可能你需要的库中已经有了一个由其他人创建的 .d.ts 文件。[DefinitelyTyped](http://definitelytyped.org/) 是最大的公共存储库，包括1000多个库文件。也有一个用来管理 TypeScript 定义的 Node.js 流行模块，叫 [Typings](https://github.com/typings/typings)。

如果仍需要亲自写声明文件，可以从[这里](http://www.typescriptlang.org/docs/handbook/writing-declaration-files.html)开始。

### TypeScript 2.0将带来的新特性

TypeScript 目前仍在积极开发和持续演变中。编写这个教程的时候，LTS 是 1.8.10 版本，但微软方面已经发布了一个TypeScript 2.0测试版。这个版本可以用来做公共测试，现在可以来试一试：

```
npm install -g typescript@beta
```

介绍些新概念，例如：

- Non-nullable 类型标志，防止变量值被设定为 null 或者 undefined。
- 通过 npm install 来直接获取声明文件的新改进系统。
- 控制流类型分析来抓取之前被编译器漏掉的错误。
- 模块导入/导出语句方面的一些创新。

另一个被期待已久的特性是在 async/await 块中能够控制异步流功能。这个特性应该会在未来 2.1 版本更新中可用。

### 扩展阅读

虽然会被官方文档的信息量震撼到，但是通读文档带来的好处是巨大的。我们的教程只是做个介绍，并没有涵盖 TypeScript 文档里的所有章节。这里有一些更有用的但是被跳过的概念。

- 命名空间 – [这里](http://www.typescriptlang.org/docs/handbook/namespaces.html).
- 枚举 – [这里](http://www.typescriptlang.org/docs/handbook/enums.html).
- 高级类型和类型保护 – [这里](http://www.typescriptlang.org/docs/handbook/advanced-types.html).
- 用TypeScript写JSX – [这里](http://www.typescriptlang.org/docs/handbook/jsx.html).

### 总结

 

我们希望您喜欢这个教程！

您对 TypeScript 有什么想法吗？会考虑在您的项目上使用 TypeScript 吗？请在下面留下您的评论！



http://web.jobbole.com/87535/