[TOC]



# 一次spring bean初始化顺序问题排查记录 @DependsOn

## 问题重现

最近在使用Springboot的时候需要通过静态的方法获取到Spring容器托管的bean对象，参照一些博文里写的，新建了个类，并实现ApplicationContextAware接口。代码大致如下： 

```java
@Component
public class SpringUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if(SpringUtils.applicationContext == null) {
            SpringUtils.applicationContext = applicationContext;
        }
    }
   public static <T> T getBean(String name,Class<T> clazz){
        return getApplicationContext().getBean(name, clazz);
    }  
}
```

然后另外一个bean需要依赖这个静态获取bean的方法，代码大致如下：

```java
@Component
public class TestBean{

   private Object dependencyBean = SpringUtils.getBean(OtherBean.class);  
    
}
```

*（注： 忽略代码逻辑是否合理~~ 这些代码是为演示所用简化的逻辑，肯定有同学会说：既然都是bean了为什么不注入，而是要用静态的获取呢？这个暂时不考虑，暂认为就必须要这样搞）*

这两个类的层次结构和包名大致如下： 

*utils*

*> notice*

　　*> TestBean*

*> SpringUtils*

就是TestBean在SpringUtils的下一级，TestBean所在包名为notice（这这个名字很重要！ 直接影响到这两个bean的加载顺序，具体原理往下看）

代码就这么多，从以上代码来静态分析看，确实有些漏洞，因为没有考虑到Spring bean的加载顺序，可能导致的SpringUtils报空指针异常（在TestBean先于SpringUtils初始化的场景下），不管怎么样先执行一下看下效果，效果如下： 

macOS操作系统下代码正常

windows平台下代码空指针异常

## 问题分析

为什么这还跟平台有关了呢？难道Spring bean的初始化顺序还跟平台有关？事实证明这个猜想是正确的。下面从Spring源代码里来找原因。

这里需要重点关注的类是 ConfigurationClassPostProcessor，这个类是干什么的？它从哪里来？如何实现bean的加载的？

在Spring里可以指定甚至自定义多个BeanFactoryPostProcessor来实现在实例化bean之前做一些bean容器的更新操作，比如修改某些bean的定义、增加一些bean、删除一些bean等，而ConfigurationClassPostProcessor就是Spring为了支持基于注解bean的功能而实现的BeanFactoryPostProcessor。

web环境的Springboot默认使用的应用上下文（ApplicationContext，BeanFactoryPostProcessor就是注册到这里才会起作用的）是AnnotationConfigEmbeddedWebApplicationContext，在AnnotationConfigEmbeddedWebApplicationContext的构造方法里初始化this.reader的时候，在reader的构造方法里把ConfigurationClassPostProcessor添加到ApplicationContext里了：

```java
public class AnnotationConfigEmbeddedWebApplicationContext
        extends EmbeddedWebApplicationContext {

    private final AnnotatedBeanDefinitionReader reader;

    private final ClassPathBeanDefinitionScanner scanner;

    private Class<?>[] annotatedClasses;

    private String[] basePackages;

    /**
     * Create a new {@link AnnotationConfigEmbeddedWebApplicationContext} that needs to be
     * populated through {@link #register} calls and then manually {@linkplain #refresh
     * refreshed}.
     */
    public AnnotationConfigEmbeddedWebApplicationContext() {
        this.reader = new AnnotatedBeanDefinitionReader(this);  // 重点关注这句
        this.scanner = new ClassPathBeanDefinitionScanner(this);
    }
```

  

```java
public class AnnotatedBeanDefinitionReader {

    private final BeanDefinitionRegistry registry;

    private BeanNameGenerator beanNameGenerator = new AnnotationBeanNameGenerator();

    private ScopeMetadataResolver scopeMetadataResolver = new AnnotationScopeMetadataResolver();

    private ConditionEvaluator conditionEvaluator;

    /**
     * Create a new {@code AnnotatedBeanDefinitionReader} for the given registry and using
     * the given {@link Environment}.
     * @param registry the {@code BeanFactory} to load bean definitions into,
     * in the form of a {@code BeanDefinitionRegistry}
     * @param environment the {@code Environment} to use when evaluating bean definition
     * profiles.
     * @since 3.1
     */
    public AnnotatedBeanDefinitionReader(BeanDefinitionRegistry registry, Environment environment) {
        Assert.notNull(registry, "BeanDefinitionRegistry must not be null");
        Assert.notNull(environment, "Environment must not be null");
        this.registry = registry;
        this.conditionEvaluator = new ConditionEvaluator(registry, environment, null);
        AnnotationConfigUtils.registerAnnotationConfigProcessors(this.registry); // 这里把ConfigurationClassPostProcessor注册到上下文里了
    }
```



上面介绍了Spring如何注册这个ConfigurationClassPostProcessor，下面看下这个类如何实现bean定义加载的。

