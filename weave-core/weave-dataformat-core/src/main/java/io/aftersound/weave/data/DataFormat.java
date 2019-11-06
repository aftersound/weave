package io.aftersound.weave.data;

public interface DataFormat {
    String getType();
    Serializer serializer();
    Deserializer deserializer();
}
