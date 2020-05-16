# jackson 入门教程

下面演示使用 Jackson 实现 Java 对象和 JSON 的相互转换。

## 1. 快速参考

#### 1.1 Java object to JSON, `writeValue(...)`

```java
ObjectMapper mapper = new ObjectMapper();
Staff obj = new Staff();

//Object to JSON in file
mapper.writeValue(new File("c:\\file.json"), obj);

//Object to JSON in String
String jsonInString = mapper.writeValueAsString(obj);
```

#### 1.2 JSON to Java object `readValue(...)`

```
ObjectMapper mapper = new ObjectMapper();
String jsonInString = "{'name' : 'mjw'}";

//JSON from file to Object
Staff obj = mapper.readValue(new File("c:\\file.json"), Staff.class);

//JSON from URL to Object
Staff obj = mapper.readValue(new URL("http://www.jianshu.com/u/c38e94dcec65"), Staff.class);

//JSON from String to Object
Staff obj = mapper.readValue(jsonInString, Staff.class);
```

## 2. POJO 和依赖

依赖

```
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-core</artifactId>
    <version>2.9.6</version>
</dependency>

```

Plain Old Java Object, 表示常规的Java对象，没有扩展特定的类，或者实现特定的接口，或者说不受特定框架扩展的影响。

例如，如果想从 JMS 接收信息，则需要实现 MessageListener 接口：

```
public class ExampleListener implements MessageListener {
  public void onMessage(Message message){
    ...
  }
}
```

这个类和JMS的 MessageListener 接口绑定，因此难以迁移到其他的信息处理框架，它不是POJO。

## 3. Java 和 JSON 的相互转换

下面通过一个实例，说明如何使用 Jackson 实现JSON和Java对象相互转换。
Album 类，包含一个字段：

```
class Album {
    private String title;
    public Album(String title) {
        this.title = title;
    }
    public String getTitle() {
        return title;
    }
}
```

### 3.1 使用 ObjectMapper 进行转换

Jackson 默认使用 BeanSerializer 序列化POJO，要求对应的字段为 public，或者有对应的 getter 方法。

##### 单字段序列化

```
Album album = new Album("Kind Of Blue");
ObjectMapper mapper = new ObjectMapper();
mapper.writeValue(System.out, album);
```

输出：

> {"title":"Kind Of Blue"}

##### 数组序列化

现在继续向 Album 添加一个数组字段及对应的 getter 和 setter 方法：

```
private String[] links;
public String[] getLinks(){
    return links;
}
public void setLinks(String[] links){
    this.links = links;
}
```

修改 main 方法：

```
Album album = new Album("Kind Of Blue");
album.setLinks(new String[]{"link1", "link2"});
ObjectMapper mapper = new ObjectMapper();
mapper.writeValue(System.out, album);
```

输出：

> {"title":"Kind Of Blue","links":["link1","link2"]}

##### List 序列化

向 Album 添加 List 字段：

```
private List<String> songs;
public List<String> getSongs(){
    return songs;
}
public void setSongs(List<String> songs){
    this.songs = songs;
}
```

修改 main 方法：

```
Album album = new Album("Kind Of Blue");
album.setLinks(new String[]{"link1", "link2"});

List<String> songs = new ArrayList<>();
songs.add("So what");
songs.add("Flamenco Sketches");
songs.add("Freddie Freeloader");

album.setSongs(songs);

ObjectMapper mapper = new ObjectMapper();
mapper.writeValue(System.out, album);
```

输出：

> {"title":"Kind Of Blue","links":["link1","link2"],"songs":["So what","Flamenco Sketches","Freddie Freeloader"]}

从输出结构可以看到，List和数组的输出格式是一样的。

##### Java 对象序列化

Java 对象，序列化后在JSON中被 {} 括起来。
定义Artist 类：

```
public class Artist{
    public String name;
    public Date birthDate;
}
```

在Album 中添加对应的字段，并在 main 中设置其值：

```
Artist artist = new Artist();
artist.name = "Miles Davis";
SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
artist.birthDate = format.parse("26-05-1926");
album.setArtist(artist);
```

输出：

> {"title":"Kind Of Blue","links":["link1","link2"],"songs":["So what","Flamenco Sketches","Freddie Freeloader"],"artist":{"name":"Miles Davis","birthDate":-1376035200000}}

