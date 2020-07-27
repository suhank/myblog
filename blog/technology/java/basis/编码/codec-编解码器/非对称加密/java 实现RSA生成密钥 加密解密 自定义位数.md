# java 实现RSA生成密钥 加密解密 自定义位数

**[ADMIN](https://leanote.zzzmh.cn/blog/admin)** Posted on Jul 3 2019 

## 前言

关于RSA原理不多赘述，
参考百度百科：[https://baike.baidu.com/item/RSA%E7%AE%97%E6%B3%95/263310](https://baike.baidu.com/item/RSA算法/263310)

**我个人的理解是**
简单来说就是非对称加密，公钥可以直接放在H5，APP等前端程序中，即使被拿到，想要用公钥破解出私钥也是极难的。

- 先决定密钥长度后生成一套一对一关系的公私钥。
- 公钥提供给前端，私钥放在服务端。
- 通过RSA公钥加密明文，加密后的密文发到服务端，服务端用RSA私钥解密得出明文。

------

另外申明本文代码并非原创
参考文章:https://blog.csdn.net/qy20115549/article/details/83105736

仅是在其基础上做出了一点点修改
\1. 去除外部jar包依赖，修改了base64依赖为java8自带的`java.util.Base64`
\2. 提取了密钥长度参数为KEY_SIZE，可根据需要修改

## 代码

```java
package com.zzzmh.api.utils;
import java.util.Base64;
import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
/**
 * Java RSA 加密工具类
 * 参考： https://blog.csdn.net/qy20115549/article/details/83105736
 */
public class RSAUtils {
    /**
     * 密钥长度 于原文长度对应 以及越长速度越慢
     */
    private final static int KEY_SIZE = 1024;
    /**
     * 用于封装随机产生的公钥与私钥
     */
    private static Map<Integer, String> keyMap = new HashMap<Integer, String>();
    /**
     * 随机生成密钥对
     */
    public static void genKeyPair() throws NoSuchAlgorithmException {
        // KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        // 初始化密钥对生成器
        keyPairGen.initialize(KEY_SIZE, new SecureRandom());
        // 生成一个密钥对，保存在keyPair中
        KeyPair keyPair = keyPairGen.generateKeyPair();
        // 得到私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        // 得到公钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        String publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        // 得到私钥字符串
        String privateKeyString = Base64.getEncoder().encodeToString(privateKey.getEncoded());
        // 将公钥和私钥保存到Map
        //0表示公钥
        keyMap.put(0, publicKeyString);
        //1表示私钥
        keyMap.put(1, privateKeyString);
    }
    /**
     * RSA公钥加密
     *
     * @param str       加密字符串
     * @param publicKey 公钥
     * @return 密文
     * @throws Exception 加密过程中的异常信息
     */
    public static String encrypt(String str, String publicKey) throws Exception {
        //base64编码的公钥
        byte[] decoded = Base64.getDecoder().decode(publicKey);
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
        //RSA加密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        String outStr = Base64.getEncoder().encodeToString(cipher.doFinal(str.getBytes("UTF-8")));
        return outStr;
    }
    /**
     * RSA私钥解密
     *
     * @param str        加密字符串
     * @param privateKey 私钥
     * @return 明文
     * @throws Exception 解密过程中的异常信息
     */
    public static String decrypt(String str, String privateKey) throws Exception {
        //64位解码加密后的字符串
        byte[] inputByte = Base64.getDecoder().decode(str);
        //base64编码的私钥
        byte[] decoded = Base64.getDecoder().decode(privateKey);
        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
        //RSA解密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        String outStr = new String(cipher.doFinal(inputByte));
        return outStr;
    }
    public static void main(String[] args) throws Exception {
        long temp = System.currentTimeMillis();
        //生成公钥和私钥
        genKeyPair();
        //加密字符串
        System.out.println("公钥:" + keyMap.get(0));
        System.out.println("私钥:" + keyMap.get(1));
        System.out.println("生成密钥消耗时间:" + (System.currentTimeMillis() - temp) / 1000.0 + "秒");
        String message = "RSA测试ABCD~!@#$";
        System.out.println("原文:" + message);
        temp = System.currentTimeMillis();
        String messageEn = encrypt(message, keyMap.get(0));
        System.out.println("密文:" + messageEn);
        System.out.println("加密消耗时间:" + (System.currentTimeMillis() - temp) / 1000.0 + "秒");
        temp = System.currentTimeMillis();
        String messageDe = decrypt(messageEn, keyMap.get(1));
        System.out.println("解密:" + messageDe);
        System.out.println("解密消耗时间:" + (System.currentTimeMillis() - temp) / 1000.0 + "秒");
    }
}
```

## 运行结果

```
公钥:MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCO2RHZWuZzAvWpJeKYSt1oAG1CehN0msUxOfqhImecvu6CJrZo9g+SnA32GjGBC73e6J+8MbMVrqr4DtrcaBT7UAAIJh2zeFbQ1P3hr7upPOjyY8ZQEK/ipPYvHXQHFMSyg7CJmosi//Vob0YknfsYHtmBwHChWsX2SKEq61X7CQIDAQAB
私钥:MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAI7ZEdla5nMC9akl4phK3WgAbUJ6E3SaxTE5+qEiZ5y+7oImtmj2D5KcDfYaMYELvd7on7wxsxWuqvgO2txoFPtQAAgmHbN4VtDU/eGvu6k86PJjxlAQr+Kk9i8ddAcUxLKDsImaiyL/9WhvRiSd+xge2YHAcKFaxfZIoSrrVfsJAgMBAAECgYEAguoRX5/dH36Q4aok1umVrCDoAUqb1fuZyRmXxmEfkBmzwHf2KI+JihWW/frXb6rxIf8TlYf+1lozug7zKZgB0Um82KUjymIPC3kzo9loZsUbo9fgEJrRct+MLDeAIQjzKr7MrqVmJ9JKgvMGMNH28A5eXu+iEhQ6PjQFosFWg/kCQQDFIJ1VYDGZGgsAQ3VIthV4BmezI6Z4d9TbrAgN9QkHlm9d7Dov0NmZTUHQj31VCThK7mm3aLjF8qtCX8fSc9ebAkEAuYKD7b9TN8rnd7q7//5JuiabJPYh8KkiC3pnA031AJsucztMQzIv/vrrMGAubG72jqER45qbuRQr3+vbc2iMKwJBAKFFPXJLcEhA9h8RETKbRJUdKFl2IQsNfib5Zt2ESg7bE+FTEYds5Zh1jBKEUZTwJg2nXvWdxwyqq1Fx6phSDWECQC3T3j+Xajl4OKJNUTA2Y4RHEUCaRVwsjCqFvHkGgyX5MAprdbWL6mt1FTDIMe+7odEuXTr68MlSAFy66WWjSC0CQQC1nmhzQeED38V+SbMgp5R2I0d3zG9dx1X9zQ8dgbY8Fdu+KYILRhr2ZvptL+cGFCh5jQq39cfKxu0zBCzI8uqu
生成密钥消耗时间:2.085秒
原文:RSA测试ABCD~!@#$
密文:h/deJx+BQDXe13/kJmUaBFRQROHkOvqj4Ru86+TYzHIx1AU8iAALErGa+HjIZ5CGGjwl8wEOKzhy2NoDIRw8QeqkBFPw0Exc5UPP19lhC3jnsYWadNNVrNYn1ozUne2EbaUxqdbPcUBaTMzhu04P7teuGYYNKgeLQdnE/zNuWFo=
加密消耗时间:2.991秒
解密:RSA测试ABCD~!@#$
解密消耗时间:0.01秒
```

## 注意事项

RSA的密钥长度决定了可以加密的明文长度，如果明文超长会出现如下报错:

```
Exception in thread "main" javax.crypto.IllegalBlockSizeException: Data must not be longer than 117 bytes
    at com.sun.crypto.provider.RSACipher.doFinal(RSACipher.java:344)
    at com.sun.crypto.provider.RSACipher.engineDoFinal(RSACipher.java:389)
    at javax.crypto.Cipher.doFinal(Cipher.java:2164)
    at com.zzzmh.api.utils.RSAUtils.encrypt(RSAUtils.java:70)
    at com.zzzmh.api.utils.RSAUtils.main(RSAUtils.java:110)
```

长度对应关系参考文章:https://blog.csdn.net/liwei16611/article/details/83751851

**我自己理解的大致内容如下**
一次能加密的明文长度与密钥长度成正比：
`len_in_byte(raw_data) = len_in_bit(key)/8 -11`
例如
1024bit 密钥 能加密明文最大的长度是 1024/8 -11 = 117 byte
2048bit 密钥 能加密明文最大的长度是 2048/8 -11 = 245 byte
以此类推

**总结：**
密钥长度决定了:
\1. 支持的明文长度越长
\2. 加解密损耗时间越长
\3. 破解起来复杂度越大





https://leanote.zzzmh.cn/blog/post/5d1c661416199b0683002dc8