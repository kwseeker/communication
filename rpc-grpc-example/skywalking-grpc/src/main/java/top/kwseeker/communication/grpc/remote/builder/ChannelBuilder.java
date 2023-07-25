package top.kwseeker.communication.grpc.remote.builder;

import io.grpc.ManagedChannelBuilder;

public interface ChannelBuilder<B extends ManagedChannelBuilder> {
    B build(B managedChannelBuilder) throws Exception;
}
