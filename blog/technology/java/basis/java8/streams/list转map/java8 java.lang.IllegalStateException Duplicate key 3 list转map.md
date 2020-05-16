[TOC]



# JDK8 java.lang.IllegalStateException: Duplicate key 3 list转map



 

 

## 异常

```
java.lang.IllegalStateException: Duplicate key 3
    at java.util.stream.Collectors.lambda$throwingMerger$0(Collectors.java:133)
    at java.util.HashMap.merge(HashMap.java:1253)
    at java.util.stream.Collectors.lambda$toMap$58(Collectors.java:1320)
    at java.util.stream.ReduceOps$3ReducingSink.accept(ReduceOps.java:169)
    at java.util.ArrayList$ArrayListSpliterator.forEachRemaining(ArrayList.java:1374)
    at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:481)
    at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:471)
    at java.util.stream.ReduceOps$ReduceOp.evaluateSequential(ReduceOps.java:708)
    at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
    at java.util.stream.ReferencePipeline.collect(ReferencePipeline.java:499)
    at com.eliteams.quick4j.test.jdk8.MapDuplicateKeyTest.mapkey(MapDuplicateKeyTest.java:30)
    at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
    at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
    at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
    at java.lang.reflect.Method.invoke(Method.java:498)
    at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50)
    at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12)
    at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47)
    at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17)
    at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325)
    at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:78)
    at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:57)
    at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290)
    at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71)
    at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288)
    at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58)
    at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268)
    at org.junit.runners.ParentRunner.run(ParentRunner.java:363)
    at org.eclipse.jdt.internal.junit4.runner.JUnit4TestReference.run(JUnit4TestReference.java:86)
    at org.eclipse.jdt.internal.junit.runner.TestExecution.run(TestExecution.java:38)
    at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:459)
    at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:678)
    at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.run(RemoteTestRunner.java:382)
    at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.main(RemoteTestRunner.java:192)1234567891011121314151617181920212223242526272829303132333435
```

## 测试代码

```
/**
 * @author Administrator
 *
 */
public class MapDuplicateKeyTest {

    @Test
    public void mapkey() {
        List<Entity> list = new ArrayList<>();
        list.add(new Entity("20170728120", 1));
        list.add(new Entity("20170728119", 3));
        list.add(new Entity("20170728119", 2));

        Map<String, Integer> map = list.stream().collect(
                Collectors.toMap(Entity::getKey, Entity::getValue));


        map.entrySet().stream().forEach(e -> System.out.println(e.getValue()));
    }

} 
```

## 问题分析

从异常可以看出，是在调用**Collectors.toMap**时出错，查看`toMap`的API：

------

### toMap

```
public static <T,K,U> Collector<T,?,Map<K,U>> toMap(Function<? super T,? extends K> keyMapper,
                                                    Function<? super T,? extends U> valueMapper) 
```

Returns a Collector that accumulates elements into a Map whose keys and values are the result of applying the provided mapping functions to the input elements. 
**If the mapped keys contains duplicates (according to Object.equals(Object)), an IllegalStateException is thrown when the collection operation is performed**. If the mapped keys may have duplicates, use toMap(Function, Function, BinaryOperator) instead.

#### API Note:

It is common for either the key or the value to be the input elements. In this case, the utility method Function.identity() may be helpful. For example, the following produces a Map mapping students to their grade point average:

```
 Map<Student, Double> studentToGPA students.stream()
        .collect(toMap(Functions.identity(), student -> computeGPA(student))); 
```

And the following produces a Map mapping a unique identifier to students:

```
 Map<String, Student> studentIdToStudent
         students.stream().collect(toMap(Student::getId, Functions.identity()); 
```

其中加粗的地方说明，如果在最后生成map的时候，mapped到的keys中如果包含重复的键（通过key类型的equals方法来判断），则会抛出异常`IllegalStateException`。但是，后面也提到，如果keys中包含有相同的键，则可以使用`toMap(Function, Function, BinaryOperator)`方法来替代。

```
public static <T,K,U> Collector<T,?,Map<K,U>> toMap(Function<? super T,? extends K> keyMapper,
                                                    Function<? super T,? extends U> valueMapper,
                                                    BinaryOperator<U> mergeFunction) 
```

其中，新增加的参数就是来处理相同key时如何生成对应的value。示例如下：

```
Map<String, String> phoneBook people.stream()
    .collect(toMap(Person::getName, Person::getAddress, (s, a) -> s + ", " + a)); 
```

如果出现相同的人名，则将他们的地址字符合并起来；

## 结论

使用含有`mergeFunction`参数的函数，本示例修改方案，**当出现相同key，则value相加**，如：

```java
public class Student {
	 public String name;
	 public Integer marks;

	 public Student(String name, Integer marks) {
	  this.setName(name);
	  this.setMarks(marks);
	 }

	 public String getName() {
	  return name;
	 }

	 public void setName(String name) {
	  this.name = name;
	 }

	 public Integer getMarks() {
	  return marks;
	 }

	 public void setMarks(Integer marks) {
	  this.marks = marks;
	 }

	 @Override
	 public String toString() {
	  return getName();
	 }
}
```



```java
    /**
     * <B>Description:</B> list转map  当出现相同key，则value相加 <br>
     * <B>Create on:</B> 2018/7/17 下午1:58 <br>
     *
     * @author xiangyu.ye
     */
    @Test
    public void mapkey() {
        //key 是一样的处理方式
        List<Student> list = new ArrayList<>();
        list.add(new Student("20170728120", 1));
        list.add(new Student("20170728119", 3));
        list.add(new Student("20170728119", 2));
        list.add(new Student("20170728121", null));

        //这个如果value是null会报空指针异常,所以一定要过滤 null
        Map<String, Integer> map = list.stream().filter(m-> Objects.nonNull(m.getMarks())).collect(
                Collectors.toMap(Student::getName, Student::getMarks, (value1, value2) -> value1 + value2 ));

        //这个就没问题,但是很怪增加复制 如果当出现相同key，则value相加 逻辑处理起来变复杂
//        Map<String, Integer> map = list.stream().collect(
//                HashMap::new, (m, v) -> m.put(v.getName(), v.getMarks()), HashMap::putAll);


        System.out.println(JSON.toJSONString(map));
    }
```

结果：

```
20170728119 = 5
20170728120 = 1 
```

------

附API地址：<http://docs.oracle.com/javase/8/docs/api/java/util/stream/Collectors.html#toMap-java.util.function.Function-java.util.function.Function->





https://blog.csdn.net/huoer_12/article/details/76651780