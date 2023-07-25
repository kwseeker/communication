package top.kwseeker.communication.grpc.config;

public class Config {

    public static class Agent {
        /**
         * Force reconnection period of grpc, based on grpc_channel_check_interval. If count of check grpc channel
         * status more than this number. The channel check will call channel.getState(true) to requestConnection.
         */
        public static long FORCE_RECONNECTION_PERIOD = 1;

        public static String VERSION = "UNKNOWN";
    }

    public static class Collector {
        public static String BACKEND_SERVICE = "";
        public static long GRPC_CHANNEL_CHECK_INTERVAL = 30;
        /**
         * 如果为true, skywalking代理将启用定期解析DNS以更新接收方服务地址。
         */
        public static boolean IS_RESOLVE_DNS_PERIODICALLY = false;
    }

    public static class Buffer {
        public static int CHANNEL_SIZE = 5;
        public static int BUFFER_SIZE = 300;
    }
}