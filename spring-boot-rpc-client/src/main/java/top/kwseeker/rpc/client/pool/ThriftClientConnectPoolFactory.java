//package top.kwseeker.rpc.client.pool;
//
//import org.apache.commons.pool2.BasePooledObjectFactory;
//import org.apache.commons.pool2.PooledObject;
//import org.apache.commons.pool2.impl.DefaultPooledObject;
//import org.apache.commons.pool2.impl.GenericObjectPool;
//import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//public class ThriftClientConnectPoolFactory {
//
//    private static final Logger log = LoggerFactory.getLogger(ThriftClientConnectPoolFactory.class);
//
//    /**
//     * 对象池
//     */
//    private final GenericObjectPool<TTSocket> pool;
//
//    /**
//     * 实例化池工厂帮助类
//     */
//    public ThriftClientConnectPoolFactory(GenericObjectPoolConfig<TTSocket> config, String ip, int port) {
//        ConnectionFactory factory = new ConnectionFactory(ip, port);
//        //实例化池对象
//        this.pool = new GenericObjectPool<>(factory, config);
//        //设置获取对象前校验对象是否可以
//        this.pool.setTestOnBorrow(true);
//    }
//
//    /**
//     * 在池中获取一个空闲的对象
//     * 如果没有空闲且池子没满，就会调用makeObject创建一个新的对象
//     * 如果满了，就会阻塞等待，直到有空闲对象或者超时
//     */
//    public TTSocket getConnect() throws Exception {
//        return pool.borrowObject();
//    }
//
//    /**
//     * 将对象从池中移除
//     */
//    public void invalidateObject(TTSocket ttSocket) {
//        try {
//            pool.invalidateObject(ttSocket);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 将一个用完的对象返还给对象池
//     */
//    public void returnConnection(TTSocket ttSocket) {
//        try {
//            pool.returnObject(ttSocket);
//        } catch (Exception e) {
//            if (ttSocket != null) {
//                try {
//                    ttSocket.close();
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                }
//            }
//        }
//    }
//
//    /**
//     * 池里面保存的对象工厂
//     */
//    static class ConnectionFactory extends BasePooledObjectFactory<TTSocket> {
//
//        //远端地址
//        private String host;
//        //端口
//        private Integer port;
//
//        /**
//         * 构造方法初始化地址及端口
//         */
//        public ConnectionFactory(String ip, int port) {
//            this.host = ip;
//            this.port = port;
//        }
//
//        /**
//         * 创建一个对象
//         */
//        @Override
//        public TTSocket create() throws Exception {
//            // 实例化一个自定义的一个thrift 对象
//            log.info("make new connection");
//            TTSocket ttSocket = new TTSocket(host, port);
//            // 打开通道
//            ttSocket.open();
//            return ttSocket;
//        }
//
//        @Override
//        public PooledObject<TTSocket> wrap(TTSocket ttSocket) {
//            return new DefaultPooledObject<>(ttSocket);
//        }
//
//        /**
//         * 销毁对象
//         */
//        public void destroyObject(PooledObject<TTSocket> pooledObject) {
//            try {
//                //尝试关闭连接
//                pooledObject.getObject().close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        /**
//         * 校验对象是否可用
//         * 通过 pool.setTestOnBorrow(boolean testOnBorrow) 设置
//         * 设置为true这会在调用pool.borrowObject()获取对象之前调用这个方法用于校验对象是否可用
//         */
//        public boolean validateObject(PooledObject<TTSocket> pooledObject) {
//            return pooledObject.getObject().isOpen();
//        }
//    }
//}
