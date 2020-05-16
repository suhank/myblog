[TOC]



# 数据库对比：选择MariaDB还是MySQL？

InfoQ 2018-09-06 11:48:25

作者 | EverSQL

译者 | 无明

这篇文章的目的主要是比较 MySQL 和 MariaDB 之间的主要相似点和不同点。我们将从性能、安全性和主要功能方面对这两个数据库展开对比，并列出在选择数据库时需要考虑的重要事项。

# 谁在使用 MySQL 和 MariaDB？

MySQL 和 MariaDB 都发布了各自的用户名单。

使用 MySQL 的有 Facebook、Github、YouTube、Twitter、PayPal、诺基亚、Spotify、Netflix 等。

使用 MariaDB 的有 Redhat、DBS、Suse、Ubuntu、1＆1、Ingenico 等。

# 功能比较

有一些令人兴奋的新功能（如窗口函数、角色控制或公共表表达式（CTE））可能值得一提，但本文只是为了比较两个数据库，所以我们在这里只讨论其中一方专门提供的功能，以便更好地帮助读者选择合适自己的数据库。

让我们来看一下只有其中一个数据库专门提供的功能：

**1. JSON 数据类型**——从 5.7 版本开始，MySQL 支持由 RFC 7159 定义的原生 JSON 数据类型，可以高效地访问 JSON 文档中的数据。

MariaDB 没有提供这一增强功能，认为 JSON 数据类型不是 SQL 标准的一部分。但为了支持从 MySQL 复制数据，MariaDB 为 JSON 定义了一个别名，实际上就是一个 LONGTEXT 列。MariaDB 声称两者之间没有显著的性能差异，但他们并没有提供基准测试数据来支持这个说法。

值得注意的是，MySQL 和 MariaDB 都提供了一些 JSON 相关函数，用于更方便地访问、解析和检索 JSON 数据。

**2. 默认身份认证**——在 MySQL 8.0 中，默认的身份认证插件是 caching_sha2_password，而不是 mysql_native_password。这一增强通过使用 SHA-256 算法提高了安全性。

**3. MySQL Shell**——MySQL Shell 是 MySQL 的高级命令行客户端和代码编辑器。除了 SQL 之外，MySQL Shell 还提供了 JavaScript 和 Python 脚本功能。不过用户不能使用 mysqlsh 访问 MariaDB 服务器，因为 MariaDB 不支持 MySQL X 协议。

**4. 加密**——MySQL 对重做 / 撤消日志进行了加密（可配），但不加密临时表空间或二进制日志。相反，MariaDB 支持二进制日志和临时表加密。

**5. 密钥管理**——MariaDB 提供开箱即用的 AWS 密钥管理插件。MySQL 也提供了一些用于密钥管理的插件，但它们仅在企业版中可用。

**6. sys 模式**——MySQL 8.0 提供了 sys 模式，这是一组对象，可帮助数据库管理员和软件工程师更好地理解通过 Performance 模式收集的数据。sys 模式对象可用于优化和诊断，不过 MariaDB 没有提供这个增强功能。

**7. validate_password 插件**——validate_password 插件主要用于测试密码并提高安全性。MySQL 默认启用了这个插件，而 MariaDB 则不启用。

**8. 超级只读**—— MySQL 通过提供超级只读（super read-only）模式来增强 read_only 功能。如果启用了 read_only，服务器只允许具有 SUPER 权限的用户执行客户端更新。如果同时启用了 super_read_only，那么服务器将禁止具有 SUPER 权限的用户执行客户端更新。

**9. 不可见列**——这个功能在 MariaDB 上可用，MySQL 不支持该功能。这个功能允许创建未在 SELECT * 语句中出现的列，而在进行插入时，如果它们的名字没有出现在 INSERT 语句中，就不需要为这些列提供值。

**10. 线程池**——MariaDB 支持连接线程池，这对于短查询和 CPU 密集型的工作负载（OLTP）来说非常有用。在 MySQL 的社区版本中，线程数是固定的，因而限制了这种灵活性。MySQL 计划在企业版中增加线程池功能。

# 性能

近年来，出现了很多关于 MySQL 和 MariaDB 引擎性能的基准测试。我们不认为“MySQL 或 MariaDB 哪个更快”这个问题会有一个最终的答案，它在很大程度上取决于具体的使用场景、查询、用户和连接数量等因素。

不过，如果你确实想知道，下面列出了我们发现的一些最新的基准测试结果。请注意，这些测试都是在一组特定的数据库 + 引擎（例如 MySQL+InnoDB）组合上进行的，因此得出的结论只与特定的组合有关。

- MySQL 8.0（InnoDB）和 MariaDB 10.3.7（MyRocks）基准测试对比：
- https://minervadb.com/index.php/2018/06/01/benchmarking-innodb-and-myrocks-performance-using-sysbench/
- MariaDB 10.1 和 MySQL 5.7 在商用硬件上的性能对比：
- https://mariadb.org/maria-10-1-mysql-5-7-commodity-hardware/
- MySQL 8.0 和 MariaDB 10.3.5 性能对比及 UTF8 的影响：
- http://dimitrik.free.fr/blog/archives/2018/04/mysql-performance-80-and-utf8-impact.html

# 复制

两个数据库都提供了将数据从一个服务器复制到另一个服务器的功能。它们的主要区别是大多数 MariaDB 版本允许你从 MySQL 复制数据，这意味着你可以轻松地将 MySQL 迁移到 MariaDB。但反过来却没有那么容易，因为大多数 MySQL 版本都不允许从 MariaDB 复制数据。

