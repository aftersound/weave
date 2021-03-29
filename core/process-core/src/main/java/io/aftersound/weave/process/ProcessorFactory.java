package io.aftersound.weave.process;

import java.util.Map;

/**
 * A conceptual entity which serves as a factory that creates PROCESSOR
 * in according to corresponding {@link ProcessorControl}
 * @param <PROCESSOR> generic type that represents concrete implementation
 *                   of {@link Processor}
 */
public interface ProcessorFactory<PROCESSOR extends Processor> {
    /**
     * Type name of this factory. Must be identical to the type
     * name of corresponding {@link ProcessorControl#getType()}
     * @return type name
     */
    String getType();

    /**
     * create a PROCESSOR in according to given config group and config
     * @param configGroup config group name
     * @param config source which contains config for target PROCESSOR
     * @return an instance of PROCESSOR
     */
    PROCESSOR create(String configGroup, Map<String, String> config);
}