##### 格式化输出

配置 ObjectMapper ，可以让输出更好看一些：

```
mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
```

此时的JSON输出如下：

```
{
  "title" : "Kind Of Blue",
  "links" : [ "link1", "link2" ],
  "songs" : [ "So what", "Flamenco Sketches", "Freddie Freeloader" ],
  "artist" : {
    "name" : "Miles Davis",
    "birthDate" : -1376035200000
  }
}
```

##### Map 序列化

向 Album 中添加如下内容：

```
private Map<String, String> musicians = new HashMap<>();
public Map<String, String> getMusicians(){
    return Collections.unmodifiableMap(musicians);
}
public void addMusician(String key, String value){
    musicians.put(key, value);
}
```

在 main 中添加如下内容：

```
album.addMusician("Miles Davis", "Trumpet, Band leader");
album.addMusician("Julian Adderley", "Alto Saxophone");
album.addMusician("Paul Chambers", "double bass");
```

输出如下：

```
{
  "title" : "Kind Of Blue",
  "links" : [ "link1", "link2" ],
  "songs" : [ "So what", "Flamenco Sketches", "Freddie Freeloader" ],
  "artist" : {
    "name" : "Miles Davis",
    "birthDate" : -1376035200000
  },
  "musicians" : {
    "Miles Davis" : "Trumpet, Band leader",
    "Paul Chambers" : "double bass",
    "Julian Adderley" : "Alto Saxophone"
  }
}
```

##### 其他

设置输出时间格式

```
SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy");
mapper.setDateFormat(outputFormat);
```

让Map按序输出

```
mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
```

此时输出：

```
{
  "title" : "Kind Of Blue",
  "links" : [ "link1", "link2" ],
  "songs" : [ "So what", "Flamenco Sketches", "Freddie Freeloader" ],
  "artist" : {
    "name" : "Miles Davis",
    "birthDate" : "26 May 1926"
  },
  "musicians" : {
    "Julian Adderley" : "Alto Saxophone",
    "Miles Davis" : "Trumpet, Band leader",
    "Paul Chambers" : "double bass"
  }
}
```

### 3.2 使用 Tree Model 进行转换

我们继续使用上面的例子，来演示 Tree Model 的使用。使用 Tree 进行输出包含如下几个步骤：

- 创建 JsonNodeFactory，用于创建 node。
- 使用JsonFactory创建 JsonGenerator，并指定输出方法。
- 创建ObjectMapper，它使用 JsonGenerator 和树的根节点输出到JSON。

如下所示：

```
public class SerializationExampleTreeModel{
    public static void main(String[] args) throws IOException{
        JsonNodeFactory factory = new JsonNodeFactory(false);

        JsonFactory jsonFactory = new JsonFactory();
        JsonGenerator generator = jsonFactory.createGenerator(System.out);

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode album = factory.objectNode();
        mapper.writeTree(generator, album);
    }
}
```

由于没有添加任何实质性内容，所以输出为：

> {}

开始添加内容：

```
album.put("Album-Title", "Kind Of Blue");
```

JSON:

```
{"Album-Title":"Kind Of Blue"}
```

添加数组：

```
ArrayNode links = factory.arrayNode();
links.add("link1").add("link2");
album.set("links", links);
```

JSON:

```
{"Album-Title":"Kind Of Blue","links":["link1","link2"]}
```

添加对象：

```
ObjectNode artist = factory.objectNode();
artist.put("Artist-Name", "Miles Davis");
artist.put("birthDate", "26 May 1926");
album.set("artist", artist);
```

JSON:

```
{"Album-Title":"Kind Of Blue","links":["link1","link2"],"artist":{"Artist-Name":"Miles Davis","birthDate":"26 May 1926"}}
```

添加 musicians:

```
ObjectNode musicians = factory.objectNode();
musicians.put("Julian Adderley", "Alto Saxophone");
musicians.put("Miles Davis", "Trumpet, Band leader");
album.set("musicians", musicians);
```

JSON:

```
{"Album-Title":"Kind Of Blue","links":["link1","link2"],"artist":{"Artist-Name":"Miles Davis","birthDate":"26 May 1926"},"musicians":{"Julian Adderley":"Alto Saxophone","Miles Davis":"Trumpet, Band leader"}}
```





https://www.jianshu.com/p/547b2c7b6748