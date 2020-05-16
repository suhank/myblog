[TOC]



# javascript 改变URL地址栏但是不刷新页面

2018-01-24 14:45:31

## 实例

一般用来清空url传递的参数不刷新，这样可以少发送一次请求到后台 

```js
// 去掉参数
history.pushState({},'',window.location.origin + window.location.pathname);
```





## 官方文档:

<https://developer.mozilla.org/zh-CN/docs/Web/API/History_API>

### pushState() 方法

`pushState()` 需要三个参数: 一个状态对象, 一个标题 (目前被忽略), 和 (可选的) 一个URL. 让我们来解释下这三个参数详细内容：

- **状态对象** — 状态对象state是一个JavaScript对象，通过pushState () 创建新的历史记录条目。无论什么时候用户导航到新的状态，popstate事件就会被触发，且该事件的state属性包含该历史记录条目状态对象的副本。

  ​        状态对象可以是能被序列化的任何东西。原因在于Firefox将状态对象保存在用户的磁盘上，以便在用户重启浏览器时使用，我们规定了状态对象在序列化表示后有640k的大小限制。如果你给 `pushState()` 方法传了一个序列化后大于640k的状态对象，该方法会抛出异常。如果你需要更大的空间，建议使用 `sessionStorage` 以及 `localStorage`.

- **标题** — Firefox 目前忽略这个参数，但未来可能会用到。在此处传一个空字符串应该可以安全的防范未来这个方法的更改。或者，你可以为跳转的state传递一个短标题。

- **URL** — 该参数定义了新的历史URL记录。注意，调用 `pushState()` 后浏览器并不会立即加载这个URL，但可能会在稍后某些情况下加载这个URL，比如在用户重新打开浏览器时。新URL不必须为绝对路径。如果新URL是相对路径，那么它将被作为相对于当前URL处理。新URL必须与当前URL同源，否则 `pushState()` 会抛出一个异常。该参数是可选的，缺省为当前URL。

**注意:** 从 Gecko 2.0 (Firefox 4 / Thunderbird 3.3 / SeaMonkey 2.1) 到 Gecko 5.0 (Firefox 5.0 / Thunderbird 5.0 / SeaMonkey 2.2)，传递的对象是使用JSON进行序列化的。 从  Gecko 6.0 (Firefox 6.0 / Thunderbird 6.0 / SeaMonkey 2.3)开始，该对象的序列化将使用[结构化克隆算法](https://developer.mozilla.org/en/DOM/The_structured_clone_algorithm)。这将会使更多对象可以被安全的传递。

​        在某种意义上，调用 `pushState()` 与 设置 `window.location = "#foo"` 类似，二者都会在当前页面创建并激活新的历史记录。但 `pushState()` 具有如下几条优点：

- 新的 URL 可以是与当前URL同源的任意URL 。相反，只有在修改哈希时，设置 `window.location` 才能是同一个 [`document`](https://developer.mozilla.org/zh-CN/docs/Web/API/Document)。
- 如果你不想改URL，就不用改。相反，设置 `window.location = "#foo";`在当前哈希不是 `#foo` 时， 才能创建新的历史记录项。
- 你可以将任意数据和新的历史记录项相关联。而基于哈希的方式，要把所有相关数据编码为短字符串。 
- 如果 `标题` 随后还会被浏览器所用到，那么这个数据是可以被使用的（哈希则不是）。

注意 `pushState()` 绝对不会触发 `hashchange` 事件，即使新的URL与旧的URL仅哈希不同也是如此。

在 [XUL](https://developer.mozilla.org/en-US/docs/Mozilla/Tech/XUL) 文档中，它创建指定的 XUL 元素。

在其它文档中，它创建一个命名空间URI为`null`的元素。

###  





<https://blog.csdn.net/weiqiang2/article/details/79150732>