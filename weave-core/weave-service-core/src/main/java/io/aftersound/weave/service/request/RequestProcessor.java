package io.aftersound.weave.service.request;

import io.aftersound.weave.service.ServiceContext;
import io.aftersound.weave.service.metadata.ServiceMetadata;
import io.aftersound.weave.service.metadata.param.ParamFields;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Main processor of raw request, which depends on subordinates of {@link ParameterProcessor}
 * to do its job, to parse and validate raw requests, in according to {@link ParamFields} defined
 * in given {@link ServiceMetadata}
 *
 * @param <REQUEST>
 *          - generic type for raw request
 */
public final class RequestProcessor<REQUEST> {

    private final ParamFields paramFields;
    private final List<ParameterProcessor<REQUEST>> subordinateProcessors;

    public RequestProcessor(ServiceMetadata insightMetadata, ParameterProcessor<REQUEST>... subordinateProcessors) {
        this.paramFields = ParamFields.from(insightMetadata.getParamFields());
        if (subordinateProcessors != null) {
            this.subordinateProcessors = Arrays.asList(subordinateProcessors);
        } else {
            this.subordinateProcessors = Collections.emptyList();
        }
    }

    /**
     * Process raw request with help from subordinates
     * @param request
     *          - raw request
     * @param context
     *          - service context
     * @return
     *          parsed parameter values in wrapper {@link ParamValueHolders}
     */
    public ParamValueHolders process(REQUEST request, ServiceContext context) {
        List<ParamValueHolder> paramValueHolderList = new ArrayList<>();
        for (ParameterProcessor subordinateProcessor : subordinateProcessors) {
            List<ParamValueHolder> paramValueHolders = subordinateProcessor.paramFields(paramFields)
                    .process(request, context);
            paramValueHolderList.addAll(paramValueHolders);
        }
        return ParamValueHolders.from(paramValueHolderList);
    }

}
