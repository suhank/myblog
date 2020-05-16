[TOC]



# thymeleaf快速入门教程

## thymeleaf介绍

简单说， Thymeleaf 是一个跟 Velocity、FreeMarker 类似的模板引擎，它可以完全替代 JSP 。相较与其他的模板引擎，它有如下三个极吸引人的特点：

- 1.Thymeleaf 在有网络和无网络的环境下皆可运行，即它可以让美工在浏览器查看页面的静态效果，也可以让程序员在服务器查看带数据的动态页面效果。这是由于它支持 html 原型，然后在 html 标签里增加额外的属性来达到模板+数据的展示方式。浏览器解释 html 时会忽略未定义的标签属性，所以 thymeleaf 的模板可以静态地运行；当有数据返回到页面时，Thymeleaf 标签会动态地替换掉静态内容，使页面动态显示。
- 2.Thymeleaf 开箱即用的特性。它提供标准和spring标准两种方言，可以直接套用模板实现JSTL、 OGNL表达式效果，避免每天套模板、该jstl、改标签的困扰。同时开发人员也可以扩展和创建自定义的方言。
- 3.Thymeleaf 提供spring标准方言和一个与 SpringMVC 完美集成的可选模块，可以快速的实现表单绑定、属性编辑器、国际化等功能。

