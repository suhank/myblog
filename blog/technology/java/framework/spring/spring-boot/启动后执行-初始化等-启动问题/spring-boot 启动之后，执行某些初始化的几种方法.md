# spring-boot 启动之后，执行某些初始化的几种方法

原创Clement-Xu 最后发布于2018-11-22 17:16:01 阅读数 3975   

直接上代码：

```java
@Configuration
public class SampleWebCommonConfig {
    // 系统启动之后，如果需要初始化的某些东东，几种不同的方法：
 
    // 1
    @PostConstruct
    public void postConstruct(){
        System.out.println("system started, triggered by postConstruct.");
    }
 
    // 5
    @Bean
    public CommandLineRunner initData(){
        return (args) -> {
            System.out.println("system started, triggered by CommandLineRunner.");
            Stream.of(args).forEach(System.out::println);
        };
    }
 
    // 4
    @Bean
    public ApplicationRunner initData2(){
        return (args) -> {
            System.out.println("system started, triggered by ApplicationRunner.");
            Stream.of(args.getSourceArgs()).forEach(System.out::println);
        };
    }
 
    // 3
    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        System.out.println("system started, triggered by ContextRefreshedEvent.");
    }
 
    // 2
    @Bean(initMethod = "init")
    public InitMethodBean initMethodBean(){
        return new InitMethodBean();
    }
 
    private static class InitMethodBean{
        void init(){
            System.out.println("system started, triggered by initMethod property.");
        }
    }
}
```

 








原文链接：https://blog.csdn.net/clementad/article/details/84345196