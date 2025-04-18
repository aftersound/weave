package io.aftersound.func;

import io.aftersound.dict.Dictionary;
import io.aftersound.dict.SimpleDictionary;
import io.aftersound.util.Handle;
import io.aftersound.util.HintHolder;
import io.aftersound.util.TreeNode;
import io.aftersound.util.WithHints;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The master func factory which creates {@link Func} based on given directive
 */
public final class MasterFuncFactory implements FuncFactory {

    private final List<MasterAwareFuncFactory> subordinates;

    private final Dictionary<Descriptor> descriptors;

    public MasterFuncFactory(List<MasterAwareFuncFactory> subordinates) {
        for (MasterAwareFuncFactory subordinate : subordinates) {
            subordinate.setMasterFuncFactory(this);
        }
        this.subordinates = Collections.unmodifiableList(subordinates);

        List<Descriptor> descriptors = new ArrayList<>();
        subordinates.forEach(funcFactory -> descriptors.addAll(funcFactory.getFuncDescriptors()));
        descriptors.sort(Comparator.comparing(Descriptor::getName));
        this.descriptors = SimpleDictionary.<Descriptor>builder()
                .withEntries(descriptors)
                .withEntryNameFunc(Descriptor::getName)
                .build();
    }

    public MasterFuncFactory(MasterAwareFuncFactory... subordinates) {
        this(List.of(subordinates));
    }

    /**
     * Creates a new instance of {@code MasterFuncFactory} initialized with the specified
     * classes of {@code MasterAwareFuncFactory} as subordinates.
     *
     * @param funcFactoryClasses - the fully qualified class names of {@code MasterAwareFuncFactory}
     *                           implementations to be included as subordinates
     * @return a new instance of {@code MasterFuncFactory} with the specified subordinates
     * @throws Exception if an error occurs while instantiating any class of {@code MasterAwareFuncFactory}
     */
    @SuppressWarnings("unchecked")
    public static MasterFuncFactory of(String... funcFactoryClasses) throws Exception {
        final List<Class<? extends MasterAwareFuncFactory>> funcFactoryClassList = new ArrayList<>();
        for (String funcFactoryClass : funcFactoryClasses) {
            funcFactoryClassList.add((Class<? extends MasterAwareFuncFactory>) Class.forName(funcFactoryClass));
        }
        return of(funcFactoryClassList);
    }

    /**
     * Creates a new instance of {@code MasterFuncFactory} with the specified classes of
     * {@code MasterAwareFuncFactory} implementations as subordinates.
     *
     * @param funcFactoryClasses - the classes of {@code MasterAwareFuncFactory} implementations
     *                           used to create the subordinates of the {@code MasterFuncFactory}
     * @return a new instance of {@code MasterFuncFactory} with the specified subordinates
     * @throws Exception if an error occurs during the creation of an instance of any
     *                   {@code MasterAwareFuncFactory} implementation
     */
    public static MasterFuncFactory of(List<Class<? extends MasterAwareFuncFactory>> funcFactoryClasses) throws Exception {
        final List<MasterAwareFuncFactory> funcFactories = new ArrayList<>();
        for (Class<? extends MasterAwareFuncFactory> funcFactoryClass : funcFactoryClasses) {
            // create an instance of MasterAwareFuncFactory and include in the list
            MasterAwareFuncFactory funcFactory = funcFactoryClass.getDeclaredConstructor().newInstance();
            funcFactories.add(funcFactory);
        }
        return new MasterFuncFactory(funcFactories);
    }

    public static void bindInstance(String id, MasterFuncFactory funcFactory) {
        Handle.of(id, MasterFuncFactory.class).setAndLock(funcFactory);
    }

    public static void bindInstance(MasterFuncFactory funcFactory) {
        bindInstance(MasterFuncFactory.class.getSimpleName(), funcFactory);
    }

    public static MasterFuncFactory instance(String id) {
        return Handle.of(id, MasterFuncFactory.class).get();
    }

    public static MasterFuncFactory instance() {
        return Handle.of(MasterFuncFactory.class.getSimpleName(), MasterFuncFactory.class).get();
    }

    /**
     * Create {@link Func} which conforms to given directive in form of {@link TreeNode}
     *
     * @param directive - the directive for the function to be created
     * @param <IN>      - the type of input to the function to be created
     * @param <OUT>     - the type of output of the function to be created
     * @return a {@link Func} which conforms to given directive
     */
    @SuppressWarnings("unchecked")
    @Override
    public <IN, OUT> Func<IN, OUT> create(TreeNode directive) {
        Func<?, ?> func = null;
        for (FuncFactory subordinate : subordinates) {
            func = subordinate.create(directive);
            if (func != null) {
                break;
            }
        }

        // include hints when applicable
        if (func instanceof WithHints) {
            HintHolder hintHolder = (HintHolder) func;
            hintHolder.addAll(directive.getAttributes());
            hintHolder.lockHints();
        }

        if (func != null) {
            return (Func<IN, OUT>) func;
        }

        throw new CreationException(directive + " is not supported");
    }

    /**
     * Get the {@link Descriptor} of target {@link Func} with specified name
     *
     * @param funcName - the name of target {@link Func}
     * @return the {@link Descriptor} of target {@link Func} with specified name
     */
    public Descriptor getFuncDescriptor(String funcName) {
        return descriptors.first(
                d -> d.getName().equals(funcName) || (d.getAliases() != null && d.getAliases().contains(funcName))
        );
    }

    /**
     * Get the {@link Dictionary} of all supported functions' descriptors
     *
     * @return the {@link Dictionary} of all supported functions' descriptors
     */
    public Dictionary<Descriptor> funcDescriptors() {
        return descriptors;
    }

    /**
     * Get all the {@link Descriptor}s of all supported {@link Func}s
     *
     * @return all the {@link Descriptor}s of all supported {@link Func}s
     */
    @Override
    public List<Descriptor> getFuncDescriptors() {
        return descriptors.all();
    }

    public List<String> getSubordinates() {
        return subordinates.stream()
                .map(s -> s.getClass().getName())
                .collect(Collectors.toList());
    }

}
