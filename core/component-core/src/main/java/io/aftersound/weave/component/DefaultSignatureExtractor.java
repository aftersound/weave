package io.aftersound.weave.component;

public final class DefaultSignatureExtractor implements SignatureExtractor {

    public static final SignatureExtractor INSTANCE = new DefaultSignatureExtractor();

    private DefaultSignatureExtractor() {
    }

    @Override
    public Signature extract(ComponentConfig config) {
        return new DefaultSignature();
    }

}
