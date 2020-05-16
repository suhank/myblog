[TOC]



# 详解Java的序列化机制

/Users/jerryye/backup/studio/AvailableCode/basis/SerializationUtils_对象序列化工具类/serialization_demo





[空同定翁](https://www.jianshu.com/u/18aeb8a9a95e) 

关注

2016.11.01 11:57* 字数 1433 阅读 321评论 0喜欢 3

​      Java的序列号机制允许将对象转换成与平台无关的二进制流，从而实现对象保存到磁盘、在网络中传输等。Java中通过实现Serializable接口，标识对象序列化。查看源码可发现，Serializable接口不包含任何方法和域，只是起到标识作用：

```
package java.io;
public interface Serializable {
}
```



# 一、序列化原理

1、每个序列号的对象都是采用了一个序列号进行保存

2、当序列化一个对象时，程序将检查该对象是否已经序列化过：

​      ---该对象若未进行序列化，则采用流中数据来构建它，并为该对象关联一个序列号；

​      ---若该对象已经序列化过，则程序直接输出该对象关联的序列号，通过该序列号可获得该对象引用。

------

# 二、自定义序列化

自定义序列化一般通过重写readObject(...)、writeObject(...)和readObjectNoData(...)方法实现

```java
    private String name;
	private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
        /**
         *一般先调用defaultReadObject()
         *该方法默认读取流中的非static和transient域
         */
        in.defaultReadObject();

        //读取自定义写入的数据
        name = in.readUTF();
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        /**
         *一般先调用defaultWriteObject()
         *该方法默认将对象中的非static和非transient域写到流中
         */
        out.defaultWriteObject();

        //自定义部分,可写入transient等域
        out.writeUTF(name);
    }

    /**
     * 当序列化淺不完整时，扣接收方和发送方版本不一致等
     * 可通过该方法正确的初始化反序列化的对象
     */
    private void readObjectNoOdta() {
    }
```

图2： 自定义序列化

------

# 三、版本管理

1、在进行对象序列化时，系统会默认给每个对象生成一个指纹，即序列化版本ID（serialVersionUID）。系统默认生成的UID是根据对象的类、超类、接口、域和方法等信息计算得到，如果对象信息发生变化，该UID也会发生变化。

2、在读入一个对象时，会将该对象的UID与其所属类的UID进行对比，如果两者不同，则说明这个类的定义在该对象被写出后发生了变化（版本不同），进而产生一个异常。

3、通过在可序列化类中定义一个public static final long serialVersionUID，实现版本兼容：

```
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;
```

4、如果版本不同（UID不同）：

​      ---在读入流中对象时，如果流中对象具有当前版本所没有的数据域，那么对象流会忽略这些额外的数据；如果当前版本具有在流中对象所没有的数据域，那么当前版本中新增加的数据域会被设置成默认值。

------

# 四、readResolve()方法

1、如果定义了readResolve()方法，那么该方法会在对象序列化之后被调用，该方法返回的对象会成为readObject()方法的返回值！该方法拥有private、protected和包私有等访问权限。

2、在对单例模式等进行实例控制的类进行序列化时，由于readObject()会重新创建一个不同的对象，而导致实例数量控制失败，可通过readObject()方法解决：

```java
public class SingleInstance implements Serializable {
    //添加版本号
    private static final long serieLVersionUID = 1L;
    private static transient SingleInstance instance = new SingleInstance();

    private SingleInstance() {
    }

    public static SingleInstance getInstance() {
        return instance;
    } 

    private Object readResolve() {
        //直接返回实例，确保单例
        return instance;
    }
}

```

图4： 测试用例

​      ---注意：在图4中，instance申明为transient。

```
package com.practice;

import java.io.Serializable;

public class SingleInstance implements Serializable {
    //添加版本号
    private static final long serieLVersionUID = 1L;
    private static transient SingleInstance instance = new SingleInstance();

    private SingleInstance() {
    }

    public static SingleInstance getInstance() {
        return instance;
    }

    private Object readResolve() {
        //直接返回实例，确保单例
        return instance;
    }
}

```

图5： 测试用例

```
instance1 == instance2:true
instance2 == instance3:false
```

图6： 注释掉readResolve()结果

​      ---由图6结果可知，在未添加readResolve()方法时，每次反序列化都重新创建了一个对象，导致单例控制失败；

```
instance1 == instance2:true
instance2 == instance3:true
```

图7： 添加readResolve()结果

​      ---由图7结果可知，在readResolve()中直接返回实例，忽略了反序列化后的对象，确保反序列化后，仍然只存在一个实例。

​      ---由于在readResolve()中忽略了反序列化后的对象，因此如果通过readResolve()进行实例控制，那类的所有实例域都应该申明为transient !

------

# 五、writeReplace()方法

1、writeReplace()是一种更彻底的自定义机制，它可以在序列化对象时，将序列化的对象替换成其它对象，该方法可拥有private、protected和包私有等访问权限。

2、系统在序列化对象时，先调用该对象的writeReplace()方法，如果该方法返回另一个对象，则系统转为序列化另一个对象：即再次调用另一个对象的writeReplace()方法……直到不再返回另一个对象为止。

```
package com.practice.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Person implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private int age;

    public Person(String pName, int pAge) {
        name = pName;
        age = pAge;

    }

    public String getPersonName() {
        return name;

    }

    public int getPersonAge() {
        return age;

    }

    /**
     * 重写该方法,序列化其他对象
     *
     * @return
     */
    private Object writeReplace() {
        List<String> list = new ArrayList<String>();
        list.add(name);
        return list;
    }
}

```

图8： 序列化成List对象

```
package com.practice;

import com.practice.entity.Person;
import com.practice.entity.SingleInstance;
import org.junit.Test;

import java.io.*;
import java.util.List;

public class WriteReplaceTest {
    @Test
    public void writeReplace() throws IOException, ClassNotFoundException {
        Person p1 = new Person("zxb", 23);

        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
        objOut.writeObject(p1);

        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream objIn = new ObjectInputStream(byteIn);
        Object obj = objIn.readObject();

        if (obj instanceof List) {
            System.out.println(obj);
        }
    }
}

```

图9： 测试用例

```
[zxb]
```

图10： 测试结果

由图10可知，序列化Person对象时，转而序列化成了List对象。

------

# 六、另一种自定义序列化机制（Externalizable）

1、Externalizable序列方式完全由程序员决定存储和恢复对象数据。

2、Externalizable接口与Serializable接口非常相似，只是Externalizable接口强制程序员自定义序列化。

3、通过重写readExternal(...)和writeExternal(...)实现反序列化和序列化。

4、注意：实现Externalizable接口的类必须提供一个无参构造函数：在反序列化对象时，对象流将调用无参构造函数创建一个对象，在调用readExternal(...)方法!

------

# 七、总结

1、对象的类名、域（基本类型、数组、对象引用）都会被序列化，但方法、static域和transient域不会被初始化！

2、如果超类没有提供一个可访问的无参构造函数，子类是不能看实现序列化的。

3、反序列化是一个“隐藏的构造器”，默认会构造一个新的对象。

4、内部类的默认序列化形式是定义不清楚的，因此内部类不该实现Serializable；但静态成员类却可以实现该接口。

5、根据经验：比如Date和BigInteger这样的值类应该实现Serializable接口，大多数的集合类也该如此；代表活动实体的类，如线程池，一般不该实现该接口。

6、为了继承设计的类和用户接口，应尽可能的少实现Serializable接口。



<https://www.jianshu.com/p/7f36f22c1a64>