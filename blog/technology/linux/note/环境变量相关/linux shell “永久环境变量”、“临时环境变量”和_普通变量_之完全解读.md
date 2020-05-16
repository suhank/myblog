[TOC]



# linux shell “永久环境变量”、“临时环境变量”和"普通变量"之完全解读

## 一. 永久环境变量（实际上属于文件， 而不属于shell, 每打开一个shell, 都会加载/导入到shell中， 形成当前shell的临时环境变量）

​        先说说"永久环境变量"， 其实， 我也知道， 没有什么东西是真正永久的， 这里的永久是指： 变量存储在文件中， 不会因为掉电或者关机而消失。下面， 我们打开一个linux shell, 并打印HOME的值， 如下：

```
[taoge@localhost Desktop]$ echo $HOME
/home/taoge 
```

​       我们看到HOME这个变量的值是/home/taoge, 这个变量的值是从哪里来的呢？ 我们可以看看用户主目录下的.bashrc文件

```
[taoge@localhost Desktop]$ cat ~/.bashrc 
```

​      其中的内容为：

```sh
# .bashrc
 
# Source global definitions
if [ -f /etc/bashrc ]; then
        . /etc/bashrc
fi
 
# User specific aliases and functions
```

​       啊？ 居然没有HOME? 不要着急， 先姑且认为是在/etc/bashrc中进行了HOME的设置吧， 在此， 我们不深究， 只需要有这个认识： HOME与文件~/.bashrc密切相关， 即使断电或者掉电， 也不怕消失。

​      实际上， 当我们开启一个shell进程的时候， HOME这个永久环境变量会自动导入到当前的shell中来（为当前shell设置了一个临时的环境变量）， 那这个HOME可不可以被unset掉呢？ 我们来看一下：

```
[taoge@localhost Desktop]$ echo $HOME
/home/taoge
[taoge@localhost Desktop]$ unset HOME
[taoge@localhost Desktop]$ echo $HOME
 
[taoge@localhost Desktop]$ 
```

​      我们看到， 当前shell进程中的HOME确实是被unset掉了， 不要着急， 我们另外打开一个shell进程， 然后看看有没有HOME,  如下：

```
[taoge@localhost Desktop]$ echo $HOME
/home/taoge
[taoge@localhost Desktop]$
```

​      可以看到， 第二个shell进程中是有HOME的， 这个不难理解， 因为开启第二个shell进程的时候， 会把~/.bashrc中的永久HOME加载一次， 所以可以看到/home/taoge.

​      我们暂时来总结一下： **永久环境变量存在于~/.bashrc文件中（掉电或者重启后， 不会消失）， 在每个shell启动的时候， 都会将永久环境变量导入到shell中， 并成为shell的临时环境变量， 这个临时的环境变量可以被unset掉后， 但不会影响其他shell， 因为我们即将会说到， 不同shell的临时环境变量是彼此独立的。**

​      你可能还在纠结并不耐烦地发出疑问：在~/.bashrc中没有看到HOME啊， 你不是在扯淡么？ 好， 我们自己来把一个变量写入到~/.bashrc文件中， 使之成为永久环境变量， ~/.bashrc文件内容如下：

```sh
# .bashrc
 
# Source global definitions
if [ -f /etc/bashrc ]; then
        . /etc/bashrc
fi
 
# User specific aliases and functions
 
 
# define permanent variable by taoge
winner="people who persists"

```

​      我定义winner这个变量的值为“people who persists”, 好， 保存文件， 我们来查看一下winner这个变量， 如下：

```
[taoge@localhost Desktop]$ echo $winner
[taoge@localhost Desktop]$  
```

​      遗憾的是， 我们没有看到winner, 为什么呢？ 因为现在只是把winner变成了永久环境变量， 这个永久环境变量并没有加载到当前的shell中来啊！ 好吧， 我们关掉当前的shell, 并打开一个新的shell,  再查看一次， 如下：

```
[taoge@localhost Desktop]$ echo $winner
people who persists
[taoge@localhost Desktop]$ 
```

​      可以看到， 这次winner有值了， 激动吧。 这样， 无论以后是重启linux, 还是怎么滴， winner就成为了文件的一部分， 就成了永久的环境变量了。 当然， 你要是把~/.bashrc文件中的winner那一行删除了， 然后跟我说：你不是说永久的么？ 现在怎么不永久啦？  好吧， 你这是在故意找茬。

## 二. 临时的环境变量（属于当前shell及其子进程）

​       上面我们已经说了， winner成了永久的环境变量， 当一个shell开启的时候， 便会加载这个winner变量， 那么在当前shell环境中， 这个winner就会变成临时的环境变量。 之所以说是临时的， 是因为你可以把他unset掉， 之所以说是环境变量， 意思是说（没被unset掉的时候）， 当前shell进程的子进程可以访问到该winner, 如下：

