package top.kwseeker.communication.grpc.context.trace;

import top.kwseeker.communication.grpc.language.agent.v3.SegmentObject;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * 此DEMO只是提取 SkyWalking GRPC 逻辑，这个类是什么结构并不重要，简单点，只保留部分字段
 */
public class TraceSegment {

    private String traceSegmentId;
    //private TraceSegmentRef ref;
    //已经结束的Span的列表
    private List<TracingSpan> spans;
    //private DistributedTraceId relatedGlobalTraceId;
    //private boolean ignore = false;
    private boolean isSizeLimited = false;
    private final long createTime;

    public TraceSegment() {
        this.traceSegmentId = UUID.randomUUID().toString().replace("-", "");
        this.spans = new LinkedList<>();
        this.createTime = System.currentTimeMillis();
    }

    //方法调用返回，span从TraceContext出栈后加入到spans列表
    public void archive(TracingSpan finishedSpan) {
        spans.add(finishedSpan);
    }

    public SegmentObject transform() {
        SegmentObject.Builder traceSegmentBuilder = SegmentObject.newBuilder();
        traceSegmentBuilder.setTraceSegmentId(this.traceSegmentId);
        for (TracingSpan span : spans) {
            traceSegmentBuilder.addSpans(span.transform());
        }
        traceSegmentBuilder.setService("SW-DEMO");
        traceSegmentBuilder.setServiceInstance("SW-DEMO-1");
        traceSegmentBuilder.setIsSizeLimited(this.isSizeLimited);
        return traceSegmentBuilder.build();
    }

    public String getTraceSegmentId() {
        return traceSegmentId;
    }

    public List<TracingSpan> getSpans() {
        return spans;
    }

    public long getCreateTime() {
        return createTime;
    }
}
