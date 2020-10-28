# SQL去重复记录查询的几种方法

1、查找表中多余的重复记录，重复记录是根据单个字段（peopleId）来判断

```sql
select *
from people
where peopleId in (
    select peopleId
    from people
    group by peopleId
    having count(peopleId) > 1
)
```

2、删除表中多余的重复记录，重复记录是根据单个字段（peopleId）来判断，只留有rowid最小的记录

```sql
delete
from people
where peopleId in (
    select peopleId
    from people
    group by peopleId
    having count(peopleId) > 1
)
  and rowid not in (
    select min(rowid)
    from people
    group by peopleId
    having count(peopleId) > 1
)
```

3、查找表中多余的重复记录（多个字段） 

```sql
select *
from vitae a
where (a.peopleId, a.seq) in (
    select peopleId,
           seq
    from vitae
    group by peopleId,
             seq
    having count(*) > 1
)
```

4、删除表中多余的重复记录（多个字段），只留有rowid最小的记录

```sql
delete
from vitae a
where (a.peopleId, a.seq) in (
    select peopleId,
           seq
    from vitae
    group by peopleId,
             seq
    having count(*) > 1
)
  and rowid not in (
    select min(rowid)
    from vitae
    group by peopleId,
             seq
    having count(*) > 1
)
```

5、查找表中多余的重复记录（多个字段），不包含rowid最小的记录

```sql
select *
from vitae a
where (a.peopleId, a.seq) in (
    select peopleId,
           seq
    from vitae
    group by peopleId,
             seq
    having count(*) > 1
)
  and rowid not in (
    select min(rowid)
    from vitae
    group by peopleId,
             seq
    having count(*) > 1
)
```

 



来源： <http://www.jb51.net/article/34820.htm>