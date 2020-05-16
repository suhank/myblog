[TOC]



# java 中 byte、byte 数组和 int、long 之间的转换

2013年04月10日 14:34:28 [Defonds](https://me.csdn.net/defonds) 阅读数 111244



## byte 和 int 之间的转换源码：

```java
	//byte 与 int 的相互转换
	public static byte intToByte(int x) {
		return (byte) x;
	}
	
	public static int byteToInt(byte b) {
		//Java 总是把 byte 当做有符处理；我们可以通过将其和 0xFF 进行二进制与得到它的无符值
		return b & 0xFF;
	}
```

测试代码：

```java
		//测试 int 转 byte
		int int0 = 234;
		byte byte0 = intToByte(int0);
		System.out.println("byte0=" + byte0);//byte0=-22
		//测试 byte 转 int
		int int1 = byteToInt(byte0);
		System.out.println("int1=" + int1);//int1=234
```





## byte 数组和 int 之间的转换源码：



```java
	//byte 数组与 int 的相互转换
	public static int byteArrayToInt(byte[] b) {
	    return   b[3] & 0xFF |
	            (b[2] & 0xFF) << 8 |
	            (b[1] & 0xFF) << 16 |
	            (b[0] & 0xFF) << 24;
	}
 
	public static byte[] intToByteArray(int a) {
	    return new byte[] {
	        (byte) ((a >> 24) & 0xFF),
	        (byte) ((a >> 16) & 0xFF),   
	        (byte) ((a >> 8) & 0xFF),   
	        (byte) (a & 0xFF)
	    };
	}
```

测试代码：

```java
		//测试 int 转 byte 数组
		int int2 = 1417;
		byte[] bytesInt = intToByteArray(int2);
		System.out.println("bytesInt=" + bytesInt);//bytesInt=[B@de6ced
		//测试 byte 数组转 int
		int int3 = byteArrayToInt(bytesInt);
		System.out.println("int3=" + int3);//int3=1417
```





## byte 数组和 long 之间的转换源码：



```java
	private static ByteBuffer buffer = ByteBuffer.allocate(8); 
	//byte 数组与 long 的相互转换
    public static byte[] longToBytes(long x) {
        buffer.putLong(0, x);
        return buffer.array();
    }
 
    public static long bytesToLong(byte[] bytes) {
        buffer.put(bytes, 0, bytes.length);
        buffer.flip();//need flip 
        return buffer.getLong();
    }
```

测试代码：

```java
		//测试 long 转 byte 数组
		long long1 = 2223;
		byte[] bytesLong = longToBytes(long1);
		System.out.println("bytes=" + bytesLong);//bytes=[B@c17164
		//测试 byte 数组 转 long
		long long2 = bytesToLong(bytesLong);
		System.out.println("long2=" + long2);//long2=2223
```





## 整体工具类源码：



```java
import java.nio.ByteBuffer;
 
 
public class Test {
	
	private static ByteBuffer buffer = ByteBuffer.allocate(8);    
 
	public static void main(String[] args) {
		
		//测试 int 转 byte
		int int0 = 234;
		byte byte0 = intToByte(int0);
		System.out.println("byte0=" + byte0);//byte0=-22
		//测试 byte 转 int
		int int1 = byteToInt(byte0);
		System.out.println("int1=" + int1);//int1=234
		
		
		
		//测试 int 转 byte 数组
		int int2 = 1417;
		byte[] bytesInt = intToByteArray(int2);
		System.out.println("bytesInt=" + bytesInt);//bytesInt=[B@de6ced
		//测试 byte 数组转 int
		int int3 = byteArrayToInt(bytesInt);
		System.out.println("int3=" + int3);//int3=1417
		
		
		//测试 long 转 byte 数组
		long long1 = 2223;
		byte[] bytesLong = longToBytes(long1);
		System.out.println("bytes=" + bytesLong);//bytes=[B@c17164
		//测试 byte 数组 转 long
		long long2 = bytesToLong(bytesLong);
		System.out.println("long2=" + long2);//long2=2223
	}
	
	
	//byte 与 int 的相互转换
	public static byte intToByte(int x) {
		return (byte) x;
	}
	
	public static int byteToInt(byte b) {
		//Java 总是把 byte 当做有符处理；我们可以通过将其和 0xFF 进行二进制与得到它的无符值
		return b & 0xFF;
	}
	
	//byte 数组与 int 的相互转换
	public static int byteArrayToInt(byte[] b) {
	    return   b[3] & 0xFF |
	            (b[2] & 0xFF) << 8 |
	            (b[1] & 0xFF) << 16 |
	            (b[0] & 0xFF) << 24;
	}
 
	public static byte[] intToByteArray(int a) {
	    return new byte[] {
	        (byte) ((a >> 24) & 0xFF),
	        (byte) ((a >> 16) & 0xFF),   
	        (byte) ((a >> 8) & 0xFF),   
	        (byte) (a & 0xFF)
	    };
	}
 
	//byte 数组与 long 的相互转换
    public static byte[] longToBytes(long x) {
        buffer.putLong(0, x);
        return buffer.array();
    }
 
    public static long bytesToLong(byte[] bytes) {
        buffer.put(bytes, 0, bytes.length);
        buffer.flip();//need flip 
        return buffer.getLong();
    }
 
}
```





运行测试结果：

```java
byte0=-22
int1=234
bytesInt=[B@de6ced
int3=1417
bytes=[B@c17164
long2=2223
```



参考文章1：http://stackoverflow.com/questions/7401550/how-to-convert-int-to-unsigned-byte-and-back。

参考文章2：http://stackoverflow.com/questions/1936857/convert-integer-into-byte-array-java。

参考文章3：http://stackoverflow.com/questions/4485128/how-do-i-convert-long-to-byte-and-back-in-java。





https://blog.csdn.net/defonds/article/details/8782785