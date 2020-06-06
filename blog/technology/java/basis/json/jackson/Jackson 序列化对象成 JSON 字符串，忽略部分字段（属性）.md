# Jackson 序列化对象成 JSON 字符串，忽略部分字段（属性）

 发表于 2019-02-28 | 分类于 [JSON](http://zhige.me/categories/JSON/) | 阅读次数： 450

1、属性上 加 @JsonIgnore

这种方式作用于全局，只要是有这个对象的序列化，就会忽略注解过的这部分字段。

2、上面那种方式需要在 bean 上加注解，作用于全局，但是有的时候，我们可能不需要在所有情况下都忽略这个对象的这些字段，下面这种方式可以支持定制过滤方式。

```java
public final class JsonFilterUtil {

    /**
     * 添加过滤的字段，这里过滤的是 ThinActivityInfo 这个 bean 下的 
     * "startAt", "expiredAt", "extra" 三个字段
     */
    public static void addFilterForMapper(ObjectMapper mapper) {
        SimpleBeanPropertyFilter fieldFilter = SimpleBeanPropertyFilter.serializeAllExcept(
                Sets.newHashSet("startAt", "expiredAt", "extra"));
        SimpleFilterProvider filterProvider = new SimpleFilterProvider().addFilter("fieldFilter", fieldFilter);
        mapper.setFilterProvider(filterProvider).addMixIn(ThinActivityInfo.class, FieldFilterMixIn.class);
    }

    /**
     * 定义一个类或接口
     */
    @JsonFilter("fieldFilter")
    interface FieldFilterMixIn{
    }
}
```





http://zhige.me/2019/02/28/2019/02/jackson_serialized_json_ignore_field/