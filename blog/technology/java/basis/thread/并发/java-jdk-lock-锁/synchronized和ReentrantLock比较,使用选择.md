# synchronized和ReentrantLock比较,使用选择

程序员Share 2019-08-05 06:20:00

 

**案例**

在分析为什么不用 synchronized 这个问题之前，我们先用代码说话，LockTest 测试案例：

```java
 
/**
 * 案例
 */
public class LockTest {

    private static Lock lock = new ReentrantLock();

    private static int num1 = 0;
    private static int num2 = 0;
    public static void main(String[] args) {
        lockTest();
        SyncDemo();
    }
    /**
     * 本机测试下20万自增基本能确定性能,但是不是特别明显,50万差距还是挺大的
     * 20万以下数据synchronized优于Lock
     * 20万以上数据Lock优于synchronized
     */
    public static void lockTest (){
        long start = System.currentTimeMillis();
        for(int i=0;i<500000;i++){
            final int num = i;
            new Runnable() {
                @Override
                public void run() {
                    lock(num);
                }
            }.run();
        }
        long end = System.currentTimeMillis();
        System.out.println("累加："+num1);
        System.out.println("ReentrantLock锁："+ (end-start));
    }
    public static void SyncDemo(){
        long start = System.currentTimeMillis();
        for(int i=0;i<500000;i++){
            final int num = i;
            new Runnable() {
                @Override
                public void run() {
                    sync(num);
                }
            }.run();
        }
        long end = System.currentTimeMillis();
        System.out.println("累加："+num2);
        System.out.println("synchronized锁："+ (end-start));
    }
    public static void lock(int i){
        lock.lock();
        num1 ++;
        lock.unlock();
    }
    public static synchronized void sync(int i){
        num2 ++;
    }
}
```

50万++测试数据：

```
累加：500000
ReentrantLock锁：20
累加：500000
synchronized锁：28
```

用数据说话，很明显在高并发下，ReentrantLock 的性能是要优于 synchronized 的，虽然仅仅是几毫秒的差距，当然这里我并没有对比CPU的使用情况。

10万++测试数据：

```
累加：100000
ReentrantLock锁：13
累加：100000
synchronized锁：8
```

**分析**

这时候小伙伴可能会问了，有没有一个准确的临界值，来区分使用这两种锁？当然，在回答这个问题之前，先了解一下这两种锁到底有何异同。

**锁的实现**

Synchronized是依赖于JVM实现的，表现为原生语法层面的互斥锁。开发者是无法直接看到相关源码，但是我们可以通过利用javap工具查看生成的class文件信息来分析Synchronize的实现。同步代码块是使用monitorenter和monitorexit指令实现的，同步方法依靠的是方法修饰符上的ACC_SYNCHRONIZED实现。

ReenTrantLock是基于JDK实现的，一个表现为API层面的互斥锁，开发人员通过查阅源码就可以了解到。

**可重入性**

ReenTrantLock 的字面意思就是再进入的锁，synchronized关键字所使用的锁也是可重入的，两者关于这个的区别不大。

**功能区别**

Synchronized的使用比较方便，不需要开发者手动加锁和释放锁，而ReenTrantLock需要手工声明来加锁和释放锁(lock() 和 unlock() 方法配合 try/finally 语句块来实现)

ReenTrantLock 在锁的细粒度和灵活度上要优于Synchronized。此外，还增加了一些高级特性，主要有以下3项：等待可中断、可实现公平锁以及锁可以绑定多个条件。

**发展历史**

关于synchronized 与ReentrantLock

在JDK 1.6之后，虚拟机对于synchronized关键字进行整体优化后，在性能上synchronized与ReentrantLock已没有明显差距，因此在使用选择上，需要根据场景而定，大部分情况下我们依然建议是synchronized关键字，原因之一是使用方便语义清晰，二是性能上虚拟机已为我们自动优化。而ReentrantLock提供了多样化的同步特性，如超时获取锁、可以被中断获取锁（synchronized的同步是不能中断的）、等待唤醒机制的多个条件变量(Condition)等，因此当我们确实需要使用到这些功能是，可以选择ReentrantLock

 

<https://www.toutiao.com/a6720854871156720132/?tt_from=android_share&utm_campaign=client_share×tamp=1564964525&app=news_article&utm_medium=toutiao_android&req_id=201908050822050100170390285919BA7&group_id=6720854871156720132>