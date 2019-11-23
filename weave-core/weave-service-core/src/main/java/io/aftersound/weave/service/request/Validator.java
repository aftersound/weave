package io.aftersound.weave.service.request;

import io.aftersound.weave.service.message.Messages;
import io.aftersound.weave.service.metadata.param.ParamField;
import io.aftersound.weave.service.metadata.param.Validation;

import java.util.List;

public interface Validator {
    String getType();
    Messages validate(ParamField paramField, List<String> values);
}
