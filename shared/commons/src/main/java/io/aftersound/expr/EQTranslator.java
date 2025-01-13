package io.aftersound.expr;

public class EQTranslator extends CompareBasedTranslator {

    @Override
    protected String getOperator() {
        return "EQ";
    }

}
