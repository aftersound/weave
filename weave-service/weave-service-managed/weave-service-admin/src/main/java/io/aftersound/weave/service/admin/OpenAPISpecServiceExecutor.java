package io.aftersound.weave.service.admin;

import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.service.ServiceContext;
import io.aftersound.weave.service.ServiceExecutor;
import io.aftersound.weave.service.ServiceMetadataRegistry;
import io.aftersound.weave.service.metadata.ExecutionControl;
import io.aftersound.weave.service.metadata.ServiceMetadata;
import io.aftersound.weave.service.metadata.param.Constraint;
import io.aftersound.weave.service.metadata.param.ParamField;
import io.aftersound.weave.service.request.ParamValueHolders;
import io.aftersound.weave.service.resources.ManagedResources;
import io.aftersound.weave.service.resources.ResourceInitializer;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OpenAPISpecServiceExecutor extends ServiceExecutor {

    public static final NamedType<ExecutionControl> COMPANION_CONTROL_TYPE = OpenAPISpecExecutionControl.TYPE;
    public static final ResourceInitializer RESOURCE_INITIALIZER = ResourcesInitializerImpl.instance();

    public OpenAPISpecServiceExecutor(ManagedResources managedResources) {
        super(managedResources);
    }

    @Override
    public String getType() {
        return COMPANION_CONTROL_TYPE.name();
    }

    @Override
    public Object execute(ServiceMetadata serviceMetadata, ParamValueHolders request, ServiceContext context) {
        if (!validate(serviceMetadata, context)) {
            return null;
        }

        return list(serviceMetadata, request, context);
    }

    private boolean validate(ServiceMetadata serviceMetadata, ServiceContext context) {
        if (!(serviceMetadata.getExecutionControl() instanceof OpenAPISpecExecutionControl)) {
            context.getMessages().addMessage(Messages.EXECUTION_CONTROL_UNEXPECTED);
            return false;
        }
        return true;
    }

    private Object list(ServiceMetadata serviceMetadata, ParamValueHolders request, ServiceContext context) {
        List<String> targetIds = request.firstWithName("id").multiValues(String.class);
        List<String> targetTypes = request.firstWithName("type").multiValues(String.class);

        ServiceMetadataRegistry serviceMetadataRegistry = managedResources.getResource(
                ServiceMetadataRegistry.class.getName(),
                ServiceMetadataRegistry.class
        );

        List<OpenAPI> apiSpecs = new ArrayList<>();
        for (ServiceMetadata metadata : serviceMetadataRegistry.all()) {
            if ((targetIds.isEmpty() || targetIds.contains(metadata.getId())) &&
                (targetTypes.isEmpty() || targetTypes.contains(metadata.getExecutionControl().getType()))) {
                apiSpecs.add(toOpenAPISpec(metadata));
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("list", apiSpecs);
        return result;
    }

    private OpenAPI toOpenAPISpec(ServiceMetadata serviceMetadata) {
        OpenAPI openAPI = new OpenAPI();

        PathItem path = new PathItem();
        path.parameters(toParameters(serviceMetadata.getParamFields()));
        openAPI.path(serviceMetadata.getId(), path);
        return openAPI;
    }

    private List<Parameter> toParameters(List<ParamField> paramFields) {
        List<Parameter> parameters = new ArrayList<>();
        for (ParamField paramField : paramFields) {
            parameters.add(toParameter(paramField));
        }
        return parameters;
    }

    private Parameter toParameter(ParamField paramField) {
        Parameter parameter = new Parameter();
        parameter.setName(paramField.getName());
        parameter.setDescription(paramField.getDescription());
        parameter.setIn(paramField.getType().name());
        parameter.setRequired(
                paramField.getConstraint().getType() == Constraint.Type.Required ||
                paramField.getConstraint().getType() == Constraint.Type.SoftRequired
        );

        if (paramField.getConstraint().getType() == Constraint.Type.SoftRequired) {
            Constraint.When when = paramField.getConstraint().getRequiredWhen();
            parameter.addExtension("requiredWhen", when);
        }

        Schema schema = new Schema();
        schema.setType(paramField.getValueType());
        parameter.schema(schema);
        return parameter;
    }


}
