[TOC]



# mysql left join操作中 on与where放置条件的区别

## 优先级

**两者放置相同条件，之所以可能会导致结果集不同，就是因为优先级。on的优先级是高于where的。**

首先明确两个概念：

- LEFT JOIN 关键字会从左表 (table_name1) 那里返回所有的行，即使在右表 (table_name2) 中没有匹配的行。
- 数据库在通过连接两张或多张表来返回记录时，都会生成一张中间的临时表，然后再将这张临时表返回给用户。

在left join下，两者的区别：

- on是在生成临时表的时候使用的条件，不管on的条件是否起到作用，都会返回左表 (table_name1) 的行。
- where则是在生成临时表之后使用的条件，此时已经不管是否使用了left join了，只要条件不为真的行，全部过滤掉。

## 测试

表1：table1

| id   | No   |
| ---- | ---- |
| 1    | n1   |
| 2    | n2   |
| 3    | n3   |

表2：table2

| No   | name |
| ---- | ---- |
| n1   | aaa  |
| n2   | bbb  |
| n3   | ccc  |

```sql
select a.id,a.No,b.name from table1 a left join table2 b on (a.No = b.No and b.name='aaa');
select a.id,a.No,b.name from table1 a left join table2 b on (a.No = b.No) where b.name='aaa'; 
```

第一个结果集：

```
|id |No |name|
|---|---|---|
|1  |n1 |aaa|
|2  |n2 |(Null)|
|3  |n3 |(Null)|     
```

第二个结果集：

```
|id |No |name|
|---|---|---|
|1  |n1 |aaa| 
```

第一个sql的执行流程：首先找到b表的name为aaa的记录行（on (a.No = b.No and b.name=’aaa’) ）。然后找到a的数据（即使不符合b表的规则），生成临时表返回用户。 
第二个sql的执行流程：首先生成临时表，然后执行where过滤b.name=’aaa’不为真的结果集，最后返回给用户。

**因为on会首先过滤掉不符合条件的行，然后才会进行其它运算，所以按理说on是最快的。**

在多表查询时，on比where更早起作用。系统首先根据各个表之间的联接条件，把多个表合成一个临时表后，再由where进行过滤，然后再计算，计算完后再由having进行过滤。由此可见，要想过滤条件起到正确的作用，首先要明白这个条件应该在什么时候起作用，然后再决定放在那里。

对于JOIN参与的表的关联操作，如果需要不满足连接条件的行也在我们的查询范围内的话，我们就必需把连接条件放在ON后面，而不能放在WHERE后面，如果我们把连接条件放在了WHERE后面，那么所有的LEFT,RIGHT,等这些操作将不起任何作用，对于这种情况，它的效果就完全等同于INNER连接。对于那些不影响选择行的条件，放在ON或者WHERE后面就可以。

**记住：所有的连接条件都必需要放在ON后面，不然前面的所有LEFT,和RIGHT关联将作为摆设，而不起任何作用。**



https://blog.csdn.net/cs958903980/article/details/60139792