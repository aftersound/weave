package io.aftersound.config;

import io.aftersound.func.Func;
import io.aftersound.func.FuncFactory;
import io.aftersound.func.MasterFuncFactory;
import io.aftersound.func.common.*;

public class Parsers {

    private static final FuncFactory FUNC_FACTORY = new MasterFuncFactory(
            new Base64FuncFactory(),
            new BooleanFuncFactory(),
            new IntegerFuncFactory(),
            new LongFuncFactory(),
            new StringFuncFactory()
    );

    public static final Func<Object, String> BASE64_DECODER = FUNC_FACTORY.create("BASE:DECODE(String,String)");
    public static final Func<Object, Boolean> BOOLEAN_PARSER = FUNC_FACTORY.create("BOOLEAN:FROM(String,true,false)");
    public static final Func<Object, Integer> INTEGER_PARSER = FUNC_FACTORY.create("INTEGER:FROM(String)");
    public static final Func<Object, Long> LONG_PARSER = FUNC_FACTORY.create("LONG:FROM(String)");
    public static final Func<Object, String> STRING_PARSER = FUNC_FACTORY.create("STR:FROM()");
}
