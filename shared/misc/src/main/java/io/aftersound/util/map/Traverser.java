package io.aftersound.util.map;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class Traverser {

    private final List<Segment> path;

    public Traverser(List<Segment> path) {
        this.path = path;
    }

    public SegmentValues on(Map<String, Object> map) {
        SegmentValues segmentValues = new SegmentValues();
        SegmentValue root = new SegmentValue(null, map, map);
        segmentValues.add(root);

        Object c = map;
        for (Segment s : path) {
            Object o = null;
            if (c instanceof Map) {
                Map cm = (Map) c;
                if (s.getName() != null) {
                    o = cm.get(s.getName());
                    if (s.hasDirective(Keys.LIST_FILTER)) {
                        // it is implied that 'o' is a list
                        Filter filter = s.getDirective(Keys.LIST_FILTER);
                        Return returnDirective = s.getDirective(Keys.LIST_RETURN);
                        c = new ListQuery(filter, returnDirective).on((List) o);
                    } else {
                        c = o;
                    }
                } else {
                    c = retrieve(cm, s.getNames());
                }
            } else if (c instanceof List) {
                o = c;
                if (s.getName() != null) {
                    c = project((List) c, s.getName());
                } else {
                    c = project((List) c, s.getNames());
                }
            } else {
                o = null;
                c = null;
            }
            SegmentValue segmentValue = new SegmentValue(s, o, c);
            segmentValues.add(segmentValue);
        }

        return segmentValues;
    }

    private Map<String, Object> retrieve(Map s, List<String> names) {
        Map<String, Object> r = new LinkedHashMap<>(names.size());
        for (String n : names) {
            r.put(n, s.get(n));
        }
        return r;
    }

    private List project(List source, String name) {
        List l = new ArrayList();
        for (Object e : source) {
            if (e instanceof Map) {
                l.add(((Map) e).get(name));
            }
        }
        return l;
    }

    private List project(List source, List<String> names) {
        List l = new ArrayList();
        for (Object e : source) {
            if (e instanceof Map) {
                l.add(retrieve((Map) e, names));
            }
        }
        return l;
    }

}
