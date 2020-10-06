# java实用数据结构Pair、MutablePair、ImmutablePair详解（推荐apache的commons组件提供）

2019-09-03阅读 1.5K0

------

## 每篇一句

>  最可怕的事情不是别人比你优秀，而是比你优秀的人还比你努力 

## 前言

我们讨论了一个非常有用的编程概念，配对(Pair)。配对提供了一种方便方式来处理简单的键值关联，当我们想从方法返回两个值时特别有用。

我们平时写代码的时候经常会遇到要返回多个元素的情况，这时我们大多数时间都是使用数组或者map或者json的方式来实现的，而common-lang包提供了组件的方式来返回多个参数，**我们这片文章要介绍的是Pair接口，返回一对数据Pair抽象类，它集成了Map.Entry接口；（这个由apache提供）**

Pair是一个抽象类，这个类是定义基本API的抽象实现，它指的是左右两个元素，它也实现了Map.Entry接口，也就是key是左元素，value是右元素；

子类实现的是可能是可变的也可能是不可变的，然而对存储的对象类型是没有限制的，如果可变的[对象存储](https://cloud.tencent.com/product/cos?from=10680)在Pair中，那么Pair对象也会变为可变的；

## 市面上的实现

#### javafx.util 包中

```javascript
Pair<Integer, String> pair = new Pair<>(1, "One");
Integer key = pair.getKey();
String value = pair.getValue();
```

#### JDK自带内部类：AbstractMap.SimpleEntry 和AbstractMap.SimpleImmutableEntry

SimpleEntry定义在抽象类AbstractMap里面，其构造方法与Pair类似：

```javascript
AbstractMap.SimpleEntry<Integer, String> entry  = new AbstractMap.SimpleEntry<>(1, "one");
Integer key = entry.getKey();
String value = entry.getValue();
```

另外AbstractMap 类还包含一个嵌套类，表示不可变配对：SimpleImmutableEntry 类。

```javascript
AbstractMap.SimpleImmutableEntry<Integer, String> entry = new AbstractMap.SimpleImmutableEntry<>(1, "one");
```

应用方式与可变的配对一样，除了配置的值不能修改，尝试修改会抛出UnsupportedOperationException异常。

#### Vavr库：这个本文不做描述。太偏门了

#### Apache Commons：组件提供的Pair（下问列出专门篇幅讲述，推荐使用）

------

## Apache Commons提供的Pair、MutablePair、ImmutablePair详解

组件类是在包org.apache.commons.lang3.tuple下 Pair抽象类部分源码申明如下： 不可直接实例化 **它虽然提供了静态方法，但实际返回的是不可变的ImmutablePair。**

```javascript
public abstract class Pair<L, R> implements Map.Entry<L, R>, Comparable<Pair<L, R>>, Serializable {

    private static final long serialVersionUID = 4954918890077093841L;
  /**
 * 返回一个不可变的ImmutablePair对象,即:左元素不可变、右元素不可变，Map的值也不可变
   */
    public static <L, R> Pair<L, R> of(final L left, final R right) {
        return new ImmutablePair<>(left, right);
    }
   /**
 * 获取左元素
   */
    public abstract L getLeft();
   /**
 * 获取右元素
   */
    public abstract R getRight();
   /**
 * 从Map中获取key
   */
    @Override
    public final L getKey() {
        return getLeft();
    }
   /**
 * 获取Map中value
   */
    @Override
    public R getValue() {
        return getRight();
    }
}
```

- ImmutablePair不可变的左右元素对
- MutablePair可以改变值得Pair左右元素对

```javascript
   public static void main(String[] args) throws Exception {
        Pair<Integer, Integer> pair = Pair.of(1, 10); //同ImmutablePair.of(1, 10)
        Integer left = pair.getLeft();
        Integer right = pair.getRight();
        System.out.println(left); //1
        System.out.println(right); //10
        //pair.setValue(30); //报错：java.lang.UnsupportedOperationException


        pair = MutablePair.of(1, 10);
        left = pair.getLeft();
        right = pair.getRight();
        ((MutablePair<Integer, Integer>) pair).setLeft(100);
        ((MutablePair<Integer, Integer>) pair).setRight(200);
        System.out.println(left); //100
        System.out.println(right); //200
        pair.setValue(200); //备注setValue同setRight方法

    }
```

例子使用起来非常的简单，只是这种数据结构很多时候确认非常好用。 **比如返回夫妻两个、返回一对双胞胎等等，一下就搞定了。**

## 扩展使用：Triple、MutableTriple、ImmutableTriple 一次性返回三个对象

使用办法基本同上

```javascript
   public static void main(String[] args) throws Exception {
        Triple<String, Integer, String> pair = Triple.of("姓名", 10, "邮箱");
        String name = pair.getLeft(); //姓名
        Integer age = pair.getMiddle(); //10
        String mail = pair.getRight(); //邮箱
        System.out.println(name);
        System.out.println(age);
        System.out.println(mail);
    }
```

## 最后

很多人问，这个和Map有什么区别？

我想举个例子： 一头驴和一群马的区别。

因为common.lang3一般为必导的包。所以我建议使用这里面的额数据结构

>  说明：Pair不能当作Controller层的返回值，或者入参。会出问题，因为它不是标准的javaBean，序列化和反序列化会出问题。一般用于系统内部，比如Service方法直接、工具方法之间传递数据，是一把利器 





https://cloud.tencent.com/developer/article/1497510