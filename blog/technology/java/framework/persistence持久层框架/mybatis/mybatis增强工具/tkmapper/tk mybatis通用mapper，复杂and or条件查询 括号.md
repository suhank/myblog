# tk mybatis通用mapper，复杂and or条件查询 括号

## 需求：

where查询，需要支持（a or b or c） and d

也就是a、b、c三个条件是或的关系，然后再与d相与。

 

尝试后，可以通过以下方式处理：

## 方式1：Weekend语法

```java
　　　　 Weekend<User> weekend = Weekend.of(User.class);
        //关键字查询部分
        String keyword = pageReq.getKeyword();
        WeekendCriteria<User, Object> keywordCriteria = weekend.weekendCriteria();
        if (StringUtils.isNotEmpty(keyword)) {
            keywordCriteria.orLike(User::getUserName, keyword).orLike(User::getPoliceNo, keyword).orLike(User::getRealName, keyword);　　　　　　  //此处不需要再用下面这一句了,不然上面这个条件组合会重复一次            //weekend.and(keywordCriteria)        }
        
        //部门查询部分
        Example example = new Example(User.class);
        WeekendCriteria<User, Object> criteria = weekend.weekendCriteria();
        criteria.andEqualTo(User::getDepartmentId, departmentId);
        weekend.and(criteria);

        PageHelper.startPage(pageReq.getPageIndex(), pageReq.getPageSize());
        List<User> users = userMapper.selectByExample(weekend);
```

ps：上面，其中Weekend是高版本的通用mapper版本才有，而且需要java8语法支持。

## 方式2：通用example语法：

```java
        Example e = new Example(User.class);
        Example.Criteria c = e.createCriteria();

        //关键字查询部分
        String keyword = pageReq.getKeyword();
        if (StringUtils.isNotEmpty(keyword)) {
            c.orEqualTo("userName", keyword).orEqualTo("policeNo",keyword).orEqualTo("realName",keyword);
        }
        //部门查询部门
        Example.Criteria criteria = e.createCriteria();
        criteria.andEqualTo("departmentId", departmentId);
        e.and(criteria);

        PageHelper.startPage(pageReq.getPageIndex(), pageReq.getPageSize());
        List<User> users = userMapper.selectByExample(e);
```

执行的sql为：

```java
WHERE (
  user_name = ? 
  OR police_no = ? 
  OR real_name = ?
) 
AND (department_id = ?)
```

总结下来，就是，

每个条件组合(a/b/c) （d）各自创建自己的cirteria，再用and或者or方法去连接



https://www.cnblogs.com/grey-wolf/p/8435723.html