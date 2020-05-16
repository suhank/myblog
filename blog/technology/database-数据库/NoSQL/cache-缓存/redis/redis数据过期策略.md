[TOC]



# 关于Redis数据过期策略

## **1、Redis中key的的过期时间**

通过EXPIRE key seconds命令来设置数据的过期时间。返回1表明设置成功，返回0表明key不存在或者不能成功设置过期时间。在key上设置了过期时间后key将在指定的秒数后被自动删除。被指定了过期时间的key在Redis中被称为是不稳定的。

**当key被DEL命令删除或者被SET、GETSET命令重置后与之关联的过期时间会被清除**

```sh
127.0.0.1:6379> setex s 20 1
OK
127.0.0.1:6379> ttl s
(integer) 17
127.0.0.1:6379> setex s 200 1
OK
127.0.0.1:6379> ttl s
(integer) 195
127.0.0.1:6379> setrange s 3 100
(integer) 6
127.0.0.1:6379> ttl s
(integer) 152
127.0.0.1:6379> get s
"1\x00\x00100"
127.0.0.1:6379> ttl s
(integer) 108
127.0.0.1:6379> getset s 200
"1\x00\x00100"
127.0.0.1:6379> get s
"200"
127.0.0.1:6379> ttl s
(integer) -1
```

使用PERSIST可以清除过期时间

```sh
127.0.0.1:6379> setex s 100 test
OK
127.0.0.1:6379> get s
"test"
127.0.0.1:6379> ttl s
(integer) 94
127.0.0.1:6379> type s
string
127.0.0.1:6379> strlen s
(integer) 4
127.0.0.1:6379> persist s
(integer) 1
127.0.0.1:6379> ttl s
(integer) -1
127.0.0.1:6379> get s
"test"
```

使用rename只是改了key值

```sh
127.0.0.1:6379> expire s 200
(integer) 1
127.0.0.1:6379> ttl s
(integer) 198
127.0.0.1:6379> rename s ss
OK
127.0.0.1:6379> ttl ss
(integer) 187
127.0.0.1:6379> type ss
string
127.0.0.1:6379> get ss
"test"
```

说明：Redis2.6以后expire精度可以控制在0到1毫秒内，key的过期信息以绝对Unix时间戳的形式存储（Redis2.6之后以毫秒级别的精度存储），所以在多服务器同步的时候，一定要同步各个服务器的时间

## 2、Redis过期键删除策略

Redis key过期的方式有三种：

- 被动删除：当读/写一个已经过期的key时，会触发惰性删除策略，直接删除掉这个过期key
- 主动删除：由于惰性删除策略无法保证冷数据被及时删掉，所以Redis会定期主动淘汰一批已过期的key
- 当前已用内存超过maxmemory限定时，触发主动清理策略

### **被动删除**

只有key被操作时(如GET)，REDIS才会被动检查该key是否过期，如果过期则删除之并且返回NIL。

1、这种删除策略对CPU是友好的，删除操作只有在不得不的情况下才会进行，不会其他的expire key上浪费无谓的CPU时间。

2、但是这种策略对内存不友好，一个key已经过期，但是在它被操作之前不会被删除，仍然占据内存空间。如果有大量的过期键存在但是又很少被访问到，那会造成大量的内存空间浪费。expireIfNeeded(redisDb *db, robj *key)函数位于src/db.c。

