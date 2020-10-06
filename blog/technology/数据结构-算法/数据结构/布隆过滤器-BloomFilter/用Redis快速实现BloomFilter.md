[TOC]



# 用Redis快速实现BloomFilter 



学习理解用,具体使用还是用redison的布隆过滤器

 

## 背景

最近工作上有个类似需求是： 现有约3亿条数据词典存在于一个csv文件A中，作为数据源。对于 用户输入的任意单词M，需要快速的在A中匹配M单词是否存在。（A文件约3G大小左右，总行数三亿）

拿到这个需求，你的第一想法怎么做呢？

正常思路可能是：

1. 将csv文件A导入某关系型数据库。
2. sql查询按M匹配。

上面的方式有个明显的缺点是：慢！

3亿多行的数据，即便是建好索引进行检索，匹配到也得话不少时间（笔者没亲自试过，感兴趣的朋友可以自行测试测试，理论上快不起来的）。

目前能 在时间复杂度和空间复杂度上达到最佳的方案，恐怕就是Bloom Filter了， 维基地址：Bloom Filter

> 此处给不太了解Bloom Filter的读者看，熟悉的朋友直接看下一节。
>
> 本文场景Bloom Filter 使用思路解释：
> 假设申请了一段bit位大数组（即数组中的元素只能是一个bit位，1或0，默认元素值都为0）
> 将csv文件A中的每个单词，经过多个hash函数进行hash运算之后得到在大数组中对应的多个下标位置
> 将步骤2中得到的多个下标位置的bit位都置为1.
> 对于用户输入的任意单词M，按照2的步骤得到多个下标位置，其对应大数组中的值全部为1则存在，否则不存在。

## 方案选型

实现Bloom Filter的方法很多，有各种语言版本的，这里为了真切感受一下算法的魅力，笔者这里决定用java 代码徒手撸了！

另一方面，考虑到分布式应用的需要，显然在单机内存上构建Bloom Filter存储是不太合适的。 这里选择redis。

redis有以下为操作，可以用于实现bloomfilter： 

```
redis> SETBIT bit 10086 1
(integer) 0
redis> GETBIT bit 10086
(integer) 1
redis> GETBIT bit 100 # bit 默认被初始化为 0
(integer) 0
```

## 实现细节

实现bloom filter的关键是hash函数，一般为了降低误报率、减少hash碰撞的影响，会选择多个hash函数。

那么，怎么写一个hash函数呢？

不要方，我们要的hash是 input: String --> output: int , jdk里面的String类不是恰好也有一个hashCode 方法吗？ 翻出来看一看！ 

```java
/**
* Returns a hash code for this string. The hash code for a
* {@code String} object is computed as
* <blockquote><pre>
* s[0]*31^(n-1) + s[1]*31^(n-2) + ... + s[n-1]
* </pre></blockquote>
* using {@code int} arithmetic, where {@code s[i]} is the
* <i>i</i>th character of the string, {@code n} is the length of
* the string, and {@code ^} indicates exponentiation.
* (The hash value of the empty string is zero.)
*
* @return a hash code value for this object.
*/
public int hashCode() {
    int h = hash;
    if (h == 0 && value.length > 0) {
        char val[] = value;
        for (int i = 0; i < value.length; i++) {
            h = 31 * h + val[i];
        }
        hash = h;
    }
    return h;
}
```

看到这一行h = 31 * h + val[i];，貌似原理其实也很简单，每个字符对应的ascii码，经过一个公式计算依次加起来。这里有个系数31, 稍微变一下， 不就可以有多个hash函数了吗。

以下是稍加修改后的hash函数： 

```java
//总的bitmap大小 64M
private static final int cap = 1 << 29;
/*
* 不同哈希函数的种子，一般取质数
* seeds数组共有8个值，则代表采用8种不同的哈希函数
*/
private int[] seeds = new int[]{3, 5, 7, 11, 13, 31, 37, 61};
private int hash(String value, int seed) {
    int result = 0;
    int length = value.length();
    for (int i = 0; i < length; i++) {
        result = seed * result + value.charAt(i);
    }
    return (cap - 1) & result;
}
```

剩下的事情便很简单了，对每个词典A中的单词，依次调seeds 中对应的hash函数（这里一共是8个），用redis的setbit操作，将下标值置为1.

redis代码 (这里用pipeline 包装了下。) 

```java
@Service
public class RedisService {
    @Autowired
    private StringRedisTemplate template;
    public void multiSetBit(String name, Boolean value, long... offsets) {
        template.executePipelined((RedisCallback<Object>) connection -> {
            for (long offset : offsets) {
                connection.setBit(name.getBytes(), offset, value);
            }
            return null;
        }
        );
    }
    public List<Boolean> multiGetBit(String name, long... offsets) {
        List<Object> results = template.executePipelined((RedisCallback<Object>) connection -> {
            for (long offset : offsets) {
                connection.getBit(name.getBytes(), offset);
            }
            return null;
        }
        );
        List<Boolean> list = new ArrayList<>();
        results.forEach(obj -> {
            list.add((Boolean) obj);
        }
        );
        return list;
    }
}
```

最后，代码串起来大概长这个样子：

复制

```java
FileInputStream inputStream = new FileInputStream("/XXXX.csv");
BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
HashSet<long> totalSet=new HashSet<>();
String word=null;
while((word = bufferedReader.readLine()) != null){
    for (int seed : seeds) {
        int hash = hash(word, seed);
        totalSet.add((long) hash);
    }
    long[] offsets = new long[totalSet.size()];
    int i=0;
    for (long l:totalSet){
        offsets[i++]=l;
    }
    redisService.multiSetBit("BLOOM_FILTER_WORDS_DICTIONARY", true, offsets);
}
```

查的时候也类似：

复制

```java
String word = "XXXX";
//实际输入
long[] offsets = new long[seeds.length];
for (int i = 0; i < seeds.length; i++) {
    int hash = hash(mobile, seeds[i]);
    offsets[i] = hash;
}
List<Boolean> results = redisService.multiGetBit("BLOOM_FILTER_WORDS_DICTIONARY", offsets);
//判断是否都为true （则存在)
Boolean isExisted=true;
for (Boolean result:results){
    if(!result){
        isExisted=false;
        break;
    }
}
```

注意事项

setbit的offset是用大小限制的，在0到 232（最大使用512M内存）之间，即0~4294967296之前，超过这个数会自动将offset转化为0，因此使用的时候一定要注意。

 

文章出处:[用Redis快速实现BloomFilter！](https://www.cnblogs.com/Java-no-1/p/11178761.html)





https://www.hellojava.com/a/81587.html