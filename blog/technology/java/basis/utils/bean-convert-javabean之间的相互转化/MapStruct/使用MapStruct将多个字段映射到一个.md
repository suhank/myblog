[TOC]



# 使用MapStruct将多个字段映射到一个

Map multiple fields to one with MapStruct

发表于 2018-10-09 13:15:17

## 问题  

我在单独的文件中有这3个类

```java
public class Book {

   @Id
    @GeneratedValue
    private Long id;

    @NonNull
    private String title;

    @NonNull
    private Author author;

}

public class Author {

    @Id
    @GeneratedValue
    private Long id;

    @NonNull
    private String firstName;

    @NonNull
    private String lastName;

}

public class BookDTO {

    private Long id;

    @NonNull
    private String title;

    @NonNull
    private String author;

}
```

我有以下映射器

```java
@Mapper
public interface BookMapper { 

    BookMapper INSTANCE = Mappers.getMapper(BookMapper.class);

    @Mappings({
            @Mapping(source = "author.lastName", target = "author")
    })
    BookDTO toDTO(Book book);

}
```

这当前只映射lastName并且工作，我想在Book with中映射作者字符串

```
author.firstName + " " author.lastName
```

我怎么能这样做？ 我无法在MapStruct文档中找到任何内容。



## 答

MapSruct不支持将多个源属性映射到单个目标属性。

您有两种方法可以实现此目的：

### 使用Mapping＃表达式

```java
@Mapping( target = "author", expression = "java(book.getAuthor().getFirstName() + \" \" + book.getAuthor().getLastName())")
```

### 使用`@AfterMapping`或`@BeforeMapping`

```java
@Mapper
public interface BookMapper { 

    BookMapper INSTANCE = Mappers.getMapper(BookMapper.class);


    @Mapping(target = "author", ignore = true)
    BookDTO toDTO(Book book);

    @AfterMapping
    default void setBookAuthor(@MappingTarget BookDTO bookDTO, Book book) {
        Author author = book.getAuthor();
        bookDTO.setAuthor(author.getFirstName() + " " + author.getLastName());
    }

}
```











<https://stackoom.com/question/3ZDP5/%E4%BD%BF%E7%94%A8MapStruct%E5%B0%86%E5%A4%9A%E4%B8%AA%E5%AD%97%E6%AE%B5%E6%98%A0%E5%B0%84%E5%88%B0%E4%B8%80%E4%B8%AA>