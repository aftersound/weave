package io.aftersound.weave.common;

import java.util.ArrayList;
import java.util.List;

public class ReturnInfos {

    private final List<ReturnInfo> list = new ArrayList<>();
    private final List<ReturnInfo> succeeded = new ArrayList<>();
    private final List<ReturnInfo> failed = new ArrayList<>();

    public void addSucceeded(ReturnInfo returnInfo) {
        if (returnInfo != null) {
            list.add(returnInfo);
            succeeded.add(returnInfo);
        }
    }

    public void addFailed(ReturnInfo returnInfo) {
        if (returnInfo != null) {
            list.add(returnInfo);
            failed.add(returnInfo);
        }
    }

    public <V> List<V> extractValues(Key<V> returnInfoKey) {
        List<V> values = new ArrayList<>();
        for (ReturnInfo ri : list) {
            V v = ri.get(returnInfoKey);
            values.add(v);
        }
        return values;
    }

}
