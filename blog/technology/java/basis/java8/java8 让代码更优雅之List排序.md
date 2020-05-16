[TOC]



# java8 让代码更优雅之List排序

## 先定义一个实体类

```
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Human {

    private String name;
    private int age;
    
}
```

下面的操作都基于这个类来进行操作。这里面使用了[Lombok](https://projectlombok.org/)类库，它用注解的方式实现了基本的get和set等方法，让代码看起来更加的优雅。

## JAVA8之前的List排序操作

在Java8之前，对集合排序只能创建一个匿名内部类

```
new Comparator<Human>() {
    @Override
    public int compare(Human h1, Human h2) {
        return h1.getName().compareTo(h2.getName());
    }
}
```

下面是简单的对Humans进行排序（按名称正序）

```
@Test
public void testSortByName_with_plain_java() throws Exception {

   ArrayList<Human> humans = Lists.newArrayList(
           new Human("tomy", 22),
           new Human("li", 25)
   );

   Collections.sort(humans, new Comparator<Human>() {
         
       public int compare(Human h1, Human h2) {
           return h1.getName().compareTo(h2.getName());
       }
   });

   Assert.assertThat(humans.get(0), equalTo(new Human("li", 25)));
}
```

## 使用Lambda的List排序

使用JAVA8函数式方式的比较器

```
(Human h1, Human h2) -> h1.getName().compareTo(h2.getName())
```

下面是使用JAVA8函数式的比较的例子

```
@Test
public void testSortByName_with_lambda() throws Exception {

   ArrayList<Human> humans = Lists.newArrayList(
           new Human("tomy", 22),
           new Human("li", 25)
   );
   humans.sort((Human h1, Human h2) -> h1.getName().compareTo(h2.getName()));

   Assert.assertThat("tomy", equalTo(humans.get(1).getName()));

}
```

## 没有类型定义的排序

对于上面的表达式还可以进行简化，JAVA编译器可以根据上下文推测出排序的类型：

```
(h1, h2) -> h1.getName().compareTo(h2.getName())
```

简化后的比较器是这样的:

```
@Test
public void testSortByNameSimplify_with_lambda() throws Exception {

   ArrayList<Human> humans = Lists.newArrayList(
           new Human("tomy", 22),
           new Human("li", 25)
   );
   humans.sort((h1, h2) -> h1.getName().compareTo(h2.getName()));

   Assert.assertThat("tomy", equalTo(humans.get(1).getName()));

}   
```

## 使用静态方法引用

JAVA8还可以提供使用Lambda表达式的静态类型引用，我们在Human类增加一个静态比较方法，如下：

```
public static int compareByNameThenAge(Human h1, Human h2) {

   if (h1.getName().equals(h2.getName())) {
       return Integer.compare(h1.getAge(), h2.getAge());
   }
   return h1.getName().compareTo(h2.getName());
}
```

然后就可以在humans.sort使用这个引用

```
@Test
public void testSort_with_givenMethodDefinition() throws Exception {

   ArrayList<Human> humans = Lists.newArrayList(
           new Human("tomy", 22),
           new Human("li", 25)
   );
   humans.sort(Human::compareByNameThenAge);
   Assert.assertThat("tomy", is(equalTo(humans.get(1).getName())));
}
```

## 使用单独的Comparator

JAVA8已经提供了很多方便的比较器供我们使用，比如Comparator.comparing方法，所以可以使用Comparator.comparing方法来实现根据Human的name进行比较的操作：

```
@Test
public void testSort_with_givenInstanceMethod() throws Exception {

   ArrayList<Human> humans = Lists.newArrayList(
           new Human("tomy", 22),
           new Human("li", 25)
   );

   Collections.sort(humans, Comparator.comparing(Human::getName));
   Assert.assertThat("tomy", equalTo(humans.get(1).getName()));
}
```

## 反序

JDK8中也提供了一个支持倒序排序的方法方便我们更快的进行倒序

```
@Test
public void testSort_with_comparatorReverse() throws Exception {

   ArrayList<Human> humans = Lists.newArrayList(
           new Human("tomy", 22),
           new Human("li", 25)
   );

   Comparator<Human> comparator = (h1, h2) -> h1.getName().compareTo(h2.getName());
   humans.sort(comparator.reversed());
   Assert.assertThat("tomy", equalTo(humans.get(0).getName()));

}
```

## 使用多个条件进行排序

Lambda提供了更复杂的表达式，还可以先对name排序再根据age进行排序：

```java
@Test
public void testSort_with_multipleComparator() throws Exception {

   ArrayList<Human> humans = Lists.newArrayList(
           new Human("tomy", 22),
           new Human("li", 25)
   );

   Comparator<Human> comparator = (h1, h2) -> {

       if (h1.getName().equals(h2.getName())) {
           return Integer.compare(h1.getAge(), h2.getAge());
       }
       return h1.getName().compareTo(h2.getName());
   };
   humans.sort(comparator.reversed());
   Assert.assertThat("tomy", equalTo(humans.get(0).getName()));

}
```

## 使用多个条件进行排序-组合的方式  推荐重点

Comparator对这种组合的排序有更优雅实现，从JDK8开始，我们可以使用链式操作进行复合操作来构建更复杂的逻辑：

```java
@Test
public void testSort_with_multipleComparator_composition() throws Exception {

   ArrayList<Human> humans = Lists.newArrayList(
           new Human("tomy", 22),
           new Human("tomy", 25)
   );

   humans.sort(Comparator.comparing(Human::getName).thenComparing(Human::getAge));
   Assert.assertThat(humans.get(0), equalTo(new Human("tomy", 22)));
}
```

## 总结

JDK8真的是一个非常值得我们学习的版本，它提供了Lambda表达式，带来了函数式编程的理念，让JAVA代码更优雅





https://my.oschina.net/HJCui/blog/1573344