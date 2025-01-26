package io.aftersound.func;

import io.aftersound.util.TreeNode;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unchecked", "rawtypes"})
public class ChainFuncFactory extends MasterAwareFuncFactory {

    private static final List<Descriptor> DESCRIPTORS = List.of(
            Descriptor.builder("CHAIN").build()
    );

    @Override
    public List<Descriptor> getFuncDescriptors() {
        return DESCRIPTORS;
    }

    @Override
    public <IN, OUT> Func<IN, OUT> create(TreeNode directive) {
        final String funcName = directive.getData();

        if ("CHAIN".equals(funcName)) {
            return createChainFunc(directive);
        }

        return null;
    }

    private Func createChainFunc(TreeNode directive) {
        final List<TreeNode> children = directive.getChildren();
        if (children.isEmpty()) {
            throw new CreationException("Chain requires at least one child");
        }
        List<Func<Object, Object>> chain = new ArrayList<>(children.size());
        for (TreeNode child : children) {
            chain.add(masterFuncFactory.create(child));
        }
        return new ChainFunc(chain);
    }

    private static class ChainFunc implements Func<Object, Object> {

        private final List<Func<Object, Object>> chain;

        public ChainFunc(List<Func<Object, Object>> chain) {
            this.chain = chain;
        }

        @Override
        public Object apply(Object source) {
            Object v = source;
            for (Func<Object, Object> func : chain) {
                v = func.apply(v);
            }
            return v;
        }
    }

}
