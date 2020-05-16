[TOC]



# spring boot 解决HttpServletRequest inputStream只能读取一次的问题 ResponseBody



/Users/jerryye/backup/studio/AvailableCode/framework/spring/spring_mvc/接口签名校验_解决HttpServletRequestinputStream只能读取一次的问题_filter_interceptor/springboot_mvc_filter_interceptor_signature_demo

## 简介

最近打算在之前的项目APP接口里面加入验证签名的功能，实现思路很简单，就是通过添加filter的方式，在filter中读取所有的请求参数，然后验证客户端传过来的SIGN值跟服务端生成的SIGN值是否一致。

目前的接口里面参数有K=V格式的，也有JSON格式的，对于前者在filter中通过HttpServletRequest.getParameterMap就可以直接获取。但是对于JSON参数，我们需要从request的inputStream中读取，当然这也不复杂，几行代码就可以搞定了。

但是当我完成filter中的代码，在进行调试的时候，发现原来controller中通过@RequestBody获取JSON参数的接口抛出“Required request body is missing”的错误。刚开始还以为参数传的有问题，查阅相关资料才明白inputStream的数据只能读取一次，从inputStream中读取过数据之后，后续再从inputStream中就不能再读取到数据了。然后就继续在网上翻资料，有的说需要自己重写HttpServletRequestWrapper，有的说通过spring自带的ContentCachingRequestWrapper就可以，我是一个比较懒的人，如果有现成好用的我才懒得自己写呢，先试试看。

```java
    @Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper((HttpServletRequest) request);
		String body = IOUtils.toString(request.getInputStream(),request.getCharacterEncoding());
		//TODO 验证签名
		
		chain.doFilter(requestWrapper, response);
	}
```

然而并没有什么用 WTF ... 查看ContentCachingRequestWrapper源码发现它也只是把inputStream的数据读取出来存到cachedContent里面了，后面仍然不能再次读取inputStream, 但是我看网上有几个都说这样可以，后来才发现他们是在 chain.doFilter(requestWrapper, response)之后再通过requestWrapper.getContentAsByteArray方法获取数据，或者在filter中读取了inputstream之后，在controller接口方法中使用requestWrapper.getContentAsByteArray方法再获取数据，这样就不能使用@RequestBody自动注入参数了，这显然不是我想要的。

看来还得自己动手，我们要做的就是读取了inputStream之后，再将数据写回去，具体实现如下：

```java
package com.spbd.core.web.filter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.io.IOUtils;

public class ContentCachingRequestWrapper extends HttpServletRequestWrapper{
	
	private byte[] body;
	
	private BufferedReader reader;

	private ServletInputStream inputStream;

	public ContentCachingRequestWrapper(HttpServletRequest request) throws IOException{
		super(request);
		loadBody(request);
	}
	
	private void loadBody(HttpServletRequest request) throws IOException{
		body = IOUtils.toByteArray(request.getInputStream());
		inputStream = new RequestCachingInputStream(body);
	}
	
	public byte[] getBody() {
		return body;
	}
	
	@Override
	public ServletInputStream getInputStream() throws IOException {
		if (inputStream != null) {			
			return inputStream;
		}
        return super.getInputStream();
	}

	@Override
	public BufferedReader getReader() throws IOException {
		if (reader == null) {
            reader = new BufferedReader(new InputStreamReader(inputStream, getCharacterEncoding()));
        }
        return reader;
	}
	
	private static class RequestCachingInputStream extends ServletInputStream {
		
        private final ByteArrayInputStream inputStream;

        public RequestCachingInputStream(byte[] bytes) {
            inputStream = new ByteArrayInputStream(bytes);
        }
        @Override
        public int read() throws IOException {
            return inputStream.read();
        }

		@Override
		public boolean isFinished() {
			return inputStream.available() == 0;
		}

		@Override
		public boolean isReady() {
			return true;
		}

		@Override
		public void setReadListener(ReadListener readlistener) {
		}

    }
	
}
```

<https://github.com/sergewu/demo>

THE END





https://my.oschina.net/serge/blog/1094063