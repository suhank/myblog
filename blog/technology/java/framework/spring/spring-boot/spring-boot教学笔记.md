

[TOC]



# Spring-boot教学笔记

/Users/jerryye/backup/studio/AvailableCode/framework/spring/spring_boot/spring-boot-demo

## 第1节 Spring Boot Hello world

### Spring Boot介绍

#### 什么是Spring boot？

•Spring Boot是由Pivotal团队提供的全新框架，其设计目的是用来简化新Spring应用的初始搭建以及开发过程。该框架使用了特定的方式来进行配置，从而使开发人员不再需要定义样板化的配置。



#### Spring Boot特性

•1. 创建独立的Spring应用程序

•2. 嵌入的Tomcat，无需部署WAR文件

•3. 简化Maven配置

•4. 自动配置Spring

•5. 提供生产就绪型功能，如指标，健康检查和外部配置

•6.开箱即用，没有代码生成，也无需XML配置。



#### Spring Boot特性理解

•为基于Spring的开发提供更快的入门体验

•开箱即用，没有代码生成，也无需XML配置。同时也可以修改默认值来满足特定的需求。

•提供了一些大型项目中常见的非功能特性，如嵌入式服务器、安全、指标，健康检测、外部配置等。

•Spring Boot并不是对Spring功能上的增强，而是提供了一种快速使用Spring的方式。





### 开发准备



•开发环境JDK1.8

•开发工具(Eclipse)

•项目管理工具（Maven ）



#### Hello World 之新建project

创建Maven Project (spring-boot-hello)

#### Hello World 之pom.xml

```
<!-- 
spring boot 父节点依赖,引入这个之后相关的引入就不需要添加version配置，spring boot会自动选择最合适的版本进行添加。
 -->
<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>1.4.1.RELEASE</version>
	</parent>


java.version 指定jdk版本号：
<java.version>1.8</java.version>

添加spring-boot-starter-web依赖
<dependency>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-web</artifactId>
</dependency>

```

#### Hello World 之coding

•Codeing步骤：

•新建一个Controller类

•新建启动类(App– Main方法)

•测试代码



## 第2节 spring boot 返回json

•步骤：

•1. 编写实体类Demo

•2. 编写getDemo()方法

•3. 测试



## 第3节 Spring Boot 完美使用FastJson解析Json数据



### 简介

•个人使用比较习惯的json框架是fastjson,所以spring boot默认的json使用起来比较不习惯，所以很自然我就想我能不能使用fastjson进行json解析呢？

### 引入fastjson依赖库

```
<dependency>
			<groupId>com.alibaba</groupId>
			<artifactId>fastjson</artifactId>
			<version>1.2.15</version>
</dependency>
```

这里要说下很重要的话，官方文档说的1.2.10以后，会有两个方法支持HttpMessageconvert，一个是FastJsonHttpMessageConverter，支持4.2以下的版本，一个是FastJsonHttpMessageConverter4支持4.2以上的版本，具体有什么区别暂时没有深入研究。这里也就是说：低版本的就不支持了，所以这里最低要求就是1.2.10+。

### 配置fastjon(支持两种方法)

#### 第一种方法

•（1）启动类继承extendsWebMvcConfigurerAdapter

•（2）覆盖方法configureMessageConverters

代码：

```
@SpringBootApplication
public class App extends WebMvcConfigurerAdapter {
	
	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		super.configureMessageConverters(converters);

		/*
		 * 1、需要先定义一个 convert 转换消息的对象;
		 * 2、添加fastJson 的配置信息，比如：是否要格式化返回的json数据;
		 * 3、在convert中添加配置信息.
		 * 4、将convert添加到converters当中.
		 *
		 */

		// 1、需要先定义一个 convert 转换消息的对象;
		FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();

		//2、添加fastJson 的配置信息，比如：是否要格式化返回的json数据;
		FastJsonConfig fastJsonConfig = new FastJsonConfig();
		fastJsonConfig.setSerializerFeatures(
                SerializerFeature.PrettyFormat
        );

		//3、在convert中添加配置信息.
        fastConverter.setFastJsonConfig(fastJsonConfig);

        //4、将convert添加到converters当中.
    	converters.add(fastConverter);
	}
}
```

#### 第二种方法

•（1）在App.java启动类中，

•注入Bean: HttpMessageConverters

