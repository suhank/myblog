[TOC]



# mybatis获取update的id

2018-01-25阅读 2.9K0

平常我门都是更新数据，用更新的条件再查询一次，得到更新的记录。这样我门就进行了两次数据库操作，链接了两次数据库。增加了接口的处理事件，因为链接数据库是很耗时的操作。

其实可以通过 mybatis 的 selectKey 标签来解决这个问题。 selectKey 这个标签大家基本上都用过，比如在插入数据的时候，返回插入数据的纪录。如：

```xml
 <selectKey resultType="int" order="AFTER" keyProperty="id">
            SELECT LAST_INSERT_ID()
 </selectKey>
insert into  ……
```

resultType ：返回的类型，为简单类型。 

order： 在insert into 语句执行后执行。 

keyProperty ： 语句执行结果的 返回目标属性

SELECT LAST_INSERT_ID() 为查询主体。 

此处用法用法就是当 insert into 执行后 执行 selectKey 的内容将数据库的最后一个id 查询出来映射到传入数据对像的ID 属性。

写更新语句，并将更新的纪录的ID 返回出来。 通过 test 的name 去更新 test 的email，并获取被更新纪录的id。

```xml
<update id="updateByUserName" parameterType="com.test.model.User">
 <selectKey keyProperty='id' resultType='int' order='BEFORE'>
            SELECT
            (select id FROM test WHERE
             name = #{name})id
            from DUAL
  </selectKey>
        UPDATE test SET
      email=#{email}
        WHERE
       name =#{name}
  </update>
```

上述代码就是通过 selectKey 实现了 通过 test 的name 去更新 test 的email，并获取被更新纪录的id。

原理

```
 <selectKey keyProperty='id' resultType='int' order='BEFORE'>
```

此处的 keyProperty＝’id’ 是指将查询出来的id 映射到传入updateByUserName 的test 的id 。类型为int 因为可能查到name 以后可能会修改name 所以order=’BEFORE’ 要在执行update之前进行查询，并把id返回出来。 

```
SELECT   (select id FROM test WHERE   name = #{name})id from DUAL 
```

此 SELECT 就是为了获取 被更新的 test的id 外边包装一个虚表查询是当 name = #{name} 查询不到纪录时不会报空纪录，会返回 null ，这个就很关键了。 当返回空记录的时候 mybatis会报错，说不能转换成 int 型。 当返回null的时候就会转换成int 的 0 。不会报错，代表没有查到。

 

https://cloud.tencent.com/developer/article/1029130