# elasticSearch增加Mapping及Mapping字段

2018-06-08 10:53:22

## **2.3.x**

**创建Mapping**

```java
POST  /new_test/record/_mapping/
{
	"record": {
		"properties": {
			"analyze_date": {
				"format": "yyyy-MM-dd HH:mm:ss",
				"type": "date"
			},
			"upload_status": {
				"index": "not_analyzed",
				"type": "string"
			},
			"event_type": {
				"index": "not_analyzed",
				"type": "string"
			}
		}
	}
}
```

**添加Mapping字段：**

```java
PUT /test_index/_mapping/test_type    mapping增加字段
 
{
  "properties": {
    "proRemark": {
      "type": "string",
      "index": "not_analyzed"
    }
  }
}
```

 

## **5.0.x**

**添加Mapping字段：**

POST

```java
test_index/test_type/_mapping 
```

删除type数据：

```
POST edemo/test/_delete_by_query?conflicts=proceed
{
  "query": {
    "match_all": {}
  }
```

 

https://blog.csdn.net/huang_550/article/details/80619750