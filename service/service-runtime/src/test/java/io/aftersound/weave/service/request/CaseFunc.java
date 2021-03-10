package io.aftersound.weave.service.request;

import io.aftersound.weave.common.ValueFunc;

public class CaseFunc implements ValueFunc<String, String> {

    private final String type;

    protected CaseFunc(String caseType) {
        this.type = caseType;
    }

    @Override
    public String process(String source) {
        if (source == null || source.isEmpty()) {
            return source;
        }

        if ("UPPERCASE".equals(type)) {
            return source.toUpperCase();
        }

        if ("lowercase".equals(type)) {
            return source.toUpperCase();
        }

        if ("Capitalization".equals(type)) {
            if (source.length() == 1) {
                return source.toUpperCase();
            } else {
                return source.substring(0,1).toUpperCase() + source.substring(1).toLowerCase();
            }
        }

        return source;
    }

}
