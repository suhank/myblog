# 线上问题解决：java的CPU100%问题分析，定位及解决

2019-12-02 10:16:16

一起探讨下，线上问题的处理思路。

## 问题合集

### ① 请求一个API接口返回json数据，慢请求

> 发送请求后，返回非常的慢。之前很快，突然变慢了。如何去分析，在公司经常出来问题，这个代码可能都不是你开发的。

1. 测试工具模拟多个用户请求。
2. jcmd查看哪些程序在运行PID。
3. jstack PID
4. 对于慢查询，线程的快照中可能残留，线程执行的内存。执行的栈，调用链路，很久没有执行完，这个线程执行需要一定的时间，如果查看到多个代码段执行的频次比较高，这些代码就比较可疑。线程执行的时候有个时间跨度，这个时间跨度，也比较可疑，也就是执行一段代码的时间比较长。
5. 找到对应的package包，找到指定的行。
6. 感觉网络有问题，可以通过ping，或者curl请求下指定接口，看看反应时间。

> 有老铁活可以通过 JvisualVM 查看，但是生产环境一般都不让开发人员直接操作的，中间可能存在跳板机。可以通过windows的跳板机来查看JVM的情况。

### ② 死锁的情况

> 示例

```java
// 死锁
public class Cpu100Demo2 {
    public static String obj1 = "obj1";
    public static String obj2 = "obj2";

    public static void main(String[] args) {
        // 处理用户请求时，出现了死锁。用户无响应，多次重试，大量资源被占用（）
        Thread a = new Thread(new Lock1());
        Thread b = new Thread(new Lock2());
        a.start();
        b.start();
    }
}

class Lock1 implements Runnable {
    @Override
    public void run() {
        try {
            System.out.println("Lock1 running");
            while (true) {
                synchronized (Cpu100Demo2.obj1) {
                    System.out.println("Lock1 lock obj1");
                    Thread.sleep(3000);//获取obj1后先等一会儿，让Lock2有足够的时间锁住obj2
                    synchronized (Cpu100Demo2.obj2) {
                        System.out.println("Lock1 lock obj2");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class Lock2 implements Runnable {
    @Override
    public void run() {
        try {
            System.out.println("Lock2 running");
            while (true) {
                synchronized (Cpu100Demo2.obj2) {
                    System.out.println("Lock2 lock obj2");
                    Thread.sleep(3000);
                    synchronized (Cpu100Demo2.obj1) {
                        System.out.println("Lock2 lock obj1");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

![线上问题解决：java的CPU100%问题分析，定位及解决](线上问题解决：java的CPU100%问题分析，定位及解决.assets/4d79b5ffd5ac438eba54ad0f8ebbf8bf)



![线上问题解决：java的CPU100%问题分析，定位及解决](线上问题解决：java的CPU100%问题分析，定位及解决.assets/8820e08c8b1f4202a44f1dd482c3c41e)



![线上问题解决：java的CPU100%问题分析，定位及解决](线上问题解决：java的CPU100%问题分析，定位及解决.assets/6484aa12634b4e94bf4bc08c829341fd)



> 如何分析，比第一个例子相对简单，其实打印的tack已经展示了问题。

### ③ 多线程wait

> 示例

```java
// 活锁
public class Cpu100Demo3 {
    /**
     * 包子店
     */
    public static Object baozidian = null;

