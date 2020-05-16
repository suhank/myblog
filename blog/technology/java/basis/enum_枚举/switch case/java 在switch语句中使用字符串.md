 

# java 在switch语句中使用字符串

在java代码中，switch语句以传入的字符串参数作为判断条件，在对应的case子句中使用的是字符串常量。  

```java
public class Test {
        public static void main(String[] args) {
            System.out.println(new Title().generate( "男"));

        }
}
class Title {
    public String generate(String gender) {
        String title = "";
        switch (gender) {
            case "男":
                title =  "先生";
                break;
            case "女":
                title =  "女士";
                break;
            default:
                title = "none";
        }
        return title;
    }
}

```

 



https://www.cnblogs.com/baorantHome/p/6896280.html