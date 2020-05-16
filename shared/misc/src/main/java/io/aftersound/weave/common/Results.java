package io.aftersound.weave.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Results {

    private final List<Result> list;

    Results(int size) {
        this.list = new ArrayList<>(size);
    }

    Results add(Result result) {
        list.add(result);
        return this;
    }

    public int size() {
        return list.size();
    }

    public List<Result> all() {
        return Collections.unmodifiableList(list);
    }

}
