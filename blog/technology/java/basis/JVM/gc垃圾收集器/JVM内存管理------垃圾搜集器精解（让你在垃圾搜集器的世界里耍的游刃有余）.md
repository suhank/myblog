[TOC]



# JVM内存管理------垃圾搜集器精解（让你在垃圾搜集器的世界里耍的游刃有余）

## **引言** 

​         在上一章我们已经探讨过hotspot上垃圾搜集器的实现，一共有六种实现六种组合。本次LZ与各位一起探讨下这六种搜集器各自的威力以及组合的威力如何。

​         为了方便各位的观看与对比，LZ决定采用当初写设计模式时使用的方式，针对某些搜集器，分几个维度去解释这些搜集器。

 

## **client模式与server模式**

 

​         在介绍本章内容之前，要说一下JVM的两种模式，**一种是client模式，一种是server模式**。我们平时开发使用的模式**默认是client模式**，也可以使用命令行参数**-server**强制开启server模式，两者最大的区别在于**在server模式下JVM做了很多优化**。

​         **server模式下的JAVA应用程序启动较慢**，不过由于server模式下JVM所做的优化，**在程序长时间运行下，运行速度将会越来越快**。相反，**client模式下的JAVA应用程序虽然启动快，但不适合长时间运行**，若是运行时间较长的话，则会在性能上明显低于server模式。

 

## **搜集器详解**

  

​         以下我们先探讨一下单个垃圾搜集器的相关内容，最后我们再简单的谈一下组合之后，各个组合的特点。

​         

### **Serial Garbage Collector**

​         **算法**：采用复制算法

​         **内存区域**：针对新生代设计

​         **执行方式**：单线程、串行

​         **执行过程**：当新生代内存不够用时，先**暂停全部用户程序**，然后开启**一条GC线程**使用复制算法对垃圾进行回收，这一过程中可能会有一些对象提升到年老代

​         **特点**：由于单线程运行，且整个GC阶段都要暂停用户程序，因此会造成应用程序停顿时间较长，但对于**小规模**的程序来说，却非常适合。

​         **适用场景**：平时的开发与调试程序使用，以及桌面应用交互程序。

​         **开启参数**：-XX:+UseSerialGC（client模式默认值） 

​        

### **Serial Old Garbage Collector**

   

​         这里针对serial old搜集器不再列举各个维度的特点，因为它与serial搜集器是一样的，区别是它是针对**年老代**而设计的，因此采用**标记/整理**算法。对于其余的维度特点，serial old与serial搜集器一模一样。

 

### **ParNew Garbage Collector**

 

​         **算法**：采用复制算法

​         **内存区域**：针对新生代设计

​         **执行方式**：多线程、并行

​         **执行过程**：当新生代内存不够用时，先暂停全部用户程序，然后开启**若干条GC线程**使用复制算法并行进行垃圾回收，这一过程中可能会有一些对象提升到年老代

​         **特点**：采用多线程并行运行，因此会对系统的内核处理器数目比较敏感，至少需要多于一个的处理器，**有几个处理器就会开几个线程（不过线程数是可以使用参数-XX:ParallelGCThreads=<N>控制的）**，因此只**适合于多核多处理器的系统**。尽管整个GC阶段还是要暂停用户程序，但多线程并行处理并不会造成太长的停顿时间。因此就吞吐量来说，ParNew要大于serial，在处理器越多的时候，效果越明显。但是这并非绝对，**对于单个处理器来说，由于并行执行的开销（比如同步），ParNew的性能将会低于serial搜集器**。不仅是单个处理器的时候，如果在容量较小的堆上，甚至在两个处理器的情况下，ParNew的性能都并非一定可以高过serial。

​         **适用场景**：在中到大型的堆上，且系统处理器至少多于一个的情况

​         **开启参数**：-XX:+UseParNewGC

 

### **Parallel Scavenge Garbage Collector**

​        这个搜集器与ParNew几乎一模一样，都是针对**新生代**设计，采用**复制算法**的并行搜集器。它与ParNew最大的不同就是可设置的参数不一样，它可以让我们**更精确的控制GC停顿时间以及吞吐量**。

​        parallel scavenge搜集器提供参数主要包括**控制最大的停顿时间（使用-XX:MaxGCPauseMillis=<N>），以及控制吞吐量（使用-XX:GCTimeRatio=<N>）**。由此可以看出，parallel scavenge就是为了提供吞吐量控制的搜集器。

​        不过千万不要以为把最大停顿时间调的越小越好，或者吞吐量越大越好，在使用parallel scavenge搜集器时，主要有三个性能指标，**最大停顿时间、吞吐量以及新生代区域的最小值**。

​        parallel scavenge搜集器具有相应的调节策略，它将会**优先满足最大停顿时间的目标，次之是吞吐量，最后才是新生代区域的最小值**。

