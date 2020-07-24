# white-space详解

white-space共有5种属性normal，nowrap，pre，pre-wrap，pre-line

网上的解释多半过于详细冗长，先做个简化处理,以便查询

normal    忽略空白  过长换行

nowrap   忽略空白  绝不换行  

pre         保留空白  无视限制 

pre-wrap 保留空白  过长换行 

pre-line   忽略空白  保留换行

实例如下:

```html
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>testWhiteSpace</title>
    <style>
        /*pre代表保留换行   wrap代表保留空白 结合p本身性质*/
        .a {
            width: 200px;
            background-color: #0bd318;
        }
        .b1 {
            /*默认 忽略空白符 仅保留一小段  保障外部限制的前提下换行 被动换行 过长溢出*/
            white-space: normal;
        }
        .b2 {
            /*忽略空白符 保留一小段 绝对不换行 无论外部限制 除非遇见<br/>*/
            white-space: nowrap;
        }
        .b3 {
            /*保留所有空白 无视外部限制*/
            white-space: pre;
        }
        .b4 {
            /*保留空白 根据外部限制换行*/
            white-space: pre-wrap;
        }
        .b5 {
            /*忽略空白  保留换行*/
            white-space: pre-line;
        }
    </style>
    <script src="js/angular.js"></script>
</head>
<body>
<div class="a">4
    <p class="b1">b1testtestetst          testtestetst
        testtestetst          testtestetst
        testtestetst          testtestetst
        testtestetst          testtestetst
        testtestetst          testtestetst
        testtestetst          testtestetst
    </p>
</div>
<div class="a">
    <p class="b2">b2testtestetst          testtestetst
        testtestetst          testtestetst
        testtestetst          testtestetst
        testtestetst          testtestetst
        testtestetst          testtestetst
        testtestetst          testtestetst
    </p>
</div>
<div class="a">
    <p class="b3">b3testtestetst          testtestetst
        testtestetst          testtestetst
        testtestetst          testtestetst
        testtestetst          testtestetst
        testtestetst          testtestetst
        testtestetst          testtestetst
    </p>
</div>
<div class="a">
    <p class="b4">b4testtestetst          testtestetst
        testtestetst          testtestetst
        testtestetst          testtestetst
        testtestetst          testtestetst
        testtestetst          testtestetst
        testtestetst          testtestetst
    </p>
</div>
<div class="a">
    <p class="b5">b5te
        sttestetst          testtestetst
        testtestetst          testtestetst
        testtestetst          testtestetst
        testtestetst          testtestetst
        testtestetst          testtestetst
        testtestetst          testtestetst1111111111111111111111111111111111111111111111
    </p>
</div>
</body>
</html>
```

 https://www.cnblogs.com/yanze/p/5973728.html

 