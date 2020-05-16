# nginx会去掉带有下划线的Header键值

2017年05月10日 12:03:01 [Shower稻草人](https://me.csdn.net/u013474436) 阅读数：5824

  

在本地可以获取前端header传的参数，但是部署到服务器获取的就是null（服务器地址用nginx做了代理）

原因： 
nginx对header name的字符做了限制，默认 underscores_in_headers 为off，表示如果header name中包含下划线，则忽略掉，部署后就获取不到。

解决：

1. 在header里不要用 “_” 下划线，可以用驼峰命名或者其他的符号（如减号-）代替。nginx默认忽略掉下划线可能有些原因。
2. 在nginx里的 nginx.conf文件中配置http的部分添加 ： underscores_in_headers on;（默认值是off）



<https://blog.csdn.net/u013474436/article/details/71516649>

