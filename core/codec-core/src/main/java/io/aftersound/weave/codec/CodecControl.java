package io.aftersound.weave.codec;

import io.aftersound.weave.metadata.Control;

/**
 * A {@link Control} for {@link CodecFactory} to act accordingly to create {@link Codec}
 */
public interface CodecControl extends Control {

    /**
     * Convert this into codec specification in string format
     * @return codec spec in form of textual expression
     */
    String asCodecSpec();
}
