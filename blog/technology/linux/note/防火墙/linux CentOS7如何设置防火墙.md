[TOC]



# CentOS7如何设置防火墙

### 1. firewall配置

在旧版本的CentOS中，是使用 iptables 命令来设置防火墙的。但是，从CentOS7开始，默认就没有安装iptables，而是改用firewall来配置防火墙。

firewall的配置文件是以xml的格式，存储在 /usr/lib/firewalld/ 和 /etc/firewalld/ 目录中。

#### 1.1 系统配置目录

```
/usr/lib/firewalld/
/usr/lib/firewalld/services
/usr/lib/firewalld/zones
```

#### 1.2 用户配置目录

```
/etc/firewalld/
/etc/firewalld/services
/etc/firewalld/zones
```

#### 1.3 设置防火墙

设置防火墙的方式有两种：firewall命令和直接修改配置文件。

推荐使用firewall命令来设置防火墙。

**注意：** 对防火墙所做的更改，必须重启防火墙服务，才会立即生效。命令如下：

```
service firewalld restart
或
systemctl restart firewalld

重启防火墙 
```

#### 1.3.1 firewall命令

```
firewall-cmd --zone=public --add-port=3306/tcp --permanent
# 对外开放3306端口，供外部的计算机访问
# 该命令方式添加的端口，可在/etc/firewalld/zones中的对应配置文件中得到体现

systemctl restart firewalld
# 重启防火漆 
```

**说明：**

- firewall-cmd：Linux中提供的操作firewall的工具。
- –zone：指定作用域。
- –add-port=80/tcp：添加的端口，格式为：端口/通讯协议。
- –permanent：表示永久生效，没有此参数重启后会失效。

#### 1.3.2 直接修改配置文件

/etc/firewalld/zones/public.xml 文件的默认内容为：

```
<?xml version="1.0" encoding="utf-8"?>
<zone>
  <short>Public</short>
  <description>For use in public areas. You do not trust the other computers on networks to not harm your computer. Only selected incoming connections are accepted.</description>
  <service name="dhcpv6-client"/>
  <service name="ssh"/>
</zone> 
```

修改该配置文件，来添加3306端口。修改后的内容为：

```
<?xml version="1.0" encoding="utf-8"?>
<zone>
  <short>Public</short>
  <description>For use in public areas. You do not trust the other computers on networks to not harm your computer. Only selected incoming connections are accepted.</description>
  <service name="dhcpv6-client"/>
  <service name="ssh"/>
  <port protocol="tcp" port="3306"/>
</zone> 
```

### 2. firewall常用命令

#### 2.1 查看firewall的状态

```
service firewalld status
或
systemctl status firewalld
或
firewall-cmd --state12345
```

#### 2.2 启动、停止、重启

```
service firewalld start
或
systemctl start firewalld
# 启动

service firewalld stop
或
systemctl stop firewalld
# 停止

service firewalld restart
或
systemctl restart firewalld
# 重启1234567891011121314
```

#### 2.3 开机自启动的关闭与开启

```
systemctl disable firewalld
# 关闭开机自启动

systemctl enable firewalld
# 开启开机自启动12345
```

#### 2.4 查看防火墙的规则

```
firewall-cmd --list-all 
```

### 3. CentOS7更改为iptables防火墙

CentOS7切换到iptables防火墙，首先应该关闭默认的firewall防火墙并禁止自启动，然后再来安装和启动iptables防火墙。

操作步骤如下：

```
systemctl stop firewalld
# 停止firewall

systemctl disable firewalld
# 禁止firewall的开机自启动

yum install iptables-services
# 安装iptables

systemctl start iptables
# 开启iptables

systemctl enable iptables
# 启用iptables的自启动1234567891011121314
```

之后，就可以在CentOS7中使用iptables配置防火墙。

允许外部的计算机访问mysql，操作如下：

```
iptables -A INPUT -p tcp -dport 3306 -j ACCEPT
# 添加3306端口

service iptables save
# 保存当前的防火墙策略

service iptables restart
# 重启iptables12345678
```

**iptables常用命令：**

```
service iptables start
# 启动iptables

service iptables stop
# 停止iptables

service iptables restart
# 重启iptables

service iptables status
# 查看iptables的状态1234567891011
```

iptables防火墙，非常重要的两个文件：

- 配置文件 /etc/sysconfig/iptables-config
- 策略文件 /etc/sysconfig/iptables（默认是不存在的，使用service ipatables save 可以保存当前策略）



https://blog.csdn.net/lamp_yang_3533/article/details/76644105