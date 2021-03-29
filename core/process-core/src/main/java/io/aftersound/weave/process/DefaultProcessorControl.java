package io.aftersound.weave.process;

public final class DefaultProcessorControl implements ProcessorControl {

    private String type;

    @Override
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
