[TOC]



# mac下快速安装php的各个版本

mac下其实已经安装好了PHP，而且版本还蛮新的，平时搞一搞开发，还是可以的，也没啥大问题，但是今天偶然发现一个验证码图片加载不了，函数报错：

```
PHP Fatal error: Call to undefined function imagettfbbox()
```

然后一搜索，发现是`freetype`扩展在PHP编译的时候没加，由于这个扩展是核心内置扩展，没法通过`phpize`来编译安装。解决办法只有一个，就是找到PHP的安装源码重新编译一下，在编译的时候，加上`–with-freetype-dir`。

可是最蛋疼的是，Mac上PHP是内置的，根本就找不到它的安装源码在哪！所以，唯一的办法，就是下载新的PHP的源码包，完全完全新的编译安装一遍PHP。

我了个擦，这不是要了老命吗？各种扩展，各种参数，关键是PHP的编译时间巨长啊。

但是，你能想到的，牛人都想到了。所以一个神奇的命令诞生了，一句命令就可以安装好PHP制定的版本，最关键是它几乎安装好了所有的PHP扩展，什么redis,memcache啊都全部安装好了。而且不会覆盖你的Mac上的已经安装好的PHP。

就是这个网站： <http://php-osx.liip.ch/>

我想安装PHP5.6。所以，用下面一条命令就可以搞定PHP5.6：

```
curl -s http://php-osx.liip.ch/install.sh | bash -s 5.6
```

这条命令执行后，会让你输入你的Mac密码，因为它需要安装各种扩展命令，输入完密码后，就静静的等吧，我好像等了一分钟吧，还是蛮快的，就安装好了。

```
.....
.....
Extracting usr/local/php5-5.6.11-20150710-223902/bin/uconv
Extracting usr/local/php5-5.6.11-20150710-223902/bin/vacuumdb
Extracting usr/local/php5-5.6.11-20150710-223902/bin/wrjpgcom
Extracting usr/local/php5-5.6.11-20150710-223902/bin/xgettext
Extracting usr/local/php5-5.6.11-20150710-223902/bin/xslt-config
Extracting usr/local/php5-5.6.11-20150710-223902/bin/xsltproc
Executing post-install script /tmp/5.6-10.10-frontenddev-post-install
Create symlink /usr/local/php5/entropy-php.conf /etc/apache2/other/+php-osx.conf
Restarting Apache
Syntax OK
```

它把PHP安装到了`/usr/local/php5`目录下，是一个单独的目录，所以，不会影响到原先的PHP，这2个版本是共存的。完全不会影响到目前的PHP版本。

这个时候，你在终端中输入`php －v` 显示的还是老的版本，如果你想用新的版本的php。可以这样`/usr/local/php5/bin/php -v`，这样很麻烦，可以将这个新的PHP路径追加到`$path`中。

```
vi ~/.profile
//有就编辑，没有就新建一个这个文件名。
export PATH=/usr/local/php5/bin:/usr/local/php5/sbin:$PATH
//清除下缓存，使得它生效
source ~/.profile
```

这样，就将新版本的`php`命令和`php-fpm`命令都追加到`$PATH`中，就可以在全局使用了。

全局运行下 :

```
 ~ php -v
PHP 5.6.11 (cli) (built: Jul 10 2015 22:36:04)
Copyright (c) 1997-2015 The PHP Group
Zend Engine v2.6.0, Copyright (c) 1998-2015 Zend Technologies
    with Zend OPcache v7.0.6-dev, Copyright (c) 1999-2015, by Zend Technologies
    with Xdebug v2.2.5, Copyright (c) 2002-2014, by Derick Rethans
~
```

```
~php-fpm -v
PHP 5.6.11 (fpm-fcgi) (built: Jul 10 2015 22:36:10)
Copyright (c) 1997-2015 The PHP Group
Zend Engine v2.6.0, Copyright (c) 1998-2015 Zend Technologies
    with Zend OPcache v7.0.6-dev, Copyright (c) 1999-2015, by Zend Technologies
    with Xdebug v2.2.5, Copyright (c) 2002-2014, by Derick Rethans
```

你看，都是新的版本了。太他妈的爽了。1分钟全安装好了。

它的`php.ini` 在 `/usr/local/php5/lib/php.ini`。然后，扩展的一些配置都在`/usr/local/php5/php.d/`目录下，这样就清楚很多：

