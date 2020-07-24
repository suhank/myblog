# p标签中文字自动换行

遇见一个问题p标签中的字符串太长不换行（但中文字会换行，字母数字不换行）

p标签中添加样式style="word-break:break-all;"

```Html
<p style="word-break:break-all;"></p>
```

或

css样式

```css
p{
  word-wrap: break-word;
  word-break: break-all; 
}
```





https://blog.csdn.net/JYL15732624861/article/details/79387619