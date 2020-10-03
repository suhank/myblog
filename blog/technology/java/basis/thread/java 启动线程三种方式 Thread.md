# java 启动线程三种方式

## 代码

```java
package com.practice.thread;

import com.google.common.collect.Lists;
import org.junit.Test;
import sun.jvm.hotspot.runtime.JavaThread;

/**
 * java 启动线程三种方式
 */
public class ThreadTest {

    //1.继承Thread
    @Test
    public void extendThread() throws InterruptedException {
        new JavaThread().run();
        System.out.println("main thread run ");
    }

    //2.实现Runnable接口
    @Test
    public void implementTheRunnableInterface() throws InterruptedException {
        new Thread(new JavaThreadRunnable("JavaThreadRunnable")).start();
        System.out.println("main thread run ");
    }

    //3.直接在函数体使用
    @Test
    public void useDirectlyInFunctionBody() throws InterruptedException {
        Thread t = new Thread(new Runnable() {
            public void run() {
                System.out.println("sub thread run ");
            }
        });
        t.start();
        System.out.println("main thread run ");
    }


    static class JavaThread extends Thread {
        public void run() {
            System.out.println("sub thread run ");
        }
    }

    static class JavaThreadRunnable implements Runnable {
        private String name;

        public JavaThreadRunnable(String name) {
            this.name = name;
        }

        public void run() {
            System.out.println("sub " + name + " thread run ");
        }
    }
}

```




## 比较：

**实现Runnable接口优势：**

1）适合多个相同的程序代码的线程去处理同一个资源

2）可以避免java中的单继承的限制

3）增加程序的健壮性，代码可以被多个线程共享，代码和数据独立。

**继承Thread类优势：**

1）可以将线程类抽象出来，当需要使用抽象工厂模式设计时。

2）多线程同步

**在函数体使用优势**

1）无需继承thread或者实现Runnable，缩小作用域。