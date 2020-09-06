# java 仅提取BigDecimal的小数部分？

## 问题

在Java中，我正在使用`BigDecimal`类，并且我的部分代码要求我从其中提取小数部分。 `BigDecimal`似乎没有任何内置方法来帮助我获取`BigDecimal`小数点后的数字。

例如:

```
BigDecimal bd = new BigDecimal("23452.4523434");
```


我想从上面表示的数字中提取`4523434`。最好的方法是什么？

## **最佳答案**

我会尝试`bd.remainder(BigDecimal.ONE)`。  

使用[ `remainder` ](http://docs.oracle.com/javase/8/docs/api/java/math/BigDecimal.html#remainder-java.math.BigDecimal-)方法和[ `ONE` ](http://docs.oracle.com/javase/8/docs/api/java/math/BigDecimal.html#ONE)常量。求余数

```java
BigDecimal bd = new BigDecimal( "23452.4523434" );
BigDecimal fractionalPart = bd.remainder( BigDecimal.ONE ); // Result:  0.4523434
```





https://www.coder.work/article/60480