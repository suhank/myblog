# 3. 小程序的AppID是什么？AppSecret是什么？

1，我前面说过，绑定在一个微信开放平台账户下的订阅号、服务号、小程序、APP、PC网站都叫做”应用“，每个应用都有自己的AppID和AppSecret，AppID叫做应用唯一标识，AppSecret叫做应用密钥。

2，AppID就像门牌，AppSecret就像钥匙。AppID可以公开，但是AppSecret必须保密。而且微信官方文档反复强调，AppSecret的安全级别很高，也就是说如果泄露出去安全风险很大，要小心保管。你可以重新生成AppSecret，但是切记重新生成AppSecret前，跟你的程序员或技术外包服务商协调好，程序里如果有用到AppSecret的地方，要同步修改，否则程序会报错。

3，很多新手找不到小程序的AppID在哪里。首先你必须要注册个小程序，如果已经有了订阅号或服务号，不必单独注册，可以在订阅号或服务号的官方管理后台，快速创建小程序。进入小程序管理界面后，左侧底部有个“设置”菜单，点击进入后，选择“开发设置”，可以看到小程序的AppID，重新生成AppSecret的操作也在这里。



https://www.kancloud.cn/qq2864694601/liangbo_xiaochengxu/368593