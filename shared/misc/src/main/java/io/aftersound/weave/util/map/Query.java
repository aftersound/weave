package io.aftersound.weave.util.map;

import java.util.List;
import java.util.Map;

public class Query {

    private List<Segment> path;

    public Query(List<Segment> path) {
        this.path = path;
    }

    public <T> T on(final Map<String, Object> map) {
        SegmentValues segmentValues = new Traverser(path).on(map);
        return (T) segmentValues.last().value();
    }

}
