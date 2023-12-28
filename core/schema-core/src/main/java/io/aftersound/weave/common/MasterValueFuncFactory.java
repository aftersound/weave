package io.aftersound.weave.common;

import io.aftersound.weave.common.valuefunc.Descriptor;
import io.aftersound.weave.utils.TextualExprTreeParser;
import io.aftersound.weave.utils.TreeNode;

import java.util.*;

/**
 * The master value func factory which creates {@link ValueFunc}
 * based on given specification
 */
public final class MasterValueFuncFactory {

    private static Object lock = new Object();
    private static List<ValueFuncFactory> subordinates = Collections.emptyList();
    private static Map<String, Descriptor> valueFuncDescriptorByName = Collections.emptyMap();
    private static Map<String, Descriptor> valueFuncDescriptorByAlias = Collections.emptyMap();

    /**
     * Instantiate given classes of {@link ValueFuncFactory} and leverage these instances as subordinates
     *
     * @param valueFuncFactoryClasses full qualified names of {@link ValueFuncFactory} implementation classes
     * @throws Exception exception needs attention from caller
     */
    public static void init(String... valueFuncFactoryClasses) throws Exception {
        synchronized (lock) {
            final List<ValueFuncFactory> vffList = new ArrayList<>();
            final List<Descriptor> vfdList = new ArrayList<>();

            for (String valueFuncFactoryClass : valueFuncFactoryClasses) {
                Class<? extends ValueFuncFactory> cls = (Class<? extends ValueFuncFactory>) Class.forName(valueFuncFactoryClass);

                // create an instance of ValueFuncFactory and include in the list
                ValueFuncFactory valueFuncFactory = cls.getDeclaredConstructor().newInstance();
                vffList.add(valueFuncFactory);

                // identify ValueFunc Descriptor(s) managed by the ValueFuncFactory
                vfdList.addAll(valueFuncFactory.getValueFuncDescriptors());
            }

            subordinates = Collections.unmodifiableList(vffList);

            Collections.sort(
                    vfdList,
                    Comparator.comparing(Descriptor::getName)
            );
            Map<String, Descriptor> vfdByName = new LinkedHashMap<>(vfdList.size());
            Map<String, Descriptor> vfdByAlias = new LinkedHashMap<>(vfdList.size());
            for (Descriptor descriptor : vfdList) {
                vfdByName.put(descriptor.getName(), descriptor);

                List<String> aliases = descriptor.getAliases();
                if (aliases != null) {
                    for (String alias : aliases) {
                        vfdByAlias.put(alias, descriptor);
                    }
                }
            }
            valueFuncDescriptorByName = Collections.unmodifiableMap(vfdByName);
            valueFuncDescriptorByAlias = Collections.unmodifiableMap(vfdByAlias);
        }
    }

    /**
     * Get all the {@link Descriptor}s of {@link ValueFunc}s currently under awareness
     *
     * @return Get all the {@link Descriptor}s of {@link ValueFunc}s currently under awareness
     */
    public static Map<String, Descriptor> getManagedValueFuncDescriptors() {
        return valueFuncDescriptorByName;
    }

    /**
     * Get the {@link Descriptor} of target {@link ValueFunc} with specified name
     *
     * @param valueFuncName the name of target {@link ValueFunc}
     * @return the {@link Descriptor} of target {@link ValueFunc} with specified name
     */
    public static Descriptor getManagedValueFuncDescriptor(String valueFuncName) {
        Descriptor descriptor = valueFuncDescriptorByName.get(valueFuncName);
        if (descriptor != null) {
            return descriptor;
        }

        return valueFuncDescriptorByAlias.get(valueFuncName);
    }

    /**
     * Create {@link ValueFunc} which conforms to given spec in form of {@link TreeNode}
     *
     * @param spec the specification for desired {@link ValueFunc}
     * @param <S>  the type of input accepted by desired {@link ValueFunc} in generic form
     * @param <T>  the type of output produced by desired {@link ValueFunc} in generic form
     * @return a {@link ValueFunc} which conforms to given spec
     */
    public static <S, T> ValueFunc<S, T> create(TreeNode spec) {
        ValueFunc<?, ?> valueFunc = null;
        for (ValueFuncFactory subordinate : subordinates) {
            valueFunc = subordinate.create(spec);
            if (valueFunc != null) {
                break;
            }
        }

        if (valueFunc != null) {
            valueFunc.setHints(spec.getAttributes());
            return (ValueFunc<S, T>) valueFunc;
        }

        throw new ValueFuncException(spec.toString() + " is not supported");
    }

    /**
     * Create {@link ValueFunc} which conforms to given spec in form of string
     *
     * @param spec the specification for desired {@link ValueFunc}
     * @param <S>  the type of input accepted by desired {@link ValueFunc} in generic form
     * @param <T>  the type of output produced by desired {@link ValueFunc} in generic form
     * @return a {@link ValueFunc} which conforms to given spec
     */
    public static <S, T> ValueFunc<S, T> create(String spec) {
        TreeNode specAsTreeNode;
        try {
            specAsTreeNode = TextualExprTreeParser.parse(spec);
        } catch (Exception e) {
            throw new IllegalArgumentException(spec + " is not valid");
        }

        return create(specAsTreeNode);
    }

}
