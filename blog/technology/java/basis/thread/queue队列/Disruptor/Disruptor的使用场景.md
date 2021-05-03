# Disruptor的使用场景

2018-08-15 14:30:39

## 前言

Disruptor是什么，怎么使用，网上有很多教材，但有些过于复杂，剖析了Disruptor的方方面面，实际上对应普通的开发人员，使用这个工具，只需要指导知道大概原理和使用方法，并不需要知道非常深入的原理。

有些文章则是写了错误的实例，或者只是摘取了项目的一段代码，实际上，要把这些代码转化成项目的实际代码，却发现困难重重。

这个文章主要是针对想提高性能，在项目组使用Disruptor的开发人员写的，会简单讲解它的一些原理，尽量把代码简单，但又包括项目使用中必须的方方面面。

##  Disruptor的使用场景

一个字，就是快，经过测试，Disruptor的速度比LinkedBlockingQueue提高了七倍。所以，当你在使用LinkedBlockingQueue出现性能瓶颈的时候，你就可以考虑采用Disruptor的代替。

当然，Disruptor性能高并不是必然的，所以，是否使用还得经过测试。

Disruptor的最常用的场景就是“生产者-消费者”场景，对场景的就是“一个生产者、多个消费者”的场景，并且要求顺序处理。

 

举个例子，我们从MySQL的BigLog文件中顺序读取数据，然后写入到ElasticSearch（搜索引擎）中。在这种场景下，BigLog要求一个文件一个生产者，那个是一个生产者。而写入到ElasticSearch，则严格要求顺序，否则会出现问题，所以通常意义上的多消费者线程无法解决该问题，如果通过加锁，则性能大打折扣。

##  一个生产者，多消费者例子

​     在贴出代码之前，这里简单讲一下代码的结构和说明。简单的图示如下，先有Producer来作为生产者，发送事件。由EventHandler作为消费者，处理事件。

 

​     这个实例代码很简单，就是由生产者发送一个字符串到Disruptor，然后消费者处理该事件，并把字符串打印处理。

​     下面是各个Java类的说明：

HelloEventProducer：生产者，负责传递一个字符串，并发布事件

HelloEventHandler：消费者，负责消费事件，并打印字符串

HelloEventFactory：事件工厂类，负责初始化一个事件

HelloEvent：表示一个事件

DisruptorMain：运行的主程序，负责将整个逻辑连接起来

```
package com.disruptor;
 
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
 
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.www.quyingyulecs.com EventHandler;
import com.lmax.disruptor.www.feifanyule188.cn RingBuffer;
import com.lmax.disruptor.www.qiantu178.com WaitStrategy;
import com.lmax.disruptor.www.zhengxianzaixian.cn  YieldingWaitStrategy;
import com.lmax.disruptor.www.thd178.com dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
 
public class DisruptorMain {
    public static void main(String[] args){
        ExecutorService executor = Executors.newFixedThreadPool(3);
        
//        WaitStrategy blockingWaitStrategy = new BlockingWaitStrategy();
//        WaitStrategy sleepingWaitStrategy = new SleepingWaitStrategy();
        WaitStrategy yieldingWaitStrategy = new YieldingWaitStrategy();
        
        EventFactory<HelloEvent> eventFactory = new HelloEventFactory();
        
        int ringBufferSize = 1024 * 1024;
        
        Disruptor<HelloEvent> disruptor = new Disruptor<HelloEvent>(eventFactory,
                        ringBufferSize, executor, ProducerType.SINGLE
                        , yieldingWaitStrategy);
        
        EventHandler<HelloEvent> eventHandler = new HelloEventHandler();
        
        disruptor.handleEventsWith(eventHandler);
        
        disruptor.start();
        
        RingBuffer<HelloEvent> ringBuffer = disruptor.getRingBuffer();
        
        HelloEventProducer producer = new HelloEventProducer(ringBuffer);
        
        for(long l = 0; l<100; l++){
            producer.onData("黄育源：Hello World！！！:" + l);
        }
    }
}
```



```
package com.disruptor;
 
public class HelloEvent {
    private String value;
 
    public String getValue() {
        return value;
    }
 
    public void setValue(String value) {
        this.value = value;
    }
}
```



```
package com.disruptor;
 
import com.lmax.disruptor.EventFactory;
 
public class HelloEventFactory implements EventFactory<HelloEvent>{
 
    @Override
    public HelloEvent newInstance() {
        return new HelloEvent();
    }
    
}
```

```
package com.disruptor;
 
import com.lmax.disruptor.EventHandler;
 
public class HelloEventHandler implements EventHandler<HelloEvent>{
 
    @Override
    public void onEvent(HelloEvent event, long sequence, boolean endOfBatch) throws Exception {
        System.out.println(event.getValue());
    }
 
}
```

```
package com.disruptor;
 
import com.lmax.disruptor.RingBuffer;
 
public class HelloEventProducer implements Runnable{
private final RingBuffer<HelloEvent> ringBuffer;
    
    public HelloEventProducer(RingBuffer<HelloEvent> ringBuffer){
        this.ringBuffer = ringBuffer;
    }
    
    /**
     * onData用来发布事件，每调用一次就发布一次事件
     * 它的参数会用过事件传递给消费者
     */
    public void onData(String str){
        long sequence = ringBuffer.next();
        System.out.println(sequence);
        try{
            HelloEvent event = ringBuffer.get(sequence);
            
            event.setValue(str);
        }finally{
            ringBuffer.publish(sequence);
        }
    }
 
    @Override
    public void run() {
        for(long l = 0; l<100; l++){
            this.onData("黄育源：Hello World！！！:" + l);
        }
    }
```





[——Disruptor的使用场景_li123128的博客-CSDN博客_disruptor使用场景](https://blog.csdn.net/li123128/article/details/81703618)