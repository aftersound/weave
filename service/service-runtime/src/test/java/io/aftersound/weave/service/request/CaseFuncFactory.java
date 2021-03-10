package io.aftersound.weave.service.request;

import io.aftersound.weave.common.*;
import io.aftersound.weave.utils.ExprTreeParsingException;
import io.aftersound.weave.utils.TextualExprTreeParser;
import io.aftersound.weave.utils.TreeNode;

public class CaseFuncFactory extends ValueFuncFactory {

    public static final NamedType<ValueFuncControl> COMPANION_CONTROL_TYPE = CaseFuncControl.TYPE;

    @Override
    public String getType() {
        return COMPANION_CONTROL_TYPE.name();
    }

    @Override
    public <S, E> ValueFunc<S, E> createValueFunc(String valueFuncSpec) {
        if (valueFuncSpec == null || valueFuncSpec.isEmpty()) {
            throw new ValueFuncException("Given value function spec is null or empty");
        }

        TreeNode treeNode;
        try {
            treeNode = TextualExprTreeParser.parse(valueFuncSpec);
        } catch (ExprTreeParsingException e) {
            throw new ValueFuncException("Given value function spec " + valueFuncSpec + " is malformed", e);
        }

        if (!CaseFuncControl.TYPE.name().equals(treeNode.getData())) {
            throw new ValueFuncException("Given value function spec " + valueFuncSpec + " is not supported");
        }

        String caseType = treeNode.getDataOfChildAt(0);
        return (ValueFunc<S, E>) new CaseFunc(caseType);
    }
}