```c
/*-----------------------------------------------------------------------------
 * Expires API
 *----------------------------------------------------------------------------*/
 
int removeExpire(redisDb *db, robj *key) {
    /* An expire may only be removed if there is a corresponding entry in the
     * main dict. Otherwise, the key will never be freed. */
    redisAssertWithInfo(NULL,key,dictFind(db->dict,key->ptr) != NULL);
    return dictDelete(db->expires,key->ptr) == DICT_OK;
}
 
void setExpire(redisDb *db, robj *key, long long when) {
    dictEntry *kde, *de;
 
    /* Reuse the sds from the main dict in the expire dict */
    kde = dictFind(db->dict,key->ptr);
    redisAssertWithInfo(NULL,key,kde != NULL);
    de = dictReplaceRaw(db->expires,dictGetKey(kde));
    dictSetSignedIntegerVal(de,when);
}
 
/* Return the expire time of the specified key, or -1 if no expire
 * is associated with this key (i.e. the key is non volatile) */
long long getExpire(redisDb *db, robj *key) {
    dictEntry *de;
 
    /* No expire? return ASAP */
    if (dictSize(db->expires) == 0 ||
       (de = dictFind(db->expires,key->ptr)) == NULL) return -1;
 
    /* The entry was found in the expire dict, this means it should also
     * be present in the main dict (safety check). */
    redisAssertWithInfo(NULL,key,dictFind(db->dict,key->ptr) != NULL);
    return dictGetSignedIntegerVal(de);
}
 
/* Propagate expires into slaves and the AOF file.
 * When a key expires in the master, a DEL operation for this key is sent
 * to all the slaves and the AOF file if enabled.
 *
 * This way the key expiry is centralized in one place, and since both
 * AOF and the master->slave link guarantee operation ordering, everything
 * will be consistent even if we allow write operations against expiring
 * keys. */
void propagateExpire(redisDb *db, robj *key) {
    robj *argv[2];
 
    argv[0] = shared.del;
    argv[1] = key;
    incrRefCount(argv[0]);
    incrRefCount(argv[1]);
 
    if (server.aof_state != REDIS_AOF_OFF)
        feedAppendOnlyFile(server.delCommand,db->id,argv,2);
    replicationFeedSlaves(server.slaves,db->id,argv,2);
 
    decrRefCount(argv[0]);
    decrRefCount(argv[1]);
}
 
int expireIfNeeded(redisDb *db, robj *key) {
    mstime_t when = getExpire(db,key);
    mstime_t now;
 
    if (when < 0) return 0; /* No expire for this key */
 
    /* Don't expire anything while loading. It will be done later. */
    if (server.loading) return 0;
 
    /* If we are in the context of a Lua script, we claim that time is
     * blocked to when the Lua script started. This way a key can expire
     * only the first time it is accessed and not in the middle of the
     * script execution, making propagation to slaves / AOF consistent.
     * See issue #1525 on Github for more information. */
    now = server.lua_caller ? server.lua_time_start : mstime();
 
    /* If we are running in the context of a slave, return ASAP:
     * the slave key expiration is controlled by the master that will
     * send us synthesized DEL operations for expired keys.
     *
     * Still we try to return the right information to the caller,
     * that is, 0 if we think the key should be still valid, 1 if
     * we think the key is expired at this time. */
    if (server.masterhost != NULL) return now > when;
 
    /* Return when this key has not expired */
    if (now <= when) return 0;
 
    /* Delete the key */
    server.stat_expiredkeys++;
    propagateExpire(db,key);
    notifyKeyspaceEvent(REDIS_NOTIFY_EXPIRED,
        "expired",key,db->id);
    return dbDelete(db,key);
}
 
/*-----------------------------------------------------------------------------
 * Expires Commands
 *----------------------------------------------------------------------------*/
 
/* This is the generic command implementation for EXPIRE, PEXPIRE, EXPIREAT
 * and PEXPIREAT. Because the commad second argument may be relative or absolute
 * the "basetime" argument is used to signal what the base time is (either 0
 * for *AT variants of the command, or the current time for relative expires).
 *
 * unit is either UNIT_SECONDS or UNIT_MILLISECONDS, and is only used for
 * the argv[2] parameter. The basetime is always specified in milliseconds. */
void expireGenericCommand(redisClient *c, long long basetime, int unit) {
    robj *key = c->argv[1], *param = c->argv[2];
    long long when; /* unix time in milliseconds when the key will expire. */
 
    if (getLongLongFromObjectOrReply(c, param, &when, NULL) != REDIS_OK)
        return;
 
    if (unit == UNIT_SECONDS) when *= 1000;
    when += basetime;
 
    /* No key, return zero. */
    if (lookupKeyRead(c->db,key) == NULL) {
        addReply(c,shared.czero);
        return;
    }
 
    /* EXPIRE with negative TTL, or EXPIREAT with a timestamp into the past
     * should never be executed as a DEL when load the AOF or in the context
     * of a slave instance.
     *
     * Instead we take the other branch of the IF statement setting an expire
     * (possibly in the past) and wait for an explicit DEL from the master. */
    if (when <= mstime() && !server.loading && !server.masterhost) {
        robj *aux;
 
        redisAssertWithInfo(c,key,dbDelete(c->db,key));
        server.dirty++;
 
        /* Replicate/AOF this as an explicit DEL. */
        aux = createStringObject("DEL",3);
        rewriteClientCommandVector(c,2,aux,key);
        decrRefCount(aux);
        signalModifiedKey(c->db,key);
        notifyKeyspaceEvent(REDIS_NOTIFY_GENERIC,"del",key,c->db->id);
        addReply(c, shared.cone);
        return;
    } else {
        setExpire(c->db,key,when);
        addReply(c,shared.cone);
        signalModifiedKey(c->db,key);
        notifyKeyspaceEvent(REDIS_NOTIFY_GENERIC,"expire",key,c->db->id);
        server.dirty++;
        return;
    }
}
 
void expireCommand(redisClient *c) {
    expireGenericCommand(c,mstime(),UNIT_SECONDS);
}
 
void expireatCommand(redisClient *c) {
    expireGenericCommand(c,0,UNIT_SECONDS);
}
 
void pexpireCommand(redisClient *c) {
    expireGenericCommand(c,mstime(),UNIT_MILLISECONDS);
}
 
void pexpireatCommand(redisClient *c) {
    expireGenericCommand(c,0,UNIT_MILLISECONDS);
}
 
void ttlGenericCommand(redisClient *c, int output_ms) {
    long long expire, ttl = -1;
 
    /* If the key does not exist at all, return -2 */
    if (lookupKeyRead(c->db,c->argv[1]) == NULL) {
        addReplyLongLong(c,-2);
        return;
    }
    /* The key exists. Return -1 if it has no expire, or the actual
     * TTL value otherwise. */
    expire = getExpire(c->db,c->argv[1]);
    if (expire != -1) {
        ttl = expire-mstime();
        if (ttl < 0) ttl = 0;
    }
    if (ttl == -1) {
        addReplyLongLong(c,-1);
    } else {
        addReplyLongLong(c,output_ms ? ttl : ((ttl+500)/1000));
    }
}
 
void ttlCommand(redisClient *c) {
    ttlGenericCommand(c, 0);
}
 
void pttlCommand(redisClient *c) {
    ttlGenericCommand(c, 1);
}
 
void persistCommand(redisClient *c) {
    dictEntry *de;
 
    de = dictFind(c->db->dict,c->argv[1]->ptr);
    if (de == NULL) {
        addReply(c,shared.czero);
    } else {
        if (removeExpire(c->db,c->argv[1])) {
            addReply(c,shared.cone);
            server.dirty++;
        } else {
            addReply(c,shared.czero);
        }
    }
}
```

