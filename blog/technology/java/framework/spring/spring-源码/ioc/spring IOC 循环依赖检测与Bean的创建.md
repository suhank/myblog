[TOC]



# spring IOC 循环依赖检测与Bean的创建

### Spring容器的循环依赖检测

> Spring容器循环依赖包括：构造器循环依赖和setter循环依赖。

##### 1- 构造器循环依赖

通过构造器注入构成的循环依赖，此依赖是无法解决的，只能抛出BeanCurrentlyInCreationException异常表示循环依赖

描述：创建A类是，构造器需要B类，那将去创建B，在创建B时又发现需要C类，则又去创建C类，最终在创建C时发现又需要A，从而形成一个环，没办法创建。

原理：Spring容器将每一个正在创建的bean标识符放在一个“当前创建bean池”中，bean标识符创建过程中将一直保持在这个池中，因为如果在创建bean过程中发现自己已经在“当前创建bean池”中时，将会抛出BeanCurrentlyInCreationException异常表示循环依赖；而对于创建完毕的bean将从“当前创建bean池”中清除掉。

**过程分析(构造器依赖 A -> B -> C -> A)：**

1. Spring容器创建A bean，首先去 “当前创建bean池”中查找是否当前bean正在创建（通过beanName），如果发现没有，则继续准备其需要的构造器参数B，并将A标识符放到“当前创建bean池”
2. Spring容器创建B bean，Spring容器创建B bean，首先去 “当前创建bean池”中查找是否当前bean正在创建（通过beanName），如果发现没有，则继续准备其需要的构造器参数C，并将B标识符放到“当前创建bean池”
3. Spring容器创建C bean，Spring容器创建C bean，首先去 “当前创建bean池”中查找是否当前bean正在创建（通过beanName），如果发现没有，则继续准备其需要的构造器参数A，并将C标识符放到“当前创建bean池”
4. C的创建需要先创建A bean，此时发现A已经在“当前创建bean池”中，检测到了循环依赖，直接抛出BeanCurrentlyInCreationException异常

##### 2- setter循环依赖

通过setter注入方式构成的循环依赖。

原理：对于setter注入造成的依赖是通过Spring容器提前暴露刚完成构造器注入但未完成其他步骤（比如setter注入）的bean来完成的，而且只能解决单例作用域的bean循环依赖。

通过提前暴露一个单例工厂方法，从而使其他bean能引用到该bean addSingletonFactory()方法

**步骤（setter依赖关系 A -> B -> C -> A）**

1. Spring容器创建单例A的bean，首先根据无参构造器创建bean，并暴露一个“ObjectFactory”用于返回一个提前暴露一个创建中的bean，并将A标识符放到“当前创建bean池”，然后进行setter注入 B bean
2. Spring容器创建单例B bean，首先根据无参构造器创建bean，并暴露一个“ObjectFactory”用于返回一个提前暴露一个创建中的bean，并将B标识符放到“当前创建bean池”，然后进行setter注入 C bean
3. Spring容器创建单例C bean，首先根据无参构造器创建bean，并暴露一个“ObjectFactory”用于返回一个提前暴露一个创建中的bean，并将C标识符放到“当前创建bean池”，然后进行setter注入 A bean，在进行注入A bean 时由于提前暴露了“ObjectFactory”工厂，从而使用它返回提前暴露一个创建中的bean
4. 最后从3中循环结束，依次返回完成对 B bean、A bean的setter注入

##### 3- prototype范围的依赖处理

1. 对于scope为prototype范围的bean，Spring容器无法完成依赖注入，因为Spring容器不进行缓存“prototype”作用域的bean，因此无法提前暴露一个创建中的bean，所以检测到循环依赖会直接抛出BeanCurrentlyInCreationException异常
2. 对于单例作用域的bean，可以通过“setAllowCircularReferences(false)”来禁用循环引用，这样如果存在循环引用就会抛出异常来通知用户。

### doCreateBean方法



程序有两个选择：

1. 如果创建了代理或者重写了InstantiationAwareBeanPostProcessor的postProcessBeforeInstantiation方法并在方法postPorcessBeforeInstantiation中改变了bean，则直接返回就可以了。
2. 否则需要进行常规bean的创建，常规创建就是doCreateBean方法中完成的。

```java
    /**
     * Actually create the specified bean. Pre-creation processing has already happened
     * at this point, e.g. checking {@code postProcessBeforeInstantiation} callbacks.
     * <p>Differentiates between default bean instantiation, use of a
     * factory method, and autowiring a constructor.
     * @param beanName the name of the bean
     * @param mbd the merged bean definition for the bean
     * @param args explicit arguments to use for constructor or factory method invocation
     * @return a new instance of the bean
     * @throws BeanCreationException if the bean could not be created
     * @see #instantiateBean
     * @see #instantiateUsingFactoryMethod
     * @see #autowireConstructor
     */
    protected Object doCreateBean(final String beanName, final RootBeanDefinition mbd, final Object[] args) {
        // Instantiate the bean.
        BeanWrapper instanceWrapper = null;
        if (mbd.isSingleton()) {
            instanceWrapper = this.factoryBeanInstanceCache.remove(beanName);
        }
        if (instanceWrapper == null) {
            instanceWrapper = createBeanInstance(beanName, mbd, args);
        }
        final Object bean = (instanceWrapper != null ? instanceWrapper.getWrappedInstance() : null);
        Class<?> beanType = (instanceWrapper != null ? instanceWrapper.getWrappedClass() : null);

        // Allow post-processors to modify the merged bean definition.
        synchronized (mbd.postProcessingLock) {
            if (!mbd.postProcessed) {
                applyMergedBeanDefinitionPostProcessors(mbd, beanType, beanName);
                mbd.postProcessed = true;
            }
        }

        // Eagerly cache singletons to be able to resolve circular references
        // even when triggered by lifecycle interfaces like BeanFactoryAware.
        boolean earlySingletonExposure = (mbd.isSingleton() && this.allowCircularReferences &&
                isSingletonCurrentlyInCreation(beanName));
        if (earlySingletonExposure) {
            if (logger.isDebugEnabled()) {
                logger.debug("Eagerly caching bean '" + beanName +
                        "' to allow for resolving potential circular references");
            }
            addSingletonFactory(beanName, new ObjectFactory<Object>() {
                @Override
                public Object getObject() throws BeansException {
                    return getEarlyBeanReference(beanName, mbd, bean);
                }
            });
        }

        // Initialize the bean instance.
        Object exposedObject = bean;
        try {
            populateBean(beanName, mbd, instanceWrapper);
            if (exposedObject != null) {
                exposedObject = initializeBean(beanName, exposedObject, mbd);
            }
        }
        catch (Throwable ex) {
            if (ex instanceof BeanCreationException && beanName.equals(((BeanCreationException) ex).getBeanName())) {
                throw (BeanCreationException) ex;
            }
            else {
                throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Initialization of bean failed", ex);
            }
        }

        if (earlySingletonExposure) {
            Object earlySingletonReference = getSingleton(beanName, false);
            if (earlySingletonReference != null) {
                if (exposedObject == bean) {
                    exposedObject = earlySingletonReference;
                }
                else if (!this.allowRawInjectionDespiteWrapping && hasDependentBean(beanName)) {
                    String[] dependentBeans = getDependentBeans(beanName);
                    Set<String> actualDependentBeans = new LinkedHashSet<String>(dependentBeans.length);
                    for (String dependentBean : dependentBeans) {
                        if (!removeSingletonIfCreatedForTypeCheckOnly(dependentBean)) {
                            actualDependentBeans.add(dependentBean);
                        }
                    }
                    if (!actualDependentBeans.isEmpty()) {
                        throw new BeanCurrentlyInCreationException(beanName,
                                "Bean with name '" + beanName + "' has been injected into other beans [" +
                                StringUtils.collectionToCommaDelimitedString(actualDependentBeans) +
                                "] in its raw version as part of a circular reference, but has eventually been " +
                                "wrapped. This means that said other beans do not use the final version of the " +
                                "bean. This is often the result of over-eager type matching - consider using " +
                                "'getBeanNamesOfType' with the 'allowEagerInit' flag turned off, for example.");
                    }
                }
            }
        }

        // Register bean as disposable.
        try {
            registerDisposableBeanIfNecessary(beanName, bean, mbd);
        }
        catch (BeanDefinitionValidationException ex) {
            throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Invalid destruction signature", ex);
        }

        return exposedObject;
    }
```

