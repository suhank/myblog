[TOC]



# Spirng Cache(第三篇)Redis缓存配置

2018年12月09日 22:04:44 [孙平平](https://me.csdn.net/sun_shaoping) 阅读数 661  



从3.1版开始，Spring Framework提供了对现有Spring应用程序**透明**地添加缓存的支持。与[事务](https://docs.spring.io/spring/docs/5.0.11.RELEASE/spring-framework-reference/data-access.html#transaction) 支持类似，缓存抽象允许一致地使用各种缓存解决方案，而对代码的**影响最小**。

从Spring 4.1开始，通过[JSR-107注释](https://docs.spring.io/spring/docs/5.0.11.RELEASE/spring-framework-reference/integration.html#cache-jsr-107)和更多自定义选项的支持，缓存抽象得到了显着改进。

这篇介绍下如果根据Spring 缓存的`Redis`实现。

在`Spring boot 1.5+`和`Spring boot 2.0+`版本，分别使用配置文件和注解实现。

 

## 切换到Redis缓存

把前面内存缓存切换到`Redis`缓存，只需要修改配置，业务代码不动。

修改配置文件 `application.yml`，添加`Redis`相关配置。[redis 安装](http://www.runoob.com/redis/redis-install.html)

添加`Redis`依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

**将上一章自定义配置都删除掉，不然会影响到Redis配置**

`application.yml`配置

```yaml
spring:
  redis:
    host: localhost
    port: 6379
```

这样就可以切换到Redis缓存了，别忘记重写`Book` 实体的`equals`和`hashCode`

单元测试

```java
@Test
public void findBook() {
    Book book1 = bookService.findBook(ISBN);
    Book book2 = bookService.findBook(ISBN);
    Book book3 = bookService.findBook(ISBN);
    assert book1.equals(book2);
    assert book1.equals(book3);
} 
```

## Redis缓存自定义配置 application.yml

此配置适用于`Spring-boot1.5.x`版本。

添加以下几项自定义配置

- `default-expiration` 默认过期时间 单位秒
- `cache-null-values` 缓存null值
- `key-prefix` 全局缓存前缀 默认值 `spring-cache`
- `expires` 指定缓存名称过期时间 单位秒

properties配置类，省略Get/Set方法

```java
@ConfigurationProperties("spring.cache.redis")
public class RedisCacheProperties {
    /**
     * 全局默认过期时间 单位秒
     */
    private Long defaultExpiration;
    /**
     * 是否缓存null值
     */
    private boolean cacheNullValues = true;
    /**
     * 缓存前缀
     */
    private String keyPrefix = "spring-cache";
    /**
     * 指定缓存名称过期时间 单位秒
     */
    private Map<String, Long> expires;
} 
```

覆盖Redis默认配置

```java
@EnableCaching
@EnableConfigurationProperties({RedisCacheProperties.class, CacheProperties.class})
public class RedisCacheConfig implements RedisCachePrefix {

    private final RedisSerializer<String> serializer = new StringRedisSerializer();

    private final RedisCacheProperties redisCacheProperties;

    private final CacheProperties cacheProperties;

    public RedisCacheConfig(RedisCacheProperties redisCacheProperties, CacheProperties cacheProperties) {
        this.redisCacheProperties = redisCacheProperties;
        this.cacheProperties = cacheProperties;
    }
    /**
     * 覆盖redis 默认缓存管理器
     */
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Object> redisTemplate = redisTemplate(redisConnectionFactory);

        RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate, null, redisCacheProperties.isCacheNullValues());
        if (redisCacheProperties.getKeyPrefix() != null) {
            cacheManager.setUsePrefix(true);
        }
        cacheManager.setCachePrefix(this);
        List<String> cacheNames = this.cacheProperties.getCacheNames();
        if (!cacheNames.isEmpty()) {
            cacheManager.setCacheNames(cacheNames);
        }
        if (redisCacheProperties.getDefaultExpiration() != null) {
            cacheManager.setDefaultExpiration(redisCacheProperties.getDefaultExpiration());
        }
        if (!CollectionUtils.isEmpty(redisCacheProperties.getExpires())) {
            cacheManager.setExpires(
                    redisCacheProperties.getExpires()
            );
        }
        return cacheManager;
    }

    private RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(serializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    /**
     * 重写缓存前缀序列化
     */
    @Override
    public byte[] prefix(String cacheName) {
        return serializer.serialize(redisCacheProperties.getKeyPrefix() + ":" + cacheName + ":");
    }

}
 
```

配置缓存失效时间 20秒、前缀`redis-cache`

```yaml
spring:
  redis:
    host: localhost
    port: 6379
  cache:
    redis:
      key-prefix: redis-cache
      default-expiration: 20
    type: redis 
```

运行单元测试

```java
com.example.service.BookServiceTest#findBook
1
```

Redis缓存服务会多一条缓存记录，缓存时间是20秒

```shell
127.0.0.1:6379> keys *
1) "redis-cache:books:1234-5678"
127.0.0.1:6379> ttl "redis-cache:books:1234-5678"
(integer) 20
1234
```

## Redis缓存自定义注解

示例基于`Spring-boot2.x`版本

修改`Spring-boot`版本

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.1.1.RELEASE</version>
</parent> 
```

使用`Lettuce`

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis-reactive</artifactId>
</dependency>
1234
```

因为2.0以后`Redis`缓存管理改动挺大的这里要删除原来的配置。

定义一个注解`RedisCacheable`实现`Redis` 自定义缓存配置,下面只给出了一个自己定义的属性，其他属性请[参考源码](https://github.com/ssp1523/spring-cache/blob/redis-spring-boob-2.x/src/main/java/com/example/RedisCacheable.java)

```java
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Cacheable(cacheResolver = "redisCacheResolver")
public @interface RedisCacheable {
    /**
     * 过期时间
     */
    String expires() default "";
} 
```

接下来我们让这个注解生效，创建一个`RedisCacheResolver`实现类 `RedisCacheResolver`，并且将它注册到Spring 容器其中，**注意**这个Bean实例名称一定要是`redisCacheResolver`，与`@RedisCacheable`注解上的`@Cacheable(cacheResolver = "redisCacheResolver")`的`cacheResolver`要保持名称一致。

`@RedisCacheable`注解解析实现，如果方法上存在这个注解，创建一个`RedisCache`缓冲实例。

```java
public class RedisCacheResolver extends SimpleCacheResolver {

    private final RedisCacheWriter cacheWriter;

    private final RedisCacheConfiguration redisCacheConfiguration;

    public RedisCacheResolver(RedisCacheWriter cacheWriter, CacheManager cacheManager, RedisCacheConfiguration redisCacheConfiguration) {
        super(cacheManager);
        this.cacheWriter = cacheWriter;
        this.redisCacheConfiguration = redisCacheConfiguration;
    }

    @Override
    public Collection<? extends Cache> resolveCaches(CacheOperationInvocationContext<?> context) {

        RedisCacheable redisCacheable = AnnotationUtils.findAnnotation(context.getMethod(), RedisCacheable.class);
        if (redisCacheable == null) {
            return super.resolveCaches(context);
        }
        return getCaches(context, redisCacheable);
    }

    private Collection<? extends Cache> getCaches(CacheOperationInvocationContext<?> context, RedisCacheable redisCacheable) {
        Collection<String> cacheNames = getCacheNames(context);
        if (cacheNames == null) {
            return Collections.emptyList();
        }
        Collection<Cache> result = new ArrayList<>(cacheNames.size());
        String expires = redisCacheable.expires();
        ConversionService conversionService = redisCacheConfiguration.getConversionService();
        Duration ttl = conversionService.convert(expires, Duration.class);
        for (String cacheName : cacheNames) {
            Cache cache = createRedisCache(cacheName, redisCacheConfiguration.entryTtl(ttl));
            if (cache == null) {
                throw new IllegalArgumentException("Cannot find cache named '" +
                        cacheName + "' for " + context.getOperation());
            }
            result.add(cache);
        }

        return result;
    }

    protected RedisCache createRedisCache(String name, @Nullable RedisCacheConfiguration cacheConfig) {
        return new RedisCache(name, cacheWriter, cacheConfig);
    }
}
1234567891011121314151617181920212223242526272829303132333435363738394041424344454647
```

RedisCache：

```java
public class RedisCache extends org.springframework.data.redis.cache.RedisCache {

    public RedisCache(String name, RedisCacheWriter cacheWriter, RedisCacheConfiguration cacheConfig) {
        super(name, cacheWriter, cacheConfig);
    }
}
123456
```

`Redis`配置

1、覆盖默认`Redis`缓存配置

2、创建`RedisCacheResolver` Bean实例，`beanName=redisCacheResolver`

```java
@EnableCaching
@EnableConfigurationProperties(CacheProperties.class)
public class RedisCacheConfig extends CachingConfigurerSupport {

    private final CacheProperties cacheProperties;


    public RedisCacheConfig(CacheProperties cacheProperties) {
        this.cacheProperties = cacheProperties;
    }

    /**
     * 覆盖reids默认配置
     */
    @Bean
    RedisCacheConfiguration redisCacheConfiguration() {
        CacheProperties.Redis redisProperties = this.cacheProperties.getRedis();
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
        //序列化
        ObjectMapper objectMapper = new ObjectMapper();
        // 4.设置可见度
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 5.启动默认的类型
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
        config = config.serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer)
        );
        if (redisProperties.getTimeToLive() != null) {
            config = config.entryTtl(redisProperties.getTimeToLive());
        }
        if (!redisProperties.isCacheNullValues()) {
            config = config.disableCachingNullValues();
        }
        if (!redisProperties.isUseKeyPrefix()) {
            config = config.disableKeyPrefix();
        }
        if (!StringUtils.isEmpty(redisProperties.getKeyPrefix())) {

            config = config.computePrefixWith(
                    cacheName -> redisProperties.getKeyPrefix() + ":" + cacheName + ":"
            );
        }

        return config;
    }


    
    @Bean("redisCacheResolver")
    @ConditionalOnMissingBean
    public RedisCacheResolver redisCacheResolver(RedisCacheManager redisCacheManager) throws NoSuchFieldException, IllegalAccessException {
        RedisCacheConfiguration redisCacheConfiguration = redisCacheConfiguration();

        Field cacheWriterField = RedisCacheManager.class.getDeclaredField("cacheWriter");
        cacheWriterField.setAccessible(true);
        RedisCacheWriter cacheWriter = (RedisCacheWriter) cacheWriterField.get(redisCacheManager);

        return new RedisCacheResolver(cacheWriter, redisCacheManager, redisCacheConfiguration);
    }

}
123456789101112131415161718192021222324252627282930313233343536373839404142434445464748495051525354555657585960616263
```

application.yml：

```yml
spring:
  redis:
    host: localhost
    port: 6379
  cache:
    redis:
      key-prefix: redis-cache
    type: redis

123456789
```

使用`@RedisCacheable`，缓存一分钟

```java
@RedisCacheable(expires = "PT1M")
public Book findBook(String isbn) {
    System.out.println("查询,isbn=" + isbn);
    return createBook(isbn);
}
12345
```

运行单元测试

```java
@Test
public void findBook() {
    Book book1 = bookService.findBook(ISBN);
    Book book2 = bookService.findBook(ISBN);
    Book book3 = bookService.findBook(ISBN);
    assert book1.equals(book2);
    assert book1.equals(book3);
}
12345678
```

缓存时间为60秒

```shell
127.0.0.1:6379> ttl "redis-cache:books:1234-5678"
(integer) 60
12
```

### RedisCacheResolver重复调用问题

`RedisCacheResolver`实现类，每当添加缓存到`Redis`时都会执行一次，其实这里只需要进行一次`Cache`实例初始化就行了，所以这里可以针对每个方法做一个内存缓存处理。

使用`ConcurrentMap`添加缓存处理，代码如下

```java
public class RedisCacheResolver extends SimpleCacheResolver {

    private final RedisCacheWriter cacheWriter;

    private final RedisCacheConfiguration redisCacheConfiguration;

    private final ConcurrentMap<Method, Collection<? extends Cache>> cacheMethodMap = new ConcurrentHashMap<>();

    public RedisCacheResolver(RedisCacheWriter cacheWriter, CacheManager cacheManager, RedisCacheConfiguration redisCacheConfiguration) {
        super(cacheManager);
        this.cacheWriter = cacheWriter;
        this.redisCacheConfiguration = redisCacheConfiguration;
    }

    @Override
    public Collection<? extends Cache> resolveCaches(CacheOperationInvocationContext<?> context) {

        RedisCacheable redisCacheable = AnnotationUtils.findAnnotation(context.getMethod(), RedisCacheable.class);
        if (redisCacheable == null) {
            return super.resolveCaches(context);
        }
        return cacheMethodMap.computeIfAbsent(context.getMethod(), method -> getCaches(context, redisCacheable));
    }

    private Collection<? extends Cache> getCaches(CacheOperationInvocationContext<?> context, RedisCacheable redisCacheable) {
        Collection<String> cacheNames = getCacheNames(context);
        if (cacheNames == null) {
            return Collections.emptyList();
        }
        Collection<Cache> result = new ArrayList<>(cacheNames.size());
        String expires = redisCacheable.expires();
        ConversionService conversionService = redisCacheConfiguration.getConversionService();
        Duration ttl = conversionService.convert(expires, Duration.class);
        for (String cacheName : cacheNames) {
            Cache cache = createRedisCache(cacheName, redisCacheConfiguration.entryTtl(ttl));
            if (cache == null) {
                throw new IllegalArgumentException("Cannot find cache named '" +
                        cacheName + "' for " + context.getOperation());
            }
            result.add(cache);
        }

        return result;
    }

    protected RedisCache createRedisCache(String name, @Nullable RedisCacheConfiguration cacheConfig) {
        return new RedisCache(name, cacheWriter, cacheConfig);
    }
}

1234567891011121314151617181920212223242526272829303132333435363738394041424344454647484950
```

## 总结

本篇介绍了从内存缓存切换到`Redis`缓存，并在没有影响到任何业务代码的情况下，这就是使用`Spring Cache`的好处

接下来分别基于`application.yml`和自定义注解`RedisCacheable`实现的`Redis`缓存自定配置，其他缓存实现也可以这样做的。

如果童鞋想对`Redis`缓存实现添加更多的配置项可以参考
[Redis缓存自定义配置 application.yml](https://blog.csdn.net/sun_shaoping/article/details/84932940#Redis%E7%BC%93%E5%AD%98%E8%87%AA%E5%AE%9A%E4%B9%89%E9%85%8D%E7%BD%AEapplication.yml)和 [Redis缓存自定义注解](https://blog.csdn.net/sun_shaoping/article/details/84932940#Redis%E7%BC%93%E5%AD%98%E8%87%AA%E5%AE%9A%E4%B9%89%E6%B3%A8%E8%A7%A3)。

代码地址[redis-spring-boob-1.5.x实现](https://github.com/ssp1523/spring-cache/tree/redis-spring-boob-1.5.x)和[redis-spring-boob-2.x](https://github.com/ssp1523/spring-cache/tree/redis-spring-boob-2.x)实现或直接使用`Git`下载

```sh
git clone -b redis-spring-boob-1.5.x https://github.com/ssp1523/spring-cache.git
git clone -b redis-spring-boob-2.x https://github.com/ssp1523/spring-cache.git 
```

参考资料 [Spring Cache Abstraction](https://docs.spring.io/spring/docs/5.1.3.RELEASE/spring-framework-reference/integration.html#cache)

如发现内容错误，欢迎在评论区指教。

对内容不解，欢迎在评论区讨论。







<https://blog.csdn.net/sun_shaoping/article/details/84932940>