​        因此，如果将最大停顿时间调的过小，将会牺牲整体的吞吐量以及新生代大小来满足你的私欲。手心手背都是肉，我们最好还是不要这么干。不过parallel scavenge有一个参数可以让parallel scavenge搜集器全权接手内存区域大小的调节，这其中还包括了晋升为年老代（可使用-XX:MaxTenuringThreshold=n调节）的年龄，也就是使用-XX:UseAdaptiveSizePolicy打开内存区域大小**自适应策略**。

​        parallel scavenge搜集器可使用参数-XX:+UseParallelGC开启，同时它也是server模式下默认的新生代搜集器。 

### **Parallel Old Garbage Collector**

 

​         Parallel Old与ParNew或者Parallel Scavenge的关系就好似serial与serial old一样，相互之间的区别并不大，只不过parallel old是针对年**老代设计的并行搜集器**而已，因此它采用**标记/整理**算法。

​         Parallel Old搜集器还有一个重要的意义就是，**它是除了serial old以外唯一一个可以与parallel scavenge搭配工作的年老代搜集器**，因此为了避免serial old影响parallel scavenge可控制吞吐量的名声，**parallel old就作为了parallel scavenge真正意义上的搭档**。

​         它可以使用参数-XX:-UseParallelOldGC开启，不过在JDK6以后，它也是在开启parallel scavenge之后默认的年老代搜集器。

 

### **Concurrent Mark Sweep Garbage Collector**

​         concurrent mark sweep（以下简称CMS）搜集器是唯一一个真正意义上实现了**应用程序与GC线程一起工作**（一起是针对客户而言，而并不一定是真正的一起，有可能是快速交替）的搜集器。

​         CMS是针对**年老代**设计的搜集器，并采用**标记/清除**算法，它也是**唯一一个**在年老代采用**标记/清除**算法的搜集器。

​         采用标记/清除算法是因为它特殊的处理方式造成的，它的处理分为四个阶段。

​         1、**初始标记**：需要暂停应用程序，快速标记存活对象。

​         2、**并发标记**：恢复应用程序，并发跟踪GC Roots。

​         3、**重新标记**：需要暂停应用程序，重新标记跟踪遗漏的对象。

​         4、**并发清除**：恢复应用程序，并发清除未标记的垃圾对象。

​         它比原来的标记/清除算法复杂了点，主要表现在**并发标记和并发清除**这两个阶段，而这两个阶段也是整个GC阶段中**耗时最长的阶段**，不过由于这两个阶段皆是与应用程序并发执行的，因此CMS搜集器造成的停顿时间是非常短暂的。这点还是比较好理解的。

​         不过它的**缺点**也是要简单提一下的，主要有以下几点。

​         1、由于GC线程与应用程序并发执行时会抢占CPU资源，因此会造成**整体的吞吐量下降**。也就是说，从吞吐量的指标上来说，CMS搜集器是要弱于parallel scavenge搜集器的。LZ这里从oracle官网上摘录下一段关于CMS的描述，里面提到CMS性能与CPU个数的关系。

