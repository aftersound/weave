package io.aftersound.func;

import io.aftersound.util.ExprTreeParsingException;
import io.aftersound.util.TreeNode;

import java.util.Collections;
import java.util.List;

public interface FuncFactory {

    /**
     * @return the descriptors of {@link Func}s supported by this {@link FuncFactory}
     */
    default List<Descriptor> getFuncDescriptors() {
        return DescriptorHelper.getDescriptors(getClass());
    }

    /**
     * Create a {@link Func} in according to given directive in form of textual expression
     *
     * @param spec  - the spec, in form of textual expression, of the function to be created
     * @param <IN>  - the input type of the function to be created
     * @param <OUT> - the output of the function to be created
     * @return a {@link Func} which fully acts upon given spec
     */
    default <IN,OUT> Func<IN,OUT> create(String spec) {
        final TreeNode treeNode;
        try {
            treeNode = TreeNode.from(spec);
        } catch (ExprTreeParsingException e) {
            String msg = String.format("'%s' is not a valid func spec", spec);
            throw new CreationException(msg, e);
        }

        return create(treeNode);
    }

    /**
     * Create {@link Func} in according to given directive in form of {@link TreeNode}
     *
     * @param spec  - the spec, in form of {@link TreeNode}, of the function to be created
     * @param <IN>  - the input type of the function to be created
     * @param <OUT> - the output of the function to be created
     * @return a {@link Func} which fully acts upon given spec
     */
    <IN, OUT> Func<IN, OUT> create(TreeNode spec);

}