**主要步骤：**

1. 如果是单例则需要首先清除缓存

2. 实例化bean，将BeanDefinition转换为BeanWrapper

   > 转换是一个复杂的过程，但是我们可以尝试概括大致的功能
   >
   > 1. 如果存在工厂方法则使用工厂方法进行初始化
   > 2. 一个类有多个构造函数，每个构造函数都有不同的参数，所以需要根据参数锁定构造函数并进行初始化
   > 3. 如果既不存在工厂方法也不存在带有参数的构造函数，则使用默认的构造函数进行bean的实例化

3. MergedBeanDefinitionPostProcessor的应用

   > bean合并后的处理，Autowired注解正是通过此方法实现诸如类型的预解析。

4. 依赖处理：用 ObjectFactory来解决单例的setter循环依赖的问题

5. 属性填充，将所有属性填充至bean的实例中

6. 循环依赖检查，对于已加载的bean，检测是否已经出现了循环依赖，并判断是否需要抛出异常。

7. 注册DisposableBean：如果配置了destroy-method，这里需要注册以便在销毁时调用

8. 完成创建并返回

下面主要讲解步骤2,4,5,6,7

##### 步骤2- 实例化bean

1. 如果在RootBeanDefinition中存在factoryMethodName属性，或者说在配置文件中配置了factory-method，nameSpring会尝试使用instantiateUsingFactoryMethod()方法根据RootBeanDefinition中的配置生成bean的实例。
2. 解析构造函数并进行构造函数的实例化。因为一个bean对应的类中可能会有多个构造函数，而每一个构造函数参数不同，需要Spring去解析，为了提高性能，每次解析的结果都缓存下来，存放在RootBeanDefinition中的属性ResolvedConstructorOrFactoryMethod中，当下次判断时直接从缓存中查找，如果没有解析则会去解析。

实例的创建分为两种：通用的实例化、带有参数的实例化

```
    /**
     * Create a new instance for the specified bean, using an appropriate instantiation strategy:
     * factory method, constructor autowiring, or simple instantiation.
     * @param beanName the name of the bean
     * @param mbd the bean definition for the bean
     * @param args explicit arguments to use for constructor or factory method invocation
     * @return BeanWrapper for the new instance
     * @see #instantiateUsingFactoryMethod
     * @see #autowireConstructor
     * @see #instantiateBean
     */
    protected BeanWrapper createBeanInstance(String beanName, RootBeanDefinition mbd, Object[] args) {
        // Make sure bean class is actually resolved at this point.
        Class<?> beanClass = resolveBeanClass(mbd, beanName);

        if (beanClass != null && !Modifier.isPublic(beanClass.getModifiers()) && !mbd.isNonPublicAccessAllowed()) {
            throw new BeanCreationException(mbd.getResourceDescription(), beanName,
                    "Bean class isn't public, and non-public access not allowed: " + beanClass.getName());
        }

        if (mbd.getFactoryMethodName() != null)  {
            return instantiateUsingFactoryMethod(beanName, mbd, args);
        }

        // Shortcut when re-creating the same bean...
        boolean resolved = false;
        boolean autowireNecessary = false;
        if (args == null) {
            synchronized (mbd.constructorArgumentLock) {
                if (mbd.resolvedConstructorOrFactoryMethod != null) {
                    resolved = true;
                    autowireNecessary = mbd.constructorArgumentsResolved;
                }
            }
        }
        if (resolved) {
            if (autowireNecessary) {
                return autowireConstructor(beanName, mbd, null, null);
            }
            else {
                return instantiateBean(beanName, mbd);
            }
        }

        // Need to determine the constructor...
        Constructor<?>[] ctors = determineConstructorsFromBeanPostProcessors(beanClass, beanName);
        if (ctors != null ||
                mbd.getResolvedAutowireMode() == RootBeanDefinition.AUTOWIRE_CONSTRUCTOR ||
                mbd.hasConstructorArgumentValues() || !ObjectUtils.isEmpty(args))  {
            return autowireConstructor(beanName, mbd, ctors, args);
        }

        // No special handling: simply use no-arg constructor.
        return instantiateBean(beanName, mbd);
    }
```

**1- 带有参数的实例化**

> 相当复杂，因为存在不确定性，所以在判断对应参数上做了大量工作。

autowireConstructor方法详解

