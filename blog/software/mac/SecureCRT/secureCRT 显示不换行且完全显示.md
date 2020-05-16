# secureCRT 显示不换行且完全显示

2017-07-13 

 阅读量：252次 | 字数：665 | 阅读时长：大约2分钟

如题

[SecureCRT download](https://www.vandyke.com/download/securecrt/download.html)

secureCRT 显示默认是换行的，如果设置了不换行，当一行字符比较多的时候，会显示不全。下面的设置就是解决这两个问题的。

1. Options -> Global Options -> Terminal -> Appearance -> Maximum columns 设置成1200（值越大，一行显示的字符越多，如果一行的字符大于这里设置的值，则多余的字符将不显示）。选上show horizontal scrollbar。
2. 上面的数值会影响到每个 Session 对应的 Logical Columns 值，相应的 Logical Columns 值只能小于等于上面的值，不能大于，如果上面的值改小了，session 里相应的 Logical Columns 会自动改小。
3. Global Options -> General -> Default Session -> Edit Default Session -> Terminal -> Emulation ->
   1. Logical Rows，设置成 33。
      这一项要根据你的显示器分辨率来设置，如果值太大了刚连接上终端时会滚动到最下面，可能需要向上滚动才能看到命令行。我的分辨率是1920*1080，设置成33正好。经过尝试，设置成36时，在编辑的时候，光标跳不到最前行。
   2. Logical Columns 设置成第一项 Global 设置里的 Maximum columns 值，可以小于这个值，但不能大于 Maximum columns 值（设置成一个大于Maximum columns的值不能保存，会有提示），见第 2 项描述。
   3. 选上 Retain size and font（这一项很重要，不选这项将等同于没有水平滚动条）。
   4. Scrollback buffer 设置成 5000（这样纵向滚动屏就可以缓存更多内容，但也更占内存）。
   5. Terminal -> Appearance -> Window -> 选上 Show horizontal scroll bar 和 Show vertical scroll bar。
   6. 上面的所有设置都是为这一步设置作准备的，万事具备，只欠东风。Terminal -> Emulation -> Modes -> Initial modes -> line wrap 取消勾选。勾选，则为换行。
4. 重新打开secureCRT，连接终端。

### 后记

对于折行，其实，还是需要的。
当然，如果不需要显示全部的信息，如果觉得通过上面的设置，显示的内容已经足够应付日常工作需要了，也可以忽略下面的内容。

经过上面的设置，一行显示的长度已经足够普通脚本的显示了，也就是说，普通脚本，完全可以在一行显示完，所以，折不折行是无所谓的。
但是，特殊情况下，比如通过 api 查询一张淘宝订单，显示的内容是很多的，一行根本显示不完，在不折行的情况下，显示不完的内容会被截断。
折行设置：
Terminal -> Emulation -> Modes -> Initial modes -> line wrap，将该选项勾选上即可。

> 最后更新时间：2019年11月4日 11:16
> 转载请注明出处：https://www.lovesofttech.com/linux/secureCRTLineWrap.html