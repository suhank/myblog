# List自定义排序 (例子省份排序)

```java
//待排序集合
List<String> list=new ArrayList<String>();
		list.add("辽宁");
		list.add("浙江");
		list.add("河北");
		list.add("山西");
		list.add("内蒙古");
		list.add("北京");
 
//用于规定顺序
final Map<String,Integer> sortSeq=new HashMap<String,Integer>(34){{
			put("北京",1);
			put("天津",2);
			put("河北",3);
			put("山西",4);
			put("内蒙古",5);
			put("辽宁",6);
			put("吉林",7);
			put("黑龙江",8);
			put("上海",9);
			put("江苏",10);
			put("浙江",11);
			put("安徽",12);
			put("福建",13);
			put("江西",14);
			put("山东",15);
			put("河南",16);
			put("湖北",17);
			put("湖南",18);
			put("广东",19);
			put("广西",20);
			put("海南",21);
			put("重庆",22);
			put("四川",23);
			put("贵州",24);
			put("云南",25);
			put("西藏",26);
			put("陕西",27);
			put("甘肃",28);
			put("青海",29);
			put("宁夏",30);
			put("新疆",31);
			put("香港",32);
			put("澳门",33);
			put("台湾",34);
		}
		};
 
	//重写compare方法
	Collections.sort(list, new Comparator(){
 
			@Override
			public int compare(Object arg0, Object arg1) {
				Integer m1= sortSeq.get(arg0.toString());
				Integer m2= sortSeq.get(arg1.toString());
				return m1-m2;
			}
			
		});
		
	for(String s:list){
			System.out.print(s+" ");
		}
		
	}
```



```
输出结果:北京 河北 山西 内蒙古 辽宁 浙江 
```





https://blog.csdn.net/qq_21492635/article/details/77448221