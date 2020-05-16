[TOC]



# 深入理解Java中的i++、++i语句

## 简介 

在几乎所有的命令式编程语言中，必然都会有i++和++i这种语法。在编程启蒙教材《C语言程序设计》一书中，也专门解释了这两条语句的区别。有些语言中i++和++i既可以作为左值又可以作为右值，笔者专门测试了一下，在`Java`语言中，这两条语句都只能作为右值，而不能作为左值。同时，它们都可以作为独立的一条指令执行。

```java
int i = 0;
int j1 = i++; // 正确
int j2 = ++i; // 正确
i++; // 正确
++i; // 正确

i++ = 5; // 编译不通过
++i = 5; // 编译不通过12345678
```

关于i++和++i的区别，稍微有经验的程序员都或多或少都是了解的，为了文章的完整性，本文也通过实例来简单地解释一下。

```java
{
    int i = 1;
    int j1 = i++;
    System.out.println("j1=" + j1); // 输出 j1=1
    System.out.println("i=" + i); // 输出 i=2
}

{
    int i = 1;
    int j2 = ++i;
    System.out.println("j2=" + j2); // 输出 j2=2
    System.out.println("i=" + i); // 输出 i=2
} 
```

上面的例子中可以看到，无论是i++和++i指令，对于`i`变量本身来说是没有任何区别，指令执行的结果都是i变量的值加1。而对于j1和j2来说，这就是区别所在。

```java
int i = 1;
int j1 = i++; // 先将i的原始值（1）赋值给变量j1（1），然后i变量的值加1
int j1 = ++i; // 先将i变量的值加1，然后将i的当前值（2）赋值给变量j1（2） 
```

上面的内容是编程基础，是程序员必须要掌握的知识点。本文将在此基础上更加深入地研究其实现原理和陷阱，也有一定的深度。在读本文之前，您应该了解：

1. 多线程相关知识
2. Java编译相关知识
3. JMM（Java内存模型）

本文接下来的主要内容包括：

1. Java中i++和++i的实现原理
2. 在使用i++和++i时可能会遇到的一些“坑”

## i++和++i的实现原理

接下来让我们深入到编译后的字节码层面上来了解i++和++i的实现原理，为了方便对比，笔者将这两个指令分别放在2个不同的方法中执行，源代码如下：

```java
public class Test {

    public void testIPlus() {
        int i = 0;
        int j = i++;
    }

    public void testPlusI() {
        int i  = 0;
        int j = ++i;
    }

} 
```

将上面的源代码编译之后，使用`javap`命令查看编译生成的代码（忽略次要代码）如下：

```java
...
{
  ... 

  public void testIPlus();
    descriptor: ()V
    flags: ACC_PUBLIC
    Code:
      stack=1, locals=3, args_size=1
         0: iconst_0               // 生成整数0
         1: istore_1               // 将整数0赋值给1号存储单元（即变量i）
         2: iload_1                // 将1号存储单元的值加载到数据栈（此时 i=0，栈顶值为0）
         3: iinc          1, 1     // 1号存储单元的值+1（此时 i=1）
         6: istore_2               // 将数据栈顶的值（0）取出来赋值给2号存储单元（即变量j，此时i=1，j=0）
         7: return                 // 返回时：i=1，j=0
      LineNumberTable:
        line 4: 0
        line 5: 2
        line 6: 7

  public void testPlusI();
    descriptor: ()V
    flags: ACC_PUBLIC
    Code:
      stack=1, locals=3, args_size=1
         0: iconst_0                // 生成整数0
         1: istore_1                // 将整数0赋值给1号存储单元（即变量i）
         2: iinc          1, 1      // 1号存储单元的值+1（此时 i=1）
         5: iload_1                 // 将1号存储单元的值加载到数据栈（此时 i=1，栈顶值为1）
         6: istore_2                // 将数据栈顶的值（1）取出来赋值给2号存储单元（即变量j，此时i=1，j=1）
         7: return                  // 返回时：i=1，j=1
      LineNumberTable:
        line 9: 0
        line 10: 2
        line 11: 7
}
...
```

