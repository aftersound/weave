package io.aftersound.util.map;

import java.util.ArrayList;
import java.util.List;

class SegmentValues {

    private List<SegmentValue> segmentValues = new ArrayList<>();

    public void add(SegmentValue segmentValue) {
        if (segmentValues.size() > 0) {
            segmentValue.parent(segmentValues.get(segmentValues.size() - 1));
        }
        segmentValues.add(segmentValue);
    }

    public SegmentValue last() {
        return segmentValues.size() > 0 ? segmentValues.get(segmentValues.size() - 1) : null;
    }

}
