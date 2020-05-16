[TOC]



# Spirng Cache(第一篇) 初体验

2018年12月09日 21:53:31 [孙平平](https://me.csdn.net/sun_shaoping) 阅读数 105 

从3.1版开始，Spring Framework提供了对现有Spring应用程序**透明**地添加缓存的支持。与[事务](https://docs.spring.io/spring/docs/5.0.11.RELEASE/spring-framework-reference/data-access.html#transaction) 支持类似，缓存抽象允许一致地使用各种缓存解决方案，而对代码的**影响最小**。

从Spring 4.1开始，通过[JSR-107注释](https://docs.spring.io/spring/docs/5.0.11.RELEASE/spring-framework-reference/integration.html#cache-jsr-107)和更多自定义选项的支持，缓存抽象得到了显着改进。



## 注解驱动

只需要掌握下面5个缓存相关注解就可以使用它了。更多请[参考](https://docs.spring.io/spring/docs/5.0.11.RELEASE/spring-framework-reference/integration.html#cache-annotations)

- `@Cacheable` 触发缓存入口
- `@CacheEvict` 触发缓存驱逐(删除缓存)
- `@CachePut` 更新缓存而不会干扰方法执行
- `@Caching` 重新组合要在方法上应用的多个缓存操作
- `@CacheConfig` 在类级别共享一些常见的缓存相关设置

## 缓存的使用

话不多说，让我们体验一下它

环境信息：java8+maven3.x+spring-boot1.5.x

本章所有示例都在`BookService`类中完成，缓存名称是`books`，使用`@CacheConfig`注解统一配置

```java
@Service
@CacheConfig(cacheNames = "books")
public class BookService {}
```

`Book`实体，省略Get/Set方法

```java
public class Book {
    private Long id;
    private String bookName;
    private String isbn;
}
```

### 添加/使用缓存@Cacheable

查询Book，并加入缓存，以后再查询从缓存中获取，这里没有指定缓存`key`默认使用方法参数`isbn`

```java
@Cacheable
public Book findBook(String isbn) {
    return createBook(isbn);
} 
```

模拟数据，每次查询创建一个`Book`实体，ID是随机数。

```java
private Book createBook(String isbn) {
    Book book = new Book();
    book.setBookName("儿童故事集");
    book.setId(new Random().nextLong());
    book.setIsbn(isbn);
    return book;
}
1234567
```

单元测试 `findBook`

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class BookServiceTest {
    private static final String ISBN = "1234-5678";
    @Autowired
    BookService bookService;
    @Test
    public void findBook() {
        Book book1 = bookService.findBook(ISBN);
        Book book2 = bookService.findBook(ISBN);
        Book book3 = bookService.findBook(ISBN);
        assert book1 == book2;
        assert book1 == book3;
    }
}
123456789101112131415
```

spring 默认情况下使用的内存缓存，所以 `book1`、`book2`、`book3`变量内存地址是相同的。

### 更新缓存@CachePut

比如我们对bookName进行修改，并更新到缓存到中。

由于上面添加缓存时缓存`key`是`isbn`,所以这里的缓存更新操作也要用`isbn`，使用[Spring Spel](https://docs.spring.io/spring/docs/5.1.3.RELEASE/spring-framework-reference/core.html#expressions)表达式

返回实体作为缓存`value`替换原有的值。

```java
@CachePut(key = "#book.isbn")
public Book updateBook(Book book) {
    Book book1 = createBook(book.getIsbn());
    book1.setBookName(book.getBookName());
    return book1;
}
123456
```

单元测试

```java
@Test
public void updateBook() {
    Book book = new Book();
    book.setIsbn(ISBN);
    book.setBookName("JAVA-编程思想");
    Book book1 = bookService.updateBook(book);
    Book book2 = bookService.findBook(ISBN);
    assert book1 == book2;
    assert book2.getBookName().equals("JAVA-编程思想");
}
12345678910
```

### 清除缓存@CacheEvict

删除某个`Book`实体，同时删除对应的缓存

```java
@CacheEvict(key = "#isbn")
public void removeBook(String isbn) {
    System.out.println("删除一本书: isbn = " + isbn);
}
1234
```

单元测试

```java
@Test
public void removeBook() {
    Book book1 = bookService.findBook(ISBN);
    bookService.removeBook(ISBN);
    Book book2 = bookService.findBook(ISBN);
    assert book1 != book2;
}
1234567
```

## 总结

到这里`Spring Cache`使用阶段已完成，下篇介绍[缓存的自定义配置](https://blog.csdn.net/sun_shaoping/article/details/84932879)。

代码地址，[点击这里](https://github.com/ssp1523/spring-cache)，或直接使用`Git`下载

```sh
git clone https://github.com/ssp1523/spring-cache.git
1
```

参考资料 [Spring Cache Abstraction](https://docs.spring.io/spring/docs/5.1.3.RELEASE/spring-framework-reference/integration.html#cache)

如发现内容有误，欢迎在评论区指教。

对内容不解，欢迎在评论区讨论。





<https://blog.csdn.net/sun_shaoping/article/details/84932802>