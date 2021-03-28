# Enum Tools 减少枚举中重复的方法

程序高手虎哥2021-01-30 10:01:00

## **背景**

最近修改项目的时候发现一些好东西，对于枚举的通用方法的处理，发现之前写的很多重复性的劳动，总体来说对于枚举的认识不够，对于这些特性的使用还不是非常的属性，编码这个东西，总在反思、学习中成长，这种小东西也是一种成长，别看微不足道，大规模推广使用起来，对于编码的统一度非常的友好的。

## **实践**

几乎在系统的枚举中都是code、msg这样的枚举，非常的多！没有直接地利用Enum中的name，主要是语法糖抽象的后果，看不到这个字段，很少用。一般都是通过code 获取到枚举，通过code 判断是否存在个枚举，通过code 获取到msg … 我相信你也写了不少的这样的东西

### **反例**

看起来无伤大雅，基本都能够读懂，但是一个系统中有很多这样的枚举，每个都写一遍，你会发现非常的伤不起。但是我也写过…

```java
package com.wangji92.github.study.other.enums;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 任务状态枚举
 *
 * @author 汪小哥
 * @date 09-03-2020
 */
@Slf4j
public enum TaskStatusEnum {
    SUCCESS("success", "成功"),
    FAIL("fail", "失败"),
    CANCEL("cancel", "取消");

    /**
     * 编码表示
     */
    private String code;
    /**
     * 说明
     */
    private String msg;

    public String getCode() {
        return code;
    }
    public String getMsg() {
        return msg;
    }
    TaskStatusEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private static Map<String, TaskStatusEnum> taskMaps = new HashMap<>();

    static {
        for (TaskStatusEnum value : TaskStatusEnum.values()) {
            taskMaps.put(value.getCode(), value);
        }
    }

    public static String getMsgByCode(String code) {
        TaskStatusEnum taskStatusEnum = getEnumByCode(code);
        String msg = taskStatusEnum != null ? taskStatusEnum.getMsg() : "";
        return msg;
    }

    public static TaskStatusEnum getEnumByCode(String code) {
        return taskMaps.get(code);
    }

    public static void main(String[] args) {
        String success = TaskStatusEnum.getMsgByCode("success");
        log.info("msg:" + success);

        boolean validEnumCode = TaskStatusEnum.getEnumByCode("success") != null;
        log.info("validEnumCode:" + validEnumCode);
    }
}
```

### **正例**

#### **继承关系图**

<img src="Enum Tools 减少枚举中重复的方法.assets/4deaf98cf6424a7b810645fc4862c3aa" alt="Enum Tools 减少枚举中重复的方法" style="zoom:50%;" />



#### **实现类**

enum 这个是一个语法糖，所有的都继承了java.lang.Enum，Java不支持多继承，只能通过接口处理咯，一个是code 一个是msg。如下所示，有区别？都是一样的而且这里的很多的方法都没有啦

```java
package com.wangji92.github.study.other.enums.enhance;

import java.io.Serializable;

/**
 * @author 汪小哥
 * @date 09-03-2020
 */
public interface EnumCode<T> extends Serializable {
    /**
     * 获取枚举的返回Code
     *
     * @param <T>
     * @return
     */
    T getCode();
}


/**
 * 枚举的返回msg的信息
 *
 * @author 汪小哥
 * @date 09-03-2020
 */
public interface EnumCodeMsg<T> extends EnumCode<T> {
    /**
     * 获取枚举的备注信息
     * @return
     */
    String getEnumMsg();
}


/**
 * @author 汪小哥
 * @date 09-03-2020
 */
@Slf4j
public enum TaskStatusEnumsTwo implements EnumCodeMsg<String> {
    SUCCESS("success", "成功"),
    FAIL("fail", "失败"),
    CANCEL("cancel", "取消");


    private String code;

    private String msg;

    TaskStatusEnumsTwo(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public String getEnumMsg() {
        return msg;
    }

    @Override
    public String getCode() {
        return code;
    }
}
```

#### **EnumCodeMsgUtils 工具类**

这里可以添加继承 org.apache.commons.lang3.EnumUtils 要不要都可以~

