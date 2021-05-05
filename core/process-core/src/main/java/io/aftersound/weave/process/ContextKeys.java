package io.aftersound.weave.process;

import io.aftersound.weave.common.Key;
import io.aftersound.weave.component.ComponentRepository;

import java.util.Map;

/**
 * {@link Key}s of common/shared/reserved context objects
 */
public final class ContextKeys {

    /**
     * Key of common context object which is an instance of {@link ComponentRepository}
     */
    public static final Key<ComponentRepository> COMPONENT_REPOSITORY = Key.of("ComponentRepository");

    /**
     * Key of common context object which holds variables
     */
    public static final Key<Map<String, Object>> VARIABLES = Key.of("variables");

    /**
     * Key of knob which controls whether observer of processor is enabled
     */
    public static final Key<Boolean> OBSERVER_ENABLED = Key.of("observer.enabled");

    /**
     * Key of common context object which holds what's observed by observers
     */
    public static final Key<Boolean> OBSERVED = Key.of("observer.enabled");

}
