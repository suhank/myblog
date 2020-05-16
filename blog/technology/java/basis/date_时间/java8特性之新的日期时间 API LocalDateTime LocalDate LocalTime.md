[TOC]



# 详解Java8特性之新的日期时间 API LocalDateTime LocalDate LocalTime

# 吐槽

Java 8 提供了一套新的日期时间 API，为什么要这么干呢。因在旧版的 Java 中，日期时间 API 存在很多问题，比如说**线程安全问题**了， java.util.Date 是非线程安全的，所有的日期类都是可变的；又还有**设计乱七八糟**，你去看看`java.util.Date`类会发现它的很多方法都被标记过时了，就知道Sun公司自己人都看不过去了，而且用于格式化和解析日期类的类在`java.text`包下，是不是抽签随便分包的；**使用起来不方便**，就拿`java.util.Calendar`类来说，加几天和减几天都是用`add`方法，意义十分不明显。刚开始学的时候我是大脸懵逼，各种风中凌乱，从现在开始我就可以彻底地忘记它们了！！！因为 Java 8 来解救我了~

# 新的日期时间 API

Java 8 提供的日期时间 API都在`java.time`包下，这个包涵盖了所有处理日期(date)，时间(time)，日期/时间(datetime)，时区（zone)，时刻（instant），间隔（duration）与时钟（clock）的操作，可以说一包在手，天下我有。

当然我不会一一介绍全部的类和方法，只是大概的用一下，只要记住一点，**你想要的功能（别太偏门），别人都已经帮你实现了，去翻API文档吧**

# 日期、时间和日期时间

- 日期（年月日）对应的是`java.time.LocalDate`
- 时间（时分秒）对应的是`java.time.LocalTime`
- 日期时间（年月日时分秒）对应的是`java.time.LocalDateTime`

这三个类的使用方法都差不多，我就以`LocalDateTime`来写个例子

```
@Test
public void test() {
    // 获取当前日期时间
    LocalDateTime now = LocalDateTime.now();
    System.out.println(now);

    // 将当前日期时间减去两天
    LocalDateTime dateTime2 = now.minusDays(2);
    System.out.println(dateTime2);

    // 将当前日期时间加上五天
    LocalDateTime dateTime3 = now.plusDays(5);
    System.out.println(dateTime3);

    // 输出当前日期时间的年份
    System.out.println(now.getYear());

    // 构造一个指定日期时间的对象
    LocalDateTime dateTime = LocalDateTime.of(2016, 10, 23, 8, 20);
    System.out.println(dateTime);
} 
```

输出结果

```
2017-05-07T13:39:32.220
2017-05-05T13:39:32.220
2017-05-12T13:39:32.220
2017
2016-10-23T08:20
```

# 时间戳

时间戳对应的是`java.time.Instant`，下面是时间戳例子

```
@Test
public void test1() {
    // 获取当前时间的时间戳
    Instant instant = Instant.now();
    // 因为中国在东八区，所以这句输出的时间跟我的电脑时间是不同的
    System.out.println(instant);

    // 既然中国在东八区，则要偏移8个小时，这样子获取到的时间才是自己电脑的时间
    OffsetDateTime dateTime = instant.atOffset(ZoneOffset.ofHours(8));
    System.out.println(dateTime);

    // 转换成毫秒，如果是当前时间的时间戳，结果跟System.currentTimeMillis()是一样的
    long milli = instant.toEpochMilli();
    System.out.println(milli);
} 
```

输出结果

```
2017-05-07T05:40:18.630Z
2017-05-07T13:40:18.630+08:00
1494135618630
```

# 间隔

表示间隔的有两个类

- `java.time.Duration`用于计算两个“时间”间隔
- `java.time.Period`用于计算两个“日期”间隔

下面是时间间隔的例子

```
@Test
public void test2() {
    LocalTime start = LocalTime.now();
    try {
        //让线程睡眠3s
        Thread.sleep(3000);
    } catch (Exception e) {
    }
    LocalTime end = LocalTime.now();
    //获取end和start的时间间隔
    Duration duration = Duration.between(start, end);

    //可能会输出PT3S或者输出PT3.001S，至于多出来的0.001秒其实就是除去线程睡眠时间执行计算时间间隔那句代码消耗的时间
    System.out.println(duration);
}
```

输出结果

```
PT3.001S
12
```

下面是日期间隔的例子

```
@Test
public void test3() {
    //起始时间指定为2015年3月4日
    LocalDate start = LocalDate.of(2015, 3, 4);
    //终止时间指定为2017年8月23日
    LocalDate end = LocalDate.of(2017, 8, 23);

    Period period = Period.between(start, end);
    //输出P2Y5M19D，Y代表年，M代表月，D代表日，说明start和end的日期间隔是2年5个月19天
    System.out.println(period);
}1234567891011
```

输出结果

```
P2Y5M19D
12
```

# 格式转换

以前我们用的日期格式化的类是`java.text.SimpleDateFormat`，Java 8 提供的日期格式化类是`java.time.format.DateTimeFormatter`，下面写个例子

```java
@Test
public void test5() {
    // 获取预定义的格式，DateTimeFormatter类预定了很多种格式
    DateTimeFormatter dtf = DateTimeFormatter.BASIC_ISO_DATE;
    // 获取当前日期时间
    LocalDate now = LocalDate.now();
    // 指定格式化器格式日期时间
    String strNow = now.format(dtf);
    System.out.println(strNow);

    // 自定义格式
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
    String strNow2 = now.format(formatter);
    System.out.println(strNow2);

    // 将字符串转换成日期
    LocalDate date = LocalDate.parse(strNow2, formatter);
    System.out.println(date);
} 
```

输出结果

```
20170507
2017年05月07日
2017-05-07
```

# 时区

带时区的日期时间是`java.time.ZonedDateTime`，我们可以通过`java.time.ZoneId`去查看支持的时区有哪些，如

```
@Test
public void test7() {
    Set<String> set = ZoneId.getAvailableZoneIds();
    set.forEach(System.out::println);
} 
```

输出结果，后面还有很多的

```
Asia/Aden
America/Cuiaba
Etc/GMT+9
Etc/GMT+8
Africa/Nairobi
America/Marigot
Asia/Aqtau
Pacific/Kwajalein
America/El_Salvador
Asia/Pontianak
Africa/Cairo
Pacific/Pago_Pago
Africa/Mbabane
Asia/Kuching
Pacific/Honolulu
Pacific/Rarotonga
America/Guatemala
Australia/Hobart
Europe/London
America/Belize
America/Panama
Asia/Chungking
America/Managua
....
```

然后是`ZonedDateTime`的例子

```
@Test
public void test8() {
    //获取当前时区的日期时间
    ZonedDateTime now = ZonedDateTime.now();
    System.out.println(now);

    //获取美国洛杉矶时区的日期时间
    ZonedDateTime USANow = ZonedDateTime.now(ZoneId.of("America/Los_Angeles"));
    System.out.println(USANow);
}
```

输出结果

```
2017-05-07T14:06:32.132+08:00[Asia/Singapore]
2017-05-06T23:06:32.134-07:00[America/Los_Angeles]
```

我也不知道为啥我的时区是新加坡的~





https://blog.csdn.net/timheath/article/details/71326329