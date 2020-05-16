[TOC]



# xxl-job的分片机制

最后发布于2019-12-05 10:41:46

## 分片概念：

任务的分布式执行，需要将一个任务拆分为多个独立的任务项，然后由分布式的服务器分别执行某一个或几个分片项。

注意：

分片参数是调度中心自动传递的，不用我们手动传递，且集群中的每个index序号是固定的，即使集群中有项目宕机，也不影响其他项目的index序号，当重启宕机项目时，它的序号还是原先的。

## 分片方案1：

```java
获取总分片数和当前分片代码：
//获取分片 根据配置的机器数量和获得的分片拿去对应的数据
ShardingUtil.ShardingVO shardingVO = ShardingUtil.getShardingVo();
//执行器数量
int number = shardingVO.getTotal();
//当前分片在这里插入代码片
int index = shardingVO.getIndex();
sql每次从表中取100条数据：
SELECT id,name,password
FROM t_push
WHERE status = 0
AND mod(id,#{number}) = #{index}  //number 分片总数，index当前分片数 mod函数取余
order by id desc
LIMIT 100;
```

原理

利用分片任务的分配总数和当前分片数巧妙实现了该功能，主要是表的id是自增的，用该id的值对总分片数进行求余，求余后的数正好等于应用的当前分片数，巧妙的实现了该分布式任务，记录一下。

注意: 

**这个只有id是连续递增的才能分片均匀**

## 方案2(推荐)

```java
//分片参数ShardingVO shardingVO = ShardingUtil.getShardingVo();
WearUserQuery wearUserQuery = new WearUserQuery();
wearUserQuery.createCriteria().andFirstCommunicationTimeIsNotNull();
List<WearUser> wearUsers = wearUserService.selectWearUserByQuery(wearUserQuery);
int i = wearUsers.size();
for (int j = 0; j < i; j++) {
    //对数据下标以当前分片取模选取处理
    if (j % shardingVO.getTotal() == shardingVO.getIndex()) {
        //do something
    }
}
```

这个支持id不是连续递增的





原文链接：https://blog.csdn.net/zlc521520/article/details/103400023