# mac制作iso文件

转载[tzjvon](https://me.csdn.net/tzjvon) 发布于2016-01-31 14:54:12 阅读数 1679 收藏



运行磁盘工具，选择文件，新建，文件夹的磁盘映像

选择要做成ISO的文件夹，点击映像。

映像格式选择“DVD/CD主映像”，点击存储后会生成一个cdr文件。(放到屋子文件下)

打开终端 把cdr转换成iso

```
hdiutil makehybrid -iso -joliet -o win8.iso win8.cdr
```



https://blog.csdn.net/tzjvon/article/details/50614700