```
<dependency>
   <groupId>org.apache.commons</groupId>
   <artifactId>commons-lang3</artifactId>
   <version>3.9</version>
</dependency> 
```

enumClass.getEnumConstants 就是获取语法糖中的values的方法

```java
package com.wangji92.github.study.other.enums.enhance;


import org.apache.commons.lang3.EnumUtils;

import java.util.Objects;
import java.util.Optional;

/**
 * 枚举处理工具类
 *
 * @author 汪小哥
 * @date 09-03-2020
 */
public class EnumCodeMsgUtils extends EnumUtils {
    /**
     * 根据code 获取到当前的枚举
     *
     * @param enumClass
     * @param code
     * @param <E>
     * @param <T>
     * @return
     */
    public static <E extends Enum<E> & EnumCodeMsg<T>, T> Optional<E> getEnumByCode(Class<E> enumClass, T code) {
        if (code == null) {
            return Optional.empty();
        }
        for (E enumConstant : enumClass.getEnumConstants()) {
            if (Objects.equals(enumConstant.getCode(), code)) {
                return Optional.of(enumConstant);
            }
        }
        return Optional.empty();
    }

    /**
     * 根据code 获取到枚举的msg的信息
     *
     * @param enumClass
     * @param code
     * @param defaultMsg 默认返回值
     * @param <E>
     * @param <T>
     * @return
     */
    public static <E extends Enum<E> & EnumCodeMsg<T>, T> String getEnumMsgByCode(Class<E> enumClass, T code, String defaultMsg) {
        Optional<E> enumByCode = EnumCodeMsgUtils.getEnumByCode(enumClass, code);
        if (enumByCode.isPresent()) {
            return enumByCode.get().getEnumMsg();
        }
        return defaultMsg;
    }

    /**
     * 根据code 获取到枚举的msg的信息,默认返回为空
     * @param enumClass
     * @param code
     * @param <E>
     * @param <T>
     * @return
     */
    public static <E extends Enum<E> & EnumCodeMsg<T>, T> String getEnumMsgByCode(Class<E> enumClass, T code) {
        return EnumCodeMsgUtils.getEnumMsgByCode(enumClass,code,"");
    }

    /**
     * 这个是否是一个有效的code
     *
     * @param enumClass
     * @param code
     * @param <E>
     * @param <T>
     * @return
     */
    public static <E extends Enum<E> & EnumCodeMsg<T>, T> boolean isValidEnumCode(Class<E> enumClass, T code) {
        Optional<E> enumByCode = EnumCodeMsgUtils.getEnumByCode(enumClass, code);
        return enumByCode.isPresent();
    }
}
```

#### **demo**

```java
public static void main(String[] args) {
    //获取msg
    String success = EnumCodeMsgUtils.getEnumMsgByCode(TaskStatusEnumsTwo.class, "success", "");
    log.info("msg:"+success);
	
    //获取是否存在
    boolean validEnumCode = EnumCodeMsgUtils.isValidEnumCode(TaskStatusEnumsTwo.class, "success");
    log.info("validEnumCode:"+validEnumCode);
	
    //获取msg的信息
    Optional<TaskStatusEnumsTwo> codeEnum = EnumCodeMsgUtils.getEnumByCode(TaskStatusEnumsTwo.class, "success");
    codeEnum.ifPresent(taskStatusEnumsTwo -> log.info("getEnumByCode:" + taskStatusEnumsTwo.getCode() + taskStatusEnumsTwo.getEnumMsg()));

 }
```

## **参考文档**

- java枚举工具类
- 深入理解Java枚举类型(enum)
- Enum 枚举小记- by 汪小哥

## **总结**

有上面的方法，如果大规模的使用，对于枚举来说进行了规范，所有的都一样这样对于自己开发来说十分的友好的。不断地总结、才能不断提高自己的编码水平；理论和实践一样重要。





[Enum Tools 减少枚举中重复的方法 (toutiao.com)](https://www.toutiao.com/i6923087713549795854/?tt_from=android_share&timestamp=1616684173&app=news_article&use_new_style=1&req_id=20210325225613010151183030281172A3&share_token=260efad1-cc13-4fec-bacb-ba8ec2ea3e06&group_id=6923087713549795854)