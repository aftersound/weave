package io.aftersound.weave.service.request;

import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.service.message.Message;
import io.aftersound.weave.service.message.MessageRegistry;
import io.aftersound.weave.service.message.Messages;
import io.aftersound.weave.service.metadata.param.Enforcement;
import io.aftersound.weave.service.metadata.param.ParamField;
import io.aftersound.weave.service.metadata.param.Validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Validator that validates if a given value, or a set of values, is in allowed value set
 */
public class AllowedValuesValidator implements Validator {

    public static final NamedType<Validation> COMPANION_CONTROL_TYPE = AllowedValuesValidation.TYPE;

    @Override
    public String getType() {
        return COMPANION_CONTROL_TYPE.name();
    }

    @Override
    public Messages validate(ParamField paramField, List<String> values) {
        Messages messages = new Messages();

        // This validation is not responsible for checking Required or not,
        // only values
        if (values == null || values.isEmpty()) {
            return messages;
        }

        AllowedValuesValidation validation = paramField.validation();

        List<String> unexpected = new ArrayList<>();

        Set<String> allowed = validation.getValueSet();
        if (allowed == null || allowed.isEmpty()) {
            // typically, this means given ParamField has problem, not values
            // anyway, give out error as if values have problems
            unexpected.addAll(values);
        } else {
            for (String value : values) {
                if (!allowed.contains(value)) {
                    unexpected.add(value);
                }
            }
        }

        if (unexpected.size() > 0) {
            Message message;

            if (validation.getEnforcement() == Enforcement.Strong) {
                message = MessageRegistry.INVALID_PARAMETER_VALUE.error(
                        paramField.getName(),
                        paramField.getParamType().name(),
                        toString(unexpected)
                );
            } else {
                message = MessageRegistry.INVALID_PARAMETER_VALUE.warning(
                        paramField.getName(),
                        paramField.getParamType().name(),
                        toString(unexpected)
                );
            }

            messages.addMessage(message);
        }

        return messages;
    }

    private String toString(List<String> values) {
        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (String v : values) {
            if (!isFirst) {
                sb.append('|');
            } else {
                isFirst = false;
            }
            sb.append(v);
        }
        return sb.toString();
    }

}
