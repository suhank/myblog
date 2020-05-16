# mysql 字符串函数之repeat用法

## 语法

REPEAT(str,count)

Returns a string consisting of the string str repeated count times. If count is less than 1, returns an empty string. Returns NULL if str or count are NULL.

返回一个由字符串str重复count次数组成的字符串。 如果count小于1，则返回一个空字符串。 如果str或count为NULL，则返回NULL。

## 实例

```mysql
SELECT repeat('MySQL', 3);        # MySQLMySQLMySQL
SELECT repeat('MySQL', -1);       # 
SELECT repeat(NULL , 3);          # null
SELECT repeat('MySQL', NULL );    # null1234


#插入一个长度为8098的字段数据
insert into  table SELECT repeat('a' , 8098);  
```

## 其他

在mysql中有一个关键字也叫REPEAT





https://blog.csdn.net/csdn_0_001/article/details/79499092