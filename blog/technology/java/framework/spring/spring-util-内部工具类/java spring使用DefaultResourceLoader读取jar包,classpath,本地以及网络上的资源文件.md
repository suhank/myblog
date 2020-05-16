# java spring使用DefaultResourceLoader读取jar包,classpath,本地以及网络上的资源文件

2018年12月09日 11:08:00 [weixin_34205076](https://me.csdn.net/weixin_34205076) 阅读数 98

 

spring使用DefaultResourceLoader读取jar包,classpath,本地以及网络上的资源文件
spring使用DefaultResourceLoader读取jar包,classpath,本地以及网络/远程的配置文件

```java
package com.pzy.component;
 
import lombok.Cleanup;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.util.ResourceUtils;
import org.testng.annotations.Test;
 
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
 
/**
 * @author pzy
 * @date 2018/12/9
 */
public class ResourceUtilTest {
    /**
     * 测试读取磁盘路径的文件
     *
     * @throws FileNotFoundException
     */
    @Test
    public void test01() throws IOException {
        Resource resource = new DefaultResourceLoader().getResource("file:/Users/pan/workspace/java/eclipse/compnent/web-starter/src/main/resources/META-INF/spring.factories");
        @Cleanup InputStream inputStream = resource.getInputStream();
        String content = IOUtils.toString(inputStream, "UTF-8");
        System.err.println(content);
    }
 
 
    /**
     * 测试读取classpath的文件(注意classpath下的文件不能以/开头)
     *
     * @throws FileNotFoundException
     */
    @Test
    public void test02() throws IOException {
        Resource resource = new DefaultResourceLoader().getResource("classpath:META-INF/spring.factories");
        @Cleanup InputStream inputStream = resource.getInputStream();
        String content = IOUtils.toString(inputStream, "UTF-8");
        System.err.println(content);
    }
 
    /**
     * 测试读取远程文件
     *
     * @throws FileNotFoundException
     */
    @Test
    public void test03() throws IOException {
        Resource resource = new DefaultResourceLoader().getResource("http://fex.baidu.com/ueditor/");
        @Cleanup InputStream inputStream = resource.getInputStream();
        String content = IOUtils.toString(inputStream, "UTF-8");
        System.err.println(content);
 
    }
}
 
```

<https://blog.csdn.net/weixin_34205076/article/details/87041360>