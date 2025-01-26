package io.aftersound.func;

import io.aftersound.schema.Field;
import io.aftersound.util.TreeNode;

import java.util.List;
import java.util.Map;

@SuppressWarnings({"unchecked","rawtypes"})
public class ParseFuncFactory extends MasterAwareFuncFactory {

    private static final List<Descriptor> DESCRIPTORS = List.of(
            Descriptor.builder("RECORD:PARSE")
                    .withControls(
                            Field.stringFieldBuilder("schemaId").build(),
                            Field.integerFieldBuilder("schemaRegistryId").build()
                    )
                    .build()
    );

    @Override
    public List<Descriptor> getFuncDescriptors() {
        return DESCRIPTORS;
    }

    @Override
    public <IN, OUT> Func<IN, OUT> create(TreeNode directive) {
        final String funcName = directive.getData();
        if ("RECORD:PARSE".equals(funcName)) {
            return createParseFunc(directive);
        }

        return null;
    }

    private Func createParseFunc(TreeNode directive) {
        final String schemaId = directive.getDataOfChildAt(0);
        final String schemaRegistryId = directive.getDataOfChildAt(1);

        return new ParseFunc(schemaId, schemaRegistryId);
    }

    private static class ParseFunc extends AbstractFuncWithHints<Object, Map<String, Object>> {

        private final String schemaId;
        private final String schemaRegistryId;

        public ParseFunc(String schemaId, String schemaRegistryId) {
            this.schemaId = schemaId;
            this.schemaRegistryId = schemaRegistryId;
        }

        @Override
        public Map<String, Object> apply(Object o) {
            return Map.of();
        }

    }

}
