[TOC]



# WIN下cmd如何指定某软件打开某文件？

## 问题

比如。我的软件是在其它文件夹的。D:\ProgramFiles(x86)\Notepad++\notepad++.exe这个是软件然后通过这个软件打开当前目录下的test.txtPS：我已经将notepad++.bat粘贴到system32文件夹...[展开](javascript:void(0))

 我来答 分享 *[举报](javascript:void(0))*

## 回答

```
"D:\Program Files (x86)\Notepad++\notepad++.exe" test.txt
```

前提你的cmd已经切换到test.txt的目录下

追问

```
明显我是想直接输入

notepad++ test.txt

这样。不想转入那一大长串的路径。

已更新提问。
```

追答

```WIN下cmd如何指定某软件打开某文件？
还有个方法是放系统变量的Path里面，效果差不多。
```





https://zhidao.baidu.com/question/1302412493009807019.html