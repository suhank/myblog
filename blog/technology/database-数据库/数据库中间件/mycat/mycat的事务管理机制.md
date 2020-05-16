[TOC]



# mycat的事务管理机制

 

## **Mycat事务源码分析**

 

Mycat的事务相关的代码逻辑，目前的实现方式如下：

用户会话Session中设定autocommit=false，开启一个事务过程，这个会话中随后的所有SQL语句进入事务模式，ServerConnection（前端连接）中有一个变量txInterrupted控制是否事务异常需要回滚

当某个SQL执行过程中发生错误，则设置txInterrupted=true，表明此事务需要回滚

当用户提交事务（commit指令）的时候，Session会检查事务回滚变量，若发现事务需要回滚，则取消Commit指令在相关节点上的执行过程，返回错误信息，Transaction need rollback，用户只能回滚事务，若所有节点都执行成功，则向每个节点发送Commit指令，事务结束。

从上面的逻辑来看，当前Mycat的事务是一种弱XA的事务，与XA事务相似的地方是，只有所有节点都执行成功（Prepare阶段都成功），才开始提交事务，与XA不同的是，在提交阶段，若某个节点宕机，没有手段让此事务在故障节点恢复以后继续执行，从实际的概率来说，这个概率也是很小很小的，因此，当前事务的方式还是能满足绝大数系统对事务的要求。 

另外，Mycat当前弱XA的事务模式，相对XA还是比较轻量级，性能更好，虽然如此，也不建议一个事务中存在跨多个节点的SQL操作问题，这样锁定的资源更多，并发性降低很多。

 

前端连接中关于事务标记txInterrupted的方法片段：

```java
public class ServerConnection extends FrontendConnection {
    /** * 设置是否需要中断当前事务 */
    public void setTxInterrupt(String txInterrputMsg) {
        if (!autocommit && !txInterrupted) {
            txInterrupted = true;

            this.txInterrputMsg = txInterrputMsg;
        }
    }

    public boolean isTxInterrupted() {
        return txInterrupted;
    }

    /** * 提交事务 */
    public void commit() {
        if (txInterrupted) {
            writeErrMessage(ErrorCode.ER_YES,
                "Transaction error, need to rollback.");
        } else {
            session.commit();
        }
    }
}

```

SQL出错时候设置事务回滚标志：

 

```java
public class SingleNodeHandler implements ResponseHandler, Terminatable,
    LoadDataResponseHandler {
    private void backConnectionErr(ErrorPacket errPkg, BackendConnection conn) {
        endRunning();

        String errmgs = " errno:" + errPkg.errno + " " +
            new String(errPkg.message);

        LOGGER.warn("execute  sql err :" + errmgs + " con:" + conn);

        session.releaseConnectionIfSafe(conn, LOGGER.isDebugEnabled(), false);

        ServerConnection source = session.getSource();

        source.setTxInterrupt(errmgs);

        errPkg.write(source);

        recycleResources();
    }
}

```

 

Session提交事务的关键代码：

```java
public class NonBlockingSession implements Session {
    public void commit() {
        final int initCount = target.size();

        if (initCount <= 0) {
            ByteBuffer buffer = source.allocate();

            buffer = source.writeToBuffer(OkPacket.OK, buffer);
            source.write(buffer);

            return;
        } else if (initCount == 1) {
            BackendConnection con = target.elements().nextElement();
            commitHandler.commit(con);
        } else {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("multi node commit to send ,total " + initCount);
            }

            multiNodeCoordinator.executeBatchNodeCmd(SQLCmdConstant.COMMIT_CMD);
        }
    }
}

```

 

 

https://my.oschina.net/u/2836632/blog/704598