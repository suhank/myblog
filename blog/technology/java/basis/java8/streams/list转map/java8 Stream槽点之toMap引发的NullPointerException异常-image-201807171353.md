[TOC]



# java8 Stream槽点之toMap引发的NullPointerException异常

## 介绍

众所周知，Java 8 Stream，是我们Java程序员的福音，可以简洁、高效的编写代码，可变通性的帮助我们开发项目，实现业务。然而，现实是，提供具体快捷方式的同时，也存在着潜在的危险，就比如说Stream中的toMap操作，就让博主掉入坑中而不能自拔。

有这样一个场景：我需要通过仓库编号去获取每个订单的发货仓库名称，进而用来填充要查询的订单属性。但是在此之前，我需要获取到全部仓库编号与名称的对应关系。

```java
List<LocationInfo> locationInfos = addressServiceClient.getAllLocationInfos().getData();
Map<Integer, String> locMap = ObjectUtil.isEmpty(locationInfos) ? null : locationInfos.parallelStream().collect(toMap(LocationInfo::getLocNo, LocationInfo::getLocChar, (key1, key2) -> key1, HashMap::new));
```

然而，理想很丰满，现实很骨感。如果LocChar存在null值时，第二行的toMap代码就会抛出NullPointerException异常。这和我们想象的根本不一样，对吧？我们希望的是，即使是null值也无所谓啊，我们大不了不显示，不至于报错啊。而且，我指定的是生成HashMap，它应该支持key/value存在null值才对。

![这里写图片描述](image-201807171353/image-20180717135250413.png)

## 方式一:

但是这个就是事实。就目前来说，有一个替代方案。 
It looks like bad, but it works. As follows:

```java
Map<Integer, String> locMap = locationInfos
.parallelStream()
.collect(HashMap::new, (m, v) -> m.put(v.getLocNo(), v.getLocChar()), HashMap::putAll);1
```

## 方式二:

```java
//这个如果value是null会报空指针异常,所以一定要过滤 null
Map<String, Integer> map = list.stream().filter(m-> Objects.nonNull(m.getMarks())).collect(
        Collectors.toMap(Student::getName, Student::getMarks, (value1, value2) -> value1 + value2 ));
```

如果各位有任何好的解决方案，或者Java 8 Stream针对该问题有哪些更好的策略，可以告诉博主哦！





https://blog.csdn.net/qq_17776287/article/details/79110651