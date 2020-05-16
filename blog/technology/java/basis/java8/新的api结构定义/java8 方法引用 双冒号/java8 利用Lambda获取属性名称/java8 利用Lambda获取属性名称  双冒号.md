[TOC]



# java8 利用Lambda获取属性名称  双冒号

/Users/jerryye/backup/studio/AvailableCode/basis/java8/function_consumer_predicate/function_consumer_predicate_demo





## functionalInterfaceInfoDemo

```
package com.practice.demo;

import com.practice.User;
import com.practice.utils.LambdaUtils;
import com.practice.utils.SerializedLambda;
import com.practice.utils.StringUtils;
import org.junit.Test;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * <B>Description:</B> FunctionalInterface功能接口信息获取 <br>
 * <B>Create on:</B> 2018/8/6 下午11:17 <br>
 *
 * @author xiangyu.ye
 * @version 1.0
 */
public class functionalInterfaceInfoDemo {


    /**
     * <B>Description:</B> 获取字段通过 双冒号 <br>
     * <B>Create on:</B> 2019-03-22 18:50 <br>
     *
     * @author xiangyu.ye
     */
    @Test
    public void getFieldNameByFunction() {
        SerializedLambda columnMap = LambdaUtils.resolve(User::getUserName);
        String fieldName = StringUtils.resolveFieldName(columnMap.getImplMethodName());
        System.out.println(fieldName);
    }

}

```



## LambdaUtils

```
package com.practice.utils;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Lambda 解析工具类
 *
 * copy from com.baomidou.mybatisplus.core.toolkit.LambdaUtils
 * @author HCL
 * @since 2018-05-10
 */
public final class LambdaUtils {


    /**
     * SerializedLambda 反序列化缓存
     */
    private static final Map<Class, WeakReference<SerializedLambda>> FUNC_CACHE = new ConcurrentHashMap<>();

    /**
     * 解析 lambda 表达式
     *
     * @param func 需要解析的 lambda 对象
     * @param <T>  类型，被调用的 Function 对象的目标类型
     * @return 返回解析后的结果
     */
    public static <T> SerializedLambda resolve(SFunction<T, ?> func) {
        Class clazz = func.getClass();
        return Optional.ofNullable(FUNC_CACHE.get(clazz))
                .map(WeakReference::get)
                .orElseGet(() -> {
                    SerializedLambda lambda = SerializedLambda.resolve(func);
                    FUNC_CACHE.put(clazz, new WeakReference<>(lambda));
                    return lambda;
                });
    }

}

```

## SerializedLambda

```
package com.practice.utils;



import org.springframework.util.SerializationUtils;

import java.io.*;

/**
 * 这个类是从 {@link java.lang.invoke.SerializedLambda} 里面 copy 过来的，
 * 字段信息完全一样
 * <p>负责将一个支持序列的 Function 序列化为 SerializedLambda</p>
 *
 * @author HCL
 * @since 2018/05/10
 */
@SuppressWarnings("unused")
public class SerializedLambda implements Serializable {

    private static final long serialVersionUID = 8025925345765570181L;

    private Class<?> capturingClass;
    private String functionalInterfaceClass;
    private String functionalInterfaceMethodName;
    private String functionalInterfaceMethodSignature;
    private String implClass;
    private String implMethodName;
    private String implMethodSignature;
    private int implMethodKind;
    private String instantiatedMethodType;
    private Object[] capturedArgs;

    /**
     * 通过反序列化转换 lambda 表达式，该方法只能序列化 lambda 表达式，不能序列化接口实现或者正常非 lambda 写法的对象
     *
     * @param lambda lambda对象
     * @return 返回解析后的 SerializedLambda
     */
    public static SerializedLambda resolve(SFunction lambda) {
        if (!lambda.getClass().isSynthetic()) {
            throw new RuntimeException("该方法仅能传入 lambda 表达式产生的合成类");
        }
        try (ObjectInputStream objIn = new ObjectInputStream(new ByteArrayInputStream(SerializationUtils.serialize(lambda))) {
            @Override
            protected Class<?> resolveClass(ObjectStreamClass objectStreamClass) throws IOException, ClassNotFoundException {
                Class<?> clazz = super.resolveClass(objectStreamClass);
                return clazz == java.lang.invoke.SerializedLambda.class ? SerializedLambda.class : clazz;
            }
        }) {
            return (SerializedLambda) objIn.readObject();
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException("This is impossible to happen", e);
        }
    }

    /**
     * 获取接口 class
     *
     * @return 返回 class 名称
     */
    public String getFunctionalInterfaceClassName() {
        return normalName(functionalInterfaceClass);
    }

    /**
     * 获取实现的 class
     *
     * @return 实现类
     */
    public Class getImplClass() {
        try {
            return Class.forName(getImplClassName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("找不到指定的class！请仅在明确确定会有 class 的时候，调用该方法", e);
        }
    }



    /**
     * 获取 class 的名称
     *
     * @return 类名
     */
    public String getImplClassName() {
        return normalName(implClass);
    }

    /**
     * 获取实现者的方法名称
     *
     * @return 方法名称
     */
    public String getImplMethodName() {
        return implMethodName;
    }

    /**
     * 正常化类名称，将类名称中的 / 替换为 .
     *
     * @param name 名称
     * @return 正常的类名
     */
    private String normalName(String name) {
        return name.replace('/', '.');
    }

    /**
     * @return 字符串形式
     */
    @Override
    public String toString() {
        return String.format("%s -> %s::%s", getFunctionalInterfaceClassName(), getImplClass().getSimpleName(),
                implMethodName);
    }

}

```

## SFunction

```
package com.practice.utils;

import java.io.Serializable;

/**
 * 支持序列化的 Function
 *
 * @author miemie
 * @since 2018-05-12
 */
@FunctionalInterface
public interface SFunction<T, R> extends Serializable {

    /**
     * Applies this function to the given argument.
     *
     * @param t the function argument
     * @return the function result
     */
    R apply(T t);
}

```

## StringUtils

```
package com.practice.utils;


import static java.util.stream.Collectors.joining;

import java.nio.charset.StandardCharsets;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * <p>
 * String 工具类
 * </p>
 *
 * @author D.Yang
 * @since 2016-08-18
 */
public class StringUtils {

    /**
     * 解析 getMethodName -> propertyName
     *
     * @param getMethodName 需要解析的
     * @return 返回解析后的字段名称
     */
    public static String resolveFieldName(String getMethodName) {
        if (getMethodName.startsWith("get")) {
            getMethodName = getMethodName.substring(3);
        } else if (getMethodName.startsWith("is")) {
            getMethodName = getMethodName.substring(2);
        }
        // 小写第一个字母
        return firstToLowerCase(getMethodName);
    }

    /**
     * 首字母转换小写
     *
     * @param param 需要转换的字符串
     * @return 转换好的字符串
     */
    public static String firstToLowerCase(String param) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(param)) {
            return "";
        }
        return param.substring(0, 1).toLowerCase() + param.substring(1);
    }
}

```

## User

```
package com.practice;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
public class User {
    public String userName;
    public Integer marks;
    public List<String> Orders;

    public User() {
    }

    public User(String name) {
        this.userName = name;
    }
}

```

