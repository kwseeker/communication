//package top.kwseeker.communication.grpc.remote.builder;
//
//import io.grpc.netty.GrpcSslContexts;
//import io.grpc.netty.NegotiationType;
//import io.grpc.netty.NettyChannelBuilder;
//import io.netty.handler.ssl.SslContextBuilder;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//
///**
// * If only ca.crt exists, start TLS. If cert, key and ca files exist, enable mTLS.
// */
//public class TLSChannelBuilder implements ChannelBuilder<NettyChannelBuilder> {
//
//    @Override
//    public NettyChannelBuilder build(
//        NettyChannelBuilder managedChannelBuilder) throws AgentPackageNotFoundException, IOException {
//
//        File caFile = new File(AgentPackagePath.getPath(), Config.Agent.SSL_TRUSTED_CA_PATH);
//        boolean isCAFileExist = caFile.exists() && caFile.isFile();
//        if (Config.Agent.FORCE_TLS || isCAFileExist) {
//            SslContextBuilder builder = GrpcSslContexts.forClient();
//
//            if (isCAFileExist) {
//                String certPath = Config.Agent.SSL_CERT_CHAIN_PATH;
//                String keyPath = Config.Agent.SSL_KEY_PATH;
//                if (StringUtil.isNotBlank(certPath) && StringUtil.isNotBlank(keyPath)) {
//                    File keyFile = new File(AgentPackagePath.getPath(), keyPath);
//                    File certFile = new File(AgentPackagePath.getPath(), certPath);
//
//                    if (certFile.isFile() && keyFile.isFile()) {
//                        try (InputStream cert = new FileInputStream(certFile);
//                             InputStream key = PrivateKeyUtil.loadDecryptionKey(keyFile.getAbsolutePath())) {
//                            builder.keyManager(cert, key);
//                        }
//                    } else if (!certFile.isFile() || !keyFile.isFile()) {
//                        LOGGER.warn("Failed to enable mTLS caused by cert or key cannot be found.");
//                    }
//                }
//
//                builder.trustManager(caFile);
//            }
//            managedChannelBuilder.negotiationType(NegotiationType.TLS).sslContext(builder.build());
//        }
//        return managedChannelBuilder;
//    }
//
//}
