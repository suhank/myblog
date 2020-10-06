# Murmur哈希算法

2019.11.15 17:23:35字数 655阅读 923

说到哈希算法，可能大部分人都会不自觉得想到 md 和 sha 系列，在这之前，我就是这样的，因为他们意味着流行安全和稳定。但是，最近我知道了一款另类的流行的哈希函数，**这款哈希函数广泛应用于分布式系统-Hadoop/Lucence等等，原因就是因为它速度快而且散列效果好**，这个哈希算法就是 MurmurHash。

哈希系列比较流行的有三个系列，分别是 MD/SHA 和 MAC 系列，但是这些系列都是比较安全，虽然 MD5 和 SHA-1 已经被王小云教授碰撞了，但是相对我们平时使用的直接简单求模这种还是比较安全的，相对安全带来的负面效果就是**计算量还是挺大的，而且不保证哈希结果的均匀**。而在分布式环境下，为了资源的合理利用，我们需要的更多是均匀，因为是内部散列的作用，所以哈希安全我们并不那么在乎，所以在这种情境下，2008 才被发明的 MurmurHash 成为了分布式中的宠儿，深受 Google 系的喜爱。

MurmurHash 当前最新的版本是 MurmurHash3，它能够产生出32-bit或128-bit哈希值。除了我们能够猜到的不再使用的 mmh1 以及 还在使用的 mmh2 之外，还有好些变种，不过都是针对平台优化的。

Murmur哈希的算法确实比较简单，它的计算过程其实就是它的名字，MUltiply and Rotate，因为它在哈希的过程要经过多次MUltiply and Rotate，所以就叫 MurMur 了。

算法原理可参考维基百科：[https://zh.wikipedia.org/wiki/Murmur%E5%93%88%E5%B8%8C](https://links.jianshu.com/go?to=https%3A%2F%2Fzh.wikipedia.org%2Fwiki%2FMurmur%E5%93%88%E5%B8%8C)

Scala API自身是有MurmurHash算法的实现的（[scala.util.hashing.MurmurHash3](https://links.jianshu.com/go?to=http%3A%2F%2Fwww.scala-lang.org%2Fapi%2Fcurrent%2Fscala%2Futil%2Fhashing%2FMurmurHash3%24.html)），返回值是int，32位。

spark也广泛采用了Murmur哈希算法，可以看一个在sparksql中的例子，在TreeNode类中有：

```kotlin
private lazy val _hashCode: Int = scala.util.hashing.MurmurHash3.productHash(this)
```

之所以调用`productHash`方法是因为TreeNode继承自scala的`Product`特质（有兴趣的同学可以通过反编译查看到，scala 的Case class类实现了scala.Product和scala.Serializable接口（Product和Serializable都是Traits）），而且有很多case class 类继承TreeNode类。

参考：
[https://liqiang.io/post/murmurhash-introduction](https://links.jianshu.com/go?to=https%3A%2F%2Fliqiang.io%2Fpost%2Fmurmurhash-introduction)
[https://leibnizhu.github.io/2017/01/19/Scala%E5%AE%9E%E7%8E%B064%E4%BD%8D%E7%9A%84MurmurHash%E5%87%BD%E6%95%B0/index.html](https://links.jianshu.com/go?to=https%3A%2F%2Fleibnizhu.github.io%2F2017%2F01%2F19%2FScala%E5%AE%9E%E7%8E%B064%E4%BD%8D%E7%9A%84MurmurHash%E5%87%BD%E6%95%B0%2Findex.html)



https://www.jianshu.com/p/ef7b692c369e