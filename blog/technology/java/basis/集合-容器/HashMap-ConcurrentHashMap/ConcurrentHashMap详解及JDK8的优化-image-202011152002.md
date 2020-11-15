# 详解ConcurrentHashMap及JDK8的优化

42019.07.06 09:11:42字数 2,237阅读 3,106

由于HashMap在并发中会出现一些问题，所以JDK中提供了并发容器ConcurrentHashMap。有关HashMap并发中的问题和原理，强烈建议查看[这篇文章进行复习](https://links.jianshu.com/go?to=https%3A%2F%2Fblog.csdn.net%2Fy277an%2Farticle%2Fdetails%2F94368875)。

ConcurrentHashMap使用分段锁技术，将整个数据结构分段（默认为16段）进行存储，然后给每一段数据配一把锁（继承ReentrantLock），当一个线程占用锁访问其中一个段的数据的时候，其他段的数据仍然能被其他线程访问，能够实现真正的并发访问。下图为JDK7的数据结构。

![img](image-202011152002/7368936-1aa9bfd6e35fcce5.png)

ConcurrentHashMap使用分段锁技术

## JDK7的操作

### JDK7的put过程

1. 首先对key进行第1次hash，通过hash值确定segment的位置
2. 然后在segment内进行操作，获取锁
3. 获取当前segment的HashEntry数组后对key进行第2次hash，通过hash值确定在HashEntry数组的索引位置
4. 通过继承ReentrantLock的tryLock方法尝试去获取锁，如果获取成功就直接插入相应的位置，如果已经有线程获取该Segment的锁，那当前线程会以自旋的方式去继续的调用tryLock方法去获取锁，超过指定次数就挂起，等待唤醒
5. 然后对当前索引的HashEntry链进行遍历，如果有重复的key，则替换；如果没有重复的，则插入到链头
6. 释放锁

> get操作和put操作类似，也是要两次hash。但是get操作的concurrenthashmap不需要加锁，原因是将存储元素都标记了volatile。要是不明白volatile，欢迎复习[这篇博客](https://links.jianshu.com/go?to=https%3A%2F%2Fblog.csdn.net%2Fy277an%2Farticle%2Fdetails%2F92876036)

### JDK7的size过程

1. size操作就是遍历了两次所有的Segments，每次记录Segment的modCount值，然后将两次的modCount进行比较，如果相同，则表示期间没有发生过写入操作，就将原先遍历的结果返回。
2. 如果经判断发现两次统计出的modCount并不一致，要重新启用全部segment加锁的方式来进行count的获取和统计了，这样在此期间每个segement都被锁住，无法进行其他操作，统计出的count自然很准确。

> 在写操作put，remove，扩容的时候，会对Segment加锁，只影响当前Segment，其他的Segment还是可以并发的

## JDK8的优化总结

JDK8的ConcurrentHashMap的数据结构已经接近对应版本的HashMap，了解Hashmap的结构，就基本了解了Concurrenthashmap了，只是增加了同步的操作来控制并发。从JDK7版本的ReentrantLock+Segment+HashEntry，到JDK8版本中synchronized+CAS+HashEntry+红黑树。

![img](image-202011152002/7368936-f2a66df637d88785.jpg)

JDK8的ConcurrentHashMap

1. 数据结构：取消了Segment分段锁的数据结构，取而代之的是Node数组+链表+红黑树的结构，从而实现了对每一行数据进行加锁，进一步减少并发冲突的概率。

> Node类成员变量Node的元素val和指针next都标注volatile，目的是在多线程环境下线程A修改结点的val或者新增节点的时候是对线程B可见的。

> ConcurrentHashMap有成员变量transient volatile Node<K,V>[] table，目的是为了使Node数组在扩容的时候对其他线程具有可见性而加的volatile。（例如：volatile int array[10]是指array的地址是volatile的而不是数组元素的值是volatile的.）

1. 保证线程安全机制：JDK7采用segment的分段锁机制实现线程安全，其中segment继承自ReentrantLock。JDK8采用CAS(读)+Synchronized(写)保证线程安全。
2. 锁的粒度：原来是对需要进行数据操作的Segment加锁，JDK8调整为对每个数组元素加锁（Node）。
3. 链表转化为红黑树：定位结点的hash算法简化会带来弊端，Hash冲突加剧，因此在链表节点数量大于8时，会将链表转化为红黑树进行存储。
4. 查询时间复杂度：从原来的遍历链表O(n)，变成遍历红黑树O(logN)。
5. JDK8推荐使用mappingCount方法而不是size方法获取当前map表的大小，因为这个方法的返回值是long类型，size方法是返回值类型是int。

### JDK8的get过程

1. 计算hash值，定位到该table索引位置，如果是首节点符合就返回
2. 如果遇到扩容的时候，会调用标志正在扩容节点ForwardingNode的find方法，查找该节点，匹配就返回
3. 以上都不符合的话，就往下遍历节点，匹配就返回，否则最后就返回null

> 写到这的时候，笔者建议大家去了解下Redis的渐进式扩容，是另一种思想，都值得学习。一句话帮助理解Redis的渐进式扩容：由于Redis是单线程，而且数据量较大时，无法一次性快速扩容，所以Redis首先申请一个新的容量加倍的哈希表，然后在插入，删除，更新操作的时候，调用rehash函数（dictRehash函数），将原有操作单元的链表移植到新的哈希表中，当原有哈希表全部移植过去，扩容结束。

### JDK8的size过程

两个重要变量：

1. baseCount用于记录节点的个数，是个volatile变量
2. counterCells是一个辅助baseCount计数的数组，每个counterCell存着部分的节点数量，这样做的目的就是尽可能地减少冲突。

counterCell类使用了 @sun.misc.Contended 标记的类，内部一个 volatile变量。这个注解标识着这个类需要避免 "伪共享".

> 避免伪共享(false sharing)。缓存系统中是以缓存行（cache line）为单位存储的。缓存行是2的整数幂个连续字节，
> 一般为32-256个字节。最常见的缓存行大小是64个字节。当多线程修改互相独立的变量时，如果这些变量共享同一个缓存行，就会无意中影响彼此的性能，这就是伪共享。所以伪共享对性能危害很大。
> 没有这个注解之前，是通过使用拼接把缓存行加满来解决这个问题，让缓存之间的修改互不影响。

ConcurrentHashMap节点的数量 = baseCount+counterCells每个cell记录下来的节点数量

由于JDK8在统计这个数量的时候并没有进行加锁，所以这个结果并不是绝对准确的。原理都是相通的，可以顺道看看LongAdder的longValue方法。想学习更多J.U.C的原子操作类，可以查看[这篇博客](https://links.jianshu.com/go?to=https%3A%2F%2Fblog.csdn.net%2Fy277an%2Farticle%2Fdetails%2F90760058)

#### 更新size的过程：

总体的原则就是：先尝试更新baseCount，失败再利用CounterCell。

1. 通过CAS尝试更新baseCount ，如果更新成功则完成，如果CAS更新失败会进入下一步
2. 线程通过随机数ThreadLocalRandom.getProbe() & (n-1) 计算出在counterCells数组的位置，如果不为null，则CAS尝试在couterCell上直接增加数量，如果失败，counterCells数组会进行扩容为原来的两倍，继续随机，继续添加

### JDK8的put过程

对当前的table进行无条件自循环直到put成功

1. 如果没有初始化就先调用initTable（）方法来进行初始化过程
2. 如果没有hash冲突就直接CAS插入
3. 如果还在进行扩容操作就先进行扩容
4. 如果存在hash冲突，就加synchronized锁来保证线程安全，这里有两种情况，一种是链表形式就直接遍历到尾端插入，一种是红黑树就按照红黑树结构插入，
5. 最后一个如果该链表的数量大于阈值8，就要先转换成黑红树的结构，break再一次进入循环
6. 如果添加成功就调用addCount方法统计size，并且检查是否需要扩容

## FAQ

### ConcurrentHashMap迭代器是强一致性还是弱一致性？

ConcurrentHashMap迭代器是弱一致性，hashmap迭代器是强一直性。

ConcurrentHashMap可以支持在迭代过程中，向map添加新元素，而HashMap则抛出了ConcurrentModificationException，因为HashMap包含一个修改计数器，当你调用他的next()方法来获取下一个元素时，迭代器将会用到这个计数器。

### ConcurrentHashMap一定线程安全么？

ConcurrentHashMap使用不当，也会造成死循环的。以后会有博客来介绍~





https://www.jianshu.com/p/31f773086e98