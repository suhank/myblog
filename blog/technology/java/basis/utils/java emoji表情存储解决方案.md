[TOC]



# java emoji表情存储解决方案

### 1.问题产生情况

我遇到这个问题是做微信开发的时候有些有用的头像用了微信的emoji表情，然而我的mysql数据库用的编码是utf8_general_ci,就是utf-8编码，结果也就报错误了。

 

### 2.为什么会出现这种原因

因为mysql的utf8编码的一个字符最多3个字节，但是一个emoji表情为4个字节，所以utf8不支持存储emoji表情。但是utf8的超集utf8mb4一个字符最多能有4字节，所以能支持emoji表情的存储。

 

### 3.解决方法之一

把你的数据库编码集设置为utf8mb4，无论是数据库还是表，还是字段。虽然会增加存储，但是这个可以忽略不计。

 

### 4.解决方法之二

有句话说得好，问题来了要么解决要么折中解决。如果有些原因你不能修改数据库编码之类的，你可以用java的一些插件，如emoji-java这种emoji表情插件对表情进行特殊处理，然后保存或者去掉表情，这也是一种解决方法哦。

 

### 5.最后说点什么

通过对一个问题不同角度的思考，原来才发现世界同而不同，不同而同......

 

最后来段代码:

```
package com.sparkle.common.util;

import com.vdurmont.emoji.EmojiParser;
import org.apache.commons.lang3.StringUtils;

/**
 * <B>Description:</B> 表情转换工具  表情处理类<br>
 * <B>Create on:</B> 2018/6/3 上午10:21 <br>
 *
 * @author xiangyu.ye
 * @version 1.0
 */
public class EmojiUtil {

    /**
     * 将emojiStr转为 带有表情的字符
     * @param emojiStr
     * @return
     */
    public static String emojiConverterUnicodeStr(String emojiStr) {
        if (StringUtils.isNotBlank(emojiStr)) {
            return EmojiParser.parseToUnicode(emojiStr);
        }
        return emojiStr;
    }

    /**
     * 带有表情的字符串转换为编码
     * @param str
     * @return
     */
    public static String emojiConverterToAlias(String str) {
        if (StringUtils.isNotBlank(str)) {
            return EmojiParser.parseToAliases(str);
        }
        return str;
    }

    /*public static void main(String[] args) {
        String str = "网红\uD83D\uDC97洁面仪~luna mini2";
    //        String str = "An 😀awesome 😃string with a few 😉emojis!";
        String result = emojiConverterToAlias(str);
        System.out.println(result);
    
    
    //        String str2 = "An :grinning:awesome :smiley:string &#128516;with a few :wink:emojis!";
        String result2 = emojiConverterUnicodeStr(result);
        System.out.println(result2);
    }
    */
}

```

  

使用的框架是：

```
<dependency>
  <groupId>com.vdurmont</groupId>
  <artifactId>emoji-java</artifactId>
  <version>4.0.0</version>
</dependency>
```





https://www.cnblogs.com/huzi007/p/6721450.html