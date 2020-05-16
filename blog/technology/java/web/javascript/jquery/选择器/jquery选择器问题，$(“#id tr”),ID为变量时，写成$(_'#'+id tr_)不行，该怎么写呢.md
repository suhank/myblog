# jquery选择器问题，\$(“#id tr”),ID为变量时，写成$("'#'+id tr")不行，该怎么写呢

这个你需要理解他的意思，比如
var id="key";
你想得到$("#key tr")，那么就必须拼接字符串。
var seletor="#"+id+" td";$(seletor)。这样就得到了。
理解了之后所以可以直接这样写：$("#" + id + " td");
希望可以帮到你。

追问

```
那如果想找到ID为变量的DIV的子节点tr,tr的class为user，改如何写呢
```

追答

```
$("#" + id + " .user");这样就行了。找到的这个id下面的所有class=user的节点。
```





https://zhidao.baidu.com/question/539820728.html