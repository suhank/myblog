# java有没有什么方法对象copy，可以将下划线和驼峰之间转换赋值

maven

```
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>fastjson</artifactId>
    <version>1.2.31</version>
</dependency>
     
<dependency>
    <groupId>cn.hutool</groupId>
    <artifactId>hutool-core</artifactId>
    <version>4.5.16</version>
</dependency>

```



```
    //下划线转驼峰
    @Test
    public void fillBeanWithMapUnderlineToCamelCase() {
        HashMap<String, Object> map = CollectionUtil.newHashMap();
        map.put("name", "Joe");
        map.put("age", 12);
        map.put("open_id", "DFDFSDFWERWER");
        SubPerson person = BeanUtil.fillBeanWithMap(map, new SubPerson(), true, false);
        Assert.assertEquals(person.getName(), "Joe");
        Assert.assertEquals(person.getAge(), 12);
        Assert.assertEquals(person.getOpenId(), "DFDFSDFWERWER");
    }
```

