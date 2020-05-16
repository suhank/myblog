[TOC]







# Spirng Cache(第二篇)自定义配置

2018年12月09日 21:59:14 [孙平平](https://me.csdn.net/sun_shaoping) 阅读数 205 

从3.1版开始，Spring Framework提供了对现有Spring应用程序**透明**地添加缓存的支持。与[事务](https://docs.spring.io/spring/docs/5.0.11.RELEASE/spring-framework-reference/data-access.html#transaction) 支持类似，缓存抽象允许一致地使用各种缓存解决方案，而对代码的**影响最小**。

从Spring 4.1开始，通过[JSR-107注释](https://docs.spring.io/spring/docs/5.0.11.RELEASE/spring-framework-reference/integration.html#cache-jsr-107)和更多自定义选项的支持，缓存抽象得到了显着改进。

这篇介绍下如果根据Spring 缓存注解实现我们的自定义配置



 

## 自定义缓存key KeyGenerator

当Spel表达式(90%情况下都能满足)不能满足我们需求是可以使用自定义缓存key来实现，只需指定`KeyGenerator`接口的实现类的bean名称

比如：key值添加后缀(Spel表达式也可以实现，这里只为演示)

```java
@Cacheable(keyGenerator = "myKeyGenerator")
public Book findBookKeyGenerator(String isbn) {
    return createBook(isbn);
}
1234
```

KeyGenerator实现类，添加后缀

```java
@Component
public class MyKeyGenerator implements KeyGenerator {
    @Override
    public Object generate(Object target, Method method, Object... params) {
        String key = params[0] + "-MyKeyGenerator";
        System.out.println(key);
        return key;
    }
}

12345678910
```

单元测试

```java
@Test
public void findBookKeyGenerator() {
    Book book1 = bookService.findBook(ISBN);
    Book book2 = bookService.findBookKeyGenerator(ISBN);
    assert book1 != book2;
}
123456
```

## 自定义缓存管理 CacheManager

这个缓存管理器只打印一句话，没有实现任何逻辑。具体的缓存委派给`cacheManager`

```java
public class MyCacheManager implements CacheManager {

    private final CacheManager cacheManager;

    public MyCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    public Cache getCache(String name) {
        System.out.println("自定义缓存管理器，name=" + name);
        return cacheManager.getCache(name);
    }

    @Override
    public Collection<String> getCacheNames() {
        return cacheManager.getCacheNames();
    }
}
12345678910111213141516171819
```

缓存配置，一个是我们自定义的缓存管理器，一个是默认缓存管理器

**注意**：如果这里没有配置默认缓存管理器，我们自定义的管理器将覆盖默认的缓存管理器。

```java
@Configurable
public class CacheConfiguration {
    /**
     * 默认缓存管理器
     */
    @Bean
    @Primary
    public ConcurrentMapCacheManager concurrentMapCacheManager() {
        return new ConcurrentMapCacheManager();
    }
    /**
     * 自定义缓存管理器
     */
    @Bean
    public MyCacheManager myCacheManager() {
        return new MyCacheManager(new ConcurrentMapCacheManager());
    }
}
123456789101112131415161718
```

使用自定义缓存管理器

```java
@Cacheable(keyGenerator = "myKeyGenerator")
public Book findBookKeyGenerator(String isbn) {
    return createBook(isbn);
}
1234
```

单元测试，`book1`和`book2`是使用两个管理器，所以它们不相等。

```java
@Test
public void findBookCacheManager() {
    Book book1 = bookService.findBook(ISBN);
    Book book2 = bookService.findBookCacheManager(ISBN);
    assert book1 != book2;
}
123456
```

## 自定义缓存解析器 CacheResolver

使用`CacheResolver`实现缓存失效，在配置文件(`application.yml`)中指定缓存名称失效。

配置books失效

```yaml
spring:
  cache:
    no-op-cache:
      list: books
1234
```

实现类，使缓存失效配置起作用

```java
@Component
@ConfigurationProperties("spring.cache.no-op-cache")
public class MyCacheResolver extends SimpleCacheResolver implements CacheResolver {


    private final Map<String, NoOpCache> noOpCacheMap = new HashMap<>();

    @Override
    public Collection<? extends Cache> resolveCaches(CacheOperationInvocationContext<?> context) {
        Set<String> cacheNames = context.getOperation().getCacheNames();
        for (String cacheName : cacheNames) {
            if (noOpCacheMap.containsKey(cacheName)) {
                return Collections.singletonList(noOpCacheMap.get(cacheName));
            }
        }
        return super.resolveCaches(context);
    }

    @Override
    protected Collection<String> getCacheNames(CacheOperationInvocationContext<?> context) {
        return null;
    }

    public void setList(List<String> list) {
        for (String name : list) {
            noOpCacheMap.put(name, new NoOpCache(name));
        }

    }

    @Autowired
    @Override
    public void setCacheManager(CacheManager cacheManager) {
        super.setCacheManager(cacheManager);
    }
}
123456789101112131415161718192021222324252627282930313233343536
```

使用上面自定义`CacheResolver`

```java
@Cacheable(cacheResolver = "myCacheResolver")
public Book findBookCacheResolver(String isbn) {
    return createBook(isbn);
}
1234
```

单元测试：

```java
@Test
public void findBookCacheResolver() {
    Book book1= bookService.findBookCacheResolver(ISBN);
    Book book2 = bookService.findBookCacheResolver(ISBN);
    assert book1 != book2;
}
123456
```

## 同步缓存 sync=true

在多线程环境中，某些操作可能为相同的参数并发调用(通常在启动时)。默认情况下，缓存抽象不会锁定任何东西，相同的值可能会被计算多次，这就违背了缓存的目的。

对于这些特殊情况，可以使用sync属性指示底层缓存提供程序在计算值时锁定缓存条目。结果，只有一个线程会忙于计算该值，而其他线程会被阻塞，直到条目在缓存中更新。

开启缓存同步，同步锁是缓存`key`值，为了模拟线程同步，线程睡2秒钟

```java
@Cacheable(key = "#isbn", sync = true)
public Book findBookSync(String isbn) {
    System.out.println("查询,isbn=" + isbn);
    try {
        TimeUnit.SECONDS.sleep(2);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    return createBook(isbn);
}
12345678910
```

单元测试，使用两个线程同时访问

```java
@Test
public void findBookSync() throws ExecutionException, InterruptedException {
    ExecutorService executorService = Executors.newFixedThreadPool(2);
    Future<Book> book1 = executorService.submit(() -> bookService.findBookSync(ISBN));
    Future<Book> book2 = executorService.submit(() -> bookService.findBookSync(ISBN));
    assert book1.get() == book2.get();
}
1234567
```

感兴趣的童鞋可以把`sync = false`在运行单元测试试试。

## 条件缓存 condition和 unless

有时候，一些值不适合缓存，可以使用`@Cacheable#condition`属性判读那些数据不缓存，它接收的是一个Spel表达式，该表达式的值是`true`或`false`。

表达式为`true`，数据被缓存，`false`不被缓存就像方法没有缓存一样。

总页数大于100缓存

```java
@Cacheable(key = "#isbn", condition = "#totalPages > 100")
public Book findBookCondition(String isbn, int totalPages) {
    return createBook(isbn);
}
1234
```

单元测试，99没有缓存，200满足条件从缓存中获取

```java
@Test
public void findBookCondition() {
    Book book1 = bookService.findBook(ISBN);
    Book book2 = bookService.findBookCondition(ISBN, 99);
    Book book3 = bookService.findBookCondition(ISBN, 200);
    assert book1 != book2;
    assert book3 == book1;
}
12345678
```

`@Cacheable#unless`用于否决方法缓存的SpEL表达式，与condition不同，这个表达式是在调用**方法之后**计算的，因此可以引用结果。

由于是在方法执行后判读是否缓存，所有对于`key`值已经存在缓存中的数据不起作用。

```java
@Cacheable(key = "#isbn", unless = "#totalPages > 100")
public Book findBookUnless(String isbn, int totalPages) {
    return createBook(isbn);
}
1234
```

单元测试

```java
@Test
public void findBookUnless() {
    Book book3 = bookService.findBookUnless(ISBN, 200);
    Book book1 = bookService.findBook(ISBN);
    Book book2 = bookService.findBookUnless(ISBN, 99);
    Book book4 = bookService.findBookUnless(ISBN, 200);
    assert book1 == book2;
    assert book3 != book1;
    assert book4 == book1;
}
12345678910
```

`@Cacheable#unless`一般是对结果条件判读是否进行缓存使用的，这个示例使用的是入参作为判断条件，童鞋可以自己写一个根据结果进行缓存的示例，切记满足条件是不缓存。Spel `#result`变量代表返回值。

## 总结

在实际开发过程中`Spring Cahce`可能不满足业务需求时，我们可以使用它提供的一些接口实现满足业务需求的缓存处理逻辑，比如下一篇介绍的[Redis缓存](https://blog.csdn.net/sun_shaoping/article/details/84932940)。

代码地址，[点击这里](https://github.com/ssp1523/spring-cache)，或直接使用`Git`下载

```sh
git clone https://github.com/ssp1523/spring-cache.git
1
```

参考资料 [Spring Cache Abstraction](https://docs.spring.io/spring/docs/5.1.3.RELEASE/spring-framework-reference/integration.html#cache)

如发现内容有误，欢迎在评论区指教。

对内容不解，欢迎在评论区讨论。







<https://blog.csdn.net/sun_shaoping/article/details/84932879>