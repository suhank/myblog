[TOC]



# 雪花算法中机器id保证全局唯一

关于分布式id的生成系统, 美团技术团队之前已经有写过一篇相关的文章, 详见 [Leaf——美团点评分布式ID生成系统](https://tech.meituan.com/2017/04/21/mt-leaf.html)

通常在生产中会用Twitter开源的雪花算法来生成分布式主键 雪花算法中的核心就是机器id和数据中心id, 通常来说数据中心id可以在配置文件中配置, 通常一个服务集群可以共用一个配置文件, 而机器id如果也放在配置文件中维护的话, 每个应用就需要一个独立的配置, 难免也会出现机器id重复的问题

解决方案: 1. 通过启动参数去指定机器id, 但是这种方式也会有出错的可能性 2. 每个应用启动的时候注册到redis或者zookeeper, 由redis或zookeeper来分配机器id

接下来主要介绍基于redis的实现方式, 一种是注册的时候设置过期时间, 配置定时器定时去检查机器id是否过期需要重新分配; 另一种是不设置过期时间, 只依靠在spring容器销毁的时候去删除记录(但是这种方式容易删除失败)

## 实现方式一

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>1.5.10.RELEASE</version>
</parent>
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter</artifactId>
        <exclusions>
            <exclusion>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-logging</artifactId>
            </exclusion>
        </exclusions>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
        <exclusions>
            <exclusion>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-logging</artifactId>
            </exclusion>
        </exclusions>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-aop</artifactId>
        <exclusions>
            <exclusion>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-logging</artifactId>
            </exclusion>
        </exclusions>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
        <exclusions>
            <exclusion>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-logging</artifactId>
            </exclusion>
        </exclusions>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>
    <!-- 日志包...开始 -->
    <!-- log配置：Log4j2 + Slf4j -->
    <dependency>
        <groupId>org.apache.logging.log4j</groupId>
        <artifactId>log4j-api</artifactId>
    </dependency>
    <dependency>
        <groupId>org.apache.logging.log4j</groupId>
        <artifactId>log4j-core</artifactId>
    </dependency>
    <dependency> <!-- 桥接：告诉Slf4j使用Log4j2 -->
        <groupId>org.apache.logging.log4j</groupId>
        <artifactId>log4j-slf4j-impl</artifactId>
    </dependency>
    <dependency> <!-- 桥接：告诉commons logging使用Log4j2 -->
        <groupId>org.apache.logging.log4j</groupId>
        <artifactId>log4j-jcl</artifactId>
        <version>2.2</version>
    </dependency>
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-api</artifactId>
    </dependency>
    <!-- 日志包...结束 -->
</dependencies>
```

redis的配置

```java
/**
 * redis的配置
 *
 * @author wang.js on 2019/3/8.
 * @version 1.0
 */
@Configuration
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port:6379}")
    private Integer port;

    @Bean
    public JedisPool jedisPool() {
        //1.设置连接池的配置对象
        JedisPoolConfig config = new JedisPoolConfig();
        //设置池中最大连接数
        config.setMaxTotal(50);
        //设置空闲时池中保有的最大连接数
        config.setMaxIdle(10);
        config.setMaxWaitMillis(3000L);
        config.setTestOnBorrow(true);
        //2.设置连接池对象
        return new JedisPool(config,host,port);
    }

}
```

snowflake算法中机器id的获取

```java
/**
 * snowflake算法中机器id的获取
 *
 * @author wang.js on 2019/3/8.
 * @version 1.0
 */
@Configuration
public class MachineIdConfig {

    @Resource
    private JedisPool jedisPool;

    @Value("${snowflake.datacenter}")
    private Integer dataCenterId;

    @Value("${snowflake.bizType}")
    private String OPLOG_MACHINE_ID_kEY;

    /**
     * 机器id
     */
    public static Integer machineId;
    /**
     * 本地ip地址
     */
    private static String localIp;
    private static TimeUnit timeUnit = TimeUnit.DAYS;

    private static final Logger LOGGER = LoggerFactory.getLogger(MachineIdConfig.class);

    /**
     * 获取ip地址
     *
     * @return
     * @throws UnknownHostException
     */
    private String getIPAddress() throws UnknownHostException {
        InetAddress address = InetAddress.getLocalHost();
        return address.getHostAddress();
    }

    /**
     * hash机器IP初始化一个机器ID
     */
    @Bean
    public SnowFlakeGenerator initMachineId() throws Exception {
        localIp = getIPAddress();

        Long ip_ = Long.parseLong(localIp.replaceAll("\\.", ""));
        //这里取128,为后续机器Ip调整做准备。
        machineId = ip_.hashCode() % 32;
        //创建一个机器ID
        createMachineId();
        LOGGER.info("初始化 machine_id :{}", machineId);

        return new SnowFlakeGenerator(machineId, dataCenterId);
    }

