package io.aftersound.weave.common;

import io.aftersound.weave.utils.Factory;
import io.aftersound.weave.utils.Registry;

public class FieldsRegistry extends Registry<String, Fields> {

    private static final String FIELDS = "FIELDS";

    private final ValueFuncRegistry valueFuncRegistry;

    public FieldsRegistry(ValueFuncRegistry valueFuncRegistry) {
        super();
        this.valueFuncRegistry = valueFuncRegistry;
    }

    /**
     * FIELDS(FIELD(name,type,valueSpec,[source]),...)
     *
     * @param fieldsSpec specification of fields
     * @return {@link Fields} which conforms to given specification
     */
    public Fields getFields(String fieldsSpec, final boolean fieldWithValueType) {
        return super.get(
                fieldsSpec,
                new Factory<String, Fields>() {
                    @Override
                    public Fields create(String spec) {
                        if (fieldWithValueType) {
                            return Fields.fromSpecWithValueType(spec, valueFuncRegistry);
                        } else {
                            return Fields.fromSpecWithoutValueType(spec, valueFuncRegistry);
                        }
                    }
                }
        );
    }

}
