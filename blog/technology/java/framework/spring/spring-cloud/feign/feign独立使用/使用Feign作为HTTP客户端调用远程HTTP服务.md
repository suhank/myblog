# 使用Feign作为HTTP客户端调用远程HTTP服务



在Spring Cloud Netflix栈中，各个微服务都是以HTTP接口的形式暴露自身服务的，因此在调用远程服务时就必须使用HTTP客户端。我们可以使用JDK原生的`URLConnection`、Apache的`Http Client`、Netty的异步HTTP Client, Spring的`RestTemplate`。但是，用起来最方便、最优雅的还是要属Feign了。

**Feign完全可以单独使用,不需要依赖Spring Cloud .**

## Feign简介

Feign是一种声明式、模板化的HTTP客户端。在Spring Cloud中使用Feign, 我们可以做到使用HTTP请求远程服务时能与调用本地方法一样的编码体验，开发者完全感知不到这是远程方法，更感知不到这是个HTTP请求。比如：

```
@Autowired
private AdvertGropRemoteService service; // 远程服务

public AdvertGroupVO foo(Integer groupId) {
    return service.findByGroupId(groupId); // 通过HTTP调用远程服务
}
```

不哔哔了直接上代码吧:

maven配置:

```
        <!-- 使用Apache HttpClient替换Feign原生httpclient -->
        <dependency>
            <groupId>org.apache.httpcomponents</groupId>
            <artifactId>httpclient</artifactId>
        </dependency>
        <dependency>
            <groupId>com.netflix.feign</groupId>
            <artifactId>feign-httpclient</artifactId>
            <version>8.18.0</version>
        </dependency>        
        <dependency>
            <groupId>com.netflix.feign</groupId>
            <artifactId>feign-core</artifactId>
            <version>8.18.0</version>
        </dependency>
        <dependency>
            <groupId>com.netflix.feign</groupId>
            <artifactId>feign-gson</artifactId>
            <version>8.18.0</version>
        </dependency>
        <dependency>
            <groupId>com.google.code.gson</groupId>
            <artifactId>gson</artifactId>
            <version>2.8.2</version>
        </dependency>
```

Feign在默认情况下使用的是JDK原生的`URLConnection`发送HTTP请求，没有连接池，但是对每个地址会保持一个长连接，即利用HTTP的`persistence connection` 。我们可以用Apache的HTTP Client替换Feign原始的http client, 从而获取连接池、超时时间等与性能息息相关的控制能力。Spring Cloud从`Brixtion.SR5`版本开始支持这种替换，首先在项目中声明Apache HTTP Client和`feign-httpclient`依赖.

Feign定义:

```
import com.migr.common.ws.ResponseBean;
import feign.Param;
import feign.RequestLine;

/**
 * Created by Administrator on 2017/10/27.
 */

public interface AOranService{
    @RequestLine("POST /exec?m={m}&reqJson={reqJson}&token={token}")// get 提交
    ResponseBean findParentOrgan(@Param("m") String m, @Param("reqJson") String reqJson, @Param("token") String token);
}
```

如何调用:

```
        //这一段完全可以做声工具类或者单例实现
        // http://base.dianxiaohuocy.net/api 为接口地址
        AOranService oranService = Feign.builder()
                .decoder(new GsonDecoder())
                .encoder(new GsonEncoder())
                .target(AOranService.class, "http://base.dianxiaohuocy.net/api");
        Map<String, String> reqMap = new HashMap<>();
        reqMap.put("vendorId", "3");
        reqMap.put("organCode", "10101");
        reqMap.put("organLevel", "2");

        String token = "H8DH9Snx9877SDER5667";
        String reqJson = JsonUtil.g.toJson(reqMap);
        // 以上
        
        // 以下是真正通过Feign调用接口的方法
        ResponseBean str = oranService.findParentOrgan("findParentOrgan", reqJson, token);
```

 

 

 

所调用的接口:

```
    @RequestMapping(value = "/exec")
    @ResponseBody
    public Object exec(@RequestParam(value = "m", required = true) String m,
                       @RequestParam(value = "reqJson", required = true) String reqJson,
                       @RequestParam(value = "token", required = true) String token){
// 内部实现完全没必要关注
}
```

 

feign项目地址: https://github.com/OpenFeign/feign





https://my.oschina.net/aulbrother/blog/1557140