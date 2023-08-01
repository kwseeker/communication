package top.kwseeker.communication.grpc.context.util;

import top.kwseeker.communication.grpc.common.v3.KeyStringValuePair;
import top.kwseeker.communication.grpc.context.tag.AbstractTag;

import java.util.Objects;

public class TagValuePair {

    private AbstractTag key;
    private String value;

    public TagValuePair(AbstractTag tag, String value) {
        this.key = tag;
        this.value = value;
    }

    public AbstractTag getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    //这个是 proto 生成的类
    public KeyStringValuePair transform() {
        KeyStringValuePair.Builder keyValueBuilder = KeyStringValuePair.newBuilder();
        keyValueBuilder.setKey(key.key());
        if (value != null) {
            keyValueBuilder.setValue(value);
        }
        return keyValueBuilder.build();
    }

    public boolean sameWith(AbstractTag tag) {
        return key.isCanOverwrite() && key.getId() == tag.getId();
    }


    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof TagValuePair))
            return false;
        final TagValuePair that = (TagValuePair) o;
        return Objects.equals(getKey(), that.getKey()) &&
            Objects.equals(getValue(), that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey(), getValue());
    }
}