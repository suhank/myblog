[TOC]



# JVM内存管理------垃圾搜集器参数精解

​          本文是GC相关的最后一篇，这次LZ只是罗列一下hotspot JVM中垃圾搜集器相关的重点参数，以及各个参数的解释。废话不多说，这就开始。

 

## **垃圾搜集器选择参数**

​          **UseSerialGC**：开启此参数使用serial & serial old搜集器（client模式默认值）。

​          **UseParNewGC**：开启此参数使用ParNew & serial old搜集器（不推荐）。

​          **UseConcMarkSweepGC**：开启此参数使用ParNew & CMS（serial old为替补）搜集器。

​          **UseParallelGC**：开启此参数使用parallel scavenge & parallel old搜集器（server模式默认值）。

​          **UseParallelOldGC**：开启此参数在年老代使用parallel old搜集器（该参数在JDK1.5之后已无用）。

 

## **JVM各个内存区域大小相关参数**

​          **Xms**：堆的初始值。默认为物理内存的1/64，最大不超1G。

​          **Xmx**：堆的最大值。默认为物理内存的1/4，最大不超1G。

​          **Xmn**：新生代的大小。

​          **Xss**：线程栈大小。

​          **PermSize**：永久代初始大小。默认为物理内存的1/64，最大不超1G。

​          **MaxPermSize**：永久代最大值。默认为物理内存的1/4，最大不超1G。

​          **NewRatio**：新生代与年老代的比例。比如为3，则新生代占堆的1/4，年老代占3/4。

​          **SurvivorRatio**：新生代中调整eden区与survivor区的比例，默认为8，即eden区为80%的大小，两个survivor分别为10%的大小。（备注：这个参数设定是讲解复制算法那一章中，解决复制算法内存减半的办法。eden区即是复制算法一章中80%的那部分，而survivor区则是两个10%的那部分。）

 

### **垃圾搜集器性能通用参数**

​          **PretenureSizeThreshold**：晋升年老代的对象大小。默认为0，比如设为10M，则超过10M的对象将不在eden区分配，而直接进入年老代。

​          **MaxTenuringThreshold**：晋升老年代的最大年龄。默认为15，比如设为10，则对象在10次普通GC后将会被放入年老代。

​          **DisableExplicitGC**：禁用System.gc()。

### **并行搜集器参数**

​          **ParallelGCThreads**：回收时开启的线程数。默认与CPU个数相等。

​          **GCTimeRatio**：设置系统的吞吐量。比如设为99，则GC时间比为1/1+99=1%，也就是要求吞吐量为99%。若无法满足会缩小新生代大小。

​          **MaxGCPauseMillis**：设置垃圾回收的最大停顿时间。若无法满足设置值，则会优先缩小新生代大小，仍无法满足的话则会牺牲吞吐量。

​          

### **并发搜集器参数**

 

​          **CMSInitiatingOccupancyFraction**：触发CMS收集器的内存比例。比如60%的意思就是说，当内存达到60%，就会开始进行CMS并发收集。

​          **UseCMSCompactAtFullCollection**：这个前面已经提过，用于在每一次CMS收集器清理垃圾后送一次内存整理。

​          **CMSFullGCsBeforeCompaction**：设置在几次CMS垃圾收集后，触发一次内存整理。

 

## **结束语**

 

​          GC相关系列基本就结束了，本篇文章只是做一个罗列，之后我们一起来进入虚拟机的源码世界吧。



http://www.cnblogs.com/zuoxiaolong/p/jvm9.html