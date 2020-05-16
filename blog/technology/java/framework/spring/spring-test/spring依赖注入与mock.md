[TOC]



# Spring依赖注入与mock



关注

 0.3 2018.09.09 11:43* 字数 523 阅读 3999评论 1喜欢 5

一般使用Spring，都会用到依赖注入(DI)。

```
@Service
public class SampleService {
    @Autowired
    private SampleDependency dependency;
    public String foo() {
        return dependency.getExternalValue("bar");
    }
}
```

如果测试中需要对Sping注入的对象进行注入，该怎么做呢？

### 选择一 修改实现

一种做法是把字段注入改为构造函数注入：

```
@Service
public class SampleService {
    private SampleDependency dependency;
    @Autowired
    public SampleService(SampleDependency dependency, PersonPoolProvider personPoolProvider) {
        this.dependency = dependency;
    }
}
```

或者属性注入：

```
private SampleDependency dependency;
@Autowired
public void setDependency(SampleDependency dependency) {
    this.dependency = dependency;
}
```

测试就可以写成

```
SampleDependency dependency = mock(SampleDependency.class);
SampleService service = new SampleService(dependency);
```

从道理来讲这样更加规范一些。不过事实上会产生更多的代码，在字段增删的时候、构造函数、getter也需要随之维护。

### 选择二 绕过限制

也可以用一些绕过访问级别的“黑魔法”，比如测试写成这样

```
SampleDependency dependency = mock(SampleDependency.class);
SampleService service = new SampleService();
ReflectionTestUtils.setField(service, "dependency", dependency);
```

总感觉不太优雅，而且万一字段改名也很可能漏改。

当然，也可以直接把字段改为package可见甚至public。不过总觉得对不起自己的代码洁癖。

### 选择三 使用Mockito InjectMocks

这里推荐使用mockito 的InjectMocks注解。测试可以写成

```
@Rule public MockitoRule rule = MockitoJUnit.rule();
@Mock SampleDependency dependency;
@InjectMocks SampleService sampleService;
```

对应于实现代码中的每个`@Autowired`字段，测试中可以用一个`@Mock`声明mock对象，并用`@InjectMocks`标示需要注入的对象。

> 这里的`MockitoRule`的作用是初始化mock对象和进行注入的。有三种方式做这件事。
>
> - 测试`@RunWith(MockitoJUnitRunner.class)`
> - 使用rule `@Rule public MockitoRule rule = MockitoJUnit.rule();`
> - 调用 `MockitoAnnotations.initMocks(this)`，一般在setup方法中调用

InjectMocks可以和Sping的依赖注入结合使用。比如：

```
@RunWith(SpringRunner.class)
@DirtiesContext
@SpringBootTest
public class ServiceWithMockTest {
    @Rule public MockitoRule rule = MockitoJUnit.rule();
    @Mock DependencyA dependencyA;
    @Autowired @InjectMocks SampleService sampleService;

    @Test
    public void testDependency() {
        when(dependencyA.getExternalValue(anyString())).thenReturn("mock val: A");
        assertEquals("mock val: A", sampleService.foo());
    }
}
```

假定Service注入了2个依赖dependencyA, dependencyB。上面测试使用Spring注入了B，把A替换为mock对象。

需要注意的是，Spring test默认会重用bean。如果另有一个测试也使用注入的SampleService并在这个测试之后运行，那么拿到service中的dependencyA仍然是mock对象。一般这是不期望的。所以需要用`@DirtiesContext`修饰上面的测试避免这个问题。

### 选择四 Springboot MockBean

如果使用的是Springboot，测试可以用MockBean更简单的写出等价的测试。

```
@RunWith(SpringRunner.class)
@SpringBootTest
public class ServiceWithMockBeanTest {
    @MockBean SampleDependencyA dependencyA;
    @Autowired SampleService sampleService;

    @Test
    public void testDependency() {
        when(dependencyA.getExternalValue(anyString())).thenReturn("mock val: A");
        assertEquals("mock val: A", sampleService.foo());
    }
}
```





<https://www.jianshu.com/p/c68ee5d08fdd>