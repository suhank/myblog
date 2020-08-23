

# csv用excel打开中文乱码

## 问题

文本存在中文字符所以出现了奇怪的问题。

 1.首先是初次写入csv文件后，用excel打开文件中文字符显示乱码 解决办法是将csv文件用txt打开后另存格式为ansi格式，再次用excel打开，中文字符正常显示；

2.其次是之后继续通过unity往csv文件中写入中文字符时，用excel打开csv文件发现后续写的中文字符依然显示乱码

解决办法是网csv文件写入内容前将txt打开后又重新另存格式为utf-8格式，此后再写中文字符用excel打开也不会显示乱码；





## 总结：

当需要写 csv文件，并且防止用excel打开csv文件中文乱码的情况发生时，建议首先将csv文件用txt打开另存为ansi格式，然后再另存为utf-8格式，此后无论何时写入csv文件中文都不会乱码。





https://blog.csdn.net/SunnyInCSDN/article/details/77435779