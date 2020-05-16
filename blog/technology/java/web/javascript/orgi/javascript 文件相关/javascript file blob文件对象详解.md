[TOC]



# JavaScript file blob文件对象详解

## 前言

在浏览器中操作文件，多数情况下用到的是 `File` 对象，从 `<input type='file' />` 元素获取，进而继续操作(例如将选择的图片展示在页面上，用ajax将文件上传至服务器等)。这里介绍在浏览器中操作文件的相关API.

`File` 对象继承自 `Blob` 对象，先看看 `Blob` 对象。

## 1. Blob 对象

### Blob构造函数

`Blob` 对象表示一个不可变、原始数据的类文件对象。Blob 表示的不一定是JavaScript原生格式的数据。

Blob构造函数 Blob(array[, options])

- array 是一个由ArrayBuffer, ArrayBufferView, Blob, string 等对象构成的 Array ，或者其他类似对象的混合体，它将会被放进 Blob。string会被编码为UTF-8。
- options 是一个可选的对象，它可能会指定如下两个属性：

1. type，默认值为 ""，它代表了将会被放入到blob中的数组内容的`ref="https://www.iana.org/assignments/media-types/media-types.xhtml">MIME类型。`
2. endings，默认值为"transparent"，用于指定包含行结束符\n的字符串如何被写入。 它是以下两个值中的一个： "native"，代表行结束符会被更改为适合宿主操作系统文件系统的换行符，或者 "transparent"，代表会保持blob中保存的结束符不变。

示例:

```js
var content1 = ['This is my firt trip to an island'];
var blob1 = new Blob(content, {type: 'text/plain'});
var content2 = {name: 'Alice', age: 23};
var blob2 = new Blob([JSON.stringify(content2, null, 2)], {type: 'application/json'});
```

### Blob实例属性

- size 只读 `Blob` 对象中所包含数据的大小（字节）。
- type 只读 一个字符串，表明该Blob对象所包含数据的`ref="https://www.iana.org/assignments/media-types/media-types.xhtml">MIME类型。如果类型未知，则该值为空字符串。例如 "image/png".`

示例:

```js
var content = ['<div id="box"><p class="pra">a paragraph</p></div>'];
var blob = new Blob(content, {type: 'text/html'});
console.log(blob.size); // 50
console.log(blob.type); // text/html
```

### Blob实例方法

- slice([start[, end[, contentType]]])

`slice` 方法接收三个可选参数，`start` 和 `end` 都是数值，表示截取的范围，`contentType`指定截取的内容的 `MIME` 类型。返回一个新的 `Blob`对象。

```js
var blob = new Blob(['This is an example of Blob slice method'], {type: 'text/plain'});
console.log(blob.size); // 39
var newBlob = blob.slice(10, 20, 'text/plain');
console.log(newBlob.size); // 10
```

从 `Blob` 对象中读取内容可以使用 `FileReader`. 下文会介绍。

## 2. File 对象

### File构造函数

我们接触的多数关于 `File` 的操作都是读取，js也为我们提供了手动创建 `File` 对象的构造函数：`File(bits, name[, options])`。

- bits (required)
  ArrayBuffer，ArrayBufferView，Blob，或者 Array[string] — 或者任何这些对象的组合。这是 UTF-8 编码的文件内容。。
- name [String] (required)
  文件名称，或者文件路径.
- options [Object] (optional)
  选项对象，包含文件的可选属性。可用的选项如下：
- `type`: string, 表示将要放到文件中的内容的`ref="https://www.iana.org/assignments/media-types/media-types.xhtml">MIME类型。默认值为 '' 。`
- `lastModified`: 数值，表示文件最后修改时间的 Unix 时间戳（毫秒）。默认值为 Date.now()。

示例：

```js
var file1 = new File(['text1', 'text2'], 'test.txt', {type: 'text/plain'});
```

根据已有的 `blob` 对象创建 `File` 对象:

```js
var file2 = new File([blob], 'test.png', {type: 'image/png'});
```

### File实例属性

`File` 对象的实例内容不可见，但是有以下属性可以访问:

- name 只读 返回文件的名称.由于安全原因,返回的值并不包含文件路径 。
- type 只读 返回 `File` 对象所表示文件的媒体类型（MIME）。例如 PNG 图像是 "image/png".
- lastModified 只读 number, 返回所引用文件最后修改日期,自 1970年1月1日0:00 以来的毫秒数。
- lastModifiedDate 只读 Date, 返回当前文件的最后修改日期,如果无法获取到文件的最后修改日期,则使用当前日期来替代。

示例：

```html
<input type="file" id='file'>
```



```js
document.getElementById('file').addEventListener('change', function(event){
  const file = this.files[0];
  if (file) {
    console.log(file.name);
    console.log(file.size);
    console.log(file.lastModified);
    console.log(file.lastModifiedDate);
  }
});
```

**备注:** 基于当前的实现，浏览器不会实际读取文件的字节流，来判断它的媒体类型。它基于文件扩展来假设；将PNG 图像文件的后缀名重命名为 `.txt`，那么读取的该文件的 `type` 属性值为 "text/plain"， 而不是 "image/png" 。而且，`file.type` 仅仅对常见文件类型可靠。例如图像、文档、音频和视频。不常见的文件扩展名会返回空字符串。开发者最好不要依靠这个属性，作为唯一的验证方案。

### File实例方法

- slice([start[, end[, contentType]]])

`File` 对象没有定义额外的方法，由于继承了 `Blob` 对象，也就继承了 `slice`方法，用法同上文 `Blob` 的 `slice` 方法。

FileReader, URL.createObjectURL(), createImageBitmap(), 及 XMLHttpRequest.send() 都能处理 Blob 和 File。

## 3. FileReader 对象

> `FileReader` 对象允许Web应用程序异步读取存储在用户计算机上的文件（或原始数据缓冲区）的内容，使用 `File` 或 `Blob` 对象指定要读取的文件或数据。

其中 `File` 对象可以是来自用户在一个 `<input>` 元素上选择文件后返回的 `FileList`, 也可以来自拖放操作生成的 `DataTransfer` 对象,还可以是来自在一个 `HTMLCanvasElement` 上执行 `mozGetAsFile()` 方法后返回结果。

### FileReader构造函数

```
var reader = new FileReader()
```

构造函数不需要传入参数，返回一个 `FileReader` 的实例。`FileReader` 继承 `EventTarget`对象。

### FileReader实例属性

- error 只读`DOMException` 的实例，表示在读取文件时发生的错误
- result 只读 文件的内容，该属性仅在读取操作完成后(load)后才有效，格式取决于读取方法
- readyState 只读 表示读取文件时状态的数字

**备注:** `readeyState`的取值如下:

- 0 EMPTY 还没有加载任何数据
- 1 LOADING 数据正在被加载
- 2 DONE 已完成全部的读取请求.

使用示例：

```js
var reader = new FileReader();
console.log(reader.error);       // null
console.log(reader.result);      // null
console.log(reader.readyState);  // 0
console.log(reader.EMPTY);       // 0
console.log(reader.LOADING);     // 1
console.log(reader.DONE);        // 2
```

`EMPTY`、`LOADING`、`DONE` 这三个属性同时存在于 `FileReader` 和它的的原型对象上，因此实例上有这三个属性，`FileReader` 对象本身也有这三个属性:

```js
console.log(FileReader.EMPTY);   // 0
console.log(FileReader.LOADING); // 1
console.log(FileReader.DONE);    // 2
```

### FileReader事件

文件的读取是一个异步的过程，和 `XMLHttpRequest` 对象一样，在读取操作过程中会触发一系列事件。

- abort 读取操作被中断时触发。示例：`reader.onabort = function(event) {}`
- error 在读取操作发生错误时触发。示例：`reader.onerror = function(event) {}`
- load 读取操作完成时触发。示例：`reader.addEventListener('load', function(event) {})`
- loadstart 读取操作开始时触发。示例：`reader.onloadstart = function(event) {}`
- loadend 读取操作结束时（要么成功，要么失败）触发。示例： `reader.onloadend = function(event) {}`
- progress 在读取Blob时触发。示例：`reader.onprogress = function(event) {}`

### FileReader实例方法

`FileReader` 的实例具有以下可操作的方法:

- abort() 手动终止读取操作，只有当 `readyState` 为 1 时才能调用，调用后，`readyState` 值为 2 . 示例: `reader.abort()`
- readAsArrayBuffer(blob) 读取指定的 `Blob` 或 `File` 对象。读取操作完成后(触发`loadend`事件)，`result`属性将包含一个 `ArrayBuffer` 对象表示所读取的文件的数据。 示例：`reader.readAsArrayBuffer(blob)`
- readAsDataURL(blob) 读取指定的 `Blob` 或 `File` 对象。读取操作完成后(触发`loadend`事件)，`result`属性将包含一个 `data:URL` 格式的字符串(base64编码) 示例：`reader.readAsArrayBuffer(file)`
- readAsBinaryString(blob) 已废弃，用 `readAsArrayBuffer` 代替。
- readAsText(blob[, encoding]) 将 Blob 或者 File 对象转根据特殊的编码格式转化为内容(字符串形式), 默认编码是 `utf-8` 示例： `reader.readAsArrayBuffer(blob)`

读取本地图片示例:

```html
<input type="file" id='file' accept="image/png, image/jpg, image/jpeg, image/gif" />><br />>
<img src="" alt="Image preview...">
```



```js
var preview = document.querySelector('img');
var reader  = new FileReader();
reader.addEventListener("load", function () {
  preview.src = reader.result;
}, false);
document.getElementById('file').addEventListener('change', function (event) {
  var file = this.files[0];
  if (file) {
    reader.readAsDataURL(file);
  }
});
```

[读取多个文件示例 - CodePen](https://link.zhihu.com/?target=https%3A//codepen.io/mr-dang/pen/qKMrzV)

dataURL是`base64`编码的数据格式，展示类型为字符串，形如: `data:image/jpeg;base64,/9j/4QXERXhpZgAATU...`

将 `dataURL` 转为 `blob`对象:

```js
function dataURLToBlob (dataurl) {
  let arr = dataurl.split(',');
  let mime = arr[0].match(/:(.*?);/)[1];
  let bstr = atob(arr[1]);
  let n = bstr.length;
  let u8arr = new Uint8Array(n);
  while (n--) {
    u8arr[n] = bstr.charCodeAt(n);
  }
  return new Blob([u8arr], { type: mime });
}
```

结合上例，根据已有的 `<img>` 对象创建一个 `File` 对象:

```js
reader.addEventListener("load", function () {
  preview.src = reader.result;
  var blob = dataURLToBlob(reader.result);
  var newFile = new File([blob], 'test.jpeg', {type: blob.type});
  console.log(newFile.name); // test.jpeg
  console.log(newFile.type);
  console.log(newFile.size);
}, false);
```

URL.createObjectURL

将图片文件转换成 `data:URL` 格式供 `<img>` 元素展示，除了使用 `fileReader.readAsDataURL`外，还可以使用 `URL.createObjectURL`方法。
`URL.createObjectURL(blob)` 方法返回一个 `blob:` 开头的字符串，指向文件在内存中的地址。

```html
<input type="file" id='file' accept="image/png, image/jpg, image/jpeg, image/gif" /><br />
<img src="" alt="Image preview...">
```



```js
var preview = document.querySelector('img');
document.getElementById('file').addEventListener('change', function (event) {
  var file = this.files[0];
  if (file) {
    preview.src = URL.createObjectURL(file);
  }
});
```

## 参考链接

- [using files from web apps - MDN](https://link.zhihu.com/?target=https%3A//developer.mozilla.org/en-US/docs/Web/API/File/Using_files_from_web_applications)
- [Blob - MDN](https://link.zhihu.com/?target=https%3A//developer.mozilla.org/zh-CN/docs/Web/API/Blob)
- [MIME types - Wikipedia](https://link.zhihu.com/?target=https%3A//www.iana.org/assignments/media-types/media-types.xhtml)
- [MIME TYPES - MDN](https://link.zhihu.com/?target=https%3A//developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/MIME_types)
- [FileReader —— MDN](https://link.zhihu.com/?target=https%3A//developer.mozilla.org/zh-CN/docs/Web/API/FileReader)
- [MIME types - w3school](https://link.zhihu.com/?target=http%3A//www.w3school.com.cn/media/media_mimeref.asp)
- [Blob/DataURL/canvas/image的相互转换](https://link.zhihu.com/?target=https%3A//www.cnblogs.com/jyuf/p/7251591.html)



https://zhuanlan.zhihu.com/p/38550846