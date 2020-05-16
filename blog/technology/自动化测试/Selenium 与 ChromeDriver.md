[TOC]



# Selenium 与 ChromeDriver

[冬瓜baba](https://www.jianshu.com/u/66c41e72644b)关注

12018.03.21 18:52:26字数 1,838阅读 38,422

*王某某瞎编于 2018年3月20日*

## Selenium

Selenium 是 ThoughtWorks 提供的一个强大的基于浏览器的开源自动化测试工具。
Selenium 是一个用于 Web 应用程序测试的工具，测试直接自动运行在浏览器中，就像真正的用户在手工操作一样。支持的浏览器包括 IE、Chrome 和 Firefox 等。这个工具的主要功能包括：测试与浏览器的兼容性 - 测试您的应用程序看是否能够很好地工作在不同浏览器和操作系统之上；测试系统功能 - 创建回归测试检验软件功能和用户需求；支持自动录制动作，和自动生成 .NET、Perl、Python、Ruby 和 Java 等不同语言的测试脚本。

Selenium 1 (又叫 Selenium RC 或 Remote Control)

## Webdriver

Selenium 2，又名 WebDriver，它的主要新功能是集成了 Selenium 1.0 以及 WebDriver， 是两个项目的合并，既兼容 Selenium API 也支持 WebDriver API。

WebDriver 曾经是 Selenium 的竞争对手（最开始是google的一个人弄的，主要用于避免在JavaScript的沙箱环境里存在的各种限制），他主要是通过利用浏览器原生API的方式来操控浏览器执行各种动作（还包括系统级别的调用来模拟用户输入）。

Selenium WebDriver 就是对浏览器提供的原生API进行封装，使其成为一套更加面向对象的Selenium WebDriver API。
使用这套API可以操控浏览器的开启、关闭，打开网页，操作界面元素，控制Cookie，还可以操作浏览器截屏、安装插件、设置代理、配置证书等。由于使用的原生API，其速度与稳定性都会好很多。但浏览器厂商各不相同，提供的驱动各异（ChromeDriver、FirefoxDriver(xpi插件)、InternetExplorerDriver(exe)等），API也会有差异（好像都走JSON Wire Protocol，并且向W3C标准靠拢）。Selenium 对不同厂商的各个驱动进行了封装，如：selenium-chrome-driver、selenium-edge-driver、selenium-firefox-driver等。
还包括了对移动应用进行测试的AndroidDriver和iOS WebDriver，以及一个基于HtmlUnit的无界面实现HtmlUnitDriver。

WebDriver API可以通过Python、Ruby、Java和C#访问

WebDriver是一个用来进行复杂重复的web自动化测试的工具。意在提供一种比Selenium1.0更简单易学，有利于维护的API。它没有和任何测试框架进行绑定，所以他可以很好的在单元测试和main方法中调用。一旦创建好一个Selenium工程，你马上会发现WebDriver和其他类库一样：它是完全独立的，你可以直接使用而不需要考虑其他配置，这个Selenium RC是截然相反的。

### 两者的差异

- 对于所有类型的浏览器Selenium- RC都是使用的同一种方法：
  当浏览器启动时，向其中注入javascript，从而使用这些js来驱动浏览器中的AUT(Application Under Test)。
- WebDriver并没有使用这种技术，它是通过调用浏览器原生的自动化API直接驱动浏览器。

### Selenium IDE

Selenium IDE (集成开发环境) 是一个创建测试脚本的原型工具。它是一个 Firefox 插件，提供创建自动化测试的建议接口。Selenium IDE 有一个记录功能，能记录用户的操作，并且能选择多种语言把它们导出到一个可重用的脚本中用于后续执行。

------

## ChromeDriver - WebDriver for Chrome

WebDriver是一个开源工具，用于在许多浏览器上自动测试webapps。它提供了导航到网页，用户输入，JavaScript执行等功能。ChromeDriver是一个独立的服务，它为 Chromium 实现 WebDriver 的 JsonWireProtocol 协议。

目前正在实现并转向W3C标准。 ChromeDriver适用于Android版Chrome和桌面版Chrome（Mac，Linux，Windows和ChromeOS）。

官网地址：
[https://sites.google.com/a/chromium.org/chromedriver/home](https://link.jianshu.com/?t=https%3A%2F%2Fsites.google.com%2Fa%2Fchromium.org%2Fchromedriver%2Fhome)

ChromeDriver 是 google 为网站开发人员提供的自动化测试接口，它是 **selenium2** 和 **chrome浏览器** 进行通信的桥梁。selenium 通过一套协议（JsonWireProtocol ：[https://github.com/SeleniumHQ/selenium/wiki/JsonWireProtocol](https://link.jianshu.com/?t=https%3A%2F%2Fgithub.com%2FSeleniumHQ%2Fselenium%2Fwiki%2FJsonWireProtocol)）和 ChromeDriver 进行通信，selenium 实质上是对这套协议的底层封装，同时提供外部 WebDriver 的上层调用类库。

**大概的工作流程：**

在代码中 new ChromeDriver() 时，selenium会随机挑选一个端口调用chromedriver程序，调用成功后 chromedriver 会在指定的端口启动一个服务（会有一个进程）

```swift
>tasklist | find "chromedriver"
chromedriver.exe              7848 Console                    1     13,740 K
```

selenium 中使用 apache 的 commons-exec 来运行 chromedriver.exe 启动 ChromeDriver 服务
直接在控制台运行 chromedriver.exe 时，默认端口是9515

selenium 通过指定的端口和约定的协议来和 ChromeDriver 进行通信，一个ChromeDriver可用管理多个chrome。
*（具体细节可以看看协议部分，ChromeDriver如何控制chrome可能需要去看源码~~）*

```swift
>tasklist | find "chrome"
chromedriver.exe             14692 Console                    1     14,888 K
chrome.exe                   20952 Console                    1     72,204 K
chrome.exe                    7288 Console                    1      9,052 K
chrome.exe                   15524 Console                    1     10,000 K
chrome.exe                   13036 Console                    1     96,028 K
chrome.exe                   11836 Console                    1     29,836 K
chrome.exe                    2788 Console                    1     59,876 K 
```

selenium 中多个 WebDriver 实例对应一个 chromedriver 进程，一个 chromedriver 进程管理多个 chrome 进程。
一个 WebDriver 实例对应一个浏览器窗口。

在代码中直接 new ChromeDriver() 将会启动一个 chromedriver 进程
使用 RemoteWebDriver 则只会连接到 chromedriver 服务，不会启动一个新的进程，连接不上会报错。
不管哪种方式都会打开一个浏览器窗口。

```cpp
WebDriver driver = new RemoteWebDriver(new URL("http://127.0.0.1:9515"), DesiredCapabilities.chrome());
```

## 使用

官方指导页面

https://sites.google.com/a/chromium.org/chromedriver/getting-started

基于java

1. 创建Maven项目
   引入相关依赖

```xml
<dependency>
    <groupId>org.seleniumhq.selenium</groupId>
    <artifactId>selenium-java</artifactId>
    <version>3.7.1</version>
</dependency>
```

1. 确保Chromium / Google Chrome安装在认可的位置

ChromeDriver希望您将Chrome安装在您平台的默认位置。您还可以强制ChromeDriver通过设置特殊功能来使用自定义位置。

```cpp
ChromeOptions options = new ChromeOptions();
options.setBinary("/path/to/other/chrome/binary");
```

1. 下载与你安装的chrome对应的chromedriver

| chromedriver 版本 | chrome 版本   |
| ----------------- | ------------- |
| ChromeDriver 2.36 | Chrome v63-65 |
| ChromeDriver 2.35 | Chrome v62-64 |
| ChromeDriver 2.34 | Chrome v61-63 |
| ChromeDriver 2.33 | Chrome v60-62 |

*（这里随便复制了几个）*
下载地址：https://sites.google.com/a/chromium.org/chromedriver/downloads

1. 指定ChromeDriver所在位置，可以通过两种方法指定：

- 通过配置ChromeDriver.exe位置到path环境变量
- 通过设置**webdriver.chrome.driver** 系统属性实现-

1. 创建一个新的ChromeDriver的实例，并调用get方法打开页面

代码:

```cpp
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class BaseTest {

    public static void main(String[] args) {
        // 设置ChromeDriver的路径
        System.setProperty("webdriver.chrome.driver", "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chromedriver.exe");

        WebDriver driver = new ChromeDriver();

        driver.get("http://www.baidu.com/");
        
        //driver.quit();
    }
}
```

执行这段代码将打开一个浏览器窗口，并访问百度
同时浏览器上将显示：Chrome 正受到自动测试软件的控制

代码执行完成后chrome并不会关闭，需要调用 driver.quit(); 才能关闭浏览器窗口。

打开百度并进行搜索：

```csharp
public static void main(String[] args) throws InterruptedException {

        // 如果不设置将搜索环境变量
        System.setProperty("webdriver.chrome.driver", "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chromedriver.exe");

        WebDriver driver = new ChromeDriver();
        driver.get("http://www.baidu.com/");

        Thread.sleep(3000);

        WebElement searchBox = driver.findElement(By.id("kw"));

        searchBox.sendKeys("ChromeDriver");
        searchBox.submit();

        Thread.sleep(3000);
        driver.quit();

    }
```

## 高效使用

ChromeDriver 启动ChromeDriver服务器进程，并在调用退出时终止它。
在大型测试时每个测试都会创建一个ChromeDriver实例，这将会浪费大量时间。有两种方法可以解决这个问题：

#### 1. 使用ChromeDriverService

```java
@RunWith(BlockJUnit4ClassRunner.class)
public class ChromeTest extends TestCase {

  private static ChromeDriverService service;
  private WebDriver driver;

  @BeforeClass
  public static void createAndStartService() {
    service = new ChromeDriverService.Builder()
        .usingDriverExecutable(new File("path/to/my/chromedriver"))
        .usingAnyFreePort()
        .build();
    service.start();
  }

  @AfterClass
  public static void createAndStopService() {
    service.stop();
  }

  @Before
  public void createDriver() {
    driver = new RemoteWebDriver(service.getUrl(),
        DesiredCapabilities.chrome());
  }

  @After
  public void quitDriver() {
    driver.quit();
  }

  @Test
  public void testGoogleSearch() {
    driver.get("http://www.google.com");
    // rest of the test...
  }
}
```

#### 2. 在运行测试之前单独启动ChromeDriver服务器，并使用Remote WebDriver连接到它

在控制台执行chromedriver（如果没有添加环境变量的话需要到对应目录下执行）

```css
F:\temp>chromedriver
Starting ChromeDriver 2.37.543627 (63642262d9fb93fb4ab52398be4286d844092a5e) on port 9515
Only local connections are allowed.
```

Java 代码:

```csharp
public static void main(String[] args) throws MalformedURLException {
    WebDriver driver = new RemoteWebDriver(new URL("http://127.0.0.1:9515"), DesiredCapabilities.chrome());

    driver.get("http://www.baidu.com");
}
```





https://www.jianshu.com/p/31c8c9de8fcd