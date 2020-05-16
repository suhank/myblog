[TOC]



# 使用Callable和DeferredResult，提高服务性能

[官方文档](https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#mvc-ann-async)中说DeferredResult和Callable都是为了异步生成返回值提供基本的支持。简单来说就是一个请求进来，如果你使用了DeferredResult或者Callable，在没有得到返回数据之前，DispatcherServlet和所有Filter就会退出Servlet容器线程，但响应保持打开状态，一旦返回数据有了，这个DispatcherServlet就会被再次调用并且处理，以异步产生的方式，向请求端返回值。 
这么做的好处就是请求不会长时间占用服务连接池，提高服务器的吞吐量。

## Callable

Callable的实现比较简单，call（）方法的返回值就是服务端返回给请求端的数据。

```java
@GetMapping("/callable")
public Callable<String> testCallable() throws InterruptedException {
    log.info("主线程开始！");
    Callable<String> result = new Callable<String>() {

        @Override
        public String call() throws Exception {
            log.info("副线程开始！");
            Thread.sleep(1000);
            log.info("副线程结束！");
            return "SUCCESS";
        }

    };
    log.info("主线程结束！");
    return result;
} 
```

运行测试一下，请求端基本和普通请求一样，但是日志输出就有差别了。主线程完成后副线程才开始。

## DeferredResult

一旦启用了异步请求处理功能 ，控制器就可以将返回值包装在DeferredResult，控制器可以从不同的线程异步产生返回值。优点就是可以实现两个完全不相干的线程间的通信。

下面是我编写的测试用例，首先创建一个存储DeferredResult的实例

```java
@Component
public class DeferredResultEntity {

    /**
     * 后续监听器中用于判断
     */
    private boolean flag = false;

    /**
     * 用于存储DeferredResult
     */
    private DeferredResult<Object> result;

    public DeferredResult<Object> getResult() {
        return result;
    }

    public void setResult(DeferredResult<Object> result) {
        this.result = result;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

} 
```

写监听器，作用是产生另一个不相干的线程来给DeferredResult设置返回值。 
（你也可以使用spring中的任务来实现相同效果）

```java
@Component
public class DeferredResultListener implements ApplicationListener<ContextRefreshedEvent> {

    Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private DeferredResultEntity deferredResultEntity;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        new Thread(()->{
            int count = 0;
            System.out.println("监听器");
            while(true) {
                if(deferredResultEntity.isFlag()) {
                    log.info("线程"+Thread.currentThread().getName()+"开始");
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    deferredResultEntity.getResult().setResult("SUCCESS");
                    deferredResultEntity.setFlag(false);
                    log.info("线程"+Thread.currentThread().getName()+"结束");
                    break;
                }else {
                    log.info("第"+count+"检查");
                    count++;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    } 
```

代码中的deferredResultEntity.getResult().setResult(“SUCCESS”)，就是用来设置返回值。

最后编写controller了

```java
@GetMapping("/deferred")
public DeferredResult<Object> testDeferredResult() {
    log.info("主线程开始！");
    DeferredResult<Object> result = new DeferredResult<Object>();
    deferredResultEntity.setResult(result);
    deferredResultEntity.setFlag(true);
    log.info("主线程结束！");
    return result;
} 
```

最后测试一下，效果与Callable一样。

## 总结

从上述测试可以看出，Callable主要用来处理一些简单的逻辑，DeferredResult主要用于处理一些复杂逻辑。





https://blog.csdn.net/smollsnail/article/details/79164826