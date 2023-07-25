package top.kwseeker.communication.grpc.remote.builder;

import io.grpc.Channel;

public interface ChannelDecorator {
    Channel build(Channel channel);
}