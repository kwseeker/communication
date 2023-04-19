package top.kwseeker.rpc.server;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rpc.manager")
public class RPCManagerProperties {

    private final ThriftProperties thrift = new ThriftProperties();

    public ThriftProperties getThrift() {
        return thrift;
    }

    public static class ThriftProperties {
        //Thrift RPC 开关
        private Boolean enabled = false;
        //Thrift 服务端端口
        private Integer port = 9081;
        //private Integer type = ;
        //线程池最小线程数
        private Integer minThreadPool = 4;
        //线程池最大线程数
        private Integer maxThreadPool = 4;

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        public Integer getPort() {
            return port;
        }

        public void setPort(Integer port) {
            this.port = port;
        }

        public Integer getMinThreadPool() {
            return minThreadPool;
        }

        public void setMinThreadPool(Integer minThreadPool) {
            this.minThreadPool = minThreadPool;
        }

        public Integer getMaxThreadPool() {
            return maxThreadPool;
        }

        public void setMaxThreadPool(Integer maxThreadPool) {
            this.maxThreadPool = maxThreadPool;
        }
    }
}
