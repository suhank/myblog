[TOC]



# 关于 Spring 事务注解的几个不能忽视的细节

 

2017.10.20 16:14* 字数 3637 阅读 2233评论 2喜欢 20

# 前言

**对于 Java 后端开发人员，Spring 事务注解几乎天天都会接触。但是，你真的全部了解 Spring 事务注解的细节吗？今天我们就来深入讨论一下 Spring 事务注解中回滚、传播行为和只读这三个属性的配置调优。**

希望通过本文能让大家了解更多和数据库事务相关的框架，以及数据库引擎的内部原理，为大家的数据库优化工作提供一些有用的建议。

# 细节一：为什么要配置 rollbackFor = Exception.class

## 一、rollbackFor 的作用

最近，阿里将其编码规范按照 IDE 插件的方式发布。插件检查结果中有这么一条：“Spring 事务需要设置 rollbackFor 属性或者显式调用 rollback 方法”。为什么需要设置 rollbackFor 属性呢？

**简单来说，如果不设置 rollbackFor = Exception.class，则当方法抛出检查型异常时，数据库操作不会回滚。**

举例来说，对于下面的代码：

```
@Transactional
public void demo() throws Exception {
    this.userRepository.save(new User(USERNAME));
    throw new Exception("No Rollback");
}
```

执行完毕之后，数据库中会增加一条 user 记录。如果你希望在方法抛出检查型异常后触发数据库回滚，那你需要这样写：

```
@Transactional(rollbackFor = Exception.class)
public void demo() throws Exception {
    this.userRepository.save(new User(USERNAME));
    throw new Exception("No Rollback");
}
```

或者使用 `TransactionStatus.setRollbackOnly()` 手动触发回滚：

```
@Transactional
public void demo() {
    this.userRepository.save(new User(USERNAME));
    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
}
```

如果你的方法没有声明异常，只会抛出运行时异常，则不需要设置 rollbackFor。运行时异常抛出后会触发事务回滚。

```
@Transactional
public void demo() {
    this.userRepository.save(new User(USERNAME));
    throw new RuntimeException("Rollback");
}
```

如果代码如上所示，则不会有新的 user 记录。因为数据库操作回滚了。

## 二、需不需要配置 rollbackFor

因为如果方法会抛出检查型异常，则必须在方法中声明异常。因此，一般而言，如果方法没有声明异常，则不需要配置在 Spring 事务注解中配置 rollbackFor。

另外，我在阿里的 Java 编码规范里也没有找到关于设置 rollbackFor 的内容。因此，我的个人意见是方法不抛出检查型异常就不必设置 rollbackFor。

## 三、Spring 为何这么设计

对于 Spring 为什么默认不回滚检查型异常这个问题，我想是和 Java 异常处理的原则有关。原则上，检查型异常都是业务异常流的一部分，需要开发人员定制开发相应的异常处理功能。既然需要开发人员定制，自然 Spring 认为自己不必画蛇添足了，自动地回滚数据库操作。

## 四、二般的情况

前面提到，一般情况下，方法不声明异常，就不需要配置 Spring 事务注解的 rollbackFor。但是有没有“二般”的情况呢？答案是有的。

原因在于 Java 检查型异常的实现原理。其实 Java 里面必须 try...catch 检查型异常完全是编译器检查的结果。其实可以通过一些黑科技让代码即抛出检查型异常，同时也不必 try...catch。

当然，一般开发人员不会主动用这些黑科技。但这不代表不会间接使用。一个常见的导致没有捕获检查型异常的原因是使用非 Java 的 JVM 语言。

例如，在 Groovy 里面，你可以这么写：

```
@Component
class GroovyDemoService {
    void withException() {
        throw new Exception("An expected exception")
    }
}
```

这么写完全是没问题的，因为 Groovy 为了简化异常使用，已经抛弃了 Java 检查型异常的设计。通常，这没什么问题。但如果这个方法在使用 Spring 事务注解声明的方法时，那就不会导致事务回滚，除非定义 `rollbackFor = Exception.class`。

那怎么避免这样的问题呢？我的一个建议是尽量使用 `RuntimeException`。使用运行时异常可以简化代码，也可以避免上述事务相关的问题。而 Spring 中定义的各种异常，绝大多数也都是运行时异常。

# 细节二：Propagation.SUPPORTS 有什么效果

## 一、Propagation.SUPPORTS 是干什么的？

在 Spring 事务的传播级别配置中，有一个选择是 `Propagation.SUPPORTS`。这个选择是什么意思呢？简单说就是在单独执行时（外层没有事务），这个方法将以非事务的方式执行。进一步地说，当 Spring 事务的传播级别被设置为 `Propagation.SUPPORTS` 时，当前 JDBC Connection 就不会配置为 `autocommit=0`，从而也就没有开启事务。

