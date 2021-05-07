package io.aftersound.weave.utils;

public interface Visitor<E, V> {
    void visit(E e);
    V getVisited();
}
