package io.aftersound.util.map;

import io.aftersound.util.Key;

import java.util.List;

public class Segment {
    /**
     * mutually exclusive with 'names'
     */
    private String name;

    /**
     * mutually exclusive with 'name'
     */
    private List<String> names;

    /**
     * Only applicable when the field pointed by this path segment is an Array or List.
     * In other words, it implies this segment points to a field of Array or List
     */
    private Directives directives;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    public Directives getDirectives() {
        return directives;
    }

    public void setDirectives(Directives directives) {
        this.directives = directives;
    }

    public boolean hasDirective(Key<?> key) {
        return directives != null && directives.get(key) != null;
    }

    public <T> T getDirective(Key<T> key) {
        return directives != null ? directives.get(key) : null;
    }

    public static Builder builderEitherOr(String name, List<String> names) {
        if (name != null) {
            if (names == null || names.isEmpty()) {
                return new Builder(name);
            }
        } else {
            if (names != null && names.size() > 0) {
                return new Builder(names);
            }
        }
        throw new IllegalArgumentException("'name' or 'names' cannot both be null or both have values");
    }

    public static Builder builder(String name) {
        return new Builder(name);
    }

    public static Builder builder(List<String> names) {
        return new Builder(names);
    }

    public static class Builder {

        private final String name;
        private final List<String> names;
        private Directives directives = new Directives();

        private Builder(String name) {
            this.name = name;
            this.names = null;
        }

        public Builder(List<String> names) {
            this.name = null;
            this.names = names;
        }

        public <T> Builder withDirective(Key<T> key, T value) {
            if (key != null && value != null) {
                this.directives.set(key, value);
            }
            return this;
        }

        public Builder withDirectives(Directives directives) {
            this.directives.acquire(directives);
            return this;
        }

        public Segment build() {
            Segment s = new Segment();
            s.name = name;
            s.names = names;
            s.directives = directives.isEmpty() ? null : directives;
            return s;
        }

    }
}
