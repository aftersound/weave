package io.aftersound.component;

/**
 * Responsible for extracting {@link Signature} of {@link ComponentConfig}
 */
public interface SignatureExtractor {
    Signature extract(ComponentConfig config);
}
