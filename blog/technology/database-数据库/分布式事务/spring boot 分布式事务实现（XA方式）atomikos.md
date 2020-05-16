# spring boot 分布式事务实现（XA方式）atomikos

关于spring boot 支持分布式事务，XA是常用的一种方式。

这里把相关的配置记下，方便以后使用。

首先配置两个不同的数据源 : 订单库、持仓库。

```java
/**
 * Created by zhangjunwei on 2017/8/2.
 */
@Configuration
public class DataSourceConfig {


    /**
     * db1的 XA datasource
     *
     * @return
     */
    @Bean(name = "symbolOrder")
    @Primary
    @Qualifier("symbolOrder")
    public AtomikosDataSourceBean symbolOrderBean() {
        AtomikosDataSourceBean atomikosDataSourceBean = new AtomikosDataSourceBean();
        atomikosDataSourceBean.setUniqueResourceName("symbolOrder");
        atomikosDataSourceBean.setXaDataSourceClassName(
                "com.mysql.jdbc.jdbc2.optional.MysqlXADataSource");
        Properties properties = new Properties();
        properties.put("URL","jdbc:mysql://localhost:3306/datamanage");
        properties.put("user", "root");
        properties.put("password", "123456");
        atomikosDataSourceBean.setXaProperties(properties);
        return atomikosDataSourceBean;
    }


    /**
     * db2的 XA datasource
     *
     * @return
     */
    @Bean(name = "symbolPosition")
    @Qualifier("symbolPosition")
    public AtomikosDataSourceBean symbolPositionDataSourceBean() {
        AtomikosDataSourceBean atomikosDataSourceBean = new AtomikosDataSourceBean();
        atomikosDataSourceBean.setUniqueResourceName("symbolPosition");
        atomikosDataSourceBean.setXaDataSourceClassName(
                "com.mysql.jdbc.jdbc2.optional.MysqlXADataSource");
        Properties properties = new Properties();
        properties.put("URL", "jdbc:mysql://localhost:3306/symbol_position");
        properties.put("user", "root");
        properties.put("password", "123456");
        atomikosDataSourceBean.setXaProperties(properties);
        return atomikosDataSourceBean;
    }

    /**
     * transaction manager
     *
     * @return
     */
    @Bean(destroyMethod = "close", initMethod = "init")
    public UserTransactionManager userTransactionManager() {
        UserTransactionManager userTransactionManager = new UserTransactionManager();
        userTransactionManager.setForceShutdown(true);
        return userTransactionManager;
    }

    /**
     * jta transactionManager
     *
     * @return
     */
    @Bean
    public JtaTransactionManager transactionManager() {
        JtaTransactionManager jtaTransactionManager = new JtaTransactionManager();
        jtaTransactionManager.setTransactionManager(userTransactionManager());
        return jtaTransactionManager;
    }

}
```

 

顺便把相关的依赖贴上，值得注意的是  spring-boot-starter-jta-atomikos 依赖，这是一个开源的事务管理器类。

```
<dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
 
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
        </dependency>
 
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-jdbc</artifactId>
        </dependency>
 
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
 
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-jta-atomikos</artifactId>
        </dependency>
    </dependencies>
```

　　 

 以上就是全部配置了。接着我们来写测试用例，看下效果怎么样。

模拟场景：用户下单成功后，他的账户持仓应该对应增加，如果持仓更新失败，则他的下单操作也需要回滚。

```
/**
 * Created by zhangjunwei on 2017/8/2.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = UserManageStart.class)
public class DataSouceTest {
    @Autowired
    @Qualifier("symbolOrder")
    private AtomikosDataSourceBean symbolOrder;
 
    @Autowired
    @Qualifier("symbolPosition")
    private AtomikosDataSourceBean symbolPosition;
 
 
    @Transactional
    @Test
    public void test() {
 
        Connection orderConnection  = null;
        Connection positionConnection = null;
        try {
             orderConnection = symbolOrder.getConnection();
            String sql = "insert into order_symbol (accountId,symbol,amount,price,orderTime) values " +
                    "({0},''{1}'',{2},{3},''{4}'')";
            sql = MessageFormat.format(sql,4,"000004.SZ",100,(float)5.5,"2017-07-27 14:31:00");
            PreparedStatement orderStatement = orderConnection.prepareStatement(sql);
            orderStatement.execute();
 
            positionConnection = symbolPosition.getConnection();
            sql = "insert into hq_position (accountId,symbol,amount) values " +
                    "({0},''{1}'',{2})";
            sql = MessageFormat.format(sql,4,"000002.SZ",200);
            PreparedStatement positionStatement = positionConnection.prepareStatement(sql);
            positionStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (orderConnection != null) {
                    orderConnection.close();
                }
                if (positionConnection != null) {
                    positionConnection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
 
}
```

　　



https://www.cnblogs.com/zhangjwcode/p/7274437.html