package io.aftersound.weave.common;

import io.aftersound.weave.common.valuefunc.Descriptor;
import io.aftersound.weave.utils.ExprTreeParsingException;
import io.aftersound.weave.utils.TextualExprTreeParser;
import io.aftersound.weave.utils.TreeNode;

import java.util.Collection;

/**
 * A conceptual factory which creates {@link ValueFunc} in according to specification
 */
public abstract class ValueFuncFactory {

    /**
     * @return the descriptors of {@link ValueFunc}s supported by this {@link ValueFuncFactory}
     */
    public abstract Collection<Descriptor> getValueFuncDescriptors();

    /**
     * Create {@link ValueFunc} in according to given specification in form of textual expression
     *
     * @param spec          value function specification in form of textual expression
     * @param <S>           generic type of source value which {@link ValueFunc} can accept
     * @param <T>           generic type of target value which {@link ValueFunc} can return
     * @return a {@link ValueFunc} which fully acts upon given specification
     */
    public final <S,T> ValueFunc<S,T> create(String spec) {
        try {
            TreeNode specAsTreeNode = TextualExprTreeParser.parse(spec);
            return create(specAsTreeNode);
        } catch (ExprTreeParsingException e) {
            throw new ValueFuncException("'" + spec + "'" + " is not a valid spec for ValueFunc ", e);
        }
    }

    /**
     * Create {@link ValueFunc} in according to given specification in form of {@link TreeNode}
     *
     * @param spec          value function specification in form of {@link TreeNode}
     * @param <S>           generic type of source value which {@link ValueFunc} can accept
     * @param <T>           generic type of target value which {@link ValueFunc} can return
     * @return a {@link ValueFunc} which fully acts upon given specification
     */
    public abstract <S, T> ValueFunc<S, T> create(TreeNode spec);

}
