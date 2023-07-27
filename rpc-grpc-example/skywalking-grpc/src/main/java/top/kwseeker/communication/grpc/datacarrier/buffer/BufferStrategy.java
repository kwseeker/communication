package top.kwseeker.communication.grpc.datacarrier.buffer;

/**
 * 这个是定义Buffer满了的话的策略
 */
public enum BufferStrategy {
    BLOCKING, IF_POSSIBLE
}
