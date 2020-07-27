# java中数字签名MD5withRSA和SHA1withRSA

## 一、简介

数字签名用于验证消息发送者的身份，确保其他人无法伪造身份。

## 二、原理

数字签名基于非对称加密算法，利用只有拥有者才有私钥的特性（这可以标识身份）进行的。

### 1、数字签名的生成

对发送内容先生成有限长度的摘要，再使用私钥进行加密，进而生成数字签名。

### 2、数字签名验证

用公钥对数字签名进行解密获取加密内容（其实也就是摘要），再用与发送方相同的摘要算法对发送内空生成摘要，

再将这两者进行比较，若相等，则验证成功，否则失败。

## 三、代码实例

在此使用java自带的数字签名api进行演示，包括MD5withRSA和SHA1withRSA两种方式，签名使用base64编码。

```java

public class DigitalSignatureMain {
    public static void main(String[] args)  throws Exception {
        String content = "study hard and make progress everyday";
        System.out.println("content :"+content);
 
        KeyPair keyPair = getKeyPair();
        // 获取公钥对象
        PublicKey publicKey =  keyPair.getPublic();
        // 获取私钥对象
        PrivateKey privateKey = keyPair.getPrivate();
 
        String md5Sign  = getMd5Sign(content,privateKey);
        System.out.println("MD5withRSA算法的签名 :"+ md5Sign);
        boolean md5Verifty = verifyWhenMd5Sign(content,md5Sign,publicKey);
        System.out.println("MD5withRSA算法的签名验签结果 :"+ md5Verifty);
 
        String sha1Sign  = getSha1Sign(content,privateKey);
        System.out.println("SHA1withRSA算法的签名 :"+ sha1Sign);
        boolean sha1Verifty = verifyWhenSha1Sign(content,sha1Sign,publicKey);
        System.out.println("SHA1withRSA算法的签名验签结果 :"+ sha1Verifty);
 
    }
 
    // 生成密钥对
    static KeyPair getKeyPair() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(512); //这是生成的秘钥长度，此处的值也可以是1024或2048。秘钥越大加密后的密文长度也越大，加密解密越慢。加密的原文要比秘钥小些。一般1024足够用了。
        KeyPair keyPair = keyGen.generateKeyPair();
        return keyPair;
    }
 
    // 用md5生成内容摘要，再用RSA的私钥加密，进而生成数字签名
    static String getMd5Sign(String content , PrivateKey privateKey) throws Exception {
        byte[] contentBytes = content.getBytes("utf-8");
        // 返回MD5withRSA签名算法的 Signature对象
        Signature signature = Signature.getInstance("MD5withRSA");
        signature.initSign(privateKey);
        signature.update(contentBytes);
        byte[] signs = signature.sign();
        return Base64.encodeBase64String(signs);
    }
 
    // 对用md5和RSA私钥生成的数字签名进行验证
    static boolean verifyWhenMd5Sign(String content, String sign, PublicKey publicKey) throws Exception {
        byte[] contentBytes = content.getBytes("utf-8");
        Signature signature = Signature.getInstance("MD5withRSA");
        signature.initVerify(publicKey);
        signature.update(contentBytes);
        return signature.verify(Base64.decodeBase64(sign));
    }
 
    // 用sha1生成内容摘要，再用RSA的私钥加密，进而生成数字签名
    static String getSha1Sign(String content , PrivateKey privateKey) throws Exception {
        byte[] contentBytes = content.getBytes("utf-8");
        // 返回SHA1WithRsa签名算法的 Signature对象
        Signature signature = Signature.getInstance("SHA1withRSA");
        signature.initSign(privateKey);
        signature.update(contentBytes);
        byte[] signs = signature.sign();
        return Base64.encodeBase64String(signs);
    }
 
    // 对用md5和RSA私钥生成的数字签名进行验证
    static boolean verifyWhenSha1Sign(String content, String sign, PublicKey publicKey) throws Exception {
        byte[] contentBytes = content.getBytes("utf-8");
        Signature signature = Signature.getInstance("SHA1withRSA");
        signature.initVerify(publicKey);
        signature.update(contentBytes);
        return signature.verify(Base64.decodeBase64(sign));
    }
} 
```



输出如下：

```
content :study hard and make progress everyday
MD5withRSA算法的签名:L0pxR69rmOYoWoHdOJHKEvHDVdEamrqQlmYV4Yrwfz0BFIAKgSL4tGyw+4G3WDiOCHeZMPAPM/F39Ygxc1rtMg==
MD5withRSA算法的签名验签结果 :true
SHA1withRSA算法的签名 :jBhPcFAhZ7mGcc3jjVhmyybIPNwnIMPzGQ+piQf6RyMbICtYzT/xxG2P0rQ09t8+9ybp/NIWy83I5kWIs3MZfg==
SHA1withRSA算法的签名验签结果 :true
```


 

————————————————
版权声明：本文为CSDN博主「全冉」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
原文链接：https://blog.csdn.net/qq_23167527/java/article/details/81122903