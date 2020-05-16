# java 怎样随机选取一个集合list里面的数

```java
//随机选取一个集合里面的数


//1、集合是List的话：
ArrayList<Integer> list = Lists.newArrayList(1, 2, 3, 4, 5, 6);
Integer random = list.get((int) (Math.random() * list.size()));
System.out.println(random);


//2、集合是Set的话：
HashSet<Integer> set = Sets.newHashSet(1, 2, 3, 4, 5, 6);
Integer[] integers = set.toArray(new Integer[0]);
Integer random2 = integers[(int) (Math.random() * integers.length)];
System.out.println(random2);
```





https://blog.csdn.net/Dy_1748204009/article/details/78619176