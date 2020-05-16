

[TOC]



# CSS伪类与伪元素完全指南

刚开始从事Web设计时，我犯了很多错误，也因此获得了进步。那时候没有*Smashing Magazine*、*Can I Use*、_ CodePen_，也没有其他我们现在常见的工具。只要有人能告诉一个设计思路，特别是CSS前沿方向的，那就谢天谢地了。

今天我的经验已经很丰富了，所以想本着友好、随意、探讨的原则，跟大分享一下CSS中的伪类和伪元素。

如果你已经是有经验的Web设计者和开发者了，那么一定对本文要讨论的伪类和伪元素有所了解。不过，还是建议你先看看本文后面[完整的列表](https://www.jianshu.com/p/9086114e07d4#h4-table-of-contents-in-alphabetical-order-link)，看有没有一两个你还不知道的？

在真正开始之前，因为我们想讲伪类和伪元素嘛，所以先问个基本的问题：你知道这里的“伪”是什么意思吗？不确定的话，可以参考[Dictionary.com的定义](https://link.jianshu.com/?t=http://dictionary.reference.com/browse/pseudo)：

> *形容词*
>
> \1. 不是真实的但有其外观；伪装的；假的或欺骗的；骗人的。
>
> \2. 差不多，很接近，或尽可能一样。

不用管W3C是怎么定义的，反正伪类就是某个元素的一种虚拟状态，或者说一种特有的性质，这种状态或性可以通过CSS捕捉到。常见的伪类有：`:link`、`:visited`、`:hover`、`:active`、`:first-child`和`:nth-child`。当然这只是一少部分，一会儿我们都会介绍。

伪类是一个冒号（`:`）后跟伪类的名字构成的，有时候名字后面还会有一个放在括号里的值。`:nth-child`是第几个？

好了，再说伪元素。伪元素是一种虚拟的元素，CSS把它当成普通HTML元素看待。之所以叫伪元素，就因为它们在文档树或DOM中并不实际存在。换句话说，我们不会在HTML中包含伪元素，只会通过CSS来创建伪元素。

以下是几个常见的伪元素：`:after`、`:before`和`:first-letter`。伪元素会在本文后面介绍。

### 伪元素是一个冒号还是两个冒号？

简单回答：多数情况下，都行。

两个冒号（`::`）是CSS3为了区分`::before`、`::after`这样的伪元素和`:hover`、`:active`等伪类才引入的。除了IE8及以下版本，所有浏览器都支持两个冒号的伪元素表示法。

不过，有些伪元素只能使用两个冒号，像`::backdrop`。

我个人使用一个冒号，为了跟以前的浏览器兼容。当然，不用两个冒号不行的时候，还是要用两个冒号。

这里没有对错，完全看你个人喜好。

不过，我在写这篇文章时查了一下，规范[建议使用单冒号表示法](https://link.jianshu.com/?t=https://www.w3.org/community/webed/wiki/Advanced_CSS_selectors#CSS3_pseudo-element_double_colon_syntax)，原因也是向后兼容：

> 请注意CSS3中表示伪元素使用双冒号，比如`a::after { … }`，这是为了与伪类区分开。伪类应该是在CSS中经常出现的。不过，CSS3也允许单冒号的伪元素，目的是向后兼容。我们也建议暂时使用单冒号。

如果伪元素同时支持单、双冒号的形式，本文标题会给出两种形式。如果只支持双冒号，那就只有一种形式。

### 什么时候使用（不使用）生成的内容

通过CSS生成内容需要用到CSS属性`content`和伪元素`:before`或`:after`。

其中的“内容”（`content`）可是纯文本，也可以是一个容器，通过CSS操作来显示[某种图形或者装饰性元素](https://link.jianshu.com/?t=http://codepen.io/ricardozea/pen/sdlvu)。本文只介绍第一种内容，即文本。

重要的内容可不要使用生成的内容，原因如下：

- 屏幕阅读器读不到它
- 无法选中
- 如果为了装饰而在生成内容中使用了多余的内容，那么支持CSS生成内容的屏幕阅读器会大声地把它读出来，导致用户体验更差

CSS生成的内容只适用于装饰性、不重要的内容，但也要确保屏幕阅读器能够适当处理它，让使用这些辅助技术的用户不至于分心。这里适用“渐进增强”原则。

在*Smashing Magazine*上，Gabriele Romanato为此写过[一篇非常棒的文章](https://link.jianshu.com/?t=https://www.smashingmagazine.com/2013/04/css-generated-content-counters/)。

### 实验性伪类和伪元素

实验性的伪类和伪元素，指的是那些不稳定或没最终定案的伪类和伪元素。它们的语法和行为还可能有变。

不过，加上厂商前缀就可以使用这些实验性的伪类和伪元素。可以参考[Can I Use](https://link.jianshu.com/?t=http://caniuse.com/)，以及一些自动加前缀的工具，比如[-prefix-free](https://link.jianshu.com/?t=https://www.smashingmagazine.com/2011/10/prefixfree-break-free-from-css-prefix-hell/)或[Autoprefixer](https://link.jianshu.com/?t=https://autoprefixer.github.io/)就是必备的。

本文会在实验性的伪类和伪元素的名字旁边加上“experimental”标签。

#### 全部伪类和伪元素（按字母顺序）

- `:active`
- `::after/:after`
- `::backdrop (experimental)`
- `::before/:before`
- `:checked`
- `:default`
- `:dir (experimental)`
- `:disabled`
- `:empty`
- `:enabled`
- `:first-child`
- `::first-letter/:first-letter`
- `::first-line/:first-line`
- `:first-of-type`
- `:focus`
- `:fullscreen (experimental)`
- `:hover`
- `:in-range`
- `:indeterminate`
- `:invalid`
- `:lang`
- `:last-child`
- `:last-of-type`
- `:link`
- `:not`
- `:nth-child`
- `:nth-last-child`
- `:nth-last-of-type`
- `:nth-of-type`
- `:only-child`
- `:only-of-type`
- `:optional`
- `:out-of-range`
- `::placeholder (experimental)`
- `:read-only`
- `:read-write`
- `:required`
- `:root`
- `::selection`
- `:scope (experimental)`
- `:target`
- `:valid`
- `:visited`
- `Bonus content: A Sass mixin for links`

好啦，诸位，好戏开场了！

### 伪类

首先，我们讨论伪类，从状态伪类开始。

### 状态伪类

状态伪类通常出现在用户执行某个操作的情况下。在CSS里，“操作”也可以是“无操作”，比如尚未点过的链接。

下面就有请它们一个一个地上场。

#### :LINK

`:link`伪类表示链接的正常状态，选择那些尚未被点过的链接。建议在其他链接相关的伪类之前声明`:link`，它们的顺序为：`:link`、`:visited`、`:hover`、`:active`。

```
a:link {
    color: orange;
}
```

当然，这个伪类也可以省略：

```
a {
    color: orange;
}
```

#### :VISITED

`:visited`伪类选择点过的链接，应该声明在第二位（在`:link`之后）。

```
a:visited {
    color: blue;
}
```

#### :HOVER

`:hover`伪类在用户指针悬停时生效。而且它不只可以用于链接。

它应该在第三位（在`:visited`之后）。

```
a:hover {
    color: orange;
}
```

[**看示例：http://codepen.io/ricardozea/pen/vGEzJK**](https://link.jianshu.com/?t=http://codepen.io/ricardozea/pen/vGEzJK)

#### :ACTIVE

`:active`伪类选择被鼠标指针或触摸操作“激活的” 元素，也可以通过键盘来激活，就像`:focus`伪类一样。

与`:focus`类似，但区别在于`:active`只发生在鼠标被按下到被释放的这段时间里。

它应该在第四位（在`hover`后面）。

```
a:active {
    color: rebeccapurple;
}
```

#### :FOCUS

`:focus`用于选择已经通过指针设备、触摸或键盘获得焦点的元素，在表单里使用得非常多。

```
a:focus {
    color: green;
}
```

或者：

```
input:focus {
    background: #eee;
}
```

#### 扩展内容：Sass中针对链接的混入

如果你用过CSS预处理器，那应该对这一部分感兴趣。

（如果你不熟悉CSS预处理器，没问题，跳过这一节，直接看下一节吧。）

为了简化CSS编码工作，这里介绍一下创建一组基本的链接样式的Sass混入（mixin）。

这里的混入没有默认参数，因此我们必须以一种友好的方式，声明链接的全部4种状态。

`:focus`和`:active`伪类的声明通常在一块，当然也可以给它们分开。

注意这个混入不仅仅适用于链接，而是适用于任何 HTML元素。

这就是我们定义的混入：

```
@mixin links ($link, $visited, $hover, $active) {
    & {
        color: $link;
        &:visited {
            color: $visited;
        }
        &:hover {
            color: $hover;
        }
        &:active, &:focus {
            color: $active;
        }
    }
}
```

**使用方法：**

```
a {
    @include links(orange, blue, yellow, teal);
}
```

**编译结果：**

```
a {
  color: orange;
}
a:visited {
  color: blue;
}
a:hover {
  color: yellow;
}
a:active, a:focus {
  color: teal;
}
```

[**看示例：http://codepen.io/ricardozea/pen/wMyZQe**](https://link.jianshu.com/?t=http://codepen.io/ricardozea/pen/wMyZQe)

### 结构化伪类

结构化伪类选择通过其他选择符无法选择的文档树或DOM中的其他信息。

#### :FIRST-CHILD

`:first-child`伪类选择父元素的第一个子元素。

在下面的例子中，只有第一个`li`元素的文本是橙色的。

**HTML:**

```
<ul>
    <li>This text will be orange.</li>
    <li>Lorem ipsum dolor sit amet.</li>
    <li>Lorem ipsum dolor sit amet.</li>
</ul>
```

**CSS:**

```
li:first-child {
    color: orange;
}
```

#### :FIRST-OF-TYPE

`:first-of-type`伪类选择父元素容器内任意类型子元素的第一个元素。

在下面的例子中，第一个`li`元素和第一个`span`元素的文本才是橙色的。

**HTML:**

```
<ul>
    <li>This text will be orange.</li>
    <li>Lorem ipsum dolor sit amet. <span>This text will be orange.</span></li>
    <li>Lorem ipsum dolor sit amet.</li>
</ul>
```

**CSS:**

```
ul :first-of-type {
    color: orange;
}
```

#### :LAST-CHILD

`:last-child`伪类选择父元素的最后一个子元素。

在下面的例子中，只有最后一个`li`元素的文本是橙色的。

**HTML:**

```
<ul>
    <li>Lorem ipsum dolor sit amet.</li>
    <li>Lorem ipsum dolor sit amet.</li>
    <li>This text will be orange.</li>
</ul>
```

**CSS:**

```
li:last-child {
    color: orange;
}
```

#### :LAST-OF-TYPE

`:last-of-type`伪类选择父元素容器内任意类型子元素的最后一个元素。

在下面的例子中，最后一个`li`元素和最后一个`span`元素的文本才是橙色的。

**HTML:**

```
<ul>
    <li>Lorem ipsum dolor sit amet. <span>Lorem ipsum dolor sit amet.</span> <span>This text will be orange.</span></li>
    <li>Lorem ipsum dolor sit amet.</li>
    <li>This text will be orange.</li>
</ul>
```

**CSS:**

```
ul :last-of-type {
    color: orange;
}
```

#### :NOT

`:not`伪类也叫取反伪类，它通过括号接受一个参数，一个“选择符”。实际上，这个参数也可以是另一个伪类。

这个伪类可以连缀使用，但不能包含别的`:not`选择符。

在下面的例子中，`:not`伪类选择与参数不匹配的元素。

**HTML:**

```
<ul>
    <li class="first-item">Lorem ipsum dolor sit amet.</li>
    <li>Lorem ipsum dolor sit amet.</li>
    <li>Lorem ipsum dolor sit amet.</li>
    <li>Lorem ipsum dolor sit amet.</li>
</ul>
```

**CSS:**

应用下面的CSS，除了类为`.first-item`的`li`之外的`li`元素的文本都是橙色的：

```
li:not(.first-item) {
    color: orange;
}
```

下面看一看“连缀”两个`:not`伪类。应用下面的CSS规则，除了类为`.first-item`的`li`和最后一个`li`，其他`li`都会有黄色背景和黑色文本：

```
li:not(.first-item):not(:last-of-type) {
    background: yellow;
    color: black;
}
```

[**看示例：http://codepen.io/ricardozea/pen/dGmqbg**](https://link.jianshu.com/?t=http://codepen.io/ricardozea/pen/dGmqbg)

#### :NTH-CHILD

`:nth-child`伪类根据元素在标记中的次序选择相应的元素。

这个伪类在CSS中是用途最广、支持也最广的。

所有`:nth`伪类都接受一个参数，这个参数是一个公式。公式可以是一个整数，或者关键字`odd`、`even`，或者形如`an+b`的结构。

对于`an+b`:

- `a`是一个数值（整数）
- `n`就是`n`
- `+`是运算符，可以是加号`+`或减号`-`
- `b`也是一个整数，但只有使用了运算符的时候才会用到

以希腊字母的英文列表为例，以下是HTML标记结构：

```
<ol>
    <li>Alpha</li>
    <li>Beta</li>
    <li>Gamma</li>
    <li>Delta</li>
    <li>Epsilon</li>
    <li>Zeta</li>
    <li>Eta</li>
    <li>Theta</li>
    <li>Iota</li>
    <li>Kappa</li>
</ol>
```

**CSS:**

选择第2个子元素，结果Beta会变成橙色:

```
ol :nth-child(2) {
    color: orange;
}
```

从第2个子元素起，隔一个选一个，结果Beta、Delta、Zeta、Theta和Kappa会变成橙色:

```
ol :nth-child(2n) {
    color: orange;
}
```

选择所有偶数个子元素：

```
ol :nth-child(even) {
    color: orange;
}
```

从第6个子元素起，隔一个选一个，结果Zeta、Theta和Kappa会变成橙色：

```
ol :nth-child(2n+6) {
    color: orange;
}
```

[**看示例：http://codepen.io/ricardozea/pen/adYaER**](https://link.jianshu.com/?t=http://codepen.io/ricardozea/pen/adYaER)

#### :NTH-LAST-CHILD

除了是从后往前选择元素，`:nth-last-child`跟`:nth-child`完全一样。

**CSS:**

选择倒数第2个子元素，只有Iota是橙色：

```
ol :nth-last-child(2) {
    color: orange;
}
```

从倒数第2个子元素开始，隔一个选一个，结果Iota、Eta、Epsilon、Gamma和Alpha会变成橙色：

```
ol :nth-last-child(2n) {
    color: orange;
}
```

从后往前，选择所有偶数个子元素：

```
ol :nth-last-child(even) {
    color: orange;
}
```

从倒数第6个元素开始，隔一个选一个，因此Epsilon、Gamma和Alpha会变成橙色：

```
ol :nth-last-child(2n+6) {
    color: orange;
}
```

#### :NTH-OF-TYPE

`:nth-of-type`伪类与`:nth-child`类似，主要区别是它更具体了，只针对特定类型的元素。

在下面的例子中，所有容器内的第2个`p`元素将为橙色。

**HTML:**

```
<article>
    <h1>Heading Goes Here</h1>
    <p>Lorem ipsum dolor sit amet.</p>
    <a href=""></a>
    <p>This text will be orange.</p>
</article>
```

**CSS:**

```
p:nth-of-type(2) {
    color: orange;
}
```

#### :NTH-LAST-OF-TYPE

`:nth-last-of-type`伪类是从后往前数，其余跟`:nth-of-type`一样。

对于下面的例子，因为是从末尾开始，所以第1个段落会变成橙色。

**HTML:**

```
<article>
    <h1>Heading Goes Here</h1>
    <p>Lorem ipsum dolor sit amet.</p>
    <a href=""></a>
    <p>This text will be orange.</p>
</article>
```

**CSS:**

```
p:nth-last-of-type(2) {
    color: orange;
}
```

**相关资源**

建议大家在使用`:nth`伪类前，一定要参考下面这两篇不错的文章：

- “[CSS3 Structural Pseudo-Class Selector Tester](https://link.jianshu.com/?t=http://lea.verou.me/demos/nth.html)” Lea Verou
- “[:nth Tester](https://link.jianshu.com/?t=https://css-tricks.com/examples/nth-child-tester/)” CSS-Tricks

#### :ONLY-CHILD

`:only-child`选择父元素中唯一的子元素。

在下面的例子中，第一个`ul`只有一个子元素，因此该子元素将变成橙色。第二个`ul`有多个子元素，因此其子元素不会受`:only-child`伪类影响。

**HTML:**

```
<ul>
    <li>This text will be orange.</li>
</ul>

<ul>
    <li>Lorem ipsum dolor sit amet.</li>
    <li>Lorem ipsum dolor sit amet.</li>
</ul>
```

**CSS:**

```
ul :only-child {
    color: orange;
}
```

#### :ONLY-OF-TYPE

`:only-of-type`伪类选择同级中类型唯一的元素，与`:only-child`类似，但针对特定类型的元素，让选择符有了更强的意义。

在下面的例子中，第一个`ul`只有一个`li`元素，因此其文本将为橙色。

**HTML:**

```
<ul>
    <li>This text will be orange.</li>
</ul>

<ul>
    <li>Lorem ipsum dolor sit amet.</li>
    <li>Lorem ipsum dolor sit amet.</li>
</ul>
```

**CSS:**

```
li:only-of-type {
    color: orange;
}
```

#### :TARGET

`:target`伪类通过元素的ID及URL中的锚名称选择元素。

在下面的例子中，当浏览器中的URL以`#target`结尾时，ID为`target`的文章将被选中。

**URL:**

```
http://awesomebook.com/#target
```

**HTML:**

```
<article id="target">
    <h1><code>:target</code> pseudo-class</h1>
    <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit!</p>
</article>
```

**CSS:**

```
:target {
    background: yellow;
}
```

**提示：** `background:`是`background-color:`的简写形式，用于指定颜色时效果一样。

### 验证伪类

表单验证一直是Web设计与开始中最不好搞的。有了验证伪类，可以让用户填写表单的过程更平顺。

有一点要注意，虽然本节介绍的伪类都用于表单元素，但其中有的伪类也可以用于其他HTML元素。

下面就来看看这些伪类吧！

#### :CHECKED

`:checked`伪类选择被勾选或选中的单选按钮、多选按钮及列表选项。

在下面的例子中，复选框被勾选后，标签会突出显示，增加了用户体验。

[**看示例：http://codepen.io/ricardozea/pen/wMYExY**](https://link.jianshu.com/?t=http://codepen.io/ricardozea/pen/wMYExY)

#### :DEFAULT

`:default`伪类从表单中一组类似元素里选择默认的元素（即“提交”按钮。——译者注）。

如果要选择表单中没有类的默认按钮，可以使用`:default`。

注意，在表单中使用Reset或Clear按钮会招致严重的**可用性问题**，所以除非绝对必要再用。参考下面两篇文章：

- “[Reset and Cancel Buttons](https://link.jianshu.com/?t=http://www.nngroup.com/articles/reset-and-cancel-buttons/),” Nielsen Norman Group (2000)
- “[Killing the Cancel Button on Forms for Good](https://link.jianshu.com/?t=http://uxmovement.com/forms/killing-the-cancel-button-on-forms-for-good/),” UX Movement (2010)

[**看示例：http://codepen.io/ricardozea/pen/WrzJKO**](https://link.jianshu.com/?t=http://codepen.io/ricardozea/pen/WrzJKO)

#### :DISABLED

`:disabled`伪类选择禁用状态的表单元素。处于禁用状态的元素，不能被选中、勾选，不能获得焦点。

在下面的例子中，`name`输入框处于禁用状态，因此会半透明。

**HTML:**

```
<input type="text" id="name" disabled>
```

**CSS:**

```
:disabled {
    opacity: .5;
}
```

**提示：** 标记中是非要使用`disabled="disabled"`，只写一个`disabled`属性就行了。在XHTML中，`disabled="disabled"`这种写法才是必须的。

[**看示例：http://codepen.io/ricardozea/pen/NxOLZm**](https://link.jianshu.com/?t=http://codepen.io/ricardozea/pen/NxOLZm)

#### :EMPTY

`:empty`伪类选择其中不包含任何内容的空元素。只要包含一个字母、其他HTML元素，甚至一个空格，都不算空。

关于空或非空，以下是定义：

- **空** 
  元素中没有内容或字符。元素中包含HTML注释不算有内容。
- **非空** 
  出现在元素中的字符。空格也算。

在下面的例子中，

- 第一个元素中包含文本，因此背景不会变成橙色
- 第二个元素包含一个空格，空格也是内容，因此也不会有橙色背景
- 第三个元素中什么也没有（空的），因此背景为橙色
- 最后一个元素中只有一个HTML注释（也是空的），因此也有橙色背景。

**HTML:**

```
<div>This box is orange</div>
<div> </div>
<div></div>
<div><!-- This comment is not considered content --></div>
```

**CSS:**

```
div {
  background: orange;
  height: 30px;
  width: 200px;
}

div:empty {
  background: yellow;
}
```

[**看示例：http://codepen.io/ricardozea/pen/rxqqaM**](https://link.jianshu.com/?t=http://codepen.io/ricardozea/pen/rxqqaM)

#### :ENABLED

`:enabled`伪类选择启用的元素。所有表单元素默认都是启用的，除非在标记中添加了`disabled`属性。

通过`:enabled`和`:disabled`可以提供视觉上的反馈，改善用户体验。

在下面的例子中，禁用后又被启用的`name`输入框的不透明度将变为`1`，同时会有一个1像素的边框：

```
:enabled {
    opacity: 1;
    border: 1px solid green;
}
```

**提示：** 标记中是非要使用`enabled="enabled"`，只写一个`enabled`属性就行了。在XHTML中，`enabled="enabled"`这种写法才是必须的。

[**看示例：http://codepen.io/ricardozea/pen/zqYQxq**](https://link.jianshu.com/?t=http://codepen.io/ricardozea/pen/zqYQxq)

#### :IN-RANGE

`:in-range`伪类选择有范围且值在指定范围内的元素。

在下面的例子中，输入元素支持输入5~10。输入值在这个范围内，会触发绿色边框。

**HTML:**

```
<input type="number" min="5" max="10">
```

**CSS:**

```
input[type=number] {
    border: 5px solid orange;
}

input[type=number]:in-range {
    border: 5px solid green;
}
```

[**看示例：http://codepen.io/ricardozea/pen/XXOKwq**](https://link.jianshu.com/?t=http://codepen.io/ricardozea/pen/XXOKwq)

#### :OUT-OF-RANGE

`:out-of-range`伪类选择有范围且值超出指定范围的元素。

在下面的例子中，输入元素支持输入1~12。输入值超出这个范围内，会触发橙色边框。

**HTML:**

```
<input id="months" name="months" type="number" min="1" max="12">`
```

**CSS:**

```
input[type=number]:out-of-range {
    border: 1px solid orange;
}
```

[**看示例：http://codepen.io/ricardozea/pen/XXOKwq**](https://link.jianshu.com/?t=http://codepen.io/ricardozea/pen/XXOKwq)

#### :INDETERMINATE

`:indeterminate`伪类选择单选按钮或复选框在页面加载时没有被勾选的。

比如，页面加载后，一组单选按钮中没有默认或预先勾选的，或者一个复选框已经通过JavaScript设置为`indeterminate`状态。

**HTML:**

```
<ul>
    <li>
        <input type="radio" name="list" id="option1">
        <label for="option1">Option 1</label>
    </li>
    <li>
        <input type="radio" name="list" id="option2">
        <label for="option2">Option 2</label>
    </li>
    <li>
        <input type="radio" name="list" id="option3">
        <label for="option3">Option 3</label>
    </li>
</ul>
```

**CSS:**

```
:indeterminate + label {
    background: orange;
}
```

[**看示例：http://codepen.io/ricardozea/pen/adXpQK**](https://link.jianshu.com/?t=http://codepen.io/ricardozea/pen/adXpQK)

#### :VALID

`:valid`伪类选择输入格式符合要求的表单元素。

在下面的例子中，`email`输入框中的电子邮箱格式是正确的，因此这个输入框会被认为有效，将出现1像素的绿色边框：

```
input[type=email]:valid {
    border: 1px solid green;
}
```

[**看示例：http://codepen.io/ricardozea/pen/bEzqVg**](https://link.jianshu.com/?t=http://codepen.io/ricardozea/pen/bEzqVg)

#### :INVALID

`:invalid`伪类选择输入格式不符合要求的表单元素。

在下面的例子中，`email`输入框中的电子邮箱格式不正确，因此这个输入框会被认为无效，将出现橙色边框：

```
input[type=email]:invalid {
    background: orange;
}
```

[**看示例：http://codepen.io/ricardozea/pen/bEzqVg**](https://link.jianshu.com/?t=http://codepen.io/ricardozea/pen/bEzqVg)

#### :OPTIONAL

`:optional`伪类选择表单中非必填的输入字段。换句话说，只要输入字段中没有`required`属性，就会被`:optional`伪类选中。

在下面的例子中，这个数值字段是可以选填的，因此其中的文本将为灰色。

**HTML:**

```
<input type="number">
```

**CSS:**

```
:optional {
    color: gray;
}
```

#### :READ-ONLY

`:read-only`伪类选择用户不能编辑的元素，与`:disabled`伪类相似，标记中使用的属性决定了使用哪个伪类。

不能编辑的元素可以用来显示预先填好、不允许修改，但又需要连同表单一起提交的信息。

在下面的例子中，文本框有一个`readonly`属性，因此会被`:read-only`伪类选中，文本将为灰色。

**HTML:**

```
<input type="text" value="I am read only" readonly>
```

**CSS:**

```
input:read-only {
    color: gray;
}
```

[**看示例：http://codepen.io/ricardozea/pen/Nxopbj**](https://link.jianshu.com/?t=http://codepen.io/ricardozea/pen/Nxopbj)

#### :READ-WRITE

`:read-write`伪类选择用户可以编辑的元素，适用于有`contenteditable`属性的HTML元素。

有时候，可以与`:focus`伪类一块使用以增强用户体验。

在下面的例子中，点击`div`元素就可以编辑其中的内容，为此可以应用特殊的样式，让用户知道自己可以编辑其中的内容。

**HTML:**

```
<div class="editable" contenteditable>
    <h1>Click on this text to edit it</h1>
    <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit!</p>
</div>
```

**CSS:**

```
:read-write:focus {
    padding: 5px;
    border: 1px dotted black;
}
```

[**看示例：http://codepen.io/ricardozea/pen/LGqWxK**](https://link.jianshu.com/?t=http://codepen.io/ricardozea/pen/LGqWxK)

#### :REQUIRED

`:required`伪类选择有`required`属性的表单元素。

除了通过标签中的星号（*）提示必填，也可以通过这个伪类为输入字段应用样式。这样就万无一失了。

在下面的例子中，输入框有`required`属性，通过这个伪类为它应用特殊样式，可以提醒用户它是必填项。

**HTML:**

```
<input type="email" required>
```

**CSS:**

```
:required {
    color: black;
    font-weight: bold;
}
```

[**看示例：http://codepen.io/ricardozea/pen/KVJWmZ**](https://link.jianshu.com/?t=http://codepen.io/ricardozea/pen/KVJWmZ)

#### :SCOPE (EXPERIMENTAL)

`:scope`伪类适用于`style`标签中有`scoped`属性的情形。

如果页面中某一部分的`style`标签里没有`scoped`属性，那么`:scope`伪类会一直向上查找，直到`html`元素，即当前样式表的默认作用范围。

在下面的例子中，第二个`section`中有一个`scoped`样式表，因此这个`section`中的文本会变成斜体。

**HTML and CSS:**

```
<article>
    <section>
        <h1>Lorem ipsum dolor sit amet</h1>
        <p>Lorem ipsum dolor sit amet.</p>
    </section>
    <section>
        **<style scoped>
                        :scope {
                            font-style: italic;
                        }
                  </style>**
        <h1>This text will be italicized</h1>
        <p>This text will be italicized.</p>
    </section>
</article>
```

[**看示例：http://codepen.io/ricardozea/pen/ZQobzz**](https://link.jianshu.com/?t=http://codepen.io/ricardozea/pen/ZQobzz)

### 语言伪类

语言伪类与页面中包含的文本相关，与图片、视频等媒体无关。

#### :DIR (EXPERIMENTAL)

`:dir`伪类选择文档中指定了语言方向的元素。换句话说，为了使用`:dir`伪类，需要在标记中为相关元素指定`dir`属性。

语言方向目前有两种：`ltr`（从左到右）和`rtl`（从右往左）。

写这篇文章时，支持`:dir`伪类的只有Firefox（`-moz-dir()`），下面的例子同时使用带前缀和不带前缀的`:dir`选择符。

**注意：** 要用带前缀和不带前缀的选择符分别创建规则，两种选择符共享一条规则是不行的。

在下面的例子中，段落中的文字是阿拉伯文（是从右往左书写的），因此其颜色是橙色。

**HTML:**

```
<article dir="rtl">
    <p>التدليك واحد من أقدم العلوم الصحية التي عرفها الانسان والذي يتم استخدامه لأغراض الشفاء منذ ولاده الطفل.</p>
</article>
```

**CSS:**

```
/* 带前缀 */
article :-moz-dir(rtl) {
    color: orange;
}

/* 不带前缀 */
article :dir(rtl) {
    color: orange;
}
```

下面段落中的文字是英文（从左到右），颜色为蓝色。

**HTML:**

```
<article dir="ltr">
    <p>If you already know some HTML and CSS and understand the principles of responsive web design, then this book is for you.</p>
</article>
```

**CSS:**

```
/* 带前缀 */
article :-moz-dir(ltr) {
    color: blue;
}

/* 不带前缀 */
article :dir(ltr) {
    color: blue;
}
```

[**看示例：http://codepen.io/ricardozea/pen/adrxJy**](https://link.jianshu.com/?t=http://codepen.io/ricardozea/pen/adrxJy)

#### :LANG

`:lang`伪类选择的元素通过`lang=""`属性、相应的`meta`元素以及HTTP首部的协议信息来确定。

`lang=""`属性常用于`html`标签，其实也可以用于其他标签。

插一句，这里通常的做法是使用CSS的`quotes` 属性来标记特定的语言。不过，多数浏览器（包括IE9及更高版本）会在CSS中没有声明的情况下自动添加适当的引用标记。

不过，自动添加的引用标记也可能不合适。因为浏览器自动添加的与CSS添加的还不太一样。

比如浏览器为德语（`de`）添加的引用标记如下：

```
„Lorem ipsum dolor sit amet.“
```

但通过CSS为德语添加的引用标签则通常如下：

```
»Lorem ipsum dolor sit amet.«
```

这两种都对。因此，使用浏览器自动添加的引用标记，还是自己通过CSS的`:lang`伪类及`quotes`属性添加，都看你的需要。

下面看看怎么通过CSS来添加引用标记。

**HTML:**

```
<article lang="en">
    <q>Lorem ipsum dolor sit amet.</q>
</article>
<article lang="fr">
    <q>Lorem ipsum dolor sit amet.</q>
</article>
<article lang="de">
    <q>Lorem ipsum dolor sit amet.</q>
</article>
```

**CSS:**

```
:lang(en) q { quotes: '“' '”'; }
:lang(fr) q { quotes: '«' '»'; }
:lang(de) q { quotes: '»' '«'; }
```

[**看示例：http://codepen.io/ricardozea/pen/gPJyvJ**](https://link.jianshu.com/?t=http://codepen.io/ricardozea/pen/gPJyvJ)

### 其他伪类

下面再看看拥有其他功能的伪类。

#### :ROOT

`:root`伪类选择文档中最高层次的父元素。

在HTML中，`:root`伪类选择的就是`html`元素。但在SVG或XML等标记语言中，它可能选择不同的元素。

以下规则为HTML文档中最高层次的父元素`html`添加背景颜色：

```
:root {
    background: orange;
}
```

**注意：** 使用`html`也可以设置相同的样式，但`:root`是一个类，拥有比元素选择符（即`html`）更高的特指度。

#### :FULLSCREEN (EXPERIMENTAL)

`:fullscreen`伪类选择在全屏模式下显示的元素。

不过，这不适用于用户按F11进入的全屏模式，只适用于通过JavaScript Fullscreen API切换进入的全屏模式，通常由父容器中的图片、视频或游戏来调用。

怎么知道已经进入全屏模式呢？一般浏览器会在窗口顶部提示你，并告诉你按Escape键可以退出全屏模式。

使用`:fullscreen`伪类前必须知道，浏览器应用样式的方式差别很大。而且，不仅要在CSS中使用前缀，JavaScript中也一样。推荐使用Hernan Rajchert的[screenfull.js](https://link.jianshu.com/?t=https://github.com/sindresorhus/screenfull.js/)，它帮我们填了不少浏览器的“坑”。

本文不会讨论全屏API，只给出一个在WebKit和Blink浏览器中可用的例子。

**HTML:**

```
<h1 id="element">This heading will have a solid background color in full-screen mode.</h1>
<button onclick="var el = document.getElementById('element'); el.webkitRequestFullscreen();">Trigger full screen!</button>
```

**CSS:**

```
h1:fullscreen {
    background: orange;
}
```

[**看示例：http://codepen.io/ricardozea/pen/ZQNZqy**](https://link.jianshu.com/?t=http://codepen.io/ricardozea/pen/ZQNZqy)

### 伪元素

如前所述，伪元素类似一种虚拟元素，可以将其视为普通的HTML元素。但伪元素并不存在于文档树或DOM中，因此不能在HTML中输入，只能通过CSS创建。

同样，双冒号(`::`)与单冒号(`:`)也只是CSS3与CSS2.1的不同。

#### ::BEFORE/:BEFORE

`:before`伪元素与`:after`类似，都可以为其他HTML元素添加内容（文本或图形）。同样，这里的内容并不实际存在于DOM中，但可以像存在一样操作它们。需要在CSS中声明`content`属性。

记住，通过这个伪元素生成的内容不能通过其他选择符选中。

**HTML:**

```
<h1>Ricardo</h1>
```

**CSS:**

```
h1:before {
    content: "Hello "; /* 注意Hello后面有一个空格 */
}
```

结果网页中会变成这样：

```
Hello Ricardo!
```

**注意：** 看到“Hello ”后面的空格了吗？没错，空格也算数。

#### ::AFTER/:AFTER

`:after`伪元素也用于为其他HTML元素添加内容（文本或图形）。这里的内容并不实际存在于DOM中，但可以像存在一样操作它们。为了使用这个伪元素，必须在CSS中声明`content`属性。

同样，通过这个伪元素添加的任何内容都无法通过其他选择符选中。

**HTML:**

```
<h1>Ricardo</h1>
```

**CSS:**

```
h1:after {
    content: ", Web Designer!";
}
```

结果如下：

```
Ricardo, Web Designer!
```

#### ::BACKDROP (EXPERIMENTAL)

`::backdrop`伪元素是在全屏元素后面生成的一个盒子，与`:fullscreen`伪类连用，修改全屏后元素的背景颜色。

**注意：** `::backdrop`伪元素必须用双冒号。

还看前面`:fullscreen`伪类的例子。

**HTML:**

```
<h1 id="element">This heading will have a solid background color in full-screen mode.</h1>
<button onclick="var el = document.getElementById('element'); el.webkitRequestFullscreen();">Trigger full screen!</button>
```

**CSS:**

```
h1:fullscreen::backdrop {
    background: orange;
}
```

[**看示例：http://codepen.io/ricardozea/pen/bEPEPE**](https://link.jianshu.com/?t=http://codepen.io/ricardozea/pen/bEPEPE)

#### ::FIRST-LETTER/:FIRST-LETTER

`:first-letter`伪元素选择一行文本第一个字符。

如果相应行前面包含图片、视频或表格元素，那么不会影响选择第一个字符。

这个伪元素非常适合对段落进行排版，有了它就不必用图片或其他技巧了。

**提示：** 这个伪元素也可以选中`:before`伪元素生成的第一个字符。

**CSS:**

```
h1:first-letter  {
    font-size: 5em;
}
```

#### ::FIRST-LINE/:FIRST-LINE

`:first-line`选择元素的第一行，只适用于块级元素，行内元素不适用。

即使一段文本有多行，也会选中第一行。

**CSS:**

```
p:first-line {
    background: orange;
}
```

#### ::SELECTION

`::selection`选择文档中被高亮选中的部分。

注意，基于Gecko的浏览器要求使用前面：`::-moz-selection`。

**注意：** 在一条规则中同时使用带前缀和不还前缀的`::selection`是不行的，要分别写。

**CSS:**

```
::-moz-selection {
    color: orange;
    background: #333;
}

::selection  {
    color: orange;
    background: #333;
}
```

#### ::PLACEHOLDER (EXPERIMENTAL)

`::placeholder`伪元素选择表单元素中通过`placeholder`属性设置的占位文本。

也可以写成`::input-placeholder`。

**注意：** 这个伪元素不是标准的，因此将来有可能会变化。

在某些浏览器（IE10及Firefox 18之前）中，`::placeholder`伪元素的实现类似一个伪类。其他浏览器都将其视为伪元素。因此，除非要兼容IE10或旧版本的Firefox浏览器，因此应该这样写：

**HTML:**

```
<input type="email" placeholder="name@domain.com">
```

**CSS:**

```
input::-moz-placeholder {
    color:#666;
}

input::-webkit-input-placeholder {
    color:#666;
}

/* IE 10 only */
input:-ms-input-placeholder {
    color:#666;
}

/* Firefox 18 and below */
input:-moz-input-placeholder {
    color:#666;
}
```

### 小结

CSS伪类和伪元素相当有用，对不？这些伪类和伪元素提供了丰富的选择便利。

不要光看，自己动手试一试吧。广受支持的伪类和伪元素是很靠谱的。

希望大家看了这篇长文能有所收获。别忘了收藏它！





https://www.jianshu.com/p/9086114e07d4