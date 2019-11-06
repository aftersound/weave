package io.aftersound.weave.service.message;

import javax.ws.rs.core.Response;

public class MessageRegistry extends BaseMessageRegistry {

    public static final Template NO_RESOURCE = new Template(1001, Category.REQUEST,
            "Resource at path {path} is not available",
            Parameter.Path);

    public static final Template RESOURCE_PATH_MISMATCH = new Template(1001, Category.REQUEST,
            "Resource paths mismatch: {path_values} in request conflicts with {path_params}",
            Parameter.ResourcePathParams,
            Parameter.ResourcePathValues);

    public static final Template PREDEFINED_PARAMETER_MISSING_VALUE = new Template(1005, Category.APPLICATION,
            "Predefined parameter {param_name} of type {param_type} is missing a value", Parameter.ParamName,
            Parameter.ParamType);

    public static final Template UNABLE_TO_READ_REQUEST_BODY = new Template(1006, Category.REQUEST,
            "Unable to read request body");

    public static final Template MISSING_REQUIRED_PARAMETER = new Template(1006, Category.REQUEST,
            "Required parameter {param_name} of type {param_type} is missing", Parameter.ParamName,
            Parameter.ParamType);

    public static final Template MISSING_SOFT_REQUIRED_PARAMETER_ALL_OTHER_EXIST = new Template(
            1007,
            Category.REQUEST,
            "Parameter {param_name} of type {param_type} cannot be missing when all other parameters {other_params} exist",
            Parameter.ParamName,
            Parameter.ParamType,
            Parameter.OtherParams
    );

    public static final Template MISSING_SOFT_REQUIRED_PARAMETER_ANY_OTHER_EXISTS = new Template(
            1008,
            Category.REQUEST,
            "Parameter {param_name} of type {param_type} cannot be missing when any one of other parameters {other_params} exist",
            Parameter.ParamName,
            Parameter.ParamType,
            Parameter.OtherParams
    );

    public static final Template MISSING_SOFT_REQUIRED_PARAMETER_ALL_OTHER_NOT_EXIST = new Template(
            1009,
            Category.REQUEST,
            "Parameter {param_name} of type {param_type} cannot be missing when all other parameters {other_params} do not exist",
            Parameter.ParamName,
            Parameter.ParamType,
            Parameter.OtherParams
    );

    public static final Template MISSING_SOFT_REQUIRED_PARAMETER_ANY_OTHER_NOT_EXIST = new Template(
            1010,
            Category.REQUEST,
            "Parameter {param_name} of type {param_type} cannot be missing when any one of other parameters {other_params} does not exist",
            Parameter.ParamName,
            Parameter.ParamType,
            Parameter.OtherParams
    );

    public static final Template INVALID_PARAMETER_VALUE = new Template(1011, Category.REQUEST,
            "Specified value {param_value} for parameter {param_name} of type {param_type} is invalid",
            Parameter.ParamName, Parameter.ParamType, Parameter.ParamValue);

    public static final Template NO_SERVICE_EXECUTOR = new Template(1012, Category.REQUEST,
            "No service executor for {path}", Parameter.Path);

    private enum Parameter implements Param {
        ResourcePathParams("path_params"),
        ResourcePathValues("path_values"),
        Path("path"),
        ParamName("param_name"),
        ParamType("param_type"),
        OtherParams("other_params"),
        ParamValue("param_value");

        private final String name;

        private Parameter(final String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

    }

    private static class StatusMapperImpl implements StatusMapper {

        @Override
        public Response.Status getStatus(Messages messages) {
            // TODO:xiaocxu
            return Response.Status.OK;
        }

    }

    static {
        BaseMessageRegistry.bindStatusMapper(new StatusMapperImpl());
    }
}
