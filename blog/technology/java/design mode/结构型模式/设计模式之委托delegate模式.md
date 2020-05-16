[TOC]



# 设计模式之委托delegate模式

委托delegate模式虽然并没有被归类在23中常用的[设计模式](http://www.liuhaihua.cn/archives/tag/%e8%ae%be%e8%ae%a1%e6%a8%a1%e5%bc%8f)之中，但是在[开发](http://www.liuhaihua.cn/archives/tag/%e5%bc%80%e5%8f%91)中确实也是比较常用的[软件](http://www.liuhaihua.cn/archives/tag/%e8%bd%af%e4%bb%b6)设计模式之一。在[Android](http://www.liuhaihua.cn/archives/tag/android)的support包中[App](http://www.liuhaihua.cn/archives/tag/app)CompatActivity就可以看到delegate的身影；iOS终端开发使用delegate更是随处可见；而在J2EE开发中业务委托模式在已经作为了官方的一种设计模式。

### 委托模式介绍

下面是维基百科中对委托模式的定义。

委托模式是软件设计模式中的一项基本技巧。在委托模式中，有两个对象参与处理同一个请求，接受请求的对象将请求委托给另一个对象来处理。委托模式是一项基本技巧，许多其它的模式，如状态模式、策略模式、访问者模式[本质](http://www.liuhaihua.cn/archives/tag/%e6%9c%ac%e8%b4%a8)上是在更特殊的场合采用了委托模式。委托模式使得我们可以用聚合来替代继承，它还使我们可以模拟mixin。

有关什么是Mixin这里就不做介绍了，有兴趣的可以直接查询一下相关资料。

委托是对类的行为进行复用和扩展的一条途径。它的工作方式是：包含原有类（被委托者）的[实例](http://www.liuhaihua.cn/archives/tag/%e5%ae%9e%e4%be%8b)引用，实现原有类（被委托者）的接口，将对原有类（被委托者）方法的调用转发给（委托者）内部的实例引用。委托的用途比继承更加广泛。用继承能实现的对类的任何形式的扩展都可以用委托的方式完成。使用委托模式可以很容易的在运行时对其行为进行组合。

委托的模式涉及到两个参与者：

- Delegator（委托者）

  – 保存Delegate的实例引用。

  – 实现Delegate的接口。

  – 将对Delegate接口方法的调用转发给Delegate。

- Delegate（被委托者）

  – 接受Delegator的调用，帮助Delegator实现其接口。

### 委托模式示例

#### 简单的委托模式

Printer拥有针式打印机RealPrinter的实例，Printer拥有的方法print()将处理转交给RealPrinter的方法print()。

```java
public class RealPrinter {// 被委托者
	void print() {
		System.out.print("something");
	}
}
public class Printer {// 委托者
 
	RealPrinter p = new RealPrinter(); // 创建一个被委托者
 
	void print() {
		p.print(); // delegation
	}
}
public class MainTest {
 
	public static void main(String[] args) {
		Printer printer = new Printer();
        printer.print();//something
	}
}
```

#### 复杂的委托模式

在这个例子里，类別C可以委托类別A或类別B，类別C擁有方法使自己可以在类別A或类別B间选择。因为类別A或类別B必须实现接口I规定的方法，所以在这里委托是类型[安全](http://www.liuhaihua.cn/archives/tag/%e5%ae%89%e5%85%a8)的。

```
public interface I {
	void f();
 
	void g();
}
public class A implements I{
 
	@Override
	public void f() {
		System.out.println(A.class.getSimpleName()+":doing f()");
	}
 
	@Override
	public void g() {
		System.out.println(A.class.getSimpleName()+":doing g()");
	}
 
}
public class B implements I{
 
	@Override
	public void f() {
		System.out.println(B.class.getSimpleName()+":doing f()");
	}
 
	@Override
	public void g() {
		System.out.println(B.class.getSimpleName()+":doing g()");
	}
 
}
public class C implements I {
 
	// 委托
	I i = new A();
 
	@Override
	public void f() {
		i.f();
	}
 
	@Override
	public void g() {
		i.g();
	}
 
	public void toA() {
		i = new A();
	}
 
	public void toB() {
		i = new B();
	}
 
}
public class MainTest {
 
	public static void main(String[] args) {
		C c = new C();
		c.f(); // A: doing f()
		c.g(); // A: doing g()
		c.toB();
		c.f(); // B: doing f()
		c.g(); // B: doing g()
	}
 
}
```

这里的比较复杂的委托模式示例跟J2EE中的业务委托很类似，下面就列举一个业务委托示例。

#### 业务委托模式

业务委托模式（Business Delegate Pattern）用于对表示层和[业务层](http://www.liuhaihua.cn/archives/tag/%e4%b8%9a%e5%8a%a1%e5%b1%82)解耦。它基本上是用来减少通信或对表示层[代码](http://www.liuhaihua.cn/archives/tag/%e4%bb%a3%e7%a0%81)中的业务层代码的远程查询功能。在业务层中我们有以下实体。

- **客户端（Client）**
  表示层代码可以是 JSP、[servlet](http://www.liuhaihua.cn/archives/tag/servlet) 或 [UI](http://www.liuhaihua.cn/archives/tag/ui) [java](http://www.liuhaihua.cn/archives/tag/java-2) 代码。
- **业务委托类（Business Delegate）**
  一个为客户端实体提供的入口类，它提供了对业务服务方法的访问。
- **查询服务（LookUp Service）**
  查找服务对象负责获取相关的业务实现，并提供业务对象对业务委托对象的访问。
- **业务服务（Business Service）**
  业务服务接口。实现了该业务服务的实体类，提供了实际的业务实现逻辑。

创建BusinessService接口。

```
public interface BusinessService {
    void doProcessing();
}
```

创建实体服务类。

```
public class EJBService implements BusinessService{
 
    public void doProcessing() {
        System.out.println("Processing task by invoking EJB Service");
    }
 
}
 
public class JMSService implements BusinessService{
 
    public void doProcessing() {
        System.out.println("Processing task by invoking JMS Service");
    }
 
}
```

创建业务查询服务。

```
public class BusinessLookUp {
    public BusinessService getBusinessService(String serviceType){
        if (serviceType.equalsIgnoreCase("EJB")){
            return new EJBService();
        } else {
            return new JMSService();    
        }
    }
}
```

创建业务委托。

```
public class BusinessDelegate {
    private BusinessLookUp lookupService = new BusinessLookUp();
    private BusinessService businessService;
    private String serviceType;
    
    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }
    
    public void doTask(){
        businessService = lookupService.getBusinessService(serviceType);
        businessService.doProcessing();
    }
    
}
```

创建客户端。

```
public class Client {
    BusinessDelegate businessDelegate;
    public Client(BusinessDelegate businessDelegate){
        this.businessDelegate = businessDelegate;
    }
    
    public void doTask(){
        businessDelegate.doTask();
    }
}
```

### 与其它设计模式区别

在许多[文章](http://www.liuhaihua.cn/archives/tag/%e6%96%87%e7%ab%a0)中将代理模式与委托模式说成是一中模式，通过上述的几个例子可以看出，这里确实跟代理模式很相似。但是使用代理模式的目的在于提供一种代理以 **控制**
对这个对象的访问，但是委托模式的出发点是将某个对象的请求委托给另一个对象，这种委托类似将整个事情 **全权**
委托给别人。另外在面向对象软件开发中，我们经常说要多用组合少用继承，而委托模式本身设计的出发点就是使用组合代替继承。

也有说代理模式中，代理类和被代理类需要实现相同的接口，但是这句话实际上有很大问题的，需要实现相同的接口这一点是针对静态代理而言的，动态代理中并不需要实现相同的接口。

### 参考资料

设计模式–delegate模式

维基百科-委托模式

设计模式中代理(proxy)与委托(delegate)的语义区别

Java Business Delegate Pattern(业务代表模式)

 



http://www.liuhaihua.cn/archives/530053.html