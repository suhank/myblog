[TOC]



# Guava之CaseFormat 下划线与驼峰命名转换

com.google.common.base.CaseFormat是一种实用工具类，以提供不同的ASCII字符格式之间的转换。

```
@GwtCompatible
public enum CaseFormat
   extends Enum<CaseFormat>
```

### 枚举常量

| S.N. | 枚举常量和说明                                               |
| ---- | ------------------------------------------------------------ |
| 1    | LOWER_CAMEL  Java变量的命名规则，如“lowerCamel”。            |
| 2    | LOWER_HYPHEN  连字符连接变量的命名规则，如“lower-hyphen”。   |
| 3    | LOWER_UNDERSCORE  C ++变量命名规则，如“lower_underscore”。   |
| 4    | UPPER_CAMEL  Java和C++类的命名规则，如“UpperCamel”。         |
| 5    | UPPER_UNDERSCORE  Java和C++常量的命名规则，如“UPPER_UNDERSCORE”。 |

### 方法

| S.N. | 方法及说明                                                   |
| ---- | ------------------------------------------------------------ |
| 1    | Converter<String,String> converterTo(CaseFormat targetFormat  返回一个转换，从这个格式转换targetFormat字符串。 |
| 2    | String to(CaseFormat format, String str)  从这一格式指定格式的指定字符串 str 转换。 |
| 3    | static CaseFormat valueOf(String name)  返回此类型具有指定名称的枚举常量。 |
| 4    | static CaseFormat[] values()  返回一个包含该枚举类型的常量数组中的顺序被声明。 |

### 继承的方法

这个类继承了以下类方法：

- java.lang.Enum
- java.lang.Object

### CaseFormat 示例

GuavaTester.java

```java
import com.google.common.base.CaseFormat;

public class GuavaTester {
   public static void main(String args[]) {
        CaseFormatTest tester = new CaseFormatTest();
        tester.testCaseFormat();
    }

    private void testCaseFormat() {
        System.out.println(CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL, "test-data"));
        System.out.println(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, "test_data"));
        System.out.println(CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, "test_data"));
        
        System.out.println(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, "testdata"));
        System.out.println(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, "TestData"));
        System.out.println(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, "testData"));
    }
}
```

结果：

```
testData
testData
TestData
testdata
test_data
test-data
```





https://www.jianshu.com/p/868bc704433a