    /**
     * 容器销毁前清除注册记录
     */
    @PreDestroy
    public void destroyMachineId() {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(OPLOG_MACHINE_ID_kEY + dataCenterId + machineId);
        }
    }


    /**
     * 主方法：获取一个机器id
     *
     * @return
     */
    public Integer createMachineId() {
        try {
            //向redis注册，并设置超时时间
            Boolean aBoolean = registerMachine(machineId, localIp);
            //注册成功
            if (aBoolean) {
                //启动一个线程更新超时时间
                updateExpTimeThread();
                //返回机器Id
                return machineId;
            }
            //检查是否被注册满了.不能注册，就直接返回
            if (!checkIfCanRegister()) {
                //注册满了，加一个报警
                return machineId;
            }
            LOGGER.info("createMachineId->ip:{},machineId:{}, time:{}", localIp, machineId, new Date());

            //递归调用
            createMachineId();
        } catch (Exception e) {
            getRandomMachineId();
            return machineId;
        }
        getRandomMachineId();
        return machineId;
    }

    /**
     * 检查是否被注册满了
     *
     * @return
     */
    private Boolean checkIfCanRegister() {
        Boolean flag = true;
        //判断0~127这个区间段的机器IP是否被占满
        try (Jedis jedis = jedisPool.getResource()) {
            for (int i = 0; i <= 127; i++) {
                flag = jedis.exists(OPLOG_MACHINE_ID_kEY + dataCenterId + i);
                //如果不存在。说明还可以继续注册。直接返回i
                if (!flag) {
                    machineId = i;
                    break;
                }
            }
        }
        return !flag;
    }

    /**
     * 1.更新超時時間
     * 注意，更新前检查是否存在机器ip占用情况
     */
    private void updateExpTimeThread() {
        //开启一个线程执行定时任务:
        //1.每23小时更新一次超时时间
        new Timer(localIp).schedule(new TimerTask() {
            @Override
            public void run() {
                //检查缓存中的ip与本机ip是否一致, 一致则更新时间，不一致则重新获取一个机器id
                Boolean b = checkIsLocalIp(String.valueOf(machineId));
                if (b) {
                    LOGGER.info("更新超时时间 ip:{},machineId:{}, time:{}", localIp, machineId, new Date());
                    try (Jedis jedis = jedisPool.getResource()) {
                        jedis.expire(OPLOG_MACHINE_ID_kEY + dataCenterId + machineId, 60 * 60 * 24 * 1000);
                    }
                } else {
                    LOGGER.info("重新生成机器ID ip:{},machineId:{}, time:{}", localIp, machineId, new Date());
                    //重新生成机器ID，并且更改雪花中的机器ID
                    getRandomMachineId();
                    //重新生成并注册机器id
                    createMachineId();
                    //更改雪花中的机器ID
                    SnowFlakeGenerator.setWorkerId(machineId);
                    // 结束当前任务
                    LOGGER.info("Timer->thread->name:{}", Thread.currentThread().getName());
                    this.cancel();
                }
            }
        }, 10 * 1000, 1000 * 60 * 60 * 23);
    }

    /**
     * 获取1~127随机数
     */
    public void getRandomMachineId() {
        machineId = (int) (Math.random() * 127);
    }

    /**
     * 机器ID顺序获取
     */
    public void incMachineId() {
        if (machineId >= 127) {
            machineId = 0;
        } else {
            machineId += 1;
        }
    }

    /**
     * @param mechineId
     * @return
     */
    private Boolean checkIsLocalIp(String mechineId) {
        try (Jedis jedis = jedisPool.getResource()) {
            String ip = jedis.get(OPLOG_MACHINE_ID_kEY + dataCenterId + mechineId);
            LOGGER.info("checkIsLocalIp->ip:{}", ip);
            return localIp.equals(ip);
        }
    }

    /**
     * 1.注册机器
     * 2.设置超时时间
     *
     * @param machineId 取值为0~127
     * @return
     */
    private Boolean registerMachine(Integer machineId, String localIp) throws Exception {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(OPLOG_MACHINE_ID_kEY + dataCenterId + machineId, localIp);
            jedis.expire(OPLOG_MACHINE_ID_kEY + dataCenterId + machineId, 60 * 60 * 24 * 1000);
            return true;
        }
    }

}
```

雪花算法(雪花算法百度上很多, 自己可以随便找一个)

```java
/**
 * 雪花算法
 *
 * @author wang.js on 2019/3/8.
 * @version 1.0
 */
