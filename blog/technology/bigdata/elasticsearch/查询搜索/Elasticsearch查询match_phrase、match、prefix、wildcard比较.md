# Elasticsearch查询match_phrase、match、prefix、wildcard比较

2019-07-11

### match

```
GET /my_index/address/_search
{
    query: {match:"hello world"}
}
```

句子中包含`hello`或`world`的都会被搜索出，比如下面的句子都会被搜索到：

```
1.hello tom, do you know me
2.see the world
```



### match_phrase

```
GET /my_index/address/_search
{
    query: {match_phrase:"hello world"}
}
```

也就是说`hello world` 必须相邻才能被搜索出来，比如下面的句子：

```
1.Hello World tom, do you know me // 能搜到
2.see the world // 搜不到
3.Hello tom // 搜不到
```



### match_phrase slop

```
GET /my_index/address/_search
{
    query: {match_phrase:{content:"hello world", slop: 2}}
}
```

可以通过指定slot来控制移动词数。这里中间间隔的词数<2才能搜到。对于下面的例子：

```
1.hello world // 能搜到
2.hello es world // 能搜索到
3.hello tom es world // 不能搜到
4.hello lity do my world // 搜不到 3>2
```

#### match_phrase原理

match_phrase执行过程：
１.如match搜索一样进行分词，
２.对分词后的单词到field中去进行搜索(多个term匹配)。这一步返回每个单词对应的doc，并返回这些单词在对应的doc中的位置，
３.对返回的doc进行第一步的筛选，找到每个单词都在同一个field的doc。
４.对第３步进行筛选后的doc进行再一次的筛选，选回位置符合要求的doc。比如，对于match_phrase，就是找到后一个单词的位置比前一个单词的位置大１。或者移动次数<slot的文档。
５．proximity match（使用slot）原理一样，只是第四位对位置进行筛选时的方法不同。

比如要搜索“hello world”

1. 分词为 hello 和 world
2. 分别对term hello和world去搜索。返回两者匹配到的文档。
3. 第一次筛选，取两个的交集。
4. 继续筛选，对于match_phrase，就是找到后一个单词world的位置比前一个单词hello的位置大１的文档

### prefix

前缀搜索
它会对分词后的term进行前缀搜索。

- 它不会分析要搜索字符串，传入的前缀就是想要查找的前缀
- 默认状态下，前缀查询不做相关度分数计算，它只是将所有匹配的文档返回，然后赋予所有相关分数值为1。它的行为更像是一个过滤器而不是查询。两者实际的区别就是过滤器是可以被缓存的，而前缀查询不行。
- 只能找到反向索引中存在的术语

prefix的原理：
需要遍历所有倒排索引，并比较每个term是否已所指定的前缀开头。
比如：

```
Term:          Doc IDs:
-------------------------
"SW50BE"    |  5
"W1F7HW"    |  3
"W1V3DG"    |  1
"W2F8HW"    |  2
"WC1N1LZ"   |  4
-------------------------

GET /my_index/address/_search
{
    "query": {
        "prefix": {
            "postcode": "W1"
        }
    }
}
```

#### prefix原理

prefix搜索过程：
为了支持前缀匹配，查询会做以下事情：

1. 扫描术语列表并查找到第一个以 W1 开始的术语。
2. 搜集关联的ID
3. 移动到下一个术语
4. 如果这个术语也是以 W1 开头，查询跳回到第二步再重复执行，直到下一个术语不以 W1 为止。

如果以w1开头的term很多，那么会有严重的性能问题。但是如果term比较小集合，可以放心使用。

### wildcard

模糊查询

- 工作原理和prefix相同，只不过它在1不是只比较开头，它能支持更为复杂的匹配模式。

- 它使用标准的 shell 模糊查询：? 匹配任意字符，* 匹配0个或多个字符。

  ```
  GET /my_index/address/_search
  {
      "query": {
          "regexp": {
              "postcode": "W[0-9].+" #1
          }
      }
  }
  ```

这也意味着我们需要注意与前缀查询中相同的性能问题，执行这些查询可能会消耗非常多的资源，所以我们需要避免使用左模糊这样的模式匹配（如，*foo 或 .foo* 这样的正则式）

> 注意：
> prefix、wildcard 和 regrep 查询是基于术语操作的，如果我们用它们来查询分析过的字段（analyzed field），他们会检查字段里面的每个术语，而不是将字段作为整体进行处理。

### match_phrase_prefix

实时模糊搜索
这种查询的行为与 match_phrase 查询一致，但是它将查询字符串的最后一个词作为前缀(prefix)使用。

比如：

```
{
    "match_phrase_prefix" : {
        "brand": "johnnie walker bl"
    }
}
```



下面的句子可能被搜索到：

- johnnie walker blll
- johnnie walker bl33
- johnnie walker blyu

查询步骤：

- `johnnie walker`
- 跟着 一个以 bl 开始的词(prefix)

与 match_phrase 一样，它也可以接受 slop 参数让相对词序位置不那么严格：

```
{
    "match_phrase_prefix" : {
        "brand" : {
            "query": "walker johnnie bl", #1
            "slop":  10
        }
    }
}
```



下面的句子可能被搜索到：

- johnnie ee walker blll
- johnnie aa walker bl33
- johnnie cc gg walker blyu

我们可以通过设置 max_expansions 参数来限制前缀扩展的影响，一个合理的值是可能是50：

```
{
    "match_phrase_prefix" : {
        "brand" : {
            "query":"johnnie walker bl",
            "max_expansions": 50
        }
    }
}
```

参数max_expansions控制着可以与前缀匹配的术语的数量

> 另一个即时搜索的方法是，使用 `Ngram部分匹配`, 这种方法会增加索引的开销，但是会加快查询速度。具体可以自行查阅。





[【ES】match_phrase、match、prefix、wildcard比较 | 易天行 (hao5743.github.io)](https://hao5743.github.io/2019/07/11/2019-07-16/)