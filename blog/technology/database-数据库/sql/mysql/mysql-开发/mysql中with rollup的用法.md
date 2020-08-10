# mysql中with rollup的用法

Mysql中有一个with rollup是用来在分组统计数据的基础上再进行统计汇总，即用来得到group by的汇总信息；

举例如下：

新建表：

```sql
create table age ( sno char(4) primary key,sname varchar(10),sage int);
```

表中数据有：

```sql
mysql> select * from age;
+------+-----------+------+
| sno  | sname     | sage |
+------+-----------+------+
| 1101 | zhangsan1 |   20 |
| 1102 | zhangsan2 |   21 |
| 1103 | zhangsan3 |   22 |
| 1104 | zhangsan4 |   20 |
| 1105 | zhangsan5 |   21 |
| 1106 | zhangsan6 |   21 |
| 1107 | zhangsan7 |   22 |
| 1108 | zhangsan8 |   22 |
+------+-----------+------+
8 rows in set (0.00 sec)
没有with rollup的查询:
```

```sql

mysql> select count(*),sage from age group by sage;
+----------+------+
| count(*) | sage |
+----------+------+
|        2 |   20 |
|        3 |   21 |
|        3 |   22 |
+----------+------+
3 rows in set (0.00 sec)
```

带with rollup的查询：

```sql

mysql> select count(*),sage from age group by sage with rollup;
+----------+------+
| count(*) | sage |
+----------+------+
|        2 |   20 |
|        3 |   21 |
|        3 |   22 |
|        8 | NULL |
+----------+------+
4 rows in set (0.00 sec)
```



https://blog.csdn.net/u012736409/article/details/17229713