package io.aftersound.weave.codec;

/**
 * A conceptual factory which creates {@link Codec} in according to
 *   1.codec specification or
 *   2.{@link CodecControl}
 */
public abstract class CodecFactory {

    /**
     * @return
     *          type name of this factory, which is identical to
     *          the type name of corresponding {@link CodecControl}
     */
    public abstract String getType();

    /**
     * Create {@link Codec} in according to given codec specification
     * @param codecSpec
     *          codec specification in string form
     * @param <S>
     *          generic type of source entity which {@link Codec} can accept
     * @param <E>
     *          generic type of target entity which {@link Codec} can encode into
     * @return
     *          a {@link Codec} which fully acts upon given codec specification
     */
    public abstract <S,E> Codec<S,E> createCodec(String codecSpec);

    /**
     * Create {@link Codec} in according to given {@link CodecControl}
     * @param codecControl
     *          a {@link CodecControl}, effectively codec specification
     * @param <S>
     *          generic type of source entity which {@link Codec} can accept
     * @param <E>
     *          generic type of target entity which {@link Codec} can encode into
     * @return
     *          a {@link Codec} which fully acts upon given {@link CodecControl}
     */
    public final <S,E> Codec<S,E> createCodec(CodecControl codecControl) {
        return createCodec(codecControl.asCodecSpec());
    }

}
