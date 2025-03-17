package io.aftersound.service.request;

import io.aftersound.func.Descriptor;
import io.aftersound.func.Example;
import io.aftersound.func.Func;
import io.aftersound.func.MasterAwareFuncFactory;
import io.aftersound.schema.Field;
import io.aftersound.schema.ProtoTypes;
import io.aftersound.schema.Type;
import io.aftersound.util.TreeNode;

import java.util.Collections;
import java.util.List;

@SuppressWarnings({ "unchecked" })
public class ParamValueFuncFactory extends MasterAwareFuncFactory {

    private static final List<Descriptor> DESCRIPTORS = Collections.singletonList(
            Descriptor.builder("PARAM:READ")
                    .withDescription("Read parameter values of interests from Map<String, ParamValueHolder> into Map<String, Object>")
                    .withControls(
                            Field.listFieldBuilder("parameters", ProtoTypes.STRING.create())
                                    .withDescription("The names of the parameters delimited by ','")
                                    .build()
                    )
                    .withInput(
                            Field.mapFieldBuilder(
                                            "paramValueHolders",
                                            ProtoTypes.STRING.create(),
                                            Type.builder("ParamValueHolder").build()
                                    )
                                    .withDescription("Map<String, ParamValueHolder> which holds the values of each parameter")
                                    .build()
                    )
                    .withOutput(
                            Field.mapFieldBuilder(
                                            "parameterValues",
                                            ProtoTypes.STRING.create(),
                                            ProtoTypes.OBJECT.create()
                                    )
                                    .withDescription("Map<String, Object> which holds the obtained values")
                                    .build()
                    )
                    .withExamples(
                            Example.as(
                                    "PARAM:READ(firstName,lastName)",
                                    "Read the values of parameter 'firstName' and 'lastName' from Map<String, ParamValueHolder>"
                            )
                    )
                    .build()
    );

    @Override
    public List<Descriptor> getFuncDescriptors() {
        return DESCRIPTORS;
    }

    @Override
    public <IN, OUT> Func<IN, OUT> create(TreeNode spec) {
        final String funcName = spec.getData();

        if ("PARAM:READ".equals(funcName)) {
            return (Func<IN, OUT>) new ParamReadFunc(spec.getDataOfChildren());
        }

        return null;
    }

}
