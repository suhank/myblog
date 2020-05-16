# StringUtils.abbreviate的方法的使用 字符串长度超长后半部分使用省略号

   

1. 应用场景：当接收的字符串长度不确定，但是确定是非常长，在打印日志时，只打算打印部分，后半部分使用省略号，就可以用
   `org.apache.commons.lang.StringUtils.abbreviate(String str, int maxWidth)`这个方法
   缩减字符串，用省略号代替省略的部分，
   分两种情况
2. 当str的长度小于maxWidth的，则返回str
3. 当maxWidth小于4时，抛出`IllegalArgumentException`异常
   例子：

```java
 StringUtils.abbreviate(null, *)      = null
 StringUtils.abbreviate("", 4)        = ""
 StringUtils.abbreviate("abcdefg", 6) = "abc..."
 StringUtils.abbreviate("abcdefg", 7) = "abcdefg"
 StringUtils.abbreviate("abcdefg", 8) = "abcdefg"
 StringUtils.abbreviate("abcdefg", 4) = "a..."
 StringUtils.abbreviate("abcdefg", 3) = IllegalArgumentException
```





<https://www.jianshu.com/p/4087ef5acef0>