[TOC]



# java8 实现list中对象属性的去重复

## 一、去除List中重复的String

```
public List<String> removeStringListDupli(List<String> stringList) {
    Set<String> set = new HashSet<>();
    set.addAll(stringList);

    stringList.clear();

    stringList.addAll(set);
    return stringList;
} 
```

 或使用Java8的写法：

```
List<String> unique = list.stream().distinct().collect(Collectors.toList());
```

## 二、根据对象的属性去重

今天在工作的时候遇到了一个问题，就是List的去重，不想用双重for，感觉太low，不想用for+Map，感觉应该有更好的方法，于是，google之。发现java8的stream流能完美解决这个问题。

```java
 List<BookInfoVo> list
```

比如在 `BookInfoVo` 中有一个 `recordId` 属性,现在需要对此去重.

怎么办呢?

有两种方法:

### 第一种: 不使用java8

```java
  private List<BookInfoVo> removeDupliByRecordId(List<BookInfoVo> persons) {
        Set<BookInfoVo> personSet = new TreeSet<>((o1, o2) ->o1.getRecordId().compareTo(o2.getRecordId()));
        personSet.addAll(persons);

        return new ArrayList<BookInfoVo>(personSet);
    }
```

这也是大多数人第一想到的,借助 TreeSet 去重,其中 TreeSet 的其中一个构造函数接收一个排序的算法,同时这也会用到 TreeSet 的去重策略上.

```java
/**
     * Constructs a new, empty tree set, sorted according to the specified
     * comparator.  All elements inserted into the set must be <i>mutually
     * comparable</i> by the specified comparator: {@code comparator.compare(e1,
     * e2)} must not throw a {@code ClassCastException} for any elements
     * {@code e1} and {@code e2} in the set.  If the user attempts to add
     * an element to the set that violates this constraint, the
     * {@code add} call will throw a {@code ClassCastException}.
     *
     * @param comparator the comparator that will be used to order this set.
     *        If {@code null}, the {@linkplain Comparable natural
     *        ordering} of the elements will be used.
     */
public TreeSet(Comparator<? super E> comparator) {
        this(new TreeMap<>(comparator));
    }
 
```

### 第二种: 炫酷的java8写法

```java
 /*方法二:炫酷的java8写法*/
ArrayList<BookInfoVo> distinctLiost = list.stream()
        .collect(
                Collectors.collectingAndThen(
                        Collectors.toCollection(() -> new TreeSet<>(Comparator.comparingLong(BookInfoVo::getRecordId))), ArrayList::new)
        );

```

如果没有第一种方法做铺垫,我们很可能一脸懵逼.

其实理解起来也不难:

关键在于`Collectors.collectingAndThen( Collectors.toCollection(() -> new TreeSet<>(Comparator.comparingLong(BookInfoVo::getRecordId))), ArrayList::new)`的理解,

`collectingAndThen` 这个方法的意思是: **将收集的结果转换为另一种类型: collectingAndThen**,

因此上面的方法可以理解为,把 `new TreeSet<>(Comparator.comparingLong(BookInfoVo::getRecordId))`这个set转换为 ArrayList,这个结合第一种方法不难理解.

可以看到java8这种写法真是炫酷又强大!!!







<https://blog.csdn.net/itguangit/article/details/82350818>