# java 解析url，获取参数

原创[浅醉樱花雨](https://me.csdn.net/u013314786) 最后发布于2018-09-26 11:55:28 阅读数 18619 收藏

 

一个简单的解析url，获取参数的Java工具类

```java
import java.util.HashMap;
import java.util.Map;

/**
 * @author lixk
 * @description url工具类
 * @date 2018/9/26 9:58
 */
public class UrlUtil {

    @Data
    public static class UrlEntity {
        /**
         * 基础url
         */
        private String baseUrl;
        /**
         * url参数
         */
        private Map<String, String> params;
    }

    /**
     * 解析url
     *
     * @param url
     * @return
     */
    public static UrlEntity parse(String url) {
        UrlEntity entity = new UrlEntity();
        if (url == null) {
            return entity;
        }
        url = url.trim();
        if (url.equals("")) {
            return entity;
        }
        String[] urlParts = url.split("\\?");
        entity.baseUrl = urlParts[0];
        //没有参数
        if (urlParts.length == 1) {
            return entity;
        }
        //有参数
        String[] params = urlParts[1].split("&");
        entity.params = new HashMap<>();
        for (String param : params) {
            String[] keyValue = param.split("=");
            entity.params.put(keyValue[0], keyValue[1]);
        }

        return entity;
    }

   /**
     * 测试
     *
     * @param args
     */
    public static void main(String[] args) {
        UrlEntity entity = parse(null);
        System.out.println(entity.baseUrl + "\n" + entity.params);
        entity = parse("http://www.123.com");
        System.out.println(entity.baseUrl + "\n" + entity.params);
        entity = parse("http://www.123.com?id=1");
        System.out.println(entity.baseUrl + "\n" + entity.params);
        entity = parse("http://www.123.com?id=1&name=小明");
        System.out.println(entity.baseUrl + "\n" + entity.params);
    }
}
```

运行输出结果

```
null
null
http://www.123.com
null
http://www.123.com
{id=1}
http://www.123.com
{name=小明, id=1} 
```



https://blog.csdn.net/u013314786/article/details/82851403