package io.aftersound.weave.process;

import io.aftersound.weave.common.Key;
import io.aftersound.weave.component.ComponentRepository;

import java.util.Map;

/**
 * {@link Key}s of common/shared/reserved context objects
 */
public final class ContextKeys {

    /**
     * Key of common context object which holds variables
     */
    public static final Key<Map<String, Object>> VARIABLES = Key.of("Variables");

    /**
     * Key of common context object which is an instance of {@link ComponentRepository}
     */
    public static final Key<ComponentRepository> COMPONENT_REPOSITORY = Key.of("ComponentRepository");

}