public class SnowFlakeGenerator {

    private final long twepoch = 1288834974657L;
    private final long workerIdBits = 5L;
    private final long datacenterIdBits = 5L;
    private final long maxWorkerId = -1L ^ (-1L << workerIdBits);
    private final long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);
    private final long sequenceBits = 12L;
    private final long workerIdShift = sequenceBits;
    private final long datacenterIdShift = sequenceBits + workerIdBits;
    private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);

    private static long workerId;
    private long datacenterId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    public SnowFlakeGenerator(long actualWorkId, long datacenterId) {
        if (actualWorkId > maxWorkerId || actualWorkId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
        }
        workerId = actualWorkId;
        this.datacenterId = datacenterId;
    }

    public static void setWorkerId(long workerId) {
        SnowFlakeGenerator.workerId = workerId;
    }

    public synchronized long nextId() {
        long timestamp = timeGen();
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }
        lastTimestamp = timestamp;
        return ((timestamp - twepoch) << timestampLeftShift) | (datacenterId << datacenterIdShift) | (workerId << workerIdShift) | sequence;
    }

    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    protected long timeGen() {
        return System.currentTimeMillis();
    }
}
```

测试的controller

```java
/**
 * 雪花算法
 *
 * @author wang.js on 2019/3/8.
 * @version 1.0
 */
@RequestMapping("/snowflake")
@RestController
public class SnowflakeController {

    @Resource
    private SnowFlakeGenerator snowFlakeGenerator;

    /**
     * 获取分布式主键
     *
     * @return
     */
    @GetMapping("/get")
    public long getDistributeId() {
        return snowFlakeGenerator.nextId();
    }

}
```

配置文件

```yaml
server:
  port: 12892
spring:
  redis:
    database: 0
    host: mini7
    lettuce:
      pool:
        max-active: 8
        max-idle: 8
        max-wait: -1
        min-idle: 0
    port: 6379
    timeout: 10000

snowflake:
  datacenter: 1 # 数据中心的id
  bizType: order_id_ # 业务类型
```

## 实现方式二

机器id注册到redis的时候, 不设置过期时间 同时采用sharding-jdbc的分布式主键生成组件

maven依赖

```
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>1.5.10.RELEASE</version>
</parent>

<properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
    <java.version>1.8</java.version>
    <sharding-sphere.version>3.0.0.M4</sharding-sphere.version>
</properties>

<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter</artifactId>
        <exclusions>
            <exclusion>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-logging</artifactId>
            </exclusion>
        </exclusions>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
        <exclusions>
            <exclusion>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-logging</artifactId>
            </exclusion>
        </exclusions>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-aop</artifactId>
        <exclusions>
            <exclusion>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-logging</artifactId>
            </exclusion>
        </exclusions>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
        <exclusions>
            <exclusion>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-logging</artifactId>
            </exclusion>
        </exclusions>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>

    <!--sharding-jdbc依赖开始-->
    <!-- for spring boot -->
    <dependency>
        <groupId>io.shardingsphere</groupId>
        <artifactId>sharding-jdbc-spring-boot-starter</artifactId>
        <version>${sharding-sphere.version}</version>
    </dependency>

    <!-- for spring namespace -->
    <dependency>
        <groupId>io.shardingsphere</groupId>
        <artifactId>sharding-jdbc-spring-namespace</artifactId>
        <version>${sharding-sphere.version}</version>
    </dependency>
    <!--sharding-jdbc依赖结束-->

    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <version>5.1.41</version>
    </dependency>
    <dependency>
        <groupId>com.alibaba</groupId>
        <artifactId>druid</artifactId>
        <version>1.1.0</version>
    </dependency>

    <!-- 日志包...开始 -->
    <!-- log配置：Log4j2 + Slf4j -->
    <dependency>
        <groupId>org.apache.logging.log4j</groupId>
        <artifactId>log4j-api</artifactId>
    </dependency>
    <dependency>
        <groupId>org.apache.logging.log4j</groupId>
        <artifactId>log4j-core</artifactId>
    </dependency>
    <dependency> <!-- 桥接：告诉Slf4j使用Log4j2 -->
        <groupId>org.apache.logging.log4j</groupId>
        <artifactId>log4j-slf4j-impl</artifactId>
    </dependency>
    <dependency> <!-- 桥接：告诉commons logging使用Log4j2 -->
        <groupId>org.apache.logging.log4j</groupId>
        <artifactId>log4j-jcl</artifactId>
        <version>2.2</version>
    </dependency>
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-api</artifactId>
    </dependency>
    <!-- 日志包...结束 -->
