package io.aftersound.weave.data;

public interface Serializer {
    <T> byte[] toBytes(T data) throws Exception;
    <T> String toString(T data) throws Exception;
}
