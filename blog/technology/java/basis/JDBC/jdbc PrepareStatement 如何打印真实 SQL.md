# PrepareStatement 如何打印真实 SQL

 

我们知道，JDBC 的 PrepareStatement 优点多多，通常都是推荐使用 PrepareStatement 而不是其基类 Statment。PrepareStatement 支持 ? 占位符，可以将参数按照类型转自动换为真实的值。既然这一过程是自动的，封装在 JDBC 内部的，那么我们外部就不得而知目标的 SQL 最终生成怎么样——于是在调试过程中便有一个打印 SQL 的问题。我们对 PrepareStatement 传入 SQL 语句，如 SELECT * FROM table WHERE id = ?，然后我们传入对应的 id 参数，假设是 id = 10，那怎么把得到参数的 SELECT * FROM table WHERE id =  12 结果完整地得出来呢？——这便是本文所要探讨的问题。

首先，我们看看典型的一个 PrepareStatement 调用方法，如下一个函数，

```java
/**
 * 查询单个结果，保存为 Map<String, Object> 结构。如果查询不到任何数据返回 null。
 * 
 * @param conn
 *            数据库连接对象
 * @param sql
 *            SQL 语句，可以带有 ? 的占位符
 * @param params
 *            插入到 SQL 中的参数，可单个可多个可不填
 * @return Map<String, Object> 结构的结果。如果查询不到任何数据返回 null。
 */
public static Map<String, Object> query(Connection conn, String sql, Object... params) {
	Map<String, Object> map = null;
	printRealSql(sql, params); // 打印真实 SQL 的函数
	
	try (PreparedStatement ps = conn.prepareStatement(sql);) {
		if(params != null)
			for (int i = 0; i < params.length; i++) 
				ps.setObject(i + 1, params[i]);
		
		try (ResultSet rs = ps.executeQuery();) {
			if (rs.isBeforeFirst()) {
				map = getResultMap(rs);
			} else {
				LOGGER.info("查询 SQL：{0} 没有符合的记录！", sql);
			}
		}
	} catch (SQLException e) {
		LOGGER.warning(e);
	}
	
	return map;
}

```

值得注意该函数里面：

```java
printRealSql(sql, params); // 打印真实 SQL 的函数
```



其参数一 sql 就是类似 SELECT * FROM table WHERE id = ? 的语句，参数二 params 为 Object... params 的参数列表，可以是任意类似的合法 SQL 值。最后，通过 printRealSql 函数最终得出形如 SELECT * FROM table WHERE id =  12 的结果。

printRealSql 函数源码如下：



```java
/**
 * 在开发过程，SQL语句有可能写错，如果能把运行时出错的 SQL 语句直接打印出来，那对排错非常方便，因为其可以直接拷贝到数据库客户端进行调试。
 * 
 * @param sql
 *            SQL 语句，可以带有 ? 的占位符
 * @param params
 *            插入到 SQL 中的参数，可单个可多个可不填
 * @return 实际 sql 语句
 */
public static String printRealSql(String sql, Object[] params) {
	if(params == null || params.length == 0) {
		LOGGER.info("The SQL is------------>\n" + sql);
		return sql;
	}
	
	if (!match(sql, params)) {
		LOGGER.info("SQL 语句中的占位符与参数个数不匹配。SQL：" + sql);
		return null;
	}
 
	int cols = params.length;
	Object[] values = new Object[cols];
	System.arraycopy(params, 0, values, 0, cols);
 
	for (int i = 0; i < cols; i++) {
		Object value = values[i];
		if (value instanceof Date) {
			values[i] = "'" + value + "'";
		} else if (value instanceof String) {
			values[i] = "'" + value + "'";
		} else if (value instanceof Boolean) {
			values[i] = (Boolean) value ? 1 : 0;
		}
	}
	
	String statement = String.format(sql.replaceAll("\\?", "%s"), values);
 
	LOGGER.info("The SQL is------------>\n" + statement);
 
	ConnectionMgr.addSql(statement); // 用来保存日志
	
	return statement;
}
 
/**
 * ? 和参数的实际个数是否匹配
 * 
 * @param sql
 *            SQL 语句，可以带有 ? 的占位符
 * @param params
 *            插入到 SQL 中的参数，可单个可多个可不填
 * @return true 表示为 ? 和参数的实际个数匹配
 */
private static boolean match(String sql, Object[] params) {
	if(params == null || params.length == 0) return true; // 没有参数，完整输出
	
	Matcher m = Pattern.compile("(\\?)").matcher(sql);
	int count = 0;
	while (m.find()) {
		count++;
	}
	
	return count == params.length;
}

```

可见，上述思路是非常简单的，——有多少个 ? 占位符，就要求有多少个参数，然后一一对照填入（数组）。match 函数会检查第一个步骤，检查个数是否匹配，否则会返回“SQL 语句中的占位符与参数个数不匹配”的提示；然后，参数的值会被转换为符合 SQL 值所要求的类型；最后，就是将 SQL 一一填入，——此处使用了一个字符串的技巧，先把 ? 字符通通转换为 %s，——那是 String.format 可识别的占位符，如此再传入 Object[] 参数列表，即可得出我们期待的 SQL 结果。

我们不能保证那 SQL 可以直接放到数据库中被解析。因为我们的初衷只是把 SQL 打印出来，务求更近一步让程序员在开发阶段看到 SQL 是怎么样子的，而且不是一堆 ？、？……，这样会显得更符合真实情形一点。

PrepareStatement 内部源码肯定有这一步骤或者某个变量是表示那个真实 SQL 的，——只是没有暴露出来。如果有，那么对程序员会更友好一些。





https://blog.csdn.net/zhangxin09/article/details/70187712