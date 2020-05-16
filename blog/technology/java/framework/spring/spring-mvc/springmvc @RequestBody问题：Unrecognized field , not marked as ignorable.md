# springmvc @RequestBody问题：Unrecognized field , not marked as ignorable

在前台传递JSON串到后台.由后台将JSON转成实体类对象时,出现一下异常信息
Unrecognized field "pager.pageSize" (Class xxxxx.AlxxxxxBean), not marked as ignorable
原因是因为前台传递的JSON串中包涵了目标java实体类没有的属性.

## 解决方法有:

1.@JsonIgnoreProperties(ignoreUnknown = true) 
在对应的实体类加上注解,表示可以忽略该目标对象不存在的属性,
该注解属于import org.codehaus.jackson.annotate.JsonIgnoreProperties;

2.格式化输入内容，保证传入的JSON串不包含目标对象的没有的属性。

3.全局DeserializationFeature配置

objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
配置该objectMapper在反序列化时，忽略目标对象没有的属性。凡是使用该objectMapper反序列化时，都会拥有该特性。 



https://my.oschina.net/MrBamboo/blog/904651