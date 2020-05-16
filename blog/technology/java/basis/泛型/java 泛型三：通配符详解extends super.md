# Java泛型三：通配符详解extends super

在java泛型中，？ 表示通配符，代表未知类型，< ? extends Object>表示上边界限定通配符，< ? super Object>表示下边界限定通配符。

## **通配符 与 T 的区别**

T：作用于模板上，用于将数据类型进行参数化，不能用于实例化对象。 
?：在实例化对象的时候，不确定泛型参数的具体类型时，可以使用通配符进行对象定义。

```
< T > 等同于 < T extends Object>
< ? > 等同于 < ? extends Object> 
```

例一：定义泛型类，将key，value的数据类型进行< K, V >参数化，而不可以使用通配符。

```
public class Container<K, V> {
    private K key;
    private V value;

    public Container(K k, V v) {
        key = k;
        value = v;
    }
} 
```

例二：实例化泛型对象，我们不能够确定eList存储的数据类型是`Integer`还是`Long`，因此我们使用`List<? extends Number>`定义变量的类型。

```
List<? extends Number> eList = null;
eList = new ArrayList<Integer>();
eList = new ArrayList<Long>(); 
```

## **上界类型通配符（? extends）**

```
List<? extends Number> eList = null;
eList = new ArrayList<Integer>();
Number numObject = eList.get(0);  //语句1，正确

//Type mismatch: cannot convert from capture#3-of ? extends Number to Integer
Integer intObject = eList.get(0);  //语句2，错误

//The method add(capture#3-of ? extends Number) in the type List<capture#3-of ? extends Number> is not applicable for the arguments (Integer)
eList.add(new Integer(1));  //语句3，错误123456789
```

语句1：`List<? extends Number>`eList存放Number及其子类的对象，语句1取出Number（或者Number子类）对象直接赋值给Number类型的变量是符合java规范的。 
语句2：`List<? extends Number>`eList存放Number及其子类的对象，语句2取出Number（或者Number子类）对象直接赋值给Integer类型（Number子类）的变量是不符合java规范的。 
语句3：`List<? extends Number>`eList不能够确定实例化对象的具体类型，因此无法add具体对象至列表中，可能的实例化对象如下。

```
eList = new ArrayList<Integer>();
eList = new ArrayList<Long>();
eList = new ArrayList<Float>(); 
```

总结：上界类型通配符add方法受限，但可以获取列表中的各种类型的数据，并赋值给父类型（extends Number）的引用。因此如果你想从一个数据类型里获取数据，使用 ? extends 通配符。限定通配符总是包括自己。

## **下界类型通配符（? super ）**

```
List<? super Integer> sList = null;
sList = new ArrayList<Number>();

//Type mismatch: cannot convert from capture#5-of ? super Integer to Number
Number numObj = sList.get(0);  //语句1，错误

//Type mismatch: cannot convert from capture#6-of ? super Integer to Integer
Integer intObj = sList.get(0);  //语句2，错误

sList.add(new Integer(1));  //语句3，正确12345678910
```

语句1：`List<? super Integer>` 无法确定sList中存放的对象的具体类型，因此`sList.get`获取的值存在不确定性，子类对象的引用无法赋值给兄弟类的引用，父类对象的引用无法赋值给子类的引用，因此语句错误。 
语句2：同语句1。 
语句3：子类对象的引用可以赋值给父类对象的引用，因此语句正确。 
总结：下界类型通配符get方法受限，但可以往列表中添加各种数据类型的对象。因此如果你想把对象写入一个数据结构里，使用 ? super 通配符。限定通配符总是包括自己。

## **总结**

- 限定通配符总是包括自己
- 上界类型通配符：add方法受限
- 下界类型通配符：get方法受限
- 如果你想从一个数据类型里获取数据，使用 ? extends 通配符
- 如果你想把对象写入一个数据结构里，使用 ? super 通配符
- 如果你既想存，又想取，那就别用通配符
- 不能同时声明泛型通配符上界和下界





https://blog.csdn.net/claram/article/details/51943742