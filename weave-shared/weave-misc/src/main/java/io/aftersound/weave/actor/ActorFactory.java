package io.aftersound.weave.actor;

import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.metadata.Control;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ActorFactory<CONTROL extends Control, ACTOR, PRODUCT> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActorFactory.class);

    private final ActorBindings<CONTROL, ACTOR, PRODUCT> acpBindings;

    public ActorFactory(ActorBindings<CONTROL, ACTOR, PRODUCT> acpBindings) {
        this.acpBindings = acpBindings;
    }

    /**
     * Create an {@link ActorRegistry} for all ACTOR types available in actor bindings
     * @param tolerateIndividualException
     *          - whether to tolerate exception when creating actor at individual level
     * @return
     *          - an unmodifiable map of { type, ACTOR}
     * @throws Exception
     *          - throw exception if any occurs and it is not tolerable
     */
    public ActorRegistry<ACTOR> createActorRegistryFromBindings(boolean tolerateIndividualException) throws Exception {
        return new ActorRegistry<>(createActorsFromBindings(tolerateIndividualException));
    }

    /**
     * Create ACTOR (s) for all ACTOR types available in actor bindings.
     * @param tolerateIndividualException
     *          - whether to tolerate exception when creating actor at individual level
     * @return
     *          - an unmodifiable map of { type, ACTOR}
     * @throws Exception
     *          - throw exception if any occurs and it is not tolerable
     */
    private Map<String, ACTOR> createActorsFromBindings(boolean tolerateIndividualException) throws Exception {
        Map<String, ACTOR> actorByTypeName = new HashMap<>();
        for (NamedType<CONTROL> controlType : acpBindings.controlTypes().all()) {
            Class<? extends ACTOR> actorType = acpBindings.getActorType(controlType.name());
            if (actorType == null) {
                continue;
            }

            try {
                Constructor<? extends ACTOR> constructor = actorType.getDeclaredConstructor();
                constructor.setAccessible(true);
                ACTOR actor = constructor.newInstance();
                actorByTypeName.put(controlType.name(), actor);
            } catch (Exception e) {
                LOGGER.error(
                        "{} occurred when trying to create {} bound with control type {}",
                        e,
                        actorType.getSimpleName(),
                        controlType.name()
                );
                if (!tolerateIndividualException) {
                    throw e;
                }
            }
        }
        return Collections.unmodifiableMap(actorByTypeName);
    }
}