```
1. 带有参数的bean的实例化
beans.factory.support.ConstructorResolver
    /**
     * "autowire constructor" (with constructor arguments by type) behavior.
     * Also applied if explicit constructor argument values are specified,
     * matching all remaining arguments with beans from the bean factory.
     * <p>This corresponds to constructor injection: In this mode, a Spring
     * bean factory is able to host components that expect constructor-based
     * dependency resolution.
     * @param beanName the name of the bean
     * @param mbd the merged bean definition for the bean
     * @param chosenCtors chosen candidate constructors (or {@code null} if none)
     * @param explicitArgs argument values passed in programmatically via the getBean method,
     * or {@code null} if none (-> use constructor argument values from bean definition)
     * @return a BeanWrapper for the new instance
     */
    public BeanWrapper autowireConstructor(final String beanName, final RootBeanDefinition mbd,
            Constructor<?>[] chosenCtors, final Object[] explicitArgs) {

        BeanWrapperImpl bw = new BeanWrapperImpl();
        this.beanFactory.initBeanWrapper(bw);

        Constructor<?> constructorToUse = null;
        ArgumentsHolder argsHolderToUse = null;
        Object[] argsToUse = null;

        if (explicitArgs != null) {
            argsToUse = explicitArgs;
        }
        else {
            Object[] argsToResolve = null;
            synchronized (mbd.constructorArgumentLock) {
                constructorToUse = (Constructor<?>) mbd.resolvedConstructorOrFactoryMethod;
                if (constructorToUse != null && mbd.constructorArgumentsResolved) {
                    // Found a cached constructor...
                    argsToUse = mbd.resolvedConstructorArguments;
                    if (argsToUse == null) {
                        argsToResolve = mbd.preparedConstructorArguments;
                    }
                }
            }
            if (argsToResolve != null) {
                argsToUse = resolvePreparedArguments(beanName, mbd, bw, constructorToUse, argsToResolve);
            }
        }

        if (constructorToUse == null) {
            // Need to resolve the constructor.
            boolean autowiring = (chosenCtors != null ||
                    mbd.getResolvedAutowireMode() == RootBeanDefinition.AUTOWIRE_CONSTRUCTOR);
            ConstructorArgumentValues resolvedValues = null;

            int minNrOfArgs;
            if (explicitArgs != null) {
                minNrOfArgs = explicitArgs.length;
            }
            else {
                ConstructorArgumentValues cargs = mbd.getConstructorArgumentValues();
                resolvedValues = new ConstructorArgumentValues();
                minNrOfArgs = resolveConstructorArguments(beanName, mbd, bw, cargs, resolvedValues);
            }

            // Take specified constructors, if any.
            Constructor<?>[] candidates = chosenCtors;
            if (candidates == null) {
                Class<?> beanClass = mbd.getBeanClass();
                try {
                    candidates = (mbd.isNonPublicAccessAllowed() ?
                            beanClass.getDeclaredConstructors() : beanClass.getConstructors());
                }
                catch (Throwable ex) {
                    throw new BeanCreationException(mbd.getResourceDescription(), beanName,
                            "Resolution of declared constructors on bean Class [" + beanClass.getName() +
                            "] from ClassLoader [" + beanClass.getClassLoader() + "] failed", ex);
                }
            }
            AutowireUtils.sortConstructors(candidates);
            int minTypeDiffWeight = Integer.MAX_VALUE;
            Set<Constructor<?>> ambiguousConstructors = null;
            LinkedList<UnsatisfiedDependencyException> causes = null;

            for (Constructor<?> candidate : candidates) {
                Class<?>[] paramTypes = candidate.getParameterTypes();

                if (constructorToUse != null && argsToUse.length > paramTypes.length) {
                    // Already found greedy constructor that can be satisfied ->
                    // do not look any further, there are only less greedy constructors left.
                    break;
                }
                if (paramTypes.length < minNrOfArgs) {
                    continue;
                }

                ArgumentsHolder argsHolder;
                if (resolvedValues != null) {
                    try {
                        String[] paramNames = ConstructorPropertiesChecker.evaluate(candidate, paramTypes.length);
                        if (paramNames == null) {
                            ParameterNameDiscoverer pnd = this.beanFactory.getParameterNameDiscoverer();
                            if (pnd != null) {
                                paramNames = pnd.getParameterNames(candidate);
                            }
                        }
                        argsHolder = createArgumentArray(beanName, mbd, resolvedValues, bw, paramTypes, paramNames,
                                getUserDeclaredConstructor(candidate), autowiring);
                    }
                    catch (UnsatisfiedDependencyException ex) {
                        if (this.beanFactory.logger.isTraceEnabled()) {
                            this.beanFactory.logger.trace(
                                    "Ignoring constructor [" + candidate + "] of bean '" + beanName + "': " + ex);
                        }
                        // Swallow and try next constructor.
                        if (causes == null) {
                            causes = new LinkedList<UnsatisfiedDependencyException>();
                        }
                        causes.add(ex);
                        continue;
                    }
                }
                else {
                    // Explicit arguments given -> arguments length must match exactly.
                    if (paramTypes.length != explicitArgs.length) {
                        continue;
                    }
                    argsHolder = new ArgumentsHolder(explicitArgs);
                }

                int typeDiffWeight = (mbd.isLenientConstructorResolution() ?
                        argsHolder.getTypeDifferenceWeight(paramTypes) : argsHolder.getAssignabilityWeight(paramTypes));
                // Choose this constructor if it represents the closest match.
                if (typeDiffWeight < minTypeDiffWeight) {
                    constructorToUse = candidate;
                    argsHolderToUse = argsHolder;
                    argsToUse = argsHolder.arguments;
                    minTypeDiffWeight = typeDiffWeight;
                    ambiguousConstructors = null;
                }
                else if (constructorToUse != null && typeDiffWeight == minTypeDiffWeight) {
                    if (ambiguousConstructors == null) {
                        ambiguousConstructors = new LinkedHashSet<Constructor<?>>();
                        ambiguousConstructors.add(constructorToUse);
                    }
                    ambiguousConstructors.add(candidate);
                }
            }

            if (constructorToUse == null) {
                if (causes != null) {
                    UnsatisfiedDependencyException ex = causes.removeLast();
                    for (Exception cause : causes) {
                        this.beanFactory.onSuppressedException(cause);
                    }
                    throw ex;
                }
                throw new BeanCreationException(mbd.getResourceDescription(), beanName,
                        "Could not resolve matching constructor " +
                        "(hint: specify index/type/name arguments for simple parameters to avoid type ambiguities)");
            }
            else if (ambiguousConstructors != null && !mbd.isLenientConstructorResolution()) {
                throw new BeanCreationException(mbd.getResourceDescription(), beanName,
                        "Ambiguous constructor matches found in bean '" + beanName + "' " +
                        "(hint: specify index/type/name arguments for simple parameters to avoid type ambiguities): " +
                        ambiguousConstructors);
            }

            if (explicitArgs == null) {
                argsHolderToUse.storeCache(mbd, constructorToUse);
            }
        }

        try {
            Object beanInstance;

            if (System.getSecurityManager() != null) {
                final Constructor<?> ctorToUse = constructorToUse;
                final Object[] argumentsToUse = argsToUse;
                beanInstance = AccessController.doPrivileged(new PrivilegedAction<Object>() {
                    @Override
                    public Object run() {
                        return beanFactory.getInstantiationStrategy().instantiate(
                                mbd, beanName, beanFactory, ctorToUse, argumentsToUse);
                    }
                }, beanFactory.getAccessControlContext());
            }
            else {
                beanInstance = this.beanFactory.getInstantiationStrategy().instantiate(
                        mbd, beanName, this.beanFactory, constructorToUse, argsToUse);
            }

            bw.setBeanInstance(beanInstance);
            return bw;
        }
        catch (Throwable ex) {
            throw new BeanCreationException(mbd.getResourceDescription(), beanName,
                    "Bean instantiation via constructor failed", ex);
        }
    }
```

