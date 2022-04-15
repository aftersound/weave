package io.aftersound.weave.process.pipeline;

import io.aftersound.weave.common.Context;
import io.aftersound.weave.process.Processor;

public class Pipeline {

    private final Processor processor;

    public Pipeline(Processor processor) {
        this.processor = processor;
    }

    public void run(Context context) {
        processor.process(context);
    }

}
