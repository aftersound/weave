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
     * Loads actor classes and registers bindings among control types, actor classes,
     * and product types. The method allows for optional tolerance of exceptions
     * during the individual registration of actor classes.
     *
     * @param <CONTROL>                   - the generic type representing the conceptual control that affects the behavior/product of the actor
     * @param <ACTOR>                     - the generic type representing the conceptual actor that acts according to control
     * @param <PRODUCT>                   - the generic type representing the conceptual product produced by the actor
     * @param actorClasses                - the set of actor classes to be loaded and registered; must extend the ACTOR type
     * @param baseControlType             - the base type representing the control type
     * @param baseProductType             - the base type representing the product type
     * @param tolerateIndividualException - indicates whether to tolerate individual exceptions during actor registration
     * @return a constructed and populated {@link ActorBindings} instance containing the registered bindings
     * @throws Exception if an error occurs during the loading and registration process and exceptions are not tolerated
     */
    public static <CONTROL, ACTOR, PRODUCT> ActorBindings<CONTROL, ACTOR, PRODUCT> loadActorBindings(
            Set<Class<? extends ACTOR>> actorClasses,
            Class<CONTROL> baseControlType,
            Class<PRODUCT> baseProductType,
            boolean tolerateIndividualException) throws Exception {
        ActorBindings<CONTROL, ACTOR, PRODUCT> bindings = new ActorBindings<>();

        if (actorClasses == null || actorClasses.isEmpty()) {
            return bindings;
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

    /**
     * Loads actor classes and registers bindings among control types, actor classes, and product types.
     * This method supports optional toleration of exceptions during the individual registration of actor classes.
     *
     * @param <CONTROL>                   - the generic type representing the conceptual control that affects the behavior/product of the actor
     * @param <ACTOR>                     - the generic type representing the conceptual actor that acts according to control
     * @param <PRODUCT>                   - the generic type representing the conceptual product produced by the actor
     * @param actorClassNameList          - the list of fully qualified class names representing actor classes to be loaded
     * @param baseControlType             - the base type representing the control type
     * @param baseProductType             - the base type representing the product type
     * @param tolerateIndividualException - indicates whether to tolerate individual exceptions during actor class loading
     * @return a constructed and populated ActorBindings instance containing the registered bindings
     * @throws Exception if an error occurs during the loading and registration process and exceptions are not tolerated
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

        return loadActorBindings(actorClasses, baseControlType, baseProductType, tolerateIndividualException);
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
