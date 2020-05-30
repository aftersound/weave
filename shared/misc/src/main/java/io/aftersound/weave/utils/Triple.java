package io.aftersound.weave.utils;

public class Triple<F,S,T> {

    private final F first;
    private final S second;
    private final T third;

    private Triple(F first, S second, T third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public static <F,S,T> Triple<F,S,T> of(F first, S second, T third) {
        return new Triple(first, second, third);
    }

    public F first() {
        return first;
    }

    public S second() {
        return second;
    }

    public T third() {
        return third;
    }

}
