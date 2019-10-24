package io.aftersound.weave.service.request;

import io.aftersound.weave.service.metadata.param.DeriveControl;

import java.util.List;

public interface Deriver {
    String getType();
    List<String> derive(DeriveControl deriveControl, ParamValueHolder sourceValueHolder);
}
