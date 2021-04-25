package io.aftersound.weave.service;

import io.aftersound.weave.jackson.ObjectMapperBuilder;
import io.aftersound.weave.service.metadata.ExecutionControl;
import io.aftersound.weave.service.metadata.ServiceMetadata;
import io.aftersound.weave.service.metadata.param.Constraint;
import io.aftersound.weave.service.metadata.param.ParamField;
import io.aftersound.weave.service.metadata.param.ParamType;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ServiceMetadataTest {

    @Test
    public void testJson() throws Exception {
        ServiceMetadata serviceMetadata = new ServiceMetadata();
        serviceMetadata.setPath("/weave/demo");

        Constraint required = new Constraint();
        required.setType(Constraint.Type.Required);

        Constraint optional = new Constraint();
        optional.setType(Constraint.Type.Optional);

        ParamField p1 = new ParamField();
        p1.setParamType(ParamType.Path);
        p1.setName("p1");
        p1.setType("String");
        p1.setConstraint(required);
        p1.setValueFuncSpec("_");

        ParamField p2 = new ParamField();
        p2.setParamType(ParamType.Path);
        p2.setName("p2");
        p2.setType("String");
        p2.setConstraint(required);
        p2.setValueFuncSpec("_");

        ParamField q1 = new ParamField();
        q1.setParamType(ParamType.Query);
        q1.setName("q1");
        q1.setType("String");
        q1.setConstraint(required);
        q1.setValueFuncSpec("_");

        ParamField q2 = new ParamField();
        q2.setParamType(ParamType.Query);
        q2.setName("q2");
        q2.setType("String");
        q2.setConstraint(required);
        q2.setValueFuncSpec("_");
        q2.setMultiValued(true);

        ParamField q3 = new ParamField();
        q3.setParamType(ParamType.Query);
        q3.setName("q3");
        q3.setType("String");
        q3.setConstraint(optional);
        q3.setValueFuncSpec("_");

        ParamField d1 = new ParamField();
        d1.setParamType(ParamType.Derived);
        d1.setName("d1");
        d1.setType("String");
        d1.setConstraint(required);
        Map<String, String> valueMapping = new HashMap<>();
        valueMapping.put("q2v1", "d1mv1");
        valueMapping.put("q2v2", "d1mv2");
        d1.setValueFuncSpec("_");
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