```sh
[taoge@localhost Desktop]$ echo $$
7203
[taoge@localhost Desktop]$ echo $winner
people who persists
[taoge@localhost Desktop]$ bash
[taoge@localhost Desktop]$ echo $$
7354
[taoge@localhost Desktop]$ echo $winner
people who persists
[taoge@localhost Desktop]$ exit
exit
[taoge@localhost Desktop]$ echo $$
7203
[taoge@localhost Desktop]$ 

```

​      我们看到， 当前进程pid是7203， 为它再开一个子shell进程， 子进程的pid为7354， 我们可以看到， 在进程中， 也可以访问到winner.   

​      上面的winner是~/.bashrc中永久环境变量加载而来的， 那我们可不可以自定义临时环境变量呢？ 可以的。 这次， 我们运行a.sh脚本来做当前shell的子进程， 如下：

```sh
[taoge@localhost Desktop]$ export x="defined in shell"
[taoge@localhost Desktop]$ vim a.sh
[taoge@localhost Desktop]$ cat a.sh 
#! /bin/bash
echo $x
[taoge@localhost Desktop]$ chmod +x a.sh 
[taoge@localhost Desktop]$ ./a.sh 
defined in shell
[taoge@localhost Desktop]$ 
```

​     可以看到， 在脚本子进程中， 也可以访问x这个临时的环境变量。 好， 我问个问题， 那别的shell能访问这个x么？  我们再开启另外一个shell, 如下：

```
[taoge@localhost Desktop]$ echo $x
[taoge@localhost Desktop]$  
```

​     肯定是没有啊， 上面的一些例子都揭露了临时环境变量的本质： **当前shell的临时环境变量， 能被自己及其子进程(子shell进程, 子脚本进程或者子C程序进程)访问， 但不能被其它shell访问(相互独立)。 对了， 我们上面已经讨论过了， 临时的环境变量可以被unset掉。在实际大型的软件开发中， 编译大工程， 经常需要用到临时环境变量。**

## 三. 普通变量（属于当前shell进程）

​      shell中的普通变量很简单， 仅能被当前shell访问， 不能被其子进程访问， 更不能被其它shell访问。 当然， 它也可以被unset掉， 测试如下：

```sh
[taoge@localhost Desktop]$ z="f(y)"
[taoge@localhost Desktop]$ echo $z
f(y)
[taoge@localhost Desktop]$ echo $$
7578
[taoge@localhost Desktop]$ bash
[taoge@localhost Desktop]$ echo $$
7653
[taoge@localhost Desktop]$ echo $z
 
[taoge@localhost Desktop]$ exit
exit
[taoge@localhost Desktop]$ echo $$
7578
[taoge@localhost Desktop]$ unset z
[taoge@localhost Desktop]$ echo $z
 
[taoge@localhost Desktop]$

```

​     可见， 确实不能被子shell访问， 当然， 肯定更不能被其它shell访问了。   普通变量要提升了临时的环境变量， 那也很简单， 加一下export就可以了， 如下：

```sh
[taoge@localhost Desktop]$ z="f(y)"
[taoge@localhost Desktop]$ echo $z
f(y)
[taoge@localhost Desktop]$ echo $$
7578
[taoge@localhost Desktop]$ export z
[taoge@localhost Desktop]$ bash
[taoge@localhost Desktop]$ echo $$
7723
[taoge@localhost Desktop]$ echo $z
f(y)
[taoge@localhost Desktop]$ exit
exit
[taoge@localhost Desktop]$ echo $$
7578
[taoge@localhost Desktop]$ 

```

## 总结 

​      啰嗦地总结一下：

shell中的普通变量， 仅能被当前shell访问， 不能被其子进程访问， 更不能被其它shell访问。 当然， 它也可以被unset掉。

​      OK,  我觉得我应该说清楚了， 早休息！

​       补充： 

​      1. 实际上， 我们也可以在~/.bashrc中设置alias别名, 这个用起来很方便， 每个shell(包括子shell)都可以用到。 修改好后， 不用再关掉shell打开shell了， 直接在当前shell中执行source ~/.bashrc即可。

      2. 如果自己在当前shell中定义一个alias, 那么仅在当前shell进程中有效， 我们没法用export使得它在子shell中生效， 毕竟， alias和上面讲的变量还是有所区别的。 如果是在脚本中定义alias, 则也必须用source来执行， 使得alias在当前shell中生效， 我经常这么玩。





https://blog.csdn.net/stpeace/article/details/45567977