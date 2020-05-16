# shell向python传参数

想要在shell中调用python脚本时实现：

```shell
python pyServer.py argu1 argu2 argu3
```

利用 sys.argv 即可读取到 相应参数：

```python
# coding=utf-8
import sys

if __name__ == '__main__':
    for i in range(0,len(sys.argv)):
        print(sys.argv[i])
```

改进点的话：

```python
# coding=utf-8
import sys

def parseArgument():
    if (len(sys.argv) < 2):
        raise Exception,u"arguments needed"
    
    #init
    argus = {}
    argus["gameName"] = u""
    argus["bSave"] = False
    argus["bpreBuild"] = False

    #set
    argus["gameName"] = sys.argv[1]
    for i in range(2,len(sys.argv)):
        if (sys.argv[i] == 'needSave'):
            argus["bSave"] = True
        elif (sys.argv[i] == 'needPreBuild'):
            argus["bpreBuild"] = True
    
    return argus

if __name__ == '__main__':
    argus = parseArgument()
    print(u'游戏名字为：{0}'.format(argus["gameName"]))
    if argus['bSave']:
        print(u'需要保存')
    else:
        print(u'不需要保存')

    if argus['bpreBuild']:
        print(u'打包预处理')
    else:
        print(u'不打包预处理')
```

执行：

```
python test.py ox needPreBuild
```

结果：

```
游戏名字为: ox
不需要保存
打包预处理
```





https://www.cnblogs.com/sixbeauty/p/4285565.html