但仅是这样是不够的，因为可能存在一些key永远不会被再次访问到，这些设置了过期时间的key也是需要在过期后被删除的，我们甚至可以将这种情况看作是一种内存泄露----无用的垃圾数据占用了大量的内存，而服务器却不会自己去释放它们，这对于运行状态非常依赖于内存的Redis服务器来说，肯定不是一个好消息

### 主动删除

先说一下时间事件，对于持续运行的服务器来说， 服务器需要定期对自身的资源和状态进行必要的检查和整理， 从而让服务器维持在一个健康稳定的状态， 这类操作被统称为常规操作（cron job）

在 Redis 中， 常规操作由 `redis.c/serverCron` 实现， 它主要执行以下操作

- 更新服务器的各类统计信息，比如时间、内存占用、数据库占用情况等。
- 清理数据库中的过期键值对。
- 对不合理的数据库进行大小调整。
- 关闭和清理连接失效的客户端。
- 尝试进行 AOF 或 RDB 持久化操作。
- 如果服务器是主节点的话，对附属节点进行定期同步。
- 如果处于集群模式的话，对集群进行定期同步和连接测试。

Redis 将 `serverCron` 作为时间事件来运行， 从而确保它每隔一段时间就会自动运行一次， 又因为 `serverCron` 需要在 Redis 服务器运行期间一直定期运行， 所以它是一个循环时间事件： `serverCron` 会一直定期执行，直到服务器关闭为止。

