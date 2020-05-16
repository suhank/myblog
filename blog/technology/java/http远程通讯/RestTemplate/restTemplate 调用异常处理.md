# restTemplate 调用异常处理

摘要: 当 restTemplate 调用捕获异常时无法 获取response body 本身信息

## 我们先来说说问问的所在

> Rest 接口中的异常,如何能在RestTemplate 中显示出来.RestTemplate 中try catch 的 e.getMessage()只能是请求本身代码异常.(400 null 或者 500 null),但是 用postman 调用的时候是可以显示错误信息的.这是怎么回事?

![postman中错误提示](https://static.oschina.net/uploads/img/201711/29180749_zaGu.png) ![println](https://static.oschina.net/uploads/img/201711/29181330_YBdI.png)

## 问题代码

- Rest 接口

```
    @PostMapping("/test")
    public ResponseEntity save(@RequestBody List<VoOrder> vos){
        System.out.println(JSON.toJSONString(vos));
//        return new ResponseEntity("fuck you ", HttpStatus.BAD_GATEWAY) ;
//        return new ResponseEntity("fuck you ", HttpStatus.OK) ; //只有ok 能走通
        return new ResponseEntity("fuck you ", HttpStatus.NOT_FOUND) ;
```

- RestTemplate调用

```
RestTemplate restTemplate = new RestTemplate();
List<VoOrder> vos = new ArrayList<>();
try {
    ResponseEntity responseEntity = restTemplate.postForEntity("url",vos, Object.class);
}catch (Exception e) {
     e.printStackTrace();
    System.out.println(e.getMessage());
}
```

> 说明try catch 只能获取restTemplate本事是否调取成功,不成功则异常.

##寻求解决办法

> restTemplate 对错误处理有个方法,从中获取到response ,从response中获取body即可,说的容易,来看代码

- RestTemplate调用

```
RestTemplate restTemplate = new RestTemplate();
List<VoOrder> vos = new ArrayList<>();
try {
        // 错误处理
       restTemplate.setErrorHandler(new CustomResponseErrorHandler());
       ResponseEntity responseEntity = restTemplate.postForEntity("/test",vos, Object.class);
}catch (CustomException e) {
     e.printStackTrace();
     System.out.println(e.getBody());
      System.out.println(e.getMessage());
}
```

- CustomResponseErrorHandler 错误处理类

```
public class CustomResponseErrorHandler implements ResponseErrorHandler {

  private ResponseErrorHandler errorHandler = new DefaultResponseErrorHandler();
   @Override
  public void handleError(ClientHttpResponse response) throws IOException {

// 队请求头的处理
    List<String> customHeader = response.getHeaders().get("x-app-err-id");

    String svcErrorMessageID = "";
    if (customHeader != null) {
      svcErrorMessageID = customHeader.get(0);
    }
//对body 的处理 (inputStream)
    String body = convertStreamToString(response.getBody());

    try {

      errorHandler.handleError(response);

    } catch (RestClientException scx) {

      throw new CustomException(scx.getMessage(), scx, body);
    }
  }
   @Override
  public boolean hasError(ClientHttpResponse response) throws IOException {
    return errorHandler.hasError(response);
  }

// inputStream 装换为 string 
  private String convertStreamToString(InputStream is) {
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    StringBuilder sb = new StringBuilder();

    String line = null;
    try {
      while ((line = reader.readLine()) != null) {
        sb.append(line);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        is.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return sb.toString();
  }
}
```

- 自定义异常捕获的类

```
public class CustomException extends RestClientException {

  private RestClientException restClientException;
  private String body;

  public RestClientException getRestClientException() {
    return restClientException;
  }

  public void setRestClientException(RestClientException restClientException) {
    this.restClientException = restClientException;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }


  public CustomException(String msg, RestClientException restClientException, String body) {
    super(msg);
    this.restClientException = restClientException;
    this.body = body;
  }

}
```

## 大功告成

![输入图片说明](https://static.oschina.net/uploads/img/201711/29183126_qXe9.png)

## 总结下

> 主要就是这个restTemplate 没有对错误请求做处理,只有正确请求.所以需要手动增加`setErrorHandler` 方法处理.

可参考：

1. <https://stackoverflow.com/questions/7878002/resttemplate-handling-response-headers-body-in-exceptions-restclientexception>
2. <http://www.developerq.com/article/1504414182>





https://my.oschina.net/ChenGuop/blog/1581759