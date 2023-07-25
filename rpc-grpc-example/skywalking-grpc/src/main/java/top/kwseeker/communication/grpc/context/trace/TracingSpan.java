package top.kwseeker.communication.grpc.context.trace;

import top.kwseeker.communication.grpc.context.util.TagValuePair;
import top.kwseeker.communication.grpc.language.agent.v3.SpanLayer;
import top.kwseeker.communication.grpc.language.agent.v3.SpanObject;
import top.kwseeker.communication.grpc.language.agent.v3.SpanType;

import java.util.List;

public class TracingSpan {

    protected int spanId;
    protected int parentSpanId;
    protected List<TagValuePair> tags;
    protected String operationName;
    protected SpanLayer layer;
    protected long startTime;
    protected long endTime;
    //protected volatile boolean isInAsyncMode = false;
    //private volatile boolean isAsyncStopped = false;
    //protected final TracingContext owner;
    //protected boolean errorOccurred = false;
    //protected int componentId = 0;
    //protected List<LogDataEntity> logs;
    //protected List<TraceSegmentRef> refs;
    //protected boolean skipAnalysis;

    public TracingSpan(int spanId, int parentSpanId, List<TagValuePair> tags, String operationName, SpanLayer layer, long startTime, long endTime) {
        this.spanId = spanId;
        this.parentSpanId = parentSpanId;
        this.tags = tags;
        this.operationName = operationName;
        this.layer = layer;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public SpanObject.Builder transform() {
        SpanObject.Builder spanBuilder = SpanObject.newBuilder();
        spanBuilder.setSpanId(this.spanId)
                .setParentSpanId(this.parentSpanId)
                .setStartTime(this.startTime)
                .setEndTime(this.endTime)
                .setOperationName(this.operationName)
                .setSpanType(SpanType.Entry)
                .setSpanLayer(SpanLayer.Http);
        for (TagValuePair tag : this.tags) {
            spanBuilder.addTags(tag.transform());
        }
        return spanBuilder;
    }
}
