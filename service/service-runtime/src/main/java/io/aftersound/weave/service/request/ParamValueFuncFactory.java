package io.aftersound.weave.service.request;

import io.aftersound.func.Descriptor;
import io.aftersound.func.Func;
import io.aftersound.func.MasterAwareFuncFactory;
import io.aftersound.util.TreeNode;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings({ "unchecked" })
public class ParamValueFuncFactory extends MasterAwareFuncFactory {

    private static final List<Descriptor> DESCRIPTORS = Arrays.asList(
//            Descriptor.builder("PARAM:READ", "Map<String, ParamValueHolder>", "Map<String, Object>")
//                    .withDescription("Read parameter values of interests from Map<String, ParamValueHolder> into Map<String, Object>")
//                    .withControls(
//                            Control.builder("String", "The names of parameters to be read, delimited by ','")
//                                    .build()
//                    )
//                    .withExamples(
//                            Example.as(
//                                    "PARAM:READ(firstName,lastName)",
//                                    "Read the values of parameter 'firstName' and 'lastName' from Map<String, ParamValueHolder>"
//                            )
//                    )
//                    .build()
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
