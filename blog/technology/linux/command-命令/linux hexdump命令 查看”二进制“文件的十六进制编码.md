[TOC]



# linux hexdump命令 查看”二进制“文件的十六进制编码

## 用途说明

hexdump命令一般用来查看”二进制“文件的十六进制编码，但实际上它的用途不止如此，手册页上的说法是“ascii, decimal, hexadecimal, octal dump“，这也就是本文标题为什么要将”十六“给引起来的原因，而且它能查看任何文件，而不只限于二进制文件了。另外还有xxd和od也可以做类似的事情，但是我从未用过。在程序输出二进制格式的文件时，常用hexdump来检查输出是否正确。当然也可以使用Windows上的UltraEdit32之类的工具查看文件的十六进制编码，但Linux上有现成的工具，何不拿来用呢。

## 常用参数

如果要看到较理想的结果，使用-C参数，显示结果分为三列（文件偏移量、字节的十六进制、ASCII字符）。

格式：hexdump -C binfile

一般文件都不是太小，最好用less来配合一下。

格式：hexdump -C binfile | less

## 语法

```
hexdump [选项] [文件]...
```

## 选项

```
-n length 只格式化输入文件的前length个字节。
-C 输出规范的十六进制和ASCII码。
-b 单字节八进制显示。
-c 单字节字符显示。
-d 双字节十进制显示。
-o 双字节八进制显示。
-x 双字节十六进制显示。
-s 从偏移量开始输出。
-e 指定格式字符串，格式字符串包含在一对单引号中，格式字符串形如：'a/b "format1" "format2"'。
-v 显示所有的重复数据
-C 输出十六进制和对应字符
```

