package io.aftersound.weave.common;

/**
 * A conceptual factory which creates {@link ValueFunc} in according to
 *  1.codec specification or
 *  2.{@link ValueFuncControl}
 */
public abstract class ValueFuncFactory {

    /**
     * @return type name of this factory, which is identical to
     * the type name of corresponding {@link ValueFuncControl}
     */
    public abstract String getType();

    /**
     * Create {@link ValueFunc} in according to given specification
     *
     * @param valueFuncSpec value function specification in string form
     * @param <S>           generic type of source value which {@link ValueFunc} can accept
     * @param <T>           generic type of target value which {@link ValueFunc} can return
     * @return a {@link ValueFunc} which fully acts upon given specification
     */
    public abstract <S,T> ValueFunc<S,T> createValueFunc(String valueFuncSpec);
}
