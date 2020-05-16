 

# shell脚本交互之:自动输入密码 expect

平时在控制台输入指令如：sudo、ssh、ftp或者修改admin权限的文件时候都会要求输入password，但是在she'll脚本运行过程中该如何交互实现自动输入密码呢？

下面总结三种实现方法。

## 一、重定向

一、重定向：用重定向方法实现交互的前提是指令需要有参数来指定密码输入方式，如ftp就有-i参数来指定使用标准输入来输入密码

​        shell用重定向作为标准输入的用法是：cmd<<delimiter ,shell 会将分界符delimiter之后直到下一个同样的分界符之前的内容作为输入

  实现ftp自动登录并运行ls指令的用法如下：其中zjk为用户名，zjk123为密码        

```
ftp -i -n 192.168.21.46 <<EOF  
user zjk zjk123  
ls  
EOF 
```

## 二、管道

二、管道：跟重定向一样，指令同样要有参数来指定密码输入方式，如sudo的-S参数，passwd的-stdin参数

​       所以实现sudo自动输入密码的脚本如下：其中zjk123为密码

​      echo 'zjk123' | sudo -S cp file1 /etc/hosts
      实现自动修改密码的脚本写法如下：

​      echo 'password' | passwd -stdin username

## 三、expect

三、expect：上面介绍的两种方法前提条件是指令有参数来设定密码输入方式，像ssh指令就没有这样的参数，第三种交互方式就派上用场了

​       expect就是用来做交互用的，基本任何交互登录的场合都能使用，但是需要安装expect包

​      语法如下：

```
#!/bin/expect  
set timeout 30  
spawn ssh -l jikuan.zjk 10.125.25.189  
expect "password:"  
send "zjk123\r"  
interact  
```

注意：expect跟bash类似，使用时要先登录到expect，所以首行要指定使用expect

在运行脚本时候要expect  file，不能sh file了

上面语句第一句是设定超时时间为30s，spawn是expect的语句，执行命令前都要加这句

expect "password："这句意思是交互获取是否返回password：关键字，因为在执行ssh时会返回输入password的提示：jikuan.zjk@10.125.25.189's password:

send就是将密码zjk123发送过去

interact代表执行完留在远程控制台，不加这句执行完后返回本地控制台 





https://blog.csdn.net/zhangjikuan/article/details/51105166