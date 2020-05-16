# java HttpServletRequest 获取完整url

```java
		HttpServletRequest request=(HttpServletRequest)httpServletRequest;  
        String strBackUrl = "http://" + request.getServerName() //服务器地址
                + ":"
                + request.getServerPort()//端口号
                + request.getContextPath()//项目名称
                + request.getServletPath()//请求页面或其他地址
                + "?" + (request.getQueryString());//参数

```



https://blog.csdn.net/qq_29290295/article/details/80429849