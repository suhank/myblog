[TOC]



# java lombok简介及入门使用

## 为何要使用Lombok

> 我们在开发过程中，通常都会定义大量的JavaBean，然后通过IDE去生成其属性的构造器、getter、setter、equals、hashcode、toString方法，当要增加属性或者对某个属性进行改变时，比如命名、类型等，都需要重新去生成上面提到的这些方法。这样重复的劳动没有任何意义，Lombok里面的注解可以轻松解决这些问题。

## Lombok简介

> Lombok是一个可以通过简单的注解形式来帮助我们简化消除一些必须有但显得很臃肿的Java代码的工具，通过使用对应的注解，可以在编译源码的时候生成对应的方法。官方地址：[https://projectlombok.org/](https://link.jianshu.com/?t=https://projectlombok.org/)，github地址：[https://github.com/rzwitserloot/lombok](https://link.jianshu.com/?t=https://github.com/rzwitserloot/lombok)。

## Lombok问题

> 无法支持多种参数构造器的重载

## Lombok使用

1.添加maven依赖

```
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.16.6</version>
</dependency>
```

2.idea中需要添加插件才能支持Lombok
1.下载插件 \\192.168.188.22\互联网金融事业部\信息技术部\soft\ide-plugins\lombok-plugin-0.13.16.zip
2.安装插件并重启

![img](https://upload-images.jianshu.io/upload_images/3629187-3cb467db5a485d17.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/700)

安装插件.png

添加maven依赖

```

<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.16.20</version>
    <scope>provided</scope>
</dependency>

```

## Lombok注解

官方文档:https://projectlombok.org/features/all?spm=a2c4e.11153940.blogcont59972.9.2aeb6d32kX9GHz

- @Data：注解在类上，将类提供的所有属性都添加get、set方法，并添加、equals、canEquals、hashCode、toString方法
- @Setter：注解在类上，为所有属性添加set方法、注解在属性上为该属性提供set方法
- @Getter：注解在类上，为所有的属性添加get方法、注解在属性上为该属性提供get方法
- @NotNull：在参数中使用时，如果调用时传了null值，就会抛出空指针异常
- @Synchronized 用于方法，可以锁定指定的对象，如果不指定，则默认创建一个对象锁定
- @Log作用于类，创建一个log属性
- @Builder：使用builder模式创建对象
- @NoArgsConstructor：创建一个无参构造函数
- @AllArgsConstructor：创建一个全参构造函数
- @ToStirng：创建一个toString方法
- @Accessors(chain = true)使用链式设置属性，set方法返回的是this对象。
- @RequiredArgsConstructor：创建对象
- @UtilityClass:工具类
- @ExtensionMethod:设置父类
- @FieldDefaults：设置属性的使用范围，如private、public等，也可以设置属性是否被final修饰。
- @Cleanup: 关闭流、连接点。
- @EqualsAndHashCode：重写equals和hashcode方法。
- @toString：创建toString方法。

## 一些使用的例子

### 普通的bean：

```
    public class User {
        private String id;
        private String name;
        private Integer age;
    
        public String getId() {
            return id;
        }
    
        public void setId(String id) {
            this.id = id;
        }
    
        public String getName() {
            return name;
        }
    
        public void setName(String name) {
            this.name = name;
        }
    
        public Integer getAge() {
            return age;
        }
    
        public void setAge(Integer age) {
            this.age = age;
        }
    }
```

### 使用 lambok时

使用lombok，代码可以变得非常的简洁，看着也舒服。

```
@Setter
@Getter
public class User {
    private String id;
    private String name;
    private Integer age;
}

public static void main(String[] args) {
    User user = new User();
    user.setId("1");
    user.setName("name");
    user.setAge(1);
}
```

## @Accessors(chain = true)：使用链式创建:

```
@Setter
@Getter
@Accessors(chain = true)
public class User {
    private String id;
    private String name;
    private Integer age;
}

public static void main(String[] args) {
    //使用@Accessors(chain = true)
    User userChain = new User();
    userChain.setId("1").setName("chain").setAge(1);
}
```

## @Builder：使用builder模式创建对象

```
@Setter
@Getter
@Builder
public class User {
    private String id;
    private String name;
    private Integer age;
}

public static void main(String[] args) {
    User user = User.builder().id("1").name("builder").age(1).build();
    System.out.println(user.getId());
}
```

## @UtilityClass：工具类注解

```
@UtilityClass
public class Utility {

    public String getName() {
        return "name";
    }
}

public static void main(String[] args) {
    // Utility utility = new Utility(); 构造函数为私有的,
    System.out.println(Utility.getName());

}
```

**参考链接：**

- [Lombok 介绍及基本使用方法](https://link.jianshu.com/?t=http%3A%2F%2Fwww.2cto.com%2Fkf%2F201603%2F491479.html)
- [Github项目地址](https://link.jianshu.com/?t=https%3A%2F%2Fgithub.com%2Frzwitserloot%2Flombok)
- [栗子演示](https://link.jianshu.com/?t=https%3A%2F%2Fgithub.com%2Fpeichhorn%2Flombok-pg)





https://www.jianshu.com/p/2ea9ff98f7d6



https://www.jianshu.com/p/ed3d5e868825