```
	/**
	 * 在这里我们使用 @Bean注入 fastJsonHttpMessageConvert
	 * @return
	 */
	@Bean
	public HttpMessageConverters fastJsonHttpMessageConverters() {
		// 1、需要先定义一个 convert 转换消息的对象;
		FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();

		//2、添加fastJson 的配置信息，比如：是否要格式化返回的json数据;
		FastJsonConfig fastJsonConfig = new FastJsonConfig();
		fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat);

		//3、在convert中添加配置信息.
		fastConverter.setFastJsonConfig(fastJsonConfig);


		HttpMessageConverter<?> converter = fastConverter;
		return new HttpMessageConverters(converter);
	}
```





## 第4节 Spring Boot热部署（springloaded） 不推荐

缺点：新增方法不支持，启动方式麻烦

### 问题的提出：

•在编写代码的时候，你会发现我们只是简单把打印信息改变了，就需要重新部署，如果是这样的编码方式，那么我们估计一天下来就真的是打几个Hello World就下班了。那么如何解决热部署的问题呢？那就是springloaded



### 使用方式

•在pom.xml文件添加依赖包：

```
<plugin>
	          		<groupId>org.springframework.boot</groupId>
	          		<artifactId>spring-boot-maven-plugin </artifactId>
	          		<dependencies>  
			           <!--springloaded  hot deploy -->  
			           <dependency>  
			               <groupId>org.springframework</groupId>  
			               <artifactId>springloaded</artifactId>  
			               <version>1.2.4.RELEASE</version>
			           </dependency>  
			        </dependencies>  
			        <executions>  
			           <execution>  
			               <goals>  
			                   <goal>repackage</goal>  
			               </goals>  
			               <configuration>  
			                   <classifier>exec</classifier>  
			               </configuration>  
			           </execution>  
		       		</executions>
</plugin>
```



### 运行方法一

•使用spring-boot:run



### 运行方法二

•如果使用的runas – java application的话，那么还需要做一些处理。

•把spring-loader-1.2.4.RELEASE.jar下载下来，放到项目的lib目录中，然后把IDEA的run参数里VM参数设置为：

•-javaagent:.\lib\springloaded-1.2.4.RELEASE.jar -noverify

•然后启动就可以了，这样在run as的时候，也能进行热部署



## 第5节 springboot + devtools（热部署）推荐

缺点：普通的项目不能使用。只有springboot才能使用

优点：

1、当我们修改了方法的返回值，是能够进行热部署的；

2、当我们重新创建了一个方法，是能够进行热部署的；

3、当我们重新创建了一个Class,是能够进行热部署的；



### 问题的提出：

•通过使用springloaded进行热部署，但是些代码修改了，并不会进行热部署。

### 介绍

•spring-boot-devtools是一个为开发者服务的一个模块，其中最重要的功能就是自动应用代码更改到最新的App上面去。原理是在发现代码有更改之后，重新启动应用，但是速度比手动停止后再启动还要更快，更快指的不是节省出来的手工操作的时间。

•其深层原理是使用了两个ClassLoader，一个Classloader加载那些不会改变的类（第三方Jar包），另一个ClassLoader加载会更改的类，称为 restart ClassLoader

•,这样在有代码更改的时候，原来的restart ClassLoader 被丢弃，重新创建一个restart ClassLoader，由于需要加载的类相比较少，所以实现了较快的重启时间（5秒以内）。





•1. devtools会监听classpath下的文件变动，并且会立即重启应用（发生在保存时机），注意：因为其采用的虚拟机机制，该项重启是很快的。

•2. devtools可以实现页面热部署（即页面修改后会立即生效，这个可以直接在application.properties文件中配置spring.thymeleaf.cache=false来实现(这里注意不同的模板配置不一样)。

### 使用方法

添加依赖包：

```xml
        <!--热部署-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <optional>true</optional>
            <scope>true</scope>
        </dependency>
```

添加spring-boot-maven-plugin：

```xml
<build>
		<plugins>
		    <plugin>
	            <groupId>org.springframework.boot</groupId>
	            <artifactId>spring-boot-maven-plugin</artifactId>
	            <configuration>
	          		<!--fork :  如果没有该项配置，这个devtools不会起作用，即应用不会restart -->
	                <fork>true</fork>
	            </configuration>
	        </plugin>
		</plugins>
   </build>
```

