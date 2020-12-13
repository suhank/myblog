# java中高效实现多因素加权排序

2019年8月6日  来源: 网络转载

我正在寻找一种有效的
[Java](http://ddrv.cn/a/tag/Java)加权排序实现.这个问题在某种程度上类似于
[How to provide most relevant results with Multiple Factor Weighted Sorting](https://stackoverflow.com/questions/8760570/how-to-provide-most-relevant-results-with-multiple-factor-weighted-sorting)和
[Need help maximizing 3 factors in multiple, similar objects and ordering appropriately](https://stackoverflow.com/questions/8661118/need-help-maximizing-3-factors-in-multiple-similar-objects-and-ordering-appropr).但是,我要求一些有效实施的指导方针.

在下面的例子中,Person类具有年龄和收入字段,我想根据给定的偏好和降序对具有较低年龄和较高收入组合的arraylist人进行排序.我对年龄和收入提供了相同的偏好.首选项的总和应为1.

正如您在这个天真的实现中所看到的那样,有太多的循环需要迭代,并且最终它的成本太高而无法运行大量的输入.我还探讨了Guava的ComparisonChain和Apache Commons CompareToBuilder,但似乎它们没有实现我的目标.

```java
package main.java.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SortingTest {

static double income_preference = 0.5;
static double age_preference = 1 - income_preference;

public static void main(String args[]) {
    ArrayList<Person> persons = new ArrayList<Person>();

    persons.add(new Person("A", 60, 55.0));
    persons.add(new Person("B", 45, 50.0));
    persons.add(new Person("C", 20, 50.0));
    persons.add(new Person("D", 55, 60.0));
    persons.add(new Person("E", 30, 85.0));

    // Sort the array list by income (descending order)
    Collections.sort(persons, new Comparator<Person>(){
        @Override
        public int compare(Person p1, Person p2) {              
            return (((double)p1.income > (double)p2.income) ? -1 : 
                ((double)p1.income < (double)p2.income) ? 1 : 0);               
        }
    });

    // Rank based on income
    int income_rank = persons.size();
    for(int i = 0; i < persons.size(); i++) {
        if(i != 0)
            if(persons.get(i).income != persons.get(i-1).income)
                --income_rank;

        persons.get(i).income_rank = income_rank * income_preference;
    }

    System.out.println("List of persons sorted by their income in descending order: ");
    for(Person p : persons) 
        System.out.println(p.toString());       

    // Sort the array list by age (ascending order)
    Collections.sort(persons, new Comparator<Person>(){
        @Override
        public int compare(Person p1, Person p2) {              
            return (((double)p2.age > (double)p1.age) ? -1 : 
                ((double)p2.age < (double)p1.age) ? 1 : 0);             
        }
    });

    // Rank based on age
    int age_rank = persons.size();
    for(int i = 0; i < persons.size(); i++) {
        if(i != 0)
            if(persons.get(i).age != persons.get(i-1).age)
                --age_rank;

        persons.get(i).age_rank = age_rank * age_preference;
    }       

    System.out.println();
    System.out.println("List of persons sorted by their age in ascending order: ");
    for(Person p : persons) 
        System.out.println(p.toString());       

    // Assign combined rank     
    for(Person p : persons)
        p.combined_rank = (p.age_rank + p.income_rank);

    // Sort the array list by the value of combined rank (descending order)
    Collections.sort(persons, new Comparator<Person>(){
        @Override
        public int compare(Person p1, Person p2) {              
            return (((double)p1.combined_rank > (double)p2.combined_rank) ? -1 : 
                ((double)p1.combined_rank < (double)p2.combined_rank) ? 1 : 0);             
        }
    });

    System.out.println();
    System.out.println("List of persons sorted by their combined ranking preference in descending order: ");
    for(Person p : persons) 
        System.out.println(p.toString());
}
}

class Person {  
String name;
int age; // lower is better
double income; // higher is better
double age_rank;
double income_rank;
double combined_rank;

public Person(String name, int age, double income) {
    this.name = name;
    this.age = age;
    this.income = income;
    this.age_rank = 0.0;
    this.income_rank = 0.0;
    this.combined_rank = 0.0;
}

@Override
public String toString() {
    return ("Person-"+this.name+", age("+this.age+"|"+this.age_rank+"th), income("+this.income+"|"+this.income_rank+"th), Combined Rank("+this.combined_rank+")");
}
}
```

控制台输出

按收入按降序排序的人员名单：

```
人E,年龄(30 | 0.0th),收入(85.0 | 2.5th),综合排名(0.0)
人D,年龄(55 | 0.0th),收入(60.0 | 2.0th),综合排名(0.0)
人A,年龄(60 | 0.0th),收入(55.0 | 1.5th),综合排名(0.0)
人B,年龄(45 | 0.0th),收入(50.0 | 1.0th),综合排名(0.0)
人C,年龄(20 | 0.0th),收入(50.0 | 1.0th),综合排名(0.0)
```

按年龄按升序排序的人员名单：

```
人C,年龄(20 | 2.5th),收入(50.0 | 1.0th),综合排名(0.0)
人E,年龄(30 | 2.0th),收入(85.0 | 2.5th),综合排名(0.0)
人B,年龄(45 | 1.5th),收入(50.0 | 1.0th),综合排名(0.0)
人D,年龄(55 | 1.0th),收入(60.0 | 2.0th),综合排名(0.0)
人A,年龄(60 | 0.5th),收入(55.0 | 1.5th),综合排名(0.0)
```

按其组合排名首选项按降序排序的人员列表：

```
人E,年龄(30 | 2.0th),收入(85.0 | 2.5th),综合排名(4.5)
人C,年龄(20 | 2.5th),收入(50.0 | 1.0th),综合排名(3.5)
人D,年龄(55 | 1.0th),收入(60.0 | 2.0th),综合排名(3)
人B,年龄(45 | 1.5th),收入(50.0 | 1.0th),综合排名(2.5)
人A,年龄(60 | 0.5th),收入(55.0 | 1.5th),综合排名(2.5)
```

最佳答案 您可以分别维护两个
[TreeSet](https://docs.oracle.com/javase/7/docs/api/java/util/TreeSet.html)用于存储年龄和收入信息,因此您可以轻松地从这两棵树中查询排序时的年龄和收入等级.

我们可以从TreeSet调用tailSet(int)方法来获取大于或等于特定数字的数字列表,在这种情况下,它将是年龄/收入的等级.

```java
TreeSet ageRank = new TreeSet();
TreeSet incomeRank = new TreeSet();

for(Person p : persons){
   ageRank.add(p.getAge());
   incomeRank.add(p.getIncome());
}

Collections.sort(persons, new Comparator<Person>(){
        @Override
        public int compare(Person p1, Person p2) {              
             int ageRank1 = ageRank.tailSet(p1.getAge()).size();
             int ageRank2 = ageRank.tailSet(p2.getAge()).size();
             int incomeRank1 = incomeRank.tailSet(p1.getIncome()).size();
             int incomeRank2 = incomeRank.tailSet(p2.getIncome()).size();
             //Calculate the combined_rank and return result here. Code omitted  

        }
});
```

使用这种方法,一个用于循环的排序将足以用于所有计算.

如果您需要定期更新人员列表,这种方法会派上用场,因为您不需要对年龄和收入进行排序,并在有更新时一次又一次地重新计算所有排名,您只需要更新这两棵树.

注意：为了在用于排序的内部Comparator类中使用ageRank和incomeRank,需要将它们声明为final或实例变量.



代码:  Interview-demo\src\test\java\com\atguigu\algorithm\多因素加权排序\SortingTest.java



http://ddrv.cn/a/369508