# java 泛型中 T 和 问号（通配符）的区别  

类型本来有：简单类型和复杂类型，引入泛型后把复杂类型分的更细了；

- 现在List<Object>;, List<String>是两种不同的类型;且无继承关系；
- 泛型的好处如：

```
// 1. 开始版本
public void write(Integer i, Integer[] ia);
public void write(Double  d, Double[] da);

// 2. 泛型版本
public <T> void write(T t, T[] ta);
```

简便了代码

## 定义泛型

```
//1. 紧跟类名后面
public class TestClassDefine<T, S extends T>{}
		
定义泛型 T, S, 且S 继承 T


//2. 紧跟修饰符后面（public）
public <T, S extends T> testGenericMethodDefine(T t, S s){}

//3. 静态方法
 public static <T> T buildHandler(Class<T> clazz){}
```

定义泛型 T, S, 且S 继承 T

## 实例化泛型

- 1.实例化定义在类上的泛型

```
// 1. 第一, 声明类变量或者实例化时
List<String> list;
list = new ArrayList<String>;

// 2. 第二, 继承类或者实现接口时。
 public class MyList<E> extends ArrayList<E> implements List<E> {...} 
 
```

- 2.实例化定义方法上的泛型

当调用范型方法时，编译器自动对类型参数(泛型)进行赋值，当不能成功赋值时报编译错误

## 通配符(?)

上面有泛型的定义和赋值；当在**赋值**的时候，上面一节说赋值的都是为**具体类型**，当赋值的类型**不确定**的时候，我们用**通配符**(?)代替了：

如

```
List<?> unknownList;
List<? extends Number> unknownNumberList;
List<? super Integer> unknownBaseLineIntgerList; 
```

> 在Java集合框架中，对于参数值是`未知类型`的容器类，`只能`读取其中元素，`不能`向其中添加元素， 因为，其类型是未知，所以编译器无法识别添加元素的类型和容器的类型是否兼容，唯一的例外是NULL