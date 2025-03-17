package io.aftersound.process;

/**
 * A conceptual entity which serves as a factory that creates PROCESSOR
 * in according to corresponding {@link ProcessorControl}
 * @param <PROCESSOR> generic type that represents concrete implementation
 *                   of {@link Processor}
 * @param <CONFIG> generic type that represents configuration for target
 *                {@link Processor}
 */
public interface ProcessorFactory<PROCESSOR extends Processor, CONFIG> {
    /**
     * Type name of this factory. Must be identical to the type
     * name of corresponding {@link ProcessorControl#getType()}
     * @return type name
     */
    String getType();

    /**
     * @return class of configuration for the corresponding {@link Processor} implementation
     */
    Class<CONFIG> getConfigType();

    /**
     * create a PROCESSOR with provided config
     * @param config configuration for target PROCESSOR
     * @return an instance of PROCESSOR
     */
    PROCESSOR create(CONFIG config);
}
