# 如何用sed命令往空文件中写入数据 

朋友碰到个难题，就是用sed命令往空文件末尾中写入数据，数据来自一个变量，怎么都添加不成功，然后问我要如何处理。仔细想了下，如果文件为非空的话，使用sed命令是可以搞定的，命令如下：

```delphi
sed -i '$a\要插入的文字' file.txt
```

i  # 直接修改文件。

$  # 匹配文件的最后一行位置

a  # 命令在后面append

但如果是空的话，上面的命令就搞不定了！为什么呢？ 因为sed是基于行来处理的文件流编辑器，如果文件为空的话，它是处理不了的！找了段英文的解释如下：



```vbnet
Sed  is  a  stream  editor.   A stream editor is used to perform basic text transformations on an input stream (a file or input from a
       pipeline).  While in some ways similar to an editor which permits scripted edits (such as ed), sed works by making only one pass  over
       the  input(s),  and  is  consequently more efficient.  But it is sed’s ability to filter text in a pipeline which particularly distin-
       guishes it from other types of editors.
```



大致情形就是：



```perl
linux@host~# cat file.txt                // 里面没有内容
linux@host~# touch file.txt              // 新建一个空文件
linux@host~# cat file.txt                // 里面没有内容
linux@host~# sed -i "\$a $var" file.txt  // 往文件里面添加变量中的数据
linux@host~# cat file.txt                // 但文件里面还是没有内容
linux@host~#
```

那么这种情形要如何处理呢？可以加个判断，如果文件存在但为空的话，使用echo命令来添加，如果非空的话，则使用sed命令，所以完整的测试应该这样：



【***文件为空***】



```ruby
# var变量中的内容
$ var='append new context'
# 新建一个空文件
$ touch file.txt
# 文件内容为空
$ cat file.txt
$
# 判断文件是否为空，空则使用echo命令添加，非空则使用sed命令
$ test -s file.txt && sed -i "\$a $var" file.txt || echo "$var" >> file.txt
$ cat file.txt
append new context
```

【**文件非空**】



```kotlin
linux@host~# cat file.txt
This is line 1.
This is line 2.
linux@host~# test -s file.txt && sed -i "\$a $var" file.txt || echo "$var" >> file.txt
linux@host~# cat file.txt
This is line 1.
This is line 2.
append new context
```

从上面来看，问题得到完美的解决！





https://blog.csdn.net/Jerry_1126/article/details/78138578