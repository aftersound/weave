package io.aftersound.weave.actor;

import io.aftersound.weave.common.NamedType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Util which handles bindings of {CONTROL, ACTOR, PRODUCT}, which are attached on ACTOR types
 */
@SuppressWarnings("unchecked")
public final class ActorBindingsUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActorBindingsUtil.class);

    /**
     * Load actor types and register bindings of control, actor and product
     * @param actorClassNameList
     *          a list of class names expected to be extended classes of ACTOR
     * @param baseControlType
     *          base type for CONTROL
     * @param baseProductType
     *          base type for PRODUCT
     * @param tolerateIndividualException
     *          indicator whether to tolerate exception at individual class level
     * @param <CONTROL>
     *          conceptual control that affects behavior/product of conceptual ACTOR
     * @param <ACTOR>
     *          conceptual actor which acts in according to CONTROL
     * @param <PRODUCT>
     *          conceptual product which ACTOR produces
     * @return
     *          bindings for conceptual CONTROL, ACTOR and PRODUCT
     * @throws Exception
     *          any exception during loading and registering
     */
    public static <CONTROL, ACTOR, PRODUCT> ActorBindings<CONTROL, ACTOR, PRODUCT> loadActorBindings(
            List<String> actorClassNameList,
            Class<CONTROL> baseControlType,
            Class<PRODUCT> baseProductType,
            boolean tolerateIndividualException) throws Exception {
        ActorBindings<CONTROL, ACTOR, PRODUCT> bindings = new ActorBindings<>();

        if (actorClassNameList == null || actorClassNameList.isEmpty()) {
            return bindings;
        }

        Set<Class<? extends ACTOR>> actorClasses = new LinkedHashSet<>();
        for (String className: actorClassNameList) {
            try {
                Class<? extends ACTOR> actorClass = (Class<? extends ACTOR>) Class.forName(className);
                actorClasses.add(actorClass);
            } catch (Exception e) {
                if (tolerateIndividualException) {
                    LOGGER.error(
                            "TOLERABLE exception occurred when attempting to load class {}",
                            className,
                            e
                    );
                } else {
                    LOGGER.error("Exception occurred when attempting to load class {}", className, e);
                    throw e;
                }
            }
        }

        for (Class<? extends ACTOR> actorClass : actorClasses) {
            try {
                NamedType<CONTROL> controlType = extractControlType(actorClass, baseControlType);
                NamedType<PRODUCT> productType = extractProductType(actorClass, baseProductType);

                /*
                 * Intentionally ignore actor types with subordinate control type.
                 * Such an actor type relies on manager actor type to do it duty, while using
                 * class loading to hand the details of duty to manager actor type
                 */
                if (!controlType.isSubordinate()) {
                    bindings.register(controlType, actorClass, productType);
                }
            } catch (Exception e) {
                if (tolerateIndividualException) {
                    LOGGER.error(
                            "{}: TOLERABLE exception occurred when attempting to register actor type {}",
                            e,
                            actorClass.getName()
                    );
                } else {
                    throw e;
                }
            }
        }

        return bindings;
    }

    private static <CONTROL> NamedType<CONTROL> extractControlType(
            Class<?> actorClass,
            Class<CONTROL> baseControlType) throws Exception {
        return NamedTypeUtil.extractNamedType(actorClass, "COMPANION_CONTROL_TYPE", baseControlType);
    }

    private static <PRODUCT> NamedType<PRODUCT> extractProductType(
            Class<?> actorClass,
            Class<PRODUCT> baseProductType) {
        try {
            return NamedTypeUtil.extractNamedType(actorClass, "COMPANION_PRODUCT_TYPE", baseProductType);
        } catch (Exception e) {
            return null;
        }
    }
}
