package io.aftersound.weave.actor;

import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.metadata.Control;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ActorFactory<CONTROL extends Control, ACTOR, PRODUCT> {

    private final ActorBindings<CONTROL, ACTOR, PRODUCT> acpBindings;

    public ActorFactory(ActorBindings<CONTROL, ACTOR, PRODUCT> acpBindings) {
        this.acpBindings = acpBindings;
    }

    /**
     * Create an instance of ACTOR which pairs with given CONTROL
     * by calling actor type's constructor which takes CONTROL
     * as single parameter
     * @param control
     *          - CONTROL for new instance of ACTOR
     * @return
     *          - an instance of ACTOR
     * @throws Exception
     *          - throws exception if any occurs
     */
    public ACTOR createActor(CONTROL control) throws Exception {
        if (control == null || control.getType() == null) {
            return null;
        }

        Class<? extends ACTOR> actorType = acpBindings.getActorType(control.getType());
        if (actorType == null) {
            return null;
        }

        Constructor<? extends ACTOR> constructor = actorType.getDeclaredConstructor(control.getClass());
        constructor.setAccessible(true);
        return constructor.newInstance(control);
    }

    /**
     * Create an instance of ACTOR which pairs with given type name
     * by calling actor type's constructor with no parameter.
     * @param typeName
     *          - nominal type name which can identify type of ACTOR
     * @return
     *          - an instance of ACTOR
     * @throws Exception
     *          - throws exception if any occurs
     */
    public ACTOR createActor(String typeName) throws Exception {
        Class<? extends ACTOR> actorType = acpBindings.getActorType(typeName);
        if (actorType == null) {
            return null;
        }

        Constructor<? extends ACTOR> constructor = actorType.getDeclaredConstructor();
        constructor.setAccessible(true);
        return constructor.newInstance();
    }

    /**
     * Create an instance of ACTOR which pairs with given type name
     * by calling actor type's constructor with no parameter. If no
     * actor type with given type name, return given nullActor
     * @param typeName
     *          - type name
     * @param nullActor
     *          - null actor if there is no actor type with given type name
     * @return
     *          - an instance ACTOR bound with given type name
     * @throws Exception
     *          - throws exception if any occurs
     */
    public ACTOR createActor(String typeName, ACTOR nullActor) throws Exception {
        ACTOR actor = createActor(typeName);
        return actor != null ? actor : nullActor;
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
    public Map<String, ACTOR> createActorsFromBindings(boolean tolerateIndividualException) throws Exception {
        Map<String, ACTOR> actorByTypeName = new HashMap<>();
        for (NamedType<CONTROL> controlType : acpBindings.controlTypes().all()) {
            try {
                ACTOR actor = createActor(controlType.name());
                actorByTypeName.put(controlType.name(), actor);
            } catch (Exception e) {
                if (!tolerateIndividualException) {
                    throw e;
                }
            }
        }
        return Collections.unmodifiableMap(actorByTypeName);
    }
}
