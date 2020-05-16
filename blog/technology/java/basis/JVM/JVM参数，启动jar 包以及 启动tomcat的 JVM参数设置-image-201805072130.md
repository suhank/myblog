[TOC]



# JVM参数，启动jar 包以及 启动tomcat的 JVM参数设置

 

## 一，基本的启动参数的位置

```
public class TestJVM {  
    public static void main(String[] args) {  
  
        long maxM = Runtime.getRuntime().maxMemory();  
        long totalM = Runtime.getRuntime().totalMemory();  
        long usedM = Runtime.getRuntime().freeMemory();  
        System.out.println("maxM=" + maxM + ",totalM=" + totalM + "usedM=" + usedM);  
    }  
}  
```

 启动示例

```
 java -Xms10m -Xmx100m  TestJVM
```



## 二，启动jar**包的参数位置示例**

```
/usr/local/webservices/jdk1.8.0_91/bin/java  -Xms8000m -Xmx8000m -Xmn6000m  -jar luckydrawall-0.1.1.jar --spring.profiles.active=rel
```



## 三，tomcat的配置示例

配置tomcat的catalina.sh

![img](image-201805072130/20160808155407607)

注意，配置的是

```
JAVA_OPTS="$JAVA_OPTS $JSSE_OPTS -Xms8000m -Xmx8000m -Xmn6000m"
```

里的参数

配置前的测试输出：

maxM=2776104960,totalM=1128267776,useM=167240936

配置后的测试输出：

maxM=7602176000,totalM=7602176000,useM=6576489896

 





https://blog.csdn.net/kkgbn/article/details/52045954