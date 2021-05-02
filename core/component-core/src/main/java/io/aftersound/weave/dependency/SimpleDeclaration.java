package io.aftersound.weave.dependency;

import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.common.NamedTypes;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SimpleDeclaration implements Declaration {

    private final NamedTypes required;

    private SimpleDeclaration(List<NamedType<?>> required) {
        NamedTypes namedTypes = new NamedTypes();
        for (NamedType<?> namedType : required) {
            namedTypes.include(namedType);
        }
        this.required = namedTypes;
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
    public Collection<NamedType<?>> getRequired() {
        return required.all();
    }

    @Override
    public boolean isRequired(String id) {
        return required.names().contains(id);
    }

    @Override
    public Collection<String> requiredIds() {
        return required.names();
    }

}