​         Since at least one processor is utilized for garbage collection during the concurrent phases, the concurrent collector does not normally provide any benefit on a uniprocessor (single-core) machine. However, there is a separate mode available that can achieve low pauses on systems with only one or two processors; see incremental mode below for details.[](http://www.oracle.com/technetwork/java/javase/gc-tuning-6-140523.html#icms)

​         LZ的英文很一般（四级都没过，惭愧，0.0），不过在借助工具的情况下也能大致翻译出来这段话的意思，如下。

​         中文大意：**由于在并发阶段垃圾搜集至少使用了一个处理器，因此在单处理器的情况下使用并发搜集器，将得不到任何好处。不过，在单个或两个处理器的系统上，有一种独立的方式可以有效的达到低停顿的目的，详情见下方的增量模式（incremental mode）。**

​         很明显，oracle的文档指出，**在单处理器的情况下，并发搜集器会因为抢占处理器，而造成性能降低**。最后给出了一种增量模式的处理方式，不过在《深入理解JAVA虚拟机》一书中指出，**增量模式已经被定义为不推荐使用**。由于LZ摘录的这段官方介绍是基于JDK5.0的介绍，而《深入理解JAVA虚拟机》一书中则是指的JDK6.0的版本，因此LZ暂且猜测，增量模式是在JDK6.0发布的时候被废弃了，不过这个废弃的时间或者说版本其实已经不重要了。

​         2、标记/清除很大的一个缺点，那就是**内存碎片**的存在。因此JVM提供了-XX:+UseCMSCompactAtFullCollection参数用于在全局GC（full GC）后进行一次碎片整理的工作，由于每次全局GC后都进行碎片整理会较大的影响停顿时间，JVM又提供了参数-XX:CMSFullGCsBeforeCompaction去**控制在几次全局GC后会进行碎片整理**。

​         3、CMS最后一个缺点涉及到一个术语---**并发模式失败（Concurrent Mode Failure****）**。对于这个术语，官方是这样解释的。

​         if the concurrent collector is unable to finish reclaiming the unreachable objects before the tenured generation fills up, or if an allocation cannot be satisfied with the available free space blocks in the tenured generation, then the application is paused and the collection is completed with all the application threads stopped.The inability to complete a collection concurrently is referred to as concurrent mode failure and indicates the need to adjust the concurrent collector parameters.

​         中文大意：如果**并发搜集器不能在年老代填满之前完成不可达（unreachable）对象的回收**，或者**年老代中有效的空闲内存空间不能满足某一个内存的分配请求**，此时应用会被暂停，并在此暂停期间开始垃圾回收，直到回收完成才会恢复应用程序。这种无法并发完成搜集的情况就成为**并发模式失败（concurrent mode failure）**，而且这种情况的发生也意味着我们需要调节并发搜集器的参数了。

​         上面两个情况感觉有点重复，不能满足内存的分配请求不就是在年老代填满之前，没有完成对象回收造成的吗？

​         这里LZ个人的理解是，年老代填满之前无法完成对象回收是指**年老代在并发清除阶段清除不及时**，因此造成的空闲内存不足。而不能满足内存的分配请求，则主要指的是新生代在提升到年老代时，由于**年老代的内存碎片过多**，导致一些分配由于没有连续的内存无法满足。

​         实际上，在**并发模式失败**的情况下，**serial old**会作为备选搜集器，进行一次全局GC（Full GC），因此serial old也算是CMS的“替补”。显然，由于serial old的介入，会造成较大的停顿时间。

​         为了尽量避免并发模式失败发生，我们可以调节-XX:CMSInitiatingOccupancyFraction=<N>参数，去控制当年老代的内存占用达到多少的时候（N%），便开启并发搜集器开始回收年老代。

**组合的威力** 

​         上面我们已经简单的介绍了各个搜集器的特点，下面LZ与各位分享三个典型的组合，其余三种组合一般不常用。

 

### **serial & serial old**

​         这个组合是我们最常见的组合之一，也是client模式下的默认垃圾搜集器组合，也可以使用参数-XX:+UseSerialGC强制开启。

​         由于它实现相对简单，没有线程相关的额外开销（主要指线程切换与同步），因此非常适合**运行于客户端PC的小型应用程序，或者桌面应用程序（比如swing编写的用户界面程序），以及我们平时的开发、调试、测试**等。

​         上面三种情况都有共同的特点。

​         1、由于都是在PC上运行，因此配置一般不会太高，或者说处理器个数不会太多。

​         2、上面几种情况的应用程序都不会运行太久。

​         3、规模不会太大，也就是说，堆相对较小，搜集起来也比较快，停顿时间会比较短。

​         

**Parallel Scavenge & Parallel Old**

​         这个组合我们并不常见，毕竟它不会出现在我们平时的开发当中，但是它却是很多对吞吐量（throughout）要求较高或者对停顿时间（pause time）要求不高的应用程序的首选，并且这个组合是server模式下的默认组合（JDK6或JDK6之后）。当然，它也可以使用-XX:+UseParallelGC参数强制开启。

​         该组合无论是新生代还是年老代都采用并行搜集，因此停顿时间较短，系统的整体吞吐量较高。它适用于一些**需要长期运行且对吞吐量有一定要求的后台程序**。

​         这些运行于后台的程序都有以下特点。

​         1、系统配置较高，通常情况下至少四核（以目前的硬件水平为准）。

​         2、对吞吐量要求较高，或需要达到一定的量。

​         3、应用程序运行时间较长。

​         4、应用程序规模较大，一般是中到大型的堆。

 

### **ParNew & CMS（Serial Old作为替补）**

​         这个组合与上面的并行组合一样，在平时的开发当中都不常见，而它则是对相应时间（response time）要求较高的应用程序的首选。该组合需要使用参数-XX:+UseConcMarkSweepGC开启。

​         该组合在新生代采用并行搜集器，因此新生代的GC速度会非常快，停顿时间很短。而年老代的GC采用并发搜集，大部分垃圾搜集的时间里，GC线程都是与应用程序并发执行的，因此造成的停顿时间依然很短。它适用于一些**需要长期运行且对相应时间有一定要求的后台程序**。

​         这些运行于后台的程序的特点与并行模式下的后台程序十分类似，不同的是第二点，采用ParNew & CMS组合的后台应用程序，一般都对相应时间有一定要求，最典型的就是我们的WEB应用程序。

 

## **结束语** 

​         本次LZ整理了各个搜集器的特点与各个组合的特点，此外，还有剩下的三种组合LZ这里没有提到，原因是这三种组合都不是特别常用，或者可以说几乎不用，因为这三个组合都给人一种四不像的感觉，而且效果也确实不好。

​         希望本文能给各位带来一些帮助，感谢各位的收看。





http://www.cnblogs.com/zuoxiaolong/p/jvm8.html