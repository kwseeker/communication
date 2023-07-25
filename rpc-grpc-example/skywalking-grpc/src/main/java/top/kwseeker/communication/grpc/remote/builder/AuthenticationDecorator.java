package top.kwseeker.communication.grpc.remote.builder;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ClientInterceptors;
import io.grpc.ForwardingClientCall;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import org.apache.skywalking.apm.agent.core.conf.Config;
import org.apache.skywalking.apm.util.StringUtil;

/**
 * Active authentication header by Config.Agent.AUTHENTICATION
 */
public class AuthenticationDecorator implements ChannelDecorator {
    private static final Metadata.Key<String> AUTH_HEAD_HEADER_NAME =
            Metadata.Key.of("Authentication", Metadata.ASCII_STRING_MARSHALLER);

    @Override
    public Channel build(Channel channel) {
        if (StringUtil.isEmpty(Config.Agent.AUTHENTICATION)) {
            return channel;
        }

        return ClientInterceptors.intercept(channel, new ClientInterceptor() {
            @Override
            public <REQ, RESP> ClientCall<REQ, RESP> interceptCall(MethodDescriptor<REQ, RESP> method,
                CallOptions options, Channel channel) {
                return new ForwardingClientCall.SimpleForwardingClientCall<REQ, RESP>(channel.newCall(method, options)) {
                    @Override
                    public void start(Listener<RESP> responseListener, Metadata headers) {
                        headers.put(AUTH_HEAD_HEADER_NAME, Config.Agent.AUTHENTICATION);

                        super.start(responseListener, headers);
                    }
                };
            }
        });
    }
}
