# List&lt;T&gt;、List&lt;?&gt;、List&lt;Object&gt;区别

linkhome 2020-05-05 10:53:20

1、都可以存储所有对象

2、List<T> ：集合中元素为T类型，运行时决定，可以进行诸如add、remove等操作

List<?>：任意类型，只读类型的，不能增加、修改操作，无法增加、修 改元素，但是却可以删除元素，比如执行remove、clear等方法，那是因为它的删除动作与泛型类型无关

List<Object>：表示List集合中的所有元素为Object类型，可以读写操作，但是写入操作时，需要进行装箱拆箱操作

使用的顺序应该是首选List<T>,次之List<?>,最后选择List<Object>





https://www.toutiao.com/a6823187771532771853/?tt_from=android_share&utm_campaign=client_share&timestamp=1589331081&app=news_article&utm_medium=toutiao_android&use_new_style=0&req_id=202005130851200100140510811A1CBB01&group_id=6823187771532771853