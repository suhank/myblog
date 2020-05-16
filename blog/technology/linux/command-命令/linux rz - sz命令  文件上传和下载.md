[TOC]



# Linux系统下安装rz/sz命令及使用说明

对于经常使用Linux系统的人员来说，少不了将本地的文件上传到服务器或者从服务器上下载文件到本地，rz / sz命令很方便的帮我们实现了这个功能，但是很多Linux系统初始并没有这两个命令。今天，我们就简单的讲解一下如何安装和使用rz、sz命令。

### 1.软件安装

root 账号登陆后，依次执行以下命令：

```
cd /tmp
wget http://www.ohse.de/uwe/releases/lrzsz-0.12.20.tar.gz
tar zxvf lrzsz-0.12.20.tar.gz && cd lrzsz-0.12.20
./configure && make && make install

```

      上面安装过程默认把lsz和lrz安装到了/usr/local/bin/目录下，现在我们并不能直接使用，下面创建软链接，并命名为rz/sz：

```
cd /usr/bin
ln -s /usr/local/bin/lrz rz
ln -s /usr/local/bin/lsz sz

```

也可以使用yum

```
yum -y install lrzsz
```



### 2.使用说明

#### 简介

      sz命令发送文件到本地：

```
 # sz filename
```

      rz命令本地上传文件到服务器：

```
# rz
```

      执行该命令后，在弹出框中选择要上传的文件即可。
​      说明：打开SecureCRT软件 -> Options -> session options -> X/Y/Zmodem 下可以设置上传和下载的目录。



#### rz -be (推荐使用) 上传文件      

     rz可以批量上传文件，也可以上传单个文件。使用的协议是ZMODEM协议。   
    下面简单说下ZModem协议的事情，先得从XMODEM协议（XMODEM Protocol）说起。XMODEM协议是一种使用拨号调制解调器的个人计算机通信中广泛使用的异步文件运输协议。这种协议以128字节块的形式传输数 据，并且每个块都使用一个校验和过程来进行错误检测。如果接收方关于一个块的校验和与它在发送方的校验和相同时，接收方就向发送方发送一个认可字节。然 而，这种对每个块都进行认可的策略将导致低性能，特别是具有很长传播延迟的卫星连接的情况时，问题更加严重。　　

        使用循环冗余校验的与XMODEM相应的一种协议称为XMODEM－CRC。还有一种是XMODEM－1K，它以1024字节一块来传输数据。 YMODEM也是一种XMODEM的实现。它包括XMODEM－1K的所有特征，另外在一次单一会话期间为发送一组文件，增加了批处理文件传输模式。　　            
        ZMODEM是最有效的一个XMODEM版本，它不需要对每个块都进行认可。事实上，它只是简单地要求对损坏的块进行重发。ZMODEM对按块 收费的分组交换网络是非常有用的。不需要认可回送分组在很大程度上减少了通信量。它是Xmodem 文件传输协议的一种增强形式，不仅能传输更大的数据，而且错误率更小。包含一种名为检查点重启的特性，如果通信链接在数据传输过程中中断，能从断点处而不 是从开始处恢复传输。

在命令行中敲一下rz -be，在SecureCRT下就会弹出文件选择框让你选择需要上传的文件了，而且可以选择多个；不过，如果是用putty，那就无能为力了。

      与rz命令相对应的，sz命令可以实现从[**Linux**](javascript:;)服务器下载文件到本地。

　　**常用参数**

　　-b 以二进制方式，默认为文本方式。（Binary （tell it like it is） file transfer override.）

　　-e 对所有控制字符转义。（Force sender to escape all control characters; normally XON， XOFF， DLE， CR-@-CR， and Ctrl-X are escaped.）

　　如果要保证上传的文件内容在服务器端保存之后与原始文件一致，最好同时设置这两个标志，如下所示方式使用：

　　**rz -be**

　　此命令执行时，会弹出文件选择对话框，选择好需要上传的文件之后，点确定，就可以开始上传的过程了。上传的速度取决于当时网络的状况。

　　如果执行完毕显示“0错误”，文件上传就成功了，其他显示则表示文件上传出现问题了。

　　有些版本的Linux下，执行rz命令报“command not found”，可以到安装盘中找 lrzsz*.rpm 去安装。

　　**使用示例**

　　**示例一 将本地的jdk安装程序上传到Linux服务器**

　　代码如下：

　　［root@qzt196 setup］# rz -be

　　rz waiting to receive.

　　正在开始 zmodem 传输。 按 Ctrl+C 取消。

　　正在传输 jdk-6u21-linux-i586-rpm.bin.。。

　　100% 77628 KB 137 KB/s 00:09:23 0 错误

　　［root@qzt196 setup］# ls -l jdk-6u21-linux-i586-rpm.bin

　　-rw-r--r-- 1 root root 79491215 06-25 07:06 jdk-6u21-linux-i586-rpm.bin

　　［root@qzt196 setup］#