在 Redis 2.6 版本中， 程序规定 `serverCron` 每秒运行 `10` 次， 平均每 `100` 毫秒运行一次。 从 Redis 2.8 开始， 用户可以通过修改 `hz`选项来调整 `serverCron` 的每秒执行次数， 具体信息请参考 `redis.conf` 文件中关于 `hz` 选项的说明

------



也叫定时删除，这里的“定期”指的是Redis定期触发的清理策略，由位于src/redis.c的activeExpireCycle(void)函数来完成。

serverCron是由redis的事件框架驱动的定位任务，这个定时任务中会调用activeExpireCycle函数，针对每个db在限制的时间REDIS_EXPIRELOOKUPS_TIME_LIMIT内迟可能多的删除过期key，之所以要限制时间是为了防止过长时间 的阻塞影响redis的正常运行。这种主动删除策略弥补了被动删除策略在内存上的不友好。

因此，Redis会周期性的随机测试一批设置了过期时间的key并进行处理。测试到的已过期的key将被删除。典型的方式为,Redis每秒做10次如下的步骤：

- 随机测试100个设置了过期时间的key
- 删除所有发现的已过期的key
- 若删除的key超过25个则重复步骤1

这是一个基于概率的简单算法，基本的假设是抽出的样本能够代表整个key空间，redis持续清理过期的数据直至将要过期的key的百分比降到了25%以下。这也意味着在任何给定的时刻已经过期但仍占据着内存空间的key的量最多为每秒的写操作量除以4.

Redis-3.0.0中的默认值是10，代表每秒钟调用10次后台任务。 

除了主动淘汰的频率外，Redis对每次淘汰任务执行的最大时长也有一个限定，这样保证了每次主动淘汰不会过多阻塞应用请求，以下是这个限定计算公式：

```sh
#define ACTIVE_EXPIRE_CYCLE_SLOW_TIME_PERC 25 /* CPU max % for keys collection */ 
... 
timelimit = 1000000*ACTIVE_EXPIRE_CYCLE_SLOW_TIME_PERC/server.hz/100;
```

hz调大将会提高Redis主动淘汰的频率，如果你的Redis存储中包含很多冷数据占用内存过大的话，可以考虑将这个值调大，但Redis作者建议这个值不要超过100。我们实际线上将这个值调大到100，观察到CPU会增加2%左右，但对冷数据的内存释放速度确实有明显的提高（通过观察keyspace个数和used_memory大小）。 

可以看出timelimit和server.hz是一个倒数的关系，也就是说hz配置越大，timelimit就越小。换句话说是每秒钟期望的主动淘汰频率越高，则每次淘汰最长占用时间就越短。这里每秒钟的最长淘汰占用时间是固定的250ms（1000000*ACTIVE_EXPIRE_CYCLE_SLOW_TIME_PERC/100），而淘汰频率和每次淘汰的最长时间是通过hz参数控制的。 
从以上的分析看，当redis中的过期key比率没有超过25%之前，提高hz可以明显提高扫描key的最小个数。假设hz为10，则一秒内最少扫描200个key（一秒调用10次*每次最少随机取出20个key），如果hz改为100，则一秒内最少扫描2000个key；另一方面，如果过期key比率超过25%，则扫描key的个数无上限，但是cpu时间每秒钟最多占用250ms。 

当REDIS运行在主从模式时，只有主结点才会执行上述这两种过期删除策略，然后把删除操作”del key”同步到从结点。

### maxmemory

当前已用内存超过maxmemory限定时，触发**主动清理**策略

- volatile-lru：只对设置了过期时间的key进行LRU(最近最少使用)（默认值）
- allkeys-lru ： 删除lru算法的key
- volatile-random：随机删除即将过期key
- allkeys-random：随机删除
- volatile-ttl ： 删除即将过期的
- noeviction ： 永不过期，返回错误当mem_used内存已经超过maxmemory的设定，对于所有的读写请求，都会触发redis.c/freeMemoryIfNeeded(void)函数以清理超出的内存。注意这个清理过程是阻塞的，直到清理出足够的内存空间。所以如果在达到maxmemory并且调用方还在不断写入的情况下，可能会反复触发主动清理策略，导致请求会有一定的延迟。 

