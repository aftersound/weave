package io.aftersound.weave.utils;

public class Pair<F,S> {

    private final F first;
    private final S second;

    private Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public static <F,S> Pair<F,S> of(F first, S second) {
        return new Pair(first, second);
    }

    public F first() {
        return first;
    }

    public S second() {
        return second;
    }

}
