package io.aftersound.weave.codec;

/**
 * A conceptual encoder which encodes entity of SOURCE type into entity of ENCODED type
 * @param <SOURCE>
 *          generic type of source entity
 * @param <ENCODED>
 *          generic type of entity encoded from source
 */
public interface Encoder<SOURCE,ENCODED> {
    ENCODED encode(SOURCE source);
}
