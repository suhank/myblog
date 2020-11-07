# 关于optional的orElse和orElseGet、orElseThrow

2019.03.06 21:55:12

#### 前言：

- `Optional`是java8中增加的API，可以很好的解决空指针异常，而不用我们来进行显式的空值检测。

  - 比如说`Optional`可以是一个为null的容器。`Optional.ofNullable(null)`
  - `isPresent`方法当值存在时返回`true`，调用`get()`方法返回其对象

- 粗看Java8中的API`Optional`中的方法，获取`Optional`中的值用`get()`方法就可以了，那么和`orElse()`、`orElseGet()`的获取值的方法有什么不同呢？

- 当我们在IDEA中使用`get()`时，IDEA会高亮提示，此方法需要先用`isPresent()`进行判断，然后再调用`get()`方法

- 以前没有推出`Optional`时，我们的代码是这样的

  

  ```java
  User user = .....
   
  if (user != null) {
   
  return user.getOrders();
   
  } else {
   
  return Collections.emptyList();
   
  }
  ```

  而有了`Optional`后代码如果写成下面这样，其实并没有多大区别

  

  ```java
  Optional<User> user = ......
   
  if (user.isPresent()) {
   
  return user.getOrders();
   
  } else {
   
  return Collections.emptyList();
   
  }
  ```

- 那么我们如果正确使用`Optional`的获取值的方法呢?

  就要提到`orElse`和`orElseGet`了

#### 用法

##### `orElse`

如果`Optional`实例有值则将其返回，否则返回`orElse`方法传入的参数



```java
public T orElse(T other)
```

- **参数：**`other`，即需要被返回的值

- **返回**：当只存在时返回值，不存在返回other（可以理解为自定义值，如字符串的内容）

- **例子：**

  

  ```java
  //调用工厂方法创建Optional实例
  Optional<String> name = Optional.of("Dolores");
  //创建一个空实例
  Optional empty = Optional.ofNullable(null);
  //创建一个不允许值为空的空实例
  Optional noEmpty = Optional.of(null);
  ```

  

  ```java
  //如果值不为null，orElse方法返回Optional实例的值。
  //如果为null，返回传入的消息。
  //输出：Dolores
  System.out.println(name.orElse("There is some value!"));
  //输出：There is no value present!
  System.out.println(empty.orElse("There is no value present!"));
  //抛NullPointerException
  System.out.println(noEmpty.orElse("There is no 
  value present!");
  ```

##### `orElseGet`

`orElseGet`与`orElse`方法类似，区别在于得到的默认值。`orElse`方法将传入的字符串作为默认值，`orElseGet`方法可以接受`Supplier`的实现用来生成默认值



```java
public T orElseGet(Supplier<? extends T> other)
```

- **参数：**继承`Supplier`接口的`other`，当值为null的时候返回

- **返回：**值存在返回值，值不存在返回`other`

- **异常：**当不允许值为空的情况（例如）下值为空时或`other`无效抛`NullPointerException`

- **例子：**

  

  ```java
  //orElseGet与orElse方法类似，区别在于orElse传入的是默认值，
  //orElseGet可以接受一个lambda表达式生成默认值。
  //输出：Dolores
  System.out.println(name.orElseGet(() -> ``"it's value"``));
  //输出：No value
  System.out.println(empty.orElseGet(() -> ``"No value"``));
  //抛出NullPointerException
  System.out.println(noEmpty.orElseGet(() -> ``"it's value"``));
  ```

##### `orElseThrow`

如果有值则将其返回，否则抛出`Supplier`接口创建的异常。

```java
public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier)
                                    throws X extends Throwable
```

- **参数：**`exceptionSupplier` 一个抛异常的实例化`Supplier`
- **返回：**当前值或当值为null抛出异常
- **异常：**当不允许值为空的时候以及`exceptionSupplier`为空抛`NullPointerException`;值为空抛`Supplier`继承的异常
- **例子：**

```java
try {
  //orElseThrow与orElse方法类似。与返回默认值不同，
  //orElseThrow会抛出lambda表达式或方法生成的异常 
  empty.orElseThrow(DemoException::new);
} catch (Throwable throwable) {
  //输出: Here is message
  System.out.println(throwable.toString());
} 
try {
  o.orElseThrow(DemoException::new);
} catch (Throwable throwable) {
  //输出：Exception in thread "main" java.lang.NullPointerException的详细信息
  System.out.println(throwable.printStackTrace());
}
   
```

以下是自定义的`DemoException`

```java
class DemoException extends Throwable {
 
  public DemoException() {
    super();
  }
 
  public DemoException(String msg) {
    super(msg);
  }
 
  @Override
  public String getMessage() {
    return "Here is message";
  }
}
```

```java
Optional
 .ofNullable(someVariable)
 .map(this::findOtherObject)
 .filter(this::isThisOtherObjectStale)
 .map(this::convertToJson)
 .map(String::trim)
 .orElseThrow(() -> new RuntimeException("something went horribly wrong."));
```

**总结**

总的来说，`orElse`与`orElseGet`方法差不多，两者都会返回定义的内容，前者是字符串，后者是实现了`Supplier`的内容；并且后者会在不允许值为空或`other`无效的情况抛`NullPointerException`异常



  



https://www.jianshu.com/p/1a711fa3adab