> Support a current transaction, execute non-transactionally if none exists. Analogous to EJB transaction attribute of the same name. Note: For transaction managers with transaction synchronization, PROPAGATION_SUPPORTS is slightly different from no transaction at all, as it defines a transaction scope that synchronization will apply for. As a consequence, the same resources (JDBC Connection, Hibernate Session, etc) will be shared for the entire specified scope. Note that this depends on the actual synchronization configuration of the transaction manager.

## 二、要不要配置 Propagation.SUPPORTS？

在知道了 `Propagation.SUPPORTS` 所产生的效果之后，我们来看一下是否需要配置 `Propagration.SUPPORTS`，以及什么时候配置。

有一些数据库相关的优化建议提到，**可以将 Spring 的事务配置为 Propagration.SUPPORTS，这样可以提高方法执行速度。原因在于，将一个方法的事务配置为 Propagration.SUPPORTS 后，如果单独调用这个方法，那这个方法就是按非事务的方式执行了。没有事务岂不是更快！**

对于上述优化建议，我要说，**结论不完全正确，原因完全不正确。**

### 1. 反对使用 Propagation.SUPPORTS 的意见

我们先来看看对这个优化意见的反面看法。在 Spring JIRA 上也有人提出 Spring Data JPA 应该按照这样（将事务默认传播行为配置为 `Propagration.SUPPORTS`）的方式修改默认行为，以提高性能：[https://jira.spring.io/browse/DATAJPA-601](https://link.jianshu.com/?t=https://jira.spring.io/browse/DATAJPA-601)。

但是，在这个帖子中，Spring Data 项目的主管 Oliver Gierke 回答说“没有必要使用这种手段优化”。原因有二：

1. 没有证据表明这样的方式能提高性能
2. 默认的行为有很多优化手段

而在 Hibernate 文档中，也对无事务的数据访问和 auto-commit 模式做了解释：[https://developer.jboss.org/wiki/Non-transactionalDataAccessAndTheAuto-commitMode](https://link.jianshu.com/?t=https://developer.jboss.org/wiki/Non-transactionalDataAccessAndTheAuto-commitMode)

文中提到，有开发人员认为数据库操作可以以非事务的方式进行，但这是不可能的。**所谓的非事务数据库访问只是没有显式的事务边界而已，数据库操作只是 auto-commit 的方式，其实还是有事务的。**

**进一步，如果数据操作以“非事务”方式进行，这便意味着在一个业务功能中，多个小的数据库事务被开启并关闭。这反而不利于性能，不如用一个完整的事务替代。**

**在伸缩性方面，Hibernate 这样的 ORM 框架对于写锁有优化，持续时间已经很短了。而读锁相对的性能消耗可以忽略。所以，在伸缩性方面，小事务不存在什么优势。**

上面这些用来支持反对意见的证据，都没什么错。确实，开启 auto-commit 之后，事务还是有，只是变为了单语句级别。所以，不要认为 `Propagration.SUPPORTS` 之后就没有事务了。至少，如果把数据库的范围限定为使用 InnoDB 引擎的 MySQL，那`Propagration.SUPPORTS` 并不会使数据库操作都以非事务的方式执行。

### 2. InnoDB 只读事务优化

在这里，剧情再次反转一下。因为对于多数互联网应用来说，数据库通常是 MySQL，搭配 InnoDB 引擎。从 MySQL 5.6 版本开始，InnoDB 增加了对只读事务的优化。这里有比较详细的描述 [https://dev.mysql.com/doc/refman/5.6/en/innodb-performance-ro-txn.html](https://link.jianshu.com/?t=https://dev.mysql.com/doc/refman/5.6/en/innodb-performance-ro-txn.html)。

简单来说，InnoDB 引擎可以避免为只读事务创建事务 ID，进而避免多余的事务和锁操作。那 InnoDB 什么时候会将一个事务视为只读事务呢？有两个情况：

1. 通过 `START TRANSACTION READ ONLY` 显式开启只读事务时；
2. 如果 `autocommit=1`，当调用一个普通的 `select` 操作（没有 `FOR UPDATE/LOCK` 的 `select` 语句）时；

对于第一点，因为 JDBC 开启事务并不是通过 `START TRANSACTION` 语句，所以这种方式与多数 Java 应用无关。但 Java 可以从第二点优化。当我们执行查询操作时，如果将当前连接设置为 `autocommit=1`，那 InnoDB 便会对这次查询操作进行只读事务优化。我们如何做呢？如果使用了 Spring，那将事务设置为 `Propagation.SUPPORTS` 即可。

### 3. 小结

将 Spring 事务的传播行为配置为 `Propagation.SUPPORTS` 并不意味着数据库不会以事务的方式执行语句，只是将事务细化到每个语句级别。但是，当使用 MySQL + InnoDB （MySQL 版本大于等于 5.6）执行查询操作时，因为 InnoDB 只读事务优化机制，所以将 Spring 事务设置为 `Propagation.SUPPORTS` 会得到更好的性能。

## 三、延伸：使用大事务的注意事项

上一段提到用大事务比一个个小的事务要更好，这么说自然很笼统。大事务有大事务的问题。我们一般而言，大事务是指在业务层（例如 Service 层）的方法上定义事务。

但是在微服务架构流行的今天，业务层方法中越来越多地引入了各种远程调用。例如，HTTP、RPC、MQ、缓存等等。这种情况下出现了一个问题就是，这些远程调用的超时问题，往往会导致数据库事务时间变长，从而导致数据库相关的种种问题。

解决这一问题有两个方法：

### 方法一：事务下沉

将原本定义在 Service 层的事务下沉到 Repository 层（这里的 Repository 层是领域驱动设计中的一个概念，这里不做过多阐述）。在 Repository 层中只有数据持久化的工作，避免了远程调用对数据库事务的影响。当然，这会导致一致性的问题。同样，因为篇幅的问题，不在这里深入讨论。

### 方法二：熔断降级保护

方法一可能会导致系统层次增加、方法粒度过细等一些问题。如果这些问题导致了代码复杂，可读性下降等问题，可以考虑使用熔断降级的方法，避免远程调用超时时间过长，从而导致影响波及数据库事务的问题。

两种都一定程度解决在微服务架构下大事务导致的问题。一般而言，并发高的业务偏向采用第一种方法，第二种方法可以用在偏向内部系统的场景中使用。当然，这只是大致的建议，更具体的场景还需具体情况具体分析。

# 细节三：readOnly = true 有什么效果

除了事务的传播级别，Spring 事务中另一个常见的配置是只读属性。`@Transactional(readOnly = true)` 便意味着这个事务是只读的。那当我们这么配置的时候，框架和数据库底层究竟做了哪些事？

JDBC 的 `Connection` 接口有一个 `setReadOnly(boolean readOnly)` 方法。当我们将 Spring 事务配置为 `readOnly = true` 时，这个方法便会被调用。在 MySQL 的 JDBC 驱动中，这个方法会向 MySQL 数据库下发 `set session transaction read only` 调用。这个有什么效果吗？MySQL 文档中并没有明确的说明。所以，具体的效果只能通过测试说话。（有哪位同学找到了相应的文档，或者阅读过 MySQL 的源码，可以帮忙解释一下）

# 测试和总结

说了这么多，我们来总结一下。对于 Spring 事务的 `rollbackFor` 配置，我建议择情选择。重点说一下和性能相关的两个细节问题，传播级别和只读事务。

前面说了，当使用 MySQL 5.6 + InnoDB 时，正确开启只读事务的姿势是将 Spring 事务的传播行为配置为 `Propagation.SUPPORTS`，而不是设置 `readOnly = true`。

当然，上面一直是在进行理论层面的讨论，实际如何呢？

经过测试，当使用 Spring Boot 1.5.8、JPA、MySQL 5.6.26 + InnoDB，按照 500 QPS 进行1万次基于索引的查询。得到的每次查询耗时为

- SUPPORTS + readOnly: 每次查询耗时约 3 ms
- 仅 SUPPORTS: 每次查询耗时大约 3 ms
- 默认事务配置（不加 SUPPORTS 和 readOnly）: 每次查询耗时大约 10 ms
- 仅 readOnly: 每次查询耗时大约 17 ms

所以，**从测试结果来看，当时用 MySQL 5.6 + InnoDB 时，为 Spring 事务配置 Propagation.SUPPORTS 对读操作的性能提升最大；readOnly = true 不但没有性能提升，反而会造成性能下降，原因可能和额外的 set session transaction read only 操作有关。**

因为 Spring、Hibernate、JDBC 等技术面向的广泛的数据库技术，所以并不会针对 MySQL + InnoDB 做专门的优化配置。但是，作为一个企业或一个团队，可以自主选择优化方向。如果有必要将上述优化建议落地普及，可以考虑使用自定义 Spring 事务注解，修改其默认传播行为等属性，避免开发人员按照不合适的方式进行配置。关于如何自定义 Spring 注解，就不在这里过多阐述了。





https://www.jianshu.com/p/ec899855f049