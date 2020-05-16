[TOC]



# spring mongoTemplate简单使用

 

展开



## 1.mongoTemplate如何做or查询

示例如下：

```java
Query query = new Query();  
query.addCriteria(new Criteria().orOperator(Criteria.where("commentEmp._id").is(emp.getId()), Criteria.where("replyEmpId").is(emp.getId())));  
```



## 2.更新集合中的数据的信息

如修改图书的名称时，同时修改员工喜爱图书中该书的名称，示例如下：

```java
mongoTemplate.updateMulti(new Query(Criteria.where("books._id").is("44325")),  
                new Update().set("books.$.name", "updateToName"), Person.class);  
```

[java][view plain](http://blog.csdn.net/gongzi2311/article/details/38061295#) [copy](http://blog.csdn.net/gongzi2311/article/details/38061295#)



1. mongoTemplate.updateMulti(**new** Query(Criteria.where("books._id").is("44325")), 
2. ​        **new** Update().set("books.$.name", "updateToName"), Person.**class**); 

需要特别注意的是要添加占位符'$'，否则更新不生效

## 3.对数值进行加减等操作

如更新某员工的年龄，使年龄加1，如下：

```
mongoTemplate.updateMulti(new Query(Criteria.where("name").is("zhang939")),   
                new Update().inc("age", 1), Person.class);  
```

## 4.查找集合中符合条件的数据

如查找喜欢某某图书的员工的信息，如下：

```
List<Person> list = mongoTemplate.find(  
                new Query(Criteria.where("books.name").is("bookOne").and("books._id").is("19978")), Person.class);  
```


需要注意，数组中的对象根据id查询时必须查'_id'，否则不能查询匹配结果



## 5.findAndModify 方法

该方法找到并修改第一条记录

## 6.删除集合中的数据

如书籍删除时，同时在员工喜爱书籍数组中删除该书，代码如下：

```
mongoTemplate.updateMulti(new Query(Criteria.where("books._id").is("44325")), new Update().pull("books", book), Person.class);  
```

## 7.对查询结果排序

```
Query query = new Query();  
query.sort().on("time", Order.DESCENDING);  
```

## 8.指定查询字段

```
Query query = new Query();  
query.fields().include("name").include("sex");  
```

## 9.分页查询

```
Query query = new Query();  
query.skip(2).limit(3);  
```



[java][view plain](http://blog.csdn.net/gongzi2311/article/details/38061295#) [copy](http://blog.csdn.net/gongzi2311/article/details/38061295#)



1. Query query = **new** Query(); 
2. query.skip(2).limit(3); 
3. 



 



转自：http://blog.csdn.net/gongzi2311/article/details/38061295 感谢作者