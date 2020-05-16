[TOC]



# FormData用法详解 文件上传模拟form表单提交

## 1. 概述

FormData类型其实是在XMLHttpRequest 2级定义的，它是为序列化表以及创建与表单格式相同的数据（当然是用于XHR传输）提供便利。

FormData对象就模拟一个原始的表单格式的数据，以前上传文件非得要用个form包起来，就是和后台约定的一个传输数据格式，

FormData就是按照规定的格式，把form中所有表单元素的name与value组装成一个queryString，省去你手工拼接的工作，如果用过jquery的话，应该知道有个表单序列号的函数，作用和它是一样的，不过FormData还提供了更多的操作方法，全部在原型中，自己本身没任何的属性及方法。

## 2. 创建一个formData对象实例的方式

创建一个formData对象实例有几种方式

1、创建一个`空对象`实例

```
var formData = new FormData();
```

此时可以调用append()方法来添加数据

2、使用已有的表单来初始化一个对象实例

假如现在页面已经有一个表单

```
<form id="myForm" action="" method="post">
    <input type="text" name="name">名字
    <input type="password" name="psw">密码
    <input type="submit" value="提交">
</form>
```

我们可以使用这个表单元素作为初始化参数，来实例化一个formData对象

```
// 获取页面已有的一个form表单
var form = document.getElementById("myForm");
// 用表单来初始化
var formData = new FormData(form);
// 我们可以根据name来访问表单中的字段
var name = formData.get("name"); // 获取名字
var psw = formData.get("psw"); // 获取密码
// 当然也可以在此基础上，添加其他数据
formData.append("token","kshdfiwi3rh");
```

## 3. 操作方法

首先，我们要明确formData里面存储的数据形式，一对key/value组成一条数据，key是唯一的，一个key可能对应多个value。如果是使用表单初始化，每一个表单字段对应一条数据，它们的HTML name属性即为key值，它们value属性对应value值。

| key  | value      |
| ---- | ---------- |
| k1   | [v1,v2,v3] |
| k2   | v4         |

### 3.1 获取值

我们可以通过get(key)/getAll(key)来获取对应的value，

```
formData.get("name"); // 获取key为name的第一个值
formData.get("name"); // 返回一个数组，获取key为name的所有值
```

### 3.2 添加数据

我们可以通过append(key, value)来添加数据，如果指定的key不存在则会新增一条数据，如果key存在，则添加到数据的末尾

```
formData.append("k1", "v1");
formData.append("k1", "v2");
formData.append("k1", "v1");

formData.get("k1"); // "v1"
formData.getAll("k1"); // ["v1","v2","v1"]
```

### 3.3 设置修改数据

我们可以通过set(key, value)来设置修改数据，如果指定的key不存在则会新增一条，如果存在，则会修改对应的value值。

```
formData.append("k1", "v1");
formData.set("k1", "1");
formData.getAll("k1"); // ["1"]
```

### 3.4 判断是否该数据

我们可以通过has(key)来判断是否对应的key值

```
formData.append("k1", "v1");
formData.append("k2",null);

formData.has("k1"); // true
formData.has("k2"); // true
formData.has("k3"); // false
```

### 3.5 删除数据

通过delete(key)，来删除数据

```
formData.append("k1", "v1");
formData.append("k1", "v2");
formData.append("k1", "v1");
formData.delete("k1");

formData.getAll("k1"); // []
```

### 3.6 遍历

我们可以通过entries()来获取一个迭代器，然后遍历所有的数据，

```js
formData.append("k1", "v1");
formData.append("k1", "v2");
formData.append("k2", "v1");

var i = formData.entries();

i.next(); // {done:false, value:["k1", "v1"]}
i.next(); // {done:fase, value:["k1", "v2"]}
i.next(); // {done:fase, value:["k2", "v1"]}
i.next(); // {done:true, value:undefined}

//遍历
var formDatas = formData.entries()
while (true) {
    let iteratorResult = formDatas.next()
    if (iteratorResult.done) {
        break
    }
    console.log(iteratorResult.value)
}
```

可以看到返回迭代器的规则

1. 每调用一次next()返回一条数据，数据的顺序由添加的顺序决定
2. 返回的是一个对象，当其done属性为true时，说明已经遍历完所有的数据，这个也可以作为判断的依据
3. 返回的对象的value属性以数组形式存储了一对key/value，数组下标0为key，下标1为value，如果一个key值对应多个value，会变成多对key/value返回

我们也可以通过values()方法只获取value值

```javascript
formData.append("k1", "v1");
formData.append("k1", "v2");
formData.append("k2", "v1");

var i = formData.entries();

i.next(); // {done:false, value:"v1"}
i.next(); // {done:fase, value:"v2"}
i.next(); // {done:fase, value:"v1"}
i.next(); // {done:true, value:undefined}
```

## 4. 原生JavaScript实例

我们可以通过xhr来发送数据

```javascript
var xhr = new XMLHttpRequest();
xhr.open("post",url);
xhr.send(formData);
xhr.onreadystatechange = function () {
    if (xhr.readyState == 4) {
        if (xhr.status == 200 || xhr.status == 304) {
            console.log( xhr.responseText)
        }
    }
};
```

这种方式可以来实现文件的异步上传。

## 5. JQuery实例

```js
//添加数据方式见上二。
//processData: false, contentType: false,多用来处理异步上传二进制文件。
 $.ajax({
    url: 'xxx',
    type: 'POST',
    data: formData,                    // 上传formdata封装的数据
    dataType: 'JSON',
    cache: false,                      // 不缓存
    processData: false,                // jQuery不要去处理发送的数据
    contentType: false,                // jQuery不要去设置Content-Type请求头
    success:function (data) {           //成功回调
        console.log(data);
    }
});
```

**附：**

```js
/**
 * 将以base64的图片url数据转换为Blob文件格式
 * @param urlData 用url方式表示的base64图片
 */
function convertBase64UrlToBlob(urlData) {
    var bytes = window.atob(urlData.split(',')[1]); //去掉url的头，并转换为byte
    //处理异常,将ascii码小于0的转换为大于0
    var ab = new ArrayBuffer(bytes.length);
    var ia = new Uint8Array(ab);
    for(var i = 0; i < bytes.length; i++) {
        ia[i] = bytes.charCodeAt(i);
    }
    return new Blob([ab], {
        type: 'image/png'
    });
}
```

## 参考

1. [兼容性查询](http://caniuse.com/#search=formdata)
2. [MDN](https://developer.mozilla.org/en-US/docs/Web/API/FormData)
3. 《JavaScript高级程序设计》





https://blog.csdn.net/zqian1994/article/details/79635413