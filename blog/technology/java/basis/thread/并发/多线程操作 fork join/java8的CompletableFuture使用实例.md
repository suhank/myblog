# java8的CompletableFuture使用实例

## thenApply(`等待并转化future`)

```
    @Test
    public void testThen() throws ExecutionException, InterruptedException {
        CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> {
            return "zero";
        }, executor);

        CompletableFuture<Integer> f2 = f1.thenApply(new Function<String, Integer>() {

            @Override
            public Integer apply(String t) {
                System.out.println(2);
                return Integer.valueOf(t.length());
            }
        });

        CompletableFuture<Double> f3 = f2.thenApply(r -> r * 2.0);
        System.out.println(f3.get());
    }
```

## thenAccept与thenRun(`监听future完成`)

```
/**
     * future完成处理,可获取结果
     */
    @Test
    public void testThenAccept(){
        CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> {
            return "zero";
        }, executor);
        f1.thenAccept(e -> {
            System.out.println("get result:"+e);
        });
    }

    /**
     * future完成处理
     */
    @Test
    public void testThenRun(){
        CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> {
            return "zero";
        }, executor);
        f1.thenRun(new Runnable() {
            @Override
            public void run() {
                System.out.println("finished");
            }
        });
    }
```

## thenCompose(`flatMap future`)

```java
/**
     * compose相当于flatMap,避免CompletableFuture<CompletableFuture<String>>这种
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    public void testThenCompose() throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> {
            return "zero";
        }, executor);
        CompletableFuture<CompletableFuture<String>> f4 = f1.thenApply(CompletableFutureTest::calculate);
        System.out.println("f4.get:"+f4.get().get());

        CompletableFuture<String> f5 = f1.thenCompose(CompletableFutureTest::calculate);
        System.out.println("f5.get:"+f5.get());

        System.out.println(f1.get());
    }

    public static CompletableFuture<String> calculate(String input) {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            System.out.println(input);
            return input + "---" + input.length();
        }, executor);
        return future;
    }
```

## thenCombine与thenAcceptBoth

- thenCombine(`组合两个future，有返回值`)

```java
/**
     * thenCombine用于组合两个并发的任务,产生新的future有返回值
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    public void testThenCombine() throws ExecutionException, InterruptedException {
        CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> {
            try {
                System.out.println("f1 start to sleep at:"+System.currentTimeMillis());
                Thread.sleep(1000);
                System.out.println("f1 finish sleep at:"+System.currentTimeMillis());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "zero";
        }, executor);
        CompletableFuture<String> f2 = CompletableFuture.supplyAsync(() -> {
            try {
                System.out.println("f2 start to sleep at:"+System.currentTimeMillis());
                Thread.sleep(3000);
                System.out.println("f2 finish sleep at:"+System.currentTimeMillis());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "hello";
        }, executor);

        CompletableFuture<String> reslutFuture =
                f1.thenCombine(f2, new BiFunction<String, String, String>() {

                    @Override
                    public String apply(String t, String u) {
                        System.out.println("f3 start to combine at:"+System.currentTimeMillis());
                        return t.concat(u);
                    }
                });

        System.out.println(reslutFuture.get());//zerohello
        System.out.println("finish combine at:"+System.currentTimeMillis());
    }
```

- thenAcceptBoth(`组合两个future,没有返回值`)

```java
/**
     * thenAcceptBoth用于组合两个并发的任务,产生新的future没有返回值
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    public void testThenAcceptBoth() throws ExecutionException, InterruptedException {
        CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> {
            try {
                System.out.println("f1 start to sleep at:"+System.currentTimeMillis());
                TimeUnit.SECONDS.sleep(1);
                System.out.println("f1 stop sleep at:"+System.currentTimeMillis());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return "zero";
        }, executor);
        CompletableFuture<String> f2 = CompletableFuture.supplyAsync(() -> {
            try {
                System.out.println("f2 start to sleep at:"+System.currentTimeMillis());
                TimeUnit.SECONDS.sleep(3);
                System.out.println("f2 stop sleep at:"+System.currentTimeMillis());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return "hello";
        }, executor);

        CompletableFuture<Void> reslutFuture = f1.thenAcceptBoth(f2, new BiConsumer<String, String>() {
            @Override
            public void accept(String t, String u) {
                System.out.println("f3 start to accept at:"+System.currentTimeMillis());
                System.out.println(t + " over");
                System.out.println(u + " over");
            }
        });

        System.out.println(reslutFuture.get());
        System.out.println("finish accept at:"+System.currentTimeMillis());

    }
```

## applyToEither与acceptEither

- applyToEither(`取2个future中最先返回的,有返回值`)

