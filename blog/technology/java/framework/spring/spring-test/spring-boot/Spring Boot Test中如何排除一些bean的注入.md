[TOC]



# Spring Boot Test中如何排除一些bean的注入? 

  

## 问题

在测试Controller时，不希望测试方法走拦截器。经过百度，使用的方法就是加入@ComponentScan的@excludeFilters 注解。但这行代码只能在程序的主入口上起作用，如果加到Test的基类上是没有任何反应的。我希望在测试时忽略Spring的自动注入并且不影响主程序的行为。谢谢

### 这是启动程序的入口

```
/**
 * Spring could web程序主入口
 */
@Configuration//配置控制
@EnableAutoConfiguration//启用自动配置
@EnableFeignClients(basePackages = {"com.konyo.service.client", "com.konyo.activiticommon.client", "com.konyo.teleport.common.inteceptor"})
@MapperScan(value = {"com.konyo.service.dao", "com.konyo.activiticommon.mapper"})
@ComponentScans(value = {
    @ComponentScan(value = {"com.konyo.teleport", "com.konyo.service", "com.konyo.activiticommon"})//组件扫描
})
@EnableDiscoveryClient
@EnableEurekaClient
@SpringBootApplication
public class JsGfrcServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(JsGfrcServiceApplication.class, args);
    }
}
```

### 这是我的测试基类

```
/**
 * Created by xx on 2018/07/12.
 * Contract 测试基类
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = JsGfrcServiceApplication.class)
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@DirtiesContext
// 我希望启动时忽略这些类，但是这行代码不起作用
@ComponentScan(value = {"com.konyo.service", "com.konyo.activiticommon"},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = {RedisConfig.class, SwaggerConfig.class,
                        TxManagerHttpRequestServiceImpl.class, TxManagerTxUrlServiceImpl.class,
                        InitDictionaryMap.class}))
@Transactional

public class MockMvcContractTest {

    static {
        System.setProperty("eureka.client.enabled", "false");
        System.setProperty("spring.cloud.config.failFast", "false");
        System.setProperty("spring.cloud.config.discovery.enabled", "false");
    }

    protected String packageGetParams(String paramName, List<String> values, String uri) {
        StringBuilder builder = new StringBuilder(uri);
        if (values == null || values.size() == 0) {
            return uri;
        }
        boolean contains = uri.contains("?");
        if (!contains) {
            builder.append("?");
        }
        for (String value : values) {
            builder.append(paramName).append("=").append(value).append("&");
        }
        builder.delete(builder.length() - 1, builder.length());
        return builder.toString();
    }

    @Autowired
    MockMvc mockMvc;

    @Before
    public void setup() {
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

}
```





## 答案

[来自stackoverflow的答案](https://stackoverflow.com/questions/48102883/spring-boot-componentscan-excludefilters-not-excluding)

```
import org.springframework.boot.test.mock.mockito.MockBean;

public class SimpleTest {
    // 排除的filter
    @MockBean
    private Starter myTestBean;
    ...
}
```

Spring将使用这个mock而不是真正的类，所以不会调用@PostConstruct方法去创建。





<https://segmentfault.com/q/1010000015667498>