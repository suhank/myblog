# CentOS /bin/bash^M: bad interpreter解决方法

Violet-Guo 2016-07-27 09:13:54  4476  收藏 2

我是在windows下保存了一个脚本文件，用ssh上传到centos，并执行。

但执行的时候出现了这句错误

```
/bin/bash^M: bad interpreter
```


网上找了资料才知道

如果这个脚本在Windows下编辑过，就有可能被转换成Windows下的dos文本格式了，这样的格式每一行的末尾都是以\r\n来标识，它的ASCII码分别是0x0D，0x0A。如果你将这个脚本文件直接放到Linux上执行就会报/bin/bash^M: bad interpreter错误提示。

解决方法很简单，首先你先要检查一下看看你的脚本文件是不是这个问题导致的，用vi命令打开要检查的脚本文件，然后用

```
:set ff?
```


命令检查一下，看看是不是dos字样，如果是dos格式的，则会显示下面的这个

![这里写图片描述](image-202007251851/20160727091310803)

然后执行

```
:set ff=unix
:qw
```

保存退出即可
 







版权声明：本文为CSDN博主「Violet-Guo」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
原文链接：https://blog.csdn.net/violet_echo_0908/java/article/details/52042137

