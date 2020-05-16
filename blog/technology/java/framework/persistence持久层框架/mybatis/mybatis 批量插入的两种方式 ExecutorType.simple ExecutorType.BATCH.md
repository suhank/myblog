[TOC]



# mybatis 批量插入的两种方式 ExecutorType.simple ExecutorType.BATCH

摘要: 工作中我们经常会遇到需要批量插入的情况，我将我个人使用mybatis的批量插入分享一下。 高效插入。

## 一、mybiats foreach标签

foreach的主要用在构建in条件中，它可以在SQL语句中进行迭代一个集合。foreach元素的属性主要有 item，index，collection，open，separator，close。item表示集合中每一个元素进行迭代时的别名，index指 定一个名字，用于表示在迭代过程中，每次迭代到的位置，open表示该语句以什么开始，separator表示在每次进行迭代之间以什么符号作为分隔 符，close表示以什么结束，在使用foreach的时候最关键的也是最容易出错的就是collection属性，该属性是必须指定的，但是在不同情况 下，该属性的值是不一样的，主要有一下3种情况：

1. 如果传入的是单参数且参数类型是一个List的时候，collection属性值为list
2. 如果传入的是单参数且参数类型是一个array数组的时候，collection的属性值为array
3. 如果传入的参数是多个的时候，我们就需要把它们封装成一个Map了

###### 具体用法如下:

```xml
<insert id="insertBatch" parameterType="List">
        INSERT INTO TStudent(name,age)
	<foreach collection="list" item="item" index="index" open="("close=")"separator="union all">
	    SELECT #{item.name} as a, #{item.age} as b FROM DUAL
	</foreach>
</insert>
```

## 二、mybatis ExecutorType.BATCH  不推荐

> Mybatis内置的ExecutorType有3种，默认的是simple，该模式下它为每个语句的执行创建一个新的预处理语句，单条提交sql；而batch模式重复使用已经预处理的语句，并且批量执行所有更新语句，显然batch性能将更优； 但batch模式也有自己的问题，比如在Insert操作时，在事务没有提交之前，是没有办法获取到自增的id，这在某型情形下是不符合业务要求的,  所以这个不推荐

###### 具体用法如下:

方式一 spring+mybatis 的

```java
//获取sqlsession
//从spring注入原有的sqlSessionTemplate
@Autowired
private SqlSessionTemplate sqlSessionTemplate;
// 新获取一个模式为BATCH，自动提交为false的session
// 如果自动提交设置为true,将无法控制提交的条数，改为最后统一提交，可能导致内存溢出
SqlSession session = sqlSessionTemplate.getSqlSessionFactory().openSession(ExecutorType.BATCH,false);

    //通过新的session获取mapper
    fooMapper = session.getMapper(FooMapper.class);
    int size = 10000;
    try{
        for(int i = 0; i < size; i++) {
            Foo foo = new Foo();
            foo.setName(String.valueOf(System.currentTimeMillis()));
        //session.insert("com.xx.mapper.UserMapper.insert",foo);
  //session.update("com.xx.mapper.UserMapper.updateByPrimaryKeySelective",foo);
           // session.insert(“包名+类名", foo);
            fooMapper.insert(foo); //这里要么是用session那样去新建，要么是用新获取的mapper
            if(i % 1000 == 0 || i == size - 1) {
             //手动每1000个一提交，提交后无法回滚 
            session.commit();
            //清理缓存，防止溢出
            session.clearCache();
            }
        }
    } catch (Exception e) {
        //没有提交的数据可以回滚
        session.rollback();
    } finally{
        session.close();
    }
    
```

- spring+mybatis

方法二:

> 结合通用mapper sql别名最好是包名＋类名

```java
public void insertBatch(Map<String,Object> paramMap, List<User> list) throws Exception {
		// 新获取一个模式为BATCH，自动提交为false的session
		// 如果自动提交设置为true,将无法控制提交的条数，改为最后统一提交，可能导致内存溢出
		SqlSession session = sqlSessionTemplate.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
		try {
			if(null != list || list.size()>0){
				int  lsize=list.size();
				for (int i = 0, n=list.size(); i < n; i++) {
					User user= list.get(i);
					user.setIndate((String)paramMap.get("indate"));
					user.setDatadate((String)paramMap.get("dataDate"));//数据归属时间
				//session.insert("com.xx.mapper.UserMapper.insert",user);
  //session.update("com.xx.mapper.UserMapper.updateByPrimaryKeySelective",_entity);
                                        session.insert(“包名+类名", user);
					if ((i>0 && i % 1000 == 0) || i == lsize - 1) {
						// 手动每1000个一提交，提交后无法回滚
						session.commit();
						// 清理缓存，防止溢出
						session.clearCache();
					}
				}
			}
		} catch (Exception e) {
			// 没有提交的数据可以回滚
			session.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
	} 
```

========== 经过测试，上面的这个还是对的。 主要是session.insert(“包名+类名", user); 这个一定要设置对。



https://my.oschina.net/u/202293/blog/741949