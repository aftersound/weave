package io.aftersound.weave.service;

import io.aftersound.weave.jackson.ObjectMapperBuilder;
import io.aftersound.weave.service.metadata.ExecutionControl;
import io.aftersound.weave.service.metadata.ServiceMetadata;
import io.aftersound.weave.service.metadata.param.Constraint;
import io.aftersound.weave.service.metadata.param.DeriveControl;
import io.aftersound.weave.service.metadata.param.ParamField;
import io.aftersound.weave.service.metadata.param.ParamType;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ServiceMetadataTest {

    private static class MappingDeriveControl extends DeriveControl {

        @Override
        public String getType() {
            return "Mapping";
        }

        private Map<String, String> valueMapping;

        public Map<String, String> getValueMapping() {
            return valueMapping;
        }

        public void setValueMapping(Map<String, String> valueMapping) {
            this.valueMapping = valueMapping;
        }
    }

    @Test
    public void testJson() throws Exception {
        ServiceMetadata serviceMetadata = new ServiceMetadata();
        serviceMetadata.setId("/weave/demo");

        Constraint required = new Constraint();
        required.setType(Constraint.Type.Required);

        Constraint optional = new Constraint();
        optional.setType(Constraint.Type.Optional);

        ParamField p1 = new ParamField();
        p1.setType(ParamType.Path);
        p1.setName("p1");
        p1.setConstraint(required);
        p1.setValueType("String");

        ParamField p2 = new ParamField();
        p2.setType(ParamType.Path);
        p2.setName("p2");
        p2.setConstraint(required);
        p2.setValueType("String");

        ParamField q1 = new ParamField();
        q1.setType(ParamType.Query);
        q1.setName("q1");
        q1.setConstraint(required);
        q1.setValueType("String");

        ParamField q2 = new ParamField();
        q2.setType(ParamType.Query);
        q2.setName("q2");
        q2.setConstraint(required);
        q2.setValueType("String");
        q2.setMultiValued(true);

        ParamField q3 = new ParamField();
        q3.setType(ParamType.Query);
        q3.setName("q3");
        q3.setConstraint(optional);
        q3.setValueType("String");

        ParamField d1 = new ParamField();
        d1.setType(ParamType.Derived);
        d1.setName("d1");
        d1.setConstraint(required);
        MappingDeriveControl derivation = new MappingDeriveControl();
        derivation.setFrom("q2");
        Map<String, String> valueMapping = new HashMap<>();
        valueMapping.put("q2v1", "d1mv1");
        valueMapping.put("q2v2", "d1mv2");
        derivation.setValueMapping(valueMapping);
        d1.setDeriveControl(derivation);
        d1.setValueType("String");
        d1.setMultiValued(true);

        serviceMetadata.setParamFields(Arrays.asList(p1, p2, q1, q2, q3, d1));

        ExecutionControl executionControl = new ExecutionControl() {

            @Override
            public String getType() {
                return "Demo";
            }

        };
        serviceMetadata.setExecutionControl(executionControl);

        String json = ObjectMapperBuilder.forJson().build().writeValueAsString(serviceMetadata);
        System.err.println(json);
    }

}
