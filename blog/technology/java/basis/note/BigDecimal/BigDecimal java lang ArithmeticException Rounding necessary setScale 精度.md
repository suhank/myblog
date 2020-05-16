[TOC]



# BigDecimal java lang ArithmeticException Rounding necessary setScale 精度

以下不会出现问题：

```java
System.out.println(new BigDecimal("1225.12").setScale(2));
System.out.println(new BigDecimal("1225.1").setScale(2));
System.out.println(new BigDecimal("1225").setScale(2));
```

如下代码就是导致问题的原因：


```java
 //不会出错
System.out.println(new BigDecimal("1225.120").setScale(2));
//出错原因精度丢失问题，要指定舍入模式即可
System.out.println(new BigDecimal("1225.121").setScale(2));
```

以下是源代码，两个方法设置舍入模式：

```java
 public BigDecimal setScale(int newScale, RoundingMode roundingMode) {
        return setScale(newScale, roundingMode.oldMode);
    }
 public BigDecimal setScale(int newScale, int roundingMode) {//代码省略}
```
以下两种都是一样，前者是定义在enum类中的，后者是BigDecimal本类的定义的静态常量。

```java
new BigDecimal("1225.125").setScale(2, RoundingMode.HALF_UP)
//等价如下
new BigDecimal("1225.121").setScale(2,BigDecimal.ROUND_HALF_UP)
```

以上两种方式解决了精度丢失的问题。





https://blog.csdn.net/qq496013218/article/details/70792655