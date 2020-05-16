# springboot mybatis多数据源 报错Parameter 0 of method SqlSessionFactory in xx.xx.MybatisConfig required a single bean, but 2 were found



## 报错

```
Description:

Parameter 0 of method masterSqlSessionFactory in com.kfit.spring_boot_mybatis.config.MybatisMasterConfig required a single bean, but 2 were found:
	- masterDataSource: defined by method 'masterDataSource' in class path resource [com/kfit/spring_boot_mybatis/config/MybatisMasterConfig.class]
	- slaveDataSource: defined by method 'slaveDataSource' in class path resource [com/kfit/spring_boot_mybatis/config/MybatisSlaveConfig.class]


Action:

Consider marking one of the beans as @Primary, updating the consumer to accept multiple beans, or using @Qualifier to identify the bean that should be consumed
```

## 解决

启动类加上 @EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class}),意思是排除 默认数据源的自动配置类

```java
/**
 * <B>Description:</B> Spring Boot启动类. <br>
 * <B>Create on:</B> 2018/4/24 下午10:36 <br>
 *
 * @author xiangyu.ye
 * @version 1.0
 */
@SpringBootApplication
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
```

