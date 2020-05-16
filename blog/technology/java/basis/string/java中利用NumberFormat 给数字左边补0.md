# java中利用NumberFormat 给数字左边补0

## 方法一:String.format()方法

```java
public class TestStringFormat {         
  public static void main(String[] args) {         
    int youNumber = 1;         
    // 0 代表前面补充0         
    // 4 代表长度为4         
    // d 代表参数为正数型         
    String str = String.format("%04d", youNumber);         
    System.out.println(str);      
  }         
}     
```

## 方法二:用java中的DecimalFormat()方法

```java
private static final String STR_FORMAT = "0000";   
  
public static String test(String liuShuiHao){  
    Integer intHao = Integer.parseInt(liuShuiHao);  
    intHao++;  
    DecimalFormat df = new DecimalFormat(STR_FORMAT);  
    return df.format(intHao);  
}  
```





http://fan2012.iteye.com/blog/774401