# JDBC批量执行SQL

## 摘要：

只创建一次Statement，然后addBatch多条SQL，最后一起执行。

批量执行SQL，效率比较高，但执行SQL太多，谨防内存溢出。



## **代码案例一：**

```java
package com.what21.jdbc.demo05;
 
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
 
public class JDBCDemo {
 
    /**
     * @param args
     */
    public static void main(String[] args) {
        // 1. 创建连接
        Connection connection = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/demo","root","123124");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Statement stat = null;
        try {
            // 2. 创建Statement
            stat = connection.createStatement();
            // 3. 执行SQL
            String sql = "insert into users(name,email,phone,mobile) value('name','email','phone','mobile')";
            String sql2 = "insert into users(name,email,phone,mobile) value('name2','email2','phone','mobile')";
            stat.addBatch(sql);
            stat.addBatch(sql2);
            int[] results = stat.executeBatch();
            for(int result : results){
                System.out.println(result);
            }
            stat.clearBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // 5. 关闭连接
        if(stat!=null){
            try {
                stat.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            stat=null;
        }
        if(connection!=null){
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            connection=null;
        }
    }
 
}
```

## **代码案例二：**

```java
package com.what21.jdbc.demo05;
 
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
 
public class JDBCDemo2 {
 
    /**
     * @param args
     */
    public static void main(String[] args) {
        // 1. 创建连接
        Connection connection = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/demo","root","123124");
        } catch (Exception e) {
            e.printStackTrace();
        }
        PreparedStatement pstat = null;
        try {
            String sql = "insert into users(name,email,phone,mobile) value(?,?,?,?)";
            // 2. 创建PreparedStatement
            pstat = connection.prepareStatement(sql);
            pstat.setString(1, "_name");
            pstat.setString(2, "_email");
            pstat.setString(3, "_phone");
            pstat.setString(4, "_mobile");
            pstat.addBatch();
            pstat.setString(1, "_name2");
            pstat.setString(2, "_email2");
            pstat.setString(3, "_phone2");
            pstat.setString(4, "_mobile2");
            pstat.addBatch();
            // 3. 执行
            int[] results = pstat.executeBatch();
            for(int result : results){
                System.out.println(result);
            }
            // 4. 处理返回结果（这里无结果）
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // 5. 关闭连接
        if(pstat!=null){
            try {
                pstat.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            pstat=null;
        }
        if(connection!=null){
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            connection=null;
        }
    }
 
}
```





http://www.what21.com/sys/view/java_jdbc_1456896129893.html