[TOC]



# mycat 临时解决utf8mb4编码问题

### 1，关于utf8mb4

utf8mb4 is a superset of utf8 
utf8mb4兼容utf8，且比utf8能表示更多的字符。 
emoji就是表情符号；词义来自日语（えもじ，e-moji，moji在日语中的含义是字符） 
　　表情符号现已普遍应用于手机短信和网络聊天软件。 
　　emoji表情符号，在外国的手机短信里面已经是很流行使用的一种表情。

### 2，mycat中支持utf8mb4

走了很多多弯路。理论上只要在server.xml的charset设置下就行，但是没有这么简单。直接连接不上了。

同样配置 utf8 gbk是可以的，但是感觉上utf8mb4这个编码太特殊了，mycat判断可能有bug。

终极必杀：修改源代码，org.opencloudb.mysql.nio.MySQLConnection： 450 行。

```
    private static CommandPacket getCharsetCommand(int ci) {
            String charset = CharsetUtil.getCharset(ci);
            StringBuilder s = new StringBuilder();
            LOGGER.info("################## MySQLConnection getCharsetCommand: "+ci+"\t|"+charset);
            s.append("SET names utf8mb4 ");//.append(charset);
            CommandPacket cmd = new CommandPacket();
            cmd.packetId = 0;
            cmd.command = MySQLPacket.COM_QUERY;
            cmd.arg = s.toString().getBytes();
            return cmd;
        } 
```

解决办法，直接写死连接设置的编码。当然mysql服务器这边也要修改编码。 
vi /etc/my.cnf

```
[client]
default-character-set = utf8mb4

[mysql]
default-character-set = utf8mb4

[mysqld]
character-set-client-handshake = FALSE
character-set-server = utf8mb4
collation-server = utf8mb4_unicode_ci
init_connect='SET NAMES utf8mb4' 
```

客户端查看编码

```
SHOW VARIABLES LIKE 'character%'
+--------------------------+--------------------+
| Variable_name            | Value              |
+--------------------------+--------------------+
| character_set_client    | utf8mb4            |
| character_set_connection | utf8mb4            |
| character_set_database  | utf8mb4            |
| character_set_filesystem | binary            |
| character_set_results    | utf8mb4            |
| character_set_server    | utf8mb4            |
| character_set_system    | utf8              |
+--------------------------+--------------------+
rows in set (0.00 sec) 
```

jdbc修改连接：

```
jdbc.driverClassName=com.mysql.jdbc.Driver
jdbc.url=jdbc:mysql://localhost:3306/database?useUnicode=true&characterEncoding=utf8&autoReconnect=true&rewriteBatchedStatements=TRUE
jdbc.username=root
jdbc.password=password 
```

修改数据表：（数据库和表字段修改）

```
ALTER DATABASE database_name CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
ALTER TABLE table_name CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE table_name CHANGE column_name VARCHAR(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci; 
```

<http://www.tutorialspoint.com/jdbc/jdbc-select-records.htm> 
测试连接数据库查看字符编码：

```
//STEP 1. Import required packages
import java.sql.*;

public class JDBCExample {
   // JDBC driver name and database URL
   static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
   static final String DB_URL = "jdbc:mysql://localhost/STUDENTS";

   //  Database credentials
   static final String USER = "username";
   static final String PASS = "password";

   public static void main(String[] args) {
   Connection conn = null;
   Statement stmt = null;
   try{
      //STEP 2: Register JDBC driver
      Class.forName("com.mysql.jdbc.Driver");

      //STEP 3: Open a connection
      System.out.println("Connecting to a selected database...");
      conn = DriverManager.getConnection(DB_URL, USER, PASS);
      System.out.println("Connected database successfully...");

      //STEP 4: Execute a query
      System.out.println("Creating statement...");
      stmt = conn.createStatement();

      String sql = " show variables like 'character%' ";
      ResultSet rs = stmt.executeQuery(sql);
      //STEP 5: Extract data from result set
      while (rs.next()) {
                //Display values
                System.out.print(rs.getString(1));
                System.out.print("\t");
                System.out.print(rs.getString(2));

                System.out.println();
            }
      rs.close();
   }catch(SQLException se){
      //Handle errors for JDBC
      se.printStackTrace();
   }catch(Exception e){
      //Handle errors for Class.forName
      e.printStackTrace();
   }finally{
      //finally block used to close resources
      try{
         if(stmt!=null)
            conn.close();
      }catch(SQLException se){
      }// do nothing
      try{
         if(conn!=null)
            conn.close();
      }catch(SQLException se){
         se.printStackTrace();
      }//end finally try
   }//end try
   System.out.println("Goodbye!");
}//end main
}//end JDBCExample
```

如果返回utf8mb4即可：

```
character_set_client    utf8mb4
character_set_connection    utf8mb4
character_set_database  utf8mb4
character_set_filesystem    binary
character_set_results   utf8mb4
character_set_server    utf8mb4
character_set_system    utf8
character_sets_dir  /usr/share/mysql/charsets/
```

更多关于mysql 字符集：<http://www.laruence.com/2008/01/05/12.html>

### 3，总结：

本文原文连接: <http://blog.csdn.net/freewebsys/article/details/45537411> 转载请注明出处！

需要注意的只有mysql 5.5才支持utf8mb4字符集。 
centos安装 mysql 5.5 参考：<http://stackoverflow.com/questions/9361720/update-mysql-version-from-5-1-to-5-5-in-centos-6-2> 
。 
mycat设计的还是挺好的，也很稳定的。开源的好处是可以自己研究代码。非常高兴。





https://blog.csdn.net/freewebsys/article/details/45537411