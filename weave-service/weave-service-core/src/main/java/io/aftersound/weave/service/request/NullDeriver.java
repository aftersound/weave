package io.aftersound.weave.service.request;

import io.aftersound.weave.service.metadata.param.DeriveControl;

import java.util.Collections;
import java.util.List;

public class NullDeriver implements Deriver {

    private static final List<String> EMPTY = Collections.emptyList();

    @Override
    public String getType() {
        return "_VOID_";
    }

    @Override
    public List<String> derive(DeriveControl deriveControl, ParamValueHolder sourceValueHolder) {
        return EMPTY;
    }
}