步骤

1. 构造函数参数的确定

> 1. 根据explicitArgs参数判断
>    在获取bean的时候（调用getBean()方法），用户不但可以指定bean的名称还可以指定bean所对应类的构造函数或者工厂方法的方法参数，主要用于静态工厂方法的调用，而这里是需要给定完全匹配的参数的，因此explicitArgs不为空，则可以确定对应的构造函数
> 2. 缓存中获取解析过的方法匹配
>    构造函数参数已经记录在缓存中，则可以直接拿来使用，但是需要注意的是缓存中的参数可能是参数的最终类型也可能是参数的初始类型，如String类型的“1”表示的int类型数据，此时需要经过类型转换器的过滤来确保参数类型与对应的构造函数参数类型完全对应。
> 3. 配置文件读取
>    即不能从1、2中获取到信息来确定构造函数的参数，则从bean配置加载转化为BeanDefinition实例的getConstructorArgumentValues()来获取配置的构造函数信息

1. 构造函数的确定

> 根据1中确定的参数锁定对应的构造函数，匹配的方法就是根据参数个数匹配，所以在匹配之前需要对构造器的可见性以及参数数量进行排序，之后就能快速判定是哪个构造函数了，对于没有限制参数位置索引的方式而是指定参数名称进行参数设定的情况，就需要首先确定构造函数中的参数名称。获取参数名称可以通过注解的方式直接获取，一种是使用工具类ParameterNameDiscoverer来获取

1. 根据确定的构造函数转换对应的参数类型，主要是使用Spring中提供的类型转换器或者用户提供的自定义类型转换器进行转换
2. 构造函数不确定性的验证，对不同构造函数的参数类型的父子关系，进行最后一次验证，这个步骤之后才能真正的锁定一个构造函数
3. 根据实例化策略以及得到的构造函数以及构造函数参数实例化bean

**2- 通用不带参数的实例化**

```
2. 不带参数实例化
beans.factory.support.AbstractAutowireCapableBeanFactory
    /**
     * Instantiate the given bean using its default constructor.
     * @param beanName the name of the bean
     * @param mbd the bean definition for the bean
     * @return BeanWrapper for the new instance
     */
    protected BeanWrapper instantiateBean(final String beanName, final RootBeanDefinition mbd) {
        try {
            Object beanInstance;
            final BeanFactory parent = this;
            if (System.getSecurityManager() != null) {
                beanInstance = AccessController.doPrivileged(new PrivilegedAction<Object>() {
                    @Override
                    public Object run() {
                        return getInstantiationStrategy().instantiate(mbd, beanName, parent);
                    }
                }, getAccessControlContext());
            }
            else {
                beanInstance = getInstantiationStrategy().instantiate(mbd, beanName, parent);
            }
            BeanWrapper bw = new BeanWrapperImpl(beanInstance);
            initBeanWrapper(bw);
            return bw;
        }
        catch (Throwable ex) {
            throw new BeanCreationException(
                    mbd.getResourceDescription(), beanName, "Instantiation of bean failed", ex);
        }
    }
```

instantiateBean方法完成不带参数的实例化，逻辑比较简单，直接调用实例化策略进行实例化，接下来介绍实例化策略。

**3- 实例化策略**

```
3 实例化策略
beans.factory.support.SimpleInstantiationStrategy

    @Override
    public Object instantiate(RootBeanDefinition bd, String beanName, BeanFactory owner) {
        // Don't override the class with CGLIB if no overrides.
        if (bd.getMethodOverrides().isEmpty()) {
            Constructor<?> constructorToUse;
            synchronized (bd.constructorArgumentLock) {
                constructorToUse = (Constructor<?>) bd.resolvedConstructorOrFactoryMethod;
                if (constructorToUse == null) {
                    final Class<?> clazz = bd.getBeanClass();
                    if (clazz.isInterface()) {
                        throw new BeanInstantiationException(clazz, "Specified class is an interface");
                    }
                    try {
                        if (System.getSecurityManager() != null) {
                            constructorToUse = AccessController.doPrivileged(new PrivilegedExceptionAction<Constructor<?>>() {
                                @Override
                                public Constructor<?> run() throws Exception {
                                    return clazz.getDeclaredConstructor((Class[]) null);
                                }
                            });
                        }
                        else {
                            constructorToUse =  clazz.getDeclaredConstructor((Class[]) null);
                        }
                        bd.resolvedConstructorOrFactoryMethod = constructorToUse;
                    }
                    catch (Throwable ex) {
                        throw new BeanInstantiationException(clazz, "No default constructor found", ex);
                    }
                }
            }
            return BeanUtils.instantiateClass(constructorToUse);
        }
        else {
            // Must generate CGLIB subclass.
            return instantiateWithMethodInjection(bd, beanName, owner);
        }
    }
beans.factory.support.CglibSubclassingInstantiationStrategy(继承自SimpleInstantiationStrategy)
内部静态类
    ClassLoaderAwareGeneratorStrategy (extends DefaultGeneratorStrategy)
        /**
         * Create a new instance of a dynamically generated subclass implementing the
         * required lookups.
         * @param ctor constructor to use. If this is {@code null}, use the
         * no-arg constructor (no parameterization, or Setter Injection)
         * @param args arguments to use for the constructor.
         * Ignored if the {@code ctor} parameter is {@code null}.
         * @return new instance of the dynamically generated subclass
         */
        public Object instantiate(Constructor<?> ctor, Object... args) {
            Class<?> subclass = createEnhancedSubclass(this.beanDefinition);
            Object instance;
            if (ctor == null) {
                instance = BeanUtils.instantiateClass(subclass);
            }
            else {
                try {
                    Constructor<?> enhancedSubclassConstructor = subclass.getConstructor(ctor.getParameterTypes());
                    instance = enhancedSubclassConstructor.newInstance(args);
                }
                catch (Exception ex) {
                    throw new BeanInstantiationException(this.beanDefinition.getBeanClass(),
                            "Failed to invoke constructor for CGLIB enhanced subclass [" + subclass.getName() + "]", ex);
                }
            }
            // SPR-10785: set callbacks directly on the instance instead of in the
            // enhanced class (via the Enhancer) in order to avoid memory leaks.
            Factory factory = (Factory) instance;
            factory.setCallbacks(new Callback[] {NoOp.INSTANCE,
                    new LookupOverrideMethodInterceptor(this.beanDefinition, this.owner),
                    new ReplaceOverrideMethodInterceptor(this.beanDefinition, this.owner)});
            return instance;
        }
```

