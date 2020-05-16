[TOC]



## HuTool 简化Java编程的法宝，让工作更高效

[~wangweijun](https://copyfuture.com/users/qq_42453117) 2019-12-16 14:07:52 **阅读数:10** **评论数:0** **点赞数:0** **收藏数:0**

上篇文章介绍到了HuTool项目中提供的一些组件，但HuTool的功能可远不止如此，接下来，我将介绍HuTool为我们提供的一些便捷工具。

如果你没有看过之前的文章，也不要紧，这并不影响你对接下来的内容的理解，不过为了照顾直接看到第二篇的同学，还是有必要介绍一下HuTool的引入方式。

在项目的pom.xml的dependencies中加入以下内容：

```
<dependency>
    <groupId>cn.hutool</groupId>
    <artifactId>hutool-all</artifactId>
    <version>5.0.7</version>
</dependency>
```

非Maven项目的话就自己百度下载一下jar包，导入即可。

### StrUtil

看这里名字也应该明白了，这是对字符串进行处理的工具类。

关于字符串，就没什么好介绍的了，直接来看看它为我们提供了哪些方法吧。

**1、hasBlank、hasEmpty**

这两个方法都是用来判断字符串是否为空的，看如下代码：

```
@Test
//判断字符串是否为空
public void hasBlankOrhasEmptyTest(){
    String str1 = "  ";
    String str2 = "";
    System.out.println(StrUtil.hasBlank(str1));
    System.out.println(StrUtil.hasBlank(str2));
    System.out.println(StrUtil.hasEmpty(str1));
    System.out.println(StrUtil.hasEmpty(str2));
}
```

运行结果：

```
true
true
false
true
```

需要注意的就是，虽然这两个方法的作用都是判断给定的字符串是否为空，但是hasEmpty方法只能判断为null和空字符串("")，而hasBlank方法还会将不可见字符也视为空。比如上面的程序中，对于str1，它的值为不可见的字符(空格)，hasEmpty方法该字符串不为空，而hasBlank方法认为该字符串为空；但对于str2，两个方法没有歧义，统一认定其为空。

**2、removePrefix、removeSuffix**

这两个方法分别用于去除字符串的指定前缀和后缀。

看代码：

```
@Test
    //去除字符串的指定前缀和后缀
    public void removePrefixOrremoveSuffixTest(){
        String str1 = "test.jpg";
        //去除指定后缀
        System.out.println(StrUtil.removeSuffix(str1,".jpg"));
        //去除指定前缀
        System.out.println(StrUtil.removePrefix(str1,"test"));
    }
```

运行结果：

```
test
.jpg
```

**3、sub**

该方法改进自JDK提供的subString方法，还记得JDK的subString方法是做什么的吗？

它是用来截取字符串的，通过给定索引返回对应的子串，由于传统的subString方法问题实在太多，你问我有什么问题？看代码：

```
@Test
    public void subTest(){
        String str = "hello world";
        System.out.println(str.substring(0,12));
    }
```

在这段程序中，字符串str的长度为11，但在截取字符串长度的时候却截取到12，显然是索引越界了，但有时候我们很容易犯这种错误，可运行直接报错并不是一个好的方式。为此，StrUtil为我们提供了sub方法，它考虑到了各种需要考虑的情况并做了相应的处理，同时，它还支持索引为负数，-1表示最后一个字符，这是Python的风格，作者应该是个Python迷。

代码如下：

```
@Test
    //截取字符串
    //index从0开始计算，最后一个字符为-1
    //如果from和to位置一样，返回 ""
    //如果from或to为负数，则按照length从后向前数位置，如果绝对值大于字符串长度，则from归到0，to归到length
    //如果经过修正的index中from大于to，则互换from和to
    public void subTest(){
        String str = "hello world";
        System.out.println(StrUtil.sub(str,0,12));
    }
```

此时即使你的索引位置极其离谱，sub方法也能轻松应对，该程序的运行结果为：

```
hello world
```

**4、format**

该方法用于格式化文本，可以使用字符串模板代替字符串拼接，看代码：

```
@Test
    //格式化文本
    public void formatTest(){
        String str = "{}山鸟飞{}";
        String formatStr = StrUtil.format(str, "千", "绝");
        System.out.println(formatStr);
    }
```

运行结果：

```
千山鸟飞绝
```

该方法通过{}作为占位符，然后按照参数顺序替换占位符，所以参数的位置一定要注意，如果把"绝"字放在前面，那结果就不一样了。

```
@Test
    //格式化文本
    public void formatTest(){
        String str = "{}山鸟飞{}";
        String formatStr = StrUtil.format(str, "绝", "千");
        System.out.println(formatStr);
    }
```

运行结果：

```
绝山鸟飞千
```

### URLUtil

该工具类专门用于处理url。

**1、url**

通过该方法可以将一个字符串转换为URL对象，代码如下：

```
@Test
    //将字符串转换为URL对象
    public void urlTest() {
        URL url = URLUtil.url("http://localhost:8080/name=zhangsan&age=20");
        //获取URL中域名部分，只保留URL中的协议
        URI uri = URLUtil.getHost(url);
        System.out.println(uri);
    }
```

运行结果：

```
http://localhost
```

**2、getURL**

该方法用于获得URL，常用于使用绝对路径时的情况 ，代码如下：

```
@Test
    //获得URL，常用于使用绝对路径时的情况
    public void getURLTest() {
        URL url = URLUtil.getURL(FileUtil.file("URLUtilTest.java"));
        System.out.println(url.toString());
    }
```

运行结果：

```
file:/C:/Users/Administrator/Desktop/ideaworkspace/HuTool/out/production/HuTool/URLUtilTest.java
```

该方法通过文件名就可以获取到该文件的绝对路径，这在使用绝对路径的场景中非常方便。

**3、normalize**

该方法用于标准化URL链接，代码如下：

```
@Test
    //标准化化URL链接
    public void normalizeTest() {
        String url = "www.baidu.com\\example\\test/a";
        String newUrl = URLUtil.normalize(url);
        System.out.println(newUrl);
    }
```

运行结果：

```
http://www.baidu.com/example/test/a
```

该方法会对不带http://头的链接进行自动补全，并统一格式。

**4、getPath**

该方法用于获取URL链接中的path部分字符串，比如：

```
@Test
    //获得path部分
    public void getPathTest() {
        String url = "http://localhost/search?name=abc&age=20";
        String pathStr = URLUtil.getPath(url);
        System.out.println(pathStr);
    }
```

运行结果：

```
/search
```

### ObjectUtil

在我们的日常使用中，有些方法是针对Object通用的，这些方法不区分何种对象，针对这些方法，Hutool封装为`ObjectUtil`。

**1、equal**

该方法用于比较两个对象是否相等，相等的条件有两个：

1. obj1 == null && obj2 == null
2. obj1.equal(obj2)

这两个条件满足其中一个就表示这两个对象相等，代码如下：

```
    @Test
    //比较两个对象是否相等。
    //相同的条件有两个，满足其一即可：
    //obj1 == null && obj2 == null obj1.equals(obj2)
    public void equalTest() {
        Object obj = null;
        Object obj2 = null;
        boolean equal = ObjectUtil.equal(obj, obj2);
        System.out.println(equal);
    }
```

运行结果：

```
true
```

**2、length**

该方法用于计算传入对象的长度，如果传入的是字符串，则计算字符串长度；如果传入的是集合，则计算集合大小；length方法会自动调用对应类型的长度计算方法。

```
@Test
    //计算对象长度，如果是字符串调用其length函数，集合类调用其size函数，数组调用其length属性，其他可遍历对象遍历计算长度
    //支持的类型包括： CharSequence Map Iterator Enumeration Array
    public void lengthTest() {
        String str = "hello world";
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6);
        System.out.println(ObjectUtil.length(str));
        System.out.println(ObjectUtil.length(list));
    }
```

运行结果：

```
11
6
```

**3、contains**

该方法用于判断给定的对象中是否还有指定的元素，代码如下：

```
@Test
    //对象中是否包含元素
    //支持的对象类型包括： String Collection Map Iterator Enumeration Array
    public void containsTest() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6);
        boolean flag = ObjectUtil.contains(list, 1);
        System.out.println(flag);
    }
```

运行结果：

```
true
```

**4、isBasicType**

该方法用于判断给定的对象是否为基本类型，包括包装类型和非包装类型 ，代码如下：

```
@Test
    //是否为基本类型，包括包装类型和非包装类型
    public void isBasicTypeTest(){
        String str = "hello";
        int num = 100;
        boolean flag = ObjectUtil.isBasicType(str);
        boolean flag2 = ObjectUtil.isBasicType(num);
        System.out.println(flag);
        System.out.println(flag2);
    }
```

运行结果：

```
false
true
```

### ReflectUtil

反射机制是Java的核心，Java框架的实现就使用到了大量的反射，HuTool针对Java反射做了一些封装。

**1、getMethods**

该方法用于获取一个类中所有的方法，包括其父类中的方法。

```
@Test
    //获得一个类中所有方法列表，包括其父类中的方法
    public void getMethodsTest() {
        Method[] methods = ReflectUtil.getMethods(Object.class);
        for (Method method : methods) {
            System.out.println(method.getName());
        }
    }
```

运行结果：

```
finalize
wait
wait
wait
equals
toString
hashCode
getClass
clone
notify
notifyAll
registerNatives
```

**2、getMethod**

该方法用于获取某个类的指定方法，代码如下：

```
@Test
    //获取某个类的指定方法
    public void getMethodsTest() {
        Method method = ReflectUtil.getMethod(Object.class, "getClass");
        System.out.println(method);
    }
```

运行结果：

```
public final native java.lang.Class java.lang.Object.getClass()
```

**3、newInstance **

该方法通过类的Class类型实例化对象，代码如下：

```
@Test
    //实例化对象
    public void newInstanceTest() {
        Object obj = ReflectUtil.newInstance(Object.class);
        boolean flag = ObjectUtil.isNull(obj);
        System.out.println(flag);
    }
```

运行结果：

```
false
```

**4、invoke**

该方法用于执行对象中的方法，代码如下：

```
@Test
    //执行方法
    public void invokeTest() {
        ArrayList list = ReflectUtil.newInstance(ArrayList.class);
        ReflectUtil.invoke(list,"add",1);
        System.out.println(list);
    }
```

运行结果：

```
[1]
```

其中第二个参数是要执行的方法名，第三个参数是要执行的方法参数。

### ClipboardUtil

这是一个剪切板的工具类，用于简化对剪切板的操作，某些场景下可能会使用到。

**1、getStr**

该方法用于获取剪切板的内容，比如你用鼠标选取了一段内容进行复制，该方法就可以获取到复制的内容，代码如下：

```
@Test
    //从剪切板获取文本内容
    public void getStrTest() {
        String  str = ClipboardUtil.getStr();
        System.out.println(str);
    }
```

运行结果：

```
String  str = ClipboardUtil.getStr();
```

**2、setStr**

该方法用于设置剪切板的内容，即：将指定的字符串设置到剪切板上，相当于你复制了该内容，代码如下：

```
@Test
    //设置剪切板文本内容
    public void setStrTest() {
        String  str = ClipboardUtil.getStr();
        System.out.println(str);
        ClipboardUtil.setStr("hello world");
        String str2 = ClipboardUtil.getStr();
        System.out.println(str2);
    }
```

运行结果：

```
String  str = ClipboardUtil.getStr();
hello world
```

还有获取图片、设置图片等等方法，大家可以自行体验。

### ClassUtil

该类主要是封装了一些反射的方法，使得调用更加方便。

**1、getShortClassName**

该方法用于获取类名的短格式，代码如下：

```
@Test
    //获取类名的短格式
    public void getShortClassNameTest(){
        String shortClassName = ClassUtil.getShortClassName("com.wwj.hutool.test.ObjectUtilTest");
        System.out.println(shortClassName);
    }
```

运行结果：

```
c.w.h.t.ObjectUtilTest
```

**2、getPackage**

获取指定类的包名，代码如下：

```
@Test
    //获取指定类的包名
    public void getPackageTest(){
        String packageName = ClassUtil.getPackage(ObjectUtilTest.class);
        System.out.println(packageName);
    }
```

运行结果：

```
com.wwj.hutool.test
```

**3、scanPackage**

这个方法是该工具类的核心，这是一个扫描包下资源的方法，在Spring中用于依赖注入，代码如下：

```
@Test
    //扫描包下资源
    public void scanPackageTest(){
        Set<Class<?>> classes = ClassUtil.scanPackage("com.wwj.hutool.test");
        for (Class<?> aclass : classes) {
            System.out.println(aclass.getName());
        }
    }
```

运行结果：

```
com.wwj.hutool.test.URLUtilTest
com.wwj.hutool.test.StrUtilTest
com.wwj.hutool.test.ObjectUtilTest
```

该方法需要传递一个包名作为参数，然后便会在指定的包下扫描所有的类，你还可以通过传入ClassFilter对象来过滤掉指定的类。

**4、getJavaClassPaths**

该方法用于获取Java的系统变量定义的ClassPath。

```
@Test
    public void scanPackageTest(){
        String[] javaClassPaths = ClassUtil.getJavaClassPaths();
        for (String javaClassPath : javaClassPaths) {
            System.out.println(javaClassPath);
        }
    }
```

运行结果：

```
F:\Tool\IntelliJ IDEA 2018.3\lib\idea_rt.jar
F:\Tool\IntelliJ IDEA 2018.3\plugins\junit\lib\junit-rt.jar
F:\Tool\IntelliJ IDEA 2018.3\plugins\junit\lib\junit5-rt.jar
E:\Java\jdk1.8.0_181\jre\lib\charsets.jar
E:\Java\jdk1.8.0_181\jre\lib\deploy.jar
......
```

### RuntimeUtil

该工具类用于执行命令行命令，在Windows下是cmd，在Linux下是shell。

因为很简单，这里就直接贴出代码即可：

```
@Test
    public void RunTimeUtilTest(){
        String str = RuntimeUtil.execForStr("ipconfig");
        System.out.println(str);
    }
```

运行结果：

```
Windows IP 配置


以太网适配器 以太网:

   媒体状态  . . . . . . . . . . . . : 媒体已断开连接
   连接特定的 DNS 后缀 . . . . . . . : 

无线局域网适配器 本地连接* 1:

   媒体状态  . . . . . . . . . . . . : 媒体已断开连接
   连接特定的 DNS 后缀 . . . . . . . : 

无线局域网适配器 本地连接* 2:

   媒体状态  . . . . . . . . . . . . : 媒体已断开连接
   连接特定的 DNS 后缀 . . . . . . . : 

无线局域网适配器 WLAN:

   连接特定的 DNS 后缀 . . . . . . . : www.tendawifi.com
   本地链接 IPv6 地址. . . . . . . . : fe80::830:2d92:1427:a434%17
   IPv4 地址 . . . . . . . . . . . . : 192.168.0.103
   子网掩码  . . . . . . . . . . . . : 255.255.255.0
   默认网关. . . . . . . . . . . . . : 192.168.0.1
```

### NumberUtil

这是针对数学运算的工具类，在传统的Java开发中，经常会遇到小数之间的计算，而小数很容易丢失精度，为了精确，通常会使用到BigDecimal类，但它们之间的转换实在复杂。为此，HuTool提供了NumberUtil类，使用该类进行数学计算将会非常轻松。

**1、加减乘除**

```
@Test
    public void calcTest(){
        double d = 3.5;
        float f = 0.5f;
        System.out.println(NumberUtil.add(d,f));//加
        System.out.println(NumberUtil.sub(d,f));//减
        System.out.println(NumberUtil.mul(d,f));//乘
        System.out.println(NumberUtil.div(d,f));//除
    }
```

运行结果：

```
4.0
3.0
1.75
7.0
```

**2、保留小数**

```
@Test
    public void calcTest(){
        double d = 1234.56789;
        System.out.println(NumberUtil.round(d,2));
        System.out.println(NumberUtil.roundStr(d,3));
    }
```

运行结果：

```
1234.57
1234.568
```

通过round和roundStr方法均可实现保留小数，默认使用四舍五入的模式，当然你也可以传入相应的模式改变程序。

**3、数字判断**

NumberUtil提供了系列方法用于常见类型的数字判断，因为很简单，就不贴代码了，直接看看方法名和作用即可。

- `NumberUtil.isNumber` 是否为数字
- `NumberUtil.isInteger` 是否为整数
- `NumberUtil.isDouble` 是否为浮点数
- `NumberUtil.isPrimes` 是否为质数

**4、其它**

当然还有一些比较常见的数学运算，NumberUtil也进行了相应的封装。

- `NumberUtil.factorial` 阶乘
- `NumberUtil.sqrt` 平方根
- `NumberUtil.divisor` 最大公约数
- `NumberUtil.multiple` 最小公倍数
- `NumberUtil.getBinaryStr` 获得数字对应的二进制字符串
- `NumberUtil.binaryToInt` 二进制转int
- `NumberUtil.binaryToLong` 二进制转long
- `NumberUtil.compare` 比较两个值的大小
- `NumberUtil.toStr` 数字转字符串，自动并去除尾小数点儿后多余的0

### IdUtil

该工具类主要用于生成唯一ID。

**1、生成UUID**

```
@Test
    public void IdUtilTest(){
        String uuid = IdUtil.randomUUID();
        String simpleUUID = IdUtil.simpleUUID();
        System.out.println(uuid);
        System.out.println(simpleUUID);
    }
```

运行结果：

```
b1e4e753-39b9-4026-8a08-ce9837e15f62
23f1603604694d029bb35c1c03d7aeb1
```

ramdimUUID方法生成的是带’-‘的UUID，而simpleUUID方法生成的是不带’-'的UUID。

**2、ObjectId**

ObjectId是MongoDB数据库的一种唯一ID生成策略，是UUID version1的变种 。

Hutool针对此封装了`cn.hutool.core.lang.ObjectId`，快捷创建方法为：

```
//生成类似：5b9e306a4df4f8c54a39fb0c
String id = ObjectId.next();

//方法2：从Hutool-4.1.14开始提供
String id2 = IdUtil.objectId();
```

**3、Snowflake**

分布式系统中，有一些需要使用全局唯一ID的场景，有些时候我们希望能使用一种简单一些的ID，并且希望ID能够按照时间有序生成。Twitter的Snowflake 算法就是这种生成器。

使用方法如下：

```
//参数1为终端ID
//参数2为数据中心ID
Snowflake snowflake = IdUtil.createSnowflake(1, 1);
long id = snowflake.nextId();
```

### ZipUtil

在Java中，对文件、文件夹打包，压缩是一件比较繁琐的事情，我们常常引入Zip4j进行此类操作。但是很多时候，JDK中的zip包就可满足我们大部分需求。ZipUtil就是针对java.util.zip做工具化封装，使压缩解压操作可以一个方法搞定，并且自动处理文件和目录的问题，不再需要用户判断，压缩后的文件也会自动创建文件，自动创建父目录，大大简化的压缩解压的复杂度。

**1、Zip**

```
@Test
    public void zipUtilTest(){
        ZipUtil.zip("C:/Users/Administrator/Desktop/test.txt");
    }
```

观察桌面：
![在这里插入图片描述](https://cdnimg.copyfuture.com/imagesLocal/201912/16/20191216152012653a56j2ra24dujor8_0.png)
压缩成功。

当然了，你也可以指定压缩后的压缩包存放位置，将路径作为第二个参数传入zip方法即可。

多文件或目录压缩。可以选择多个文件或目录一起打成zip包 ：

```
@Test
    public void zipUtilTest() {
        ZipUtil.zip(FileUtil.file("d:/bbb/ccc.zip"), false,
                FileUtil.file("d:/test1/file1.txt"),
                FileUtil.file("d:/test1/file2.txt"),
                FileUtil.file("d:/test2/file1.txt"),
                FileUtil.file("d:/test2/file2.txt")
        );

    }
```

解压操作和压缩一样，不重复讲解，解压方法为unzip。

**2、GZip**

Gzip是网页传输中广泛使用的压缩方式，Hutool同样提供其工具方法简化其过程。

`ZipUtil.gzip` 压缩，可压缩字符串，也可压缩文件 `ZipUtil.unGzip` 解压Gzip文件

**3、Zlib**

`ZipUtil.zlib` 压缩，可压缩字符串，也可压缩文件 `ZipUtil.unZlib` 解压zlib文件

### IdCardUtil

在日常开发中，我们对身份证的验证主要是正则方式（位数，数字范围等），但是中国身份证，尤其18位身份证每一位都有严格规定，并且最后一位为校验位。而我们在实际应用中，针对身份证的验证理应严格至此。于是`IdcardUtil`应运而生。

`IdcardUtil`现在支持大陆15位、18位身份证，港澳台10位身份证。

工具中主要的方法包括：

1. `isValidCard` 验证身份证是否合法
2. `convert15To18` 身份证15位转18位
3. `getBirthByIdCard` 获取生日
4. `getAgeByIdCard` 获取年龄
5. `getYearByIdCard` 获取生日年
6. `getMonthByIdCard` 获取生日月
7. `getDayByIdCard` 获取生日天
8. `getGenderByIdCard` 获取性别
9. `getProvinceByIdCard` 获取省份

**使用**

```
String ID_18 = "321083197812162119";
String ID_15 = "150102880730303";

//是否有效
boolean valid = IdcardUtil.isValidCard(ID_18);
boolean valid15 = IdcardUtil.isValidCard(ID_15);

//转换
String convert15To18 = IdcardUtil.convert15To18(ID_15);
Assert.assertEquals(convert15To18, "150102198807303035");

//年龄
DateTime date = DateUtil.parse("2017-04-10");

int age = IdcardUtil.getAgeByIdCard(ID_18, date);
Assert.assertEquals(age, 38);

int age2 = IdcardUtil.getAgeByIdCard(ID_15, date);
Assert.assertEquals(age2, 28);

//生日
String birth = IdcardUtil.getBirthByIdCard(ID_18);
Assert.assertEquals(birth, "19781216");

String birth2 = IdcardUtil.getBirthByIdCard(ID_15);
Assert.assertEquals(birth2, "19880730");

//省份
String province = IdcardUtil.getProvinceByIdCard(ID_18);
Assert.assertEquals(province, "江苏");

String province2 = IdcardUtil.getProvinceByIdCard(ID_15);
Assert.assertEquals(province2, "内蒙古");
```

### 最后

本篇文章也只是例举了HuTool中的部分工具类，实际上，HuTool是一个非常完美的项目，实现了很多Java操作，使得我们在操作一些比较复杂的内容时也能轻松应对。

关于HuTool的更多内容，大家可以自己去了解一下。





> 版权声明
>
> 本文为[~wangweijun]所创，转载请带上原文链接，感谢
>
> https://blog.csdn.net/qq_42453117/article/details/103561700