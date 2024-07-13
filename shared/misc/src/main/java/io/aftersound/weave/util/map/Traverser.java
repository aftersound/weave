package io.aftersound.weave.util.map;

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
        segmentValues.add(new Segment(), map);

        Object c = map;
        for (Segment s : path) {
            if (c instanceof Map) {
                Map cm = (Map) c;
                if (s.getName() != null) {
                    Object o = cm.get(s.getName());
                    if (s.hasDirective(Key.LIST_FILTER)) {
                        // it is implied that 'o' is a list
                        Filter filter = s.getDirective(Key.LIST_FILTER);
                        Return returnDirective = s.getDirective(Key.LIST_RETURN);
                        c = new ListQuery(filter, returnDirective).on((List) o);
                    } else {
                        c = o;
                    }
                } else {
                    c = retrieve(cm, s.getNames());
                }
            } else if (c instanceof List) {
                if (s.getName() != null) {
                    c = project((List) c, s.getName());
                } else {
                    c = project((List) c, s.getNames());
                }
            } else {
                c = null;
            }
            segmentValues.add(s, c);
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
