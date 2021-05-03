package io.aftersound.weave.process.pipeline;

import io.aftersound.weave.common.Context;
import io.aftersound.weave.process.Processor;

import java.util.Collections;
import java.util.List;

public class Pipeline {

    private final List<Processor> processors;

    public Pipeline(List<Processor> processorList) {
        assert (processorList != null && processorList.size() > 0);
        this.processors = Collections.unmodifiableList(processorList);
    }

    public void run(Context context) {
        for (Processor processor : processors) {
            processor.process(context);
        }
    }

}
