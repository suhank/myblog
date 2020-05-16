[TOC]



# spring 资源查找 ResourceLoader接口 DefaultResourceLoader PathMatchingResourcePatternResolver

2018年01月23日 20:00:20 [汪小哥](https://me.csdn.net/u012881904) 阅读数 410

 

ResourceLoader接口用于返回Resource对象；其实现可以看作是一个生产Resource的工厂类。ResourceLoader接口只提供了classpath前缀的支持。getResource接口用于根据提供的location参数返回相应的Resource对象, Spring提供了一个适用于所有环境的DefaultResourceLoader实现，可以返回ClassPathResource、UrlResource；还提供一个用于web环境的ServletContextResourceLoader，它继承了DefaultResourceLoader的所有功能，又额外提供了获取ServletContextResource的支持。 

```java
/**
 * Strategy interface for loading resources (e.. class path or file system 
 * resources). An {@link org.springframework.context.ApplicationContext}
 * is required to provide this functionality, plus extended 
 * {@link org.springframework.core.io.support.ResourcePatternResolver} support. 
 * @see org.springframework.core.io.support.ResourcePatternResolver
 * @see org.springframework.context.ApplicationContext
 * @see org.springframework.context.ResourceLoaderAware
 */
public interface ResourceLoader {

    /** Pseudo URL prefix for loading from the class path: "classpath:" */
    String CLASSPATH_URL_PREFIX = ResourceUtils.CLASSPATH_URL_PREFIX;


    /**
     * <li>Must support fully qualified URLs, e.g. "file:C:/test.dat".
     * <li>Must support classpath pseudo-URLs, e.g. "classpath:test.dat".
     * <li>Should support relative file paths, e.g. "WEB-INF/test.dat".
     * @see org.springframework.core.io.Resource#exists
     * @see org.springframework.core.io.Resource#getInputStream
     */
    Resource getResource(String location); //抽象化处理不同的资源信息

    /**
     * Expose the ClassLoader used by this ResourceLoader.
     * @see org.springframework.util.ClassUtils#getDefaultClassLoader()
     */
    ClassLoader getClassLoader();

}  
```



## ResourcePatternResolver

提供了classpath*的前缀支持,可以通过模式匹配找到所有的Resource

```java
public interface ResourcePatternResolver extends ResourceLoader {

    /**
     * Pseudo URL prefix for all matching resources from the class path: "classpath*:"
     * This differs from ResourceLoader's classpath URL prefix in that it
     * retrieves all matching resources for a given name (e.g. "/beans.xml"),
     * for example in the root of all deployed JAR files.
     * @see org.springframework.core.io.ResourceLoader#CLASSPATH_URL_PREFIX
     */
    String CLASSPATH_ALL_URL_PREFIX = "classpath*:";

    /**
     * Resolve the given location pattern into Resource objects.
     * <p>Overlapping resource entries that point to the same physical
     * resource should be avoided, as far as possible. The result should
     * have set semantics.
     * @param locationPattern the location pattern to resolve
     * @return the corresponding Resource objects
     * @throws IOException in case of I/O errors
     */
    Resource[] getResources(String locationPattern) throws IOException;

}
```

通过2个接口的源码对比，我们发现ResourceLoader提供 classpath下单资源文件的载入，而ResourcePatternResolver提供了多资源文件的载入。 
单资源文件的读取,DefaultResourceLoader默认的实现，其他的ApplicationContext也是通过这个类实现的。 
Will return a {@link UrlResource} if the location value is a URL,and a {@link ClassPathResource} if it is a non-URL path or a * classpath。这里对于不同的资源类型产生不同的Resource资源的实现类型，具体参考源码。做了一个简单的区分，可以参考例子

```java
@Override
    public Resource getResource(String location) {
        Assert.notNull(location, "Location must not be null");
        if (location.startsWith("/")) {
            return getResourceByPath(location);
        }
        else if (location.startsWith(CLASSPATH_URL_PREFIX)) {
            return new ClassPathResource(location.substring(CLASSPATH_URL_PREFIX.length()), getClassLoader());
        }
        else {
            try {
                // Try to parse the location as a URL...
                URL url = new URL(location);
                return new UrlResource(url);
            }
            catch (MalformedURLException ex) {
                // No URL -> resolve as resource path.
                return getResourceByPath(location);
            }
        }
    }
```

使用

```java
 @Test
    public void DefalutResourceLoader()  throws Exception{
        ResourceLoader loader = new DefaultResourceLoader();
        Resource setup = loader.getResource("classpath:test/log4j.properties");
        //E:\Demo\.....\target\classes\test\log4j.properties
        System.out.println(setup.getFile());//ClassPathResource

        Resource txt = loader.getResource("file://D:/text.txt");
        //D:\text.txt
        System.out.println(txt.getFile()); // UrlResource

        Resource xsd = loader.getResource("http://www.springframework.org/schema/beans/spring-beans-4.1.xsd");
        System.out.println(xsd.getURI());// UrlResource
        // http://www.springframework.org/schema/beans/spring-beans-4.1.xsd
        System.out.println(xsd.getInputStream());
        //sun.net.www.protocol.http.HttpURLConnection$HttpInputStream@41975e01

    }
```

ResourcePatternResolver 使用这个实现PathMatchingResourcePatternResolver，通过AntPathMatcher赋予模式匹配的实现。 
[spring resourceclassloader](http://www.cnblogs.com/doit8791/p/5774743.html) 
\1. 处理的思路,当前是否有classpath*,如果没有，看看匹配路径中是否有 ( * 或者 ? 等匹配模式 )，有选择所有的就好了，没有直接使用默认的DefaultResourceLoader进行加载。 
\2. 如果含有classpath*,根据匹配选出没有模式匹配的一个顶级的根路径 (classpath **:/META-INF/ **.txt)—>(classpath *:/META-INF/),然后根据这个路径去查找所有的这样的文件夹.

```java
public Resource[] getResources(String locationPattern) throws IOException {
        // CLASSPATH_ALL_URL_PREFIX = "classpath*:";
        if (locationPattern.startsWith(CLASSPATH_ALL_URL_PREFIX)) {
            // a class path resource (multiple resources for same name possible)
            // 去掉classpath*:后面还有通配符
            if (getPathMatcher().isPattern(locationPattern.substring(CLASSPATH_ALL_URL_PREFIX.length()))) {
                // a class path resource pattern
                return findPathMatchingResources(locationPattern);
            }
            else {
                 //这里没有通配符了
                // all class path resources with the given name
                return findAllClassPathResources(locationPattern.substring(CLASSPATH_ALL_URL_PREFIX.length()));
            }
        }
        else {
            // Only look for a pattern after a prefix here
            // (to not get fooled by a pattern symbol in a strange prefix).
            int prefixEnd = locationPattern.indexOf(":") + 1;
            //没有classpath*,但是后面又通配符的情况的处理
            if (getPathMatcher().isPattern(locationPattern.substring(prefixEnd))) {
                // a file pattern
                return findPathMatchingResources(locationPattern);
            }
            else {
                // a single resource with the given name
                //只有一个匹配，使用默认的去寻找就行了。
                return new Resource[] {getResourceLoader().getResource(locationPattern)};
            }
        }
    }
```

### 以classPath*开头没有通配符

（classpath*:/META-INF/notice.txt）

```java
protected Resource[] findAllClassPathResources(String location) throws IOException {
        String path = location;
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        ClassLoader cl = getClassLoader();
        //cl.getResources 找到所有的Classpath下面的文件中包含这个的
        Enumeration<URL> resourceUrls = (cl != null ? cl.getResources(path) : ClassLoader.getSystemResources(path));
        Set<Resource> result = new LinkedHashSet<Resource>(16);
        while (resourceUrls.hasMoreElements()) {
            URL url = resourceUrls.nextElement();
            result.add(convertClassLoaderURL(url));
        }
        return result.toArray(new Resource[result.size()]);
    }

protected Resource convertClassLoaderURL(URL url) {
        return new UrlResource(url);// 转换为 UrlResource
    }
 public ClassLoader getClassLoader() {  
    return getResourceLoader().getClassLoader();  
}  


public ResourceLoader getResourceLoader() {  
    return this.resourceLoader;  
}  

//默认情况下  
public PathMatchingResourcePatternResolver() {  
    this.resourceLoader = new DefaultResourceLoader();  
}  
```

最关键的代码在于ClassLoader的getResources()方法。

```java
public Enumeration<URL> getResources(String name) throws IOException {
        @SuppressWarnings("unchecked")
        Enumeration<URL>[] tmp = (Enumeration<URL>[]) new Enumeration< ?>[2];
        if (parent != null) {
            tmp[0] = parent.getResources(name);
        } else {
            tmp[0] = getBootstrapResources(name);
        }
        tmp[1] = findResources(name);

        return new CompoundEnumeration<>(tmp);
}
当前类加载器，如果存在父加载器，则向上迭代获取资源， 因此能加到 jar包里面的资源文件 
```

### 不以classpath*开头，且路径不包含通配符的

```
return new Resource[] {getResourceLoader().getResource(locationPattern)};   
```

### 路径包含通配符的

这种情况是最复杂的，涉及到层层递归,其实主要的思想就是 
1.先获取目录，加载目录里面的所有资源 
2.在所有资源里面进行查找匹配，找出我们需要的资源

```java
protected Resource[] findPathMatchingResources(String locationPattern) throws IOException {
        //确定没有模式匹配的根路径
        String rootDirPath = determineRootDir(locationPattern);
        //去掉根路径后的余下的需要匹配的
        String subPattern = locationPattern.substring(rootDirPath.length());
        //找到根路径下的所有的资源，递归的
        Resource[] rootDirResources = getResources(rootDirPath);
        Set<Resource> result = new LinkedHashSet<Resource>(16);
        for (Resource rootDirResource : rootDirResources) {
            rootDirResource = resolveRootDirResource(rootDirResource);
            if (isJarResource(rootDirResource)) { // 如果是JAR文件遍历里面的
                //可以参考 http://blog.csdn.net/u012881904/article/details/79140744
                result.addAll(doFindPathMatchingJarResources(rootDirResource, subPattern));
            }
            else if (rootDirResource.getURL().getProtocol().startsWith(ResourceUtils.URL_PROTOCOL_VFS)) { // 虚拟文件这里进行处理
                result.addAll(VfsResourceMatchingDelegate.findMatchingResources(rootDirResource, subPattern, getPathMatcher()));
            }
            else {
                // 如果是文件夹遍历文件夹下面的文件
                result.addAll(doFindPathMatchingFileResources(rootDirResource, subPattern));
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Resolved location pattern [" + locationPattern + "] to resources " + result);
        }
        return result.toArray(new Resource[result.size()]);
    } 
```

使用参考

```java
    @Test
    public void PathMatchingResourcePatternResolver()  throws Exception{
        ResourcePatternResolver loader = new PathMatchingResourcePatternResolver();
        Resource[] resources = loader.getResources("classpath*:/META-INF/notice.txt");
        for(int i=0;i< resources.length;i++) {
            System.out.println(resources[i].getURL().getFile());
        }
    }
    @Test
    public void PathMatchingResourcePatternResolver1()  throws Exception{
        ResourcePatternResolver loader = new PathMatchingResourcePatternResolver();
        Resource[] resources = loader.getResources("classpath:/META-INF/*.txt");
        for(int i=0;i< resources.length;i++) {
            System.out.println(resources[i].getURL().getFile());
        }
    }
```





<https://blog.csdn.net/u012881904/article/details/79143932>