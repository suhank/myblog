[TOC]



# SpringBoot 优雅的整合Shiro

2018年10月14日 11:10:30

 

**Apache Shiro**是一个功能强大且易于使用的Java安全框架，可执行身份验证，授权，加密和会话管理。借助Shiro易于理解的API，您可以快速轻松地保护任何应用程序 - 从最小的移动应用程序到最大的Web和企业应用程序。网上找到大部分文章都是以前SpringMVC下的整合方式，很多人都不知道shiro提供了官方的starter可以方便地跟SpringBoot整合。

请看shiro官网关于springboot整合shiro的链接：[Integrating Apache Shiro into Spring-Boot Applications](https://shiro.apache.org/spring-boot.html)

## 整合准备

这篇文档的介绍也相当简单。我们只需要按照文档说明，然后在spring容器中注入一个我们自定义的`Realm`，shiro通过这个realm就可以知道如何获取用户信息来处理`鉴权（Authentication）`，如何获取用户角色、权限信息来处理`授权（Authorization）`。如果是web应用程序的话需要引入`shiro-spring-boot-web-starter`，单独的应用程序的话则引入`shiro-spring-boot-starter`。

### 依赖

```xml
<dependency>
    <groupId>org.apache.shiro</groupId>
    <artifactId>shiro-spring-boot-web-starter</artifactId>
    <version>1.4.0-RC2</version>
</dependency>
12345
```

### 用户实体

首先创建一个用户的实体，用来做认证

```java
package com.maoxs.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
public class User  implements Serializable {
    private Long uid;       // 用户id
    private String uname;   // 登录名，不可改
    private String nick;    // 用户昵称，可改
    private String pwd;     // 已加密的登录密码
    private String salt;    // 加密盐值
    private Date created;   // 创建时间
    private Date updated;   // 修改时间
    private Set<String> roles = new HashSet<>();    //用户所有角色值，用于shiro做角色权限的判断
    private Set<String> perms = new HashSet<>();    //用户所有权限值，用于shiro做资源权限的判断
}
123456789101112131415161718192021
```

这里了为了方便，就不去数据库读取了，方便测试我们把，权限信息，角色信息，认证信息都静态模拟下。

### Resources

```java
package com.maoxs.service;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class ResourcesService {
    /**
     * 模拟根据用户id查询返回用户的所有权限
     *
     * @param uid
     * @return
     */
    public Set<String> getResourcesByUserId(Long uid) {
        Set<String> perms = new HashSet<>();
        //三种编程语言代表三种角色：js程序员、java程序员、c++程序员
        //docker的权限
        perms.add("docker:run");
        perms.add("docker:ps");
        //maven的权限
        perms.add("mvn:debug");
        perms.add("mvn:test");
        perms.add("mvn:install");
        //node的权限
        perms.add("npm:clean");
        perms.add("npm:run");
        perms.add("npm:test");
        return perms;
    }

}
123456789101112131415161718192021222324252627282930313233
```

### Role

```java
package com.maoxs.service;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class RoleService {

    /**
     * 模拟根据用户id查询返回用户的所有角色
     *
     * @param uid
     * @return
     */
    public Set<String> getRolesByUserId(Long uid) {
        Set<String> roles = new HashSet<>();
        //这里用三个工具代表角色
        roles.add("docker");
        roles.add("maven");
        roles.add("node");
        return roles;
    }

}
 
```

### User

```java
package com.maoxs.service;

import com.maoxs.pojo.User;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Random;

@Service
public class UserService {

    /**
     * 模拟查询返回用户信息
     *
     * @param uname
     * @return
     */
    public User findUserByName(String uname) {
        User user = new User();
        user.setUname(uname);
        user.setNick(uname + "NICK");
        user.setPwd("J/ms7qTJtqmysekuY8/v1TAS+VKqXdH5sB7ulXZOWho=");//密码明文是123456
        user.setSalt("wxKYXuTPST5SG0jMQzVPsg==");//加密密码的盐值
        user.setUid(new Random().nextLong());//随机分配一个id
        user.setCreated(new Date());
        return user;
    }
}
12345678910111213141516171819202122232425262728
```

## 认证

Shiro 从从Realm获取安全数据（如用户、角色、权限），就是说SecurityManager要验证用户身份，那么它需要从Realm获取相应的用户进行比较以确定用户身份是否合法；也需要从Realm得到用户相应的角色/权限进行验证用户是否能进行操作；可以把Realm看成DataSource ， 即安全数据源。

### Realm

```java
package com.maoxs.realm;

import com.maoxs.cache.MySimpleByteSource;
import com.maoxs.pojo.User;
import com.maoxs.service.ResourcesService;
import com.maoxs.service.RoleService;
import com.maoxs.service.UserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

/**
 * 这个类是参照JDBCRealm写的，主要是自定义了如何查询用户信息，如何查询用户的角色和权限，如何校验密码等逻辑
 */
public class CustomRealm extends AuthorizingRealm {

    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private ResourcesService resourcesService;

    //告诉shiro如何根据获取到的用户信息中的密码和盐值来校验密码
    {
        //设置用于匹配密码的CredentialsMatcher
        HashedCredentialsMatcher hashMatcher = new HashedCredentialsMatcher();
        hashMatcher.setHashAlgorithmName(Sha256Hash.ALGORITHM_NAME);
        hashMatcher.setStoredCredentialsHexEncoded(false);
        hashMatcher.setHashIterations(1024);
        this.setCredentialsMatcher(hashMatcher);
    }


    //定义如何获取用户的角色和权限的逻辑，给shiro做权限判断
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        //null usernames are invalid
        if (principals == null) {
            throw new AuthorizationException("PrincipalCollection method argument cannot be null.");
        }
        User user = (User) getAvailablePrincipal(principals);
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        System.out.println("获取角色信息：" + user.getRoles());
        System.out.println("获取权限信息：" + user.getPerms());
        info.setRoles(user.getRoles());
        info.setStringPermissions(user.getPerms());
        return info;
    }

    //定义如何获取用户信息的业务逻辑，给shiro做登录
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken upToken = (UsernamePasswordToken) token;
        String username = upToken.getUsername();
        // Null username is invalid
        if (username == null) {
            throw new AccountException("请输入用户名");
        }
        User userDB = userService.findUserByName(username);
        if (userDB == null) {
            throw new UnknownAccountException("用户不存在");
        }
        //查询用户的角色和权限存到SimpleAuthenticationInfo中，这样在其它地方
        //SecurityUtils.getSubject().getPrincipal()就能拿出用户的所有信息，包括角色和权限
        Set<String> roles = roleService.getRolesByUserId(userDB.getUid());
        Set<String> perms = resourcesService.getResourcesByUserId(userDB.getUid());
        userDB.getRoles().addAll(roles);
        userDB.getPerms().addAll(perms);
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(userDB, userDB.getPwd(), getName());
        if (userDB.getSalt() != null) {
            info.setCredentialsSalt(ByteSource.Util.bytes(userDB.getSalt()));
        }
        return info;
    }

}
12345678910111213141516171819202122232425262728293031323334353637383940414243444546474849505152535455565758596061626364656667686970717273747576777879808182838485
```

### 相关配置

然后呢在只需要吧这个Realm注册到Spring容器中就可以啦

```java
@Bean
public CustomRealm customRealm() {
   CustomRealm realm = new CustomRealm();
   return realm;  
}
12345
```

为了保证实现了Shiro内部lifecycle函数的bean执行 也是shiro的生命周期，注入LifecycleBeanPostProcessor

```java
@Bean
public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
	return new LifecycleBeanPostProcessor();
}
1234
```

紧接着配置安全管理器，SecurityManager是Shiro框架的核心，典型的Facade模式，Shiro通过SecurityManager来管理内部组件实例，并通过它来提供安全管理的各种服务。

```java
@Bean
public DefaultWebSecurityManager securityManager() {
    DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
    securityManager.setRealm(customRealm());
    return securityManager;
}

1234567
```

除此之外Shiro是一堆一堆的过滤链，所以要对shiro 的过滤进行设置，

```java
@Bean
public ShiroFilterChainDefinition shiroFilterChainDefinition() {
    DefaultShiroFilterChainDefinition chainDefinition = new DefaultShiroFilterChainDefinition();
    chainDefinition.addPathDefinition("favicon.ico", "anon");
    chainDefinition.addPathDefinition("/login", "anon");
    chainDefinition.addPathDefinition("/**", "user");
    return chainDefinition;
}
12345678
```

如果想要自定义过滤链那么 `ShiroFilterChainDefinition` 就不ok了 我们就要换个写法 定义一个 `ShiroFilterFactoryBean`

```java
 /**
  * 不需要在此处配置权限页面,因为上面的ShiroFilterFactoryBean已经配置过,
  * 但是此处必须存在,因为shiro-spring-boot-web-starter或查找此Bean,没有会报错
  *
  * @return
  */
@Bean
public ShiroFilterChainDefinition shiroFilterChainDefinition() {
    return new DefaultShiroFilterChainDefinition();
}

/* *********************************************shiro过滤连**********************************************/
@Bean
 public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
     ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
     shiroFilterFactoryBean.setSecurityManager(securityManager);
     //拦截器
     Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
     // 添加自己的过滤器并且取名
     Map<String, Filter> filterMap = new HashMap<>(16);
     filterMap.put("my", new MyFilter());
     shiroFilterFactoryBean.setFilters(filterMap);
     filterChainDefinitionMap.put("login", "anon");
     //<!-- 过滤链定义，从上向下顺序执行，一般将/**放在最为下边
     filterChainDefinitionMap.put("/**", "jwt");
     shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
     return shiroFilterFactoryBean;
 }
12345678910111213141516171819202122232425262728
```

### yml

这里要说明下由于我们引入的是`shiro-spring-boot-web-starter`，官方对配置进行了一系列的简化，并加入了一些自动配置项，所以我们要在yml中加入

```yaml
shiro:
  web:
    enabled: true
  loginUrl: /login
1234
```

除此之外呢还有这些属性

| 键                                                           | 默认值       | 描述                                                   |
| ------------------------------------------------------------ | ------------ | ------------------------------------------------------ |
| shiro.enabled                                                | `true`       | 启用Shiro的Spring模块                                  |
| shiro.web.enabled                                            | `true`       | 启用Shiro的Spring Web模块                              |
| shiro.annotations.enabled                                    | `true`       | 为Shiro的注释启用Spring支持                            |
| shiro.sessionManager.deleteInvalidSessions                   | `true`       | 从会话存储中删除无效会话                               |
| shiro.sessionManager.sessionIdCookieEnabled                  | `true`       | 启用会话ID到cookie，用于会话跟踪                       |
| shiro.sessionManager.sessionIdUrlRewritingEnabled            | `true`       | 启用会话URL重写支持                                    |
| shiro.userNativeSessionManager                               | `false`      | 如果启用，Shiro将管理HTTP会话而不是容器                |
| [shiro.sessionManager.cookie.name](http://shiro.sessionmanager.cookie.name/) | `JSESSIONID` | 会话cookie名称                                         |
| shiro.sessionManager.cookie.maxAge                           | `-1`         | 会话cookie最大年龄                                     |
| shiro.sessionManager.cookie.domain                           | 空值         | 会话cookie域                                           |
| shiro.sessionManager.cookie.path                             | 空值         | 会话cookie路径                                         |
| shiro.sessionManager.cookie.secure                           | `false`      | 会话cookie安全标志                                     |
| [shiro.rememberMeManager.cookie.name](http://shiro.remembermemanager.cookie.name/) | `rememberMe` | RememberMe cookie名称                                  |
| shiro.rememberMeManager.cookie.maxAge                        | 一年         | RememberMe cookie最大年龄                              |
| shiro.rememberMeManager.cookie.domain                        | 空值         | RememberMe cookie域名                                  |
| shiro.rememberMeManager.cookie.path                          | 空值         | RememberMe cookie路径                                  |
| shiro.rememberMeManager.cookie.secure                        | `false`      | RememberMe cookie安全标志                              |
| shiro.loginUrl                                               | `/login.jsp` | 未经身份验证的用户重定向到登录页面时使用的登录URL      |
| shiro.successUrl                                             | `/`          | 用户登录后的默认登录页面（如果在当前会话中找不到替代） |
| shiro.unauthorizedUrl                                        | 空值         | 页面将用户重定向到未授权的位置（403页）                |

在Controller中添加登录方法

```java
@RequestMapping(value = "/login", method = RequestMethod.POST)
@ResponseBody
public Result login(@RequestParam("username") String userName, @RequestParam("password") String Password) throws Exception {
    Subject currentUser = SecurityUtils.getSubject();
    UsernamePasswordToken token = new UsernamePasswordToken(userName, Password);
    token.setRememberMe(true);// 默认不记住密码
    try {
        currentUser.login(token); //登录
        log.info("==========登录成功=======");
        return new Result(true, "登录成功");

    } catch (UnknownAccountException e) {
        log.info("==========用户名不存在=======");
        return new Result(false, "用户名不存在");
    } catch (DisabledAccountException e) {
        log.info("==========您的账户已经被冻结=======");
        return new Result(false, "您的账户已经被冻结");
    } catch (IncorrectCredentialsException e) {
        log.info("==========密码错误=======");
        return new Result(false, "密码错误");
    } catch (ExcessiveAttemptsException e) {
        log.info("==========您错误的次数太多了吧,封你半小时=======");
        return new Result(false, "您错误的次数太多了吧,封你半小时");
    } catch (RuntimeException e) {
        log.info("==========运行异常=======");
        return new Result(false, "运行异常");
    }
}
@RequestMapping("/logout")
public String logOut() {
    Subject subject = SecurityUtils.getSubject();
    subject.logout();
    return "index";
}
 
```

这样就实现了整合认证的流程，，如果token信息与数据库表总username和password数据一致，则该用户身份认证成功。

## 鉴权

### 只用注解控制鉴权授权

使用注解的优点是控制的粒度细，并且非常适合用来做基于资源的权限控制。
切记加入aop

```xml
<dependency>
     <groupId>org.springframework.boot</groupId>
     <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
1234
```

只用注解的话非常简单。我们只需要使用url配置配置一下所以请求路径都可以匿名访问：

```java
@Bean
public ShiroFilterChainDefinition shiroFilterChainDefinition() {
	DefaultShiroFilterChainDefinition chain = new DefaultShiroFilterChainDefinition();
    //这里配置所有请求路径都可以匿名访问
    chain.addPathDefinition("/**", "anon");
    // 这另一种配置方式。但是还是用上面那种吧，容易理解一点。
    // chainDefinition.addPathDefinition("/**", "authcBasic[permissive]");
    return chain;
}
123456789
```

然后在控制器类上使用shiro提供的种注解来做控制：

| 注解                    | 功能                                 |
| ----------------------- | ------------------------------------ |
| @RequiresGuest          | 只有游客可以访问                     |
| @RequiresAuthentication | 需要登录才能访问                     |
| @RequiresUser           | 已登录的用户或“记住我”的用户能访问   |
| @RequiresRoles          | 已登录的用户需具有指定的角色才能访问 |
| @RequiresPermissions    | 已登录的用户需具有指定的权限才能访问 |

示例

```java
@RestController
public class Test1Controller {
    // 由于TestController类上没有加@RequiresAuthentication注解，
    // 不要求用户登录才能调用接口。所以hello()和a1()接口都是可以匿名访问的
    @GetMapping("/hello")
    public String hello() {
        return "hello spring boot";
    }

    // 游客可访问，这个有点坑，游客的意思是指：subject.getPrincipal()==null
    // 所以用户在未登录时subject.getPrincipal()==null，接口可访问
    // 而用户登录后subject.getPrincipal()！=null，接口不可访问
    @RequiresGuest
    @GetMapping("/guest")
    public String guest() {
        return "@RequiresGuest";
    }

    // 已登录用户才能访问，这个注解比@RequiresUser更严格
    // 如果用户未登录调用该接口，会抛出UnauthenticatedException
    @RequiresAuthentication
    @GetMapping("/authn")
    public String authn() {
        return "@RequiresAuthentication";
    }

    // 已登录用户或“记住我”的用户可以访问
    // 如果用户未登录或不是“记住我”的用户调用该接口，UnauthenticatedException
    @RequiresUser
    @GetMapping("/user")
    public String user() {
        return "@RequiresUser";
    }

    // 要求登录的用户具有mvn:build权限才能访问
    // 由于UserService模拟返回的用户信息中有该权限，所以这个接口可以访问
    // 如果没有登录，UnauthenticatedException
    @RequiresPermissions("mvn:install")
    @GetMapping("/mvnInstall")
    public String mvnInstall() {
        return "mvn:install";
    }

    // 要求登录的用户具有mvn:build权限才能访问
    // 由于UserService模拟返回的用户信息中【没有】该权限，所以这个接口【不可以】访问
    // 如果没有登录，UnauthenticatedException
    // 如果登录了，但是没有这个权限，会报错UnauthorizedException
    @RequiresPermissions("gradleBuild")
    @GetMapping("/gradleBuild")
    public String gradleBuild() {
        return "gradleBuild";
    }

    // 要求登录的用户具有js角色才能访问
    // 由于UserService模拟返回的用户信息中有该角色，所以这个接口可访问
    // 如果没有登录，UnauthenticatedException
    @RequiresRoles("docker")
    @GetMapping("/docker")
    public String docker() {
        return "docker programmer";
    }

    // 要求登录的用户具有js角色才能访问
    // 由于UserService模拟返回的用户信息中有该角色，所以这个接口可访问
    // 如果没有登录，UnauthenticatedException
    // 如果登录了，但是没有该角色，会抛出UnauthorizedException
    @RequiresRoles("python")
    @GetMapping("/python")
    public String python() {
        return "python programmer";
    }

}
12345678910111213141516171819202122232425262728293031323334353637383940414243444546474849505152535455565758596061626364656667686970717273
```

**注意** 解决spring aop和注解配置一起使用的bug。如果您在使用shiro注解配置的同时，引入了spring aop的starter，会有一个奇怪的问题，导致shiro注解的请求，不能被映射，需加入以下配置：

```java
/**
* setUsePrefix(false)用于解决一个奇怪的bug。在引入spring aop的情况下。
* 在@Controller注解的类的方法中加入@RequiresRole等shiro注解，会导致该方法无法映射请求，
* 导致返回404。加入这项配置能解决这个bug
*/
@Bean
@DependsOn("lifecycleBeanPostProcessor")
public static DefaultAdvisorAutoProxyCreator getDefaultAdvisorAutoProxyCreator(){
        DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator=new DefaultAdvisorAutoProxyCreator();
        defaultAdvisorAutoProxyCreator.setUsePrefix(true);
        return defaultAdvisorAutoProxyCreator;
}
123456789101112
```

### 只用url配置控制鉴权授权

shiro提供和多个默认的过滤器，我们可以用这些过滤器来配置控制指定url的权限：

| 配置缩写          | 对应的过滤器                   | 功能                                                         |
| ----------------- | ------------------------------ | ------------------------------------------------------------ |
| anon              | AnonymousFilter                | 指定url可以匿名访问                                          |
| authc             | FormAuthenticationFilter       | 指定url需要form表单登录，默认会从请求中获取`username`、`password`,`rememberMe`等参数并尝试登录，如果登录不了就会跳转到loginUrl配置的路径。我们也可以用这个过滤器做默认的登录逻辑，但是一般都是我们自己在控制器写登录逻辑的，自己写的话出错返回的信息都可以定制嘛。 |
| authcBasic        | BasicHttpAuthenticationFilter  | 指定url需要basic登录                                         |
| logout            | LogoutFilter                   | 登出过滤器，配置指定url就可以实现退出功能，非常方便          |
| noSessionCreation | NoSessionCreationFilter        | 禁止创建会话                                                 |
| perms             | PermissionsAuthorizationFilter | 需要指定权限才能访问                                         |
| port              | PortFilter                     | 需要指定端口才能访问                                         |
| rest              | HttpMethodPermissionFilter     | 将http请求方法转化成相应的动词来构造一个权限字符串，这个感觉意义不大，有兴趣自己看源码的注释 |
| roles             | RolesAuthorizationFilter       | 需要指定角色才能访问                                         |
| ssl               | SslFilter                      | 需要https请求才能访问                                        |
| user              | UserFilter                     | 需要已登录或“记住我”的用户才能访问                           |

在spring容器中使用`ShiroFilterChainDefinition`来控制所有url的鉴权和授权。优点是配置粒度大，对多个Controller做鉴权授权的控制。下面是栗子

```java
@Bean
public ShiroFilterChainDefinition shiroFilterChainDefinition() {
    DefaultShiroFilterChainDefinition chain = new DefaultShiroFilterChainDefinition();
    /**
    * 这里小心踩坑！我在application.yml中设置的context-path: /api/v1
    * 但经过实际测试，过滤器的过滤路径，是context-path下的路径，无需加上"/api/v1"前缀
     */
    //访问控制
    chain.addPathDefinition("/user/login", "anon");//可以匿名访问
    chain.addPathDefinition("/page/401", "anon");//可以匿名访问
    chain.addPathDefinition("/page/403", "anon");//可以匿名访问
    chain.addPathDefinition("/my/hello", "anon");//可以匿名访问
    chain.addPathDefinition("/my/changePwd", "authc");//需要登录
    chain.addPathDefinition("/my/user", "user");//已登录或“记住我”的用户可以访问
    chain.addPathDefinition("/my/mvnBuild", "authc,perms[mvn:install]");//需要mvn:build权限
    chain.addPathDefinition("/my/npmClean", "authc,perms[npm:clean]");//需要npm:clean权限
    chain.addPathDefinition("/my/docker", "authc,roles[docker]");//需要js角色
    chain.addPathDefinition("/my/python", "authc,roles[python]");//需要python角色
    // shiro 提供的登出过滤器，访问指定的请求，就会执行登录，默认跳转路径是"/"，或者是"shiro.loginUrl"配置的内容
    // 由于application-shiro.yml中配置了 shiro:loginUrl: /page/401，返回会返回对应的json内容
    // 可以结合/user/login和/t1/js接口来测试这个/t4/logout接口是否有效
    chain.addPathDefinition("/logout", "anon,logout");
    //其它路径均需要登录
    chain.addPathDefinition("/**", "authc");
    return chain;
}

123456789101112131415161718192021222324252627
```

### 二者结合，url配置控制鉴权，注解控制授权

就个人而言，我是非常喜欢注解方式的。但是两种配置方式灵活结合，才是适应不同应用场景的最佳实践。只用注解或只用url配置，会带来一些比较累的工作。`用url配置控制鉴权，实现粗粒度控制；用注解控制授权，实现细粒度控制`。下面是示例:

```java
/**
 * 这里统一做鉴权，即判断哪些请求路径需要用户登录，哪些请求路径不需要用户登录。
 * 这里只做鉴权，不做权限控制，因为权限用注解来做。
 * @return
 */
@Bean
public ShiroFilterChainDefinition shiroFilterChainDefinition() {
    DefaultShiroFilterChainDefinition chain = new DefaultShiroFilterChainDefinition();
    //哪些请求可以匿名访问
    chain.addPathDefinition("/user/login", "anon");
    chain.addPathDefinition("/page/401", "anon");
    chain.addPathDefinition("/page/403", "anon");
    chain.addPathDefinition("/hello", "anon");
    chain.addPathDefinition("/guest", "anon");
    //除了以上的请求外，其它请求都需要登录
    chain.addPathDefinition("/**", "authc");
    return chain;
}
123456789101112131415161718
@RestController
public class Test5Controller {

    // 由于ShiroConfig中配置了该路径可以匿名访问，所以这接口不需要登录就能访问
    @GetMapping("/hello")
    public String hello() {
        return "hello spring boot";
    }

    // 如果ShiroConfig中没有配置该路径可以匿名访问，所以直接被登录过滤了。
    // 如果配置了可以匿名访问，那这里在没有登录的时候可以访问，但是用户登录后就不能访问
    @RequiresGuest
    @GetMapping("/guest")
    public String guest() {
        return "@RequiresGuest";
    }

    @RequiresAuthentication
    @GetMapping("/authn")
    public String authn() {
        return "@RequiresAuthentication";
    }

    @RequiresUser
    @GetMapping("/user")
    public String user() {
        return "@RequiresUser";
    }

    @RequiresPermissions("mvn:install")
    @GetMapping("/mvnInstall")
    public String mvnInstall() {
        return "mvn:install";
    }

    @RequiresPermissions("gradleBuild")
    @GetMapping("/gradleBuild")
    public String gradleBuild() {
        return "gradleBuild";
    }

    @RequiresRoles("python")
    @GetMapping("/python")
    public String python() {
        return "python programmer";
    }

}
123456789101112131415161718192021222324252627282930313233343536373839404142434445464748
```

## 记住我

记住我功能在各大网站是比较常见的，实现起来也是大同小异，主要就是利用cookie来实现，而shiro对记住我功能的实现也是比较简单的，只需要几步即可。

首先呢配置下Cookie的生成模版，配置下cookie的name，cookie的有效时间等等。

```java
@Bean
public SimpleCookie rememberMeCookie() {
    //System.out.println("ShiroConfiguration.rememberMeCookie()");
    //这个参数是cookie的名称，对应前端的checkbox的name = rememberMe
    SimpleCookie simpleCookie = new SimpleCookie("rememberMe");
    //<!-- 记住我cookie生效时间30天 ,单位秒;-->
    simpleCookie.setMaxAge(259200);
    return simpleCookie;
}
```

然后呢配置rememberMeManager

```java
@Bean
public CookieRememberMeManager rememberMeManager() {
    //System.out.println("ShiroConfiguration.rememberMeManager()");
    CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
    cookieRememberMeManager.setCookie(rememberMeCookie());
    //rememberMe cookie加密的密钥 建议每个项目都不一样 默认AES算法 密钥长度(128 256 512 位)
    cookieRememberMeManager.setCipherKey(Base64.decode("2AvVhdsgUs0FSA3SDFAdag=="));
    return cookieRememberMeManager;
} 
```

rememberMeManager()方法是生成rememberMe管理器，而且要将这个rememberMe管理器设置到securityManager中。

```java
@Bean
public DefaultWebSecurityManager securityManager() {
    DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
    securityManager.setRealm(customRealm(redisCacheManager));
    securityManager.setRememberMeManager(rememberMeManager());
    return securityManager;
}
 
```

好了记住我功能就到这里了，不过要记住一点，如果使用了authc的过滤的url的是不能使用记住我功能的，切记，至于什么原因，很好理解。有一些操作你是不需要别人在记住我功能下完成的，这样很不安全，**所以shiro规定记住我功能最多得user级别的，不能到authc级别**。

## 启用缓存

Shiro提供了类似Spring的Cache抽象，即Shiro本身不实现Cache，但是对Cache进行了又抽象，方便更换不同的底层Cache实现。对应前端的一个页面访问请求会同时出现很多的权限查询操作，这对于权限信息变化不是很频繁的场景，每次前端页面访问都进行大量的权限数据库查询是非常不经济的。因此，非常有必要对权限数据使用缓存方案。

由于Spring和Shiro都各自维护了自己的Cache抽象，为防止Realm注入的service里缓存注解和事务注解失效，所以定义自己的CacheManager处理缓存。

### 整合Redis

CacheManager代码如下。

```java
package com.maoxs.cache;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.util.Destroyable;
import org.springframework.data.redis.cache.RedisCacheManager;

import java.util.Collection;
import java.util.Set;

public class ShiroRedisCacheManager implements CacheManager, Destroyable {
    private RedisCacheManager cacheManager;

    public RedisCacheManager getCacheManager() {
        return cacheManager;
    }

    public void setCacheManager(RedisCacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    //为了个性化配置redis存储时的key，我们选择了加前缀的方式，所以写了一个带名字及redis操作的构造函数的Cache类
    public <K, V> Cache<K, V> getCache(String name) throws CacheException {
        if (name == null) {
            return null;
        }
        return new ShiroRedisCache<K, V>(name, getCacheManager());
    }

    @Override
    public void destroy() throws Exception {
        cacheManager = null;
    }

    /**
    * <p> 自定义缓存 将数据存入到redis中 </p>
    *
    * @param <K>
    * @param <V>
    * @author xxx
    * @date 2018年2月1日
    * @time 22:32:11
    */
    @Slf4j
    class ShiroRedisCache<K, V> implements org.apache.shiro.cache.Cache<K, V> {
        private RedisCacheManager cacheManager;
        private org.springframework.cache.Cache cache;

        //    private RedisCache cache2;
        public ShiroRedisCache(String name, RedisCacheManager cacheManager) {
            if (name == null || cacheManager == null) {
                throw new IllegalArgumentException("cacheManager or CacheName cannot be null.");
            }
            this.cacheManager = cacheManager;
            //这里首先是从父类中获取这个cache,如果没有会创建一个redisCache,初始化这个redisCache的时候
            //会设置它的过期时间如果没有配置过这个缓存的，那么默认的缓存时间是为0的，如果配置了，就会把配置的时间赋予给这个RedisCache
            //如果从缓存的过期时间为0，就表示这个RedisCache不存在了，这个redisCache实现了spring中的cache
            this.cache = cacheManager.getCache(name);
        }

        @Override
        public V get(K key) throws CacheException {
            log.info("从缓存中获取key为{}的缓存信息", key);
            if (key == null) {
                return null;
            }
            org.springframework.cache.Cache.ValueWrapper valueWrapper = cache.get(key);
            if (valueWrapper == null) {
                return null;
            }
            return (V) valueWrapper.get();
        }

        @Override
        public V put(K key, V value) throws CacheException {
            log.info("创建新的缓存，信息为：{}={}", key, value);
            cache.put(key, value);
            return get(key);
        }

        @Override
        public V remove(K key) throws CacheException {
            log.info("干掉key为{}的缓存", key);
            V v = get(key);
            cache.evict(key);//干掉这个名字为key的缓存
            return v;
        }

        @Override
        public void clear() throws CacheException {
            log.info("清空所有的缓存");
            cache.clear();
        }

        @Override
        public int size() {
            return cacheManager.getCacheNames().size();
        }

        /**
         * 获取缓存中所的key值
         */
        @Override
        public Set<K> keys() {
            return (Set<K>) cacheManager.getCacheNames();
        }

        /**
         * 获取缓存中所有的values值
         */
        @Override
        public Collection<V> values() {
            return (Collection<V>) cache.get(cacheManager.getCacheNames()).get();
        }

        @Override
        public String toString() {
            return "ShiroSpringCache [cache=" + cache + "]";
        }
    }
}
```

然后呢就是把这个CacheManager注入到securityManager中

```java
@Bean
public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
    RedisTemplate<Object, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);
    //使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值（默认使用JDK的序列化方式）
    Jackson2JsonRedisSerializer serializer = new Jackson2JsonRedisSerializer(Object.class);
    ObjectMapper mapper = new ObjectMapper();
    mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
    mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
    serializer.setObjectMapper(mapper);
    template.setValueSerializer(serializer);
    //使用StringRedisSerializer来序列化和反序列化redis的key值
    template.setKeySerializer(new StringRedisSerializer());
    template.afterPropertiesSet();
    return template;
}
/**
* Spring缓存管理器配置
*
* @param redisTemplate
* @return
*/
@Bean
public RedisCacheManager redisCacheManager(RedisTemplate redisTemplate) {
    CollectionSerializer<Serializable> collectionSerializer = CollectionSerializer.getInstance();
    RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(redisTemplate.getConnectionFactory());
    RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
        .entryTtl(Duration.ofHours(1))
        .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(collectionSerializer));
    return new RedisCacheManager(redisCacheWriter, redisCacheConfiguration);
}
/**
* shiro缓存管理器的配置
*
* @param redisCacheManager
* @return
*/
@Bean
public ShiroRedisCacheManager shiroRedisCacheManager(RedisCacheManager redisCacheManager) {
    ShiroRedisCacheManager cacheManager = new ShiroRedisCacheManager();
    cacheManager.setCacheManager(redisCacheManager);
    //name是key的前缀，可以设置任何值，无影响，可以设置带项目特色的值
    return cacheManager;
}
1234567891011121314151617181920212223242526272829303132333435363738394041424344
```

相对应的Realm和securityManager也要稍做更改

```java
@Bean
public CustomRealm customRealm(RedisCacheManager redisCacheManager) {
    CustomRealm realm = new CustomRealm();
    realm.setCachingEnabled(true);
    //设置认证密码算法及迭代复杂度
    //realm.setCredentialsMatcher(credentialsMatcher());
    //认证
    realm.setCacheManager(shiroRedisCacheManager(redisCacheManager));
    realm.setAuthenticationCachingEnabled(true);
    //授权
    realm.setAuthorizationCachingEnabled(true);
    //这里主要是缓存key的名字
    realm.setAuthenticationCacheName("fulinauthen");
    realm.setAuthenticationCacheName("fulinauthor");
    return realm;
}
@Bean
public DefaultWebSecurityManager securityManager(RedisCacheManager redisCacheManager) {
    DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
    securityManager.setRealm(customRealm(redisCacheManager));
    securityManager.setCacheManager(shiroRedisCacheManager(redisCacheManager));
    securityManager.setRememberMeManager(rememberMeManager());
    return securityManager;
}
123456789101112131415161718192021222324
```

这样的话每次认证的时候就会把权限信息放入redis中，就不用反复的去查询数据库了。

#### 注意

**Realm里注入的UserService等service，需要延迟注入，所以都要添加@Lazy注解(如果不加需要自己延迟注入)，否则会导致该service里的@Cacheable缓存注解、@Transactional事务注解等失效**。

整合的时候应该会有人遇到不能序列化的问题吧,原因是因为用了Shiro的SimpleAuthenticationInfo中的setCredentialsSalt注入的属性ByteSource没有实现序列化接口，此时呢只用把源码一贴，实现下序列化接口即可

```java
package com.maoxs.cache;

import org.apache.shiro.codec.Base64;
import org.apache.shiro.codec.CodecSupport;
import org.apache.shiro.codec.Hex;
import org.apache.shiro.util.ByteSource;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Arrays;

/**
 * 解决ByteSource 序列化问题
 */
public class MySimpleByteSource implements ByteSource, Serializable {
    private byte[] bytes;
    private String cachedHex;
    private String cachedBase64;

    public MySimpleByteSource() {
    }

    public MySimpleByteSource(byte[] bytes) {
        this.bytes = bytes;
    }

    public MySimpleByteSource(char[] chars) {
        this.bytes = CodecSupport.toBytes(chars);
    }

    public MySimpleByteSource(String string) {
        this.bytes = CodecSupport.toBytes(string);
    }

    public MySimpleByteSource(ByteSource source) {
        this.bytes = source.getBytes();
    }

    public MySimpleByteSource(File file) {
        this.bytes = (new MySimpleByteSource.BytesHelper()).getBytes(file);
    }

    public MySimpleByteSource(InputStream stream) {
        this.bytes = (new MySimpleByteSource.BytesHelper()).getBytes(stream);
    }

    public static boolean isCompatible(Object o) {
        return o instanceof byte[] || o instanceof char[] || o instanceof String || o instanceof ByteSource || o instanceof File || o instanceof InputStream;
    }

    public byte[] getBytes() {
        return this.bytes;
    }

    public boolean isEmpty() {
        return this.bytes == null || this.bytes.length == 0;
    }

    public String toHex() {
        if (this.cachedHex == null) {
            this.cachedHex = Hex.encodeToString(this.getBytes());
        }

        return this.cachedHex;
    }

    public String toBase64() {
        if (this.cachedBase64 == null) {
            this.cachedBase64 = Base64.encodeToString(this.getBytes());
        }

        return this.cachedBase64;
    }

    public String toString() {
        return this.toBase64();
    }

    public int hashCode() {
        return this.bytes != null && this.bytes.length != 0 ? Arrays.hashCode(this.bytes) : 0;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof ByteSource) {
            ByteSource bs = (ByteSource) o;
            return Arrays.equals(this.getBytes(), bs.getBytes());
        } else {
            return false;
        }
    }

    private static final class BytesHelper extends CodecSupport {
        private BytesHelper() {
        }

        public byte[] getBytes(File file) {
            return this.toBytes(file);
        }

        public byte[] getBytes(InputStream stream) {
            return this.toBytes(stream);
        }
    }
}
123456789101112131415161718192021222324252627282930313233343536373839404142434445464748495051525354555657585960616263646566676869707172737475767778798081828384858687888990919293949596979899100101102103104105106107
```

然后在realm中改变使用

```java
if (userDB.getSalt() != null) {
	info.setCredentialsSalt(new MySimpleByteSource(userDB.getSalt()));
}
123
```

### 整合Ehcache

整合ehcache就更简单，套路都是一样的只不过2.x和3.x 需要注入不同的CacheManager即可。这里需要注入下3.x的Ehcache是实现了Jcache，不过整合起来都是一样的，详情可以去看我之前的整合Spring抽象缓存的帖子。

官方提供了shiro-ehcache的整合包，不过这个整合包是针对Ehcache2.x的。

## Redis存储Session

关于共享session的问题大家都应该知道了，传统的部署项目，两个相同的项目部署到不同的服务器上，Nginx负载均衡后会导致用户在A上登陆了，经过负载均衡后，在B上要重新登录，因为A上有相关session信息，而B没有。这种情况也称为“有状态”服务。而“无状态”服务则是：在一个公共的地方存储session，每次访问都会统一到这个地方来拿。思路呢就是实现Shiro的Session接口，然后呢自己控制，这里我们实现AbstractSessionDAO。

```java
package com.maoxs.cache;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.TimeUnit;


@Slf4j
public class ShiroRedisSessionDao extends AbstractSessionDAO {

    private RedisTemplate redisTemplate;

    public ShiroRedisSessionDao(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void update(Session session) throws UnknownSessionException {
        log.info("更新seesion,id=[{}]", session.getId().toString());
        try {
            redisTemplate.opsForValue().set(session.getId().toString(), session, 30, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void delete(Session session) {
        log.info("删除seesion,id=[{}]", session.getId().toString());
        try {
            String key = session.getId().toString();
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.info(e.getMessage(), e);
        }

    }

    @Override
    public Collection<Session> getActiveSessions() {
        log.info("获取存活的session");
        return Collections.emptySet();
    }

    @Override
    protected Serializable doCreate(Session session) {
        Serializable sessionId = generateSessionId(session);
        assignSessionId(session, sessionId);
        log.info("创建seesion,id=[{}]", session.getId().toString());
        try {
            redisTemplate.opsForValue().set(session.getId().toString(), session, 30, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return sessionId;
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        log.info("获取seesion,id=[{}]", sessionId.toString());
        Session readSession = null;
        try {
            readSession = (Session) redisTemplate.opsForValue().get(sessionId.toString());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return readSession;
    }

}
```

最后吧你写好的SessionDao注入到shiro的securityManager中即可

```java
/**
* 配置sessionmanager，由redis存储数据
*/
@Bean(name = "sessionManager")
public DefaultWebSessionManager sessionManager(RedisTemplate redisTemplate) {
    DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
    CollectionSerializer<Serializable> collectionSerializer = CollectionSerializer.getInstance();
    redisTemplate.setDefaultSerializer(collectionSerializer);
    //redisTemplate默认采用的其实是valueSerializer，就算是采用其他ops也一样，这是一个坑。
    redisTemplate.setValueSerializer(collectionSerializer);
    ShiroRedisSessionDao redisSessionDao = new ShiroRedisSessionDao(redisTemplate);
    //这个name的作用也不大，只是有特色的cookie的名称。
    sessionManager.setSessionDAO(redisSessionDao);
    sessionManager.setDeleteInvalidSessions(true);
    SimpleCookie cookie = new SimpleCookie();
    cookie.setName("starrkCookie");
    sessionManager.setSessionIdCookie(cookie);
    sessionManager.setSessionIdCookieEnabled(true);
    return sessionManager;
}
@Bean
public DefaultWebSecurityManager securityManager(RedisTemplate redisTemplate, RedisCacheManager redisCacheManager) {
    DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
    securityManager.setRealm(customRealm(redisCacheManager));
    securityManager.setCacheManager(shiroRedisCacheManager(redisCacheManager));
    securityManager.setRememberMeManager(rememberMeManager());
    securityManager.setSessionManager(sessionManager(redisTemplate));
    return securityManager;
}
 
```

这样每次读取Session就会从Redis中取读取了，当然还有谢谢开源的插件解决方案，比如crazycake ，有机会在补充这个。

**本博文是基于springboot2.x 如果有什么不对的请在下方留言。**

 







<https://blog.csdn.net/qq_32867467/article/details/83045505>