### 测试方法

•修改类-->保存：应用会重启

•修改配置文件-->保存：应用会重启

•修改页面-->保存：应用会重启，页面会刷新（原理是将spring.thymeleaf.cache设为false）



启动:

mvn spring-boot:run启动或者 右键application debug启动Java文件时，系统会监视classes文件，当有classes文件被改动时，系统会重新加载类文件，不用重启启动服务。

注：IDEA下需要重新编译文件 Ctrl+Shift+F9或者编译项目 Ctrl+F9

### 不能使用分析

•对应的spring-boot版本是否正确，这里使用的是1.4.1版本；

•是否加入plugin以及属性\<fork>true\</fork>

•Eclipse Project 是否开启了BuildAutomatically（我自己就在这里栽了坑，不知道为什么我的工具什么时候关闭了自动编译的功能）。

•如果设置SpringApplication.setRegisterShutdownHook(false)，则自动重启将不起作用。





## 第6节  Spring Boot JPA/Hibernate/Spring Data概念



### 简介

如何在Spring Boot中使用JPA。在具体介绍之前我们有必要先介绍《JPA/Hibernate/Spring Data概念》



【从零开始学习Spirng Boot—常见异常汇总】

事情的起源，无意当中在一个群里看到这么一句描述：”有人么？默默的问一句，现在开发用mybatis还是hibernate还是jpa”?然后大家就进行各种回答，但是没有有质疑这句话描述的合理性，个人觉得需要清楚概念的，在这里mybatis大家肯定是没有什么疑问，我们把上面那句话更改下，方便我们抛出一些点出来，去掉mybatis修改为：“现在开发是使用hibernate还是jpa”?那么在这里的话，我们就要清楚hibernate/jpa/spring

data/spring data jpa到底怎么一个关系？

### **什么是JPA?**

