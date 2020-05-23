[TOC]



# BigDecimal 如何比较是否相等



BigDecimal比较大小

这个类是Java里精确计算的类，下面说一下两个BigDecimal对象大小，相等的判断

## 问题实例

比较对象是否相等,一般的对象用于等于，但是BigDecimal比较特殊，举个例子：

```java
BigDecimal a = new BigDecimal("2.00");
BigDecmial b = new BigDecimal(2);
```

 在现实中这两个数字是相等的，但是如果用a.equals(b) 或 a==b 结果都返回false 

结果是假的;怎么不相等了呢？因为equals是比较内容,而等于比较符是比较内存地址，“1.0”和“1.000”二者内容当然不一样了,并且是2个不同的对象,内存地址也不一样
     

## 解决办法：

```java
 if(a.compareTo(b)== 0)  //结果是true
```

 说明:   

```
public int compareTo(BigDecimal val)
```

将此BigDecimal与指定的BigDecimal进行比较。 两个BigDecimal对象的价值相等但具有不同的比例（如2.0和2.00）被认为是相等的这种方法。
         

    方法定义：
    compareTo 定义在Comparable <BigDecimal> 
    参数：
    val - BigDecimal要与此BigDecimal进行比较。
    返回：
       -1，0或1，因为BigDecimal数值小于等于或大于val。

 



https://blog.csdn.net/shadow_zed/article/details/73478298

 