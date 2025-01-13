package io.aftersound.util.map;

class SegmentValue {

    private final Segment segment;
    private final Object originalValue;
    private final Object filteredValue;

    private SegmentValue parent;

    public SegmentValue(Segment segment, Object originalValue, Object filteredValue) {
        this.segment = segment;
        this.originalValue = originalValue;
        this.filteredValue = filteredValue;
    }

    public Segment segment() {
        return segment;
    }

    public Object originalValue() {
        return originalValue;
    }

    public Object filteredValue() {
        return filteredValue;
    }

    public SegmentValue parent(SegmentValue segmentValue) {
        this.parent = segmentValue;
        return this;
    }

    public SegmentValue parent() {
        return this.parent;
    }

}