```
/**
     * 当任意一个CompletionStage 完成的时候，fn 会被执行,它的返回值会当做新的CompletableFuture<U>的计算结果
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    public void testApplyToEither() throws ExecutionException, InterruptedException {
        CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> {
            try {
                System.out.println("f1 start to sleep at:"+System.currentTimeMillis());
                TimeUnit.SECONDS.sleep(5);
                System.out.println("f1 stop sleep at:"+System.currentTimeMillis());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return "fromF1";
        }, executor);
        CompletableFuture<String> f2 = CompletableFuture.supplyAsync(() -> {
            try {
                System.out.println("f2 start to sleep at:"+System.currentTimeMillis());
                TimeUnit.SECONDS.sleep(2);
                System.out.println("f2 stop sleep at:"+System.currentTimeMillis());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return "fromF2";
        }, executor);

        CompletableFuture<String> reslutFuture = f1.applyToEither(f2,i -> i.toString());
        System.out.println(reslutFuture.get()); //should not be null , wait for complete
    }
```

- acceptEither(`取2个future中最先返回的,无返回值`)

```
/**
    * 取其中返回最快的一个
     * 当任意一个CompletionStage 完成的时候，action 这个消费者就会被执行。这个方法返回 CompletableFuture<Void>
     */
    @Test
    public void testAcceptEither() throws ExecutionException, InterruptedException {
        CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> {
            try {
                System.out.println("f1 start to sleep at:"+System.currentTimeMillis());
                TimeUnit.SECONDS.sleep(3);
                System.out.println("f1 stop sleep at:"+System.currentTimeMillis());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return "zero";
        }, executor);
        CompletableFuture<String> f2 = CompletableFuture.supplyAsync(() -> {
            try {
                System.out.println("f2 start to sleep at:"+System.currentTimeMillis());
                TimeUnit.SECONDS.sleep(5);
                System.out.println("f2 stop sleep at:"+System.currentTimeMillis());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return "hello";
        }, executor);

        CompletableFuture<Void> reslutFuture = f1.acceptEither(f2,r -> {
            System.out.println("quicker result:"+r);
        });
        reslutFuture.get(); //should be null , wait for complete

    }
```

## allOf与anyOf

- allOf(`等待所有future返回`)

```
    /**
     * 等待多个future返回
     */
    @Test
    public void testAllOf() throws InterruptedException {
        List<CompletableFuture<String>> futures = IntStream.range(1,10)
                .mapToObj(i ->
                        longCost(i)).collect(Collectors.toList());
        final CompletableFuture<Void> allCompleted = CompletableFuture.allOf(futures.toArray(new CompletableFuture[]{}));
        allCompleted.thenRun(() -> {
            futures.stream().forEach(future -> {
                try {
                    System.out.println("get future at:"+System.currentTimeMillis()+", result:"+future.get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            });

        });
        Thread.sleep(100000); //wait
    }
```

- anyOf(`取多个future当中最快的一个返回`)

```
/**
     * 等待多个future当中最快的一个返回
     * @throws InterruptedException
     */
    @Test
    public void testAnyOf() throws InterruptedException {
        List<CompletableFuture<String>> futures = IntStream.range(1,10)
                .mapToObj(i ->
                        longCost(i)).collect(Collectors.toList());
        final CompletableFuture<Object> firstCompleted = CompletableFuture.anyOf(futures.toArray(new CompletableFuture[]{}));
        firstCompleted.thenAccept((Object result) -> {
            System.out.println("get at:"+System.currentTimeMillis()+",first result:"+result);
        });
    }

    private CompletableFuture<String> longCost(long i){
        return CompletableFuture.supplyAsync(() -> {
            try {
                System.out.println("f"+i+" start to sleep at:"+System.currentTimeMillis());
                Thread.sleep(3000);
                System.out.println("f"+i+" stop sleep at:"+System.currentTimeMillis());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return String.valueOf(i);
        },executor);
    }
```

## doc

- [CompletableFuture（二）](https://link.jianshu.com/?t=http://blog.csdn.net/zero__007/article/details/50573562#)
- [Java CompletableFuture 详解](https://link.jianshu.com/?t=http://colobu.com/2016/02/29/Java-CompletableFuture/)
- [Java 8：CompletableFuture终极指南](https://link.jianshu.com/?t=http://www.importnew.com/10815.html)
- [Java 8: Definitive guide to CompletableFuture](https://link.jianshu.com/?t=https://www.javacodegeeks.com/2013/05/java-8-definitive-guide-to-completablefuture.html)
- [并发编程 | JDK 1.8中的CompletableFuture | FRP风格](https://link.jianshu.com/?t=http://www.sczyh30.com/posts/Java/java-8-completable-future/)