# mysql中谓词使用date_format的优化  时间日期使用索引

优化前：

```mysql
SELECT a.* FROM t1 a,

(SELECT obj_id,MAX(PRE_DETAIL_INST_ID) PRE_DETAIL_INST_ID FROM t1 WHERE DATE_FORMAT(crt_date,'%Y-%m-%d %H') < DATE_FORMAT(NOW(),'%Y-%m-%d %H') AND 

DATE_FORMAT(crt_date,'%Y-%m-%d %H') >= DATE_FORMAT(DATE_ADD(NOW(),INTERVAL -1 HOUR),'%Y-%m-%d %H') GROUP BY obj_id) b

WHERE a.pre_detail_inst_id = b.pre_detail_inst_id;
```

索引字段：

```shell
+---------------------+------------+---------------------------+--------------+--------------------+-----------+-------------+----------+--------+------+------------+---------+---------------+
| Table | Non_unique | Key_name  | Seq_in_index | Column_name  | Collation | Cardinality | Sub_part | Packed | Null | Index_type | Comment | Index_comment |
+---------------------+------------+---------------------------+--------------+--------------------+-----------+-------------+----------+--------+------+------------+---------+---------------+
| t1|   0 | PRIMARY                   | 1 | PRE_DETAIL_INST_ID | A  |       14897 |     NULL | NULL   |      | BTREE      |         |               |
| t1|   1 | PRE_INST_OBJ_ID_xxx | 1 | OBJ_ID             | A  |       14897 |     NULL | NULL   | YES  | BTREE      |         |               |
| t1|   1 | PRE_INST_PRE_INST_ID_xxx | 1 | PRE_INST_ID        | A  |       14897 |     NULL | NULL   | YES  | BTREE      |         |               |
| t1|   1 | pre_inst_pre_rule_id_xxx | 1 | PRE_RULE_ID        | A  |        1354 |     NULL | NULL   | YES  | BTREE      |         |               |
| t1|   1 | idx_bil_cust_order_id_xxx | 1 | CUST_ORDER_ID      | A  |        1489 |     NULL | NULL   | YES  | BTREE      |         |               |
| t1|   1 | idx_crt_date              | 1 | CRT_DATE           | A  |        1354 |     NULL | NULL   | YES  | BTREE      |         |               |
+---------------------+------------+---------------------------+--------------+--------------------+-----------+-------------+----------+--------+------+------------+---------+---------------+
```

执行计划如下：

```shell
+----+-------------+---------------------+--------+---------------------+---------------------+---------+----------------------+-------+-------------+
| id | select_type | table               | type   | possible_keys       | key                 | key_len | ref                  | rows  | Extra       |
+----+-------------+---------------------+--------+---------------------+---------------------+---------+----------------------+-------+-------------+
|  1 | PRIMARY     | <derived2>          | ALL    | NULL                | NULL                | NULL    | NULL                 | 14897 | Using where |
|  1 | PRIMARY     | a                   | eq_ref | PRIMARY             | PRIMARY             | 8       | b.PRE_DETAIL_INST_ID |     1 | NULL        |
|  2 | DERIVED     | tb_bil_pre_inst_xxx | index  | PRE_INST_OBJ_ID_xxx | PRE_INST_OBJ_ID_xxx | 9       | NULL                 | 14897 | Using where |
+----+-------------+---------------------+--------+---------------------+---------------------+---------+----------------------+-------+-------------+
```

对谓词crt_date加date_format函数无法使用索引。从而导致使用全表扫描。由于是innodb引擎，且无法使用索引导致行锁升级为表锁，在高并发环境下，导致大量的等待。

数据量： 2500000

执行时间：00:00:16:274

优化后：

```mysql
SELECT a.* FROM
(SELECT * FROM t1 WHERE crt_date >= CONCAT(DATE_FORMAT(DATE_SUB(NOW(),INTERVAL 1 HOUR),'%Y-%m-%d %H'),':00:00')
 AND crt_date < CONCAT(DATE_FORMAT(NOW(),'%Y-%m-%d %H'),':00:00')) a,
(SELECT obj_id,MAX(pre_detail_inst_id) pre_detail_inst_id FROM t1 WHERE crt_date >= CONCAT(DATE_FORMAT(DATE_SUB(NOW(),INTERVAL 1 HOUR),'%Y-%m-%d %H'),':00:00')
 AND crt_date < CONCAT(DATE_FORMAT(NOW(),'%Y-%m-%d %H'),':00:00') GROUP BY obj_id) b
WHERE a.pre_detail_inst_id = b.pre_detail_inst_id;
```

执行计划如下：

```sh
+----+-------------+---------------------+-------+--------------------------------------+------------------+---------+----------------------+------+--------------------------------------------------------+
| id | select_type | table               | type  | possible_keys                        | key              | key_len | ref                  | rows | Extra                                                  |
+----+-------------+---------------------+-------+--------------------------------------+------------------+---------+----------------------+------+--------------------------------------------------------+
|  1 | PRIMARY     | <derived2>          | ALL   | NULL                                 | NULL             | NULL    | NULL                 |    2 | NULL                                                   |
|  1 | PRIMARY     | <derived3>          | ref   | <auto_key0>                          | <auto_key0>      | 9       | a.PRE_DETAIL_INST_ID |    2 | NULL                                                   |
|  3 | DERIVED     | t1| range | PRE_INST_OBJ_ID_xxx ,idx_crt_date_xxx | idx_crt_date_xxx | 6       | NULL                 |    1 | Using index condition; Using temporary; Using filesort |
|  2 | DERIVED     | t1| range | idx_crt_date_xxx | idx_crt_date_xxx | 6       | NULL                 |    1 | Using index condition                                  |
+----+-------------+---------------------+-------+--------------------------------------+------------------+---------+----------------------+------+--------------------------------------------------------+
```

改进crt_date，mysql使用索引范围查找，利用行锁，规避了表锁和高并发下的表锁等待问题。

数据量： 2500000

执行时间：00:00:00:188





```
and create_time >= CONCAT(DATE_FORMAT('2018-06-20','%Y-%m-%d'),' 00:00:00')
and create_time <= CONCAT(DATE_FORMAT('2018-06-20','%Y-%m-%d'),' 23:59:59')
```

  

https://www.cnblogs.com/jandison/p/4261838.html