# mysql Blob类型大小 上传文件大小问题

 

 MySQL有四种BLOB类型:

```
　　·tinyblob:仅255个字符

　　·blob:最大限制到65K字节

　　·mediumblob:限制到16M字节

　　·longblob:可达4GB
```

　　在每个MySQL的文档(从MySQL4.0开始)的介绍中,一个longblob列的最大允许长度依赖于在客户/服务器协议中可配置的最大包的大小和可用内存数。

　　你可能对在BLOB中存储大型文件非常谨慎，但是请放心使用，MYSQL提供了这样的灵活性！最大包的大小可容易地中的在配置文件中设置。例如:

一,Windows通过文件my.ini (在系统盘)
[mysqld]
set-variable = max_allowed_packet=10M

二,linux通过etc/my.cnf
[mysqld]
max_allowed_packet = 16M

　　你能指定几乎任何你需要的大小。默认是1M。





https://blog.csdn.net/wangpingpaul/article/details/4075568