package io.aftersound.dict;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SimpleDictionary<E> implements Dictionary<E> {

    private final String name;
    private final List<E> entries;
    private final Map<String, E> byEntryName;
    private final AttributeAccessor<E> attributeAccessor;

    private SimpleDictionary(
            String name, List<E> entries, Map<String, E> byEntryName, AttributeAccessor<E> attributeAccessor) {
        this.name = name;
        this.entries = entries;
        this.byEntryName = byEntryName;
        this.attributeAccessor = attributeAccessor != null ? attributeAccessor : new NullAttributeAccessor<>();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<E> all() {
        return entries;
    }

    @Override
    public List<E> filter(Predicate<E> predicate) {
        return entries.stream().filter(predicate).collect(Collectors.toList());
    }

    @Override
    public E byEntryName(String name) {
        return byEntryName.get(name);
    }

    @Override
    public <ATTR> ATTR getAttribute(String entryName, String attributeName) {
        E entry = byEntryName.get(entryName);
        if (entry != null) {
            return attributeAccessor.get(entry, attributeName);
        } else {
            return null;
        }
    }

    public static <E> Builder<E> builder() {
        return new Builder<>(null);
    }

    public static <E> Builder<E> builder(String name) {
        return new Builder<>(name);
    }

    public static class Builder<E> {

        private final String name;
        private final List<E> entries;
        private Function<E, String> entryNameFunc;
        private AttributeAccessor<E> attributeAccessor;

        private Builder(String name) {
            this.name = name;
            this.entries = new ArrayList<>();
        }

        public Builder<E> withEntries(Collection<E> entries) {
            if (entries != null) {
                for (E entry : entries) {
                    if (entry != null) {
                        this.entries.add(entry);
                    }
                }
            }
            return this;
        }

        public Builder<E> withEntry(E entry) {
            if (entry != null) {
                this.entries.add(entry);
            }
            return this;
        }

        public Builder<E> withEntryNameFunc(Function<E, String> nameFunc) {
            this.entryNameFunc = nameFunc;
            return this;
        }

        public Builder<E> withAttributeAccessor(AttributeAccessor<E> attributeAccessor) {
            this.attributeAccessor = attributeAccessor;
            return this;
        }

        public Dictionary<E> build() {
            final Function<E, String> nameFunc = Objects.requireNonNullElseGet(entryNameFunc, () -> Object::toString);

            return new SimpleDictionary<>(
                    name,
                    Collections.unmodifiableList(entries),
                    entries.stream().collect(Collectors.toMap(nameFunc, e -> e)),
                    attributeAccessor
            );
        }

    }
}
