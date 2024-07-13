package io.aftersound.weave.util.map;

import java.util.List;
import java.util.Map;

class SegmentValue {

    private final Segment segment;
    private final Object value;

    private SegmentValue parent;

    public SegmentValue(Segment segment, Object value) {
        this.segment = segment;
        this.value = value;
    }

    public Segment segment() {
        return segment;
    }

    public Object value() {
        return value;
    }

    public SegmentValue parent(SegmentValue segmentValue) {
        this.parent = segmentValue;
        return this;
    }

    public SegmentValue parent() {
        return this.parent;
    }

    public boolean isMap() {
        return value instanceof Map;
    }

    public boolean isList() {
        return value instanceof List;
    }

    public boolean isNull() {
        return value == null;
    }

}