## i++和++i在使用时的一些坑

i++和++i在一些特殊场景下可能会产生意想不到的结果，本节介绍两种会导致结果混乱的使用场景，并剖析其原因。

### i = i++的导致的结果“异常”

首先来看一下下面代码执行后的结果。

```java
int i = 0;
i = i++;

System.out.println("i=" + i); // 输出 i=0 
```

正常来讲，执行的结果应该是：`i=1，实际结果却是：i=0，这多少会让人有些诧异。为什么会出现这种情况呢？我们来从编码后的代码中找答案。上面的代码编译后的核心代码如下：`

```java
0: iconst_0                          // 生成整数0
1: istore_1                          // 将整数0赋值给1号存储单元（即变量i，i=0）
2: iload_1                           // 将1号存储单元的值加载到数据栈（此时 i=0，栈顶值为0）
3: iinc          1, 1                // 号存储单元的值+1（此时 i=1）
6: istore_1                          // 将数据栈顶的值（0）取出来赋值给1号存储单元（即变量i，此时i=0）
7: getstatic     #16                 // 下面是打印到控制台指令
10: new           #22               
13: dup
14: ldc           #24                 
16: invokespecial #26                 
19: iload_1
20: invokevirtual #29                
23: invokevirtual #33                 
26: invokevirtual #37                 
29: return123456789101112131415
```

从编码指令可以看出，`i`被栈顶值所覆盖，导致最终`i`的值仍然是`i`的初始值。无论重复多少次`i = i++`操作，最终i的值都是其初始值。

`i++`会产生这样的结果，那么`++i`又会是怎样呢？同样的代码顺序，将`i++`替换成`++i`如下：

```java
int i = 0;
i = ++i; // IDE抛出【The assignment to variable i has no effect】警告

System.out.println("i=" + i); // 输出i=11234
```

可以看到，使用`++i`时出现了“正确”的结果，同时Eclipse IDE中抛出【The assignment to variable i has no effect】警告，警告的意思是将值赋给变量i毫无作用，并不会改变i的值。也就是说：`i = ++i`等价于`++i`。

### 多线程并发引发的混乱

先来看看之前博客中的一个[例子](http://hinylover.space/2016/09/17/relearn-java-thread/#%E7%BA%BF%E7%A8%8B%E5%90%8C%E6%AD%A5)，例子中展示了在多线程环境下由`++i`操作引起的数据混乱。引发混乱的原因是：`++i`操作不是原子操作。

虽然在`Java`中`++i`是一条语句，字节码层面上也是对应`iinc`这条JVM指令`，但是从最底层的CPU层面上来说，++i操作大致可以分解为以下3个指令：`

1. 取数
2. 累加
3. 存储

其中的一条指令可以保证是原子操作，但是3条指令合在一起却不是，这就导致了`++i`语句不是原子操作。

如果变量`i`用`volatile`修饰是否可以保证`++i`是原子操作呢，实际上这也是不行的。至于原因，以后会专门写文章介绍`volatile`等关键词的意义。如果要保证累加操作的原子性，可以采取下面的方法：

1. 将`++i`置于同步块中，可以是`synchronized`或者J.U.C中的排他锁（如[ReentrantLock](http://hinylover.space/2015/09/27/jdk-source-learn-reentrantlock/)等）。
2. 使用原子性（Atomic）类替换`++i`，具体使用哪个类由变量类型决定。如果`i`是整形，则使用`AtomicInteger`类，其中的`AtomicInteger#addAndGet()`就对应着`++i`语句，不过它是原子性操作。

------

本文由[xialei](http://hinylover.space/)原创，转载请说明出处<http://hinylover.space/2017/07/30/java-i-self-increament/>。







<https://blog.csdn.net/xialei199023/article/details/76383013>