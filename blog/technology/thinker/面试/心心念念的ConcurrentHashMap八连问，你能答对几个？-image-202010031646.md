# 心心念念的ConcurrentHashMap八连问，你能答对几个？

头顶好凉 2020-07-22 20:55:32

## 1.ConcurrentHashMap与HashMap有什么区别？

数据结构：HashMap的数据结构在HashMap那一篇已经有了很详细的说明，这里就不赘述了。在JDK1.7中ConcurrentHashMap底层采用分段数组+链表的方式实现。在JDK1.8中ConcurrentHashMap与JDK1.8中的HashMap底层数据结构一样，都是采用数组+链表或者数组+红黑树的方式实现。这二者底层数据结构都是以数组为主体的。

线程安全：HashMap是线程不安全的，ConcurrentHashMap是线程安全的。

## 2.说一下ConcurrentHashMap的工作原理，put()和get()的工作流程是怎样的？

存储对象时，将key和vaule传给put()方法：

- 如果没有初始化，就调用initTable()方法对数组进行初始化；
- 如果没有hash冲突则直接通过CAS进行无锁插入；
- 如果需要扩容，就先进行扩容，扩容为原来的两倍；
- 如果存在hash冲突，就通过加锁的方式进行插入，从而保证线程安全。（如果是链表就按照尾插法插入，如果是红黑树就按照红黑树的数据结构进行插入）；
- 如果达到链表转红黑树条件，就将链表转为红黑树；
- 如果插入成功就调用addCount()方法进行计数并且检查是否需要扩容；

注意：在并发情况下ConcurrentHashMap会调用多个工作线程一起帮助扩容，这样效率会更高。

下面以一个很详细的流程图方式展现一下ConcurrentHashMap的put()过程（由于流程图比较庞大复杂，所以没有将计数和扩容阶段的流程画出，有兴趣的小伙伴可以去看一下addCount()和transfer()这两个方法的源码）：

![心心念念的ConcurrentHashMap八连问，你能答对几个？](image-202010031646/73fe123b0ef5441785fc1e25fddf29c3)



获取对象时，将key传给get()方法：

- 计算hash值，定位table索引位置，如果头节点符合条件则直接返回key对应的value；
- 如果遇到正在扩容，则调用标记正在扩容的节点，查找该节点，匹配就返回；
- 以上条件都不符合，就继续向下遍历；

注意：其实get()的流程跟HashMap基本是一样的。put()的流程只是比HashMap多了一些保证线程安全的操作而已

## 3.ConcurrentHashMap和HashTable的效率哪个更高？为什么？

ConcurrentHashMap的效率要高于HashTable，因为HashTable是使用一把锁锁住整个链表结构从而实现线程安全。而ConcurrentHashMap的锁粒度更低，在JDK1.7中采用分段锁实现线程安全，在JDK1.8中采用CAS（无锁算法）+Synchronized实现线程安全。

**追问：那你具体说一下HashTable和ConcurrentHashMap的锁机制（重点）**

HashTable中的锁机制：

HashTab是使用Synchronized来实现线程安全的，是使用一把锁锁住整个链表结构，效率非常低。当有一个线程访问同步方法的时候，其他线程是访问不了的，其他线程可能会被阻塞或者进入轮询状态。如果有一个线程正在执行put()操作的时候，其他线程是不可以进行put()操作的，也不可以进行get()操作，并发线程越多，竞争越激烈，效率越低下。

![心心念念的ConcurrentHashMap八连问，你能答对几个？](image-202010031646/ae4535d06785458a8c760843c89cb3ba)



**ConcurrentHashMap在JDK1.7中的分段锁机制：**

对整个数组进行分段（每段都是由若干个hashEntry对象组成的链表），每个分段都有一个Segment分段锁（继承ReentrantLock分段锁），每个Segment分段锁只会锁住它锁守护的那一段数据，多线程访问不同数据段的数据，就不会存在竞争，从而提高了并发的访问率。

![心心念念的ConcurrentHashMap八连问，你能答对几个？](image-202010031646/7dfa4ac2b1c8485885a8cadefb515058)