[Thymeleaf](http://www.thymeleaf.org/)是一款用于渲染XML/XHTML/HTML5内容的模板引擎。类似JSP，Velocity，FreeMaker等，它也可以轻易的与Spring MVC等Web框架进行集成作为Web应用的模板引擎。与其它模板引擎相比，Thymeleaf最大的特点是能够直接在浏览器中打开并正确显示模板页面，而不需要启动整个Web应用。





本教程涵盖了常见的前端操作，比如，判断，循环，引入模板，常用函数（日期格式化，字符串操作）下拉，js和css中使用，基本可以应对一般场景。

## 怎么使用？

前端html页面标签中引入如下：

```
<html xmlns:th="http://www.thymeleaf.org">1
```

## 表达式

- 简单表达式 

  - 可用值表达式(后台设置): ${…}
  - 所有可用值表达式: *{…}

  > 比如*{name} 从可用值中查找name，如果有上下文，比如上层是object，则查object中的name属性。

- 消息表达式: #{…}

  > 国际化时使用，也可以使用内置的对象，比如date格式化数据

- 链接表达式: @{…} 
  用来配合link src href使用的语法

  - 它的写法与th：src一样 一般写法为th:href="@{值}"

    如果是需要从model中取值的话，写法为

    th:href="@{${model中的name值}}"。

    有的时候我们不止需要从model中进行取值，还需写字符串与model中的值进行拼接，写法为

    th:href="@{'字符串'+${model中的nam值}}"。

    例子：

    ```
     <a th:href="@{${logoutUrl}}">注销</a>
    ```

    

- 片段表达式: ~{…} 
  用来引入公共部分代码片段，并进行传值操作使用的语法。

文字

- 文本： ‘one text’,’another text’,…
- 数字： 1,2,1.2，…
- 布尔: true,false
- 空值：null
- 单词: something,main,name,…

文本操作

- 链接：+

- 替换：|The name is ${name}| 

  ```html
   <a href="" th:href="@{|/name/${test.size()}|}">链接地址：</a> 
  //渲染后的结果 
  <a href="/name/3">链接地址：</a> 
  ```

  

数学操作

- 二元操作：+, - , * , / , %
- 一元操作: - （负）

布尔操作

- 一元 : and or
- 二元 : !,not

比较

- 比较：> , < , >= , <= ( gt , lt , ge , le )
- 等于：== , != ( eq , ne )

条件

- If-then: (if) ? (then)
- If-then-else: (if) ? (then) : (else)
- Default: (value) ?: (defaultvalue)

无操作 
使用_ 来禁止转义。

## 支持的操作

html5的操作支持：

```html
th:abbr          th:accept             th:accept-charset
th:accesskey             th:action             th:align
th:alt             th:archive             th:audio
th:autocomplete             th:axis             th:background
th:bgcolor             th:border             th:cellpadding
th:cellspacing             th:challenge             th:charset
th:cite             th:class             th:classid
th:codebase             th:codetype             th:cols
th:colspan             th:compact             th:content
th:contenteditable             th:contextmenu             th:data
th:datetime             th:dir             th:draggable
th:dropzone             th:enctype             th:for
th:form             th:formaction             th:formenctype
th:formmethod             th:formtarget             th:fragment
th:frame             th:frameborder             th:headers
th:height             th:high             th:href
th:hreflang             th:hspace             th:http-equiv

th:icon             th:id             th:inline
th:keytype             th:kind             th:label
th:lang             th:list             th:longdesc
th:low             th:manifest             th:marginheight
th:marginwidth             th:max             th:maxlength
th:media             th:method             th:min
th:name            th:onabort            th:onafterprint
th:onbeforeprint            th:onbeforeunload            th:onblur
th:oncanplay            th:oncanplaythrough            th:onchange
th:onclick            th:oncontextmenu            th:ondblclick
th:ondrag            th:ondragend            th:ondragenter
th:ondragleave            th:ondragover            th:ondragstart
th:ondrop            th:ondurationchange            th:onemptied
th:onended            th:onerror            th:onfocus
th:onformchange            th:onforminput            th:onhashchange
th:oninput            th:oninvalid            th:onkeydown
th:onkeypress            th:onkeyup            th:onload
th:onloadeddata            th:onloadedmetadata            th:onloadstart
th:onmessage            th:onmousedown            th:onmousemove
th:onmouseout            th:onmouseover            th:onmouseup
th:onmousewheel            th:onoffline            th:ononline
th:onpause            th:onplay            th:onplaying
th:onpopstate            th:onprogress            th:onratechange
th:onreadystatechange            th:onredo            th:onreset
th:onresize            th:onscroll            th:onseeked
th:onseeking            th:onselect            th:onshow
th:onstalled            th:onstorage            th:onsubmit
th:onsuspend            th:ontimeupdate            th:onundo
th:onunload            th:onvolumechange            th:onwaiting
th:optimum            th:pattern            th:placeholder
th:poster            th:preload            th:radiogroup
th:rel            th:rev            th:rows
th:rowspan            th:rules            th:sandbox
th:scheme            th:scope            th:scrolling
th:size            th:sizes            th:span
th:spellcheck            th:src            th:srclang
th:standby            th:start            th:step
th:style            th:summary            th:tabindex
th:target            th:title            th:type
th:usemap            th:value            th:valuetype
th:vspace            th:width            th:wrap

th:vspace            th:width            th:wrap
th:xmlbase            th:xmllang            th:xmlspace 
```

布尔类型

```html
th:async th:autofocus th:autoplay
th:checked th:controls th:declare
th:default th:defer th:disabled
th:formnovalidate th:hidden th:ismap
th:loop th:multiple th:novalidate
th:nowrap th:open th:pubdate
th:readonly th:required th:reversed
th:scoped th:seamless th:selected12345678
```

## 判断操作

thymeleaf提供了几种判断，if、switch

- 后台数据

```java
public class TestVo {
    private String name;
    private  Integer Score;
    private  Integer male;
    private Date birthday;

    public TestVo(String name, Integer score, Date birthday, Integer male) {
        this.name = name;
        Score = score;
        this.male = male;
        this.birthday = birthday;
    }
}


@RequestMapping("/test01")
public String thymeleaf(ModelMap map){
    List<TestVo> testVos=new ArrayList<>();
    testVos.add(new TestVo("数学",10,new Date(),1));
    testVos.add(new TestVo("数学0001",70,new Date(),2));
    testVos.add(new TestVo("数学01",100,new Date(),3));
    map.put("test",testVos);
    return "/back/import/test";
} 

```

- 前端语法

```
  <table>
       <thead>
           <th></th>
       </thead>
       <tbody>
            <tr th:each="test:${test}">
                <!--判断成绩-->
                <td th:if="${test.Score} gt 0 and ${test.Score} lt 60">差</td>
                <td th:if="${test.Score} ge 60 and ${test.Score} le 70">中</td>
                <td th:if="${test.Score} gt 70 and ${test.Score} le 80">良</td>
                <td th:if="${test.Score} gt 80 and ${test.Score} le 90">优</td>
                <td th:if="${test.Score} gt 90 and ${test.Score} le 100">超级优秀</td>
            </tr>
            <br>
            <tr th:each="test:${test}">
                <!--判断成绩   一般只有两种情况的时候可以使用这种方式-->
                <td th:if="${test.Score} gt 0 and ${test.Score} lt 60">差</td>
                <!--除了这些条件之外的-->
                <td th:unless="${test.Score} gt 0 and ${test.Score} lt 60">及格</td>
            </tr>
            <tr th:each="test:${test}">
                <td th:switch="${test.male}">
                    <span th:case="1">男</span>
                    <span th:case="2">女</span>
                    <!--其他情况-->
                    <span th:case="*">未知</span>
                </td>
            </tr>

       </tbody>
   </table> 
```

- 结果

差 
中 
超级优秀

差 
及格 
及格

男 
女

## 模板操作

主要引入公共头部和底部相关代码使用 ，当然也可以其他地方使用 
\- 示例

底部模板代码

```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.w3.org/1999/xhtml">
<body>
<div th:fragment="footer">
    © 2016 xxx
</div>
</body>
</html> 
```

Springboot整合引入模块

```html
<!--写入绝对路径即可引入 -->
<div th:include="/back/import/footer :: footer"></div>12
```

## 文本和带格式文本

用来输入内容到标签内部，而不是attr中。分为th:text和th:utext两种，后者可以渲染文本中的标签。 
\- th:utext

```java
    map.put("msgutext","<b>1111</b>");1
<div th:utext="${msgutext}"></div>
 
```

结果：被渲染了

- th:text

```html
<div th:text="${msgutext}"></div>
 
```

结果:原样输出到页面。

## 外围包裹–block

有时候需要在代码外部加层条件，但写div之类的又影响样式，此情况下你可以用下面这种方式:

```html
    <th:block th:with="score=${test.Score}">
        <td th:if="${score} ge 60">及格啦</td>
    </th:block>123
```

## 循环

遍历元素

- 示例：

```html
 <tr th:each="test:${test}">
    <td th:text="${test.Score}"></td>
 </tr>123
```

## 循环下标判断

```java
 List<String> list=new ArrayList<String>();
        list.add("1s");
        list.add("2s");
        list.add("3s");
        map.put("list",list);12345
<th:block th:each="mylist,iterStat:${list}">
    111
    <span th:text="${mylist}"></span>
        <th:block th:if="${iterStat.index le 1}">
            <span th:text="${mylist}"></span>
        </th:block>
</th:block>1234567
```

## 常用操作

- 日期格式化

```html
 <td th:text="${#dates.format(content.createDate,'yyyy-MM-dd HH:mm:ss')}"  ></td>1
```

- 字符截取长度

```html
<td th:if="${#strings.length(content.title) gt 5 } "  th:text="${#strings.substring(content.title,0,5) + '…'}"></td>1
```

- 下拉选择

```html
 <select name="subId" id="subId" lay-verify="" >
            <option value="">请选择</option>
            <option th:each="channelsub:${subchannels}" th:selected="${channelsub.id == subId}"    th:value="${channelsub.id}" th:text="${channelsub.name}"></option>
        </select>1234
```

## js取值

有时候需要传递参数到js中，按如下方式：

```javascript
  <script th:inline="javascript"  >
        var size= [[${test.size()}]];
        console.info(size)
  </script> 
```

- 进阶 
  ps: 下面涉及到thymeleaf的语法出，可以替换成其他thymeleaf的语法来用

```javascript
 <script th:inline="javascript"  >
        //尺寸等于3时打印日志..
        /*[# th:if="${test.size() eq 3}"]*/
        console.info('Welcome admin');
        /*[/]*/
    </script> 
```

## css取值

首先需要后台设置classname、align的值，之后按如下方式：

```css
<style th:inline="css">
.[[${classname}]] {
text-align: [[${align}]];
}
</style> 
```

## 结语

thymeleaf还有很多其他的语法，这里只是做个入门，方便上手，详细教程请参阅 官方教程。当然也可以加群交流。





https://blog.csdn.net/u014042066/article/details/75614906