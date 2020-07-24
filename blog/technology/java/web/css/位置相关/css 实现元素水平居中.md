[TOC]



# css 实现元素水平居中

元素主要分为块级元素和行内元素，所以对元素进行水平居中也分这两种情况来讨论，另外块级元素的实现比较复杂，将分情况讨论。

## 一、行内元素

常用行内元素为`a`/`img`/`input`/`span` 等，标签内的HTML文本也属于此类。对于此类情况，水平居中是通过给父元素设置 `text-align:center`来实现的。
HTML结构：

```
<body>
  <div class="txtCenter">
    Hello World!!!
  </div>
</body>
```

CSS样式：

```
<style>
  div.txtCenter{
    text-align:center;
  }
</style>
```

## 二、块级元素

常用块级元素为`div`/`table`/`ul`/`dl`/`form`/`h1`/`p`等。根据应用场景不同又分为定宽块级与不定宽块级两种情况，分别讨论。

### 1.定宽块级元素

满足**定宽**和**块状**两个条件的元素是可以通过设置**“左右`margin`”值为“`auto`”**来实现居中的。
HTML结构：

```
<body>
  <div>
    Hello World!!!
  </div>
</body>
```

CSS样式：

```
<style>
  div{
    border:1px solid red;/*为了显示居中效果明显为 div 设置了边框*/
    width:500px;/*定宽*/
    margin:20px auto;/* margin-left 与 margin-right 设置为 auto */
  }
</style>
```

### 2.不定宽块级元素

我们经常会遇到不定宽度块级元素的使用，如分页导航，因为分页的数目不定，所以不能用宽度限制住。此时对元素进行水平居中主要有三种方式：

- 加入 `table` 标签
- 设置 `display;inline` 方法
- 设置 `position:relative` 和 `left:50%;`

#### 2.1加入 `table` 标签

第一步：为需要设置的居中的元素外面加入一个 table 标签 ( 包括 `<tbody>`、`<tr>`、`<td>` )。

第二步：为这个 `table` 设置“左右 `margin:auto`”（这个和定宽块状元素的方法一样）。
HTML结构：

```html
<div>
  <table>
    <tbody>
      <tr><td>
        <ul>
          <li><a href="#">1</a></li>
          <li><a href="#">2</a></li>
          <li><a href="#">3</a></li>
        </ul>
      </td></tr>
    </tbody>
  </table>
</div>
```

CSS样式：

```
<style>
  table{
    margin:0 auto;
  }
  ul{list-style:none;margin:0;padding:0;}
  li{float:left;display:inline;margin-right:8px;}
</style>
```

这种方法的缺点是增加了无语义的HTML标签，增加了嵌套深度

#### 2.2设置 `display;inline` 方法

改变块级元素的 `dispaly` 为 `inline` 类型，然后使用 `text-align:center` 来实现居中效果。
HTML结构：

```html
<body>
  <div class="container">
    <ul>
      <li><a href="#">1</a></li>
      <li><a href="#">2</a></li>
      <li><a href="#">3</a></li>
    </ul>
  </div>
</body>
```

CSS样式：

```css
<style>
  .container{
    text-align:center;
  }
  .container ul{
    list-style:none;
    margin:0;
    padding:0;
    display:inline;
  }
  .container li{
    margin-right:8px;
    display:inline;
  }
</style>
```

**这种方法的缺点是将块级元素的display设置为inline，于是少了很多功能，比如盒子模型**

#### 2.3设置 `position:relative` 和 `left:50%;`

通过给父元素设置 `float`，然后给父元素设置 `position:relative` 和 `left:50%`，子元素设置 `position:relative` 和 `left:-50%` 来实现水平居中。
HTML结构：

```html
<body>
  <div class="container">
    <ul>
      <li><a href="#">1</a></li>
      <li><a href="#">2</a></li>
      <li><a href="#">3</a></li>
    </ul>
  </div>
</body>
```

CSS样式：

```css
<style>
  .container{
    float:left;
    position:relative;
    left:50%
  }
  .container ul{
    list-style:none;
    margin:0;
    padding:0;
    position:relative;
    left:-50%;
  }
  .container li{float:left;display:inline;margin-right:8px;}
</style>
```

**这种方法可以保留块状元素仍以 display:block 的形式显示，优点不添加无语议表标签，不增加嵌套深度，但它的缺点是设置了 position:relative，带来了一定的副作用。**

三种方式各有利弊，根据实际情况相应选用。





https://segmentfault.com/a/1190000003110179#articleHeader2