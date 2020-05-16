# java 8 Function，Consumer，Predicate 接口

## **一. Function 接口的使用**

​    该接口目前发布在 java.util.function 包中。接口中主要有方法：

```
R apply(T t);
```

 将Function对象应用到输入的参数上，然后返回计算结果。

Demo：

​        如果 字符串为空，显示 "字符串不能为空"，如果字符串长度大于3，显示 "字符串过长"。那么按照普通的方式，我就就是两个 if 语句。现在使用Function 接口如下：

```java
public class Test {
	public static void main(String[] args) throws InterruptedException {
		String name = "";
		String name1 = "12345";
		System.out.println(validInput(name, inputStr -> inputStr.isEmpty() ? "名字不能为空":inputStr));
		System.out.println(validInput(name1, inputStr -> inputStr.length() > 3 ? "名字过长":inputStr));
	}
	
	public static String validInput(String name,Function<String,String> function) {
		return function.apply(name);
	}
}

```

​        解释：

1. 定义 validInput 方法，传入 function 接口，然后在该方法中定义 function.apply(name)，也就是说，传入一个 name 参数，应用某些规则，返回一个结果，至于是什么规则，先不定义。
2. 在main 方法中调用 validInput(name,inputStr ...)，这里我们定义规则，利用lambda 表达式， 规则是：传入一个 inputStr 字符串，如果为空，返回 xx；否则 返回 xx。

从上述的例子可以看出，其实 这种方式 比起 两个 if 语句 看起来要爽一点（个人感觉）。

## **二. Consumer 接口的使用 以及 和Function 接口 的区别**

​        该接口表示接受单个输入参数并且没有返回值的操作。接口里面重要方法为：

```
void accept(T t);
```

**2.1 使用**

​        还是如上的例子，Demo代码如下：

```java
public class Test {
	public static void main(String[] args) throws InterruptedException {
		String name = "";
		String name1 = "12345";
		
		validInput(name, inputStr ->  
				System.out.println(inputStr.isEmpty() ? "名字不能为空":"名字正常"));
		validInput(name1, inputStr ->
				System.out.println(inputStr.isEmpty() ? "名字不能为空":"名字正常"));
		
	}
	public static void validInput(String name,Consumer<String> function) {
		function.accept(name);
	}
}

```

​        理解和Function 差不多。

注意，如下代码并不能通过编译（作者也不太了解，目前猜测是该）：

```
validInput(name1, inputStr -> inputStr.length() > 3 ? 
    System.out.println("名字过长"):System.out.println("名字正常"));
```

错误显示为：

```
The method validInput(String, Consumer<String>) in the type Test is not applicable for the arguments (String, (<no type> inputStr) -> {})
```

希望有会的 不吝赐教。

**2.2 Consumer 和 Function 的区别**

​        主要就是 Consumer 接口没有返回值， Function 接口有返回值。

## **三. Predicate 接口的使用**

​        Predicate 方法 表示 判断 输入的对象是否 符合某个条件。主要方法如下：

```
boolean test(T t);
```

Demo，还是如上面的例子：

```java
public class Test {
	public static void main(String[] args) throws InterruptedException {
		String name = "";
		String name1 = "12";
		String name2 = "12345";
		
		System.out.println(validInput(name,inputStr ->  !inputStr.isEmpty() &&  inputStr.length() <= 3 ));
		System.out.println(validInput(name1,inputStr ->  !inputStr.isEmpty() &&  inputStr.length() <= 3 ));
		System.out.println(validInput(name2,inputStr ->  !inputStr.isEmpty() &&  inputStr.length() <= 3 ));
		
	}
	public static boolean validInput(String name,Predicate<String> function) {
		return function.test(name);
	}
}

```

输出为：

```
false
true
false
```

## 总结：

1. Function：接受一个参数，返回一个参数。
2. Consumer：接受一个参数，不返回参数。
3. Predicate：用于测试是否符合条件。

参考：

1.Function接口 - Java8中 java.util.function包下的函数式接口： <http://ifeve.com/jjava-util-function-java8/>

2.Predicate接口和Consumer接口 - Java8中 java.util.function包下的函数式接口： <http://ifeve.com/predicate-and-consumer-interface-in-java-util-function-package-in-java-8/>





https://blog.csdn.net/pzxwhc/article/details/48314039