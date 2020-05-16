[TOC]



# mysql 不存在则插入，存在则更新或忽略

 

## 前言

在插入数据时，可能需要忽略或替换掉重复的数据（依据某个字段），这时可以在应用层处理，也可以使用复杂的 SQL 语句来处理（如果仅仅知道一些简单的 SQL 语法的话），当然也可以使用一些简单的 SQL 语法，不过它并不是通用所有的数据库类型。

> 以下所有实例仅针对`MySQL`而言，并不能随意用于其它数据库

## 实例

表名称：student

表字段：

| Column Name | Primary Key | Auto Increment | Unique |
| ----------- | ----------- | -------------- | ------ |
| id          | true        | true           |        |
| name        |             |                | true   |
| age         |             |                |        |

初始表数据：

| id   | name | age  |
| ---- | ---- | ---- |
| 1    | Jack | 18   |

> 注：以下所有的示例都需要被插入的数据中需要存在`UNIQUE索引`或`PRIMARY KEY`字段，同时这里引入表的主键`id`，并设置成`自动递增`，后面可以看到它的变化

### 1. 不存在则插入，存在则更新

#### 1.1 on duplicate key update

如果插入的数据会导致`UNIQUE 索引`或`PRIMARY KEY`发生冲突/重复，则**执行UPDATE语句**，例：

```mysql
INSERT INTO `student`(`name`, `age`) VALUES('Jack', 19)
  ON DUPLICATE KEY 
  UPDATE `age`=19; -- If will happen conflict, the update statement is executed

-- 2 row(s) affected
```

这里受影响的行数是`2`，因为数据库中存在`name='Jack'`的数据，如果不存在此条数据，则受影响的行数为`1`

最新的表数据如下：

| id   | name | age  |
| ---- | ---- | ---- |
| 1    | Jack | 19   |

#### 1.2 replace into

如果插入的数据会导致`UNIQUE 索引`或`PRIMARY KEY`发生冲突/重复，则**先删除旧数据再插入最新的数据**，例：

```mysql
REPLACE INTO `student`(`name`, `age`) VALUES('Jack', 18);

-- 2 row(s) affected 
```

这里受影响的行数是`2`，因为数据库中存在`name='Jack'`的数据，并且`id`的值会变成`2`，因为它是先删除旧数据，然后再插入数据，最新的表数据如下：

| id   | name | age  |
| ---- | ---- | ---- |
| 2    | Jack | 19   |

### 2. 避免重复插入

关键字/句：`insert ignore into`，如果插入的数据会导致`UNIQUE索引`或`PRIMARY KEY`发生冲突/重复，则忽略此次操作/不插入数据，例：

```
INSERT IGNORE INTO `student`(`name`, `age`) VALUES('Jack', 18);

-- 0 row(s) affected123
```

这里已经存在`name='Jack'`的数据，所以会忽略掉新插入的数据，受影响行数为`0`，表数据不变。

以上。





https://blog.csdn.net/t894690230/article/details/77996355