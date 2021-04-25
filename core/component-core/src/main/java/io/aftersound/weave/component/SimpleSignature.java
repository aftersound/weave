package io.aftersound.weave.component;

public final class SimpleSignature implements Signature {

    private final String type;
    private final int optionHash;

    SimpleSignature(SimpleComponentConfig simpleComponentConfig) {
        this.type = simpleComponentConfig.getType();
        this.optionHash = simpleComponentConfig.getOptions().hashCode();
    }

    @Override
    public boolean match(Signature another) {
        if (!(another instanceof SimpleSignature)) {
            return false;
        }
        SimpleSignature that = (SimpleSignature) another;
        return this.type.equals(that.type) && this.optionHash == that.optionHash;
    }

}
