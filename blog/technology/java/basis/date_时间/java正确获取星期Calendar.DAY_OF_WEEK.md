[TOC]



# Java正确获取星期Calendar.DAY_OF_WEEK

## 说明

正确获取星期几（Calendar.DAY_OF_WEEK）

```java
Calendar now = Calendar.getInstance();  
//一周第一天是否为星期天  
boolean isFirstSunday = (now.getFirstDayOfWeek() == Calendar.SUNDAY);  
//获取周几  
int weekDay = now.get(Calendar.DAY_OF_WEEK);  
//若一周第一天为星期天，则-1  
if(isFirstSunday){  
    weekDay = weekDay - 1;  
    if(weekDay == 0){  
        weekDay = 7;  
    }  
}  
//打印周几  
System.out.println(weekDay);  
  
//若当天为2014年10月13日（星期一），则打印输出：1  
//若当天为2014年10月17日（星期五），则打印输出：5  
//若当天为2014年10月19日（星期日），则打印输出：7  
```




| 星期日为一周的第一天 | **SUN** | **MON** | **TUE** | **WED** | **THU** | **FRI** | **SAT** |
| -------------------- | ------- | ------- | ------- | ------- | ------- | ------- | ------- |
| DAY_OF_WEEK返回值    | 1       | 2       | 3       | 4       | 5       | 6       | 7       |
| 星期一为一周的第一天 | **MON** | **TUE** | **WED** | **THU** | **FRI** | **SAT** | **SUN** |
| DAY_OF_WEEK返回值    | 1       | 2       | 3       | 4       | 5       | 6       | 7       |

 

## 以下参考：

### 1.在获取月份时，Calendar.MONTH + 1 的原因

Java中的月份遵循了罗马历中的规则：当时一年中的月份数量是不固定的，第一个月是JANUARY。而Java中Calendar.MONTH返回的数值其实是当前月距离第一个月有多少个月份的数值，JANUARY在Java中返回“0”，所以我们需要+1。

### 2.在获取星期几 Calendar.DAY_OF_WEEK – 1 的原因

Java中Calendar.DAY_OF_WEEK其实表示：一周中的第几天，所以他会受到 **第一天是星期几** 的影响。
有些地区以星期日作为一周的第一天，而有些地区以星期一作为一周的第一天，这2种情况是需要区分的。
看下表的返回值

| 星期日为一周的第一天 | **SUN** | **MON** | **TUE** | **WED** | **THU** | **FRI** | **SAT** |
| -------------------- | ------- | ------- | ------- | ------- | ------- | ------- | ------- |
| DAY_OF_WEEK返回值    | 1       | 2       | 3       | 4       | 5       | 6       | 7       |
| 星期一为一周的第一天 | **MON** | **TUE** | **WED** | **THU** | **FRI** | **SAT** | **SUN** |
| DAY_OF_WEEK返回值    | 1       | 2       | 3       | 4       | 5       | 6       | 7       |

所以Calendar.DAY_OF_WEEK需要根据本地化设置的不同而确定是否需要 “-1”
Java中设置不同地区的输出可以使用 **Locale.setDefault(Locale.地区名)** 来实现。

 

System.out.println(calendar.get(Calendar.DAY_OF_WEEK));

返回的是周几，而不是一周的第几天

 

可以这样设置，星期第一天是星期几：

calendar.setFirstDayOfWeek(Calendar.MONDAY);

也可以设置Calendar.SUNDAY

设置好了就决定了当前日期的WEEK_OF_YEAR，**但并不会改变DAY_OF_WEEK** !

 

### 3.获取日期时 Calendar.DAY_OF_MONTH 不需要特殊的操作，他直接返回一个月中的第几天





http://chamcon.iteye.com/blog/2144433