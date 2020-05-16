## Jackson用法?这一篇就够了

 发表于 2017-10-31 |  分类于 [Jackson ](https://www.carryingcoder.com/categories/Jackson/)| *总阅读量* 255次

[![img](https://www.carryingcoder.com/2017/10/31/Jackson%E7%94%A8%E6%B3%95-%E8%BF%99%E4%B8%80%E7%AF%87%E5%B0%B1%E5%A4%9F%E4%BA%86/json.png)](https://www.carryingcoder.com/2017/10/31/Jackson%E7%94%A8%E6%B3%95-%E8%BF%99%E4%B8%80%E7%AF%87%E5%B0%B1%E5%A4%9F%E4%BA%86/json.png)

### 摘要

目前比较流行，还在用的库有：json-lib，jackson，fastjson，gson。

这几个框架的性能对比不再说明，看一篇文章[这么快老外为啥还是热衷 jackson?](https://www.zhihu.com/question/44199956)。

讲真，我之前用的也是fastjson，只是被口号”快”字蒙蔽，并没有看源码，其实不然，”快”不能解决一切问题。

接下来咱们一起看看老外热衷的这款。



###Jackson常用注解

#### 读写注解

##### @JsonIgnore

作用在字段或者方法，在序列化/反序列化时，忽略一个字段

##### @JsonProperty

作用在字段或者方法，在序列化/反序列化时，指定属性名

##### @JsonIgnoreProperties

作用在类上，可以指定多个字段忽略

##### @JsonIgnoreType

作用在类上，忽略这个类，不会被序列化和反序列化

##### @JsonAutoDetect

作用在类上，自动坚持是否序列化字段，根据其属性的修饰符属性，

Visibility值：

```
public static enum Visibility {
       ANY, 
       NON_PRIVATE,
       PROTECTED_AND_PUBLIC,
       PUBLIC_ONLY,
       NONE,
       DEFAULT;
}
```

#### 读注解

##### @JsonAnySetter

如果在JSON数据中有Object没有的字段属性，怎么办？可以用这个注解，放入到map中

```
public class Bag {

    private Map<String, Object> properties = new HashMap<>();

    @JsonAnySetter
    public void set(String fieldName, Object value){
        this.properties.put(fieldName, value);
    }

    public Object get(String fieldName){
        return this.properties.get(fieldName);
    }
}
```

Jackson会调用这个set方法把没有识别的字段放入map中。

##### @JsonCreator

作用于方法，通常用来标注构造方法或静态工厂方法上，使用该方法来构建实例，默认的是使用无参的构造方法，通常是和@JsonProperty或@JacksonInject配合使用

```
@Test
public void jsonCreator() throws Exception {
	ObjectMapper objectMapper = new ObjectMapper();
	String jsonStr = "{\"full_name\":\"myName\",\"age\":12}";
	TestPOJO testPOJO = objectMapper.readValue(jsonStr,TestPOJO.class);
	Assert.assertEquals("myName",testPOJO.getName());
	Assert.assertEquals(12, testPOJO.getAge());
}

public static class TestPOJO{
	private String name;
	private int age;

	@JsonCreator
	public TestPOJO(@JsonProperty("full_name") String name,@JsonProperty("age") int age){
		this.name = name;
		this.age = age;
	}
	public String getName() {
		return name;
	}
	public int getAge() {
		return age;
	}
}
```

##### @JacksonInject

作用于属性、方法、构造参数上，被用来反序列化时标记已经被注入的属性，注入是需要手动注入的。

```
public class PersonInject {

    public long   id   = 0;
    public String name = null;

    @JacksonInject
    public String source = null;

}
InjectableValues inject = new InjectableValues.Std().addValue(String.class, "jenkov.com");
PersonInject personInject = new ObjectMapper().reader(inject)
                        .forType(PersonInject.class)
                        .readValue(new File("data/person.json"));
```

##### @JsonDeserialize

作用于方法和字段上，通过 using(JsonSerializer)和using(JsonDeserializer)来指定序列化和反序列化的实现，通常我们在需要自定义序列化和反序列化时会用到，比如下面的例子中的日期转换，BigDecimal等。

自定义实现继承类JsonSerializer。

#### 写注解

##### @JsonInclude

可以过滤属性的值，null，empty不进行序列化。

##### @JsonAnyGetter

##### @JsonPropertyOrder

作用在类上，被用来指明当序列化时需要对属性做排序，它有2个属性

一个是alphabetic：布尔类型，表示是否采用字母拼音顺序排序，默认是为false，即不排序

##### @JsonRawValue

写入原始值，不做处理

```
{"personId":0,"address":"$#"}
如下：
{"personId":0,"address":$#}
```

##### @JsonValue

序列化时，调用方法序列化

##### @JsonSerialize

### Jackson核心操作类ObjectMapper

#### 线程安全的ObjectMapper

`ObjectMapper`类提供了很多方法操作JSON，这个类**是线程安全**的，推荐是用单例，注入方式，保证对象能够复用。

还有其他类：JsonParser，JsonGenerator等，用这些类可以很容易的从String，File，Streams，URL等读取和写入JSON。

**注意**：在JSON序列化和反序列化的时候，会调用对象的getter/setter方法。所以需要在处理对象上要加上getter/setter，否则会出错。

#### Object对象转JSON

三个方法:

- writeValue()
- writeValueAsBytes()
- writeValueAsString()

```
/**
 * @author Liu Hailin
 * @create 2017-11-01 下午7:14
 **/
public class JSONTest {

    private ObjectMapper mapper;

    private User user;

    @Before
    public void setUp() {
        mapper = new ObjectMapper();
        user = User.builder().id( 1 ).name( "liuhailin" ).money( 100.001d ).createDate( new Date() ).build();
    }
  
    @Test
    public void userToJSON() throws JsonProcessingException {
        String userJson = mapper.writeValueAsString( user );
        System.out.println( userJson );
    }
}
```

结果

```
{"id":1,"name":"liuhailin","createDate":1509535233281,"money":100.001}
Process finished with exit code 0
```

#### Object对象转JSON并格式化

```
@Test
public void userToJSONPretty() throws JsonProcessingException {
  String userJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString( user );
  System.out.println( userJson );
}
```

结果

```
{
  "id" : 1,
  "name" : "liuhailin",
  "createDate" : 1509537316534,
  "money" : 100.001
}

Process finished with exit code 0
```

会格式化输出。

#### JSON转Object对象

```
@Test
public void jsonToUser() throws IOException {
  String str = "{\n"
    + "  \"id\" : 1,\n"
    + "  \"name\" : \"liuhailin\",\n"
    + "  \"createDate\" : 1509537316534,\n"
    + "  \"money\" : 100.001\n"
    + "}";
  User user = mapper.readValue( str, User.class );
  System.out.println(user);
}
```

结果(User实现了toString)

```
User(id=1, name=liuhailin, createDate=Wed Nov 01 19:55:16 CST 2017, money=100.001)
Process finished with exit code 0
```

#### 用Reader反序列化JSON，转Object对象

jackson可以用抽象类**Reader**类**BufferedReader**，**CharArrayReader**，**FilterReader**，**InputStreamReader**，**PipedReader**，**StringReader**，反序列化Json数据。

```
@Test
public void jsonToUserByReader() throws IOException {
  String str = "{\n"
    + "  \"id\" : 1,\n"
    + "  \"name\" : \"liuhailin\",\n"
    + "  \"createDate\" : 1509537316534,\n"
    + "  \"money\" : 100.001\n"
    + "}";

  Reader reader = new StringReader( str );
  User user = mapper.readValue( reader, User.class );
  System.out.println( user );
}
```

代码中我们用**StringReader**，读取一段字符串。

#### JSON转HashMap

```
@Test
public void jsonToUserMap() throws IOException {
  String str = "{\n"
    + "  \"id\" : 1,\n"
    + "  \"name\" : \"liuhailin\",\n"
    + "  \"createDate\" : 1509537316534,\n"
    + "  \"money\" : 100.001\n"
    + "}";

  HashMap userMap = mapper.readValue( str, HashMap.class );
  System.out.println( userMap.get( "id" ) );
  System.out.println( userMap.get( "name" ) );
  System.out.println( userMap.get( "createDate" ) );
  System.out.println( userMap.get( "money" ) );
}
```

结果

```
1
liuhailin
1509537316534
100.001

Process finished with exit code 0
```

#### 从文件/流中读取 JSON

```
@Test
public void jsonToUserByReader() throws IOException {
  String str = "{\n"
    + "  \"id\" : 1,\n"
    + "  \"name\" : \"liuhailin\",\n"
    + "  \"createDate\" : 1509537316534,\n"
    + "  \"money\" : 100.001\n"
    + "}";

  Reader reader = new StringReader( str );
  User user = mapper.readValue( reader, User.class );
  System.out.println( user );
}
@Test
public void jsonToUserMap() throws IOException {
  String str = "{\n"
    + "  \"id\" : 1,\n"
    + "  \"name\" : \"liuhailin\",\n"
    + "  \"createDate\" : 1509537316534,\n"
    + "  \"money\" : 100.001\n"
    + "}";

  HashMap<String,String> userMap = mapper.readValue( str, HashMap.class );
  System.out.println( userMap.get( "id" ) );
  System.out.println( userMap.get( "name" ) );
  System.out.println( userMap.get( "createDate" ) );
  System.out.println( userMap.get( "money" ) );
}
@Test
public void jsonToUserFromInputStreamReader() throws IOException {
  File userFile = new File( "user.json" );
  InputStream inputStream = new FileInputStream( userFile );
  InputStreamReader inputStreamReader = new InputStreamReader( inputStream, Charset.forName( "utf8" ) );
  User user = mapper.readValue( inputStreamReader, User.class );
  System.out.println( user );
}
```

**InputStreamReader** 可以设定编码。

#### 从BtyeArray中读取JSON

```
@Test
public void jsonToUserFromByteArray() throws IOException {
  String str = "{\n"
    + "  \"id\" : 1,\n"
    + "  \"name\" : \"liuhailin\",\n"
    + "  \"createDate\" : 1509537316534,\n"
    + "  \"money\" : 100.001\n"
    + "}";
  byte[] array = str.getBytes();

  User user = mapper.readValue( array, User.class );
  System.out.println( user );
}
```

### Jackson JsonParser

Jackson JsonParser类是一个低级JSON解析器。 它与Java的StAX解析器类似，除了JsonParser解析JSON而不是XML。

JsonParser比ObjectMapper更高效，但是使用难度比ObjectMapper大。

```
@Test
public void testJsonParser() throws IOException {
  String str = "{\n"
    + "  \"id\" : 1,\n"
    + "  \"name\" : \"liuhailin\",\n"
    + "  \"createDate\" : 1509537316534,\n"
    + "  \"money\" : 100.001\n"
    + "}";
  JsonFactory factory = new JsonFactory(  );
  JsonParser parser = factory.createParser( str );
  while (!parser.isClosed()){
    JsonToken token = parser.nextToken();

    if(JsonToken.FIELD_NAME.equals( token )){
      String fieldName = parser.getCurrentName();
      parser.nextToken();
      System.out.println(fieldName+":"+parser.getValueAsString());

    }
  }
}
```

如果token是JsonToken.FIELD_NAM，getCurrentName()返回当前属性的名称。nextToken会把parser指向属性的value。

结果

```
START_OBJECT
FIELD_NAME
id:1
FIELD_NAME
name:liuhailin
FIELD_NAME
createDate:1509537316534
FIELD_NAME
money:100.001
END_OBJECT
null

Process finished with exit code 0
```

### Jackson JsonGenerator

JsonGenerator用于从Java对象生成JSON。

```
@Test
public void testJsonGenerator() throws IOException {

  JsonFactory factory = new JsonFactory(  );

  PrintStream stream = System.out;

  JsonGenerator generator = factory.createGenerator( stream, JsonEncoding.UTF8 );
  generator.writeStartObject();
  generator.writeNumberField( "id",2 );
  generator.writeStringField( "name","liuhailin" );
  generator.writeNumberField( "create",System.currentTimeMillis() );
  generator.writeNumberField( "money",1000.01d );
  generator.writeEndObject();

  stream.flush();
  generator.close();
}
```

要关闭 JsonGenerato，调用close()方法，同时还会关闭输出的文件或者输出流。