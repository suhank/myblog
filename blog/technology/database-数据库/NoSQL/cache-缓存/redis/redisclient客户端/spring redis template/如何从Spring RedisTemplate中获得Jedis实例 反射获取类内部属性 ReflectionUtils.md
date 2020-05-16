# 如何从Spring RedisTemplate中获得Jedis实例 反射获取类内部属性 ReflectionUtils

/Users/jerryye/backup/studio/AvailableCode/database/NoSQL/redis/springboot-redisTemplate/src/main/java/io/ymq/redis/test/GetInstanceTest.java

项目组同事提出一个问题，使用Spring RestTemplate不能在“不存在时设值”的同时，设置超时时间。我通过阅读源代码，发现Jedis是支持这一指令的，以下代码来自于 redis.clients.jedis.Jedis

```java
  /**
   * Set the string value as value of the key. The string can't be longer than 1073741824 bytes (1 GB).
   * @param key
   * @param value
   * @param nxxx NX|XX, NX -- Only set the key if it does not already exist. XX -- Only set the key if it already exist.
   * @param expx EX|PX, expire time units: EX = seconds; PX = milliseconds
   * @param time expire time in the units of <code>expx</code>
   * @return Status code reply
   */
  public String set(final String key, final String value, final String nxxx, final String expx, final long time) {
```

而RestTemplate在封装时，忽略了返回值。这个返回值表示了设值是否成功，在分布式锁等应用场景中是非常重要的。以下代码来自于org.springframework.data.redis.connection.RedisStringCommands

```java
/**
     * Set {@code value} for {@code key} applying timeouts from {@code expiration} if set and inserting/updating values
     * depending on {@code option}.
     *
     * @param key must not be {@literal null}.
     * @param value must not be {@literal null}.
     * @param expiration can be {@literal null}. Defaulted to {@link Expiration#persistent()}.
     * @param option can be {@literal null}. Defaulted to {@link SetOption#UPSERT}.
     * @since 1.7
     * @see <a href="http://redis.io/commands/set">Redis Documentation: SET</a>
     */
    void set(byte[] key, byte[] value, Expiration expiration, SetOption option); 
```

如何在使用RedisTemplate的同时，获取Jedis实例，执行相关的方法？以下的方案是从RedisConnectionFactory中获取Redis连接（JedisConnection实现类），然后使用反射的方法从中取得了Jedis实例，即可直接执行其中的方法，供大家参考

```java
    @Autowired
    private RedisConnectionFactory connectionFactory;

    @RequestMapping("/greeting")
    public String greeting() {
        Field jedisField = ReflectionUtils.findField(JedisConnection.class, "jedis");
        ReflectionUtils.makeAccessible(jedisField);
        System.out.println(connectionFactory.getConnection());
        Jedis jedis = (Jedis) ReflectionUtils.getField(jedisField, connectionFactory.getConnection());
        String result = jedis.set("test-key", "Hello world-", "NX", "EX", 1);
```

代码执行后，返回字符串”OK”或者”null”，表示是否设值成功。



https://blog.csdn.net/gongxsh00/article/details/77763385