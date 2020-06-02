package io.aftersound.weave.codec;

/**
 * A conceptual encoder which decodes entity in endoced form
 * @param <ENCODED>
 *          generic type of entity encoded from source
 * @param <DECODED>
 *          generic type of entity decoded from encoded form
 */
public interface Decoder<ENCODED, DECODED> {
    DECODED decode(ENCODED encoded);
}
