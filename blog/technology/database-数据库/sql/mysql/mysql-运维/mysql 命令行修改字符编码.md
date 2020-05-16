# mysql命令行修改字符编码

## 1、修改数据库字符编码

```
mysql> alter database mydb character set utf8 ;
```

## 2、创建数据库时，指定数据库的字符编码

```
mysql> create database mydb character set utf8 ;
```

## 3、查看mysql数据库的字符编码

```
mysql> show variables like 'character%'; //查询当前mysql数据库的所有属性的字符编码

+--------------------------+----------------------------+
| Variable_name            | Value                      |
+--------------------------+----------------------------+
| character_set_client     | latin1                     |
| character_set_connection | latin1                     |
| character_set_database   | utf8                       |
| character_set_filesystem | binary                     |
| character_set_results    | latin1                     |
| character_set_server     | utf8                       |
| character_set_system     | utf8                       |
| character_sets_dir       | /usr/share/mysql/charsets/ |
+--------------------------+----------------------------+
```

## 4、修改mysql数据库的字符编码

修改字符编码必须要修改mysql的配置文件my.cnf,**然后重启才能生效**

通常需要修改my.cnf的如下几个地方：

【client】下面，加上default-character-set=utf8，或者character_set_client=utf8

【mysqld】下面，加上character_set_server = utf8 ；

因为以上配置，mysql默认是latin1，如果仅仅是通过命令行客户端，mysql重启之后就不起作用了。

如下是客户端命令行修改方式，不推荐使用

```
mysql> set character_set_client=utf8 ;

mysql> set character_set_connection=utf8 ;

mysql> set character_set_database=utf8 ;

mysql> set character_set_database=utf8 ;

mysql> set character_set_results=utf8 ;

mysql> set character_set_server=utf8 ;

mysql> set character_set_system=utf8 ;

mysql> show variables like 'character%';
+--------------------------+----------------------------+
| Variable_name            | Value                      |
+--------------------------+----------------------------+
| character_set_client     | utf8                       |
| character_set_connection | utf8                       |
| character_set_database   | utf8                       |
| character_set_filesystem | binary                     |
| character_set_results    | utf8                       |
| character_set_server     | utf8                       |
| character_set_system     | utf8                       |
| character_sets_dir       | /usr/share/mysql/charsets/ |
+--------------------------+----------------------------+
8 rows in set (0.00 sec)
```





https://www.cnblogs.com/candle806/archive/2013/01/14/2859721.html