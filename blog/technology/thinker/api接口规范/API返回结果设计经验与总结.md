[TOC]



# API返回结果设计经验与总结

## 前言

RESTful API的设计已经很成熟了，大家也都比较认可。本文也不再过多介绍RESTful API相关的知识，而是针对JSON型API的返回结果设计，总结下自己的经验。

## 结构

先来看看返回结果的结构示例：

```json
{
    data : { // 请求数据，对象或数组均可
        user_id: 123,
        user_name: "tutuge",
        user_avatar_url: "http://tutuge.me/avatar.jpg"
        ...
    },
    msg : "done", // 请求状态描述，调试用
    code: 1001, // 业务自定义状态码
    extra : { // 全局附加数据，字段、内容不定
        type: 1,
        desc: "签到成功！"
    }
}


{
  "data": {
    "": "// 请求数据，对象或数组均可",
    "userId": "int,123",
    "userName": "string,tutuge",
    "userAvatarUrl": "string,http://tutuge.me/avatar.jpg",
    "...": ""
  },
  "msg": "string,done,// 请求状态描述，调试用",
  "code": "int,1001, // 业务自定义状态码",
  "extra": {
    "": "// 全局附加数据，字段、内容不定",
    "type": "int,1",
    "desc": "string,签到成功！"
  }
}

```

```java
/**
 * <B>Description:</B> API返回结果 <br>
 * <B>Create on:</B> 2018-12-31 22:39 <br>
 *
 * @author xiangyu.ye
 * @version 1.0
 */
@Data
public class Result<T, F> {

    /**
     * data : {"":"// 请求数据，对象或数组均可","userId":"int,123","userName":"string,tutuge","userAvatarUrl":"string,http://tutuge.me/avatar.jpg","...":""}
     * msg : string,done,// 请求状态描述，调试用
     * code : int,1001, // 业务自定义状态码
     * extra : {"":"// 全局附加数据，字段、内容不定","type":"int,1","desc":"string,签到成功！"}
     */
    private T data;// 请求数据，对象或数组均可
    private String msg;// 请求状态描述，调试用
    private String code = "200";//业务自定义状态码,200表示成功,非200失败
    private F extra;// 全局附加数据，字段、内容不定
}
```



### data字段 - 请求数据

首先是本次请求结果的数据`data`字段，其值为对象（字典）或数组均可以，根据业务而定。

如请求的是某个用户的个人profile信息，就可以是对象，对象里面是用户profile的键值对数据，如`user_id: 123`、`user_name: "tutuge"`等。

如果请求的是列表数据，就可以是数组，如请求用户列表：

```
data: [
    {user_id: 123, user_name: "tutuge"},
    {user_id: 321, user_name: "zekunyan"},
    ...
]
```

数组、对象，相互嵌套，灵活组合即可。

对于iOS来说，解析`data`字段是对象还是数组也很容易，在接收到JSON数据字典后，如AFNetworking的返回结果，对`data`判断其类型即可：

```
if ([jsonDict[@"data"] isKindOfClass:[NSDictionary class]]) {
    // JSON对象
} else if ([jsonDict[@"data"] isKindOfClass:[NSArray class]]) {
    // JSON数组
}
```

### msg字段 - 请求状态描述，调试用

`msg`字段是本次请求的业务、状态描述信息，主要用于调试、测试等。

如“done”、“请求缺少参数！”

服务端可以自由发挥，开发人员看得懂就好。 -_-|||

### code字段 - 业务自定义状态码

`code`字段，业务自定义的状态码。

其实是否要在API里面自定义业务状态码，**非常有争议**=。=，因为Http请求本身已经有了完备的状态码，再定义一套状态码直观上感受却是不对劲。但是实际开发中，确实发现自定义业务状态码的必要性，如一次成功的Http status 200的请求，可能由于用户未登录、登录过期而有不同的返回结果和处理方式，所以还是保留了`code`。

状态码的定义也最好有一套规范，如按照用户相关、授权相关、各种业务，做简单的分类：

```
// Code 业务自定义状态码定义示例

// 授权相关
1001: 无权限访问
1002: access_token过期
1003: unique_token无效
...

// 用户相关
2001: 未登录
2002: 用户信息错误
2003: 用户不存在

// 业务1
3001: 业务1XXX
3002: 业务1XXX

// ...
```

`Code`业务状态码最好是用常量定义的，当然有能力的动态配置更新更好，这里就不再详细说明。