1. 首先判断如果BeanDefinition.getMethodOverrides()为空也就是用户没有使用replace或者lookup的配置方法，则直接使用反射调用构造器构造对象实例
2. 如果设置了上述两个特性，则必须使用动态代理的方式将包含两个特性所对应的逻辑的拦截器增强器设置进去，这样才可以保证在调用方法的时候会被相应的拦截器增强，返回值为包含拦截器的代理实例

##### 步骤4- 创建bean的ObjectFactory，用以解决循环依赖的问题

重点看一下ObjectFactory的实现

**可以总结出来Spring解决循环依赖的方法：**

> 在B中创建依赖A时通过ObjectFactory提供的实例化方法来中断A中的属性填充，使B中持有的A仅仅是刚刚初始化并没有填充任何属性的A，而这正初始化A的步骤还是在最开始创建A的时候进行的，但是因为A与B中的A所表示的属性地址是一样的，所以在A中创建好的属性填充自然可以通过B中的A获取，这样就解决了循环依赖的问题。

```
记录创建bean的ObjectFactory
beans.factory.support.AbstractAutowireCapableBeanFactory
    /**
     * Obtain a reference for early access to the specified bean,
     * typically for the purpose of resolving a circular reference.
     * @param beanName the name of the bean (for error handling purposes)
     * @param mbd the merged bean definition for the bean
     * @param bean the raw bean instance
     * @return the object to expose as bean reference
     */
    protected Object getEarlyBeanReference(String beanName, RootBeanDefinition mbd, Object bean) {
        Object exposedObject = bean;
        if (bean != null && !mbd.isSynthetic() && hasInstantiationAwareBeanPostProcessors()) {
            for (BeanPostProcessor bp : getBeanPostProcessors()) {
                if (bp instanceof SmartInstantiationAwareBeanPostProcessor) {
                    SmartInstantiationAwareBeanPostProcessor ibp = (SmartInstantiationAwareBeanPostProcessor) bp;
                    exposedObject = ibp.getEarlyBeanReference(exposedObject, beanName);
                    if (exposedObject == null) {
                        return null;
                    }
                }
            }
        }
        return exposedObject;
    }
```

##### 步骤5- 属性注入

```java
 属性注入

    /**
     * Populate the bean instance in the given BeanWrapper with the property values
     * from the bean definition.
     * @param beanName the name of the bean
     * @param mbd the bean definition for the bean
     * @param bw BeanWrapper with bean instance
     */
    protected void populateBean(String beanName, RootBeanDefinition mbd, BeanWrapper bw) {
        PropertyValues pvs = mbd.getPropertyValues();

        if (bw == null) {
            if (!pvs.isEmpty()) {
                throw new BeanCreationException(
                        mbd.getResourceDescription(), beanName, "Cannot apply property values to null instance");
            }
            else {
                // Skip property population phase for null instance.
                return;
            }
        }

        // Give any InstantiationAwareBeanPostProcessors the opportunity to modify the
        // state of the bean before properties are set. This can be used, for example,
        // to support styles of field injection.
        boolean continueWithPropertyPopulation = true;

        if (!mbd.isSynthetic() && hasInstantiationAwareBeanPostProcessors()) {
            for (BeanPostProcessor bp : getBeanPostProcessors()) {
                if (bp instanceof InstantiationAwareBeanPostProcessor) {
                    InstantiationAwareBeanPostProcessor ibp = (InstantiationAwareBeanPostProcessor) bp;
                    if (!ibp.postProcessAfterInstantiation(bw.getWrappedInstance(), beanName)) {
                        continueWithPropertyPopulation = false;
                        break;
                    }
                }
            }
        }

        if (!continueWithPropertyPopulation) {
            return;
        }

        if (mbd.getResolvedAutowireMode() == RootBeanDefinition.AUTOWIRE_BY_NAME ||
                mbd.getResolvedAutowireMode() == RootBeanDefinition.AUTOWIRE_BY_TYPE) {
            MutablePropertyValues newPvs = new MutablePropertyValues(pvs);

            // Add property values based on autowire by name if applicable.
            if (mbd.getResolvedAutowireMode() == RootBeanDefinition.AUTOWIRE_BY_NAME) {
                autowireByName(beanName, mbd, bw, newPvs);
            }

            // Add property values based on autowire by type if applicable.
            if (mbd.getResolvedAutowireMode() == RootBeanDefinition.AUTOWIRE_BY_TYPE) {
                autowireByType(beanName, mbd, bw, newPvs);
            }

            pvs = newPvs;
        }

        boolean hasInstAwareBpps = hasInstantiationAwareBeanPostProcessors();
        boolean needsDepCheck = (mbd.getDependencyCheck() != RootBeanDefinition.DEPENDENCY_CHECK_NONE);

        if (hasInstAwareBpps || needsDepCheck) {
            PropertyDescriptor[] filteredPds = filterPropertyDescriptorsForDependencyCheck(bw, mbd.allowCaching);
            if (hasInstAwareBpps) {
                for (BeanPostProcessor bp : getBeanPostProcessors()) {
                    if (bp instanceof InstantiationAwareBeanPostProcessor) {
                        InstantiationAwareBeanPostProcessor ibp = (InstantiationAwareBeanPostProcessor) bp;
                        pvs = ibp.postProcessPropertyValues(pvs, filteredPds, bw.getWrappedInstance(), beanName);
                        if (pvs == null) {
                            return;
                        }
                    }
                }
            }
            if (needsDepCheck) {
                checkDependencies(beanName, mbd, filteredPds, pvs);
            }
        }

        applyPropertyValues(beanName, mbd, bw, pvs);
    }
```

