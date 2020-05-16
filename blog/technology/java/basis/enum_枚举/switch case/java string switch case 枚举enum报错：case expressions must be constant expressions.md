[TOC]



# java string switch case 枚举enum报错：case expressions must be constant expressions

## 1. 问题描述

​    今天在代码中想对Java switch case 和枚举类型Enum对象进行联合使用，但发现有Eclipse中异常提示信息：case expressions must be constant expressions，导致编译始终过不去。

枚举类型定义如下：

```java
public enum TradeStatus {  
       CLOSE(-1, "已关闭"), NO_TRADE(0, "未创建"), CREATE(1, "拍下"), PAY(2, "已付款"), SHIP(3, "已发货"), SUCCESS(4, "已完成");  
  
       private int value;  
       private String name;  
  
       private TradeStatus(int value, String name) {  
           this.setValue(value);  
           this.setName(name);  
       }  
  
       public int getValue() {  
           return value;  
       }  
  
       public void setValue(int value) {  
           this.value = value;  
       }  
  
       public String getName() {  
           return name;  
       }  
  
       public void setName(String name) {  
           this.name = name;  
       }  
  
       public static TradeStatus getByValue(int value) {  
           for (TradeStatus tradeStatus : values()) {  
               if (tradeStatus.getValue() == value) {  
                   return tradeStatus;  
               }  
           }  
           return null;  
       }  
   }  
```


   使用代码入下：

```java
private String getStatusDesc(Integer tradeStatus) {  
        switch(tradeStatus){  
        case TradeStatus.CREATE.getValue() :   
            break;  
        default:  
            break;  
        }  
        return "交易状态";  
    }  
```




##      2. 原因分析

​       本意是想对tradeStatus值进行分类过滤，但由于 TradeStatus.CREATE.getValue() 返回值是一个变量不符合Java switch case的语法，导致报错。

​      接下来修改了代码，如下：

```java
private String getStatusDesc(Integer tradeStatus) {  
        switch (TradeStatus.getByValue(tradeStatus)) {  
        case OrderInfoSearchDO.TradeStatus.CREATE:  
            break;  
        default:  
            break;  
        }  
        return "交易状态";  
    }  
```

​         但Eclipse继续提示错误信息：The qualified case label TradeStatus.CREATE must be replaced with the unqualified enum constant CREATE。。。

百思不得其解。 

## 3. 问题解决

​    把枚举常量前的冗余类信息去掉即可，如下所示：

```java
private String getStatusDesc(Integer tradeStatus) {  
        switch (TradeStatus.getByValue(tradeStatus)) {  
        case CREATE:  
            break;  
        default:  
            break;  
        }  
        return "交易状态";  
    } 
```






http://nuistcc.iteye.com/blog/2263782