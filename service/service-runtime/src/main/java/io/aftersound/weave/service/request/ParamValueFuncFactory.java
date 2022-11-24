package io.aftersound.weave.service.request;

import io.aftersound.weave.common.ValueFunc;
import io.aftersound.weave.common.ValueFuncFactory;
import io.aftersound.weave.common.valuefunc.Control;
import io.aftersound.weave.common.valuefunc.Descriptor;
import io.aftersound.weave.common.valuefunc.Example;
import io.aftersound.weave.utils.TreeNode;

import java.util.Arrays;
import java.util.Collection;

public class ParamValueFuncFactory extends ValueFuncFactory {

    private static final Collection<Descriptor> DESCRIPTORS = Arrays.asList(
            Descriptor.builder("PARAM:READ", "Map<String, ParamValueHolder>", "Map<String, Object>")
                    .withDescription("Read parameter values of interests from Map<String, ParamValueHolder> into Map<String, Object>")
                    .withControls(
                            Control.builder("String", "The names of parameters to be read, delimited by ','")
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
    public Collection<Descriptor> getValueFuncDescriptors() {
        return DESCRIPTORS;
    }

    @Override
    public <S, E> ValueFunc<S, E> create(TreeNode spec) {
        final String funcName = spec.getData();

        if ("PARAM:READ".equals(funcName)) {
            return (ValueFunc<S, E>) new ParamReadFunc(spec.getDataOfChildren());
        }

        return null;
    }

}
