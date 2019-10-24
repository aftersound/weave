package io.aftersound.weave.service.couchbase;

import io.aftersound.weave.service.message.MessageData;

import static io.aftersound.weave.service.message.MessageData.serviceError;

class Errors {

    public static final MessageData EXECUTION_CONTROL_UNEXPECTED = serviceError(1001, "ServiceMetadata.executionControl is either missing or unexpected");
    public static final MessageData EXECUTION_CONTROL_MALFORMED = serviceError(1002, "ServiceMetadata.executionControl is malformed");
    public static final MessageData REPOSITORY_MISSING = serviceError(1003, "ServiceMetadata.executionControl.repository is missing");

    public static final MessageData MALFORMED_BY_KEY = serviceError(1011, "ServiceMetadata.executionControl.byKey is malformed");

    public static final MessageData VIEW_QUERY_CONTROL_MISSING = serviceError(1021, "ServiceMetadata.executionControl.repository.viewQueryControl is missing");
    public static final MessageData VIEW_QUERY_TEMPLATE_MISSING = serviceError(1022, "ServiceMetadata.executionControl.byViewQuery.template is missing");
    public static final MessageData VIEW_QUERY_NOT_RESOLVABLE = serviceError(1023, "ServiceMetadata.executionControl.byViewQuery.template cannot be resolved as ViewQuery");

    public static final MessageData MALFORMED_BY_N1QL = serviceError(1031, "ServiceMetadata.executionControl.byN1QL is malformed");
}