此外，值得注意的是，MySQL GTID 不同于 MariaDB GTID，所以将数据从 MySQL 复制到 MariaDB 后，GTID 数据将相应地做出调整。

以下是这两个数据库在复制配置方面的一些差别：

- MySQL 的默认二进制日志格式是基于行的，而在 MariaDB 中，默认的二进制日志格式是混合式的。
- log_bin_compress——这个配置决定了是否可以压缩二进制日志。这个增强功能是 MariaDB 独有的，因此 MySQL 不支持。

# 两者之间的不兼容性

MariaDB 的文档中列出了 MySQL 和 MariaDB 之间的数百个不兼容问题。因此，我们无法通过简单的方案在这两个数据库之间进行迁移。

大多数数据库管理员都希望 MariaDB 只是作为 MySQL 的一个 branch，这样就可以轻松地在两者之间进行迁移。但从最新发布的几个版本来看，这种想法是不现实的。MariaDB 实际上是 MySQL 的一个 fork，这意味着在它们之间进行迁移需要考虑很多东西。

# 存储引擎

MariaDB 比 MySQL 支持更多的存储引擎类型。但话说回来，数据库可以支持多少个存储引擎并不重要，重要的是哪个数据库可以支持适合你需求的存储引擎。

- MariaDB 支持的存储引擎包括：XtraDB、InnoDB、MariaDB ColumnStore、Aria、Archive、Blackhole、Cassandra Storage Engine、Connect、CSV、FederatedX、Memory、Merge、Mroonga、MyISAM、MyRocks、QQGraph、Sequence Storage Engine、SphinxSE、Spider、TokuDB。
- MySQL 支持的存储引擎包括：InnoDB、MyISAM、Memory、CSV、Archive、Blackhole、Merge、Federated、Example。

# 在 Linux 上安装

当你在某些 Linux 发行版上安装 MySQL 时，最后可能安装的是 MariaDB，因为它是很多（不是全部）Linux 发行版的默认设置。

Red Hat Enterprise/CentOS/Fedora/Debian 发行版默认会安装 MariaDB，而其他发行版（如 Ubuntu）默认安装 MySQL。

# 云平台上的可用性

MariaDB 可作为运行在 Amazon Web Services（AWS）、微软 Azure 和 Rackspace Cloud 上的服务。

MySQL 在上面提到的三个平台上也是可用的，同时还可以作为托管服务在谷歌云服务平台上运行。

因此，如果你正在使用谷歌云平台，并希望云提供商为你管理服务，那么可以考虑使用 MySQL，除非你希望自己安装和管理 MariaDB 实例。

# 许可

MariaDB 采用了 GPL v2 许可，而 MySQL 提供了两个许可选项——GPL v2（用于社区版）和企业许可。

MySQL 的两个许可之间的主要区别在于可用的功能和支持服务。用户可以使用 MariaDB 的所有功能，但对于 MySQL 来说并非如此。MySQL 的社区版不包含线程池等功能，而这些功能会对数据库和查询性能产生重大影响。

# 发布频率和更新

通常，MariaDB 的发布频率比 MySQL 更频繁。太高的发布频率既有利也有弊。从好的方面来说，用户可以更及时地收到功能和错误修复。从不好的方面来说，为了让 MariaDB 保持最新的状态，需要更多的工作量。

# 技术支持

MySQL 的支持团队（包括 MySQL 开发人员和支持工程师）为客户提供全天候服务。甲骨文提供了多种支持选项，包括扩展支持、持续支持和高级支持，具体取决于客户的要求。MariaDB 支持团队的支持工程师包括了 MariaDB 和 MySQL 数据库专家（因为很多功能最初是由 MySQL 团队开发的），他们为生产系统提供全天候的企业级支持。

# 正在进行中的开发

MySQL 的开发者主要是甲骨文的 MySQL 团队，而 MariaDB 开发通过公开投票和邮件列表讨论的方式进行。此外，任何人都可以向 MariaDB 提交补丁，MariaDB 开发团队会考虑将这些补丁添加到主代码库中。因此，从某种程度上说，MariaDB 是由社区开发的，而 MySQL 主要由甲骨文开发。

# 结论

好吧，我们无法为你做出决定。我们能做的就是有针对性地问你一些问题，然后你自己做出决定：

- 你是否分别基于这两个数据库对你的产品性能做过测试？哪一个表现更好，为什么？
- 你是否打算使用其中一个数据库专门提供的功能？
- 你是否打算使用其中一个数据库专门提供的数据库引擎？
- 能够对数据库的开发过程产生影响对你来说有多重要？能够参与下一个功能变更投票对你来说有多重要？
- 你是要为企业版本付费还是使用社区版？社区版的功能是否能够满足你的需求？
- 你的操作系统是否默认支持你所选的数据库？要部署它需不需要很多工作量？
- 你使用的是哪个云提供商？他们是否提供托管服务，其中包括你选择的数据库？
- 你是否计划将来从一种数据库类型迁移到另一种数据库类型？如果是这样，你是否考虑过兼容性和复制方面的问题？

**如果你能回答好这些问题，可能就很清楚哪个数据库更适合你。**

英文原文

https://www.eversql.com/mariadb-vs-mysql/



https://www.toutiao.com/a6597927247489794568/?tt_from=android_share&utm_campaign=client_share&timestamp=1536279139&app=news_article&iid=43398557445&utm_medium=toutiao_android&group_id=6597927247489794568