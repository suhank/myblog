[TOC]



# java 可重入锁ReentrantLock简单用法

2018年04月24日 19:16:45

Java 中显示锁的接口和类主要位于`java.util.concurrent.locks`下，其主要的接口和类有：

- 锁接口Lock，其主要实现为ReentrantLock
- 读写锁接口ReadWriteLock，其主要实现为ReentrantReadWriteLock


## 一、接口Lock

其中显示锁Lock的定义为：

```java
public interface Lock {
    void lock();
    void lockInterruptibly() throws InterruptedException;
    boolean tryLock();
    boolean tryLock(long time, TimeUnit unit) throws InterruptedException;
    void unlock();
    Condition newCondition();
} 
```

其中：

1. lock()/unlock() ： 为获取锁和释放锁的方法，其中lock()会阻塞程序，直到成功的获取锁。
2. lockInterruptibly()：与lock()不同的地方是，它可以响应程序中断，如果被其他程序中断了，则抛出InterruptedException。
3. tryLock()：尝试获取锁，该方法会立即返回，并不会阻塞程序。如果获取锁成功则返回true，反之则返回false。
4. tryLock(long time, TimeUnit unit)：尝试获取锁，如果能获取锁则直接返回true；否则阻塞等待，阻塞时长由传入的参数来决定，在等待的同时响应程序中断，如果发生了中断则抛出InterruptedException；如果在等待的时间中获取了锁则返回true，反之返回false。
5. newCondition()：新建一个条件，一个Lock可以关联多个条件。

**相比synchronized，显示锁可以用非阻塞的方式获取锁，可以响应程序中断，可以设定程序的阻塞时间，拥有更加灵活的操作。**

## 二、可重入锁ReentrantLock

### 2.1 基本用法

ReentrantLock是Lock接口的主要实现类，其基本用法`lock()/unlock()`实现了与`synchronized`一样的语义，其中包括：

- 可重入，一个线程在持有一个锁的前提下，可以继续获得该锁；
- 可以解决竞态条件问题（临界区资源）；
- 可以保证内存可见性问题。

ReentrantLock有两个构造方法。

```
public ReentrantLock()
public ReentrantLock(boolean fair) 
```

参数fair表示是否保证公平，在不指定的情况下默认值为false，表示不保证公平。

**公平的意思是指：等待时间最长的线程优先获取锁。**

但是保证公平可能会影响程序的性能，在一般情况下也不需要保证公平，所以默认值为 false 。而synchronized也是不保证公平的。

在使用显示锁的情况下，一定要记得调用 unlock 。一般而言，应该将 lock 之后的代码块包装在 try 语句中，在 finally 语句中释放锁，例如以下实现计数器的代码：

```
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Counter {
    private final Lock lock = new ReentrantLock();
    private volatile int count;
    public void incr() {
        lock.lock();
        try {
            count++;
        } finally {
            lock.unlock();
        }
    }
    public int getCount() {
        return count;
    }
} 
```

### 2.2 使用tryLock避免死锁

使用`tryLock()`方法可以避免死锁的发生。在持有一个锁而尝试获取另外一个锁，但是获取不到的时候，可以释放已持有的锁，给其他线程获取锁的机会，然后重试获取所有的锁。

接下来使用银行之间转账的例子。

**表示账户的Account类：**

```java
public class Account {
    private Lock lock = new ReentrantLock();
    private volatile double money;
    public Account(double initialMoney) {
        this.money = initialMoney;
    }
    public void add(double money) {
        lock.lock();
        try {
            this.money += money;
        } finally {
            lock.unlock();
        }
    }
    public void reduce(double money) {
        lock.lock();
        try {
            this.money -= money;
        } finally {
            lock.unlock();
        }
    }
    public double getMoney() {
        return money;
    }
    void lock() {
        lock.lock();
    }
    void unlock() {
        lock.unlock();
    }
    boolean tryLock() {
        return lock.tryLock();
    }
} 
```

Account类中的money表示当前的余额。add/reduce用于修改余额。在账户之间转账，需要这两个账户都要进行锁定。如果我们直接只用 lock() ，我们的代码清单如下：

```java
public class AccountMgr {
    public static class NoEnoughMoneyException extends Exception {}
    public static void transfer(Account from, Account to, double money)
            throws NoEnoughMoneyException {
        from.lock();
        try {
            to.lock();
            try {
                if(from.getMoney() >= money) {
                    from.reduce(money);
                    to.add(money);
                } else {
                    throw new NoEnoughMoneyException();
                }
            } finally {
                to.unlock();
            }
        } finally {
            from.unlock();
        }
    }
} 
```

但是这种写法容易发生死锁。比如，两个账户都想同时给对方进行转账，并且均获得了第一个锁。在这种情况下就会发生死锁。

接下来的代码用于模拟账户转账的死锁过程。

```java
public static void simulateDeadLock() {
    final int accountNum = 10;
    final Account[] accounts = new Account[accountNum];
    final Random rnd = new Random();
    for(int i = 0; i < accountNum; i++) {
        accounts[i] = new Account(rnd.nextInt(10000));
    }
    int threadNum = 100;
    Thread[] threads = new Thread[threadNum];
    for(int i = 0; i < threadNum; i++) {
        threads[i] = new Thread() {
            public void run() {
                int loopNum = 100;
                for(int k = 0; k < loopNum; k++) {
                    int i = rnd.nextInt(accountNum);
                    int j = rnd.nextInt(accountNum);
                    int money = rnd.nextInt(10);
                    if(i != j) {
                        try {
                            transfer(accounts[i], accounts[j], money);
                            System.out.println(i + "--->" + j + "转账成功:" + money);
                        } catch (NoEnoughMoneyException e) {
                        }
                    }
                }
            }
        };
        threads[i].start();
    }
}

public static void main(String[] args) {
    simulateDeadLock();
} 
```

以上代码创建了10个账户，100个线程，每个线程均循环100次，在循环中随机挑选两个账户进行转账。在程序运行多次之后你会发现如下图所示的情况，程序因为发生死锁陷入阻塞态，无法完整执行程序： 

![死锁.png-29.3kB](http://static.zybuluo.com/ZzzJoe/l251yubvz3bjjuv6263og892/%E6%AD%BB%E9%94%81.png)



接下来我们使用 tryLock 书写一个新的方法，代码如下所示：

```java
public static boolean tryTransfer(Account from, Account to, double money)
            throws NoEnoughMoneyException {
    if (from.tryLock()) {
        try {
            if (to.tryLock()) {
                try {
                    if (from.getMoney() >= money) {
                        from.reduce(money);
                        to.add(money);
                    } else {
                        throw new NoEnoughMoneyException();
                    }
                    return true;
                } finally {
                    to.unlock();
                }
            }
        } finally {
            from.unlock();
        }
    }
    return false;
} 
```

尝试获取账户的锁，如果两个锁都能获取成功，则返回 true，反之则返回 false。无论锁的获取状态如何，在方法体结束之后都会释放所有的锁。同时我们可以改造 transfer 方法来循环调用该方法以避免死锁情况的发生，其代码可以为：

```java
public static void transfer(Account from, Account to, double money)
            throws NoEnoughMoneyException {
    boolean success = false;
    do {
        success = tryTransfer(from, to, money);
        if (!success) {
            Thread.yield();
        }
    } while (!success);
}
```





 

版权声明：本文为博主原创文章，遵循[ CC 4.0 BY-SA ](http://creativecommons.org/licenses/by-sa/4.0/)版权协议，转载请附上原文出处链接和本声明。

本文链接：<https://blog.csdn.net/u011669700/article/details/80069097>