# jdk8 Stream map和flatMap区别

/Users/jerryye/backup/studio/AvailableCode/basis/streams/streams_demo

##  1.map

将一种类型的值转换为另外一种类型的值。
代码：将List 转换成List

```java
 List<String> collected = Stream.of("a", "b").collect(Collectors.toList());
 
  List<Integer> figure = collected.stream().map(s -> {
            Integer i;
            switch (s) {
                case "a":
                    i = 1;
                    break;
                case "b":
                    i = 2;
                    break;
                default:
                    i = -1;
                    break;
            }
            return i;
        }).collect(Collectors.toList());

```

## 2.flatMap（类似C# AddRange）

- 将多个Stream连接成一个Stream，这时候不是用新值取代Stream的值，与map有所区别，这是重新生成一个Stream对象取而代之。

```java
   List<Integer> a=new ArrayList<>();
        a.add(1);
        a.add(2);
        List<Integer> b=new ArrayList<>();
        b.add(3);
        b.add(4);
        List<Integer> figures=Stream.of(a,b).flatMap(u->u.stream()).collect(Collectors.toList());
        figures.forEach(f->System.out.println(f));

```







## 具体示例

```java
    /**
     * <B>Description:</B>
     * Stream的map和flatMap的区别:
     * map会将一个元素变成一个新的Stream
     * 但是flatMap会将结果打平，得到一个单个元素 <br>
     * <B>Create on:</B> 2018/7/13 下午10:43 <br>
     *
     * @author xiangyu.ye
     */
    @Test
    public void mapAndFlatMapDiff() {
        /**获取单词，并且去重**/
        List<String> list = Arrays.asList("hello welcome", "world hello", "hello world",
                "hello world welcome");

        //map和flatmap的区别
        list.stream().map(item -> Arrays.stream(item.split(" "))).distinct().collect(Collectors.toList()).forEach(System.out::println);
        System.out.println("---------- ");
        list.stream().flatMap(item -> Arrays.stream(item.split(" "))).distinct().collect(Collectors.toList()).forEach(System.out::println);

        //实际上返回的类似是不同的
        List<Stream<String>> listResult = list.stream().map(item -> Arrays.stream(item.split(" "))).distinct().collect(Collectors.toList());
        List<String> listResult2 = list.stream().flatMap(item -> Arrays.stream(item.split(" "))).distinct().collect(Collectors.toList());

        System.out.println("---------- ");

        //也可以这样
        list.stream().map(item -> item.split(" ")).flatMap(Arrays::stream).distinct().collect(Collectors.toList()).forEach(System.out::println);

        System.out.println("================================================");

        /**相互组合**/
        List<String> list2 = Arrays.asList("hello", "hi", "你好");
        List<String> list3 = Arrays.asList("zhangsan", "lisi", "wangwu", "zhaoliu");

        list2.stream()
                .map(item -> list3.stream().map(item2 -> item + " " + item2))
                .collect(Collectors.toList())
                .forEach(e -> System.out.println("相互组合之map:" + JSON.toJSONString(e.collect(Collectors.toList()))));
        list2.stream()
                .flatMap(item -> list3.stream().map(item2 -> item + " " + item2))
                .collect(Collectors.toList())
                .forEach(e -> System.out.println("相互组合之flatMap:" + e));


        //实际上返回的类似是不同的
        List<Stream<String>> list2Result = list2.stream()
                .map(item -> list3.stream().map(item2 -> item + " " + item2))
                .collect(Collectors.toList());
        List<String> list2Result2 = list2.stream()
                .flatMap(item -> list3.stream().map(item2 -> item + " " + item2))
                .collect(Collectors.toList());
    }
```



