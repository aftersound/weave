package io.aftersound.weave.utils;

import java.util.*;

public class MapHandler {

    private final Map<String, Object> m;
    private final MapCreator mapCreator;

    public MapHandler(Map<String, Object> map) {
        assert map != null;

        this.m = map;

        MapCreator mapCreator;
        final String cls = map.getClass().getName();
        if (HashMap.class.getName().equals(cls)) {
            mapCreator = new HashMapCreator();
        } else if (TreeMap.class.getName().equals(cls)) {
            mapCreator = new TreeMapCreator();
        } else {
            mapCreator = new LinkHashMapCreator();
        }
        this.mapCreator = mapCreator;
    }

    public Map<String, Object> get() {
        return m;
    }

    public void setValueAtPath(Object value, String... path) {
        assert (path != null && path.length > 0) : "path is null or empty";
        Map<String, Object> c = ensureContainer(path);
        c.put(path[path.length - 1], value);
    }

    public Object getValueAtPath(String... path) {
        assert (path != null && path.length > 0) : "path is null or empty";
        Map<String, Object> c = getContainer(path);
        return c != null ? c.get(path[path.length - 1]) : null;
    }

    private Map<String, Object> ensureContainer(String[] path) {
        if (path.length > 1) {
            String[] cpath = Arrays.copyOfRange(path, 0, path.length - 1);
            Map<String, Object> c = m;
            for (String s : cpath) {
                Object v = c.get(s);
                if (v == null) {
                    Map<String, Object> t = mapCreator.create();
                    c.put(s, t);
                    c = t;
                } else if (v instanceof Map) {
                    c = (Map<String, Object>) v;
                } else {
                    throw new RuntimeException("Map, as container, is expected");
                }
            }
            return c;
        } else {
            return m;
        }
    }

    private Map<String, Object> getContainer(String[] path) {
        if (path.length > 1) {
            String[] cpath = Arrays.copyOfRange(path, 0, path.length - 1);
            Map<String, Object> c = m;
            for (String s : cpath) {
                Object v = c.get(s);
                if (v instanceof Map) {
                    c = (Map<String, Object>) v;
                } else {
                    c = null;
                    break;
                }
            }
            return c;
        } else {
            return m;
        }
    }

    private interface MapCreator {
        Map<String, Object> create();
    }

    private static class HashMapCreator implements MapCreator {

        @Override
        public Map<String, Object> create() {
            return new HashMap<>();
        }

    }

    private static class LinkHashMapCreator implements MapCreator {

        @Override
        public Map<String, Object> create() {
            return new LinkedHashMap<>();
        }

    }

    private static class TreeMapCreator implements MapCreator {

        @Override
        public Map<String, Object> create() {
            return new TreeMap<>();
        }

    }

}