**ConcurrentHashMap在JDK1.8中的锁机制：**

ConcurrentHashMap在JDK1.8中采用Node+CAS+Synchronized实现线程安全，取消了segment分段锁，直接使用Table数组存储键值对（与1.8中的HashMap一样），主要是使用Synchronized+CAS的方法来进行并发控制。在put()的时候如果CAS失败就说明存在竞争，会进行自旋，具体流程上面已有说明，这里就不在赘述。

![心心念念的ConcurrentHashMap八连问，你能答对几个？](image-202010031646/d6a181c37c0c4dd3ac5bfa567c7fc199)



## 4.ConcurrentHashMap在JDK1.8中为什么要使用内置锁Synchronized来替换ReentractLock重入锁？

- 锁粒度降低了；
- 官方对synchronized进行了优化和升级，使得synchronized不那么“重”了；
- 在大数据量的操作下，对基于API的ReentractLock进行操作会有更大的内存开销；

## 5.ConcurrentHashMap的get()方法需要加锁吗？

不需要，get操作可以无锁是由于Node的元素val和指针next是用volatile修饰的，在多线程环境下线程A修改结点的val或者新增节点的时候是对线程B可见的。

## 6.ConcurrentHashMap中的key和value可以为null吗？为什么？

不可以，因为源码中是这样判断的，进行put()操作的时候如果key为null或者value为null，会抛出NullPointerException空指针异常。

追问：那么源码为什么要这么设计呢？

如果ConcurrentHashMap中存在一个key对应的value是null，那么当调用map.get(key)的时候，必然会返回null，那么这个null就有两个意思：

- 这个key从来没有在map中映射过，也就是不存在这个key；
- 这个key是真实存在的，只是在设置key的value值的时候，设置为null了；

这个二义性在非线程安全的HashMap中可以通过map.containsKey(key)方法来判断，如果返回true，说明key存在只是对应的value值为空。如果返回false，说明这个key没有在map中映射过。这样是为什么HashMap可以允许键值为null的原因，但是ConcurrentHashMap只用这个判断是判断不了二义性的。

**追问：说说为什么ConcurrentHashMap判断不了呢？**

此时如果有A、B两个线程，A线程调用ConcurrentHashMap.get(key)方法返回null，但是我们不知道这个null是因为key没有在map中映射还是本身存的value值就是null，此时我们假设有一个key没有在map中映射过，也就是map中不存在这个key，此时我们调用ConcurrentHashMap.containsKey(key)方法去做一个判断，我们期望的返回结果是false。但是恰好在A线程get(key)之后，调用constainsKey(key)方法之前B线程执行了ConcurrentHashMap.put(key,null)，那么当A线程执行完containsKey(key)方法之后我们得到的结果是true，与我们预期的结果就不相符了。

至于ConcurrentHashMap中的key为什么也不能为null的问题，ConcurrentHashMap的作者Doug Lea认为map中允许键值为null是一种不合理的设计，HashMap虽然可以判断二义性，但是Doug Lea仍然觉得这样设计是不合理的。

## 7.ConcurrentHashMap的并发度是什么？

程序在运行时能够同时更新ConcurrentHashMap且不产生锁竞争的最大线程数默认是16，这个值可以在构造函数中设置。如果自己设置了并发度，ConcurrentHashMap会使用大于等于该值的最小的2的幂指数作为实际并发度，也就是比如你设置的值是17，那么实际并发度是32。

## 8.你认为自己有什么缺点？（HR提问）

回答此问题，注意以下几点即可：

不能说自己没有缺点，太假了吧！

不要说影响工作的缺点，这不是把致命弱点告诉别人嘛！

可以说一些表面上的缺点，从工作的角度来看是优点的缺点，可以说对事情和自我要求都比较高，比如代码，产品的设计，都比较追求完美，但是也会根据实际情况调整！

> 作者：Yz_Jesse
>
> 原文链接：https://blog.csdn.net/w1453114339/article/details/107175612