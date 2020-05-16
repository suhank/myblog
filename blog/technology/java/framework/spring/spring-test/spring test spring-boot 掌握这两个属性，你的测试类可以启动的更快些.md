# spring test spring-boot 掌握这两个属性，你的测试类可以启动的更快些

 

单元测试是项目开发中必不可少的一环，在 SpringBoot 的项目中，我们用 `@SpringBootTest` 注解来标注一个测试类，在测试类中注入这个接口的实现类之后对每个方法进行单独测试。

比如下面这个示例测试类：

```
@SpringBootTest
public class HelloServiceTests {
    
    @Autowired
    private IHelloSerive helloService;
    
    @Test
    public void testHello() {
        // ...
    }
} 
```

但是随着项目的代码量越来越大，你会发现测试类的启动速度变得越来越慢，而大多数情况下只是为了测试一下某个实现类的某个方法而已，比如测试一个DAO类的`persist`方法。

实际上， `@SpringBootTest` 注解还提供了两个参数，好好利用这两个参数就可以让测试类的启动速度变得更快。

## 1. webEnvironment

这个属性决定了测试类要不要启动一个 `web` 环境，说白了就是要不要启动一个 `Tomcat` 容器，可选的值为：

- MOCK, 启动一个模拟的 Servlet 环境，这是默认值。
- RANDOM_PORT，启动一个 Tomcat 容器，并监听一个随机的端口号
- DEFINED_PORT，启动一个 Tomcat 容器，并监听配置文件中定义的端口（未定义则默认监听8080）
- NONE，不启动 Tomcat 容器

如果你要测试的方法不需要用到 Tomcat 容器，比如：

- 测试一个 DAO 类的增删改查
- 测试一个 Service 类的业务方法
- 测试一个 Util 类的公用方法
- 测试一个配置文件类是否读取到了正确的值
- ... ...

只需要通过指定 `@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)` 即可达到加速的效果。这时测试类启动时就只会初始化 Spring 上下文，不再启动 Tomcat 容器了:

```
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class HelloServiceTests {
    
    @Autowired
    private IHelloSerive helloService;
    
    @Test
    public void testHello() {
        // ...
    }
}
复制代码
```

## 2. classes

classes 属性用来指定运行测试类需要装载的 class 集合，如果不指定，那么会默认装载 `@SpringBootConfiguration` 注解标注的类。

提到 `@SpringBootConfiguration` 你可能比较陌生，其实 `@SpringBootApplication` 的源码里就引入了这个注解：

```
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(
    excludeFilters = {@Filter(
    type = FilterType.CUSTOM,
    classes = {TypeExcludeFilter.class}
), @Filter(
    type = FilterType.CUSTOM,
    classes = {AutoConfigurationExcludeFilter.class}
)}
)
public @interface SpringBootApplication {
    // ...
}

复制代码
```

也就是说，如果我们不指定classes属性，那么启动测试类时需要加载的Bean的数量和正常启动一次入口类(即有`@SpringBootApplication`注解的类)加载的 Bean 数量是一样的。

如果你的项目中有很多个 Bean， 特别是有以下几种时：

- 有 CommandLineRunner 的实现类
- 用 `@PostConstruct` 注解指定了初始化方法的类

这几种类在程序初始化的过程中都会运行自身的业务代码或者初始化代码，从而延后了测试方法的运行。

在这种情况下，我们在编写测试类的时候，如果明确这个测试类会用到哪几个 Bean，则可以在 classes 属性处指定，之后启动测试类的时候，就只会加载需要的 Bean 到上下文中，从而加快启动速度。比如：

```
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes={HelloServiceImpl.class})
public class HelloServiceTests {

    @Autowired
    private IHelloService helloService;
    
    @Test
    public void testHello() {
        // ...
    }
}
复制代码
```

即使此时项目中还有另外一个 Bean 在它的初始化方法里写了类似 `Thread.sleep(10000)` 等操作也不会影响，因为这个 Bean 根本就没有被加载和初始化。

------

> 最后要注意，如果你的 JUnit 的版本是 4.x 还需要在测试类上新增 `@RunWith(SpringRunner.class)` 注解。





https://juejin.im/post/5e709f25f265da570c754d8d