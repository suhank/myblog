# 基于guava的重试组件Guava-Retryer

/Users/jerryye/backup/studio/AvailableCode/basis/guava/guava_demo/src/main/java/com/gtt/retrying/RetryingDemo.java

在日常开发中，我们经常会遇到需要调用外部服务和接口的场景。外部服务对于调用者来说一般都是不可靠的，尤其是在网络环境比较差的情况下，网络抖动很容易导致请求超时等异常情况，这时候就需要使用失败重试策略重新调用 API 接口来获取。重试策略在服务治理方面也有很广泛的使用，通过定时检测，来查看服务是否存活。

Guava Retrying 是一个灵活方便的重试组件，包含了多种的重试策略，而且扩展起来非常容易。

使用 Guava-retrying 你可以自定义来执行重试，同时也可以监控每次重试的结果和行为，最重要的基于 Guava 风格的重试方式真的很方便。

# 代码示例

以下会简单列出 guava-retrying 的使用方式：

```java

/**
 * <B>Description:</B> 如果抛出 IOException 则重试，如果返回结果为 null 或者等于 2 则重试，固定等待时长为 3000 ms,最多尝试 3 次； <br>
 * <B>Create on:</B> 2018/4/6 下午4:14 <br>
 *
 * @author xiangyu.ye
 */
public class RetryingDemo {
    public static void main(String[] args) {
        Callable<Integer> task = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                System.out.println(Thread.currentThread().getName()+"====zhixingle ..");
                return 2;
            }
        };
        Retryer<Integer> retryer = RetryerBuilder.<Integer> newBuilder()
            .retryIfResult(Predicates.<Integer> isNull()).retryIfResult(Predicates.equalTo(2))
            .retryIfExceptionOfType(IOException.class)
            .withStopStrategy(StopStrategies.stopAfterAttempt(3))
            .withWaitStrategy(WaitStrategies.fixedWait(3000, TimeUnit.MILLISECONDS)).build();
        try {
            retryer.call(task);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (RetryException e) {
            e.printStackTrace();
        }
    }
}

```

结果:

```
main====zhixingle ..
main====zhixingle ..
main====zhixingle ..
com.github.rholder.retry.RetryException: Retrying failed to complete successfully after 3 attempts.
	at com.github.rholder.retry.Retryer.call(Retryer.java:174)
	at com.gtt.retrying.RetryingDemo.main(RetryingDemo.java:37)

```



# 依赖引入

```
 		<dependency>
            <groupId>com.github.rholder</groupId>
            <artifactId>guava-retrying</artifactId>
            <version>2.0.0</version>
        </dependency>
```



https://www.toutiao.com/a6540516242883609096/?tt_from=android_share&utm_campaign=client_share&timestamp=1522860739&app=news_article&iid=28537493856&utm_medium=toutiao_android