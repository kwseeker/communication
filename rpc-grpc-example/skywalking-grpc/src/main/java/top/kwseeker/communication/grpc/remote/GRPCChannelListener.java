package top.kwseeker.communication.grpc.remote;

public interface GRPCChannelListener {
    void statusChanged(GRPCChannelStatus status);
}