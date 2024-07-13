package io.aftersound.weave.util.map;

import java.util.ArrayList;
import java.util.List;

class SegmentValues {

    private List<SegmentValue> segmentValues = new ArrayList<>();

    public void add(Segment segment, Object value) {
        SegmentValue segmentValue = new SegmentValue(segment, value);
        if (segmentValues.size() > 0) {
            segmentValue.parent(segmentValues.get(segmentValues.size() - 1));
        }
        segmentValues.add(segmentValue);
    }

    public SegmentValue last() {
        return segmentValues.size() > 0 ? segmentValues.get(segmentValues.size() - 1) : null;
    }

}
