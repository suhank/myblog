[TOC]



# springboot 实现Filter过滤器

 SpringBoot实现过滤器和SpringMVC上实现没有多大差别，主要的差别就是在过滤器注册上，SpringMVC是通过XML配置文件注册过滤器，而SpringBoot则是通过代码注解的形式进行注册。下面一起看下在SpringBoot上怎么实现Filter过滤器吧。

## Filter过滤器具体实现类

```java
/**
 * 权限验证过滤器
 */
public class AuthFilter implements Filter {

    @Override
    public void destroy() {
        // 顾名思义，在销毁时使用
    }

    @Override
    public void doFilter(ServletRequest arg0, ServletResponse arg1,
            FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)arg0;
        HttpServletResponse response = (HttpServletResponse)arg1;
        if(needLogin(request)) {
            // 需要登录则跳转到登录Controller
            response.sendRedirect("login");
            return;
        }
        chain.doFilter(arg0, arg1);
    }

    @Override
    public void init(FilterConfig config) throws ServletException {
        // 初始化操作
    }

    /**
     * 判断是否需要登录
     * @param request
     * @return
     */
    private boolean needLogin(HttpServletRequest request) {
        //进行是否需要登录的判断操作
        return false;
    }

} 
```

这里实现了一个用作权限验证的过滤器，来判断用户是否登录，若没有登陆，则跳转到登录界面，否则继续下面你的操作。Filter的实现及操作都和SpringMVC上一样，这里就不做过多解释了。

## 注册Filter过滤器

```java
/**
 * 自定义配置项类，该类中和存入拦截器、过滤器等配置项信息
 * @author Administrator
 */
@Configuration
public class CustemConfigurerAdapter {

    @Bean
    public FilterRegistrationBean authFilterRegistrationBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setName("authFilter");
        AuthFilter authFilter = new AuthFilter();
        registrationBean.setFilter(authFilter);
        registrationBean.setOrder(1);
        List<String> urlList = new ArrayList<String>();
        urlList.add("/*");
        registrationBean.setUrlPatterns(urlList);
        return registrationBean;
    }

} 
```

上面是一个自定义的配置项注册类。使用`@Configuration`标签是为了让SpringBoot知道这个类是配置类，需要进行注册。在`@Configuration`中，声明注解`@Bean`相当于在Spring老版本中在配置文件中声明一个Bean。

`authFilterRegistrationBean`方法是对AuthFilter过滤类的注册，`urlList.add("/*")`是添加这个过滤器需要过滤的URL地址，可以添加多个；`registrationBean.setOrder(1)`是设置该过滤器执行的顺序。SpringBoot会根据order从小到大的顺序执行。





https://blog.csdn.net/A632189007/article/details/78596676