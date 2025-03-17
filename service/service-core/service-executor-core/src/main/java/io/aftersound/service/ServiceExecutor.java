package io.aftersound.service;

import io.aftersound.msg.Message;
import io.aftersound.common.NamedType;
import io.aftersound.component.ComponentRepository;
import io.aftersound.dependency.Declaration;
import io.aftersound.service.metadata.ExecutionControl;
import io.aftersound.service.metadata.ServiceMetadata;
import io.aftersound.service.request.ParamValueHolders;

/**
 * Conceptual entity, which executes/serves request in according to {@link ExecutionControl} in
 * {@link ServiceMetadata}.
 *
 * A concrete implementation of {@link ServiceExecutor} is expected to
 *      1.have a public static final field COMPANION_CONTROL_TYPE of {@link NamedType} of {@link ExecutionControl}
 *      2.have a public static final field DEPENDENCY_DECLARATION of {@link Declaration}, if there is
 *        any required component before any instance could serve requests.
 * @param <RESPONSE>
 *          -response in generic type
 */
public abstract class ServiceExecutor<RESPONSE> {

    public static class Info {

        private final String controlType;
        private final String serviceExecutorType;

        Info(String controlType, String serviceExecutorType) {
            this.controlType = controlType;
            this.serviceExecutorType = serviceExecutorType;
        }

        public String getControlType() {
            return controlType;
        }

        public String getServiceExecutorType() {
            return serviceExecutorType;
        }
    }

    /**
     * An instance of {@link ComponentRepository} which contains components shared across requests.
     */
    protected final ComponentRepository componentRepository;

    protected ServiceExecutor(ComponentRepository componentRepository) {
        this.componentRepository = componentRepository;
    }

    /**
     * Get nominal type name of this {@link ServiceExecutor}, which must be identical
     * to type name of pairing {@link ExecutionControl}
     * @return nominal type name
     */
    public abstract String getType();

    /**
     * @return
     *          basic information of this ServiceExecutor
     */
    public final Info getInfo() {
        return new Info(getType(), this.getClass().getName());
    }

    /**
     * Serves request in according to {@link ExecutionControl}
     * in given {@link ServiceMetadata}.
     * Note:
     * 1.Concrete implementation must not throw out any {@link Exception}, including {@link RuntimeException}.
     * 2.Concrete implementation is recommended to map exception into
     *   {@link Message} at error or warning level if there is need
     *   to give client some level of visibility.
     *
     * @param executionControl - {@link ExecutionControl} that controls behavior of this {@link ServiceExecutor}
     * @param request          - a {@link ParamValueHolders} which contains information parsed from raw service request by framework
     * @param context          - context which allows messages, diagnostics, etc.
     * @return a response, type of which is managed by actual implementation
     */
    public abstract RESPONSE execute(ExecutionControl executionControl, ParamValueHolders request, ServiceContext context);
}
