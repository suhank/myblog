# nginx启动、停止、重启、配置文件校验

nginx启动命令

1、第一种方法   格式为： ngin地址 -c nginx配置文件位置

a.命令   /usr/local/nginx/sbin/nginx -c /usr/local/nginx/conf/nginx.conf

2、nginx停止命令（3种方法 2种方式）

    a、从容停止  需要知道进程号

        查看nginx进程号命令为:ps -ef|grep nginx  只需要查看master的进程号

        停止命令 kill -QUIT  进程号

    b、快速停止  kill -TERM 进程号  或者   kill -INT 进程号

    c、强制停止   pkill -9 nginx

3、nginx重启命令

    有时候我们重启是由于修改了conf文件，所以重启前，需要验证下配置文件是否正确

    a、第一种重启方法

        进入sbin目录  命令 cd /usr/local/nginx/sbin

        重启命令./nginx -s reload

    b.重启第二种方法

        重启命令kill -HUP 进程号

4、验证配置文件是否正确的命令：

    a、方法一   

       /usr/local/nginx/sbin/nginx -t -c /usr/local/nginx/conf/nginx.conf

    b、进入sbin目录  命令 cd /usr/local/nginx/sbin

        验证命令为 ./nginx -t 

        出现  XXXXXX  is ok 表示配置文件没问题



https://my.oschina.net/u/244918/blog/500960