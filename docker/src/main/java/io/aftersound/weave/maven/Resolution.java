package io.aftersound.weave.maven;

import java.util.List;
import java.util.Map;

public class Resolution {

    public final List<Map<String, String>> resolved;
    public final List<Map<String, String>> unresolved;
    public final List<Map<String, String>> resolvedWithDependencies;

    public Resolution(List<Map<String, String>> resolved, List<Map<String, String>> unresolved, List<Map<String, String>> resolvedWithDependencies) {
        this.resolved = resolved;
        this.unresolved = unresolved;
        this.resolvedWithDependencies = resolvedWithDependencies;
    }

    public List<Map<String, String>> getResolved() {
        return resolved;
    }

    public List<Map<String, String>> getUnresolved() {
        return unresolved;
    }

    public List<Map<String, String>> getResolvedWithDependencies() {
        return resolvedWithDependencies;
    }

}
