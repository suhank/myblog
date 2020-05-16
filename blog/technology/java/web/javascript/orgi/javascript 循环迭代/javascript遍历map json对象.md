# javascript遍历map json对象

2011年07月07日 17:16:20

阅读数：26554

遍历map

```js
var testMap={"key1":"value1","key2":"value2","key3":new Array("one","two","three")};
for(var key in testMap){
  alert(“testMap[”+key+"]="+testMap[key]);
}
```



https://blog.csdn.net/jbgtwang/article/details/6590889