**populatedBean方法的调用逻辑：**

1. InstantiationAwareBeanPostProcessor处理器的postProcessAfterInstantiation函数的应用，此函数可以控制是否继续进行填充
2. 根据注入类型（byName、byType），提取依赖的bean，并统一存入PropertyValues中
3. 应用InstantiationAwareBeanPostProcessor处理器的postProcessPropertyValues方法，对属性获取完毕填充前对属性的再次处理，典型应用是RequiredAnnotationBeanPostProcessor类中对属性的验证。
4. 将所有PropertyValues中的属性填充至BeanWrapper中。

下面主要分析属性依赖注入填充：

**1- autowireByName**

> 原理：在传入的参数pvs中找到已经加载的bean，并递归实例化，进而加入到pvs中

```
1 autowireByName
    /**
     * Fill in any missing property values with references to
     * other beans in this factory if autowire is set to "byName".
     * @param beanName the name of the bean we're wiring up.
     * Useful for debugging messages; not used functionally.
     * @param mbd bean definition to update through autowiring
     * @param bw BeanWrapper from which we can obtain information about the bean
     * @param pvs the PropertyValues to register wired objects with
     */
    protected void autowireByName(
            String beanName, AbstractBeanDefinition mbd, BeanWrapper bw, MutablePropertyValues pvs) {

        String[] propertyNames = unsatisfiedNonSimpleProperties(mbd, bw);
        for (String propertyName : propertyNames) {
            if (containsBean(propertyName)) {
                Object bean = getBean(propertyName);
                pvs.add(propertyName, bean);
                registerDependentBean(propertyName, beanName);
                if (logger.isDebugEnabled()) {
                    logger.debug("Added autowiring by name from bean name '" + beanName +
                            "' via property '" + propertyName + "' to bean named '" + propertyName + "'");
                }
            }
            else {
                if (logger.isTraceEnabled()) {
                    logger.trace("Not autowiring property '" + propertyName + "' of bean '" + beanName +
                            "' by name: no matching bean found");
                }
            }
        }
    }
```

**2- autowireByType**
相比于1最复杂的在于寻找bw中需要依赖注入的属性，然后遍历这些属性并寻找类型匹配的bean，其中最复杂的就是寻找类型匹配的bean，同时，Spring中提供了对集合的类型注入的支持，如

```
@Autowired
        private List<AType> ATypes;
```

这会将所有AType匹配的类型找出来并注入到Atypes集合中，正是因为这一因素，autowireByType函数中新建了autowiredBeanNames，用于存储所有依赖的bean。
寻找类型匹配的逻辑实现封装在了resolveDependency函数中

