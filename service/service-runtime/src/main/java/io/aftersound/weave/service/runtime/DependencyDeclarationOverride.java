package io.aftersound.weave.service.runtime;

import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.dependency.Declaration;
import io.aftersound.weave.dependency.SimpleDeclaration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DependencyDeclarationOverride {

    public static class NamedTypeConfig {
        private String name;
        private String type;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    private String serviceExecutor;
    private List<NamedTypeConfig> dependingResourceTypes;

    public String getServiceExecutor() {
        return serviceExecutor;
    }

    public void setServiceExecutor(String serviceExecutor) {
        this.serviceExecutor = serviceExecutor;
    }

    public List<NamedTypeConfig> getDependingResourceTypes() {
        return dependingResourceTypes;
    }

    public void setDependingResourceTypes(List<NamedTypeConfig> dependingResourceTypes) {
        this.dependingResourceTypes = dependingResourceTypes;
    }

    private static List<NamedType<?>> createDependencyNamedTypes(List<NamedTypeConfig> ntcList) throws Exception {
        if (ntcList == null) {
            return Collections.emptyList();
        }
        List<NamedType<?>> namedTypes = new ArrayList<>(ntcList.size());
        for (NamedTypeConfig ntc : ntcList) {
            namedTypes.add(NamedType.of(ntc.getName(), Class.forName(ntc.getType())));
        }
        return namedTypes;
    }

    public Declaration dependencyDeclaration() throws Exception{
        return SimpleDeclaration.withRequired(createDependencyNamedTypes(dependingResourceTypes));
    }
}
