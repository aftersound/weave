package io.aftersound.weave.service.request;

import io.aftersound.weave.service.ServiceContext;
import io.aftersound.weave.service.metadata.param.ParamFields;

import java.util.List;

/**
 * Conceptual entity which is responsible for parsing and validating raw service request.
 * @param <REQUEST>
 *          - generic type of service request
 */
public abstract class ParameterProcessor<REQUEST> {

    protected ParamFields paramFields;

    /**
     * Set {@link ParamFields} (s) in the interests of this {@link ParameterProcessor}
     * @param paramFields
     *          - {@link ParamFields} of interest
     * @param <P>
     *          - generic type of this {@link ParameterProcessor}
     * @return
     *          this in generic type in call context
     */
    @SuppressWarnings("unchecked")
    final <P extends ParameterProcessor> P paramFields(ParamFields paramFields) {
        this.paramFields = paramFields;
        return (P) this;
    }

    /**
     * Parse and validate raw request
     * @param request
     *          - raw request
     * @param context
     *          - service context
     * @return
     *          a list of parsed {@link ParamValueHolder}
     */
    protected abstract List<ParamValueHolder> process(REQUEST request, ServiceContext context);

}
