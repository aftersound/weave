package io.aftersound.weave.service.request;

import io.aftersound.weave.service.message.Messages;
import io.aftersound.weave.service.metadata.param.ParamField;

import java.util.List;

public class NullValidator implements Validator {

    @Override
    public String getType() {
        return "VOID";
    }

    @Override
    public Messages validate(ParamField paramField, List<String> values) {
        return new Messages();
    }
}
