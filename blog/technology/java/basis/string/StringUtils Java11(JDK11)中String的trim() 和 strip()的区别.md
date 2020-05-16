# StringUtils Java11(JDK11)中String的trim() 和 strip()的区别

org.apache.commons.lang3.StringUtils#strip(java.lang.String) 也一样

JDK11则发布不久，strip()方法是JDK11中新方法，和trim()作用相同，就是去掉字符串的开始和结尾的空白字符。本文主要介绍一下他们之间的区别。

**1、trim()方法不足之处**

trim()早在Java早期就存在，当时Unicode还没有完全发展到我们今天广泛使用的标准。

trim()方法移除字符串两侧的空白字符(空格、tab键、换行符)

支持Unicode的空白字符的判断应该使用isWhitespace(int)。

此外，开发人员无法专门删除缩进空白或专门删除尾随空白。 

简单得说就是，trim()方法无法删除掉Unicode空白字符，但用Character.isWhitespace(c)方法可以判断出来。

**2、strip()方法**

JAVA11(JDK11)中的strip()方法，适用于字符首尾空白是Unicode空白字符的情况，通过一段代码来具体看一下，

```
public static void main(String[] args) {
    String s = "\t abc \n";
       System.out.println( "abc".equals(s.trim()));//true
       System.out.println("abc".equals(s.trim()));//true
       Character c = '\u2000';
       String s1 = c + "abc" + c;
       System.out.println(Character.isWhitespace(c));//true
       System.out.println(s1.equals(s1.trim()));//true
       System.out.println("abc".equals(s1.strip()));//true
}
```

上面输出结果都是true， Character c = '\u2000';中'\u2000'就是Unicdoe空白字符。





https://www.cjavapy.com/article/80/