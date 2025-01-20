package io.aftersound.util;

public class Triplet<F,S,T> {

    private final F first;
    private final S second;
    private final T third;

    private Triplet(F first, S second, T third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public static <F,S,T> Triplet<F,S,T> of(F first, S second, T third) {
        return new Triplet<>(first, second, third);
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
