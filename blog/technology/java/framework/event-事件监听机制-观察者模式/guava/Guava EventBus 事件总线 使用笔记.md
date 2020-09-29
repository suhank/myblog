[TOC]



# Guava-EventBus 事件总线 使用笔记

## Guava EventBus 的作用

​    个人对EventBus的理解是： 它是一个事件（消息）发布订阅框架，在我们的应用中可以处理一些异步任务。先通过代码来看下它的简单用法：

```java
package com.gtt.eventBus;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


/**
 * <B>Description:</B> guava事件总线测试类 <br>
 * <B>Create on:</B> 2018/12/5 下午4:12 <br>
 *
 * @author xiangyu.ye
 * @version 1.0
 */
@Slf4j
public class GuavaEventbusTest {

    /**
     * <B>Description:</B> 同步事件 <br>
     * <B>Create on:</B> 2018/12/5 下午4:12 <br>
     *
     * @author xiangyu.ye
     */
    @Test
    public void eventBusTest() {
        EventBus eventBus = new EventBus();
        /**
         * 注册事件处理器
         */
        eventBus.register(new Object() {
            @Subscribe
            public void handleUserInfoChangeEvent(UserInfoChangeEvent userInfoChangeEvent) {
                log.info("处理用户信息变化事件：" + userInfoChangeEvent.getUserName());
            }

            @Subscribe
            public void handleUserInfoChangeEvent(BaseEventBusEvent userInfoChangeEvent) {
                log.info("所有事件的父类");
            }

        });
        eventBus.post(new UserInfoChangeEvent("apple"));
    }



    private void sleep(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    class BaseEventBusEvent {

    }

    class UserInfoChangeEvent extends BaseEventBusEvent {
        private String userName;

        public UserInfoChangeEvent(String userName) {
            this.userName = userName;
        }

        public String getUserName() {
            return userName;
        }
    }
}

```

输出：
处理用户信息变化事件：apple
所有事件的父类

## EventBus的基本用法是：

### 1.先创建EventBus对象

​        EventBus对象的构造函数如下:

```java
/**
 * 创建一个新的EventBus对象，默认名字为"default".
 */
public EventBus() {
  this("default");
}

/**
 * 使用给定的标识符创建一个EventBus对象，注意该标识符必须是合法的java标识符.         
 */
public EventBus(String identifier) {
  this(new LoggingSubscriberExceptionHandler(identifier));
}

/**
 * 通过参数指定的 SubscriberExceptionHandler 对象创建EventBus对象
 * 
 * @param subscriberExceptionHandler Handler for subscriber exceptions.
 * @since 16.0
 */
public EventBus(SubscriberExceptionHandler subscriberExceptionHandler) {
  this.subscriberExceptionHandler = checkNotNull(subscriberExceptionHandler);
}

/**
 * 处理subscribers处理事件时抛出的异常
 *
 * @since 16.0
 */
public interface SubscriberExceptionHandler {
  /**
   * Handles exceptions thrown by subscribers.
   */
  void handleException(Throwable exception, SubscriberExceptionContext context);
}
```

### 2.注册事件处理器

```java
/**
 * 注册参数object指定的订阅者所有的方法来处理事件
 * EventBus通过SubscriberFindingStrategy类的实例来查找订阅者的事件处理方法；
 * 默认的策略类是AnnotatedSubscriberFinder
 */
public void register(Object object) {
  Multimap<Class<?>, EventSubscriber> methodsInListener =
      finder.findAllSubscribers(object);
  subscribersByTypeLock.writeLock().lock();
  try {
    subscribersByType.putAll(methodsInListener);
  } finally {
    subscribersByTypeLock.writeLock().unlock();
  }
}
```

### 3.发送事件

```java
/**
 * 发送一个事件给所有注册的订阅者.  该方法会在该事件发送给所用的订阅者后成功返回，除非
 任何订阅者抛出任何异常。
 *如果没有任何注册的订阅者来处理该事件，该事件会被包装为一个DeadEvent来重新发送
 */
public void post(Object event) {
    ...
}
```

## **EventBus的事件对象继承问题**

​        EventBus中的事件可以是任意类型的，事件分发的时候只需要根据订阅参数类型来分发消息，如果编码中，多个订阅事件类型上存在类型继承的关系，则当前的事件会分发到多个不同的订阅者上，这一点大家在使用的时候可能要仔细处理，在不需要重复处理的消息上就要做好细节处理了。

## **EventBus的并发问题**

