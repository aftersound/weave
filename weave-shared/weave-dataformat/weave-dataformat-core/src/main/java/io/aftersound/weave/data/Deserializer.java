package io.aftersound.weave.data;

public interface Deserializer {
    <T> T fromBytes(byte[] bytes, Class<T> type) throws Exception;
    <T> T fromString(String content, Class<T> type) throws Exception;
}
