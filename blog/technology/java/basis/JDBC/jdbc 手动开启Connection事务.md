# jdbc 手动开启Connection事务

2013年06月28日 08:44:30 [温欢](https://me.csdn.net/wwwwenhuan) 阅读数：4843

​        三层架构中的业务逻辑层是处理业务逻辑的部分，很多时候需要调用多步Dao层的增删改操作，这就涉及到使用事务保证数据的一致性。

​       Connection接口自带的事务机制需要保证多步SQL操作使用相同的连接对象，这样才能保证事务的执行环境。

​       事务的边界一般是在业务逻辑层的(即事务的开启、提交、回滚都是在业务逻辑层)，因为业务逻辑层会涉及多步操作，所以Connection对象要在业务逻辑层创建，然后将Connection对象传给Dao层的方法即可。

​       为了确保事务的正确性，异常要统一在业务逻辑层处理



下面展示使用Connection启动事务的具体代码

```java
	/**
	 * 开始事务
	 * @param cnn
	 */
	public static void beginTransaction(Connection cnn){
		if(cnn!=null){
			try {
				if(cnn.getAutoCommit()){
					cnn.setAutoCommit(false);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 提交事务
	 * @param cnn
	 */
	public static void commitTransaction(Connection cnn){
		if(cnn!=null){
			try {
				if(!cnn.getAutoCommit()){
					cnn.commit();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 回滚事务
	 * @param cnn
	 */
	public static void rollBackTransaction(Connection cnn){
		if(cnn!=null){
			try {
				if(!cnn.getAutoCommit()){
					cnn.rollback();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

```

​       这三个方法一般会放在一个公共类DbUtil中，方法都是静态的，可通过类名.方法直接调用。只需将连接作为参数传给这些方法即可，以下为使用上述方法创建事务环境的代码框架。

```java
public void funcExample(){
	Connection cnn=DbUtil.getConnection();
	try{
		//开启事务
		DbUtil.beginTransaction(cnn);
	
		//调用Dao层多个增删改方法
		//..........
		
		//提交事务
		DbUtil.commitTransaction(cnn);
	}catch(Exception e){
		e.printStackTrace();
		//回滚事务
		DbUtil.rollBackTransaction(cnn);
	}finally{
		//还原连接状态
		DbUtil.resetConnection(cnn);
		DbUtil.close(pstm);
		DbUtil.close(cnn);
	}
	
}

```

刚接触JDBC不久，小结一下。





https://blog.csdn.net/wzwenhuan/article/details/9193337