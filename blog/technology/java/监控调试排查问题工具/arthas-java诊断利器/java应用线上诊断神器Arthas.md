[TOC]



# java应用线上诊断神器Arthas

 

官网:https://github.com/alibaba/arthas/blob/master/README_CN.md

**前言碎语**

Arthas是阿里巴巴最近开源的一款在线诊断java应用程序的工具，是greys工具的升级版本，深受开发者喜爱。当你遇到以下类似问题而束手无策时，Arthas可以帮助你解决：

1. 这个类从哪个 jar 包加载的？为什么会报各种类相关的 Exception？
2. 我改的代码为什么没有执行到？难道是我没 commit？分支搞错了？
3. 遇到问题无法在线上 debug，难道只能通过加日志再重新发布吗？
4. 线上遇到某个用户的数据处理有问题，但线上同样无法 debug，线下无法重现！
5. 是否有一个全局视角来查看系统的运行状况？
6. 有什么办法可以监控到JVM的实时运行状态？
7. Arthas采用命令行交互模式，同时提供丰富的 Tab 自动补全功能，进一步方便进行问题的定位和诊断。

项目地址：https://github.com/alibaba/arthas

官方文档：https://alibaba.github.io/arthas/

关联项目greys地址：https://github.com/oldmanpushcart/greys-anatomy

**原理解析**

attach：jdk1.6新增功能，通过attach机制，可以在jvm运行中，通过pid关联应用

instrument：jdk1.5新增功能，通过instrument俗称javaagent技术，可以修改jvm加载的字节码

然后arthas和其他诊断工具一样，都是先通过attach链接上目标应用，通过instrument动态修改应用程序的字节码达到不重启应用而监控应用的目的

**快速体验**

Arthas 支持在 Linux/Unix/Mac 等平台上一键安装，请复制以下内容，并粘贴到命令行中，敲 回车 执行即可：

curl -L https://alibaba.github.io/arthas/install.sh | sh

然后就可以看到当前目录生成了一个as.sh的脚本，执行./as.sh，就会列出本机所有的java pid进程，选择一个进程后，就会连接arthas的服务。使用起来超级简单，然后试用了一下arthas提供的监控大盘功能，输出结果如下，会动态刷新哦





可以看到，以上大盘信息，cpu使用比例，堆内存（新生代，伊甸园区，幸存者区）使用情况，应用GC次数，应用GC耗时都很清楚。

**其他功能**

以下是arthas其他的一些常用的功能，如应用方法调用入参出参监控（watch命令），记录应用的每次调用时间片（tt命令），就不一一举例了。请往下看

**基础命令**

help——查看命令帮助信息

cls——清空当前屏幕区域

session——查看当前会话的信息

reset——重置增强类，将被 Arthas 增强过的类全部还原，Arthas 服务端关闭时会重置所有增强过的类

version——输出当前目标 Java 进程所加载的 Arthas 版本号

quit——退出当前 Arthas 客户端，其他 Arthas 客户端不受影响

shutdown——关闭 Arthas 服务端，所有 Arthas 客户端全部退出

keymap——Arthas快捷键列表及自定义快捷键

**jvm相关**

dashboard——当前系统的实时数据面板

thread——查看当前 JVM 的线程堆栈信息

jvm——查看当前 JVM 的信息

sysprop——查看和修改JVM的系统属性

New! getstatic——查看类的静态属性

**class/classloader相关**

sc——查看JVM已加载的类信息

sm——查看已加载类的方法信息

dump——dump 已加载类的 byte code 到特定目录

redefine——加载外部的.class文件，redefine到JVM里

jad——反编译指定已加载类的源码

classloader——查看classloader的继承树，urls，类加载信息，使用classloader去getResource

**monitor/watch/trace相关**

请注意，这些命令，都通过字节码增强技术来实现的，会在指定类的方法中插入一些切面来实现数据统计和观测，因此在线上、预发使用时，请尽量明确需要观测的类、方法以及条件，诊断结束要执行 shutdown 或将增强过的类执行 reset 命令。

monitor——方法执行监控

watch——方法执行数据观测

trace——方法内部调用路径，并输出方法路径上的每个节点上耗时

stack——输出当前方法被调用的调用路径

tt——方法执行数据的时空隧道，记录下指定方法每次调用的入参和返回信息，并能对这些不同的时间下调用进行观测



https://www.toutiao.com/a6602190446837891597/?tt_from=android_share&utm_campaign=client_share&timestamp=1537197753&app=news_article&iid=43398557445&utm_medium=toutiao_android&group_id=6602190446837891597