[TOC]



# linux系统后台运行应用三板斧supervisor、screen、nohup

木讷大叔爱运维 2020-03-14 11:39:40

  

Linux系统中我们经常需要将应用或程序放在后台运行，下面从应用角度介绍下后台运行的三板斧supervisor、screen、nohup。

## supervisor

为什么要先介绍supervisor呢？

因为supervisor可以对所管理的进程启动、重载、停止，当监测到进程停止后，会自动拉起，实现了进程的“故障自愈”。我们不需要再额外开发守护脚本，导致维护成本的增加。

对于运维这简直是神器，必须放在第一位，但是需要花点配置成本。下面就来看下supervisor如何使用。

### **1.安装**

```
#centos7
yum install supervisor
vim /etc/supervisord.conf
#最后两行可看到，最终配置文件在 supervisord.d/目录下
[include]
files = supervisord.d/*.ini
#开机自启动
systemctl enable supervisord
#查看是否开机自启动
systemctl is-enabled supervisord
```

### **2.配置**

```
cd /etc/supervisord vim hello.ini 
#程序名hello [program:hello] ;
启动用户 user=root ;
程序启动命令 command=java -Dspring.profiles.active=test -jar hello.jar numprocs=1 ;
程序启动目录 directory=/opt/java_app ;
在supervisord启动时自启动 autostart=true ;
程序异常退出后自动重启,可选值：[unexpected,true,false]，默认为unexpected autorestart=true ;
启动10秒后没有异常退出，就表示进程正常启动了 startsecs=10 ;
启动失败自动重试次数 startretries=3
```

### **3.管理**

```
#参数可以为all或单个项目hello
supervisorctl reload [all | hello]
#更新配置文件，更新配置文件并重启与更新有关的进程
supervisorctl update hello
#重载配置文件 ，注意reload会导致supervisor重启，所管理的进程会重启
supervisorctl reload hello
#查看状态
supervisorctl status
#启动hello
supervisorctl start hello
```

### **4.应用场景**

supervisor适用于可多次启动并长期运行的后台任务，如java服务、缓存服务及其他自定义服务等。

### **5.小结**

supervisor可以很优雅的解决掉关于进程的启动、重启、重载等方面的操作，而之前我们可能需要花更多的时间去额外处理，如判断进程存在、杀掉进程甚至可能还需要配合脚本写个循环去串联这些操作。

另supervisor还提供了很多第三方的Web-UI统一的 WebUI 集中化管理各个服务器节点的进程，如CeSi、supervisor-easy、Supervisord-monitor等，在此不多做描述。

### **注意：**

1. supervisor管理运行于前台的进程，对于运行后台daemon的进程，如tomcat、jetty、nginx等启动后会直接在后台运行，supervisorctl status会报错"BACKOFF Exited too quickly (process log may have details"。
2. Centos6.5默认yum安装supervisor版本为2.1版本，此版本运行有问题，不建议使用。

##  screen

Screen的会话保持特性，即screen打开的会话可以分离或恢复，而不影响会话内部的操作，这样我们将命令行、脚本甚至是数据传输放到screen会话中运行，效果就类似于后台运行。

**1.普通模式**

```sh
[root@test #]$ yum install screen -y
#创建会话hello，此时会登入新会话
[root@test #]$ screen
或
[root@test #]$ screen -S hello
#分离会话，此时程序不会中断
键盘ctrl+a+d 分离会话
[detached from 28877.hello]

#列出所有会话
[root@test #]$ screen -ls
There is a screen on:
  28877.hello  (Detached)
  28876.test   (Dead)
1 Socket in /var/run/screen/S-root.
#恢复会话
[root@test #]$ screen -r 28877
或
[root@test #]$ screen -r hello
#清除dead会话
[root@test #]$ screen -wipe
```

**2.分离模式**

在分离模式下的屏幕会话，作为守护程序启动。

```sh
#创建一个后台运行任务
[root@test #]$ vim test.sh
#!/bin/bash
n=0
while [ $n -le 50 ]
do 
    echo $n
    n=$(( $n + 1 ))
    sleep 1
done

#创建处于分离模式的会话，启动后直接断开会话
[root@test #]$ screen -dmS test./test.sh
#此时会话已断开，但是任务仍在运行，相当于把任务放在后台运行
[root@test #]$ screen -ls
There is a screen on:
  30537.test  (Detached)
#登入会话脚本正在会话中打印输出，执行完毕后会会话终止
[root@test #]$ screen -r 30537
0
1
2
```

**注意：**

如果要打印screen日志，需如下设置：

```
#其中%t 为标题，如screen_test.log
echo "logfile /root/screen_%t.log" >> /etc/screenrc
#-L 打开日志输出
#-t 为标题
#执行命令后，会在/root下生成screen_test.log
screen -L -t test -dmS test ./test.sh
```

**3.应用场景**

screen适用于单次长时间运行的任务，如备份、ftp传输、下载、数据导入导出、终端超时断开等。

**4.小结**

screen的会话保持和日志输出，在一定程度上也可以作为后台运行的一种方式。但是需要多用户会话的管理，如test用户创建的会话，root通过screen -ls查看是看不到test用户新建的会话的。因此会话管理，一定需要头脑清醒。

 

## nohup

nohup后台运行最常见的方式，拿来即用，没有什么配置成本，可直接上手。这个大家比较熟悉，就不作过多介绍。

```sh
#还是以上面的test.sh脚本为例
[root@test #]$ vim test.sh
#!/bin/bash
n=0
while [ $n -le 50 ]
do 
    echo $n
    n=$(( $n + 1 ))
    sleep 1
done
#默认情况下nohup运行的程序，输出记录会打印到当前目录下的nohup.out文件中
[root@test #]$ nohup bash test.sh &
[1] 7415
nohup: ignoring input and appending output to ‘nohup.out’
[root@test #]$ tail -f nohup.out
0
1
2
[root@test #]$ jobs -l
[1]+  7415 Running                 nohup bash test.sh &
#标准输出及错误输出，重定向到自定义日志
[root@test #]$ nohup bash test.sh > test.log 2>&1 &
[root@test #]$ tail -f test.log
nohup: ignoring input
0
1
```

## 总结

以上三种后台运行的方式，大家可各取所需，不必矫枉过正，毕竟我们首先要保证的是业务稳定运行。





https://www.toutiao.com/a6803879653791498766/?tt_from=android_share&utm_campaign=client_share&timestamp=1584229823&app=news_article&utm_medium=toutiao_android&req_id=202003150750220100140400950385765E&group_id=6803879653791498766