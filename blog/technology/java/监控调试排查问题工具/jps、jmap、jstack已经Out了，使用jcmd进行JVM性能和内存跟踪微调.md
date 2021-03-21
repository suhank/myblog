# jps、jmap、jstack已经Out了，使用jcmd进行JVM性能和内存跟踪微调

20-09-03

## 前言

当您的应用程序在真实环境中运行时，您开始遇到在本地或开发环境中未发现的问题。

您如何与应用程序进行交互以查找应用程序的运行方式并找到问题的根源？JVM的优势之一是可用于诊断的工具数量众多。

如果监视和应用程序日志提供的信息不够，我们必须进入服务器并使用这种类型的实用程序。

其中一些被视为**实验性的工具（jps，jmap，jstack ...）正在收敛到[jcmd命令中](https://docs.oracle.com/en/java/javase/14/docs/specs/man/jcmd.html)**。

jcmd是随JDK一起分发的只有几千字节的实用程序。它只是JVM的前端/客户端，所有逻辑都驻留在JVM中。如果您使用不包含jcmd可执行文件的发行版或docker映像，并将其添加到路径中，则它将不起作用。您需要使用[jcmd模块](https://docs.oracle.com/en/java/javase/11/docs/api/jdk.jcmd/module-summary.html)生成JVM 。

这里可以学习如何使用jcmd诊断问题：获取堆栈跟踪，内存直方图，堆转储，GC日志等。

## **获取pid**

每个进程都有一个关联的进程ID，称为pid。要获得与我们的应用程序关联的pid，可以使用jcmd本身，它将列出所有适用的Java进程。

```
$JAVA_HOME/bin/jcmd
12385 org.apache.catalina.startup.Bootstrap start
3019 sun.tools.jcmd.JCmd
```

我们的应用是tomcat的12385

## **JCMD命令列表**

键入：

```
jcmd 12385 help 
```

### 获得虚拟机版本：

```
jcmd 12385 VM.version
12385:
Java HotSpot(TM) Server VM version 25.171-b11
JDK 8.0_171
```

### 虚拟机JVM参数

```
jcmd 12385 VM.flags
12385:
-XX:CICompilerCount=2 -XX:CMSInitiatingOccupancyFraction=90 -XX:+CMSParallelRemarkEnabled -XX:+CMSScavengeBeforeRemark -XX:+DisableExplicitGC -XX:InitialHeapSize=1572864000 -XX:+ManagementServer -XX:MaxGCPauseMillis=50 -XX:MaxHeapSize=1572864000 -XX:MaxNewSize=134217728 -XX:MaxTenuringThreshold=6 -XX:MinHeapDeltaBytes=131072 -XX:NewSize=134217728 -XX:OldPLABSize=16 -XX:OldSize=1438646272 -XX:+ScavengeBeforeFullGC -XX:+UseAdaptiveGCBoundary -XX:+UseCMSInitiatingOccupancyOnly -XX:+UseConcMarkSweepGC -XX:+UseParNewGC 
```

其他命令，例如VM.system_properties，VM.command_line，VM.uptime，VM.dynlibs，还提供了有关所使用的各种其他属性的其他基本和有用的详细信息。

### 线程打印

该命令用于获取线程转储，即它将打印当前正在运行的所有线程的堆栈跟踪。

```
jcmd 12385 Thread.print 
```

### GC.class_histogram

该命令将提供有关堆使用情况的重要信息，并列出所有类（外部类或特定于应用程序的类）以及许多实例，并按堆使用情况对它们的堆使用进行排序。由于此列表很长，因此可以使用grep命令来查找其已知的类以验证详细信息，或者可以将此命令的输出路由到文件输出。以下是JiveJdon运行情况：

```
num     #instances         #bytes  class name
----------------------------------------------
   1:        851817      171944304  [C
   2:          5157       33565504  [B
   3:         81230       17192520  [Ljava.lang.Object;
   4:        849365       13589840  java.lang.String
   5:        265701        8502432  java.lang.ref.Finalizer
   6:        504856        8077696  java.lang.Long
   7:        222058        5329392  java.util.ArrayList
   8:         58818        5175984  com.jdon.jivejdon.domain.model.ForumMessageReply
   9:        114716        4588640  com.google.common.cache.LocalCache$StrongAccessEntry
  10:         79245        1901880  com.jdon.jivejdon.domain.model.message.MessageVO
  11:        114720        1835520  com.google.common.cache.LocalCache$StrongValueReference
  12:        114716        1835456  com.jdon.cache.CacheableWrapper
  13:         20427        1634160  com.jdon.jivejdon.domain.model.ForumMessage
```

### GC.heap_dump

如果要立即获取jvm堆转储，则可以执行此命令。

```
jcmp 12385 GC.heap_dump 文件名 
```

### JFR命令选项

如果要分析其应用程序的性能问题，那么JFR（即Java Flight Recorder）是一种提供信息的实用工具。尽管JFR是一项商业功能，但人们可以在其本地计算机上免费使用它。

jcmd命令可以提供相关的JFR文件以进行动态分析。默认情况下，JFR功能处于禁用状态，要启用这些功能，需要使用JFR.start

启用JFR功能后，开始JFR录制。就我而言，我要求在延迟10秒后记录30秒。可以根据用途进行配置。也可以使用 JFR.check 命令检查记录的状态。

### VM.native_memory（本机内存跟踪）

这是最好的命令之一，可以提供有关堆和非堆 内存的许多有用的详细信息。这可用于调整内存使用情况并检测任何内存泄漏。 

众所周知，JVM内存的使用取决于许多内存区域，大致分为堆和非堆内存。要获取完整的JVM内存使用情况的详细信息，请使用此实用程序。如果要创建分布式应用程序，则这对于定义应用程序容器的大小很有用，如果正确调整，则可以节省成本。

要使用此功能，我必须使用其他参数重新启动应用程序，即-XX:NativeMemoryTracking = summary  或 -XX:NativeMemoryTracking = detail。

结果解释：

Java Heap内存外，Thread项指定线程正在使用的内存，Class项还指定捕获的用于存储类元数据的内存，Code项提供用于存储JIT生成的代码的内存，Compiler项本身具有一定的空间使用率，类似GC占用空间。所有这些都属于本机内存使用情况。reserved项可以粗略估计您的应用程序所需的内存，但是各种VM参数仍然可以控制事情，这主要是默认内存使用情况。

例如：

```
Java Heap (reserved=524288KB, committed=524288KB)
                (mmap: reserved=524288KB, committed=524288KB) 
```

我们预定reserved了堆内存即Xms = 512m，相当于524288KB，因此JVM确认的内存与Xms相同。同样，Xmx映射到预定的内存。

### 内存泄漏分析

VM.native_memory命令提供当前内存使用情况的快照。要分析内存泄漏，应在使用命令启动应用程序后将内存统计信息作为基准：

```
jcmd ##pid VM.native_memory baseline 
```

然后，可以使用summary.diff观察更改，确切地使用内存。随着GC的运行，您会发现内存增加和减少。但是，如果仅增加内存使用量，则可能是内存泄漏问题。确定泄漏区域，例如堆，线程，代码，类等。如果您的应用程序需要更多内存，请相应地调整相应的VM参数。

如果内存泄漏在堆中，请进行堆转储（如前所述），或者如果线程数在增加，请使用线程池。如果任何线程导致OOM，可以调整Xss。 

```
thread (reserved=11895KB +529KB, committed=11895KB +529KB)
                (thread #20 +2)
                (stack: reserved=11812KB +520KB, committed=11812KB +520KB)
                (malloc=61KB +7KB #101 +10)
                (arena=22KB +2 #35 +4)
```

“ stack ”显示线程堆栈内存，这可能与运行应用程序时使用Xss配置的内容不匹配。由于某些jvm系统线程会根据使用情况分配堆栈内存，因此用户无法使用Xss覆盖堆栈内存。 

我们将线程堆栈大小指定为Xss = 256k，这里共有18 + 2 = 20个线程。这两个额外的线程是处理请求的特定于应用程序的线程，因此2 *（Xss = 256k）〜520k。

https://www.jdon.com/54887