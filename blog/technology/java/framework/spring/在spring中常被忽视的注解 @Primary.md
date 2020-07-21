# **在spring中常被忽视的注解 @Primary**

在spring 中使用注解，常使用@Autowired， 默认是根据类型Type来自动注入的。但有些特殊情况，对同一个接口，可能会有几种不同的实现类，而默认只会采取其中一种的情况下 @Primary  的作用就出来了。下面是个简单的使用例子。

有如下一个接口

```
public interface Singer {
    String sing(String lyrics);
}
```

有下面的两个实现类:

```
@Component // 加注解，让spring识别
public class MetalSinger implements Singer{
    @Override
    public String sing(String lyrics) {
        return "I am singing with DIO voice: "+lyrics;
    }
}
```

```
//注意，这里没有注解
public class OperaSinger implements Singer {
    @Override
    public String sing(String lyrics) {
        return "I am singing in Bocelli voice: "+lyrics;
    }
}
```

下面就是注入上面的接口实现类:

```
@Component
public class SingerService {
    private static final Logger logger = LoggerFactory.getLogger(SingerService.class);
    @Autowired
    private Singer singer;
    public String sing(){
        return singer.sing("song lyrics");
    }
}
```

结果是什么呢？
I am singing with DIO voice: song lyrics. 原因很简单，就是 OperaSinger 这个类上面根本没有加上注解@Copmonent 或者 @Service, 所以spring 注入的时候，只能找到 MetalSinger 这个实现类. 所以才有这个结果。

但是如果一旦 OperaSinger 这个类加上了@Copmonent 或者 @Service 注解，有趣的事情就会发生，你会发现一个错误的结果或异常:
org.springframework.beans.factory.NoUniqueBeanDefinitionException: No qualifying bean of type [main.service.Singer] is defined: expected single matching bean but found 2: metalSinger,operaSinger

提示很明确了，spring 根据类型无法选择到底注入哪一个。这个时候@Primay 可以闪亮登场了。

```
@Primary
@Component
public class OperaSinger implements Singer{
    @Override
    public String sing(String lyrics) {
        return "I am singing in Bocelli voice: "+lyrics;
    }
}
```

如果代码改成这样，再次运行，结果如下：

"I am singing in Bocelli voice: song lyrics"， 用@Primary 告诉spring 在犹豫的时候优先选择哪一个具体的实现。

思考：貌似还有另外一种方法：利用qualifier names，应该会更好。



http://www.yihaomen.com/article/java/581.htm