

[TOC]



# css样式覆盖顺序 优先级

## 0.样式位置分类

1）使用外部css文件

```
<link href="/static/css/main.css" rel="stylesheet">
```

2）使用head中的style元素

```
<head>
<style type="text/css">
.main{color:red;}
</style>
</head>
```

3）使用元素上的style

```
<body style="color:red;"></body>
```

## 1.样式覆盖总体原则：

**元素上的style > 文件头上的style元素 >外部样式文件**


不同级别的样式均对同一元素进行渲染时，对于冲突的样式，会优先采取元素上的style去覆盖文件头上的style元素；对于不冲突的样式，会进行样式叠加。

例如：

```
<head>
<style type="text/css">
.main{color:red;background-color:yellow;}
</style>
</head>
<body id="mainbody" class="main" style="color:green;"></body>
```

对于body元素，元素style与文件头上的style均会命中，此时，backgroud-color样式会进行叠加，color样式出现冲突，会优先选用body元素中的样式。渲染结果为：

```
color:green;
background-color:yellow;
```

## 2.同级别样式文件下：


1）样式表的元素选择器选择越精确，样式优先级越高：


id选择器指定的样式 > 类选择器指定的样式 > 元素类型选择器指定的样式
在示例中，css进行渲染时，#mainbody > .main > body


2）对于相同类型选择器制定的样式，在样式表文件中，越靠后的优先级越高：


这里是样式表文件中越靠后的优先级越高，而不是在元素class出现的顺序。

例如，.class2 在样式表中出现在.class1之后，

```
.class1{color:red;}
.class2{color:green;}
```

对于

```
<div class="class2 class1">
<div class="class1 class2">
```

虽然class1在元素中指定时排在class2的后面，但因为在样式表文件中class1处于class2前面，此时仍然是class2的优先级更高，将采用 color:green

3）如果要让某个样式的优先级变高，可以使用!important

```
<head>
<style type="text/css">
.main{color:red !important; background-color:yellow;}
</style>
</head>
<body id="mainbody" class="main" style="color:green;"></body>
```

由于head的style中使用了 !important ，渲染时将会采用 color:red



http://leettest.com/blog/24/