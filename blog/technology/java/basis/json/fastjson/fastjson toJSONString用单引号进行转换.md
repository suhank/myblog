# fastjson toJSONString用单引号进行转换

```java
String str = '[{"sourceItemId":"554826261696","itemTitle":"服务商测试商品不发货","itemCount":"1","itemCountMax":""},{"sourceItemId":"554864314455","itemTitle":"测试店铺不发货","itemCount":"1","itemCountMax":""}]';
JSONArray _list_item_ids_update = JSON.parseArray(request.getParameter("_list_item_ids"));
//主要需要添加  SerializerFeature.UseSingleQuotes
JSON.toJSONString(_list_item_ids_update,SerializerFeature.UseSingleQuotes);
```





https://blog.csdn.net/Kindle_code/article/details/75094680