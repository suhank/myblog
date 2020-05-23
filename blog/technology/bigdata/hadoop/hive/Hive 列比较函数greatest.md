# Hive 列比较函数greatest

> `greatest(col_a, col_b, ..., col_n)`比较n个column的大小，过滤掉`null`，但是当某个column中是string，而其他是int/double/float等时，返回`null`。

------

1. 正常使用：

```sql
select greatest(-1, 0, 5, 8, null) 
from some_table
where dt='2018-06-19'
limit 1
```

返回

```sql
8
```

1. 与str比较

```sql
select greatest(-1, 0, 5, 8, "dfsf") 
from some_table
where dt='2018-06-19'
limit 1
```

返回

```sql
null
```



https://www.jianshu.com/p/0a3ed303ef47