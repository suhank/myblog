# RSA关于加密长度限制的解决办法

RSA关于加密长度限制的解决办法

**因为rsa采用分块进行加密的,所以有长度限制.如果加密信息较多,可分段加解密(不建议对大量信息rsa加密,效率低效):**

**正常加密情形如下:**

```java
public static String encrypt(String source, String publicKey)
        throws Exception {
    Key key = getPublicKey(publicKey);
    /** 得到Cipher对象来实现对源数据的RSA加密 */
    Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
    cipher.init(Cipher.ENCRYPT_MODE, key);
    byte[] b = source.getBytes();
    /** 执行加密操作 */
    byte[] b1 = cipher.doFinal(b);
    return new String(Base64.encodeBase64(b1), "UTF-8");
}
```

**分段加密如下:**

```java
    public static byte[] encryptByPublicKey(byte[] data, String publicKeyStr) throws Exception {
        PublicKey publicKey = RSAEncrypt.loadPublicKeyByStr(publicKeyStr);
        Cipher cipher = null;
        // 使用默认RSA
        cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }
```

**即把超过117(加密)和128(解密)长度的原文内容分割成多个部分,依次加解密,再合并.**





https://www.cnblogs.com/hupu-jr/p/7559879.html