# kibana json格式收集日志问题



系统打印日志,不能是标准的json格式否则会识别不了.



解决办法:

json打印出来 用单引号

```
fastjson toJSONString用单引号进行转换


//主要需要添加  SerializerFeature.UseSingleQuotes
JSON.toJSONString(_list_item_ids_update,SerializerFeature.UseSingleQuotes);
```

