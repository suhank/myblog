[TOC]



# 使用spring自带工具对uri进行通配符匹配

自己做项目的时候碰到一个要对请求的uri进行过滤的需求，而过滤配置则是在配置文件里以通配符的方式存入的 

在网上搜索很久，大部分方法研究都是调用了Spring-security的包。通过阅读Spring源码发现在Spring-core包里已经有相关方法的实现了 

对应的类就是org.springframework.util.AntPathMatcher 

调用方法很简单，new一个对象后直接调用match方法即可匹配 
给出案例： 

```
	@Test  
    public void testMatch(){  
        AntPathMatcher matcher = new AntPathMatcher();  
        String pattern = "/abc/**/a.jsp";  
        System.out.println("pattern:"+pattern);  
        System.out.println("/abc/aa/bb/a.jsp:"+matcher.match(pattern,"/abc/aa/bb/a.jsp"));  
        System.out.println("/aBc/aa/bb/a.jsp:"+matcher.match(pattern,"/aBc/aa/bb/a.jsp"));  
        System.out.println("/abc/a.jsp:"+matcher.match(pattern,"/abc/a.jsp"));  
    }  
```



测试结果： 
pattern:/abc/**/a.jsp 
/abc/aa/bb/a.jsp:true 
/aBc/aa/bb/a.jsp:false 
/abc/a.jsp:true 

可以看出这个类的匹配大小写敏感，可用通配符为:?,*,** 
?表示单个字符 
*表示一层路径内的任意字符串，不可跨层级 
**表示任意层路径 

如果有人对具体实现算法感兴趣可以自行阅读AntPathMatcher类的源码。





http://yixiandave.iteye.com/blog/1883734








