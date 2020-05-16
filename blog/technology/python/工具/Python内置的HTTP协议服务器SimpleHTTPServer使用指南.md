# Python内置的HTTP协议服务器SimpleHTTPServer使用指南

这篇文章主要介绍了Python内置的HTTP协议服务器SimpleHTTPServer使用指南,SimpleHTTPServer本身的功能十分简单,文中介绍了需要的朋友可以参考下

首先确保装了Python，我装的是2.x版本，对了，我的操作系统是WIN7，其实对于Python来说，什么操作系统并不重要。Python内置了一个简单的HTTP服务器，只需要在命令行下面敲一行命令，一个HTTP服务器就起来了：

```python
python -m SimpleHTTPServer 80
```

后面的80端口是可选的，不填会采用缺省端口8000。注意，这会将当前所在的文件夹设置为默认的Web目录，试着在浏览器敲入本机地址：

http://localhost:80
如果当前文件夹有index.html文件，会默认显示该文件，否则，会以文件列表的形式显示目录下所有文件。这样已经实现了最基本的文件分享的目的，你可以做成一个脚本，再建立一个快捷方式，就可以很方便的启动文件分享了。如果有更多需求，完全可以根据自己需要定制，具体的请参见[官方文档SimpleHTTPServer](https://docs.python.org/2/library/simplehttpserver.html)，或者直接看源码。我拷贝一段，方便参考：

```python
import SimpleHTTPServer
import SocketServer
 
PORT = 8000
 
Handler = SimpleHTTPServer.SimpleHTTPRequestHandler
 
httpd = SocketServer.TCPServer(("", PORT), Handler)
 
print "serving at port", PORT
httpd.serve_forever()
```



如果你想改变端口号，你可以使用如下的命令： 

```python
python -m SimpleHTTPServer 8080
```

如果你只想让这个HTTP服务器服务于本地环境，那么，你需要定制一下你的Python的程序，下面是一个示例：

```python
import sys 
import BaseHTTPServer 
from SimpleHTTPServer import SimpleHTTPRequestHandler 
HandlerClass = SimpleHTTPRequestHandler 
ServerClass = BaseHTTPServer.HTTPServer 
Protocol = "HTTP/1.0"
  
if sys.argv[1:]: 
  port = int(sys.argv[1]) 
else: 
  port = 8000
server_address = ('127.0.0.1', port) 
  
HandlerClass.protocol_version = Protocol 
httpd = ServerClass(server_address, HandlerClass) 
  
sa = httpd.socket.getsockname() 
print "Serving HTTP on", sa[0], "port", sa[1], "..."
httpd.serve_forever()
```



注意：所有的这些东西都可以在 Windows 或 Cygwin 下工作。





http://blog.51cto.com/tenderrain/1980603