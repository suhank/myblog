[TOC]



# mysql 命令行登录Mysql，远程登录Mysql的方法

2017年07月28日 10:11:46

  

Mysql命令行登录，远程登录Mysql的方法

同事给了一个存放数据的服务器，想访问，采用常规的 mysql -u username -h ip_XXXX  -p 的形式始终无法登录，奇怪是的Navicat却可以通过ssh访问，后来遂发现原来同事建库时的端口不是默认端口3306，于是恍然大悟，百度之余，解决问题，遂总结以下，共享！

## 1.本地登录MySQL

命令：

```
mysql -u root -p   
```

//root是用户名，

输入这条命令按回车键后系统会提示你输入密码

## 2.指定端口号登录MySQL数据库

将以上命令：

```
mysql -u root -p改为 mysql -u root -p  -P 3306
```

即可，注意指定端口的字母P为大写，

而标识密码的p为小写。MySQL默认端口号为3306

  

## 3.指定IP地址和端口号登录MySQL数据库

命令格式为： 

databasename:选填

```
mysql -h ip databasename -P 3306 -u root -p 
```

例如: 在shell终端或者ssh终端，或者cmd窗口远程登录 端口为3306，用户名为user1，ip为 182.167.12.3 ,数据名称为sys_mst的mysql服务器的命令是

```
mysql -h 182.167.12.3 sys_mst -P 3306 -u user1 -p 'xxxxxx'
```



   

  



版权声明：本文为博主原创文章，遵循[ CC 4.0 BY-SA ](http://creativecommons.org/licenses/by-sa/4.0/)版权协议，转载请附上原文出处链接和本声明。

本文链接：<https://blog.csdn.net/helloxiaozhe/article/details/76229074>