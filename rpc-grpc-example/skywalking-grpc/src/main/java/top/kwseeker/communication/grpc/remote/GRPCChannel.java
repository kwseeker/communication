package top.kwseeker.communication.grpc.remote;

import io.grpc.*;
import io.grpc.internal.DnsNameResolverProvider;
import io.grpc.netty.NettyChannelBuilder;
import top.kwseeker.communication.grpc.remote.builder.ChannelBuilder;
import top.kwseeker.communication.grpc.remote.builder.ChannelDecorator;

import java.util.LinkedList;
import java.util.List;

public class GRPCChannel {

    private final ManagedChannel originChannel;
    private final Channel channelWithDecorators;

    private GRPCChannel(String host, int port, List<ChannelBuilder> channelBuilders,
                        List<ChannelDecorator> decorators) throws Exception {
        //TODO
        ManagedChannelBuilder channelBuilder = NettyChannelBuilder.forAddress(host, port);

        NameResolverRegistry.getDefaultRegistry().register(new DnsNameResolverProvider());

        for (ChannelBuilder builder : channelBuilders) {
            channelBuilder = builder.build(channelBuilder);
        }

        this.originChannel = channelBuilder.build();

        Channel channel = originChannel;
        for (ChannelDecorator decorator : decorators) {
            channel = decorator.build(channel);
        }

        channelWithDecorators = channel;
    }

    public static Builder newBuilder(String host, int port) {
        return new Builder(host, port);
    }

    public Channel getChannel() {
        return this.channelWithDecorators;
    }

    public boolean isTerminated() {
        return originChannel.isTerminated();
    }

    public void shutdownNow() {
        originChannel.shutdownNow();
    }

    public boolean isShutdown() {
        return originChannel.isShutdown();
    }

    public boolean isConnected() {
        return isConnected(false);
    }

    public boolean isConnected(boolean requestConnection) {
        return originChannel.getState(requestConnection) == ConnectivityState.READY;
    }

    public static class Builder {
        private final String host;
        private final int port;
        private final List<ChannelBuilder> channelBuilders;
        private final List<ChannelDecorator> decorators;

        private Builder(String host, int port) {
            this.host = host;
            this.port = port;
            this.channelBuilders = new LinkedList<>();
            this.decorators = new LinkedList<>();
        }

        public Builder addChannelDecorator(ChannelDecorator interceptor) {
            this.decorators.add(interceptor);
            return this;
        }

        public GRPCChannel build() throws Exception {
            return new GRPCChannel(host, port, channelBuilders, decorators);
        }

        public Builder addManagedChannelBuilder(ChannelBuilder builder) {
            channelBuilders.add(builder);
            return this;
        }
    }
}
