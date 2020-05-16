# linux使用vi中文乱码的解决办法

2008年08月26日 09:57:00

阅读数：9809

linux使用vi中文乱码的解决办法 在~/.vimrc文件中添加如下两行即：
set encoding=utf-8
set fileencoding=utf-8
因为在secureCRT里面也是 utf-8的。



https://blog.csdn.net/poson/article/details/2831030