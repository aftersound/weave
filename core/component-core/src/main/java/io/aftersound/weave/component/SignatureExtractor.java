package io.aftersound.weave.component;

/**
 * Responsible for extracting {@link Signature} of {@link ComponentConfig}
 */
public interface SignatureExtractor {
    Signature extract(ComponentConfig config);
}
