# mysql DDL 建表语句结构导出到excel

2020-06-15 15:37:10

​    工作过程中，经常需要把一些sql的DDL 建表语句，导出到EXCEL，或者表格文档中，当作数据字典，供其他人员进行查看和分析，对于在 windows 或者mac 环境的一些mysql可视化工具差异，导致有的可以直接导出，有的则不行。为了导出DDL，还要安装其他支出导出的可视化工具， 甚至是需要破解，成本较大，故参考研究出以下sql 可以直接查询，然后把查询的结果，复制到excel即可，简单方便。

```sql
SELECT
TABLE_NAME 表名,
COLUMN_NAME 列名,
COLUMN_TYPE 数据类型,
DATA_TYPE 字段类型,
CHARACTER_MAXIMUM_LENGTH 长度,
IS_NULLABLE 是否为空,
COLUMN_DEFAULT 默认值,
COLUMN_COMMENT 备注 
FROM
INFORMATION_SCHEMA.COLUMNS
WHERE
-- 对应的数据库
table_schema = 'your_selected_database'
-- 对应库下的具体表名，不加 table_name 则查所有表的字段
and table_name = 'your_selected_table_name'
```

查询结果展示：

![img](image-202011031723/20200615153952922.png)

然后复制结果，直接粘贴到excel 中即可。





https://blog.csdn.net/tangfeng61/article/details/106763430