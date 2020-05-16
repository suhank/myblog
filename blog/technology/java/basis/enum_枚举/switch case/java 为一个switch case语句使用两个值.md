# java 为一个switch case语句使用两个值



在我的代码中，程序根据用户输入的文本执行某些操作。我的代码看起来像：

```java
switch (name) {
        case text1: {
            //blah
            break;
        }
        case text2: {
            //blah
            break;
        }
        case text3: {
            //blah
            break;
        }
        case text4: {
            //blah
            break;
        }
```

但是，案例text1和text4内的代码是相同的。因此，我想知道是否有可能实现类似的东西

```
case text1||text4: {
            //blah
            break;
        }
```

我知道||运算符不会在case语句中工作，但有一些类似的，我可以使用。

您可以使用两个CASE语句如下。

```
  case text1: 
  case text4:{
            //blah
            break;
        }
```

请参见示例：代码示例计算特定月份的天数：

```java
class SwitchDemo {
    public static void main(String[] args) {

        int month = 2;
        int year = 2000;
        int numDays = 0;

        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                numDays = 31;
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                numDays = 30;
                break;
            case 2:
                if (((year % 4 == 0) && 
                     !(year % 100 == 0))
                     || (year % 400 == 0))
                    numDays = 29;
                else
                    numDays = 28;
                break;
            default:
                System.out.println("Invalid month.");
                break;
        }
        System.out.println("Number of Days = "
                           + numDays);
    }
}
```

这是代码的输出：

```
Number of Days = 29
```

  http://docs.oracle.com/javase/tutorial/java/nutsandbolts/switch.html)





<https://codeday.me/bug/20170526/19445.html>