package top.kwseeker.communication.grpc.context;

import top.kwseeker.communication.grpc.context.trace.TraceSegment;
import top.kwseeker.communication.grpc.context.trace.TracingSpan;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 这个类不是重点关注的目标，这里简化了这个类的实现
 * TracingContext 其实就是链路追踪活跃Span的栈（Stack）容器，Span结束（如方法返回），出栈然后加入到 TraceSegment 已完成Span列表（List）
 */
public class TracingContext {

    private TraceSegment segment;
    //使用LinkedList实现的栈结构
    private LinkedList<TracingSpan> activeSpanStack = new LinkedList<>();
    private int spanIdGenerator;

    public TracingContext() {
        this.segment = new TraceSegment();
        this.spanIdGenerator = 0;
    }

    public TracingSpan createSpan(String operationName) {
        TracingContext owner = this;
        final TracingSpan parentSpan = peek();    //父Span, 对应方法调用者
        final int parentSpanId = parentSpan == null ? -1 : parentSpan.getSpanId();
        TracingSpan span = new TracingSpan(spanIdGenerator++, parentSpanId, operationName, owner);
        return push(span);
    }

    public boolean stopSpan(TracingSpan span) {
        TracingSpan lastSpan = peek();
        if (lastSpan != null && lastSpan == span) {
            if (lastSpan.finish(segment)) {
                pop();
            }
        } else {
            throw new IllegalStateException("Stopping the unexpected span = " + span);
        }

        finish();

        return activeSpanStack.isEmpty();
    }

    private TracingSpan pop() {
        return activeSpanStack.removeLast();
    }

    private TracingSpan push(TracingSpan span) {
        activeSpanStack.addLast(span);
        return span;
    }

    private TracingSpan peek() {
        if (activeSpanStack.isEmpty()) {
            return null;
        }
        return activeSpanStack.getLast();
    }

    /**
     * 比如追踪的方法返回, EntrySpan结束(Span栈为空，结束的Span都存入TraceSegment已完成的span列表)，
     * 会调用此方法上报，具体流程是通过 ListenerManager#notifyFinish() 此方法内部通过生产者端往DataCarrier Buffer中添加数据
     */
    private void finish() {
        TracingContext.ListenerManager.notifyFinish(segment);
    }

    public static class ListenerManager {

        private static List<TracingContextListener> LISTENERS = new LinkedList<>();

        public static synchronized void add(TracingContextListener listener) {
            LISTENERS.add(listener);
        }

        static void notifyFinish(TraceSegment finishedSegment) {
            for (TracingContextListener listener : LISTENERS) {
                listener.afterFinished(finishedSegment);
            }
        }

        public static synchronized void remove(TracingContextListener listener) {
            LISTENERS.remove(listener);
        }
    }
}