```
public void processConfigBeanDefinitions(BeanDefinitionRegistry registry) {
        // ....
        // Parse each @Configuration class
        ConfigurationClassParser parser = new ConfigurationClassParser(
                this.metadataReaderFactory, this.problemReporter, this.environment,
                this.resourceLoader, this.componentScanBeanNameGenerator, registry);

        Set<BeanDefinitionHolder> candidates = new LinkedHashSet<BeanDefinitionHolder>(configCandidates);
        Set<ConfigurationClass> alreadyParsed = new HashSet<ConfigurationClass>(configCandidates.size());
        do {
            parser.parse(candidates);
            parser.validate();

            Set<ConfigurationClass> configClasses = new LinkedHashSet<ConfigurationClass>(parser.getConfigurationClasses());
            
```

主要的代码是 parser.parse(candidates); 这句，当执行到这里的时候candidates已经包含Springboot的配置类，这个parse会根据配置类里定义的basePackge递归扫描这个目录下面的class文件（如果没有定义basePackage字段则把配置类所在的包作为basePackage），debug跟踪代码到最底层可以看到使用的是PathMatchingResourcePatternResolver的doRetrieveMatchingFiles方法：

```java

protected void doRetrieveMatchingFiles(String fullPattern, File dir, Set<File> result) throws IOException {
   if (logger.isDebugEnabled()) {
      logger.debug("Searching directory [" + dir.getAbsolutePath() +
            "] for files matching pattern [" + fullPattern + "]");
   }
   File[] dirContents = dir.listFiles();
   if (dirContents == null) {
      if (logger.isWarnEnabled()) {
         logger.warn("Could not retrieve contents of directory [" + dir.getAbsolutePath() + "]");
      }
      return;
   }
   Arrays.sort(dirContents);
   for (File content : dirContents) {
      String currPath = StringUtils.replace(content.getAbsolutePath(), File.separator, "/");
      if (content.isDirectory() && getPathMatcher().matchStart(fullPattern, currPath + "/")) {
         if (!content.canRead()) {
            if (logger.isDebugEnabled()) {
               logger.debug("Skipping subdirectory [" + dir.getAbsolutePath() +
                     "] because the application is not allowed to read the directory");
            }
         }
         else {
            doRetrieveMatchingFiles(fullPattern, content, result);
         }
      }
      if (getPathMatcher().match(fullPattern, currPath)) {
         result.add(content);
      }
   }
}
 
```



在这里可以看到有句 Arrays.sort(dirContents); 这个代码，就是在遍历一个文件夹下的资源时（包括文件夹和class文件），会先把资源排序一下，这个排序决定了bean的加载顺序！

那再看下File（上面代码中的dirContents是File列表）是如何排序的：

```
public class File
    implements Serializable, Comparable<File>
{

    /**
     * The FileSystem object representing the platform's local file system.
     */
    private static final FileSystem fs = DefaultFileSystem.getFileSystem();


    /* -- Basic infrastructure -- */

    /**
     * Compares two abstract pathnames lexicographically.  The ordering
     * defined by this method depends upon the underlying system.  On UNIX
     * systems, alphabetic case is significant in comparing pathnames; on Microsoft Windows
     * systems it is not.
     *
     * @param   pathname  The abstract pathname to be compared to this abstract
     *                    pathname
     *
     * @return  Zero if the argument is equal to this abstract pathname, a
     *          value less than zero if this abstract pathname is
     *          lexicographically less than the argument, or a value greater
     *          than zero if this abstract pathname is lexicographically
     *          greater than the argument
     *
     * @since   1.2
     */
    public int compareTo(File pathname) {
        return fs.compare(this, pathname);
    }
```



使用的是FileSystem的排序方法，再看看DefaultFileSystem.getFileSystem();拿到是是什么：

mac下：

```
/**
 *
 * @since 1.8
 */
class DefaultFileSystem {

    /**
     * Return the FileSystem object for Unix-based platform.
     */
    public static FileSystem getFileSystem() {
        return new UnixFileSystem();
    }
}
// UnixFileSystem

class UnixFileSystem extends FileSystem {
/* -- Basic infrastructure -- */
　　public int compare(File f1, File f2) {
        　　　　return f1.getPath().compareTo(f2.getPath());
    　　}
}
```



windows下：

```
/**
 *
 * @since 1.8
 */
class DefaultFileSystem {

    /**
     * Return the FileSystem object for Windows platform.
     */
    public static FileSystem getFileSystem() {
        return new WinNTFileSystem();
    }
}
//WinNTFileSystem
 @Override
    public int compare(File f1, File f2) {
        　　return f1.getPath().compareToIgnoreCase(f2.getPath());
    }
```



由于windows下和mac下使用的FileSystem不同，jdk windows版FileSystem实现的compare方法在比较文件是忽略了文件名的大小写，而mac版没有忽略大小写，所以导致前面提出的同样的代码在windows下报错，在mac下就是正常的问题。

但是为什么会有这样的差别呢？不是太明白为什么

## 解决

那还剩最后一个问题，这个问题如何解决呢？我这有两个方法，

### 方式一

一是修改类名让被依赖的类排在前面，这种方法不是太优雅，而且如果以后jdk更新了排序方法可能还会出bug，

### 方式二(推荐)

第二种是在使用类上加DependOn注解，主动说明在初始化使用类时首先加载被依赖的类，这样就没有问题了，但是我感觉在开发的时候尽量避免这种依赖问题，这让容器和业务代码参杂，以后维护是个噩梦



<https://www.cnblogs.com/caiyao/p/10096263.html>