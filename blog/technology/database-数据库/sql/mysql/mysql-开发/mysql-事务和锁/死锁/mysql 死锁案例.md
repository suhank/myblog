[TOC]



# mysql 死锁案例

## for update 导致死锁

### 准备

ddl

```sql
-- auto-generated definition
CREATE TABLE rt_qty_info
(
  id              BIGINT(18) AUTO_INCREMENT
  COMMENT 'id'
    PRIMARY KEY,
  store_id        INT DEFAULT '0'                NOT NULL
  COMMENT '门店编号',
  item_no         INT DEFAULT '0'                NOT NULL
  COMMENT '商品货号',
  qty             DECIMAL(15, 3) DEFAULT '0.000' NOT NULL
  COMMENT '库存',
  qty_type        TINYINT(2) DEFAULT '0'         NOT NULL
  COMMENT '可卖量类型 0:线下 1:线上',
  store_item_type VARCHAR(64) DEFAULT '0'        NOT NULL
  COMMENT '门店编号,商品货号,可卖量类型',
  CONSTRAINT uk_rqi_store_item_type
  UNIQUE (store_item_type)
)
  COMMENT '门店商品库存表'
  ENGINE = InnoDB;

CREATE INDEX idx_rqi_store_id
  ON rt_qty_info (store_id);

CREATE INDEX idx_rqi_item_no
  ON rt_qty_info (item_no);


```

运行窗口1:

```sql
BEGIN;
# SELECT * from rt_qty_info where store_item_type='1999,1027,1' for UPDATE; 主键或者唯一索引都一样
SELECT * from rt_qty_info where  id='6369' for UPDATE;
COMMIT;

BEGIN;

INSERT INTO   rt_qty_info (id, store_id, item_no, qty, qty_type,store_item_type)
VALUES (6369, 1999, 1027, 5102.000, 1,  '1999,1027,1');

COMMIT;
```

运行窗口2:

```sql
BEGIN;
# SELECT * from rt_qty_info where store_item_type='1999,1026,1' for UPDATE;
SELECT * from rt_qty_info where id='6368' for UPDATE;
COMMIT;

BEGIN;

INSERT INTO   rt_qty_info (id, store_id, item_no, qty, qty_type,   store_item_type)
VALUES (6368, 1999, 1026, 5102.000, 1,  '1999,1026,1');

COMMIT;

```

### 运行步骤:

| 步骤 | 事务1                                                        | 事务2                                                        |
| ---- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| 1    | BEGIN;  SELECT * from rt_qty_info where  id='6369' for UPDATE; |                                                              |
| 2    |                                                              | BEGIN;  SELECT * from rt_qty_info where id='6368' for UPDATE; |
| 3    | INSERT INTO   rt_qty_info (id, store_id, item_no, qty, qty_type,store_item_type) VALUES (6369, 1999, 1027, 5102.000, 1,  '1999,1027,1'); |                                                              |
| 4    |                                                              | INSERT INTO rt_qty_info (id, store_id, item_no, qty, qty_type, store_item_type)  VALUES (6368, 1999, 1026, 5102.000, 1, '1999,1026,1'); |

1.删除表所有数据,运行窗口1执行如下.id='6369'获得锁

```
BEGIN; 
SELECT * from rt_qty_info where  id='6369' for UPDATE;
```

2.运行窗口2执行如下.id='6368'获得锁

```
BEGIN; 
SELECT * from rt_qty_info where id='6368' for UPDATE;
```

3.运行窗口1执行如下sql 获取锁等待

```
INSERT INTO   rt_qty_info (id, store_id, item_no, qty, qty_type,store_item_type)
VALUES (6369, 1999, 1027, 5102.000, 1,  '1999,1027,1');
```

4.运行窗口2执行如下sql 死锁产生

```
INSERT INTO   rt_qty_info (id, store_id, item_no, qty, qty_type,   store_item_type)
VALUES (6368, 1999, 1026, 5102.000, 1,  '1999,1026,1');
```





个人猜测:是间隙锁原因



间隙锁的出现主要集中在同一个事务中先delete 后 insert的情况下， 当我们通过一个参数去删除一条记录的时候， 如果参数在数据库中存在， 那么这个时候产生的是普通行锁， 锁住这个记录， 然后删除， 然后释放锁。如果这条记录不存在，问题就来了， 数据库会扫描索引，发现这个记录不存在， 这个时候的delete语句获取到的就是一个间隙锁，然后数据库会向左扫描扫到第一个比给定参数小的值， 向右扫描扫描到第一个比给定参数大的值， 然后以此为界，构建一个区间， 锁住整个区间内的数据， 一个特别容易出现死锁的间隙锁诞生了。