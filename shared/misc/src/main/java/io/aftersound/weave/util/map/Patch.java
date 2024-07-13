package io.aftersound.weave.util.map;

public class Patch {

    private String op;
    private String path;
    private Object value;


    public static Patch update(String path, Object value) {
        return new Builder("update", path).withValue(value).build();
    }

    public static Patch remove(String path) {
        return new Builder("remove", path).build();
    }

    public static class Builder {

        private final String op;
        private final String path;
        private Object value;

        public Builder(String op, String path) {
            this.op = op;
            this.path = path;
        }

        public Builder withValue(Object value) {
            this.value = value;
            return this;
        }

        public Patch build() {
            Patch p = new Patch();
            p.op = op;
            p.path = path;
            p.value = value;
            return p;
        }

    }
}
