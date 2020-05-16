[TOC]



# java进制转换

## 16进制内

```java
public class Test {  
    public static void main(String[] args) throws Exception{  
        int a = 10;  
          
        //十进制 -> 二进制  
        String str = Integer.toBinaryString(a);  
        while(str.length() < 32){  
            str = 0 + str;  
        }  
        System.out.println("10 -> 2：" + str);  
          
        //十进制 -> 八进制  
        str = Integer.toOctalString(a);  
        System.out.println("10 -> 8：" + str);  
          
        //十进制 -> 十六进制  
        str = Integer.toHexString(a);  
        System.out.println("10 -> 16：" + str);  
          
        //十进制 -> 特定进制  
        int random = (int)(Math.random() * 10);  
        str = Integer.toString(a, random);  
        System.out.println("10 -> " + random + "：" + str);  
          
        //二进制 -> 十进制  
        str = "1010"; //补0  
        a = Integer.parseInt(str, 2);  
        System.out.println("2 -> 10：" + a);  
          
        //八进制 -> 十进制  
        str = "12";  
        a = Integer.parseInt(str, 8); //str转为int后的值必须大于8  
        System.out.println("8 -> 10：" + a);  
          
        //十六进制 -> 十进制  
        str = "a";  
        a = Integer.parseInt(str, 16);   
        System.out.println("16 -> 10：" + a);  
    }  
}  
```

运行结果：

```
10 -> 2：00000000000000000000000000001010  
10 -> 8：12  
10 -> 16：a  
10 -> 2：1010  
2 -> 10：10  
8 -> 10：10  
16 -> 10：10
```

http://blog.csdn.net/a19881029/article/details/32093193

## 2至62进制转换

**进制转换工具，最大支持十进制和62进制的转换，1、将十进制的数字转换为指定进制的字符串；2、将其它进制的数字（字符串形式）转换为十进制的数字；**

```java
package com.kfit.util;

/**
 * 进制转换工具，最大支持十进制和62进制的转换
 * 1、将十进制的数字转换为指定进制的字符串；
 * 2、将其它进制的数字（字符串形式）转换为十进制的数字
 *
 * @author xuliugen
 * @date 2018/04/23
 */
public class NumericConvertUtils {

    /**
     * 在进制表示中的字符集合，0-Z分别用于表示最大为62进制的符号表示
     */
    private static final char[] digits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

    /**
     * 将十进制的数字转换为指定进制的字符串
     *
     * @param number 十进制的数字
     * @param seed   指定的进制
     * @return 指定进制的字符串
     */
    public static String toOtherNumberSystem(long number, int seed) {
        if (number < 0) {
            number = ((long) 2 * 0x7fffffff) + number + 2;
        }
        char[] buf = new char[32];
        int charPos = 32;
        while ((number / seed) > 0) {
            buf[--charPos] = digits[(int) (number % seed)];
            number /= seed;
        }
        buf[--charPos] = digits[(int) (number % seed)];
        return new String(buf, charPos, (32 - charPos));
    }

    /**
     * 将其它进制的数字（字符串形式）转换为十进制的数字
     *
     * @param number 其它进制的数字（字符串形式）
     * @param seed   指定的进制，也就是参数str的原始进制
     * @return 十进制的数字
     */
    public static long toDecimalNumber(String number, int seed) {
        char[] charBuf = number.toCharArray();
        if (seed == 10) {
            return Long.parseLong(number);
        }

        long result = 0, base = 1;

        for (int i = charBuf.length - 1; i >= 0; i--) {
            int index = 0;
            for (int j = 0, length = digits.length; j < length; j++) {
                //找到对应字符的下标，对应的下标才是具体的数值
                if (digits[j] == charBuf[i]) {
                    index = j;
                }
            }
            result += index * base;
            base *= seed;
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println(toOtherNumberSystem(1857568745871168L, 62));
        System.out.println(toDecimalNumber("8vtxrGTuw", 62));
        System.out.println();
        System.out.println(toOtherNumberSystem(185748383552778241L, 62));
        System.out.println(toDecimalNumber("dIJesjQJSF", 62));
        System.out.println();
        System.out.println(toOtherNumberSystem(110510427133706240L, 16));
        System.out.println(toDecimalNumber("1889ca5e1480000", 16));
    }
}

```

https://gitee.com/xuliugen/codes/df8zhk263os7nebglruwq50