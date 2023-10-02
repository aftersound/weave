package io.aftersound.weave.common;

public enum TypeEnum {
    ARRAY {
        @Override
        public Type createType() {
            Type type = new Type();
            type.setName("ARRAY");
            return type;
        }
    },
    BOOLEAN {
        @Override
        public Type createType() {
            Type type = new Type();
            type.setName("BOOLEAN");
            return type;
        }
    },
    BYTES {
        @Override
        public Type createType() {
            Type type = new Type();
            type.setName("BYTES");
            return type;
        }
    },
    CHAR {
        @Override
        public Type createType() {
            Type type = new Type();
            type.setName("CHAR");
            return type;
        }
    },
    DATE {
        @Override
        public Type createType() {
            Type type = new Type();
            type.setName("DATE");
            return type;
        }
    },
    DOUBLE {
        @Override
        public Type createType() {
            Type type = new Type();
            type.setName("DOUBLE");
            return type;
        }
    },
    FLOAT {
        @Override
        public Type createType() {
            Type type = new Type();
            type.setName("FLOAT");
            return type;
        }
    },
    INTEGER {
        @Override
        public Type createType() {
            Type type = new Type();
            type.setName("INTEGER");
            return type;
        }
    },
    LIST {
        @Override
        public Type createType() {
            Type type = new Type();
            type.setName("LIST");
            return type;
        }
    },
    LONG {
        @Override
        public Type createType() {
            Type type = new Type();
            type.setName("LONG");
            return type;
        }
    },
    MAP {
        @Override
        public Type createType() {
            Type type = new Type();
            type.setName("MAP");
            return type;
        }
    },
    SET {
        @Override
        public Type createType() {
            Type type = new Type();
            type.setName("SET");
            return type;
        }
    },
    SHORT {
        @Override
        public Type createType() {
            Type type = new Type();
            type.setName("SHORT");
            return type;
        }
    },
    STRING {
        @Override
        public Type createType() {
            Type type = new Type();
            type.setName("STRING");
            return type;
        }
    },
    OBJECT {
        @Override
        public Type createType() {
            Type type = new Type();
            type.setName("OBJECT");
            return type;
        }
    };

    public abstract Type createType();
}
