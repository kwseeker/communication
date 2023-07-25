package top.kwseeker.communication.grpc.remote.builder;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ClientInterceptors;
import io.grpc.ForwardingClientCall;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import top.kwseeker.communication.grpc.config.Config;

public class AgentIDDecorator implements ChannelDecorator {

    private static final Metadata.Key<String> AGENT_VERSION_HEAD_HEADER_NAME =
            Metadata.Key.of("Agent-Version", Metadata.ASCII_STRING_MARSHALLER);

    @Override
    public Channel build(Channel channel) {
        return ClientInterceptors.intercept(channel, new ClientInterceptor() {
            @Override
            public <REQ, RESP> ClientCall<REQ, RESP> interceptCall(MethodDescriptor<REQ, RESP> method,
                CallOptions options, Channel channel) {
                return new ForwardingClientCall.SimpleForwardingClientCall<REQ, RESP>(channel.newCall(method, options)) {
                    @Override
                    public void start(Listener<RESP> responseListener, Metadata headers) {
                        headers.put(AGENT_VERSION_HEAD_HEADER_NAME, Config.Agent.VERSION);

                        super.start(responseListener, headers);
                    }
                };
            }
        });
    }
}