```
-rw-r--r--  1 root  wheel    75B  7 11 04:39 10-extension_dir.ini
-rw-r--r--  1 root  wheel   114B  7 11 04:39 20-extension-opcache.ini
-rw-r--r--  1 root  wheel   103B  7 11 04:39 50-extension-apcu.ini
-rw-r--r--  1 root  wheel    18B  7 11 04:39 50-extension-curl.ini
-rw-r--r--  1 root  wheel    17B  7 11 04:39 50-extension-gmp.ini
-rw-r--r--  1 root  wheel    83B  7 11 04:39 50-extension-igbinary.ini
-rw-r--r--  1 root  wheel    18B  7 11 04:39 50-extension-imap.ini
-rw-r--r--  1 root  wheel    79B  7 11 04:39 50-extension-intl.ini
-rw-r--r--  1 root  wheel    20B  7 11 04:39 50-extension-mcrypt.ini
-rw-r--r--  1 root  wheel    83B  7 11 04:39 50-extension-memcache.ini
-rw-r--r--  1 root  wheel    84B  7 11 04:39 50-extension-memcached.ini
-rw-r--r--  1 root  wheel    80B  7 11 04:39 50-extension-mongo.ini
-rw-r--r--  1 root  wheel    41B  7 11 04:39 50-extension-mssql.ini
-rw-r--r--  1 root  wheel    80B  7 11 04:39 50-extension-oauth.ini
-rw-r--r--  1 root  wheel    23B  7 11 04:39 50-extension-pdo_dblib.ini
-rw-r--r--  1 root  wheel    23B  7 11 04:39 50-extension-pdo_pgsql.ini
-rw-r--r--  1 root  wheel    19B  7 11 04:39 50-extension-pgsql.ini
-rw-r--r--  1 root  wheel    81B  7 11 04:39 50-extension-propro.ini
-rw-r--r--  1 root  wheel    80B  7 11 04:39 50-extension-raphf.ini
-rw-r--r--  1 root  wheel    22B  7 11 04:39 50-extension-readline.ini
-rw-r--r--  1 root  wheel    80B  7 11 04:39 50-extension-redis.ini
-rw-r--r--  1 root  wheel    79B  7 11 04:39 50-extension-solr.ini
-rw-r--r--  1 root  wheel    79B  7 11 04:39 50-extension-ssh2.ini
-rw-r--r--  1 root  wheel    80B  7 11 04:39 50-extension-twig.ini
-rw-r--r--  1 root  wheel    90B  7 11 04:39 50-extension-uploadprogress.ini
-rw-r--r--  1 root  wheel   430B  7 11 04:39 50-extension-xdebug.ini
-rw-r--r--  1 root  wheel    81B  7 11 04:39 50-extension-xhprof.ini
-rw-r--r--  1 root  wheel    17B  7 11 04:39 50-extension-xsl.ini
-rw-r--r--  1 root  wheel    79B  7 11 04:39 60-extension-pecl_http.ini
-rwxrwxrwx  1 root  wheel    32B  7 21 18:24 99-liip-developer.ini
```

所以你看，是不是我们开发中用到的能用到的几乎所有扩展他都帮我们安装好了，真是太赞了。

按照官网上的一些说明，最后的一个文件`99-liip-developer.ini`,当我们需要修改一些配置的时候，我们只需要修改这个文件就可以了，不需要去动`php.ini`，写入这个文件，它会覆盖`php.ini`里的一些配置项。

比如，我需要改正时区：

```
vi 99-liip-developer.ini
date.timezone = Asia/Shanghai
```

就可以了。

如果，要启动新的php-fpm，就将老的进程杀掉，然后再运行新的版本命令：

```
sudo php-fpm
[21-Jul-2015 23:07:30] ERROR: failed to open configuration file '/usr/local/php5/etc/php-fpm.conf': No such file or directory (2)
[21-Jul-2015 23:07:30] ERROR: failed to load configuration file '/usr/local/php5/etc/php-fpm.conf'
[21-Jul-2015 23:07:30] ERROR: FPM initialization failed
```

会报错，说找不到php-fpm.conf。它默认去`/usr/local/php5/etc/`目录下去找了，由于，我之前已经有了一个在`/etc/php-fpm.conf`。所以，我直接用这个配置好了：

```
sudo php-fpm -y /etc/php-fpm.conf 
```

就可以了。

我写了这么多，其实，就一句话可以搞定，现在就可以愉快的使用新版本的PHP了。而且官网还有其他的版本和一些用法和注意项，可以好好看下。





https://www.zybuluo.com/phper/note/137276