```
2 autowireByType
    /**
     * Abstract method defining "autowire by type" (bean properties by type) behavior.
     * <p>This is like PicoContainer default, in which there must be exactly one bean
     * of the property type in the bean factory. This makes bean factories simple to
     * configure for small namespaces, but doesn't work as well as standard Spring
     * behavior for bigger applications.
     * @param beanName the name of the bean to autowire by type
     * @param mbd the merged bean definition to update through autowiring
     * @param bw BeanWrapper from which we can obtain information about the bean
     * @param pvs the PropertyValues to register wired objects with
     */
    protected void autowireByType(
            String beanName, AbstractBeanDefinition mbd, BeanWrapper bw, MutablePropertyValues pvs) {

        TypeConverter converter = getCustomTypeConverter();
        if (converter == null) {
            converter = bw;
        }

        Set<String> autowiredBeanNames = new LinkedHashSet<String>(4);
        String[] propertyNames = unsatisfiedNonSimpleProperties(mbd, bw);
        for (String propertyName : propertyNames) {
            try {
                PropertyDescriptor pd = bw.getPropertyDescriptor(propertyName);
                // Don't try autowiring by type for type Object: never makes sense,
                // even if it technically is a unsatisfied, non-simple property.
                if (Object.class != pd.getPropertyType()) {
                    MethodParameter methodParam = BeanUtils.getWriteMethodParameter(pd);
                    // Do not allow eager init for type matching in case of a prioritized post-processor.
                    boolean eager = !PriorityOrdered.class.isAssignableFrom(bw.getWrappedClass());
                    DependencyDescriptor desc = new AutowireByTypeDependencyDescriptor(methodParam, eager);
                    Object autowiredArgument = resolveDependency(desc, beanName, autowiredBeanNames, converter);
                    if (autowiredArgument != null) {
                        pvs.add(propertyName, autowiredArgument);
                    }
                    for (String autowiredBeanName : autowiredBeanNames) {
                        registerDependentBean(autowiredBeanName, beanName);
                        if (logger.isDebugEnabled()) {
                            logger.debug("Autowiring by type from bean name '" + beanName + "' via property '" +
                                    propertyName + "' to bean named '" + autowiredBeanName + "'");
                        }
                    }
                    autowiredBeanNames.clear();
                }
            }
            catch (BeansException ex) {
                throw new UnsatisfiedDependencyException(mbd.getResourceDescription(), beanName, propertyName, ex);
            }
        }
    }
2.1 寻找类型匹配
beans.factory.support.DefaultListableBeanFactory
    @Override
    public Object resolveDependency(DependencyDescriptor descriptor, String requestingBeanName,
            Set<String> autowiredBeanNames, TypeConverter typeConverter) throws BeansException {

        descriptor.initParameterNameDiscovery(getParameterNameDiscoverer());
        if (javaUtilOptionalClass == descriptor.getDependencyType()) {
            return new OptionalDependencyFactory().createOptionalDependency(descriptor, requestingBeanName);
        }
        else if (ObjectFactory.class == descriptor.getDependencyType() ||
                ObjectProvider.class == descriptor.getDependencyType()) {
            return new DependencyObjectProvider(descriptor, requestingBeanName);
        }
        else if (javaxInjectProviderClass == descriptor.getDependencyType()) {
            return new Jsr330ProviderFactory().createDependencyProvider(descriptor, requestingBeanName);
        }
        else {
            Object result = getAutowireCandidateResolver().getLazyResolutionProxyIfNecessary(
                    descriptor, requestingBeanName);
            if (result == null) {
                result = doResolveDependency(descriptor, requestingBeanName, autowiredBeanNames, typeConverter);
            }
            return result;
        }
    }

    public Object doResolveDependency(DependencyDescriptor descriptor, String beanName,
            Set<String> autowiredBeanNames, TypeConverter typeConverter) throws BeansException {

        InjectionPoint previousInjectionPoint = ConstructorResolver.setCurrentInjectionPoint(descriptor);
        try {
            Object shortcut = descriptor.resolveShortcut(this);
            if (shortcut != null) {
                return shortcut;
            }

            Class<?> type = descriptor.getDependencyType();
            Object value = getAutowireCandidateResolver().getSuggestedValue(descriptor);
            if (value != null) {
                if (value instanceof String) {
                    String strVal = resolveEmbeddedValue((String) value);
                    BeanDefinition bd = (beanName != null && containsBean(beanName) ? getMergedBeanDefinition(beanName) : null);
                    value = evaluateBeanDefinitionString(strVal, bd);
                }
                TypeConverter converter = (typeConverter != null ? typeConverter : getTypeConverter());
                return (descriptor.getField() != null ?
                        converter.convertIfNecessary(value, type, descriptor.getField()) :
                        converter.convertIfNecessary(value, type, descriptor.getMethodParameter()));
            }

            Object multipleBeans = resolveMultipleBeans(descriptor, beanName, autowiredBeanNames, typeConverter);
            if (multipleBeans != null) {
                return multipleBeans;
            }

            Map<String, Object> matchingBeans = findAutowireCandidates(beanName, type, descriptor);
            if (matchingBeans.isEmpty()) {
                if (isRequired(descriptor)) {
                    raiseNoMatchingBeanFound(type, descriptor.getResolvableType(), descriptor);
                }
                return null;
            }

            String autowiredBeanName;
            Object instanceCandidate;

            if (matchingBeans.size() > 1) {
                autowiredBeanName = determineAutowireCandidate(matchingBeans, descriptor);
                if (autowiredBeanName == null) {
                    if (isRequired(descriptor) || !indicatesMultipleBeans(type)) {
                        return descriptor.resolveNotUnique(type, matchingBeans);
                    }
                    else {
                        // In case of an optional Collection/Map, silently ignore a non-unique case:
                        // possibly it was meant to be an empty collection of multiple regular beans
                        // (before 4.3 in particular when we didn't even look for collection beans).
                        return null;
                    }
                }
                instanceCandidate = matchingBeans.get(autowiredBeanName);
            }
            else {
                // We have exactly one match.
                Map.Entry<String, Object> entry = matchingBeans.entrySet().iterator().next();
                autowiredBeanName = entry.getKey();
                instanceCandidate = entry.getValue();
            }

            if (autowiredBeanNames != null) {
                autowiredBeanNames.add(autowiredBeanName);
            }
            return (instanceCandidate instanceof Class ?
                    descriptor.resolveCandidate(autowiredBeanName, type, this) : instanceCandidate);
        }
        finally {
            ConstructorResolver.setCurrentInjectionPoint(previousInjectionPoint);
        }
    }
```

**3- 将获取的属性应用到实例化bean中**
1、2完成了属性的获取，并以PropertyValues形式存在的。

```
3 应用获取的属性到实例化bean上
beans.factory.support.AbstractAutowireCapableBeanFactory
    /**
     * Apply the given property values, resolving any runtime references
     * to other beans in this bean factory. Must use deep copy, so we
     * don't permanently modify this property.
     * @param beanName the bean name passed for better exception information
     * @param mbd the merged bean definition
     * @param bw the BeanWrapper wrapping the target object
     * @param pvs the new property values
     */
    protected void applyPropertyValues(String beanName, BeanDefinition mbd, BeanWrapper bw, PropertyValues pvs) {
        if (pvs == null || pvs.isEmpty()) {
            return;
        }

        MutablePropertyValues mpvs = null;
        List<PropertyValue> original;

        if (System.getSecurityManager() != null) {
            if (bw instanceof BeanWrapperImpl) {
                ((BeanWrapperImpl) bw).setSecurityContext(getAccessControlContext());
            }
        }

        if (pvs instanceof MutablePropertyValues) {
            mpvs = (MutablePropertyValues) pvs;
            if (mpvs.isConverted()) {
                // Shortcut: use the pre-converted values as-is.
                try {
                    bw.setPropertyValues(mpvs);
                    return;
                }
                catch (BeansException ex) {
                    throw new BeanCreationException(
                            mbd.getResourceDescription(), beanName, "Error setting property values", ex);
                }
            }
            original = mpvs.getPropertyValueList();
        }
        else {
            original = Arrays.asList(pvs.getPropertyValues());
        }

        TypeConverter converter = getCustomTypeConverter();
        if (converter == null) {
            converter = bw;
        }
        BeanDefinitionValueResolver valueResolver = new BeanDefinitionValueResolver(this, beanName, mbd, converter);

        // Create a deep copy, resolving any references for values.
        List<PropertyValue> deepCopy = new ArrayList<PropertyValue>(original.size());
        boolean resolveNecessary = false;
        for (PropertyValue pv : original) {
            if (pv.isConverted()) {
                deepCopy.add(pv);
            }
            else {
                String propertyName = pv.getName();
                Object originalValue = pv.getValue();
                Object resolvedValue = valueResolver.resolveValueIfNecessary(pv, originalValue);
                Object convertedValue = resolvedValue;
                boolean convertible = bw.isWritableProperty(propertyName) &&
                        !PropertyAccessorUtils.isNestedOrIndexedProperty(propertyName);
                if (convertible) {
                    convertedValue = convertForProperty(resolvedValue, propertyName, bw, converter);
                }
                // Possibly store converted value in merged bean definition,
                // in order to avoid re-conversion for every created bean instance.
                if (resolvedValue == originalValue) {
                    if (convertible) {
                        pv.setConvertedValue(convertedValue);
                    }
                    deepCopy.add(pv);
                }
                else if (convertible && originalValue instanceof TypedStringValue &&
                        !((TypedStringValue) originalValue).isDynamic() &&
                        !(convertedValue instanceof Collection || ObjectUtils.isArray(convertedValue))) {
                    pv.setConvertedValue(convertedValue);
                    deepCopy.add(pv);
                }
                else {
                    resolveNecessary = true;
                    deepCopy.add(new PropertyValue(pv, convertedValue));
                }
            }
        }
        if (mpvs != null && !resolveNecessary) {
            mpvs.setConverted();
        }

        // Set our (possibly massaged) deep copy.
        try {
            bw.setPropertyValues(new MutablePropertyValues(deepCopy));
        }
        catch (BeansException ex) {
            throw new BeanCreationException(
                    mbd.getResourceDescription(), beanName, "Error setting property values", ex);
        }
    }
```

