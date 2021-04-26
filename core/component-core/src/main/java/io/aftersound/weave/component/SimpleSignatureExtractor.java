package io.aftersound.weave.component;

public class SimpleSignatureExtractor implements SignatureExtractor {

    @Override
    public Signature extract(ComponentConfig config) {
        if (config instanceof SimpleComponentConfig) {
            return new SimpleSignature((SimpleComponentConfig) config);
        }
        return null;
    }

}
