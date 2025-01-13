package io.aftersound.util.map;

import java.util.List;
import java.util.Map;

class Removal implements Mutation {

    private final List<Segment> path;

    public Removal(List<Segment> path) {
        this.path = path;
    }

    @Override
    public <T> T on(Map<String, Object> map) {
        SegmentValues segmentValues = new Traverser(path).on(map);
        return (T) remove(segmentValues.last());
    }

    private Object remove(SegmentValue segmentValue) {
        // check if it's about removing elements from list
        if (intendedToRemoveElements(segmentValue)) {
            Filter filter = segmentValue.segment().getDirective(Keys.LIST_FILTER);
            return null;
        } else {
            SegmentValue parent = segmentValue.parent();
            if (parent.filteredValue() instanceof Map) {
                return ((Map<String, Object>) parent.filteredValue()).remove(segmentValue.segment().getName());
            } else {
                if (parent.filteredValue() == null) {
                    throw new NullPointerException("Null container, field value at specified path cannot be set");
                }
                throw new RuntimeException("Specified path is not a field of container");
            }
        }
    }

    private boolean intendedToRemoveElements(SegmentValue segmentValue) {
        return false;
    }

}