Http的状态码参考：[List of HTTP status codes](https://en.wikipedia.org/wiki/List_of_HTTP_status_codes)

### extra字段 - 全局附加数据

`extra`字段，用来表示全局的附加数据。

这个字段来源于之前做项目时，用户的操作（数据请求），会导致用户的等级、经验变化，而具体什么时候产生不确定，由服务端的规则决定，并且客户端要及时向用户展示变化，所以加上了`extra`字段。

在设计`extra`字段的时候，并没有对其结构内容做限制，所以比较灵活，但是还是要有个`type`字段，来做约束，如：

```
// 升级
type: 1,
show_msg: "恭喜您升级到XXX"

// 完成任务
type: 2,
task_desc: "达成XXX成就"
```

总的来说就是自由发挥，只要服务端、客户端相互沟通好即可。当然，也要避免乱用，保证真的需要全局附加数据才使用这个字段。

## 最佳实践

总结几点最佳实践。

### 规范统一的命名

**命名风格统一**

不管是驼峰式还是下划线式，统一就好。当然，按照目前的“大众规范”，还是统一小写加下划线比较好=。=
如：`user_id`，`user_name`，`user_age`等。

**语义清晰，遵守常用缩写**

字段的名字最好能体现字段的类型，遵守一些“常用”的缩写，如：

```
// 字符串
user_name, task_desc, date_str, article_title, feed_content 等

// 数字
user_id, users_count, task_num, xxx_offset 等

// 日期
login_at, create_date, logout_time 等

// 布尔
is_done, is_vip, protected, can_read 等

// URL
user_avatar_url, thumb_url 等

// 数组
users, profiles, thumb_imgs 等
```

### 空值、空字段的处理

空值、空字段的处理也是比较容易出问题。

**统一空值用null**

除了布尔类型的，其余的空值统一用`null`表示，客户端保证每种字段的`null`可以被正常处理。

**给不同类型设置默认空值**

除了`null`，还可以对字段设置“默认值”，如数字就是`0`，字符串就是空字符串`""`，数组就是空数组`[]`，对象就是空对象`{}`，这样有个好处就是可以避免很多客户端（Java、OC）处理空值（Null、nil、null）产生的异常。但是危害就是容易语义不明。还是要根据具体业务、前后端约定而定。

以前写过一篇[用Runtime的手段填充任意NSObject对象的nil属性](http://tutuge.me/2015/07/08/fill-nil-property-of-object/)，其实就是为对象空值统一设置默认值的=。=，可以参考。

### 布尔boolean值的处理

说实话，我见过各种布尔值表示方式，如：

```
is_login: true,
is_login: "true",
is_login: 1
is_login: "TRUE"
is_login: "YES"
// ...天啊
```

由于语言本身的限制、框架的处理方式，不对布尔类型的值做限制总觉得不踏实，像C、C++、Objective-C里面的布尔就是数字0和1，其它语言也都各自不一样，还有从数据库读写导致的布尔值类型不一致等。

所以，如果可以的话，最好一开始就对所有请求参数、结果的布尔值类型做限定，个人觉得**统一成数字0和1**最好。

然后在客户端和服务端统一设置常量、宏定义，定义布尔的类型，所有的参数、结果的布尔字段全部做强制约束。

### 时间、日期字段

时间的处理也是非常容易出错的，特别是遇上时区转换的时候。

**强制GMT/UTC时间戳**

一种做法就是强制所有时间参数只能传Unix时间戳，也就是标准GMT/UTC时间戳，然后由各自的客户端根据自己的时区、显示要求做处理后显示。

```
// 从服务器接收的时间数据
login_at: 1462068610

// 根据时区、显示要求转换，如北京时间
显示：2016年5月1日下午1点、1天前等
```

这样的话，客户端、服务端存储、读取时间都相当于处理纯数字。

**使用ISO 8601带时区的时间日期字符串**

使用Unix时间戳有个坏处，就是：

- 最早只能到`1970/1/1 0:0:0`GMT时间，一旦需求早于这个时间，时间戳就成了负数=。=
- 不方便人阅读。调试API的时候，开发人员不能直观看出具体时间，很不方便

所以，可以按照[ISO 8601](https://en.wikipedia.org/wiki/ISO_8601)标准，用字符串保存、传输时间。

如果以`YYYY-MM-DDThh:mm:ssTZD`格式为准, 时间的形式就是`1997-07-16T19:20:30+01:00`，保存了时区信息，也方便阅读。

### type类型的处理

API数据中免不了各种类型字段，如用户类型`user_type`、登录类型`login_type`等，类型的表示也可以分为数字、字符串两种。

**数字表示类型**

这个应该是最直接的方式了，客户端和服务端共同维护某个API下、某个数据类型中的type常量，靠文档约束。

**字符串表示类型**

数字的类型毕竟不利于直观阅读，如果可以的话，用字符串也是不错的，当然坏处就是代码里面就不能用`Switch`语句了（除了强大的Swift=。=）

```
// 如登录类型，QQ、微信、微博等
login_type: "qq",
login_type: "wechat",
login_type: "sina_weibo",
```

### 完整的URL

API里面的数据也会有URL类型的，一般来说如用户的头像、各种图片、音频等资源，都是以URL链接的形式返回的。

返回的URL一定要“完整”，主要指的是不要忘记URL里面的**协议**部分，也就是`scheme`部分。

像`tutuge.me/imgs/1.jpg`这种URL值，就是不完整的，没有指明网络协议，难道靠猜=。=
应该是`http://tutuge.me/imgs/1.jpg`。

## 总结

嗯，规范非常重要。:-D





http://tutuge.me/2016/05/02/design-json-api-respoense/