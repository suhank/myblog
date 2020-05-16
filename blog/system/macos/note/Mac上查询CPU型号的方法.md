# Mac上查询CPU型号的方法



在苹果的笔记本上，关于本机里面一般是看不到CPU的具体型号的。

可以使用命令来查询CPU具体型号：

```sh
sysctl -n machdep.cpu.brand_string
```

这里就显示出来了，我的MacBook Pro的CPU型号是i7-6820HQ





原文链接：https://blog.csdn.net/wddyzzw/article/details/68945295