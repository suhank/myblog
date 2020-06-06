# mysql 查询结果输出到文件

mysql查询结果导出/输出/写入到文件

## **方法一：**

直接执行命令：
mysql> **select count(1) from table into outfile '/tmp/test.xls';**

Query OK, 31 rows affected (0.00 sec)
在目录/tmp/下会产生文件test.xls
遇到的问题：
mysql> **select count(1) from table  into outfile '/data/test.xls';**
报错：
ERROR 1 (HY000): Can't create/write to file '/data/test.xls' (Errcode: 13)
可能原因：mysql没有向/data/下写的权限

## **方法二：**

查询都自动写入文件：
mysql> **pager cat > /tmp/test.txt ;**
PAGER set to 'cat > /tmp/test.txt'
之后的所有查询结果都自动写入/tmp/test.txt'，并前后覆盖
mysql> select * from table ;
30 rows in set (0.59 sec)
在框口不再显示查询结果

## **方法三：**

跳出mysql命令行
[root@SHNHDX63-146 ~]# **mysql -h 127.0.0.1 -u root -p XXXX -P 3306 -e "select \* from table" > /tmp/test/txt**



https://www.cnblogs.com/emanlee/p/4233602.html

