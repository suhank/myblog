[TOC]



# MongoDB开启权限验证及设置用户名密码



## 一、MongoDB数据库角色

内建的角色

数据库用户角色：read、readWrite;

数据库管理角色：dbAdmin、dbOwner、userAdmin；

集群管理角色：clusterAdmin、clusterManager、clusterMonitor、hostManager；

备份恢复角色：backup、restore；

所有数据库角色：readAnyDatabase、readWriteAnyDatabase、userAdminAnyDatabase、dbAdminAnyDatabase

超级用户角色：root // 这里还有几个角色间接或直接提供了系统超级用户的访问（dbOwner 、userAdmin、userAdminAnyDatabase）

内部角色：system

角色说明：

Read：允许用户读取指定数据库

readWrite：允许用户读写指定数据库

dbAdmin：允许用户在指定数据库中执行管理函数，如索引创建、删除，查看统计或访问system.profile

userAdmin：允许用户向system.users集合写入，可以找指定数据库里创建、删除和管理用户

clusterAdmin：只在admin数据库中可用，赋予用户所有分片和复制集相关函数的管理权限。

readAnyDatabase：只在admin数据库中可用，赋予用户所有数据库的读权限

readWriteAnyDatabase：只在admin数据库中可用，赋予用户所有数据库的读写权限

userAdminAnyDatabase：只在admin数据库中可用，赋予用户所有数据库的userAdmin权限

dbAdminAnyDatabase：只在admin数据库中可用，赋予用户所有数据库的dbAdmin权限。

root：只在admin数据库中可用。超级账号，超级权限

## 二、MongoDB用户的操作

### 1.新建管理员用户

```
use admin
db.createUser(
    {
        user: "adminUser",
        pwd: "admin123",
        roles: [{role: "userAdminAnyDatabase", db: "admin"}]
    }
    )
```


#结果

```
Successfully added user: {
"user" : "admin",
"roles" : [
{
    "role" : "userAdminAnyDatabase",
    "db" : "admin"
}
]
}
```



### 2.创建普通用户

```
 	use foo
  show roles #可以查看角色
   db.createUser(
    {
	user: "simpleUser",
	pwd: “123456”,
	roles: [“readWrite”,”dbAdmin”,”userAdmin”]
    }
  )
```

### 3.查看已存在的用户

```
db.system.users.find()
```



### 4.删除用户

```
db.system.users.remove({user:”simpleUser”})
```


注：在操作用户时，启动mongod服务时尽量不开启授权

 

## 三、开启权限验证

   在启动时指定--auth即需要授权才能操作

```
	#开启服务
 > mongod  --auth --dbpath /home/user1/mongodb/data  --logpath  /home/user1/mongodb/log/logs  --fork
 #客户端连接并认证
 >mongo
 >use foo
 >db.auth(“simpleUser”,”123456”)
————————————————
版权声明：本文为CSDN博主「思维的深度」的原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接及本声明。
原文链接：https://blog.csdn.net/skh2015java/article/details/83545712
```

 每次只有认证后才能操作数据



参考地址：https://blog.csdn.net/u013066244/article/details/53874216

https://blog.csdn.net/qq_32502511/article/details/80619277

 

原文链接：https://blog.csdn.net/skh2015java/article/details/83545712



 