package io.aftersound.util.map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Path {

    private final List<Segment> segments;

    private Path() {
        this.segments = new ArrayList<>();
    }

    public static Path of(Segment... segments) {
        Path p = new Path();
        if (segments != null) {
            for (Segment s : segments) {
                p.addSegment(s);
            }
        }
        return p;
    }

    public static Path of(List<Segment> segments) {
        Path p = new Path();
        if (segments != null) {
            for (Segment s : segments) {
                p.addSegment(s);
            }
        }
        return p;
    }

    public static Path of(String path) {
        return new PathParser().parse(path);
    }

    private void addSegment(Segment segment) {
        if (segment != null) {
            segments.add(segment);
        }
    }

    public List<Segment> getSegments() {
        return Collections.unmodifiableList(segments);
    }

    public Query query() {
        return new Query(segments);
    }

    /**
     * Create update mutation at this path
     *
     * @param value target value of the field at this path
     * @return a {@link Mutation} which can enforce the desired update
     */
    public Mutation update(Object value) {
        return new Update(segments, value);
    }

    /**
     * Create removal mutation at this path
     *
     * @return a {@link Mutation} which can enforce the desired removal
     */
    public Mutation removal() {
        return new Removal(segments);
    }

}