##### 步骤6- 初始化bean

> Spring中已经执行过bean的实例化，并且进行了属性的填充，而就在这时会调用用户配置的初始化方法（init-method子元素）除了调用用户自定义的初始化方法还做了一些必要的工作。

**1- 激活Aware方法**
Spring中提供了一些Aware相关接口，比如BeanFactoryAware、ApplicationContextAware、ResourceLoaderAware、ServletContextAware等，实现了这些Aware接口的bean在被初始化后，Spring容器将会注入BeanFactory的实例，而实现ApplicationContextAware的bean，在bean被初始化后，将会被注入ApplicationContext的实例等

```
private void invokeAwareMethods(final String beanName, final Object bean) {
        if (bean instanceof Aware) {
            if (bean instanceof BeanNameAware) {
                ((BeanNameAware) bean).setBeanName(beanName);
            }
            if (bean instanceof BeanClassLoaderAware) {
                ((BeanClassLoaderAware) bean).setBeanClassLoader(getBeanClassLoader());
            }
            if (bean instanceof BeanFactoryAware) {
                ((BeanFactoryAware) bean).setBeanFactory(AbstractAutowireCapableBeanFactory.this);
            }
        }
    }
```

**2- 处理器的应用**
处理器给用户充足的权限去更改或者拓展Spring

```
    @Override
    public Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName)
            throws BeansException {

        Object result = existingBean;
        for (BeanPostProcessor beanProcessor : getBeanPostProcessors()) {
            result = beanProcessor.postProcessBeforeInitialization(result, beanName);
            if (result == null) {
                return result;
            }
        }
        return result;
    }

        @Override
    public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName)
            throws BeansException {

        Object result = existingBean;
        for (BeanPostProcessor beanProcessor : getBeanPostProcessors()) {
            result = beanProcessor.postProcessAfterInitialization(result, beanName);
            if (result == null) {
                return result;
            }
        }
        return result;
    }
```

**3- 激活自定义的init方法**
有两种自定义初始化方法：自定义实现了InitializingBean接口，并在afterPropertiesSet中实现自己的初始化业务逻辑；在配置中设置init-method。
执行顺序是先执行afterPropertiesSet然后在执行init-method方法

```
    /**
     * Give a bean a chance to react now all its properties are set,
     * and a chance to know about its owning bean factory (this object).
     * This means checking whether the bean implements InitializingBean or defines
     * a custom init method, and invoking the necessary callback(s) if it does.
     * @param beanName the bean name in the factory (for debugging purposes)
     * @param bean the new bean instance we may need to initialize
     * @param mbd the merged bean definition that the bean was created with
     * (can also be {@code null}, if given an existing bean instance)
     * @throws Throwable if thrown by init methods or by the invocation process
     * @see #invokeCustomInitMethod
     */
    protected void invokeInitMethods(String beanName, final Object bean, RootBeanDefinition mbd)
            throws Throwable {

        boolean isInitializingBean = (bean instanceof InitializingBean);
        if (isInitializingBean && (mbd == null || !mbd.isExternallyManagedInitMethod("afterPropertiesSet"))) {
            if (logger.isDebugEnabled()) {
                logger.debug("Invoking afterPropertiesSet() on bean with name '" + beanName + "'");
            }
            if (System.getSecurityManager() != null) {
                try {
                    AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
                        @Override
                        public Object run() throws Exception {
                            ((InitializingBean) bean).afterPropertiesSet();
                            return null;
                        }
                    }, getAccessControlContext());
                }
                catch (PrivilegedActionException pae) {
                    throw pae.getException();
                }
            }
            else {
                ((InitializingBean) bean).afterPropertiesSet();
            }
        }

        if (mbd != null) {
            String initMethodName = mbd.getInitMethodName();
            if (initMethodName != null && !(isInitializingBean && "afterPropertiesSet".equals(initMethodName)) &&
                    !mbd.isExternallyManagedInitMethod(initMethodName)) {
                invokeCustomInitMethod(beanName, bean, mbd);
            }
        }
    }
```

##### 步骤7- 注册DisposableBean

Spring提供了销毁方法的拓展入口，Spring提供了两种方式：配置属性destroy-method；实现DisposableBean接口的destory方法执行顺序是先执行destroy方法，然后在执行destroy-method配置的方法

```
beans.factory.support.AbstractBeanFactory
    /**
     * Add the given bean to the list of disposable beans in this factory,
     * registering its DisposableBean interface and/or the given destroy method
     * to be called on factory shutdown (if applicable). Only applies to singletons.
     * @param beanName the name of the bean
     * @param bean the bean instance
     * @param mbd the bean definition for the bean
     * @see RootBeanDefinition#isSingleton
     * @see RootBeanDefinition#getDependsOn
     * @see #registerDisposableBean
     * @see #registerDependentBean
     */
    protected void registerDisposableBeanIfNecessary(String beanName, Object bean, RootBeanDefinition mbd) {
        AccessControlContext acc = (System.getSecurityManager() != null ? getAccessControlContext() : null);
        if (!mbd.isPrototype() && requiresDestruction(bean, mbd)) {
            if (mbd.isSingleton()) {
                // Register a DisposableBean implementation that performs all destruction
                // work for the given bean: DestructionAwareBeanPostProcessors,
                // DisposableBean interface, custom destroy method.
                registerDisposableBean(beanName,
                        new DisposableBeanAdapter(bean, beanName, mbd, getBeanPostProcessors(), acc));
            }
            else {
                // A bean with a custom scope...
                Scope scope = this.scopes.get(mbd.getScope());
                if (scope == null) {
                    throw new IllegalStateException("No Scope registered for scope name '" + mbd.getScope() + "'");
                }
                scope.registerDestructionCallback(beanName,
                        new DisposableBeanAdapter(bean, beanName, mbd, getBeanPostProcessors(), acc));
            }
        }
    }
```





https://www.jianshu.com/p/58e2977c420d