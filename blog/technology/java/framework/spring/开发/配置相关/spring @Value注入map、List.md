[TOC]



# spring @Value注入map、List

2018年11月05日 15:19:23 [铁铲](https://me.csdn.net/u012903926) 阅读数 3726

## java代码

```java
@Value("#{'${list}'.split(',')}")
private List<String> list;
 
@Value("#{${maps}}")  
private Map<String,String> maps;

@Value("#{${redirectUrl}}")
private Map<String,String> redirectUrl;
 
```

## yaml配置文件

```yaml
list: topic1,topic2,topic3
maps: "{key1: 'value1', key2: 'value2'}"
redirectUrl: "{sso_client_id: '${id}',sso_client_secret: '${secret}',redirect_url: '${client.main.url.default}'}"
```

注意上面的map解析中，一定要用"“把map所对应的value包起来，要不然解析会失败，导致不能转成 Map<String,String>
因为yaml语法中如果一个值以 “{” 开头, YAML 将认为它是一个字典, 所以我们必须引用它必须用”"
<http://www.ansible.com.cn/docs/YAMLSyntax.html>

yaml写法注意：
字符串默认不用加上单引号或者双引号
“”：双引号；不会转义字符串里面的特殊字符；特殊字符会作为本身想表示的意思
name: “zhangsan \n lisi”：输出；zhangsan 换行 lisi
‘’：单引号；会转义特殊字符，特殊字符最终只是一个普通的字符串数据
name: ‘zhangsan \n lisi’：输出；zhangsan \n lisi

## properties配置文件

```properties
jdbc.driverClass=mytesthahahyxy
list=topic1,topic2,topic3
maps={key1: 'value1', key2: 'value2'}
redirectUrl={sso_client_id: '${id}',sso_client_secret: '${secret}',redirect_url: '${client.main.url.default}'}
id=我是id
secret=我是secret
client.main.url.default:=我是地方是非得失
```

 





<https://blog.csdn.net/u012903926/article/details/83750230>