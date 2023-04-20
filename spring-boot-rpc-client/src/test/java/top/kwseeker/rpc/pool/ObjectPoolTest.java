package top.kwseeker.rpc.pool;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.junit.Test;

import java.sql.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 使用commons-pool作为jdbc连接对象池的Demo
 */
public class ObjectPoolTest {

    @Test
    public void testObjectPool() throws Exception {
        String url = "jdbc:mysql://localhost:3306/employees";
        String username = "root";
        String password = "123456";
        JdbcConnectionFactory factory = new JdbcConnectionFactory(url, username, password);
        GenericObjectPoolConfig<Connection> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMaxTotal(4);
        poolConfig.setMaxIdle(2);
        poolConfig.setTestOnBorrow(true);   //取对象后执行工厂方法校验对象是否可用 validateObject

        JdbcConnectionPool connectionPool = new JdbcConnectionPool(factory, poolConfig);

        String query = "SELECT * FROM employees WHERE emp_no = ? for update";
        final int empNo = 10001;

        Runnable queryTask = () -> {
            Connection conn = null;
            try {
                conn = connectionPool.getConnection();
                conn.setAutoCommit(false);  //开启事务

                TimeUnit.SECONDS.sleep(3);

                PreparedStatement statement = conn.prepareStatement(query);
                statement.setInt(1, empNo);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    int employeeNo = resultSet.getInt(1);
                    Date birthDate = resultSet.getDate(2);
                    String firstName = resultSet.getString(3);
                    String lastName = resultSet.getString(4);
                    String gender = resultSet.getString(5);    //枚举也用String类型读取
                    Date hireDate = resultSet.getDate(6);
                    System.out.println("conn" + conn.hashCode() + " " + employeeNo + " " + birthDate + " " + firstName + " " + lastName + " " + gender + " " + hireDate);
                }

                conn.commit();
                conn.setAutoCommit(true);
            } catch (Exception exception) {
                exception.printStackTrace();
            } finally {
                if (conn != null) {
                    try {
                        connectionPool.releaseConnection(conn);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            }
        };

        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(4);
        executor.execute(queryTask);

        TimeUnit.SECONDS.sleep(1);
        for (int i = 0; i < 3; i++) {
            executor.execute(queryTask);
        }

        TimeUnit.SECONDS.sleep(1);

        //----------------
        //这时一个线程在sleep, 另外3个在等待记录锁
        System.out.println("numActive: " + connectionPool.getNumActive());
        System.out.println("numIdle: " + connectionPool.getNumIdle());
        System.out.println("maxTotal: " + connectionPool.getMaxTotal());
        System.out.println("maxIdle: " + connectionPool.getMaxIdle());

        executor.awaitTermination(1000, TimeUnit.SECONDS);
        connectionPool.close();
    }

    public static class JdbcConnectionFactory extends BasePooledObjectFactory<Connection> {

        private final String url;
        private final String username;
        private final String password;

        public JdbcConnectionFactory(String url, String username, String password) {
            this.url = url;
            this.username = username;
            this.password = password;
        }

        /**
         * 对象池中没有可用对象可以通过此方法新建对象
         */
        @Override
        public Connection create() throws Exception {
            //创建sql连接
            System.out.println("create object");
            return DriverManager.getConnection(url, username, password);
        }

        @Override
        public PooledObject<Connection> wrap(Connection connection) {
            return new DefaultPooledObject<>(connection);
        }

        /**
         * 销毁连接对象
         * 对象池将对象从池中删除，然后执行销毁
         */
        @Override
        public void destroyObject(PooledObject<Connection> conn) throws Exception {
            System.out.println("destroyObject");
            Connection rawConn = conn.getObject();
            rawConn.close();
        }

        /**
         * 校验对象
         * 比如数据库连接：查看连接是否关闭
         */
        @Override
        public boolean validateObject(PooledObject<Connection> conn) {
            try {
                return !conn.getObject().isClosed();
            } catch (SQLException e) {
                return false;
            }
        }
    }

    public static class JdbcConnectionPool extends GenericObjectPool<Connection> {

        public JdbcConnectionPool(JdbcConnectionFactory factory, GenericObjectPoolConfig<Connection> poolConfig) {
            super(factory, poolConfig);
        }

        public Connection getConnection() throws Exception {
            System.out.println("borrowObject");
            return borrowObject();
        }

        public void releaseConnection(Connection conn) throws Exception {
            System.out.println("returnObject");
            returnObject(conn);
        }
    }
}
