package io.aftersound.weave.service.message;

public class MessageRegistry {

    public static final Template NO_RESOURCE = new Template(
            101,
            Category.REQUEST,
            "Resource at path {path} is not available",
            Parameter.Path
    );

    public static final Template RESOURCE_PATH_MISMATCH = new Template(
            102,
            Category.REQUEST,
            "Resource paths mismatch: {path_values} in request conflicts with {path_params}",
            Parameter.ResourcePathParams,
            Parameter.ResourcePathValues
    );

    public static final Template PREDEFINED_PARAMETER_MISSING_VALUE = new Template(
            103,
            Category.SERVICE,
            "Predefined parameter {param_name} of type {param_type} is missing a value",
            Parameter.ParamName,
            Parameter.ParamType
    );

    public static final Template UNABLE_TO_READ_REQUEST_BODY = new Template(
            104,
            Category.REQUEST,
            "Unable to read request body"
    );

    public static final Template MISSING_REQUIRED_PARAMETER = new Template(
            105,
            Category.REQUEST,
            "Required parameter {param_name} of type {param_type} is missing",
            Parameter.ParamName,
            Parameter.ParamType
    );

    public static final Template MISSING_SOFT_REQUIRED_PARAMETER_ALL_OTHER_EXIST = new Template(
            106,
            Category.REQUEST,
            "Parameter {param_name} of type {param_type} cannot be missing when all other parameters {other_params} exist",
            Parameter.ParamName,
            Parameter.ParamType,
            Parameter.OtherParams
    );

    public static final Template MISSING_SOFT_REQUIRED_PARAMETER_ANY_OTHER_EXISTS = new Template(
            107,
            Category.REQUEST,
            "Parameter {param_name} of type {param_type} cannot be missing when any one of other parameters {other_params} exist",
            Parameter.ParamName,
            Parameter.ParamType,
            Parameter.OtherParams
    );

    public static final Template MISSING_SOFT_REQUIRED_PARAMETER_ALL_OTHER_NOT_EXIST = new Template(
            108,
            Category.REQUEST,
            "Parameter {param_name} of type {param_type} cannot be missing when all other parameters {other_params} do not exist",
            Parameter.ParamName,
            Parameter.ParamType,
            Parameter.OtherParams
    );

    public static final Template MISSING_SOFT_REQUIRED_PARAMETER_ANY_OTHER_NOT_EXIST = new Template(
            109,
            Category.REQUEST,
            "Parameter {param_name} of type {param_type} cannot be missing when any one of other parameters {other_params} does not exist",
            Parameter.ParamName,
            Parameter.ParamType,
            Parameter.OtherParams
    );

    public static final Template INVALID_PARAMETER_VALUE = new Template(
            110,
            Category.REQUEST,
            "Specified value {param_value} for parameter {param_name} of type {param_type} is invalid",
            Parameter.ParamName,
            Parameter.ParamType,
            Parameter.ParamValue
    );

    public static final Template NO_SERVICE_EXECUTOR = new Template(
            111,
            Category.APPLICATION,
            "No service executor for {path}",
            Parameter.Path
    );

    public static final Message SERVICE_METADATA_MALFORMED = Message.serviceError(
            112,
            "ServiceMetadata is malformed"
    );

    public static final Message INTERNAL_SERVICE_ERROR = Message.serviceError(
            500,
            "Internal service error"
    );

    public static final Template UNAUTHORIZED = new Template(
            401,
            Category.SERVICE,
            "{reason}",
            Parameter.Reason
    );

    private enum Parameter implements Param {
        ResourcePathParams("path_params"),
        ResourcePathValues("path_values"),
        Path("path"),
        ParamName("param_name"),
        ParamType("param_type"),
        OtherParams("other_params"),
        ParamValue("param_value"),
        Reason("reason");

        private final String name;

        Parameter(final String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

    }
}
