package io.aftersound.func;

import io.aftersound.util.HintHolder;
import io.aftersound.util.TreeNode;
import io.aftersound.util.WithHints;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The master func factory which creates {@link Func} based on given directive
 */
public final class MasterFuncFactory implements FuncFactory {

    private final List<FuncFactory> subordinates;

    private final Descriptors descriptors;

    public MasterFuncFactory(List<FuncFactory> subordinates) {
        this.subordinates = Collections.unmodifiableList(subordinates);

        List<Descriptor> descriptors = new ArrayList<>();
        subordinates.forEach(funcFactory -> descriptors.addAll(funcFactory.getFuncDescriptors()));
        this.descriptors = new Descriptors(descriptors);
    }

    public MasterFuncFactory(FuncFactory... subordinates) {
        this(List.of(subordinates));
    }

    /**
     * Create a MasterFuncFactory which has subordinates of given implementations
     *
     * @param funcFactoryClasses - the classes of {@link FuncFactory} implementations
     * @return a MasterFuncFactory which has subordinates of given implementations
     * @throws Exception - any exception that causes the creation failure
     */
    @SuppressWarnings("unchecked")
    public static MasterFuncFactory of(String... funcFactoryClasses) throws Exception {
        final List<FuncFactory> funcFactories = new ArrayList<>();
        for (String funcFactoryClass : funcFactoryClasses) {
            Class<? extends FuncFactory> cls = (Class<? extends FuncFactory>) Class.forName(funcFactoryClass);

            // create an instance of FuncFactory and include in the list
            FuncFactory funcFactory = cls.getDeclaredConstructor().newInstance();
            funcFactories.add(funcFactory);
        }
        return new MasterFuncFactory(funcFactories);
    }

    /**
     * Get the {@link Descriptor} of target {@link Func} with specified name
     *
     * @param funcName - the name of target {@link Func}
     * @return the {@link Descriptor} of target {@link Func} with specified name
     */
    public Descriptor getFuncDescriptor(String funcName) {
        return descriptors.byName(funcName);
    }

    /**
     * GEt the {@link Descriptors} of all supported {@link Func}s
     *
     * @return the {@link Descriptors} of all supported {@link Func}s
     */
    public Descriptors funcDescriptors() {
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

}
