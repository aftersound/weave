package io.aftersound.func.common;

import io.aftersound.func.*;
import io.aftersound.util.TreeNode;

import java.util.*;

@SuppressWarnings({ "rawtypes", "unchecked" })
class CollectionsFuncFactory extends MasterAwareFuncFactory {

    private static final List<Descriptor> DESCRIPTORS = DescriptorHelper.getDescriptors(CollectionsFuncFactory.class);

    @Override
    public List<Descriptor> getFuncDescriptors() {
        return DESCRIPTORS;
    }

    @Override
    public <IN, OUT> Func<IN, OUT> create(TreeNode spec) {
        final String funcName = spec.getData();

        switch (funcName) {
            case "LIST:AS": {
                return createListAsFunc(spec);
            }
            case "LIST:OF": {
                return createListOfFunc(spec);
            }
            case "LIST:FILTER": {
                return createListFilterFunc(spec);
            }
            case "SET:AS": {
                return createSetAsFunc(spec);
            }
            case "SET:OF": {
                return createSetOfFunc(spec);
            }
            case "SET:FILTER": {
                return createSetFilterFunc(spec);
            }
            default: {
                return null;
            }
        }
    }

    static class ListAsFunc extends AbstractFuncWithHints<Object, List<Object>> {

        @Override
        public List<Object> apply(Object source) {
            if (source instanceof List) {
                return (List<Object>) source;
            }

            if (source instanceof Collection) {
                return new ArrayList<>((Collection<Object>) source);
            }

            return null;
        }
    }

    static class ListOfFunc extends AbstractFuncWithHints<Object, List<Object>> {

        @Override
        public List<Object> apply(Object source) {
            List<Object> list = new ArrayList<>();
            list.add(source);
            return list;
        }

    }

    static final class ListFilterFunc<T> extends AbstractFuncWithHints<List<T>, List<T>> {

        private final Func<Object, Boolean> predicate;

        public ListFilterFunc(Func<Object, Boolean> predicate) {
            this.predicate = predicate;
        }

        @Override
        public List<T> apply(List<T> source) {
            if (source != null) {
                List<T> filtered = new ArrayList<>();
                source.forEach(
                        e -> {
                            if (predicate.apply(e)) {
                                filtered.add(e);
                            }
                        }
                );
                return filtered;
            } else {
                return null;
            }
        }
    }

    static class SetAsFunc extends AbstractFuncWithHints<Object, Set<Object>> {

        @Override
        public Set<Object> apply(Object source) {
            if (source instanceof Set) {
                return (Set<Object>) source;
            }

            if (source instanceof Collection) {
                return new LinkedHashSet<>((Collection<Object>) source);
            }

            return null;
        }
    }

    static class SetOfFunc extends AbstractFuncWithHints<Object, Set<Object>> {

        @Override
        public Set<Object> apply(Object source) {
            Set<Object> set = new LinkedHashSet<>();
            set.add(source);
            return set;
        }

    }

    static final class SetFilterFunc<T> extends AbstractFuncWithHints<Set<T>, Set<T>> {

        private final Func<Object, Boolean> predicate;

        public SetFilterFunc(Func<Object, Boolean> predicate) {
            this.predicate = predicate;
        }

        @Override
        public Set<T> apply(Set<T> source) {
            if (source != null) {
                Set<T> filtered = new LinkedHashSet<>();
                source.forEach(
                        e -> {
                            if (predicate.apply(e)) {
                                filtered.add(e);
                            }
                        }
                );
                return filtered;
            } else {
                return null;
            }
        }
    }

    private Func createListAsFunc(TreeNode spec) {
        return new ListAsFunc();
    }

    private Func createListOfFunc(TreeNode spec) {
        return new ListOfFunc();
    }

    private Func createListFilterFunc(TreeNode spec) {
        try {
            Func<Object, Boolean> predicate = masterFuncFactory.create(spec.getChildAt(0));
            return new ListFilterFunc(predicate);
        } catch (Exception e) {
            throw FuncHelper.createCreationException(
                    spec,
                    "LIST:FILTER(predicate)",
                    "LIST:FILTER(MAP:GET(isInventor))",
                    e
            );
        }
    }

    private Func createSetAsFunc(TreeNode spec) {
        return new SetAsFunc();
    }

    private Func createSetOfFunc(TreeNode spec) {
        return new SetOfFunc();
    }

    private Func createSetFilterFunc(TreeNode spec) {
        try {
            Func<Object, Boolean> predicate = masterFuncFactory.create(spec.getChildAt(0));
            return new SetFilterFunc(predicate);
        } catch (Exception e) {
            throw FuncHelper.createCreationException(
                    spec,
                    "LIST:FILTER(predicate)",
                    "LIST:FILTER(MAP:GET(isInventor))",
                    e
            );
        }
    }

}
