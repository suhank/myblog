# Node.js 的 require.resolve 简介

 

简单的说, 在 Node.js 中使用 fs 读取文件的时候, 经常碰到要拼一个文件的绝对路径的问题 (fs 处理相对路径均以进程执行目录为准). 之前一直的方法都是, 使用 **path** 模块以及 **__dirname** 变量 :

```
var path=require('path');
var fs=require('fs');


fs.readFileSync(path.join(__dirname, './assets/some-file.txt'));
```

使用 **require.resolve** 可以简化这一过程:

```
var path=require('path');
var fs=require('fs');


fs.readFileSync(require.resolve('./assets/some-file.txt'));
```

此外, **require.resolve** 还会在拼接好路径之后检查该路径是否存在, 如果 resolve 的目标路径不存在, 就会抛出 `Cannot find module './some-file.txt'` 的异常. 省略了一道检查文件是否存在的工序 (fs.exists).

这个报错并不会加重你的检查负担, 毕竟使用 fs 去操作文件时, 如果发现文件不存在也会抛出异常. 反之, 通过 **require.resovle** 可以在提前在文件中作为常量定义, 那么在应用启动时就可以抛异常, 而不是等到具体操作文件的时候才抛异常.



https://lellansin.wordpress.com/2017/04/22/node-js-%E7%9A%84-require-resolve-%E7%AE%80%E4%BB%8B/