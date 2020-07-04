package io.aftersound.weave.utils;

public final class Range<T> {

    private T lower;
    private T upper;
    private boolean lowerInclusive;
    private boolean upperInclusive;

    public T getLower() {
        return lower;
    }

    public void setLower(T lower) {
        this.lower = lower;
    }

    public T getUpper() {
        return upper;
    }

    public void setUpper(T upper) {
        this.upper = upper;
    }

    public boolean isLowerInclusive() {
        return lowerInclusive;
    }

    public void setLowerInclusive(boolean lowerInclusive) {
        this.lowerInclusive = lowerInclusive;
    }

    public boolean isUpperInclusive() {
        return upperInclusive;
    }

    public void setUpperInclusive(boolean upperInclusive) {
        this.upperInclusive = upperInclusive;
    }

}
