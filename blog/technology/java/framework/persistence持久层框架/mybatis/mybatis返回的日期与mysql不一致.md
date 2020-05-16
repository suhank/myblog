[TOC]



# mybatis返回的日期与mysql不一致

## 问题描述

测试接口时发现，接口返回的时间跟sql查询出来的时间早了10个小时，很纳闷，SQL语句没有问题，程序也没有任务问题，那么问题出在哪儿了呢？

经过各种百度查询发现是时区的问题，解决办法就是在jdbc.url后面添加上**serverTimezone=Asia/Shanghai**即可。

代码如下：

```
jdbc.url=jdbc:mysql://127.0.0.1:3306/test?characterEncoding=UTF-8&useUnicode=true&allowMultiQueries=true&serverTimezone=Asia/Shanghai
```

东八区中，中国内的时区分别是：重庆、上海、乌鲁木齐、澳门、香港、台湾

```
Asia/Chongqing
Asia/Shanghai
Asia/Urumqi
Asia/Macao
Asia/Hong_Kong
Asia/Taipei
```

然后突然很好奇，为什么东八区里为啥没有北京这个时区。于是又接着各种搜索。。。

以下内容属于转载热心贴主的帖子，附上链接

https://www.cnblogs.com/zhengyun_ustc/archive/2009/01/16/asia_beijing_timezone.html

## [为何没有asia/beijing时区？](https://www.cnblogs.com/softidea/p/6939925.html)

 

**Asia/Beijing** 这个时区是消失了么？ [大约1小时 ago](http://twitter.com/webleon/status/1122796289)

@[tinyfool](http://twitter.com/tinyfool) 对啊，我就奇怪为什么北京时间就要用上海和成都。。。 [大约1小时 ago](http://twitter.com/webleon/status/1122805042)

@[tinyfool](http://twitter.com/tinyfool) @[CatChen](http://twitter.com/CatChen)我所疑惑的就是为什么不统一一下呢，很容易产生困惑噢 [大约1小时 ago](http://twitter.com/webleon/status/1122813702)

**开发者都知道**

想必做开发的，尤其是PHP或Java的，很多年前就都会注意到这个情况：时区中没有asia/beijing，只有asia/shanghai和asia/chongqing。以前看到不少这种抱怨的帖子，毕竟和心理预期不一样，还会导致程序出错或程序员浪费时间调试。大家会猜测这是不是老外故意和北京捣乱。我认为不是。

**投诉BUG**

有国人愤而[投诉Ubuntu](https://bugs.launchpad.net/ubuntu/+source/libgweather/+bug/228554)：

for i'm from P.R.China and BeiJing is our capital city, we are always using BeiJing time zone. please fix it.

甚至[投诉Sun](http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4141080)说“The Time Zone id name for PRC is wrong”：

```
it is better if there exists a id name as "Asia/Beijing" for PRC.
Ubuntu认为：首先这个问题不归他。其次维护这个将会是易错的，而且容易与其他软件不兼容。
Sun的回答是“It is not wrong”。对他给出的理由，还是有说服力的：
```

**首先，\**我想确这一点\**，在JDK诞生之前，国际标准时区就没有Asia/Beijing，只有Asia/Shanghai或Asia/Chongqing？**（One thing I want to be confirmed here is, before JDK's birth, the international standard TimeZone name for PRC is Asia/Shanghai or Asia/Irkutsk? not Asia/Beijing?）

其次，在1986年到1991年期间，中华人民共和国采用了夏时制。为了能够处理任何给定的时间格式，时区就需要知道是否历史上使用夏令时。daylight就是表明这一点的字段。因此，在JDK中使用daylight字段是不对的。

但根据他后面说的，我认为sun似乎有必要更改时区，毕竟sun认为时区的api应该支持同时代的时区。Asia/Shanghai和Asia/Chongqing代表中国，都是国民党时期的老黄历了：

sun的策略是只支持同时代（contemporary）的时区，而不是历史上（historical）的某一个时区。TimeZone和DateFormat code只应该对于当前的时区工作。最后，Fixed in JDK1.2 FCS-M, but **the value is Asia/Shanghai, better one is Asia/Beijing**.



## 小结

anyway，有没有asia/beijing时区，开发都没影响，保持各种系统和软件的兼容可能更重要吧。
谁知道更多呢？请留言。

https://www.cnblogs.com/yangtsecode/p/12088284.html