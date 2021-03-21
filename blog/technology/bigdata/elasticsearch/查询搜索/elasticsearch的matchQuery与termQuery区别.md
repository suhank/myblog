# elasticsearch的matchQuery与termQuery区别

matchQuery：会将搜索词分词，再与目标查询字段进行匹配，若分词中的任意一个词与目标字段匹配上，则可查询到。

termQuery：不会对搜索词进行分词处理，而是作为一个整体与目标字段进行匹配，若完全匹配，则可查询到。 **前提是 mapping 字段类型是 keyword**

转载于:https://www.cnblogs.com/zhi-leaf/p/6198260.html