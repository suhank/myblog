[TOC]



# nginx/tengine里的那些timeout时间

## 各个timeout时间配置

   老早用nginx时就零零散散的接触这些时间，一直没静下心系统的梳理一遍，其实理解了这些时间的作用和设置，对配置tengine（nginx）线上业务的优化有不可小觑的作用，对nginx的工作流程也会有更深的理解，目前我线上配置是服务http小文件（非视频和下载类）的，具体参数配置如下，因为没有看过nginx的源码，纯从运维角度理解：

 client_header_timeout 10s;

 client_body_timeout 10s;

 proxy_connect_timeout 10s;

 proxy_send_timeout 55s;

 proxy_read_timeout 60s;

 keepalive_timeout  65s;

注：我这些参数全部配在了http中。

   线上的环境是用tengine2.1.2做负载，配置了upstream，在配这些时间参数的时候特意对顺序做了调整，你会发现这个顺序就是整个服务流转的逻辑顺序，第一步请求头过来，第二步连接upstream的server，第三步将请求发送给upstream的server，第四步接收upstream的server数据，第五步是服务结束后是否采用长连接，理清楚过程就好理解了。

###  **client_header_timeout 10s;**

 默认：60s

 配在：http中、server中、location中

###  **client_body_timeout 10s;**

 默认：60s

 配在：http中、server中

 都跟请求相关，就一起理解了说了，这两个参数是对请求头和请求体（想了解请求头和请求体的概念自己百度）的超时时间，就是从三次握手到第一次读取请求头和请求体失败的时间。比如当前服务器负载大、网络卡，恰好在第一次读取请求头或请求提时没有得到且时间超过10s了，tengine就会超时报错，对于我当前应用而言，60s显而是太长了，优化到10s。

###  **proxy_connect_timeout 10s;**

 默认：60s

 配在：http中、server中、location中

  在收到请求头后，会将请求转发到upstream里面的server，这个呢就是与对应的server连接的超时时间，设置时最大值不能超过75s，我这里的server和tengine是放在同一个交换机上的内网，所以将连接时间优化到10s，超过10s连接不上，说明业务有问题了。

###  **proxy_send_timeout 55s;**

 默认：60s

 配在：http中、server中、location中

  在与upstream的server建立连接后，就会把请求往server发送，这个时间是两次数据的发送时间差，不是整个发送过程的。比如说负载大、网络卡，在tengine向server发送请求时突然卡了一下，然后继续发送，而这两次的时间差（其实就是两次write的时间差）超过了我设置的55s，tengine就会超时报错，对于这个参数，我当前优化的是55s。

###  **proxy_read_timeout 60s;**

 默认：60s

 配在：http中、server中、location中

  在将请求发送给upstream的server后，后端server就会回传数据，这个时间是两次收取数据的时间差，不是整个的接收时间。比如说负载大、网络卡，在第1次收到请求的数据时断了，然后过了60s后才收到后面的数据，这两个时间差(其实就是两次read的时间差)超过了设置的60s，tengine（nginx）就会超时报错，我当前走的是默认设置60s。

###  **keepalive_timeout  65s;**

 默认：75s

 配在：http中、server中、location中

  http是无状态的协议，当服务结束后，就面临着是否断开tcp连接的问题，当客户端或者服务器端需要时，可以在建链的时候采用长连接方式，即服务结束后在一段时间内不断开连接，当再有请求过来时省掉了建链的资源消耗，超时后tengine（nginx）会主动断开连接，当然配置里还有另外一个参数 keepalive_requests 600;，这个参数是说即使长连接没到过期时间，但服务的http总数量超过指定值后也是要断开连接，我目前设置的是600。





好了，目前主要总结这些，因为线上tengine（nginx）的主要应用还是负载均衡，所有暂时没有考虑到fastcgi这些时间的配置，以后在遇到其他时间的参数会继续补充，在此多谢开发谢兄的启发。



## **后续添加补充：**

### **resolver_timeout 10s;**

默认：30s

配在：http中、server中、location中

这个是dns解析超时时间，如果用作正向代理时就有用了，同时可以用resolver 127.0.0.1 valid=10m;指令来指定dns，后面是解析后缓存的有效时间。



### **server 127.0.0.1:9999 max_fails=20 fail_timeout=10s;**

这个是指某一个upstream的server如果失败20次后，不可以操作的时间，默认就是10s，其实可以另外的写法配在http中，我习惯直接配在server的后端。



### **keepalive_timeout  65 70;**

这是前端keepalive_timeout的一个延伸配置，前面65是告诉客户端我给你保持多久，后面一个是多久我就给断开连接了。



**自建个人原创站**[**运维网咖社(www.net-add.com)**](http://www.net-add.com/)**，新的博文会在网咖社更新，欢迎浏览**。





http://blog.51cto.com/benpaozhe/1761697