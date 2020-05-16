# java 中获取当前运行的类名，方法名，行数

  

```java
package com.cosmos.common.utils;


import org.apache.commons.lang3.StringUtils;

/**
 * <B>Description:</B> 堆栈信息工具类 <br>
 * <B>Create on:</B> 2019-05-16 14:16 <br>
 *
 * @author xiangyu.ye
 * @version 1.0
 */
public class CurrentTraceInfoUtil {
    /**
     * <B>Description:</B> 获取当前堆栈信息(方法名,类名,行号等) <br>
     * <B>Create on:</B> 2019-03-19 16:45 <br>
     *
     * @author xiangyu.ye
     */
    public static CurrentTraceInfo getTraceInfo() {
        StackTraceElement[] stacks = new Throwable().getStackTrace();
        CurrentTraceInfo traceInfo = new CurrentTraceInfo();
        traceInfo.setClassName(stacks[1].getClassName());
        traceInfo.setMethodName(stacks[1].getMethodName());
        traceInfo.setLineNumber(stacks[1].getLineNumber());
        traceInfo.setStacksLen(stacks.length);
        return traceInfo;
    }


    /**
     * <B>Description:</B> 获取类名和方法名字符串格式(类名.方法名) <br>
     * <B>Create on:</B> 2019-05-16 14:18 <br>
     *
     * @author xiangyu.ye
     */
    public static String getClassNameAndMethodName() {
        StackTraceElement[] stacks = new Throwable().getStackTrace();
        String ClassName = StringUtils.removeEnd(stacks[1].getFileName(), ".java");
        return ClassName + "." + stacks[1].getMethodName();

    }


    public static class CurrentTraceInfo {
        private String className;//类名
        private String methodName;//方法名
        private int lineNumber;//行号
        private int stacksLen;//堆栈深度

        @Override
        public String toString() {
            return "CurrentTraceInfo{" +
                    "className='" + className + '\'' +
                    ", methodName='" + methodName + '\'' +
                    ", lineNumber='" + lineNumber + '\'' +
                    ", stacksLen=" + stacksLen +
                    '}';
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public String getMethodName() {
            return methodName;
        }

        public void setMethodName(String methodName) {
            this.methodName = methodName;
        }

        public int getLineNumber() {
            return lineNumber;
        }

        public void setLineNumber(int lineNumber) {
            this.lineNumber = lineNumber;
        }

        public int getStacksLen() {
            return stacksLen;
        }

        public void setStacksLen(int stacksLen) {
            this.stacksLen = stacksLen;
        }
    }
}

```

 



https://www.cnblogs.com/stono/p/5663627.html