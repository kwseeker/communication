package top.kwseeker.communication.grpc.config;

public class Config {

    public static class Agent {
        /**
         * Force reconnection period of grpc, based on grpc_channel_check_interval. If count of check grpc channel
         * status more than this number. The channel check will call channel.getState(true) to requestConnection.
         */
        public static long FORCE_RECONNECTION_PERIOD = 1;
        public static String VERSION = "UNKNOWN";
        public static String AUTHENTICATION = "";
    }

    public static class Collector {
        public static String BACKEND_SERVICE = "localhost:50051";
        public static long GRPC_CHANNEL_CHECK_INTERVAL = 30;
        /**
         * 如果为true, skywalking代理将启用定期解析DNS以更新接收方服务地址。
         */
        public static boolean IS_RESOLVE_DNS_PERIODICALLY = false;
        public static int GRPC_UPSTREAM_TIMEOUT = 30;
        /**
         * 自增的属性，消费者线程消费间隔，默认20ms, 为方便调试改为1000ms
         */
        public static int CONSUME_CYCLE = 1000;
    }

    public static class Buffer {
        public static int CHANNEL_SIZE = 5;
        public static int BUFFER_SIZE = 300;
    }
}