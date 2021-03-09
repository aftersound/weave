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

    /**
     * Create {@link ValueFunc} in according to given {@link ValueFuncControl}
     * @param valueFuncControl
     *          a {@link ValueFuncControl}, effectively value function specification
     * @param <S>
     *          generic type of source entity which {@link ValueFunc} can accept
     * @param <E>
     *          generic type of target entity which {@link ValueFunc} can return
     * @return
     *          a {@link ValueFunc} which fully acts upon given {@link ValueFuncControl}
     */
    public final <S,E> ValueFunc<S,E> createCodec(ValueFuncControl valueFuncControl) {
        return createValueFunc(valueFuncControl.asValueFuncSpec());
    }
}
