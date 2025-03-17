package io.aftersound.sample.extension.service.service;

import io.aftersound.common.NamedType;
import io.aftersound.service.metadata.ExecutionControl;

import java.util.List;

public class GreetingExecutionControl implements ExecutionControl {

    static final NamedType<ExecutionControl> TYPE = NamedType.of("GreetingService", GreetingExecutionControl.class);

    @Override
    public String getType() {
        return TYPE.name();
    }

    private List<String> greetingWords;

    public List<String> getGreetingWords() {
        return greetingWords;
    }

    public void setGreetingWords(List<String> greetingWords) {
        this.greetingWords = greetingWords;
    }

}
