# Hive 修改table、column

2017.06.12 22:57:19字数 90阅读 12,118

### 表

1、重命名表重命名表的语句如下：

```sql
ALTER TABLE table_name RENAME TO new_table_name 
```

2、修改表属性：

```sql
ALTER TABLE table_name SET TBLPROPERTIES (property_name = property_value, property_name = property_value,... )  
```

3、修改表注释

```sql
ALTER TABLE table_name SET TBLPROPERTIES('comment' = new_comment);  
```

### 列

1、添加列

```sql
-- Add/Replace Columns 语法
ALTER TABLE table_name ADD|REPLACE
  COLUMNS (col_name data_type [COMMENT col_comment], ...)

--【注】ADD COLUMNS 允许用户在当前列的末尾增加新的列，但是在分区列之前。

-- 添加 a 列表注释：类型
alter table test_change add columns  (a STRING COMMENT '类型')

-- 将 a 列的名字改为 a1，a 列的数据类型改为 string，并将它放置在列 b 之后。
ALTER TABLE test_change CHANGE a a1 STRING AFTER b;

-- 将 b 列的名字修改为 b1, 并将它放在第一列。
ALTER TABLE test_change CHANGE b b1 INT FIRST

注意：对列的改变只会修改Hive的元数据，而不会改变实际数据。用户应该确定保证元数据定义和实际数据结构的一致性。
```

2、修改列

```sql
--- Change Column Name/Type/Position/Comment 语法
ALTER TABLE table_name CHANGE [COLUMN]
  col_old_name col_new_name column_type
    [COMMENT col_comment]
    [FIRST|AFTER column_name]

--- Change Column Name/Type/Position/Comment 案例
CREATE TABLE test_change (a int, b int, c int);
ALTER TABLE test_change CHANGE a a1 INT; --将 a 列的名字改为 a1.

--将 a 列的名字改为 a1，a 列的数据类型改为 string，并将它放置在列 b 之后。新的表结构为： b int, a1 string, c int.
ALTER TABLE test_change CHANGE a a1 STRING AFTER b; 

--将 b 列的名字修改为 b1, 并将它放在第一列。新表的结构为： b1 int, a string, c int.
ALTER TABLE test_change CHANGE b b1 INT FIRST; 
```

### 分区

1、增加分区

```sql
--Add Partitions 语法
ALTER TABLE table_name ADD
  partition_spec [ LOCATION 'location1' ]
  partition_spec [ LOCATION 'location2' ] ...

partition_spec:
  : PARTITION (partition_col = partition_col_value,
        partition_col = partiton_col_value, ...)

--Add Partitions 语法案例：用户可以用 ALTER TABLE ADD PARTITION 来向一个表中增加分区。当分区名是字符串时加引号。

  ALTER TABLE page_view ADD
    PARTITION (dt='2008-08-08', country='us')
      location '/path/to/us/part080808'
    PARTITION (dt='2008-08-09', country='us')
      location '/path/to/us/part080809';
```

2、修改分区

3、删除分区

```sql
---DROP PARTITION 删除分区
ALTER TABLE table_name DROP
    partition_spec, partition_spec,...
```

### 参考资料

[1、 Hive学习之修改表、分区、列](https://link.jianshu.com/?t=http://blog.csdn.net/skywalker_only/article/details/30224309)
[2、hive操作create，alter等](https://link.jianshu.com/?t=http://www.cnblogs.com/xd502djj/archive/2013/01/16/2863053.html)



 

https://www.jianshu.com/p/9088fe002e2a