package io.aftersound.weave.service.request;

import io.aftersound.weave.component.ComponentRepository;
import io.aftersound.weave.service.ServiceContext;
import io.aftersound.weave.service.metadata.param.ParamFields;

import java.util.List;

/**
 * Conceptual entity which is responsible for parsing and validating raw service request.
 * @param <REQUEST>
 *          - generic type of service request
 */
public abstract class ParameterProcessor<REQUEST> {

    protected final ComponentRepository componentRepository;

    protected ParameterProcessor(ComponentRepository componentRepository) {
        this.componentRepository = componentRepository;
    }

    /**
     * Parse and validate raw request
     * @param request
     *          - raw request
     * @param paramFields
     *          - {@link ParamFields}, definition of parameters
     * @param context
     *          - service context
     * @return
     *          a list of parsed {@link ParamValueHolder}
     */
    protected abstract List<ParamValueHolder> process(REQUEST request, ParamFields paramFields, ServiceContext context);

}
