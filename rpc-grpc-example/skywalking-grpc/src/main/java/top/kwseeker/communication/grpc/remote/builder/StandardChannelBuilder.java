package top.kwseeker.communication.grpc.remote.builder;

import io.grpc.ManagedChannelBuilder;

public class StandardChannelBuilder implements ChannelBuilder {

    private final static int MAX_INBOUND_MESSAGE_SIZE = 1024 * 1024 * 50;

    @Override
    public ManagedChannelBuilder build(ManagedChannelBuilder managedChannelBuilder) {
        return managedChannelBuilder.maxInboundMessageSize(MAX_INBOUND_MESSAGE_SIZE)
                                    .usePlaintext();
    }
}