[TOC]

/Users/jerryye/backup/studio/AvailableCode/basis/utils/codec编码解码的工具类/codec_demo/src/test/java/com/practice/codec/CommonsCodecTest.java

# commons-codec使用介绍

`commons-codec`是Apache开源组织提供的用于摘要运算、编码的包。在该包中主要分为四类加密：BinaryEncoders、DigestEncoders、LanguageEncoders、NetworkEncoders。

今天就为大家介绍一下如何用`commons-codec`包完成常见的编码、摘要运算。

```
<dependency>
    <groupId>commons-codec</groupId>
    <artifactId>commons-codec</artifactId>
    <version>1.13</version>
</dependency>
```



## Base64

示例代码：

```
@Test
public void testBase64()
{
   System.out.println("==============Base64================");
   byte[] data = "jianggujin".getBytes();
   Base64 base64 = new Base64();
   String encode = base64.encodeAsString(data);
   System.out.println(encode);
   System.out.println(new String(base64.decode(encode)));
} 
```

运行结果：

```
==============Base64================
amlhbmdndWppbg==
jianggujin 
```

## MD5摘要运算

示例代码：

```
@Test
public void testMD5()
{
   System.out.println("==============MD5================");
   String result = DigestUtils.md5Hex("jianggujin");
   System.out.println(result);
}1234567
```

运行结果：

```
acab4efdfd3b8efcdec37fe160d7be0e1
```

SHA等摘要运算和MD5类似。

## URLCodec

示例代码：

```
@Test
public void testURLCodec() throws Exception
{
   System.out.println("==============URLCodec================");
   URLCodec codec = new URLCodec();
   String data = "蒋固金";
   String encode = codec.encode(data, "UTF-8");
   System.out.println(encode);
   System.out.println(codec.decode(encode, "UTF-8"));
}12345678910
```

运行结果：

```
==============URLCodec================
%E8%92%8B%E5%9B%BA%E9%87%91
蒋固金123
```

完整示例代码：

```
package com.gujin.codec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.net.URLCodec;
import org.junit.Test;

public class CodecTest
{
   @Test
   public void testBase64()
   {
      System.out.println("==============Base64================");
      byte[] data = "jianggujin".getBytes();
      Base64 base64 = new Base64();
      String encode = base64.encodeAsString(data);
      System.out.println(encode);
      System.out.println(new String(base64.decode(encode)));
   }

   @Test
   public void testMD5()
   {
      System.out.println("==============MD5================");
      String result = DigestUtils.md5Hex("jianggujin");
      System.out.println(result);
   }

   @Test
   public void testURLCodec() throws Exception
   {
      System.out.println("==============URLCodec================");
      URLCodec codec = new URLCodec();
      String data = "蒋固金";
      String encode = codec.encode(data, "UTF-8");
      System.out.println(encode);
      System.out.println(codec.decode(encode, "UTF-8"));
   }
}
```







https://blog.csdn.net/jianggujin/article/details/51149133