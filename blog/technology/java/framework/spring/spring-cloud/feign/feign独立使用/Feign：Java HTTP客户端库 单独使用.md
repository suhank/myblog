# Feign：Java HTTP客户端库

[feign](http://hao.jobbole.com/tag/feign/) , [HTTP](http://hao.jobbole.com/tag/front-end-http/) , [Java](http://hao.jobbole.com/tag/java/)

本资源由 [伯乐在线](http://www.jobbole.com/) - [王涛](http://www.jobbole.com/members/wt726553124) 整理

Feign使得 Java HTTP 客户端编写更方便。Feign 灵感来源于Retrofit、JAXRS-2.0和WebSocket。Feign最初是为了降低统一绑定Denominator到HTTP API的复杂度，不区分是否支持Restful。Feign旨在通过最少的资源和代码来实现和HTTP API的连接。通过可定制的解码器和错误处理，可以编写任意的HTTP API。

[![feign](http://jbcdn1.b0.upaiyun.com/2015/12/402ffcbe20c6987dec750fbacfc63c6a.png)](http://jbcdn1.b0.upaiyun.com/2015/12/402ffcbe20c6987dec750fbacfc63c6a.png)

## 主要特点

- 定制化
- 提供多个接口
- 支持JSON格式的编码和解码
- 支持XML格式的编码和解码

## 工作机制

Feign通过配置注入一个模板化请求进行工作。只需在发送之前关闭它，参数就可以被直接的运用到模板中。然而这也限制了Feign，只支持文本形式的API，它可以在响应请求方面来简化系统。了解了这一点，这也非常容易进行你的单元测试转换。

## 基础示例

下面是[标准Retrofit](https://github.com/square/retrofit/blob/master/samples/src/main/java/com/example/retrofit/SimpleService.java)的典型用法：

Java

```
interface GitHub {
  @RequestLine("GET /repos/{owner}/{repo}/contributors")
  List<Contributor> contributors(@Param("owner") String owner, @Param("repo") String repo);
}

static class Contributor {
  String login;
  int contributions;
}

public static void main(String... args) {
  GitHub github = Feign.builder()
                       .decoder(new GsonDecoder())
                       .target(GitHub.class, "https://api.github.com");

  // Fetch and print a list of the contributors to this library.
  List<Contributor> contributors = github.contributors("netflix", "feign");
  for (Contributor contributor : contributors) {
    System.out.println(contributor.login + " (" + contributor.contributions + ")");
  }
}
```



## 可定制

Feign在一些方面可以定制。举个简单的例子，你可以使用Feign.builder()来构建一个API 接口，包含你自定义的内容。例如：

Java

```
interface Bank {
  @RequestLine("POST /account/{id}")
  Account getAccountInfo(@Param("id") String id);
}
...
Bank bank = Feign.builder().decoder(new AccountDecoder()).target(Bank.class, "https://api.examplebank.com");
```



## 支持多种接口

Feign可以提供多种API 接口。这些都被定义为Target<T>（默认为 HardCodeTarget<T>）。它允许动态扩展，并且被修饰过的请求会被优先执行。

例如，以下部分可以修饰来自当前身份验证服务的URL和AUTH的每一个请求。

Java

```
Feign feign = Feign.builder().build();
CloudDNS cloudDNS = feign.target(new CloudIdentityTarget<CloudDNS>(user, apiKey));
```



## 示例

Feign被应用于[GitHub](https://github.com/Netflix/feign/tree/master/example-github)和[维基百科](https://github.com/Netflix/feign/tree/master/example-wikipedia)客户端。相似的项目也同样在实践中运用了Feign。尤其是它的[示例后台程序](https://github.com/Netflix/denominator/tree/master/example-daemon)。

官方网站：<https://github.com/Netflix/feign>
开源地址：<https://github.com/Netflix/feign>







http://hao.jobbole.com/feign/