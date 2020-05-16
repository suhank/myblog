# 跨域：The 'Access-Control-Allow-Origin' header contains multiple values '*, *', but only one is allowed

使用Ajax跨域请求资源，Nginx作为代理，出现：The 'Access-Control-Allow-Origin' header contains multiple values '*, *', but only one is allowed 错误。

服务端允许跨域配置：

```java
#region 设置允许跨域，允许复杂请求
HttpContext.Current.Response.AddHeader("Access-Control-Allow-Origin", "*");
if (HttpContext.Current.Request.HttpMethod == "OPTIONS")
{
    HttpContext.Current.Response.AddHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,PATCH,OPTIONS");
    HttpContext.Current.Response.AddHeader("Access-Control-Allow-Headers", "Content-Type, Accept, Authorization");
    //HttpContext.Current.Response.AddHeader("Access-Control-Max-Age", "1728000");
    HttpContext.Current.Response.End();
}
#endregion
```

Nginx的配置：

```csharp
add_header 'Access-Control-Allow-Origin' '*';
    location / {
		if ($request_method = 'OPTIONS') {
		add_header Access-Control-Allow-Origin *;
			add_header Access-Control-Allow-Methods GET,POST,PUT,DELETE,PATCH,OPTIONS;
			return 200;
		}
		proxy_pass http://xx:8002/;
		#proxy_pass http://localhost:62249/;
    }
```

看上面错误提示，contains multiple values "*" 意思就是设置了2次跨域，但是只有一个是允许的，移除其中的任意一个就好了。如果服务器设置了允许跨域，使用Nginx代理里面就不需要了（或者就不用使用Nginx了）



https://blog.csdn.net/q646926099/article/details/79082204