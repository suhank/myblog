# Jackson 时间格式化,时间注解@JsonFormat 用法,时差问题说明

 [Jackson](https://www.sojson.com/tag_jackson.html) 是 [SpringMvc](https://www.sojson.com/tag_springmvc.html) 官方推荐结合的，其实我是习惯用  [Gson](https://www.sojson.com/tag_gson.html) 的，但是由于公司统一使用  [Jackson](https://www.sojson.com/tag_jackson.html) ，自然对  [Jackson](https://www.sojson.com/tag_jackson.html) 需要关注的更多。下面来说说其中一个注解，就是 [@JsonFormat](https://www.sojson.com/blog/246.html) 。

## @JsonFormat 使用

我们可以有两种用法（我知道的），在对象属性上，或者在属性的 `getter` 方法上，如下代码所示：

增加到属性上：

```
... ...

/**更新时间  用户可以点击更新，保存最新更新的时间。**/
@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
private Date updateTime;

... ...
```

增加到 `getter` 方法上：

```
... ...

@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
public Date getUpdateTime() {
    return updateTime;
}

... ...
```

以上结果输出都是一样的。这个没有什么好说明的。具体输出格式，自己调整 `pattern` 。

## @JsonFormat 相差8小时问题 

上面直接这么使用，在我们中国来讲和我们的[北京时间](https://www.sojson.com/time/bjtime.html)，会相差8个小时，因为我们是东八区（北京时间）。

所以我们在格式化的时候要指定时区（`timezone` ），代码如下：

```
... ... 

/**更新时间  用户可以点击更新，保存最新更新的时间。**/
@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
private Date updateTime;

... ...
```

也就是增加一个属性，`timezone="GMT+8"` 即可，`getter` 方法我就不写了，一样的。

咱看看结果，我这个接口就是这么输出的：[公安网备查询](https://www.sojson.com/api/beianByGA.html) ，以 `https://www.sojson.com/api/gongan/sina.com.cn` 为例。

```
{
    "data": {
        "id": "11000002000016",
        "sitename": "新浪网",
        "sitedomain": "sina.com.cn",
        "sitetype": "交互式",
        "cdate": "2016-01-21",
        "comtype": "企业单位",
        "comname": "北京新浪互联信息服务有限公司",
        "comaddress": "北京市网安总队",
        "updateTime": "2017-09-05 02:26:34" //看这...这里就是刚刚输出的。
    },
    "status": 200
}
```



OK，打完收工！！！

> 版权所属：[SO JSON在线解析](https://www.sojson.com/)
>
> 原文地址：https://www.sojson.com/blog/246.html
>
> 转载时必须以链接形式注明原始出处及本声明。