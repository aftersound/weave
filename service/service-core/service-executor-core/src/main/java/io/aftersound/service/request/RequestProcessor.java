package io.aftersound.service.request;

import io.aftersound.service.ServiceContext;
import io.aftersound.service.metadata.ServiceMetadata;
import io.aftersound.service.metadata.param.ParamField;
import io.aftersound.service.metadata.param.ParamFields;

import java.util.Collections;
import java.util.List;

/**
 * Main processor of raw request, which depends on subordinate of {@link ParameterProcessor}
 * to do its job, to parse and validate raw requests, in according to {@link ParamFields} defined
 * in given {@link ServiceMetadata}
 *
 * @param <REQUEST>
 *          - generic type for raw request
 */
public final class RequestProcessor<REQUEST> {

    private final ParameterProcessor<REQUEST> subordinate;

    public RequestProcessor(ParameterProcessor<REQUEST> subordinateProcessor) {
        this.subordinate = subordinateProcessor;
    }

    /**
     * Process raw request with help from subordinates
     * @param request
     *          - raw request
     * @param paramFields
     *          - definition of parameter fields
     * @param context
     *          - service context
     * @return
     *          parsed parameter values in wrapper {@link ParamValueHolders}
     */
    public ParamValueHolders process(REQUEST request, List<ParamField> paramFields, ServiceContext context) {
        List<ParamValueHolder> paramValueHolderList;
        if (subordinate != null) {
            paramValueHolderList = subordinate.process(request, ParamFields.from(paramFields), context);
        } else {
            paramValueHolderList = Collections.emptyList();
        }
        return ParamValueHolders.from(paramValueHolderList);
    }

}
