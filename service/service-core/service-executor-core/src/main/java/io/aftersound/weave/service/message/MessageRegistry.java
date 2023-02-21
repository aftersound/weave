package io.aftersound.weave.service.message;

public class MessageRegistry {

    public static final Template NO_RESOURCE = new Template(
            404,
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

    public static final Template MISSING_REQUIRED_PARAMETER = new Template(
            400,
            Category.REQUEST,
            "Required {param_type} parameter {param_name} is missing",
            Parameter.ParamName,
            Parameter.ParamType
    );

    public static final Template MISSING_SOFT_REQUIRED_PARAMETER_ALL_OTHER_EXIST = new Template(
            400,
            Category.REQUEST,
            "Parameter {param_name} of type {param_type} cannot be missing when all other parameters {other_params} exist",
            Parameter.ParamName,
            Parameter.ParamType,
            Parameter.OtherParams
    );

    public static final Template MISSING_SOFT_REQUIRED_PARAMETER_ANY_OTHER_EXISTS = new Template(
            400,
            Category.REQUEST,
            "Parameter {param_name} of type {param_type} cannot be missing when any one of other parameters {other_params} exist",
            Parameter.ParamName,
            Parameter.ParamType,
            Parameter.OtherParams
    );

    public static final Template MISSING_SOFT_REQUIRED_PARAMETER_ALL_OTHER_NOT_EXIST = new Template(
            400,
            Category.REQUEST,
            "Parameter {param_name} of type {param_type} cannot be missing when all other parameters {other_params} do not exist",
            Parameter.ParamName,
            Parameter.ParamType,
            Parameter.OtherParams
    );

    public static final Template MISSING_SOFT_REQUIRED_PARAMETER_ANY_OTHER_NOT_EXIST = new Template(
            400,
            Category.REQUEST,
            "Parameter {param_name} of type {param_type} cannot be missing when any one of other parameters {other_params} does not exist",
            Parameter.ParamName,
            Parameter.ParamType,
            Parameter.OtherParams
    );

    public static final Template NO_SERVICE_EXECUTOR = new Template(
            500,
            Category.APPLICATION,
            "No service executor for {path}",
            Parameter.Path
    );

    public static final Template PARTIAL_SUCCESS = new Template(
            206,
            Category.SERVICE,
            "{reason}",
            Parameter.Reason
    );

    public static final Template INVALID_PARAMETER_VALUE = new Template(
            400,
            Category.REQUEST,
            "Specified value {param_value} for {param_type} parameter {param_name} is invalid",
            Parameter.ParamName,
            Parameter.ParamType,
            Parameter.ParamValue
    );

    public static final Template BAD_REQUEST = new Template(
            401,
            Category.REQUEST,
            "{reason}",
            Parameter.Reason
    );

    public static final Template UNAUTHORIZED = new Template(
            401,
            Category.SERVICE,
            "{reason}",
            Parameter.Reason
    );

    public static final Template NOT_FOUND = new Template(
            404,
            Category.SERVICE,
            "{reason}",
            Parameter.Reason
    );

    public static final Template CONFLICT = new Template(
            409,
            Category.SERVICE,
            "{reason}",
            Parameter.Reason
    );

    public static final Template INTERNAL_SERVICE_ERROR = new Template(
            500,
            Category.APPLICATION,
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
