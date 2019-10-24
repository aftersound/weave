package io.aftersound.weave.actor;

import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.common.NamedTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Bindings of control, actor and product, where actor is the center
 * @param <CONTROL>
 *          - generic form of control, which affects behavior of actor
 * @param <ACTOR>
 *          - generic form of actor, which acts upon control and produces product
 * @param <PRODUCT>
 *          - generic form of product, which is produced by actor
 */
public class ActorBindings<CONTROL, ACTOR, PRODUCT> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActorBindings.class);

    private final NamedTypes<CONTROL> controlTypes = new NamedTypes<>();
    private final Map<String, Class<? extends ACTOR>> actorTypeByControlName = new HashMap<>();
    private final NamedTypes<PRODUCT> productTypes = new NamedTypes<>();

    private final Map<Class<?>, Class<? extends ACTOR>> actorTypeByProductType = new IdentityHashMap<>();
    private final Map<String, Class<?>> productTypeByName = new HashMap<>();
    private final Map<Class<?>, String> nameByOutputType = new IdentityHashMap<>();

    /**
     * Register the binding among CONTROL, ACTOR and PRODUCT
     * @param controlType
     *          named type of CONTROL
     * @param actorType
     *          class of ACTOR
     * @param productType
     *          type of product that ACTOR produces with CONTROL, optional
     */
    public void register(NamedType<CONTROL> controlType, Class<? extends ACTOR> actorType, NamedType<PRODUCT> productType) {
        if (controlType == null || actorType == null) {
            throw new IllegalArgumentException("neither controlType nor actorType could be null");
        }

        if (productType != null && !controlType.name().equals(productType.name())) {
            throw new IllegalArgumentException("controlType.name() and productType.name() must be same");
        }

        NamedType<CONTROL> existingControlType = controlTypes.get(controlType.name());
        Class<? extends ACTOR> existingActorType = actorTypeByControlName.get(controlType.name());

        if (existingControlType == null && existingActorType == null) {
            controlTypes.include(controlType);
            actorTypeByControlName.put(controlType.name(), actorType);

            if (productType != null) {
                productTypes.include(productType);
                actorTypeByProductType.put(productType.getClass(), actorType);
                productTypeByName.put(productType.name(), productType.type());
                nameByOutputType.put(productType.type(), productType.name());
            }

            String info = new StringBuilder()
                    .append("[")
                    .append(actorType.getName())
                    .append("] is registered/bound with ")
                    .append(controlType)
                    .append("]").toString();
            LOGGER.info(info);
            return;
        }

        if (controlType.equals(existingControlType) && actorType.equals(existingActorType)) {
            String info = new StringBuilder()
                    .append("[")
                    .append(actorType.getName())
                    .append("] is already registered/bound with ")
                    .append(controlType)
                    .append("]").toString();
            LOGGER.info(info);
            return;
        }

        String message = new StringBuilder()
                .append("[")
                .append(existingActorType.getName())
                .append("] is already registered/bound with ")
                .append(existingControlType)
                .append("]").toString();

        throw new RuntimeException(message);
    }

    /**
     * @return
     *          CONTROL types bound with ACTOR types in form of {@link NamedTypes}
     */
    public NamedTypes<CONTROL> controlTypes() {
        return controlTypes.readOnly();
    }

    /**
     * @param typeName
     *          - nominal type name
     * @return
     *          a class of ACTOR with given nominal type name
     */
    public Class<? extends ACTOR> getActorType(String typeName) {
        return actorTypeByControlName.get(typeName);
    }

    /**
     * @return
     *          all ACTOR classes managed in this bindings
     */
    public Collection<Class<? extends ACTOR>> actorTypes() {
        return actorTypeByControlName.values();
    }

    /**
     * @return
     *          all PRODUCT types in form of {@link NamedTypes}
     */
    public NamedTypes<PRODUCT> productTypes() {
        return productTypes.readOnly();
    }

    /**
     * @param productType
     *          - PRODUCT type
     * @return
     *          a class of ACTOR which is bound with given PRODUCT type, not necessarily 1:1 mapping
     */
    public Class<? extends ACTOR> getActorTypeByProductType(Class<?> productType) {
        return actorTypeByProductType.get(productType);
    }

    /**
     * @param name
     *          - nominal name of PRODUCT type
     * @return
     *          a class of PRODUCT, which is bound with given nominal name
     */
    @SuppressWarnings("unchecked")
    public Class<PRODUCT> getProductTypeByName(String name) {
        return (Class<PRODUCT>) productTypeByName.get(name);
    }

}
