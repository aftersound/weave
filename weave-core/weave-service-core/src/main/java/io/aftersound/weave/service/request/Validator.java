package io.aftersound.weave.service.request;

import io.aftersound.weave.service.message.Messages;
import io.aftersound.weave.service.metadata.param.Validation;

import java.util.List;

public interface Validator<VALIDATION extends Validation> {
    String getType();
    Messages validate(VALIDATION validation, List<String> values);
}