JPA全称Java Persistence API.JPA通过JDK 5.0注解或XML描述对象－关系表的映射关系，并将运行期的实体对象持久化到数据库中。[百度百科JPA](http://baike.baidu.com/link?url=5x4ncJTsKTOKerWYJlMwHLoPINPP6VGi33BlAvWDKC5RrGCmhNYKvVcmcop1gNW4fjH1ILnLqDZHlzOw_f7-6a)

在上面只是一个JPA的定义，我们看看另外一段更能看出是什么的描述：

JPA(Java Persistence API)是Sun官方提出的Java持久化规范。它为Java开发人员提供了一种对象/关系映射工具来管理Java应用中的关系数据。

在这段话就比较清晰了，这里有一个关键词“持久化规范”。我们可以拆成两部分进行解读“持久化”、“规范”。所谓的持久化是将程序数据在瞬时数据（比如内存中的数据）转换为持久数据（比如：保存到数据库中，磁盘文件…）。这个个人粗糙的描述，看看专业的描述，如下：

持久化（Persistence），即把数据（如内存中的对象）保存到可永久保存的存储设备中（如磁盘）。持久化的主要应用是将内存中的对象存储在的数据库中，或者存储在磁盘文件中、XML数据文件中等等。

持久化是将程序数据在持久状态和瞬时状态间转换的机制。

JDBC就是一种持久化机制。文件IO也是一种持久化机制。

好了，上面已经描述很清楚了，我们在说说“规范”： 所谓的规范意指明文规定或约定俗成的标准。如：道德规范、技术规范，公司管理规范。

那么“持久化规范”就是Sun针对持久化这一层操作指定的规范，如果没有指定JPA规范，那么新起的框架就随意按照自己的标准来了，那我们开发者的世界就玩完了，我们就没法把我们的经历全部集中在我们的业务层上，而是在想我们进行兼容了，这种情况有点像Android开发，Android本身有官方的SDK,但是由于SDK过于开源了，结果导致很多厂商基于SDK二次开发，但是开发完兼容性就不是很好，最好的例子就是Android的头像上传，就是一件很烦人的事情。好了，JPA就唠叨到这里。

### **什么是Hibernate?**

这里引用百度百科的话[hibernate](http://baike.baidu.com/link?url=VBjBmRmgo1_Rn3XOkSJ4RfmPzaar9UNH9Oi1LyWdsvwiKK5wgmnm6spC1aCsuWkhvhOSOPML1UxQmAfmHu8Q_mhUBRRHvmId7vkS2sFdMM7)：

Hibernate是一个开放源代码的对象关系映射框架，它对JDBC进行了非常轻量级的对象封装，它将POJO与数据库表建立映射关系，是一个全自动的orm框架，hibernate可以自动生成SQL语句，自动执行，使得Java程序员可以随心所欲的使用对象编程思维来操纵数据库。Hibernate可以应用在任何使用JDBC的场合，既可以在Java的客户端程序使用，也可以在Servlet/JSP的Web应用中使用，最具革命意义的是，Hibernate可以在应用EJB的[J2EE](http://baike.baidu.com/view/1507.htm)架构中取代CMP，完成[数据持久化](http://baike.baidu.com/view/4549557.htm)的重任。

在上面这段描述中抓住核心的一句话就可以了“是一个全自动的ORM框架”。那么是ORM呢? ORM是对象关系映射的意思，英语：Object Relational Mapping简称ORM，是一种程序技术，用于实现面向对象编程语言里不同系统类型的系统之间的数据转换。好了，更多的概念需要自己去挖掘，这里只是抛装引玉下。

### **什么是Spring Data?**

Spring Data是一个用于简化数据库访问，并支持云服务的开源框架。其主要目标是使得数据库的访问变得方便快捷，并支持map-reduce框架和云计算数据服务。此外，它还支持基于关系型数据库的数据服务，如Oracle

RAC等。对于拥有海量数据的项目，可以用Spring Data来简化项目的开发，就如Spring Framework对JDBC、ORM的支持一样，Spring Data会让数据的访问变得更加方便。

在上面这段描述中我觉得核心的就是“Spring Data是用于简化数据库访问，支持云服务的开源框架”。所以Spring Data本身就是一个开源的框架。

### **什么是Spring Data JPA?**

我们先看一个描述：

Spring Data JPA能干什么

可以极大的简化JPA的写法，可以在几乎不用写实现的情况下，实现对数据的访问和操作。除了CRUD外，还包括如分页、排序等一些常用的功能。

首先我们需要清楚的是Spring Data是一个开源框架，在这个框架中Spring Data JPA只是这个框架中的一个模块，所以名称才叫Spring

Data JPA。如果单独使用JPA开发，你会发现这个代码量和使用JDBC开发一样有点烦人，所以Spring Data JPA的出现就是为了简化JPA的写法，让你只需要编写一个接口继承一个类就能实现CRUD操作了。

### **JPA/Hibernate**关系？

我们先看下别人的描述：

Jpa是一种规范，而Hibernate是它的一种实现。除了Hibernate，还有EclipseLink(曾经的toplink)，OpenJPA等可供选择，所以使用Jpa的一个好处是，可以更换实现而不必改动太多代码。

从上面这个描述，我们能就是能看出: JPA定义了一个规范，Hibernate是其中的一种实现方式可以，所以我们可以说：Hibernate是JPA的一种实现方式。但是这么说就有点欠妥当了：开发是使用hibernate还是jpa。如果你回答使用JPA的话，那么你根本做不了什么事情，因为你需要使用它具体的一种实现方式，比如：Hibernate,EclipseLink,toplink。如果回答说是使用Hibernate的话，还勉强说的过去，但是在Hibernate中也有JPA的影子。但是这里不要造成一个误解，hibernate一定依赖JPA什么之类的，JPA现在只是Hibernate功能的一个子集。Hibernate从3.2开始，开始兼容JPA的。Hibernate3.2获得了Sun

TCK的JPA(JavaPersistence API)兼容认证。

那么我们在描述的时候，别人问你持久化具体使用了什么，我们可以说：使用了基于Hibernate实现的JPA，或者是Hibernate JPA，那么加上spring data的，我们一般都简化说：spring data jpa，一般默认的就是使用了hibernate进行实现，现在网上这方面的资料也比较多，可能就约定俗成了。当然你要别人清楚的话，可以自己在进行补充下。

好了，这个困惑就到这里，在这里就是博主个人的一些见解，有什么个别的见解都可以在评论中探讨，如有错误之处，请指正。

这篇也是博主花了一些心血去梳理的，请大家都都支持。







## 第7节 Spring Boot JPA-Hibernate





### 在pom.xml添加mysql,spring-data-jpa依赖

```
		<!-- 添加MySQL数据库驱动依赖包. -->
		<dependency>
				<groupId>mysql</groupId>
				<artifactId>mysql-connector-java</artifactId>
		</dependency>
		
		<!-- 添加Spring-data-jpa依赖. -->
		<dependency>
		    <groupId>org.springframework.boot</groupId>
		    <artifactId>spring-boot-starter-data-jpa</artifactId>
		</dependency>
```

### 在application.properties文件中配置mysql连接配置文件

```
########################################################
###datasource
########################################################
spring.datasource.url = jdbc:mysql://localhost:3306/test
spring.datasource.username = root
spring.datasource.password = root
spring.datasource.driverClassName = com.mysql.jdbc.Driver
spring.datasource.max-active=20
spring.datasource.max-idle=8
spring.datasource.min-idle=8
spring.datasource.initial-size=10
```

### 在application.properties文件中配置JPA配置信息

```
########################################################
### Java Persistence Api
########################################################
# Specify the DBMS
spring.jpa.database = MYSQL
# Show or not log for each sql query
spring.jpa.show-sql = true
# Hibernate ddl auto (create, create-drop, update)
spring.jpa.hibernate.ddl-auto = update
# Naming strategy
#[org.hibernate.cfg.ImprovedNamingStrategy  #org.hibernate.cfg.DefaultNamingStrategy]
spring.jpa.hibernate.naming-strategy = org.hibernate.cfg.ImprovedNamingStrategy
# stripped before adding them to the entity manager)
spring.jpa.properties.hibernate.dialect = org.hibernate.dialect.MySQL5Dialect
```



### 编写测试例子

(1)  创建实体类Demo,如果已经存在，可以忽略。

(2)  创建jparepository类操作持久化(CrudRepository)。

(3)  创建service类。

(4)  创建restful请求类。

(5)  测试;





## 第8节 Spring Boot JPA接口介绍

• Spring Data 的一个核心接口为我们提供了常用的接口，在这节我们就来简单介绍下。



### Repository接口

Repository接口是 SpringData 的一个核心接口，它不提供任何方法，开发者需要在自己定义的接口中声明需要的方法 ：

```
 publicinterface Repository<T, ID extends Serializable> { } 
```



有这么几点需要强调下：

1. Repository是一个空接口，即是一个标记接口；

2. 若我们定义的接口继承了Repository，则该接口会被IOC容器识别为一个Repository Bean纳入到IOC容器中，进而可以在该接口中定义满足一定规范的方法。

3. 实际上也可以通过@RepositoryDefinition,注解来替代继承Repository接口。

4. 查询方法以find | read | get开头；

5. 涉及查询条件时，条件的属性用条件关键字连接，要注意的是条件属性以首字母大写。

6.使用@Query注解可以自定义JPQL语句实现更灵活的查询。



CrudRepository 接口提供了最基本的对实体类的添删改查操作

• --T save(T entity);//保存单个实体   

• --Iterable<T>save(Iterable<?extends T> entities);//保存集合        

• --T findOne(IDid);//根据id查找实体         

• --booleanexists(ID id);//根据id判断实体是否存在         

• --Iterable<T>findAll();//查询所有实体,不用或慎用!         

• --long count();//查询实体数量         

• --void delete(ID id);//根据Id删除实体         

• --void delete(T entity);//删除一个实体  

• --void delete(Iterable<? extends T> entities);//删除一个实体的集合         

• --void deleteAll();//删除所有实体,不用或慎用!  



### PagingAndSortingRepository接口

该接口提供了分页与排序功能   

 --Iterable<T>findAll(Sortsort); //排序   

--Page<T> findAll(Pageablepageable);//分页查询（含排序功能） 



### 其它接口

JpaRepository：查找所有实体，排序、查找所有实体，执行缓存与数据库同步

JpaSpecificationExecutor：不属于Repository体系，实现一组 JPA Criteria 查询相关的方法，封装 JPA Criteria 查询条件。通常使用匿名内部类的方式来创建该接口的对象。

自定义 Repository：可以自己定义一个MyRepository接口。





## 第9节 Spring Boot JdbcTemplate

### 在pom.xml加入jdbcTemplate的依赖

```
<dependency>
		    <groupId>org.springframework.boot</groupId>
		    <artifactId>spring-boot-starter-jdbc</artifactId>
</dependency>

如果在JPA已经加入的话，则可以不用引入以上的配置。
<dependency>
		    <groupId>org.springframework.boot</groupId>
		    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

```



### 代码使用

```
那么只需要在需要使用的类中加入：
@Resource
private JdbcTemplate jdbcTemplate;

```

### 编写DemoDao类

•声明为：@Repository，引入JdbcTemplate

```
public Demo getById(long id){
	String sql = "select *from Demo where id=?";
	RowMapper<Demo> rowMapper = new BeanPropertyRowMapper<Demo>(Demo.class);
	return jdbcTemplate.queryForObject(sql, rowMapper,id);
}

```

编写DemoService类，引入DemoDao进行使用

```
@Resource
private DemoDao demoDao;

public void save(Demo demo){
	 demoDao.save(demo);
}

```

### 编写Demo2Controller进行简单测试

```
@Resource
private DemoService demoService;

@RequestMapping("/getById")
public Demo getById(long id){
	return demoService.getById(id);
}

```

## 第10节全局异常捕捉

在一个项目中的异常我们我们都会统一进行处理的，那么如何进行统一进行处理呢？

新建一个类GlobalDefaultExceptionHandler，

在class注解上@ControllerAdvice,

在方法上注解上@ExceptionHandler(value = Exception.class)，具体代码如下：

### 核心代码

```
@ControllerAdvice
public class GlobalDefaultExceptionHandler{
	
	@ExceptionHandler(value = Exception.class)
	public void defaultErrorHandler(HttpServletRequest req, Exception e)  {
}
```




## 第11节 Spring Boot之Hello World访问404



（1）404 -- 确定地址是否输入正确，，此路径非彼路径
（2）404 -- 是否用对注解，此注解非彼注解
（3）404 -- 包路径是否正确，此包非彼包

（4）404 -- 确认类包是否正确，此类包非彼类包



## 第12节 配置server信息

### 配置端口号

Springboot 默认端口是8080，如果想要进行更改的话，只需要修改applicatoin.properties文件，在配置文件中加入：

server.port=8081



### 配置context-path

在application.properties进行配置：

server.context-path=/spring-boot

访问地址就是[http://ip:port/spring-boot](http://ip:port/spring-boot)



### 其它server配置

```
#server.port=8080
#server.address= # bind to a specific NIC
#server.session-timeout= # session timeout in seconds
#the context path, defaults to '/'
#server.context-path=/spring-boot
#server.servlet-path= # the servlet path, defaults to '/'
#server.tomcat.access-log-pattern= # log pattern of the access log
#server.tomcat.access-log-enabled=false # is access logging enabled
#server.tomcat.protocol-header=x-forwarded-proto # ssl forward headers
#server.tomcat.remote-ip-header=x-forwarded-for
#server.tomcat.basedir=/tmp # base dir (usually not needed, defaults to tmp)
#server.tomcat.background-processor-delay=30; # in seconds
#server.tomcat.max-threads = 0 # number of threads in protocol handler
#server.tomcat.uri-encoding = UTF-8 # character encoding to use for URL decoding
```





## 第13节 spring boot使用thymeleaf

### 在pom.xml中引入thymeleaf

在pom.xml加入thymeleaf的依赖：

```
<dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>

```



### 如何关闭thymeleaf缓存

```
########################################################
###THYMELEAF (ThymeleafAutoConfiguration)
########################################################
#spring.thymeleaf.prefix=classpath:/templates/
#spring.thymeleaf.suffix=.html
#spring.thymeleaf.mode=HTML5
#spring.thymeleaf.encoding=UTF-8
# ;charset=<encoding> is added
#spring.thymeleaf.content-type=text/html 
# set to false for hot refresh
spring.thymeleaf.cache=false 
```

### 编写模板文件.html

编写模板文件src/main/resouces/templates/hello.html:

```
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:th="http://www.thymeleaf.org"
      xmlns:sec="http://www.thymeleaf.org/thymeleaf-extras-springsecurity3">
    <head>
        <title>Hello World!</title>
    </head>
    <body>
        <h1 th:inline="text">Hello.v.2</h1>
        <p th:text="${hello}"></p>
    </body>
</html>

```

### 编写访问模板文件controller



```
@Controller
public class TemplateController {
	
	/**
	 * 返回html模板.
	 */
	@RequestMapping("/helloHtml")
	public String helloHtml(Map<String,Object> map){
		map.put("hello","from TemplateController.helloHtml");
		return "/helloHtml";
	}
	
}
```

## 第14节 Spring Boot 使用freemarker

### 在pom.xml中引入freemarker

```
<dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-freemarker</artifactId>
</dependency>

```



### 如何关闭freemarker缓存

```
########################################################
###FREEMARKER (FreeMarkerAutoConfiguration)
########################################################
spring.freemarker.allow-request-override=false
spring.freemarker.cache=true
spring.freemarker.check-template-location=true
spring.freemarker.charset=UTF-8
spring.freemarker.content-type=text/html
spring.freemarker.expose-request-attributes=false
spring.freemarker.expose-session-attributes=false
spring.freemarker.expose-spring-macro-helpers=false
#spring.freemarker.prefix=
#spring.freemarker.request-context-attribute=
#spring.freemarker.settings.*=
#spring.freemarker.suffix=.ftl
#spring.freemarker.template-loader-path=classpath:/templates/ #comma-separated list
#spring.freemarker.view-names= # whitelist of view names that can be resolved

```

### 编写模板文件.ftl

```
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:th="http://www.thymeleaf.org"
      xmlns:sec="http://www.thymeleaf.org/thymeleaf-extras-springsecurity3">
    <head>
        <title>Hello World!</title>
    </head>
    <body>
        <h1>Hello.v.2</h1>
        <p>${hello}</p>
    </body>
</html>

```



### 编写访问文件的controller

```
@RequestMapping("/helloFtl")
	public String helloFtl(Map<String,Object> map){
		map.put("hello","from TemplateController.helloFtl");
		return "/helloFtl";
	}

```

## 第15节 Spring Boot添加JSP支持

### （1）创建Maven web project

使用Eclipse新建一个Maven Web Project ，项目取名为：

spring-boot-jsp

### （2）在pom.xml文件添加依赖

```
<!-- spring boot parent节点，引入这个之后，在下面和spring boot相关的就不需要引入版本了; -->
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>1.4.1.RELEASE</version>
	</parent>

 <!-- 指定一下jdk的版本 ，这里我们使用jdk 1.8 ,默认是1.6 -->
    <java.version>1.8</java.version>
<!-- web支持: 1、web mvc; 2、restful; 3、jackjson支持; 4、aop ........ -->
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>

<!-- servlet 依赖. -->
		<dependency>
			<groupId>javax.servlet</groupId>
			<artifactId>javax.servlet-api</artifactId>
			<scope>provided</scope>
		</dependency>

JSTL（JSP Standard Tag Library，JSP标准标签库)是一个不断完善的开放源代码的JSP标签库，是由apache的jakarta小组来维护的。
<dependency>
			<groupId>javax.servlet</groupId>
			<artifactId>jstl</artifactId>
		</dependency>

<!-- tomcat 的支持.-->
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-tomcat</artifactId>
			<scope>provided</scope>
		</dependency>
		<dependency>
			<groupId>org.apache.tomcat.embed</groupId>
			<artifactId>tomcat-embed-jasper</artifactId>
			<scope>provided</scope>
		</dependency>

```

### （3）application.properties配置

添加src/main/resources/application.properties：

```
# 页面默认前缀目录
spring.mvc.view.prefix=/WEB-INF/jsp/
# 响应页面默认后缀
spring.mvc.view.suffix=.jsp
# 自定义属性，可以在Controller中读取
application.hello=Hello Angel From application

```

### （4）编写测试Controller

```
@Controller
public class HelloController {
private String hello;    
	
	@RequestMapping("/helloJsp")
	public String helloJsp(Map<String,Object> map){
		System.out.println("HelloController.helloJsp().hello=hello");
		map.put("hello", hello);
		return "helloJsp";
	}
}

```

### （5）编写JSP页面

在
src/main
下面创建 webapp/WEB-INF/jsp 目录用来存放我们的jsp页面：helloJsp.jsp:

```
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
	helloJsp
	<hr>
	${hello}
	
</body>
</html>

```

### （6）编写启动类

```
@SpringBootApplication
public class App {
	
	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}
}

```

### 注意

•特别说明：针对el表达式，类似${hello}
这个对于servlet的版本是有限制的，2.4版本版本以下是不支持的，是无法进行识别的，请注意。



## 第16节 Spring Boot集成MyBatis

这一章节在另一个项目中



### 操作步骤

（1）新建maven project;

（2）在pom.xml文件中引入相关依赖；

（3）创建启动类App.java

（4）在application.properties添加配置文件；

（5）编写Demo测试类;

（6）编写DemoMapper；

（7）编写DemoService

（8）编写DemoController;

（9）加入PageHelper

（10）获取自增长ID;



### （1）新建maven project;

新建一个maven project,取名为：spring-boot-mybatis



### （2）在pom.xml文件中引入相关依赖

（1）基本依赖，jdk版本号；

（2）mysql驱动，mybatis依赖包，mysql分页PageHelper:

```
<!-- mysql 数据库驱动. -->
    <dependency>
		<groupId>mysql</groupId>
		<artifactId>mysql-connector-java</artifactId>
</dependency>	
<!-- 	
			spring-boot mybatis依赖：
			
			请不要使用1.0.0版本，因为还不支持拦截器插件，
	    	1.1.1 是博主写帖子时候的版本，大家使用最新版本即可
	     -->
	<dependency>
	    <groupId>org.mybatis.spring.boot</groupId>
	    <artifactId>mybatis-spring-boot-starter</artifactId>
	    <version>1.1.1</version>
	</dependency>
<!-- 
    	MyBatis提供了拦截器接口，我们可以实现自己的拦截器，
    	将其作为一个plugin装入到SqlSessionFactory中。 
		Github上有位开发者写了一个分页插件，我觉得使用起来还可以，挺方便的。 
		Github项目地址： https://github.com/pagehelper/Mybatis-PageHelper
     -->	
    <dependency>
	    <groupId>com.github.pagehelper</groupId>
	    <artifactId>pagehelper</artifactId>
	    <version>4.1.0</version>
	</dependency>	

```

### （3）创建启动类App.java

```
@SpringBootApplication
@MapperScan("com.kfit.*.mapper")
public class App {
	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}
}

//这里和以往不一样的地方就是MapperScan的注解，这个是会扫描该包下的接口

```

### （4）在application.properties添加配置文件

```
########################################################
###datasource
########################################################
spring.datasource.url = jdbc:mysql://localhost:3306/test
spring.datasource.username = root
spring.datasource.password = root
spring.datasource.driverClassName = com.mysql.jdbc.Driver
spring.datasource.max-active=20
spring.datasource.max-idle=8
spring.datasource.min-idle=8
spring.datasource.initial-size=10

```

### （5）编写Demo测试类

```
public class Demo {
	private long id;
	private String name;
      //省略getter and setter….
}

```

### （6）编写DemoMapper

```
public interface DemoMappper {
	
	@Select("select *from Demo where name = #{name}")
	public List<Demo> likeName(String name);
	
	@Select("select *from Demo where id = #{id}")
	public Demo getById(long id);
	
	@Select("select name from Demo where id = #{id}")
	public String getNameById(long id);
}

```



### （7）编写DemoService

```
@Service
public class DemoService {
	@Autowired
	private DemoMappper demoMappper;
	
    public List<Demo> likeName(String name){
        return demoMappper.likeName(name);
    }
}

```

### （8）编写DemoController

```
@RestController
public class DemoController {
	@Autowired
	private DemoService demoService;
	
	@RequestMapping("/likeName")
	public List<Demo> likeName(String name){
		return demoService.likeName(name);
	}
	
}

//运行访问：http://127.0.0.1:8080/likeName?name=张三  就可以看到返回的数据了

```

### （9）加入PageHelper

```
@Configuration
public class MyBatisConfiguration {
	
	@Bean
    public PageHelper pageHelper() {
		System.out.println("MyBatisConfiguration.pageHelper()");
        PageHelper pageHelper = new PageHelper();
        Properties p = new Properties();
        p.setProperty("offsetAsPageNum", "true");
        p.setProperty("rowBoundsWithCount", "true");
        p.setProperty("reasonable", "true");
        pageHelper.setProperties(p);
        return pageHelper;
    }
}

```

```
@RequestMapping("/likeName")
public List<Demo> likeName(String name){
		 PageHelper.startPage(1,1);
	     return demoService.likeName(name);
}

```



### （10）获取自增长ID

```
@Insert("insert into Demo(name,password) values(#{name},#{password})")
public long save(Demo name);
@Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id") 

```