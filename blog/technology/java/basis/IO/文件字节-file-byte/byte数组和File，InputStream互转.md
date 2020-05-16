[TOC]



# byte数组和File，InputStream互转

2018年02月11日 16:26:25  更多



## 1、将File、FileInputStream 转换为byte数组：

```java
File file = new File("file.txt");
InputStream input = new FileInputStream(file);
byte[] byt = new byte[input.available()];
input.read(byt);
```

 

## 2、将byte数组转换为InputStream：

```java
byte[] byt = new byte[1024];
InputStream input = new ByteArrayInputStream(byt);
```

 

## 3、将byte数组转换为File：

```java
File file = new File('');
OutputStream output = new FileOutputStream(file);
BufferedOutputStream bufferedOutput = new BufferedOutputStream(output);
bufferedOutput.write(bytes);
bufferedOutput.flush();
bufferedOutput.close();
```





## 封装的工具

```java
public static byte[] getStreamBytes(InputStream is) throws Exception {  
    ByteArrayOutputStream baos = new ByteArrayOutputStream();  
    byte[] buffer = new byte[1024];  
    int len = 0;  
    while ((len = is.read(buffer)) != -1) {  
        baos.write(buffer, 0, len);  
    }  
    byte[] b = baos.toByteArray();  
    is.close();  
    baos.close();  
    return b;  
}
```



```java
default byte[] readFileBytes(InputStream is){  
    byte[] data = null;  
    try {  
        if(is.available()==0){//严谨起见,一定要加上这个判断,不要返回data[]长度为0的数组指针  
            return data;  
        }  
        data = new byte[is.available()];  
        is.read(data);  
        is.close();  
        return data;  
    } catch (IOException e) {  
        LogCore.BASE.error("readFileBytes, err", e);  
        return data;  
    }  
}  

```





## 推荐使用 commons-io 或者 hutool

```xml
        <dependency>
            <groupId>commons-io</groupId>
            <artifactId>commons-io</artifactId>
            <version>2.6</version>
        </dependency>
        <dependency>
            <groupId>cn.hutool</groupId>
            <artifactId>hutool-all</artifactId>
            <version>4.5.12</version>
        </dependency>
```



```java
byte[] bytes1 = FileUtils.readFileToByteArray(file);
FileUtils.writeByteArrayToFile(file,bytes1);

//cn.hutool.core.io.FileUtil#readBytes(java.io.File)
//cn.hutool.core.io.FileUtil#writeBytes(byte[], java.lang.String)
```

https://blog.csdn.net/qq_35893120/article/details/79311629