每个格式字符串由三部分组成，每个由空格分隔，第一个形如a/b，b表示对每b个输入字节应用format1格式，a表示对每a个输入字节应用format2格式，一般a>b，且b只能为1，2，4，另外a可以省略，省略则a=1。format1和format2中可以使用类似[printf](http://man.linuxde.net/printf)的格式字符串，如：

```
%02d：两位十进制
%03x：三位十六进制
%02o：两位八进制
%c：单个字符等
```

还有一些特殊的用法：

```
%_ad：标记下一个输出字节的序号，用十进制表示。
%_ax：标记下一个输出字节的序号，用十六进制表示。
%_ao：标记下一个输出字节的序号，用八进制表示。
%_p：对不能以常规字符显示的用 . 代替。
```

同一行如果要显示多个格式字符串，则可以跟多个`-e`选项。

```
hexdump -e '16/1 "%02X " "  |  "' -e '16/1 "%_p" "\n"' test
00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F  |  ................  
10 11 12 13 14 15 16 17 18 19 1A 1B 1C 1D 1E 1F  |  ................  
20 21 22 23 24 25 26 27 28 29 2A 2B 2C 2D 2E 2F  |   !"#$%&'()*+,-./ 
```



## 使用示例

### 示例一 比较各种参数的输出结果

```sh
[root@new55 ~]# echo /etc/passwd | hexdump 
0000000 652f 6374 702f 7361 7773 0a64          
000000c
[root@new55 ~]# echo /etc/passwd | od -x 
0000000 652f 6374 702f 7361 7773 0a64
0000014
[root@new55 ~]# echo /etc/passwd | xxd 
0000000: 2f65 7463 2f70 6173 7377 640a            /etc/passwd.
[root@new55 ~]# echo /etc/passwd | hexdump -C      <== 规范的十六进制和ASCII码显示（Canonical hex+ASCII display ）
00000000  2f 65 74 63 2f 70 61 73  73 77 64 0a              |/etc/passwd.|
0000000c
[root@new55 ~]# echo /etc/passwd | hexdump -b      <== 单字节八进制显示（One-byte octal display） 
0000000 057 145 164 143 057 160 141 163 163 167 144 012                
000000c
[root@new55 ~]# echo /etc/passwd | hexdump -c      <== 单字节字符显示（One-byte character display） 
0000000   /   e   t   c   /   p   a   s   s   w   d  \n                
000000c
[root@new55 ~]# echo /etc/passwd | hexdump -d      <== 双字节十进制显示（Two-byte decimal display） 
0000000   25903   25460   28719   29537   30579   02660                
000000c
[root@new55 ~]# echo /etc/passwd | hexdump -o       <== 双字节八进制显示（Two-byte octal display） 
0000000  062457  061564  070057  071541  073563  005144                
000000c
[root@new55 ~]# echo /etc/passwd | hexdump -x       <== 双字节十六进制显示（Two-byte hexadecimal display） 
0000000    652f    6374    702f    7361    7773    0a64                
000000c
[root@new55 ~]# echo /etc/passwd | hexdump -v 
0000000 652f 6374 702f 7361 7773 0a64          
000000c
```

**比较来比较去，还是hexdump -C的显示效果更好些。**

### 示例二 确认文本文件的格式

文本文件在不同操作系统上的行结束标志是不一样的，经常会碰到由此带来的问题。比如Linux的许多命令不能很好的处理DOS格式的文本文件。Windows/DOS下的文本文件是以\r\n作为行结束的，而Linux/Unix下的文本文件是以\n作为行结束的。 

```sh
[root@new55 ~]# cat test.bc 
123*321
123/321
scale=4;123/321
[root@new55 ~]# hexdump -C test.bc 
00000000  31 32 33 2a 33 32 31 0a   31 32 33 2f 33 32 31 0a  |123*321.123/321.|
00000010  73 63 61 6c 65 3d 34 3b  31 32 33 2f 33 32 31 0a  |scale=4;123/321.|
00000020  0a                                                |.|
00000021
[root@new55 ~]#
```

注：常见的ASCII字符的十六进制表示

\r      0D

\n     0A

\t      09

DOS/Windows的换行符 \r\n 即十六进制表示 0D 0A

Linux/Unix的换行符      \n    即十六进制表示 0A

### 示例三 查看wav文件

有些IVR系统需要8K赫兹8比特的语音文件，可以使用hexdump看一下具体字节编码。

```sh
[root@web186 root]# ls -l tmp.wav 
-rw-r--r--    1 root     root        32381 2010-04-19  tmp.wav
[root@web186 root]# file tmp.wav 
tmp.wav: RIFF (little-endian) data, WAVE audio, ITU G.711 a-law, mono 8000 Hz

[root@web186 root]# hexdump -C tmp.wav | less 
00000000  52 49 46 46 75 7e 00 00  57 41 56 45 66 6d 74 20  |RIFFu~..WAVEfmt |
00000000  52 49 46 46 75 7e 00 00  57 41 56 45 66 6d 74 20  |RIFFu~..WAVEfmt |
00000010  12 00 00 00 06 00 01 00  40 1f 00 00 40 1f 00 00  |........@...@...|
00000020  01 00 08 00 00 00 66 61  63 74 04 00 00 00 43 7e  |......fact....C~|
00000030  00 00 64 61 74 61 43 7e  00 00 d5 d5 d5 d5 d5 d5  |..dataC~........|
00000040  d5 d5 d5 d5 d5 d5 d5 d5  d5 d5 d5 d5 d5 d5 d5 d5  |................|
*
000000a0  d5 d5 d5 d5 d5 d5 d5 d5  d5 55 d5 55 d5 d5 55 d5  |.........U.U..U.|
000000b0  55 d5 d5 55 d5 55 d5 d5  55 d5 55 55 55 55 55 55  |U..U.U..U.UUUUUU|
000000c0  55 55 55 55 55 55 55 d5  d5 d5 d5 d5 d5 d5 d5 d5  |UUUUUUU.........|
000000d0  d5 55 55 55 55 55 55 55  55 55 55 55 55 55 55 55  |.UUUUUUUUUUUUUUU|
000000e0  55 55 55 55 55 55 55 55  55 d5 d5 d5 d5 d5 d5 d5  |UUUUUUUUU.......|
000000f0  d5 d5 d5 d5 55 55 55 55  55 55 55 55 55 55 55 55  |....UUUUUUUUUUUU|
00000100  55 55 55 55 55 55 55 55  55 55 55 55 d5 d5 d5 d5  |UUUUUUUUUUUU....|
00000110  d5 d5 d5 d5 d5 d5 55 55  55 55 55 55 55 55 55 55  |......UUUUUUUUUU|
00000120  55 55 55 55 55 55 55 55  55 55 55 55 55 55 d5 d5  |UUUUUUUUUUUUUU..|
00000130  d5 d5 d5 d5 d5 d5 d5 d5  d5 55 55 55 55 55 55 55  |.........UUUUUUU|
00000140  55 55 d5 55 55 55 55 55  55 55 55 55 55 55 55 55  |UU.UUUUUUUUUUUUU|
00000150  55 d5 d5 d5 d5 d5 d5 d5  d5 d5 d5 55 55 55 55 55  |U..........UUUUU|
00000160  55 55 55 55 55 55 55 55  55 55 55 55 55 55 55 55  |UUUUUUUUUUUUUUUU|
00000170  55 55 55 55 d5 d5 d5 d5  d5 d5 d5 d5 d5 55 d5 55  |UUUU.........U.U|
00000180  55 55 55 55 55 55 55 55  55 55 55 55 55 55 55 55  |UUUUUUUUUUUUUUUU|
00000190  55 55 55 55 55 55 55 d5  d5 d5 d5 d5 d5 d5 d5 55  |UUUUUUU........U|
000001a0  55 55 55 55 55 55 55 d5  d5 55 55 55 55 55 55 55  |UUUUUUU..UUUUUUU|
000001b0  55 55 55 55 55 55 55 d5  55 55 d5 55 55 55 55 55  |UUUUUUU.UU.UUUUU|
000001c0  55 55 d5 55 d5 d5 55 d5  55 55 55 55 55 55 55 55  |UU.U..U.UUUUUUUU|
000001d0  55 55 55 55 55 55 55 55  55 55 55 55 55 55 55 d5  |UUUUUUUUUUUUUUU.|
000001e0  55 d5 d5 d5 d5 55 55 55  55 55 55 55 55 55 55 55  |U....UUUUUUUUUUU|
000001f0  55 55 55 55 55 55 55 55  55 55 55 55 d5 55 55 d5  |UUUUUUUUUUUU.UU.|
00000200  55 55 55 55 55 55 55 55  55 d5 d5 d5 d5 d5 55 55  |UUUUUUUUU.....UU|
00000210  55 55 55 55 55 55 55 55  55 55 55 55 55 55 55 d5  |UUUUUUUUUUUUUUU.|
00000220  55 55 d5 55 d5 55 55 d5  55 d5 55 55 d5 55 d5 d5  |UU.U.UU.U.UU.U..|
00000230  d5 d5 d5 d5 d5 d5 d5 d5  d5 d5 d5 d5 d5 d5 d5 d5  |................|
*
00000ba0  d5 d5 d5 d5 d5 d5 d5 d5  d5 d5 d5 55 55 d5 55 d5  |...........UU.U.|
00000bb0  55 55 d5 55 d5 55 d5 d5  55 d5 55 55 55 55 55 55  |UU.U.U..U.UUUUUU|
00000bc0  55 55 55 55 55 55 55 55  55 d5 d5 55 55 55 55 55  |UUUUUUUUU..UUUUU|
00000bd0  55 55 55 55 55 55 55 d5  55 55 55 55 55 55 d5 55  |UUUUUUU.UUUUUU.U|
00000be0  55 55 55 55 55 55 55 55  55 55 55 d5 55 55 55 55  |UUUUUUUUUUU.UUUU|
00000bf0  55 55 55 55 55 55 55 55  d5 d5 55 55 55 55 55 d5  |UUUUUUUU..UUUUU.|
00000c00  d5 55 55 55 55 d5 d5 d5  55 55 55 55 55 d5 d5 55  |.UUUU...UUUUU..U|
:q

[root@web186 root]#


```





http://codingstandards.iteye.com/blog/786653