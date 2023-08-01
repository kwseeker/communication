package top.kwseeker.communication.grpc.datacarrier.consumer;

import top.kwseeker.communication.grpc.datacarrier.buffer.Channels;

public interface IDriver {

    boolean isRunning(Channels channels);

    void close(Channels channels);

    void begin(Channels channels);
}
