package top.kwseeker.communication.grpc.context;

import top.kwseeker.communication.grpc.context.trace.TraceSegment;

public interface TracingContextListener {
    void afterFinished(TraceSegment traceSegment);
}