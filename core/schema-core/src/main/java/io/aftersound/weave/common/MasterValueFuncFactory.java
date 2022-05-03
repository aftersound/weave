package io.aftersound.weave.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.aftersound.weave.common.valuefunc.Descriptor;
import io.aftersound.weave.utils.TextualExprTreeParser;
import io.aftersound.weave.utils.TreeNode;

import java.io.InputStream;
import java.util.*;

public final class MasterValueFuncFactory {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static Object lock = new Object();
    private static List<ValueFuncFactory> subordinates = Collections.emptyList();
    private static Map<String, Descriptor> valueFuncDescriptors = Collections.emptyMap();

    public static void init(String... valueFuncFactoryClasses) throws Exception {
        synchronized (lock) {
            final List<ValueFuncFactory> vffList = new ArrayList<>();
            final List<Descriptor> vfdList = new ArrayList<>();

            for (String valueFuncFactoryClass : valueFuncFactoryClasses) {
                Class<? extends ValueFuncFactory> cls = (Class<? extends ValueFuncFactory>)
                        MasterValueFuncFactory.class.getClassLoader().loadClass(valueFuncFactoryClass);

                // create an instance of ValueFuncFactory and include in the list
                ValueFuncFactory valueFuncFactory = cls.getDeclaredConstructor().newInstance();
                vffList.add(valueFuncFactory);

                // identify ValueFunc Descriptor(s) managed by the ValueFuncFactory
                try (InputStream is = cls.getResourceAsStream("/" + cls.getSimpleName() + ".json")) {
                    Descriptor[] descriptors = MAPPER.readValue(is, Descriptor[].class);
                    vfdList.addAll(Arrays.asList(descriptors));
                } catch (Exception e) {
                    throw new IllegalArgumentException(cls.getSimpleName() + ".json is either missing or malformed", e);
                }
            }

            subordinates = Collections.unmodifiableList(vffList);

            Collections.sort(
                    vfdList,
                    new Comparator<Descriptor>() {
                        @Override
                        public int compare(Descriptor d1, Descriptor d2) {
                            return d1.getName().compareTo(d2.getName());
                        }
                    }
            );
            Map<String, Descriptor> vfdMap = new LinkedHashMap<>(vfdList.size());
            for (Descriptor descriptor : vfdList) {
                vfdMap.put(descriptor.getName(), descriptor);
            }
            valueFuncDescriptors = Collections.unmodifiableMap(vfdMap);
        }
    }

    public static Map<String, Descriptor> getManagedValueFuncDescriptors() {
        return valueFuncDescriptors;
    }

    public static Descriptor getManagedValueFuncDescriptor(String valueFuncName) {
        return valueFuncDescriptors.get(valueFuncName);
    }

    public static <S, T> ValueFunc<S, T> create(TreeNode spec) {
        ValueFunc<?, ?> valueFunc = null;
        for (ValueFuncFactory subordinate : subordinates) {
            valueFunc = subordinate.create(spec);
            if (valueFunc != null) {
                break;
            }
        }

        if (valueFunc != null) {
            return (ValueFunc<S, T>) valueFunc;
        }

        throw new IllegalArgumentException(spec.toString() + " is not supported");
    }

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
