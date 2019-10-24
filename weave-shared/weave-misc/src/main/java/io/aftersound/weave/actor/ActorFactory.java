package io.aftersound.weave.actor;

import io.aftersound.weave.metadata.Control;

import java.lang.reflect.Constructor;

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
     * by calling actor type's constructor which no parameter
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

    public ACTOR createActor(String typeName, ACTOR nullActor) throws Exception {
        ACTOR actor = createActor(typeName);
        return actor != null ? actor : nullActor;
    }
}
