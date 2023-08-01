package top.kwseeker.communication.grpc.context.trace;

import top.kwseeker.communication.grpc.context.TracingContext;
import top.kwseeker.communication.grpc.context.tag.AbstractTag;
import top.kwseeker.communication.grpc.context.tag.Tags;
import top.kwseeker.communication.grpc.context.util.TagValuePair;
import top.kwseeker.communication.grpc.language.agent.v3.SpanLayer;
import top.kwseeker.communication.grpc.language.agent.v3.SpanObject;
import top.kwseeker.communication.grpc.language.agent.v3.SpanType;

import java.util.ArrayList;
import java.util.List;

public class TracingSpan {

    protected int spanId;
    protected int parentSpanId;
    protected List<TagValuePair> tags;
    protected String operationName;
    protected SpanLayer layer;
    protected long startTime;
    protected long endTime;
    protected final TracingContext owner;
    //protected volatile boolean isInAsyncMode = false;
    //private volatile boolean isAsyncStopped = false;
    //protected final TracingContext owner;
    //protected boolean errorOccurred = false;
    //protected int componentId = 0;
    //protected List<LogDataEntity> logs;
    //protected List<TraceSegmentRef> refs;
    //protected boolean skipAnalysis;

    public TracingSpan(int spanId, int parentSpanId, String operationName, TracingContext owner) {
        this.spanId = spanId;
        this.parentSpanId = parentSpanId;
        this.operationName = operationName;
        this.owner = owner;
    }

    public TracingSpan(int spanId, int parentSpanId, List<TagValuePair> tags, String operationName, SpanLayer layer, long startTime, long endTime, TracingContext owner) {
        this.spanId = spanId;
        this.parentSpanId = parentSpanId;
        this.tags = tags;
        this.operationName = operationName;
        this.layer = layer;
        this.startTime = startTime;
        this.endTime = endTime;
        this.owner = owner;
    }

    public TracingSpan start() {
        this.startTime = System.currentTimeMillis();
        return this;
    }

    public boolean finish(TraceSegment owner) {
        this.endTime = System.currentTimeMillis();
        owner.archive(this);
        return true;
    }

    public TracingSpan setLayer(SpanLayer layer) {
        this.layer = layer;
        return this;
    }

    public TracingSpan tag(String key, String value) {
        return tag(Tags.ofKey(key), value);
    }

    public TracingSpan tag(AbstractTag<?> tag, String value) {
        if (tags == null) {
            tags = new ArrayList<>(8);
        }

        if (tag.isCanOverwrite()) {
            for (TagValuePair pair : tags) {
                if (pair.sameWith(tag)) {
                    pair.setValue(value);
                    return this;
                }
            }
        }

        tags.add(new TagValuePair(tag, value));
        return this;
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


    public int getSpanId() {
        return spanId;
    }
}
