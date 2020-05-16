# Java使用Calendar月操作（时间段内的月份、前三个月、月份加1）

## 1、获取当前时间和前三个月时间

> #### 代码：

> ```java
> SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");//格式化为2017-10
> Calendar calendar = Calendar.getInstance();//得到Calendar实例
> calendar.add(Calendar.MONTH, -3);//把月份减三个月
> Date starDate = calendar.getTime();//得到时间赋给Data
> String stardtr = formatter.format(starDate);//使用格式化Data
> tv_start_time.setText(stardtr);//显示
> ```
>
> 如果想得到当前时间，把calendar.add(Calendar.MONTH, -3);去掉就可以了

> #### 结果：

> 当前时间：2017-10
>
> 减三个月：2017-07

## 2、获取时间段内所有的年月集合

> 代码：
>
> ```java
> /**
>      * 获取时间段内所有的年月集合
>      *
>      * @param minDate 最小时间  2017-01
>      * @param maxDate 最大时间 2017-10
>      * @return 日期集合 格式为 年-月
>      * @throws Exception
>      */
>     public static List<String> getMonthBetween(String minDate, String maxDate) throws Exception {
>         ArrayList<String> result = new ArrayList<String>();
>         SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");//格式化为年月
>  
>         Calendar min = Calendar.getInstance();
>         Calendar max = Calendar.getInstance();
>  
>         min.setTime(sdf.parse(minDate));
>         min.set(min.get(Calendar.YEAR), min.get(Calendar.MONTH), 1);
>  
>         max.setTime(sdf.parse(maxDate));
>         max.set(max.get(Calendar.YEAR), max.get(Calendar.MONTH), 2);
>  
>         Calendar curr = min;
>         while (curr.before(max)) {
>             result.add(sdf.format(curr.getTime()));
>             curr.add(Calendar.MONTH, 1);
>         }
>  
>         return result;
>     }
> 
> ```
>
> 结果：
>
>
>
> [2017-01,2017-02,2017-03,2017-04,2017-05,2017-06,2017-07,2017-08,2017-09,2017-10]

## 3、月份加1

> 代码：
>
>
>
> ```java
>    /**
>      * 月份加一
>      * @param date
>      * @return
>      */
>     public static String monthAddFrist(String date) {
>  
>         DateFormat df = new SimpleDateFormat("yyyy-MM");
>         try {
>             Calendar ct = Calendar.getInstance();
>             ct.setTime(df.parse(date));
>             ct.add(Calendar.MONTH, +1);
>             return df.format(ct.getTime());
>         } catch (ParseException e) {
>             e.printStackTrace();
>         }
>  
>         return "";
>     }
> 
> ```
>
> 结果：
>
>
>
> 2017-01 返回 2017-02
>
> 2017-12 返回 2018-01





https://blog.csdn.net/u012246458/article/details/78293088