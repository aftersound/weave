package io.aftersound.weave.util.map;

import java.util.List;
import java.util.Map;

public class Update implements Mutation {

    private final List<Segment> path;
    private final Object value;

    public Update(List<Segment> path, Object value) {
        this.path = path;
        this.value = value;
    }

    @Override
    public <T> T on(Map<String, Object> map) {
        SegmentValues segmentValues = new Traverser(path).on(map);
        return (T) update(segmentValues.last(), value);
    }

    private Object update(SegmentValue segmentValue, Object targetValue) {
        // check if it's about updating elements in the list
        if (segmentValue.isList()) {
            Filter filter = segmentValue.segment().getDirective(Key.LIST_FILTER);
            // TODO
            return null;
        } else {
            SegmentValue parent = segmentValue.parent();
            Segment segment = segmentValue.segment();
            if (parent.isMap()) {
                return ((Map<String, Object>) parent).put(segment.getName(), targetValue);
            } else {
                if (parent.isNull()) {
                    throw new NullPointerException("Null container, field value at specified path cannot be set");
                }
                throw new RuntimeException("Specified path is not a field of container");
            }
        }
    }

}
