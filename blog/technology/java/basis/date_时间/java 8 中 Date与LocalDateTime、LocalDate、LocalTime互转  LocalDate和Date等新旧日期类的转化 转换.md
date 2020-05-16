# Java 8 中 Date与LocalDateTime、LocalDate、LocalTime互转  LocalDate和Date等新旧日期类的转化 转换

## 说明

基本概念

- 时刻：所有计算机系统内部都用一个整数表示时刻，这个整数是距离格林尼治标准时间1970年1月1日0时0分0秒的毫秒数，可以理解时刻就是绝对时间，它与时区无关，不同时区对同一时刻的解读，即年月日时分秒是不一样的；
- 时区：同一时刻，世界上各个地区的时间可能是不一样的，具体时间与时区有关，一共有24个时区，英国格林尼治是0时区，北京是东八区，也就是说格林尼治凌晨1点，北京是早上9点；
- 年历：我们都知道，中国有公历和农历之分，公历和农历都是年历，不同的年历，一年有多少月，每月有多少天，甚至一天有多少小时，这些可能都是不一样的，我们主要讨论公历。

Java 8中表示日期和时间的类有多个，主要的有：

- Instant：表示时刻，不直接对应年月日信息，需要通过时区转换
- LocalDateTime: 表示与时区无关的日期和时间信息，不直接对应时刻，需要通过时区转换
- LocalDate：表示与时区无关的日期，与LocalDateTime相比，只有日期信息，没有时间信息
- LocalTime：表示与时区无关的时间，与LocalDateTime相比，只有时间信息，没有日期信息
- ZonedDateTime： 表示特定时区的日期和时间
- ZoneId/ZoneOffset：表示时区



## 转换

Java 8中 java.util.Date 类新增了两个方法，分别是from(Instant instant)和toInstant()方法

```java
// Obtains an instance of Date from an Instant object.
public static Date from(Instant instant) {
    try {
        return new Date(instant.toEpochMilli());
    } catch (ArithmeticException ex) {
        throw new IllegalArgumentException(ex);
    }
}

// Converts this Date object to an Instant.
public Instant toInstant() {
    return Instant.ofEpochMilli(getTime());
}

 
```

这两个方法使我们可以方便的实现将旧的日期类转换为新的日期类，具体思路都是通过Instant当中介，然后通过Instant来创建LocalDateTime（这个类可以很容易获取LocalDate和LocalTime），新的日期类转旧的也是如此，将新的先转成LocalDateTime，然后获取Instant，接着转成Date，具体实现细节如下：

```java

// 01. java.util.Date --> java.time.LocalDateTime
public void UDateToLocalDateTime() {
    java.util.Date date = new java.util.Date();
    Instant instant = date.toInstant();
    ZoneId zone = ZoneId.systemDefault();
    LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
}

// 02. java.util.Date --> java.time.LocalDate
public void UDateToLocalDate() {
    java.util.Date date = new java.util.Date();
    Instant instant = date.toInstant();
    ZoneId zone = ZoneId.systemDefault();
    LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
    LocalDate localDate = localDateTime.toLocalDate();
}

// 03. java.util.Date --> java.time.LocalTime
public void UDateToLocalTime() {
    java.util.Date date = new java.util.Date();
    Instant instant = date.toInstant();
    ZoneId zone = ZoneId.systemDefault();
    LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
    LocalTime localTime = localDateTime.toLocalTime();
}


// 04. java.time.LocalDateTime --> java.util.Date
public void LocalDateTimeToUdate() {
    LocalDateTime localDateTime = LocalDateTime.now();
    ZoneId zone = ZoneId.systemDefault();
    Instant instant = localDateTime.atZone(zone).toInstant();
    java.util.Date date = Date.from(instant);
}


// 05. java.time.LocalDate --> java.util.Date
public void LocalDateToUdate() {
    LocalDate localDate = LocalDate.now();
    ZoneId zone = ZoneId.systemDefault();
    Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
    java.util.Date date = Date.from(instant);
}

// 06. java.time.LocalTime --> java.util.Date
public void LocalTimeToUdate() {
    LocalTime localTime = LocalTime.now();
    LocalDate localDate = LocalDate.now();
    LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
    ZoneId zone = ZoneId.systemDefault();
    Instant instant = localDateTime.atZone(zone).toInstant();
    java.util.Date date = Date.from(instant);
}
```





https://www.cnblogs.com/exmyth/p/6425878.html