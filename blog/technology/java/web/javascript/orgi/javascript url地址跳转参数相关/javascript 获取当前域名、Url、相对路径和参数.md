# javascript 获取当前域名、Url、相对路径和参数

### 一、js获取当前域名有2种方法

```
var domain = document.domain;
var domain = window.location.host;
```

 

### 二、获取当前Url的4种方法

```
var url = window.location.href;
var url = self.location.href;
var url = document.URL;
var url = document.location;
```

 

### 三、获取当前相对路径的方法

```js
function GetUrlRelativePath() {
    var url = document.location.toString();
    var arrUrl = url.split("//");

    var start = arrUrl[1].indexOf("/");
    var relUrl = arrUrl[1].substring(start);//stop省略，截取从start开始到结尾的所有字符

    if (relUrl.indexOf("?") != -1) {
        relUrl = relUrl.split("?")[0];
    }
    return relUrl;
}
```

 

### 四、获取当前Url参数的方法

```js
function GetUrlPara() {
    var url = document.location.toString();
    var arrUrl = url.split("?");

    var para = arrUrl[1];
    return para;
}
```

 



<https://www.cnblogs.com/ajk4/articles/6054410.html>