Guava EventBus中默认订阅方法为线程不安全的，在异步调度时会自动将其包装成线程安全的方法。对于一般线程安全的实现上，可以通过@AllowConcurrentEvents注解来标识。

```java
/**
 * 该注解标识了订阅者的事件处理方法是线程安全的。告诉EventBus该方法可以在多线程环境下同   步调用
 * 该注解必须是注解@Subscribe同时使用
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Beta
public @interface AllowConcurrentEvents {
}
```

## **EventBus的DeadEvent问题**

 当EventBus发布了一个事件，但是注册的订阅者中没有找到处理该事件的方法，那么EventBus就会把该事件包装成一个DeadEvent事件来重新发布；我们在应用中可以提供如下的事件处理方法来处理DeadEvent。

```java
 @Subscribe
 public void onEvent(DeadEvent de) {
     logger.info("发布了错误的事件:" + de.getEvent());
 }
```


异步EventBus-AsyncEventBus   

上面我们说的都是同步的事件处理，但在我们的应用场景中可能需要异步来处理事件，这时异步EventBus --》 AsyncEventBus 就派上用场了；上代码


```java
/**
 * 异步的EventBus
 */
public static void testAsyncEventBus(){
    Executor executor = Executors.newFixedThreadPool(10);
    AsyncEventBus asyncEventBus = new AsyncEventBus("asyncEventBus", executor);
    /**
     * 注册事件处理器
     */
    asyncEventBus.register(new Object(){
        @Subscribe
        public void handleUserInfoChangeEvent(UserInfoChangeEvent userInfoChangeEvent){
            System.out.println("处理用户信息变化事件：" + userInfoChangeEvent.getUserName());
        }

        @Subscribe
        public void handleUserInfoChangeEvent(BaseEventBusEvent userInfoChangeEvent){
            System.out.println("所有事件的父类");
        }

    });
    asyncEventBus.post(new UserInfoChangeEvent("apple"));
    System.out.println("异步EventBus");
}
```

异步EventBus 使用一个Executor来异步执行订阅者处理事件的方法。这样不会因为事件处理代码执行缓慢而导致调用线程阻塞。





## 完整代码

```java
package com.gtt.eventBus;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


/**
 * <B>Description:</B> guava事件总线测试类 <br>
 * <B>Create on:</B> 2018/12/5 下午4:12 <br>
 *
 * @author xiangyu.ye
 * @version 1.0
 */
@Slf4j
public class GuavaEventbusTest {

    /**
     * <B>Description:</B> 同步事件 <br>
     * <B>Create on:</B> 2018/12/5 下午4:12 <br>
     *
     * @author xiangyu.ye
     */
    @Test
    public void eventBusTest() {
        EventBus eventBus = new EventBus();
        /**
         * 注册事件处理器
         */
        eventBus.register(new Object() {
            @Subscribe
            public void handleUserInfoChangeEvent(UserInfoChangeEvent userInfoChangeEvent) {
                log.info("处理用户信息变化事件：" + userInfoChangeEvent.getUserName());
            }

            @Subscribe
            public void handleUserInfoChangeEvent(BaseEventBusEvent userInfoChangeEvent) {
                log.info("所有事件的父类");
            }

        });
        eventBus.post(new UserInfoChangeEvent("apple"));
    }

    /**
     * <B>Description:</B> 异步事件 <br>
     * <B>Create on:</B> 2018/12/5 下午4:12 <br>
     *
     * @author xiangyu.ye
     */
    @Test
    public void asyncEventBusTest() {
        Executor executor = Executors.newFixedThreadPool(10);
        AsyncEventBus asyncEventBus = new AsyncEventBus("asyncEventBus", executor);
        /**
         * 注册事件处理器
         */
        asyncEventBus.register(new Object() {
            @Subscribe
            public void handleUserInfoChangeEvent(UserInfoChangeEvent userInfoChangeEvent) {
                sleep(1000);
                log.info("处理用户信息变化事件：" + userInfoChangeEvent.getUserName());
            }

            @Subscribe
            public void handleUserInfoChangeEvent(BaseEventBusEvent userInfoChangeEvent) {
                log.info("所有事件的父类");
            }

        });
        asyncEventBus.post(new UserInfoChangeEvent("apple"));
        log.info("异步EventBus");


        sleep(10000);
    }

    private void sleep(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    class BaseEventBusEvent {

    }

    class UserInfoChangeEvent extends BaseEventBusEvent {
        private String userName;

        public UserInfoChangeEvent(String userName) {
            this.userName = userName;
        }

        public String getUserName() {
            return userName;
        }
    }
}

```

http://blog.51cto.com/leokongwq/1703763