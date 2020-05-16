# 【实体 报错 】No serializer found for class org.hibernate.proxy.pojo.javassist.JavassistLazyInitializer and no properties discovered to create BeanSerializer

```
1 HTTP Status 500 - Could not write content: No serializer found for class org.hibernate.proxy.pojo.javassist.JavassistLazyInitializer and no properties discovered to create BeanSerializer (to avoid exception, disable SerializationFeature.FAIL_ON_EMPTY_BEANS) (through reference chain: java.util.ArrayList[0]->com.agen.entity.User["positionchanges"]->org.hibernate.collection.internal.PersistentSet[0]->com.agen.entity.Positionchange["position"]->com.agen.entity.Position_$$_jvst714_7["handler"]); nested exception is com.fasterxml.jackson.databind.JsonMappingException: No serializer found for class org.hibernate.proxy.pojo.javassist.JavassistLazyInitializer and no properties discovered to create BeanSerializer (to avoid exception, disable SerializationFeature.FAIL_ON_EMPTY_BEANS) (through reference chain: java.util.ArrayList[0]->com.agen.entity.User["positionchanges"]->org.hibernate.collection.internal.PersistentSet[0]->com.agen.entity.Positionchange["position"]->com.agen.entity.Position_$$_jvst714_7["handler"])
```

 

解决方式：

1.可以将报错位置的

![img](https://images2015.cnblogs.com/blog/978388/201702/978388-20170204133123761-110949950.png)

修改为

![img](https://images2015.cnblogs.com/blog/978388/201702/978388-20170204133145229-1687198.png)

这是一种解决方式！

 

2.网友解决方法

hibernate会给每个被管理的对象加上hibernateLazyInitializer属性，同时struts-jsonplugin或者其他的jsonplugin都是

因为jsonplugin用的是java的内审机制.hibernate会给被管理的pojo加入一个hibernateLazyInitializer属性,jsonplugin通过java的反射机制将pojo转换成json，会把hibernateLazyInitializer也拿出来操作,但是hibernateLazyInitializer无法由反射得到，所以就抛异常了。 

所以在我的pojo类上加上如下声明：

@JsonIgnoreProperties(value={"hibernateLazyInitializer","handler","fieldHandler"}) 

 

@JsonIgnoreProperties(value={"hibernateLazyInitializer"})   （此时只是忽略hibernateLazyInitializer属性）要加载被lazy的，也就是many-to-one的one端的pojo上

这行代码的作用在于告诉你的jsonplug组件，在将你的代理对象转换为json对象时，忽略value对应的数组中的属性，即：

通过java的反射机制将pojo转换成json的，属性，(通过java的反射机制将pojo转换成json的，)

"hibernateLazyInitializer","handler","fieldHandler",（如果你想在转换的时候继续忽略其他属性，可以在数组中继续加入）





http://www.cnblogs.com/sxdcgaq8080/p/6364736.html