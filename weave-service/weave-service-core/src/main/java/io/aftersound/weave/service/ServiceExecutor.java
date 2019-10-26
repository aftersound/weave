package io.aftersound.weave.service;

import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.service.metadata.ExecutionControl;
import io.aftersound.weave.service.metadata.ServiceMetadata;
import io.aftersound.weave.service.request.ParamValueHolders;
import io.aftersound.weave.resources.ManagedResources;
import io.aftersound.weave.resources.ResourceInitializer;

/**
 * Conceptual entity, which executes/serves request in according to {@link ExecutionControl} in
 * {@link ServiceMetadata}.
 *
 * A concrete implementation of {@link ServiceExecutor} is expected to
 *      1.have a public static final field COMPANION_CONTROL_TYPE of {@link NamedType< ExecutionControl >}
 *      2.have a public static final field RESOURCE_INITIALIZER of {@link ResourceInitializer}, if there is
 *        any required resource needs to be initialized before any instance could serve requests.
 */
public abstract class ServiceExecutor {

    /**
     * An instance of {@link ManagedResources} which contains resources shared across requests.
     */
    protected final ManagedResources managedResources;

    protected ServiceExecutor(ManagedResources managedResources) {
        this.managedResources = managedResources;
    }

    /**
     * Get nominal type name of this {@link ServiceExecutor}, which must be identical
     * to type name of pairing {@link ExecutionControl}
     * @return
     *          nominal type name
     */
    public abstract String getType();

    /**
     * Serves request in according to {@link ExecutionControl}
     * in given {@link ServiceMetadata}.
     * Note:
     *   1.Concrete implementation must not throw out any {@link Exception}, including {@link RuntimeException}.
     *   2.Concrete implementation is recommended to map exception into
     *     {@link io.aftersound.weave.service.message.MessageData} at error or warning level if there is need
     *     to give client some level of visibility.
     *
     * @param serviceMetadata
     *          - service metadata which contains {@link ExecutionControl} that controls behavior of this
     *          {@link ServiceExecutor}
     * @param request
     *          - a {@link ParamValueHolders} which contains information parsed from raw service request by framework
     * @param context
     *          - context which allows messages, diagnostics, etc.
     * @return
     *          a response, type of which is managed by actual implementation
     */
    public abstract Object execute(ServiceMetadata serviceMetadata, ParamValueHolders request, ServiceContext context);
}
