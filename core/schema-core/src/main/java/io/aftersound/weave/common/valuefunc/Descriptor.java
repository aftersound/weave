package io.aftersound.weave.common.valuefunc;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Descriptor implements Serializable {

    private String name;
    private List<String> aliases;
    private List<Control> controls;
    private String input;
    private String output;
    private String description;
    private List<Example> examples;
    private List<Map<String, Object>> resources;

    public String getName() {
        return name;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public List<Control> getControls() {
        return controls;
    }

    public String getInput() {
        return input;
    }

    public String getOutput() {
        return output;
    }

    public String getDescription() {
        return description;
    }

    public List<Example> getExamples() {
        return examples;
    }

    public List<Map<String, Object>> getResources() {
        return resources;
    }

    public static Builder builder(String name, String input, String output) {
        return new Builder(name, input, output);
    }

    public static class Builder {

        private final String name;
        private final String input;
        private final String output;

        private String[] aliases;
        private Control[] controls;
        private String description;
        private Example[] examples;
        private Map<String, Object>[] resources;

        private Builder(String name, String input, String output) {
            this.name = name;
            this.input = input;
            this.output = output;
        }

        public Builder withAliases(String... aliases) {
            this.aliases = aliases;
            return this;
        }

        public Builder withControls(Control... controls) {
            this.controls = controls;
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder withExamples(Example... examples) {
            this.examples = examples;
            return this;
        }

        public Builder withResources(Map<String, Object>... resources) {
            this.resources = resources;
            return this;
        }

        public Descriptor build() {
            Descriptor descriptor = new Descriptor();
            descriptor.name = name;
            descriptor.input = input;
            descriptor.output = output;
            descriptor.description = description;
            descriptor.aliases = aliases != null ? Arrays.asList(aliases) : Collections.emptyList();
            descriptor.controls = controls != null ? Arrays.asList(controls) : Collections.emptyList();
            descriptor.examples = examples != null ? Arrays.asList(examples) : Collections.emptyList();
            descriptor.resources = resources != null ? Arrays.asList(resources) : Collections.emptyList();
            return descriptor;
        }
    }
}
