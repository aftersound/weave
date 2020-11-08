package io.aftersound.weave.dependency;

import io.aftersound.weave.common.NamedType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SimpleDeclaration implements Declaration {

    private final List<NamedType<?>> required;

    private SimpleDeclaration(List<NamedType<?>> required) {
        this.required = required;
    }

    public static SimpleDeclaration withRequired(NamedType<?>... dependencyTypes) {
        return new SimpleDeclaration(
                dependencyTypes != null ? Arrays.asList(dependencyTypes) : Collections.<NamedType<?>>emptyList()
        );
    }

    public static SimpleDeclaration withRequired(List<NamedType<?>> dependencyTypes) {
        return new SimpleDeclaration(
                dependencyTypes != null ? Collections.unmodifiableList(dependencyTypes) : Collections.<NamedType<?>>emptyList()
        );
    }

    @Override
    public List<NamedType<?>> getRequired() {
        return Collections.unmodifiableList(required);
    }
}