当mem_used内存已经超过maxmemory的设定，对于所有的读写请求，都会触发redis.c/freeMemoryIfNeeded(void)函数以清理超出的内存。注意这个清理过程是阻塞的，直到清理出足够的内存空间。所以如果在达到maxmemory并且调用方还在不断写入的情况下，可能会反复触发主动清理策略，导致请求会有一定的延迟。

清理时会根据用户配置的maxmemory-policy来做适当的清理（一般是LRU或TTL），这里的LRU或TTL策略并不是针对redis的所有key，而是以配置文件中的maxmemory-samples个key作为样本池进行抽样清理。

maxmemory-samples在redis-3.0.0中的默认配置为5，如果增加，会提高LRU或TTL的精准度，redis作者测试的结果是当这个配置为10时已经非常接近全量LRU的精准度了，并且增加maxmemory-samples会导致在主动清理时消耗更多的CPU时间，建议：

- 尽量不要触发maxmemory，最好在mem_used内存占用达到maxmemory的一定比例后，需要考虑调大hz以加快淘汰，或者进行集群扩容。
- 如果能够控制住内存，则可以不用修改maxmemory-samples配置；如果Redis本身就作为LRU cache服务（这种服务一般长时间处于maxmemory状态，由Redis自动做LRU淘汰），可以适当调大maxmemory-samples。

以下是上文中提到的配置参数的说明

```properties
# Redis calls an internal function to perform many background tasks, like 
# closing connections of clients in timeout, purging expired keys that are 
# never requested, and so forth. 
# 
# Not all tasks are performed with the same frequency, but Redis checks for 
# tasks to perform according to the specified "hz" value. 
# 
# By default "hz" is set to 10. Raising the value will use more CPU when 
# Redis is idle, but at the same time will make Redis more responsive when 
# there are many keys expiring at the same time, and timeouts may be 
# handled with more precision. 
# 
# The range is between 1 and 500, however a value over 100 is usually not 
# a good idea. Most users should use the default of 10 and raise this up to 
# 100 only in environments where very low latency is required. 
hz 10 
 
# MAXMEMORY POLICY: how Redis will select what to remove when maxmemory 
# is reached. You can select among five behaviors: 
# 
# volatile-lru -> remove the key with an expire set using an LRU algorithm 
# allkeys-lru -> remove any key according to the LRU algorithm 
# volatile-random -> remove a random key with an expire set 
# allkeys-random -> remove a random key, any key 
# volatile-ttl -> remove the key with the nearest expire time (minor TTL) 
# noeviction -> don't expire at all, just return an error on write operations 
# 
# Note: with any of the above policies, Redis will return an error on write 
#       operations, when there are no suitable keys for eviction. 
# 
#       At the date of writing these commands are: set setnx setex append 
#       incr decr rpush lpush rpushx lpushx linsert lset rpoplpush sadd 
#       sinter sinterstore sunion sunionstore sdiff sdiffstore zadd zincrby 
#       zunionstore zinterstore hset hsetnx hmset hincrby incrby decrby 
#       getset mset msetnx exec sort 
# 
# The default is: 
# 
maxmemory-policy noeviction 
 
# LRU and minimal TTL algorithms are not precise algorithms but approximated 
# algorithms (in order to save memory), so you can tune it for speed or 
# accuracy. For default Redis will check five keys and pick the one that was 
# used less recently, you can change the sample size using the following 
# configuration directive. 
# 
# The default of 5 produces good enough results. 10 Approximates very closely 
# true LRU but costs a bit more CPU. 3 is very fast but not very accurate. 
# 
maxmemory-samples 5
```



### Replication link和AOF文件中的过期处理

为了获得正确的行为而不至于导致一致性问题，当一个key过期时DEL操作将被记录在AOF文件并传递到所有相关的slave。也即过期删除操作统一在master实例中进行并向下传递，而不是各salve各自掌控。这样一来便不会出现数据不一致的情形。当slave连接到master后并不能立即清理已过期的key（需要等待由master传递过来的DEL操作），slave仍需对数据集中的过期状态进行管理维护以便于在slave被提升为master会能像master一样独立的进行过期处理。





https://www.cnblogs.com/chenpingzhao/p/5022467.html