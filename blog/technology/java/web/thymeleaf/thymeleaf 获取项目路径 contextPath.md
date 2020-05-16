# thymeleaf 获取项目路径 contextPath

## thymeleaf 标签：

```html
${#httpServletRequest.getContextPath()}+'/'
 // 输出结果: /projectName/
```

直接引用 

```
th:onclick="@{'location.href=\'' + ${#httpServletRequest.getContextPath()} + '/xxxx\'}"
```

其它：

```
${#servletContext.contextPath} 
```

## javascript中引用

```html
<!-- 根路径 -->
<script type="text/javascript" th:inline="javascript">
   /*<![CDATA[*/
   ctxPath = /*[[@{/}]]*/ '';
   /*]]>*/
   console.info(ctxPath);
   // 输出结果: /projectName/
</script>
```





https://blog.csdn.net/wdd668/article/details/79282601