[TOC]



# javascript switch语句的技巧

## 概述

switch语句对一个表达式求值，将结果与 case 子语句比较，如果匹配，则从 case 处的语句向下执行。

## 语法

`break;`语句是可选择的，如果遇到`break;`则会跳出整个`switch`语句。如果没有任何case匹配，则进入到`default:`的分支。`default:`分支也是可选的。

```javascript
switch (expression) {
  case value1:
    // 当 expression 的结果与 value1 匹配时，从此处开始执行
    statements1；
    [break;]
  case value2:
    // 当 expression 的结果与 value2 匹配时，从此处开始执行
    statements2;
    [break;]
  ...
  case valueN:
    // 当 expression 的结果与 valueN 匹配时，从此处开始执行
    statementsN;
    [break;]
  default:
    // 如果 expression 与上面的 value 值都不匹配时，执行此处的语句
    statements_def;
    [break;]
}
```

## switch语句的技巧

### case中使用条件判断

看看下面的代码，当foo为0，1，2，3的时候显示alert。

```javascript
var foo = 1;
switch (foo) {
    case 0:
    case 1:
    case 2:
    case 3:
        alert('yes');
        break;
    default:
        alert('not');
}
```

有没有更好的写法呢？下面这个显然更简洁清晰啊。

```javascript
var foo = 1;
switch (true) { // 非变量 TRUE 替代 foo
    case foo >= 0 && foo <= 3:
        alert('yes');
        break;
    default:
        alert('not');
}
```

### 表示等级

精心设计的switch把*最少最稀有的条件*在上面，普通的条件放在相对下面的位置

```javascript
function rankProgrammer(rank){ 
    switch(rank){ 
      case "高级": 
        this.secretary = true;
      case "中级": 
        this.laptop = true;
        this.bonus = true;
      case "初级": 
        this.salary = true;
        this.vacation = true; 
    }
}
var xiaohu=new rankProgrammer("高级");
console.log(xiaohu);
```

上面这段程序显示出“高级”程序猿拥有所有的待遇，而初级程序员只有工资和假期。





<https://www.jianshu.com/p/3efebd2c785d>