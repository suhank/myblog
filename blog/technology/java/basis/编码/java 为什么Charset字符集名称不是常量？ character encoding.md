# java 为什么Charset字符集名称不是常量？ character encoding



字符集问题是混乱和复杂的自己，但除此之外，你必须记住你的字符集的确切名称。是“utf8”吗？还是“utf-8”？或者可能是“UTF-8”？当搜索互联网的代码样本，你会看到所有上述。为什么不让它们命名常量和使用Charset.UTF8？

然而，有六个需要存在，因此常数可能已经为那些很久以前。我不知道为什么他们不是。

JDK 1.4通过引入Charset类型做了一件了不起的事。在这一点上，他们不想再提供String常量，因为目标是让每个人都使用Charset实例。那么为什么不提供六个标准的字符集常量呢？我问马丁·布霍尔茨，因为他恰好坐在我旁边，他说没有一个真正特别的理由，除了当时，事情仍然半裸 – 太少的JDK API已经改装接受字符集，以及那些，字符集重载通常执行得稍差。

很遗憾，只有在JDK 1.6中，他们终于完成了所有的Charset重载。而这种倒退的性能情况仍然存在(为什么是令人难以置信的奇怪，我不能解释，但是与安全相关的原因)。

## 答

长故事短 – 只是定义你自己的常量，或使用Guava的Charsets类托尼小马链接(虽然那个图书馆还没有真正发布)。

更新：一个[`StandardCharsets`](http://docs.oracle.com/javase/7/docs/api/java/nio/charset/StandardCharsets.html)类是在JDK 7。

翻译自：https://stackoverflow.com/questions/1684040/java-why-charset-names-are-not-constants



```java
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
 
public class Demo {
 
	public static void main(String[] args) {
		Charset utf8 = StandardCharsets.UTF_8;
		System.out.println(utf8.name());
	}
}

```



https://codeday.me/bug/20170318/6995.html