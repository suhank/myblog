# TransmittableThreadLocal详解

2019-08-09阅读 1.2K0

## 1、简介

TransmittableThreadLocal 是Alibaba开源的、用于解决 “在使用线程池等会缓存线程的组件情况下传递ThreadLocal”的扩展，比如：线程本地变量在线程池之间的传递问题。若希望 TransmittableThreadLocal 在线程池与主线程间传递，需配合 TtlRunnable 和 TtlCallable 使用。

## 2、使用场景

下面是几个典型场景例子。

- 分布式跟踪系统
- 应用容器或上层框架跨应用代码给下层SDK传递信息
- 日志收集记录系统上下文

## 3、源码分析

TransmittableThreadLocal 继承自 InheritableThreadLocal，这样可以在不破坏ThreadLocal 本身的情况下，使得当用户利用 new Thread() 创建线程时仍然可以达到传递InheritableThreadLocal 的目的。

```javascript
public class TransmittableThreadLocal<T> extends InheritableThreadLocal<T> { ......
```

TransmittableThreadLocal 相比较 InheritableThreadLocal 很关键的一点改进是引入holder变量，这样就不必对外暴露Thread中的 inheritableThreadLocals(参考InheritableThreadLocal详解

> https://www.jianshu.com/p/94ba4a918ff5

)，保持ThreadLocal.ThreadLocalMap的封装性。

```javascript
/ 理解holder，需注意如下几点：
// 1、holder 是 InheritableThreadLocal 变量；
// 2、holder 是 static 变量；
// 3、value 是 WeakHashMap；
// 4、深刻理解 ThreadLocal 工作原理；
private static InheritableThreadLocal<Map<TransmittableThreadLocal<?>, ?>> holder =
      new InheritableThreadLocal<Map<TransmittableThreadLocal<?>, ?>>() {
          @Override
          protected Map<TransmittableThreadLocal<?>, ?> initialValue() {
              return new WeakHashMap<>();
          }

          @Override
          protected Map<TransmittableThreadLocal<?>, ?> childValue(Map<TransmittableThreadLocal<?>, ?> parentValue) {
              return new WeakHashMap<>(parentValue);
          }
      };
```

个人认为 holder 变量的设计，极大体现了作者的智慧，让人无数次献上膝盖。。。

```javascript
// 调用 get() 方法时，同时将 this 指针放入 holder
public final T get() {
    T value = super.get();
    if (null != value) {
        addValue();
    }
    return value;
}
void addValue() {
    if (!holder.get().containsKey(this)) {
        holder.get().put(this, null); // WeakHashMap supports null value.
    }
}
// 调用 set() 方法时，同时处理 holder 中 this 指针
public final void set(T value) {
    super.set(value);
    if (null == value) { // may set null to remove value
        removeValue();
    } else {
        addValue();
    }
}
void removeValue() {
    holder.get().remove(this);
}
```



## 4、工作流程简介

自定义 TtlRunnable 实现 Runnable，TtlRunnable初始化方法中保持当前线程中已有的TransmittableThreadLocal

```javascript
private TtlRunnable(Runnable runnable, boolean releaseTtlValueReferenceAfterRun) {
    this.copiedRef = new AtomicReference<Map<TransmittableThreadLocal<?>, Object>>(TransmittableThreadLocal.copy());
    this.runnable = runnable;
    this.releaseTtlValueReferenceAfterRun = releaseTtlValueReferenceAfterRun;
}
```

线程池中线程 调用run方法，执行前先backup holder中所有的TransmittableThreadLocal， copiedRef中不存在，holder存在的，说明是后来加进去的，remove掉holder中的；将copied中的TransmittableThreadLocal set到当前线程中

```javascript
public void run() {
    Map<TransmittableThreadLocal<?>, Object> copied = copiedRef.get();
    if (copied == null || releaseTtlValueReferenceAfterRun && !copiedRef.compareAndSet(copied, null)) {
        throw new IllegalStateException("TTL value reference is released after run!");
    }

    Map<TransmittableThreadLocal<?>, Object> backup = TransmittableThreadLocal.backupAndSetToCopied(copied);
    try {
        runnable.run();
    } finally {
        TransmittableThreadLocal.restoreBackup(backup);
    }
}
```

执行后再恢复 backup 的数据到 holder 中（backup中不存在，holder中存在的TransmittableThreadLocal，从holder中remove掉），将 backup 中的 TransmittableThreadLocal set到当前线程中

## 5、参考文献

1. **[transmittable-thread-local](https://link.jianshu.com/?t=https%3A%2F%2Fgithub.com%2Falibaba%2Ftransmittable-thread-local)**





https://cloud.tencent.com/developer/article/1484420