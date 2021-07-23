package io.aftersound.weave.process;

import io.aftersound.weave.common.Context;

import java.util.List;

public class ChainedProcessor implements Processor {

    private final List<Processor> chain;

    public ChainedProcessor(List<Processor> chain) {
        this.chain = chain;
    }

    @Override
    public void process(Context ctx) {
        for (Processor processor : chain) {
            processor.process(ctx);
        }
    }

}
