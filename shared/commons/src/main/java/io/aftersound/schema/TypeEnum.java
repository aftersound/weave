package io.aftersound.schema;

import java.util.Map;

public enum TypeEnum {
    ARRAY {

        @Override
        public Type createType() {
            return createType(null);
        }

        @Override
        public Type createType(Map<String, Object> options) {
            return Type.builder("ARRAY").withOptions(options).build();
        }
    },
    BOOLEAN {

        @Override
        public Type createType() {
            return createType(null);
        }

        @Override
        public Type createType(Map<String, Object> options) {
            return Type.builder("BOOLEAN").withOptions(options).build();
        }

    },
    BYTES {

        @Override
        public Type createType() {
            return createType(null);
        }

        @Override
        public Type createType(Map<String, Object> options) {
            return Type.builder("BYTES").withOptions(options).build();
        }

    },
    CHAR {

        @Override
        public Type createType() {
            return createType(null);
        }

        @Override
        public Type createType(Map<String, Object> options) {
            return Type.builder("CHAR").withOptions(options).build();
        }

    },
    DATE {

        @Override
        public Type createType() {
            return createType(null);
        }

        @Override
        public Type createType(Map<String, Object> options) {
            return Type.builder("DATE").withOptions(options).build();
        }

    },
    DOUBLE {

        @Override
        public Type createType() {
            return createType(null);
        }

        @Override
        public Type createType(Map<String, Object> options) {
            return Type.builder("DOUBLE").withOptions(options).build();
        }

    },
    FLOAT {

        @Override
        public Type createType() {
            return createType(null);
        }

        @Override
        public Type createType(Map<String, Object> options) {
            return Type.builder("FLOAT").withOptions(options).build();
        }

    },
    INTEGER {

        @Override
        public Type createType() {
            return createType(null);
        }

        @Override
        public Type createType(Map<String, Object> options) {
            return Type.builder("INTEGER").withOptions(options).build();
        }

    },
    LIST {

        @Override
        public Type createType() {
            return createType(null);
        }

        @Override
        public Type createType(Map<String, Object> options) {
            return Type.builder("LIST").withOptions(options).build();
        }

    },
    LONG {

        @Override
        public Type createType() {
            return createType(null);
        }

        @Override
        public Type createType(Map<String, Object> options) {
            return Type.builder("LONG").withOptions(options).build();
        }

    },
    MAP {

        @Override
        public Type createType() {
            return createType(null);
        }

        @Override
        public Type createType(Map<String, Object> options) {
            return Type.builder("MAP").withOptions(options).build();
        }

    },
    SET {

        @Override
        public Type createType() {
            return createType(null);
        }

        @Override
        public Type createType(Map<String, Object> options) {
            return Type.builder("SET").build();
        }

    },
    SHORT {

        @Override
        public Type createType() {
            return createType(null);
        }

        @Override
        public Type createType(Map<String, Object> options) {
            return Type.builder("SHORT").withOptions(options).build();
        }

    },
    STRING {

        @Override
        public Type createType() {
            return createType(null);
        }

        @Override
        public Type createType(Map<String, Object> options) {
            return Type.builder("STRING").withOptions(options).build();
        }

    },
    OBJECT {

        @Override
        public Type createType() {
            return createType(null);
        }

        @Override
        public Type createType(Map<String, Object> options) {
            return Type.builder("OBJECT").withOptions(options).build();
        }

    };

    public abstract Type createType();

    public abstract Type createType(Map<String, Object> options);
}