    /**
     * 会导致程序永久等待的wait/notify
     */
    public void waitNotifyDeadLockTest() throws Exception {
        // 启动消费者线程
        new Thread(() -> {
            if (baozidian == null) { // 如果没包子，则进入等待
                try {
                    Thread.sleep(5000L);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                synchronized (this) {
                    try {
                        System.out.println("1、进入等待，线程ID为： " + Thread.currentThread().getId());
                        this.wait(); // 多次查看
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            System.out.println("2、买到包子，回家");
        }).start();
        // 3秒之后，生产一个包子
        Thread.sleep(3000L);
        baozidian = new Object();
        synchronized (this) {
            this.notifyAll();
            System.out.println("3、通知消费者");
        }
    }

    public static void main(String[] args) throws Exception {
        new Cpu100Demo3().waitNotifyDeadLockTest();
    }
}
```

![线上问题解决：java的CPU100%问题分析，定位及解决](线上问题解决：java的CPU100%问题分析，定位及解决.assets/3cece5b037384cda8e9f429cd74447fc)



![线上问题解决：java的CPU100%问题分析，定位及解决](线上问题解决：java的CPU100%问题分析，定位及解决.assets/e2d4f81df47b43839e98469f247e2b75)



![线上问题解决：java的CPU100%问题分析，定位及解决](线上问题解决：java的CPU100%问题分析，定位及解决.assets/01de898e36ad449c98063e76768c05ac)



### ④ 线程过多

> 一个系统很多的功能，其实在运行的过程中，热点代码不是特别多的，线程过多的情况，一般是机器配置不行，解决不了大量的过来的请求导致的。
> 查看CPU情况，网络连接数情况，在上线之初，4核8G容纳500个并发没有问题。先看网络连接，这就是基本所有互联网大型公司都有网络监控这块，

```
netstat -nat | grep -i '8080' | wc -l
```

> 示例

```java
import java.util.Random;

// 线程过多导致的问题(jstack定位)
public class Cpu100Demo4 {
    // 资源：每一个请求,业务执行需要占用多少资源，CPU * 1--> 增加资源。
    // 线程池，控制线程数量，升级更高的配置
    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 1000; i++) {
            new Thread(() -> {
                try {
                    int x = 0;
                    for (int j = 0; j < 10000; j++) {
                        x = x + 1;
                        long random = new Random().nextInt(100);
                        Thread.sleep(random); // 模拟c处理耗时
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
            long random = new Random().nextInt(500);
            Thread.sleep(random); // 模拟接口调用
        }
    }
}
```

### ⑤ CPU100%

1. jcmd找到对应的进程PID。
2. top -H -p PID 获取到占用CPU最大的那个线程ID。
3. 通过window计算器切换成开发者模式 将线程ID 转成16进制的数字 转换后就是 【0x数字】。
4. jstack 进程PID > a.log。
5. 搜索a.log中的 nid=【0x数字】就可以定位到代码的位置了。

```
prinf "%x\n" 26144
#5ae1
```

> 由于单个线程产生死循环的其实很少的，除非你使用了第三方的工具，一般使用了第三方工具的就会产生死循环，否则很难开发人员自杀式的写成直接死循环。掌握上边的使用技巧就可以了。

> 示例

```java
import java.util.Random;

// 个别线程占用资源过多

public class Cpu100Demo5 {
    public static void main(String[] args) throws InterruptedException {
        new Thread(() -> {
            while (true) {
                new Random().nextInt(100);
            }
        }, "CPU-high").start();
        for (int i = 0; i < 1000; i++) {
            new Thread(() -> {
                try {
                    int x = 0;
                    for (int j = 0; j < 10000; j++) {
                        x = x + 1;
                        long random = new Random().nextInt(100);
                        Thread.sleep(random); // 模拟c处理耗时
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
            long random = new Random().nextInt(500);
            Thread.sleep(random); // 模拟接口调用
        }
    }
}
```

https://www.ixigua.com/6936509581828817446?logTag=VDbr30sQv7f-hp__GFUWA

### ⑥ CPU升高

> CPU升高，任务处理不过来了，肯定会堆积，堆积的结果，内存也会升高，这是一个相辅相成的。不可能单独抛开一方面不去管，内存和CPU还是有关联关系的。

## 线上处理问题

1. CPU
2. 内存
3. 网络
4. 系统日志 tail - f /var/log/messages 很多东西都可以变成系统日志反应出来。
5. 很多买人买的云服务器，内存比较低，可能java进程突然就消失了，其实就是linux本身有个机制，超过内存值的时候就会kill。系统内存块耗尽的时候干掉一些进程
6. 日志文件定时的清理：log4j file，一定要定义规则什么时候删除文件。这个东西真的很致命，一旦满了，程序很容易写满。

PS：一般的生产服务器CPU稳定在80-85以内，不会让资源利用率太高，也不会太低，资源利用率很高的话，留一些剩余的空间，证明你的机器买了那么多可能就是浪费，CPU和内存都是一样的。在高并发的情况，一般都是需要提前做优化，做测试的，往往有时候大家的一些编码习惯导致的出其不意的问题。网络突然慢了，请求慢了，都可以按照这个思路来定位问题。