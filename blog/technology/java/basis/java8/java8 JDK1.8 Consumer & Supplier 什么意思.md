[TOC]



# java8 JDK1.8 Consumer & Supplier 什么意思

2018-08-24 16:24:21

 

## JDK1.8 函数式接口 Consumer & Supplier 以及 JAVA新纪元 λ表达式的到来

### 背景什么的被吞了,直接进入主题

------

### 函数式接口(定义自己百度,一大堆)

> 因为看了一些关于JDK1.8函数式接口的文章,发现基本上都是糊里糊涂一笔带过.所以就抽空赶紧整理了一下.

还是附上几个学习了解的传送门 :

- [菜鸟教程](http://www.runoob.com/java/java8-functional-interfaces.html)
- [易百教程](https://www.yiibai.com/java8/java8_functional_interfaces.html)
- [汇智网](http://www.hubwiz.com/class/57525f2eda97b6e9299d301b)

------

### Consumer 函数式接口

JDK 源码

```java
/**
 * 接受单个输入参数并且不返回结果的操作。
 * 与大多数其他功能接口不同， Consumer预期通过副作用进行操作。
 *
 * @since 1.8
 */
@FunctionalInterface
public interface Consumer<T> {

    /**
     * 对给定的参数执行此操作。
     *
     * @param t the input argument
     */
    void accept(T t);

    /**
     * 返回一个组合的Consumer ，依次执行此操作，然后执行after操作。 
     * 如果执行任一操作会抛出异常，它将被转发到组合操作的调用者。 
     * 如果执行此操作会引发异常，则不会执行after操作。
     *
     * @param 此操作后执行的操作
     * @return 一个组成的 Consumer ，依次执行 after操作
     * @throws NullPointerException - if after is null
     */
    default Consumer<T> andThen(Consumer<? super T> after) {
        Objects.requireNonNull(after);
        return (T t) -> { accept(t); after.accept(t); };
    }
 
```

刚拿过来看的时候可能会有一些绕,但是我们换个角度来看一下.

Consumer 直译过来就是消费者的意思,那我们是不是可以理解成消费代码.既然他要消费,那我们就要给他提供代码.

**来看一个简单的demo**

```java
public void testConsumer1() {
        Consumer<String> consumer = new Consumer<String>() {
            @Override
            public void accept(String s) {
                System.out.println(s + "?");
            }
        };
        consumer.accept("李磊");
    } 
```

**输出结果**

```java
李磊?1
```

**简单解释一下**

`Consumer`是一个接口,所以当我们直接使用的话,要实现其 `accept()`方法,而这个方法的参数,就是我们定义接口时候给到的泛型,这里给的是一个`String`类型,方法当中的内容,就是我们所谓的消费代码,当调用`accept()`方法时执行.

注意 : 也就是上面提到的通过副作用处理,我不清楚这个单词翻译的是否准确,看了很多博主和一些机器翻译都是这个意思,但我个人的理解意思,更趋近于说是通过侧面来解决问题.

再看一下 `consumer.accept("李磊")`这一句,这里便是真正的执行的地方,也就是调用的我们刚刚自行实现的`accept()`方法.

**让我们继续刚刚的demo往下看**

这种写法和上面在JDK1.8环境中是等价的. 主要就是利用到了1.8中的 λ 表达式.

```java
    public void testConsumer1() {
        Consumer<String> consumer = s -> System.out.println(s + "?");
        consumer.accept("李磊");
    }
12345
下面的例子均使用λ表达式完成
```

**泛型为自定义对象时**

```java
public void testConsumerToSupplier() {
        Consumer<Person> consumer = person -> {
            person.setName("张颖");
            person.setSize(34);
        };
        Person person = new Person();
        consumer.accept(person);
        System.out.println("person = " + person);
    }
12345678910
```

输入结果:

```
person = Person{name='张颖', size=34}1
```

**泛型为自定义接口时**

```java
public interface People {
    void come(Person person);
}123
public void testConsumerAndInterfaceFunction() {
        Consumer<People> consumer = people -> {
            people.come(new Person("李四", 23));
            people.come(new Person("找钱", 34));
            people.come(new Person("孙俪", 45));
        };

        consumer.accept(this::print);
    }

    public void print(Person person) {
        System.out.println("person = " + person);
    }12345678910111213
```

输出结果

```java
person = Person{name='李四', size=23}
person = Person{name='找钱', size=34}
person = Person{name='孙俪', size=45}123
```

如果到了这里还没有明白怎么回事,我建议你亲自动手敲上那么一遍.真的,如果还不懂来杭州,我当面给你讲.

------

### Supplier 函数式接口

还是一样,先看一下JDK源码

```java
/**
 * 获得对象的一个函数式接口
 *
 * @since 1.8
 */
@FunctionalInterface
public interface Supplier<T> {

    /**
     * 得到一个对象
     *
     * @return 目标对象
     */
    T get();
}
 
```

这个是不是看起来很容易理解了,`Supplier`的意思是供应商,那我们是不是可以把他理解成一个商场,然后你告诉他你想要的东西是什么样子的,它是不是就会给你了.

**来看一下这个简单的demo**

```java
void testSupplier1() {
        Supplier<String> supplier = () -> "这是你要的字符串";
        String str = supplier.get();
        System.out.println("str = " + str);
    } 
```

运行结果:

```java
str = 这是你要的字符串1
```

**继续自定义对象**

```java
void testSupplier2() {
        Supplier<Person> supplier = () -> {
            Person person = new Person();
            person.setName("张三");
            person.setSize(32);
            return person;
        };

        Person person = supplier.get();
        System.out.println("person = " + person);
    } 
```

运行结果

```java
person = Person{name='张三', size=32}1
```

**再来刺激的自定义接口**

```java
    void testSupplier3() {
        Supplier<People> supplier = new Supplier<People>() {
            @Override
            public People get() {
                People people = new People() {
                    @Override
                    public void come(Person person) {
                        System.out.println("person = " + person)
                    }
                };
                return people;
            }
        };

        People people = supplier.get();
        people.come(new Person("李四", 24));
    }
 
```

输出结果

```java
person = Person{name='李四', size=24}1
```

**看好别眨眼,λ表达式的写法** 下面的一行和上面的一堆是等价的

```java
void testSupplier4() {
    Supplier<People> supplier = () -> person -> System.out.println("person = " + person);
    People people = supplier.get();
    people.come(new Person("李四", 24));
} 
```

输出结果

```java
person = Person{name='李四', size=24}1
```

想必看到这你不光明白了 `Supplier`的用法,更清楚的λ表达式的用处了.

------

## 写在最后,写这篇文章的原因是因为在整理工厂模式的时候遇到的一些问题

工厂模式简单的是不能再简单了,但是随着技术的发展,也出现了一些新颖的工厂方法.`CTS`便是其中之一.

至于`Consumer`&`Supplier`应用在工厂模式的代码如下,因为比较特殊,写在了一起,想要亲自体检复制粘贴运行`TTT`类的`main()`方法即可

```java
/**
 * @author lvgo
 * @version 1.0
 * @Description: CTS实现工厂模式
 * @date 18-8-24 下午3:57
 */
public interface CTS {

    static CTS getCts(Consumer<Peoples> consumer) {
        Map<String, Supplier<Persons>> map = new HashMap<>();
        consumer.accept(map::put);
        return person -> map.get(person).get();

    }

    Persons getPerson(String name);
}

interface Peoples {
    void come(String name, Supplier<Persons> personSupplier);
}

class TTT {
    public static void main(String[] args) {
        CTS cts = CTS.getCts(people -> {
            people.come("张三", () -> new Persons("张三"));
            people.come("李四", () -> new Persons("李四"));
            people.come("王五", () -> new Persons("王五"));
        });

        Persons person = cts.getPerson("王五");
        System.out.println("persons = " + person);
    }
}

class Persons {

    private String name;

    public Persons() {
    }

    public Persons(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + "'}'";
    }
}
 
```

CTS工厂模式说明: Consumer To Supplier 自造词,无处可寻,他处偶遇纯属抄袭;

通过`Peoples`接口的`come()`方法,可以动态在CTS工厂内添加`person`,然后使其具于生产该实例的能力.

------

- [本文所有源代码点我](https://gitee.com/lvgo/java-design-patterns-cn/blob/master/factory/src/test/java/org/lvgo/CTS.java)
- [更多设计模式学习点我或浏览博客设计模式专栏查看](https://gitee.com/lvgo/java-design-patterns-cn)

------

参考文献

- [Design Patterns: Elements of Reusable Object-Oriented Software 1st Edition](https://www.amazon.com/Design-Patterns-Elements-Reusable-Object-Oriented/dp/0201633612)





<https://blog.csdn.net/sinat_34344123/article/details/82015802>