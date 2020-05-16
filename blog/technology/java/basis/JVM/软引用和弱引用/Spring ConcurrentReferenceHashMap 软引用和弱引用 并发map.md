# Spring ConcurrentReferenceHashMap 软引用和弱引用 并发map

ConcurrentReferenceHashMap与ConcurrentHashMap的区别是ConcurrentReferenceHashMap能指定所存放对象的引用级别，适用于并发下Map的数据缓存。

**注：Java四种对象引用级别：强引用、软引用、弱引用、虚引用**

测试代码：

注：用弱引用来及时查看效果。

```java
public class TestConcurrentReferenceHashMap {

    public static void main(String[] args) {
        ConcurrentReferenceHashMap map = new ConcurrentReferenceHashMap(16, ConcurrentReferenceHashMap.ReferenceType.WEAK);
        map.put("key","val");

        System.out.println(map);

        System.gc();
        try {
            Thread.currentThread().sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(map);

    }
} 
```

结果：

```
{key=val}
{}

Process finished with exit code 0
```

注：从下面ConcurrentReferenceHashMap的部分源码我们可以知道ConcurrentReferenceHashMap是基于Segment分段锁实现的，与JDK1.7的ConcurrentHashMap是一样的，与JDK1.8的ConcurrentHashMap是不一样的。

```java
protected final class Segment extends ReentrantLock {
        private final ConcurrentReferenceHashMap<K, V>.ReferenceManager referenceManager = ConcurrentReferenceHashMap.this.createReferenceManager();
        private final int initialSize;
        private volatile ConcurrentReferenceHashMap.Reference<K, V>[] references;
        private volatile int count = 0;
        private int resizeThreshold;
}
```



https://blog.csdn.net/u012834750/article/details/72403978