来源： [http://blog.csdn.net/kobejayandy/article/details/13291655](http://blog.csdn.net/kobejayandy/article/details/13291655)



#### Linux文件夹打包发送到本地

```
tar -cvf script.tar script
sz script.tar
```

具体：

 **sz/rz命令:**

 一般来说，linux服务器大多是通过ssh来进行远程的登陆和管理的，如何在命令方式下上传和下载文件到服务器和本地呢？
与ssh有关的两个命令可以提供很方便的操作：
      sz：将选定的文件发送（send）到本地机器
      rz：运行该命令会弹出一个文件选择窗口，从本地选择文件上传到服务器(receive)设置上传和下载的默认目录:
options–>session options–>X/Y/Zmodem 下可以设置上传和下载的目录

**tar命令**

[root@linux ~]# tar [-cxtzjvfpPN]文件与目录....

参数：

-c：建立一个压缩文件的参数指令(create的意思)；

-x：解开一个压缩文件的参数指令！

-t：查看tarfile里面的文件！

特别注意，在参数的下达中，c/x/t仅能存在一个！不可同时存在！

因为不可能同时压缩与解压缩。

-z：是否同时具有gzip的属性？亦即是否需要用gzip压缩？

-j：是否同时具有bzip2的属性？亦即是否需要用bzip2压缩？

-v：压缩的过程中显示文件！这个常用，但不建议用在背景执行过程！

-f：使用档名，请留意，在f之后要立即接档名喔！不要再加参数！

　　　例如使用『tar -zcvfP tfile sfile』就是错误的写法，要写成

　　　『tar -zcvPf tfile sfile』才对喔！

-p：使用原文件的原来属性（属性不会依据使用者而变）

-P：可以使用绝对路径来压缩！

-N：比后面接的日期(yyyy/mm/dd)还要新的才会被打包进新建的文件中！

--exclude FILE：在压缩的过程中，不要将FILE打包！

范例：

范例一：将整个/etc目录下的文件全部打包成为/tmp/etc.tar

[root@linux ~]# tar -cvf /tmp/etc.tar /etc <==仅打包，不压缩！

[root@linux ~]# tar -zcvf /tmp/etc.tar.gz /etc <==打包后，以gzip压缩

[root@linux ~]# tar -jcvf /tmp/etc.tar.bz2 /etc <==打包后，以bzip2压缩

\#特别注意，在参数f之后的文件档名是自己取的，我们习惯上都用.tar来作为辨识。

\#如果加z参数，则以.tar.gz或.tgz来代表gzip压缩过的tar file～

\#如果加j参数，则以.tar.bz2来作为附档名啊～

\#上述指令在执行的时候，会显示一个警告讯息：

\#『tar: Removing leading `/' from member names』那是关於绝对路径的特殊设定。

范例二：查阅上述/tmp/etc.tar.gz文件内有哪些文件？

[root@linux ~]# tar -ztvf /tmp/etc.tar.gz

\#由於我们使用gzip压缩，所以要查阅该tar file内的文件时，

\#就得要加上z这个参数了！这很重要的！

 

范例三：将/tmp/etc.tar.gz文件解压缩在/usr/local/src底下

[root@linux ~]# cd /usr/local/src

[root@linux src]# tar -zxvf /tmp/etc.tar.gz

\#在预设的情况下，我们可以将压缩档在任何地方解开的！以这个范例来说，

\#我先将工作目录变换到/usr/local/src底下，并且解开/tmp/etc.tar.gz，

\#则解开的目录会在/usr/local/src/etc呢！另外，如果您进入/usr/local/src/etc

\#则会发现，该目录下的文件属性与/etc/可能会有所不同喔！

 

范例四：在/tmp底下，我只想要将/tmp/etc.tar.gz内的etc/passwd解开而已

[root@linux ~]# cd /tmp

[root@linux tmp]# tar -zxvf /tmp/etc.tar.gz etc/passwd

\#我可以透过tar -ztvf来查阅tarfile内的文件名称，如果单只要一个文件，

\#就可以透过这个方式来下达！注意到！etc.tar.gz内的根目录/是被拿掉了！

 

范例五：将/etc/内的所有文件备份下来，并且保存其权限！

[root@linux ~]# tar -zxvpf /tmp/etc.tar.gz /etc

\#这个-p的属性是很重要的，尤其是当您要保留原本文件的属性时！

 

范例六：在/home当中，比2005/06/01新的文件才备份

[root@linux ~]# tar -N '2005/06/01' -zcvf home.tar.gz /home

 

范例七：我要备份/home, /etc，但不要/home/dmtsai

[root@linux ~]# tar --exclude /home/dmtsai -zcvf myfile.tar.gz /home/* /etc

 

范例八：将/etc/打包后直接解开在/tmp底下，而不产生文件！

[root@linux ~]# cd /tmp

[root@linux tmp]# tar -cvf - /etc | tar -xvf -

\#这个动作有点像是cp -r /etc /tmp啦～依旧是有其有用途的！

\#要注意的地方在於输出档变成-而输入档也变成-，又有一个|存在～

\#这分别代表standard output, standard input与管线命令啦！

\#这部分我们会在Bash shell时，再次提到这个指令跟大家再解释啰！



http://www.cnblogs.com/end/archive/2012/06/06/2537823.html





