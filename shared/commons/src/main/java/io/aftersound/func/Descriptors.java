package io.aftersound.func;

import io.aftersound.dict.Dictionary;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A {@link Dictionary} of function {@link Descriptor}
 */
public class Descriptors implements Dictionary<Descriptor> {

    private final List<Descriptor> all;
    private final Map<String, Descriptor> byName;

    public Descriptors(List<Descriptor> descriptors) {
        descriptors.sort(Comparator.comparing(Descriptor::getName));

        this.all = Collections.unmodifiableList(descriptors);
        this.byName = descriptors.stream().collect(
                Collectors.toMap(Descriptor::getName, d -> d)
        );
    }

    @Override
    public List<Descriptor> all() {
        return all;
    }

    @Override
    public Descriptor byName(String name) {
        return byName.get(name);
    }

    @Override
    public <ATTR> ATTR getAttribute(String name, String attributeName) {
        return null;
    }

}