</dependencies>
```

配置文件

```
server:
  port: 12893

# sharding-jdbc分库分表的配置
sharding:
  jdbc:
    datasource:
      ds0:
        type: com.alibaba.druid.pool.DruidDataSource
        driver-class-name: com.mysql.jdbc.Driver
        url: jdbc:mysql://localhost:3306/ds0
        username: root
        password: 123456
      names: ds0
spring:
  redis:
    database: 0
    host: mini7
    lettuce:
      pool:
        max-active: 8
        max-idle: 8
        max-wait: -1
        min-idle: 0
    port: 6379
    timeout: 10000

snowflake:
  datacenter: 1 # 数据中心的id
  bizType: sharding_jdbc_id_ # 业务类型
```

redis的配置

```
/**
 * redis的配置
 *
 * @author wang.js on 2019/3/8.
 * @version 1.0
 */
@Configuration
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port:6379}")
    private Integer port;

    @Bean
    public JedisPool jedisPool() {
        //1.设置连接池的配置对象
        JedisPoolConfig config = new JedisPoolConfig();
        //设置池中最大连接数
        config.setMaxTotal(50);
        //设置空闲时池中保有的最大连接数
        config.setMaxIdle(10);
        config.setMaxWaitMillis(3000L);
        config.setTestOnBorrow(true);
        //2.设置连接池对象
        return new JedisPool(config,host,port);
    }

}
```

机器id的配置

```
/**
 * 保证workerId的全局唯一性
 *
 * @author wang.js on 2019/3/8.
 * @version 1.0
 */
@Component
public class WorkerIdConfig {

    @Resource
    private JedisPool jedisPool;

    @Value("${snowflake.datacenter}")
    private Integer dataCenterId;

    @Value("${snowflake.bizType}")
    private String bizType;
    /**
     * 机器id
     */
    private int workerId;

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerIdConfig.class);

    public int getWorkerId() throws UnknownHostException {
        String ipAddress = getIPAddress();
        Long ip = Long.parseLong(ipAddress.replaceAll("\\.", ""));
        //这里取128,为后续机器Ip调整做准备。
        workerId = ip.hashCode() % 1024;
        try (Jedis jedis = jedisPool.getResource()) {
            Long setnx = jedis.setnx(bizType + dataCenterId + workerId, ipAddress);
            if (setnx > 0) {
                return workerId;
            } else {
                // 判断是否是同一ip
                String cacheIp = jedis.get(bizType + dataCenterId + workerId);
                if (ipAddress.equalsIgnoreCase(cacheIp)) {
                    return workerId;
                }
            }
            throw new RuntimeException("机器id:" + workerId + "已经存在, 请先清理缓存");
        }
    }

    @PreDestroy
    public void delWorkerId() {
        LOGGER.info("开始销毁机器id:" + workerId);
        try (Jedis jedis = jedisPool.getResource()) {
            Long del = jedis.del(bizType + dataCenterId + workerId);
            if (del == 0) {
                throw new RuntimeException("机器id:" + workerId + "删除失败");
            }
        }
    }

    /**
     * 获取ip地址
     *
     * @return
     * @throws UnknownHostException
     */
    private String getIPAddress() throws UnknownHostException {
        InetAddress address = InetAddress.getLocalHost();
        return address.getHostAddress();
    }
}
```

sharding-jdbc分布式主键生成的配置

```
/**
 * sharding-jdbc分布式主键生成的配置
 *
 * @author wang.js on 2019/3/8.
 * @version 1.0
 */
@Configuration
public class ShardingIdConfig {

    @Resource
    private WorkerIdConfig workerIdConfig;

    @Bean
    public DefaultKeyGenerator defaultKeyGenerator() throws UnknownHostException {
        DefaultKeyGenerator generator = new DefaultKeyGenerator();
        // 最大值小于1024
        DefaultKeyGenerator.setWorkerId(workerIdConfig.getWorkerId());
        return generator;
    }

}
```

测试的controller

```
/**
 * 生成分布式主键
 *
 * @author wang.js on 2019/3/8.
 * @version 1.0
 */
@RestController
@RequestMapping("/id")
public class GenIdController {

    @Resource
    private DefaultKeyGenerator generator;

    @GetMapping("/get")
    public long get() {
        return generator.generateKey().longValue();
    }

}
```

sharding-jdbc的DefaultKeyGenerator中的源码中可以看到最大的workerId是1024L

```
public static void setWorkerId(long workerId) {
    Preconditions.checkArgument(workerId >= 0L && workerId < 1024L);
    workerId = workerId;
}
```







https://www.cnblogs.com/shanzhai/p/10500274.html