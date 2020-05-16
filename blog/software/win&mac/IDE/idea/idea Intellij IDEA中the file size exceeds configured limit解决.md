# idea Intellij IDEA中the file size exceeds configured limit解决

2017.11.24 10:18 字数 86 阅读 9评论 0喜欢 0

工作中用到个项目代码主执行文件代码高达62997行，IDEA报file size exceeds configured limit错误。

解决办法如下：

修改intellij IDE安装目录下的bin/idea.properties, 将其中的idea.max.intellisense.filesize=2500

改成大一些，比如：idea.max.intellisense.filesize=9900

然后重启问题解决。



https://www.jianshu.com/p/0bcf3ce4c9ae