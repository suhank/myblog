# Node.js 中文乱码解决

 Node.js 支持中文不太好（实际上是Javascript支持），见《Node.js开发指南》。
要想Node.js正常显示中文，需要两点：
1、js文件保存为unicode格式。js文件是否为unicode格式，一个简单的方法是使用记事本来判断。使用记事本打开JS文件，点菜单另存为，看编码格式是否为"UTF-8"。若不是，可使用UltraEdit工具进行转换，使用记事本也可以转换。
2、在js文件中增加编码说明meta数据，让浏览器知道使用什么编码来解释网页。

两个条件缺一不可。

一个Node.js中使用中文的例子如下，该app.js需保存为utf-8格式，同时在文中增加meta编码数据说明：

```
   <meta charset="utf-8"/>  
```

   

```
//app.js   
  
var http = require('http');  
  
http.createServer(function(req, res) {  
  res.writeHead(200, {'Content-Type': 'text/html'});  
  res.write('<head><meta charset="utf-8"/></head>');  
  res.write('<h1>Node.js</h1>');  
  res.write('<b>亲爱的，你慢慢飞，小心前面带刺的玫瑰...</b>');  
  res.end('<p>Hello World</p>');  
    
}).listen(3000);  
  
console.log("HTTP server is listening at port 3000.");  
```

 



**《Node.js开发指南》节选：**
        Node.js 不支持完整的Unicode，很多字符无法用string 表示。公平地说这不是Node.js 的缺陷，而是JavaScript 标准的问题。目前JavaScript 支持的字符集还是双字节的UCS2，即用两个字节来表示一个Unicode 字符，这样能表示的字符数量是65536。显然，仅仅是汉字就不止这个数目，很多生僻汉字，以及一些较为罕见语言的文字都无法表示。这其实是一个历史遗留问题，像2000 年问题（俗称千年虫）一样，都起源于当时人们的主观判断。最早的Unicode 设计者认为65536个字符足以囊括全世界所有的文字了，因此那个时候盲目兼容Unicode 的系统或平台（如Windows、Java 和JavaScript）在后来都遇到了问题。 
        Unicode 随后意识到2个字节是不够的，因此推出了UCS4，即用4 个字节来表示一个Unicode 字符。很多原先用定长编码的UCS2 的系统都升级为了变长编码的UTF-16，因为只有它向下兼容UCS2。UTF-16 对UCS2 以内的字符采用定长的双字节编码，而对它以外的部分使用多字节的变长编码。这种方式的好处是在绝大多数情况下它都是定长的编码，有利于提高运算效率，而且兼容了UCS2，但缺点是它本质还是变长编码，程序中处理多少有些不便。
        许多号称支持UTF-16 的平台仍然只支持它的子集UCS2，而不支持它的变长编码部分。相比之下，UTF-8 完全是变长编码，有利于传输，而UTF-32 或UCS4 则是4 字节的定长编码，有利于计算。
        当下的JavaScript 内部支持的仍是定长的UCS2 而不是变长的UTF-16，因此对于处理UCS4 的字符它无能为力。所有的JavaScript 引擎都被迫保留了这个缺陷，包括V8 在内，因此你无法使用Node.js 处理罕见的字符。想用Node.js 实现一个多语言的字典工具？还是算了吧，除非你放弃使用string 数据类型，把所有的字符当作二进制的Buffer 数据来处理。



https://blog.csdn.net/hongweigg/article/details/8760372