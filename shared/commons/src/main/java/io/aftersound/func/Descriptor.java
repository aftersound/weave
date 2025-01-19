package io.aftersound.func;

import io.aftersound.schema.Field;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Describes a {@link Func}
 */
public class Descriptor implements Serializable {

    /**
     * the name of the function
     */
    private String name;

    /**
     * the aliases of the function
     */
    private List<String> aliases;

    /**
     * the control parameters of the function
     */
    private List<Field> controls;

    /**
     * the input of the function
     */
    private Field input;

    /**
     * the output of the function
     */
    private Field output;

    /**
     * the description of the function
     */
    private String description;

    /**
     * the examples of the function
     */
    private List<Example> examples;

    /**
     * the resources that the function depends on
     */
    private List<Map<String, Object>> resources;

    public String getName() {
        return name;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public List<Field> getControls() {
        return controls;
    }

    public Field getInput() {
        return input;
    }

    public Field getOutput() {
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

    public static Builder builder(String name) {
        return new Builder(name);
    }

    public static class Builder {

        private final String name;

        private String[] aliases;

        private Field input;
        private Field output;

        private Field[] controls;
        private String description;
        private Example[] examples;
        private Map<String, Object>[] resources;

        private Builder(String name) {
            this.name = name;
        }

        public Builder withAliases(String... aliases) {
            this.aliases = aliases;
            return this;
        }

        public Builder withInput(Field input) {
            this.input = input;
            return this;
        }

        public Builder withOutput(Field output) {
            this.output = output;
            return this;
        }

        public Builder withControls(Field... controls) {
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
