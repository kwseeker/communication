package top.kwseeker.communication.grpc.context.tag;

import top.kwseeker.communication.grpc.context.trace.TracingSpan;

import java.util.Objects;

public abstract class AbstractTag<T> {

    private int id;

    private boolean canOverwrite;
    /**
     * The key of this Tag.
     */
    protected final String key;

    public AbstractTag(int id, String tagKey, boolean canOverwrite) {
        this.id = id;
        this.key = tagKey;
        this.canOverwrite = canOverwrite;
    }

    public AbstractTag(String key) {
        this(-1, key, false);
    }

    protected abstract void set(TracingSpan span, T tagValue);

    /**
     * @return the key of this tag.
     */
    public String key() {
        return this.key;
    }

    public boolean sameWith(AbstractTag<T> tag) {
        return canOverwrite && this.id == tag.id;
    }

    public int getId() {
        return id;
    }

    public boolean isCanOverwrite() {
        return canOverwrite;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof AbstractTag))
            return false;
        final AbstractTag<?> that = (AbstractTag<?>) o;
        return getId() == that.getId() &&
            isCanOverwrite() == that.isCanOverwrite() &&
            key.equals(that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), isCanOverwrite(), key);
    }
}
