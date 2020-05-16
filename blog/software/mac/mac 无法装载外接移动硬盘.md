# mac 无法装载外接移动硬盘

2019.10.17 11:31:18字数 227阅读 2,860

2019.10.17

> 电脑：MacBook Pro 2012 年中款
> 移动硬盘：西数 500G 机械硬盘

> 原因：没有推出成功就直接拔了硬盘连接线，再连接时用磁盘工具发现盘符为灰色，显示未装载。

**解决方式：**
磁盘工具修复、抹除、急救、装载全部失败。
使用命令行 `diskutil mount / unmount` 等也全部失败。
最后使用 `sudo lsof | grep disk2` 发现 disk2(我的移动硬盘的设备名，可以用磁盘工具看到) 下有几个进程

```undefined
fsck_hfs  2352               root    4u      CHR               1,13 0x8eb78000        763 /dev/rdisk2s1
fsck_hfs  2352               root    5u      CHR               1,13 0x8eb78000        763 /dev/rdisk2s1
```

然后使用下面的命令杀掉这些进程
`sudo pkill -f fsck_hfs`

之后可以看到磁盘工具中盘符已经不是灰色了。
如果不起作用，可以尝试重新连接移动硬盘再重复上面的解决方法。我是试了第二次才解决的。





https://www.jianshu.com/p/26771be572be