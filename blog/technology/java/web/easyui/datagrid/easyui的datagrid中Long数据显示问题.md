# easyui的datagrid中Long数据显示问题

## 问题

感觉easyui挺不错的，于是想把原来用SSH2做的工程中加入easyui，结果发现datagrid在显示Long类型数据时有问题。 问题如下：比如一个数据ID为20121229101239002，经过转换之后的JSON数据也没有问题，但是在显示的时候就会显示为20121229101239000，自己感觉可能是JAVASCRIPT对长数据支持的问题，可是又不知道怎么转换成String类型，大侠们来帮帮忙吧，难道自己只能在后台对数据进行一次转换再传到前台吗？



## 解决办法:

生成JSON的时候用""括起就是字符串了，如果是用组件动态生成的，那你要修改你的对象，不要用long类型，用string的  要不就自己组合成json串，而不是使用组件,



就是后台long类型转成String在传给页面.



下面是从数据库查询出来,直接传到页面的特殊处理demo

```
		//直接从数据库查询出来的map list
		List list = pageDao.queryForList(queryName, paramMap, startRow, pageSize);
		
		//循环处理Long类型转为string    修复: 数值比较大的时候显示不准确 例如:数据库中是202397231104917504,页面显示为202397231104917500
		for (Object o : list) {
			if (o instanceof  HashMap){
				HashMap map = (HashMap) o;
				for (Object key : map.keySet()) {
					Object value = map.get(key);
					if (value!=null && value instanceof Long){
							map.put(key,value.toString());
					}
				}
			}
		}
```





https://bbs.csdn.net/topics/390332817