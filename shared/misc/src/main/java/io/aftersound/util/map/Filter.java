package io.aftersound.util.map;

import io.aftersound.weave.utils.MapBuilder;
import org.mvel2.MVEL;
import org.mvel2.compiler.CompiledExpression;
import org.mvel2.compiler.ExpressionCompiler;

import java.util.*;

public class Filter {

    private static final Set<Class<?>> BOXED_TYPES;
    static {
        Set<Class<?>> boxedTypes = new HashSet<>();
        boxedTypes.add(Boolean.class);
        boxedTypes.add(Byte.class);
        boxedTypes.add(Character.class);
        boxedTypes.add(Double.class);
        boxedTypes.add(Float.class);
        boxedTypes.add(Integer.class);
        boxedTypes.add(Long.class);
        boxedTypes.add(Short.class);
        boxedTypes.add(Void.class);
        BOXED_TYPES = Collections.unmodifiableSet(boxedTypes);
    }

    private final String expression;
    private final boolean isWildcard;
    private CompiledExpression compiledExpression;

    public Filter(String expr) {
        this.expression = expr;
        this.isWildcard = "*".equals(expr);
    }

    public boolean isWildcard() {
        return isWildcard;
    }

    public Filter compile() {
        if (!isWildcard) {
            String expr = expression.replace("%5B", "[").replace("%5D", "]");
            this.compiledExpression = new ExpressionCompiler(expr).compile();
        }
        return this;
    }

    public Boolean apply(Object o) {
        if (isWildcard) {
            return true;
        }

        Map variables = new HashMap();
        if (o instanceof Map) {
            variables = (Map) o;
        }

        if (o instanceof String || o.getClass().isPrimitive() || BOXED_TYPES.contains(o.getClass())) {
            variables = MapBuilder.hashMap().put("$", o).build();
        }

        return MVEL.executeExpression(compiledExpression, variables, Boolean.class);
    }
}
