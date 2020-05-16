# Mac OS X 平台 Sublime Text 3 中文乱码

sublime Mac是一款很好的编辑器，但是其中不支持GBK和GB2312这两种格式的中文，好在 Sublime Text 有非常丰富的开源插件，帮忙解决很多日常问题。
1、没有安装Sublime package control的童鞋，记得安装一下，否则无法下载插件的
安装方法：
使用Control +‘~’命令打开控制台，Sublime text3，输入如下：
import urllib.request,os,hashlib; h = '2915d1851351e5ee549c20394736b442' + '8bc59f460fa1548d1514676163dafc88'; pf = 'Package Control.sublime-package'; ipp = sublime.installed_packages_path(); urllib.request.install_opener( urllib.request.build_opener( urllib.request.ProxyHandler()) ); by = urllib.request.urlopen( '[http://packagecontrol.io/'](https://link.jianshu.com/?t=http://packagecontrol.io/%27) + pf.replace(' ', '%20')).read(); dh = hashlib.sha256(by).hexdigest(); print('Error validating download (got %s instead of %s), please try manual install' % (dh, h)) if dh != h else open(os.path.join( ipp, pf), 'wb' ).write(by)，回车安装
2、Command+shift+p，打开刚才安装的package control 命令行控件，输入Install Package。输入“ConvertToUTF8”或“GBK Encoding Support”，然后回车安装。如果还要支持 Windows 平台默认的 GB2312 中文编码，那么需要安装 Codecs33 插件，不然会提示如下错误：
File: /Users/xxx/yyy.cc
Encoding: GB2312
Error: Codecs missing
Please install Codecs33 plugin ([https://github.com/seanliang/Codecs33/tree/osx](https://link.jianshu.com/?t=https://github.com/seanliang/Codecs33/tree/osx)).
待安装好之后重新打开文件，汉字可以正常显示了。



https://www.jianshu.com/p/35e631e27d38