# 微信小程序 – block组件

 

开发微信小程序这么多天，突然发现有这么个组件block，看来开始开发前仔细看官方文档还是有必要的。

block与view,text等的区别是渲染页面时，它不实际输出自身，但包含在block块中的组件会被输出。举个例子

```
<block wx:if="{{isShow}}">
    <view>...</view>
    <view>...</view>
    ...
</block>
```

或者你可以这样写：

```
<view wx:if="{{isShow}}">
    <view>...</view>
    <view>...</view>
    ...
</view>
```

比起上面的写法，这种写法在渲染时会多出外面的一层view

你也可以这样写：

```
<view wx:if="{{isShow}}">...</view>
<view wx:if="{{isShow}}">...</view>
...
```

相比之下还是第一种方法科学点。

block仅仅作为指令的载体，除了在block组件上使用wx:if指令我，我们还可以使用wx:for指令。



http://www.pinphoo.com/121.html