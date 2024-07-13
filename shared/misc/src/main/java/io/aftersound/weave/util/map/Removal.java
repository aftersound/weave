package io.aftersound.weave.util.map;

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
        if (segmentValue.isList()) {
            Filter filter = segmentValue.segment().getDirective(Key.LIST_FILTER);
            // TODO
            return null;
        } else {
            SegmentValue parent = segmentValue.parent();
            if (parent.isMap()) {
                return ((Map<String, Object>) parent.value()).remove(segmentValue.segment().getName());
            } else {
                if (parent.isNull()) {
                    throw new NullPointerException("Null container, field value at specified path cannot be set");
                }
                throw new RuntimeException("Specified path is not a field of container");
            }
        }
    }

}
