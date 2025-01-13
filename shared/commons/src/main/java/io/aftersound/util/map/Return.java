package io.aftersound.util.map;

public class Return {

    public enum Type {
        AllMatched,
        FirstMatched,
        LastMatched,
        AtIndex,
        InRange;
    }

    private static final Return ALL_MATCHED = new Return(Type.AllMatched);
    private static final Return FIRST_MATCHED = new Return(Type.FirstMatched);
    private static final Return LAST_MATCHED = new Return(Type.LastMatched);

    private final Type type;

    private int index;

    private int startIndex;
    private int endIndex;

    private Return(Type type) {
        this.type = type;
    }

    public static Return allMatched() {
        return ALL_MATCHED;
    }

    public static Return firstMatched() {
        return FIRST_MATCHED;
    }

    public static Return lastMatched() {
        return LAST_MATCHED;
    }

    public static Return atIndex(int index) {
        return new Return(Type.AtIndex).elementAt(index);
    }

    public static Return inRange(int startIndex, int endIndex) {
        int s = Math.min(startIndex, endIndex);
        int e = Math.max(startIndex, endIndex);
        return new Return(Type.InRange).elementsInRange(s, e);
    }

    private Return elementAt(int index) {
        this.index = index;
        return this;
    }

    private Return elementsInRange(int startIndex, int endIndex) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        return this;
    }

    public Type getType() {
        return type;
    }

    public int getIndex() {
        return index;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }
}
