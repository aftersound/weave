package io.aftersound.weave.actor;

import java.util.HashMap;
import java.util.Map;

public class ActorRegistry<ACTOR> {

    private final Map<String, ACTOR> actorByTypeName = new HashMap<>();

    public ActorRegistry(Map<String, ACTOR> actorByTypeName) {
        if (actorByTypeName != null) {
            this.actorByTypeName.putAll(actorByTypeName);
        }
    }

    public ACTOR get(String typeName) {
        return actorByTypeName.get(typeName);
    }

    public ACTOR get(String typeName, ACTOR defaultActor) {
        ACTOR actor = get(typeName);
        return actor != null ? actor : defaultActor;
    }

}
