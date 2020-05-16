[TOC]



# nginx配置多域名单ip访问不同目录不同应用

首先安装 nginx

```
sudo apt-get install nginx
```

修改 nginx 配置文件 /etc/nginx/nginx.conf , 这里只是修改其中http部分 : 

```nginx
http {
    include       mime.types;
    default_type  application/octet-stream;
    sendfile        on;
    keepalive_timeout  65;
	
    upstream www.ccc.com {  
        server 192.168.1.10:8080;      
    }  

    upstream www.bbb.com {  
        server 192.168.1.10:8081;
    }
	
    server {
        listen       80;
        server_name  www.aaa.com;

        location / {
            index  index.html index.jsp;    
            proxy_pass  http://www.aaa.com;    
            proxy_set_header    X-Real-IP   $remote_addr;    
            client_max_body_size    100m; 
        }
		
    }
	
    server {
        listen       80;
        server_name www.bbb.com;

        location / {
            index  index.html index.jsp;    
            proxy_pass  http://www.bbb.com;    
            proxy_set_header    X-Real-IP   $remote_addr;    
            client_max_body_size    100m; 
        }
    }
}
```

可以配合tomcat实现 nginx+tomcat单IP, 多域名, 多站点的访问

测试的时候可以在hosts文件中虚拟两个域名假设为两实例中的站点的域名. 





https://my.oschina.net/imhuayi/blog/475552