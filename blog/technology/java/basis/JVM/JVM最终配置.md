# jvm最终配置

-Xmn2g:整个堆大小=年轻代大小+年老代大小+持久代大小。持久代一般固定大小为64m,所以增大年轻代后,将会减小年老代大小。此值对系统性能影响较大,Sun官方推荐配置为整个堆的3/8。 
使用-XX:NewSize和-XX:MaxNewsize设置新域的初始值和最大值。 
在tomcat   bin目录下catalina.bat 头部添加如下：

```
set JAVA_OPTS=-Xms256m -Xmx512m -XX:+UseConcMarkSweepGC
```


linux下修改JVM内存大小:

要添加在tomcat 的bin 下catalina.sh 里，位置cygwin=false前 。注意引号要带上,红色的为新添加的.

```
# OS specific support. $var must be set to either true or false.

JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseConcMarkSweepGC"

```



 



解释： 

JAVA_OPTS='-Xms【初始化内存大小】-Xmx【可以使用的最大内存】'

JVM初始分配的内存由-Xms指定，默认是物理内存的1/64；JVM最大分配的内存由-Xmx指 定，默认是物理内存的1/4。默认空余堆内存小于40%时，JVM就会增大堆直到-Xmx的最大限制；空余堆内存大于70%时，JVM会减少堆直到 -Xms的最小限制。因此服务器一般设置-Xms、-Xmx相等以避免在每次GC 后调整堆的大小。对象的堆内存由称为垃圾回收器的自动内存管理系统回收。





**ParNew & CMS（Serial Old作为替补）** 

​         这个组合与上面的并行组合一样，在平时的开发当中都不常见，而它则是对相应时间（response time）要求较高的应用程序的首选。该组合需要使用参数-XX:+UseConcMarkSweepGC开启。

​         该组合在新生代采用并行搜集器，因此新生代的GC速度会非常快，停顿时间很短。而年老代的GC采用并发搜集，大部分垃圾搜集的时间里，GC线程都是与应用程序并发执行的，因此造成的停顿时间依然很短。它适用于一些**需要长期运行且对相应时间有一定要求的后台程序**。

​         这些运行于后台的程序的特点与并行模式下的后台程序十分类似，不同的是第二点，采用ParNew & CMS组合的后台应用程序，一般都对相应时间有一定要求，最典型的就是我们的WEB应用程序。

   