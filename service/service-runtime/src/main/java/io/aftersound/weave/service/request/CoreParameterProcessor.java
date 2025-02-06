package io.aftersound.weave.service.request;

import io.aftersound.func.Directive;
import io.aftersound.func.Func;
import io.aftersound.func.FuncFactory;
import io.aftersound.func.FuncRegistry;
import io.aftersound.msg.Message;
import io.aftersound.msg.Messages;
import io.aftersound.msg.Severity;
import io.aftersound.schema.Constraint;
import io.aftersound.schema.ProtoTypes;
import io.aftersound.schema.Type;
import io.aftersound.weave.component.ComponentRepository;
import io.aftersound.weave.service.ServiceContext;
import io.aftersound.weave.service.message.MessageRegistry;
import io.aftersound.weave.service.metadata.param.ParamField;
import io.aftersound.weave.service.metadata.param.ParamFields;
import io.aftersound.weave.service.metadata.param.ParamType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Pattern;

public class CoreParameterProcessor extends ParameterProcessor<HttpServletRequest> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoreParameterProcessor.class);

    private static final Pattern SLASH_SPLITTER = Pattern.compile("/");

    private final FuncRegistry valueFuncRegistry;

    public CoreParameterProcessor(ComponentRepository componentRepository, FuncFactory funcFactory) {
        super(componentRepository);
        this.valueFuncRegistry = new FuncRegistry(funcFactory::create);
    }

    @Override
    protected List<ParamValueHolder> process(HttpServletRequest request, ParamFields paramFields, ServiceContext context) {
        Map<String, ParamValueHolder> methodParamValues = extractMethodParamValues(request, paramFields);
        Map<String, ParamValueHolder> headerParamValues = extractHeaderParamValues(request, paramFields);
        Map<String, ParamValueHolder> pathParamValues = extractPathParamValues(request, paramFields, context);
        Map<String, ParamValueHolder> queryParamValues = extractQueryParamValues(request, paramFields);
        Map<String, ParamValueHolder> bodyParamValues = extractBodyParamValues(request, paramFields);
        Map<String, ParamValueHolder> predefinedParamValues = extractPredefinedParamValues(paramFields);
        Map<String, ParamValueHolder> derivedParamValues = extractDerivedParamValues(
                paramFields,
                methodParamValues,
                headerParamValues,
                queryParamValues,
                bodyParamValues,
                predefinedParamValues
        );

        Map<String, ParamValueHolder> paramValueHolders = new LinkedHashMap<>(
                methodParamValues.size() +
                        headerParamValues.size() +
                        pathParamValues.size() +
                        queryParamValues.size() +
                        bodyParamValues.size() +
                        predefinedParamValues.size() +
                        predefinedParamValues.size() +
                        derivedParamValues.size()
        );
        paramValueHolders.putAll(methodParamValues);
        paramValueHolders.putAll(headerParamValues);
        paramValueHolders.putAll(pathParamValues);
        paramValueHolders.putAll(queryParamValues);
        paramValueHolders.putAll(bodyParamValues);
        paramValueHolders.putAll(predefinedParamValues);
        paramValueHolders.putAll(derivedParamValues);

        for (Map.Entry<String, ParamValueHolder> e : paramValueHolders.entrySet()) {
            ParamValueHolder paramValueHolder = e.getValue();
            validate(paramValueHolder, paramValueHolders, context.getMessages());
        }

        return new ArrayList<>(paramValueHolders.values());
    }

    private Map<String, ParamValueHolder> extractMethodParamValues(
            HttpServletRequest request,
            ParamFields paramFields) {
        ParamField paramField = paramFields.firstIfExists(ParamType.Method);
        if (paramField != null) {
            String method = request.getMethod();
            Func<Object, Object> valueFunc = getValueFunc(paramField);
            Object value = valueFunc.apply(method);
            if (value != null) {
                ParamValueHolder paramValueHolder = ParamValueHolder
                        .singleValued(paramField.getName(), paramField.getType(), value)
                        .bindRawValue(method)
                        .bindParamField(paramField);
                Map<String, ParamValueHolder> paramValueHolders = new HashMap<>(1);
                paramValueHolders.put(paramField.getName(), paramValueHolder);
                return paramValueHolders;
            }
        }
        return Collections.emptyMap();
    }

    private Map<String, ParamValueHolder> extractHeaderParamValues(
            HttpServletRequest request,
            ParamFields paramFields) {

        // Defined Header Parameters
        ParamFields headerParamFields = paramFields.withParamType(ParamType.Header);

        Map<String, ParamValueHolder> paramValueHolders = new LinkedHashMap<>();
        for (ParamField paramField : headerParamFields.all()) {
            String header = request.getHeader(paramField.getName());
            Func<Object, Object> valueFunc = getValueFunc(paramField);
            Object value = valueFunc.apply(header);
            if (value != null) {
                ParamValueHolder paramValueHolder = ParamValueHolder
                        .singleValued(paramField.getName(), paramField.getType(), value)
                        .bindRawValue(header)
                        .bindParamField(paramField);
                paramValueHolders.put(paramField.getName(), paramValueHolder);
            }
        }

        return paramValueHolders;
    }

    private Map<String, ParamValueHolder> extractPathParamValues(
            HttpServletRequest request,
            ParamFields paramFields,
            ServiceContext context) {
        String requestURI = request.getRequestURI();
        if (requestURI.startsWith("/")) {
            requestURI = requestURI.substring(1);
        }
        String[] path = SLASH_SPLITTER.split(requestURI);

        ParamFields orderedPathParamFields = paramFields.withParamType(ParamType.Path);

        if (path.length != orderedPathParamFields.all().size()) {
            String pathParams = String.join("/", Arrays.asList(path));
            context.getMessages().addMessage(MessageRegistry.RESOURCE_PATH_MISMATCH.error(pathParams, request.getRequestURI()));
            return Collections.emptyMap();
        }

        Map<String, ParamValueHolder> paramValueHolders = new LinkedHashMap<>();
        for (int index = 0; index < path.length; index++) {
            String pathValue = path[index];
            ParamField paramField = orderedPathParamFields.all().get(index);
            Func<Object, Object> valueFunc = getValueFunc(paramField);
            Object value = valueFunc.apply(pathValue);
            if (value != null) {
                ParamValueHolder paramValueHolder = ParamValueHolder
                        .singleValued(paramField.getName(), paramField.getType(), value)
                        .bindRawValue(pathValue)
                        .bindParamField(paramField);
                paramValueHolders.put(paramField.getName(), paramValueHolder);
            }
        }
        return paramValueHolders;
    }

    private Map<String, ParamValueHolder> extractQueryParamValues(HttpServletRequest request, ParamFields paramFields) {
        // Defined Query Parameters
        ParamFields queryParamFields = paramFields.withParamType(ParamType.Query);

        ParamFields namedParamFields = queryParamFields.named();
        ParamField dynamicParamField = queryParamFields.unnamed();

        Map<String, ParamValueHolder> paramValueHolders = new LinkedHashMap<>();

        // process named query parameters
        for (ParamField paramField : namedParamFields.all()) {
            String[] rawValues = request.getParameterValues(paramField.getName());
            ParamValueHolder paramValueHolder = parseParamValue(paramField, rawValues);
            if (paramValueHolder != null) {
                paramValueHolders.put(paramField.getName(), paramValueHolder);
            }
        }

        // process unnamed query parameters
        if (dynamicParamField != null) {
            Enumeration<String> paramNames = request.getParameterNames();
            while (paramNames.hasMoreElements()) {
                String paramName = paramNames.nextElement();
                if (!namedParamFields.contains(paramName)) {
                    String[] rawValues = request.getParameterValues(paramName);
                    ParamValueHolder paramValueHolder = parseParamValue(dynamicParamField, rawValues);
                    if (paramValueHolder != null) {
                        paramValueHolders.put(paramName, paramValueHolder);
                    }
                }
            }
        }

        return paramValueHolders;
    }

    private Map<String, ParamValueHolder> extractBodyParamValues(HttpServletRequest request, ParamFields paramFields) {
        Map<String, ParamValueHolder> paramValueHolders = new HashMap<>();
        ParamField paramField = paramFields.firstIfExists(ParamType.Body);
        if (paramField == null) {
            return paramValueHolders;
        }

        Func<Object, Object> valueFunc = getValueFunc(paramField);
        try (InputStream is = request.getInputStream()) {
            Object value = valueFunc.apply(is);
            if (value != null) {
                ParamValueHolder paramValueHolder = ParamValueHolder
                        .singleValued(paramField.getName(), paramField.getType(), value)
                        .bindRawValue(value)
                        .bindParamField(paramField);
                paramValueHolders.put(paramField.getName(), paramValueHolder);
            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred on extracting and parsing body parameter", e);
        }
        return paramValueHolders;
    }

    private Map<String, ParamValueHolder> extractPredefinedParamValues(ParamFields paramFields) {
        // Predefined Parameters
        ParamFields predefinedParamFields = paramFields.withParamType(ParamType.Predefined);

        Map<String, ParamValueHolder> predefinedParamValueHolders = new LinkedHashMap<>();
        for (ParamField paramField : predefinedParamFields.all()) {
            Func<Object, Object> valueFunc = getValueFunc(paramField);
            Object value = valueFunc.apply(null);
            if (value != null) {
                ParamValueHolder paramValueHolder = ParamValueHolder
                        .singleValued(paramField.getName(), paramField.getType(), value)
                        .bindParamField(paramField);
                predefinedParamValueHolders.put(paramField.getName(), paramValueHolder);
            }
        }
        return predefinedParamValueHolders;
    }

    private Map<String, ParamValueHolder> extractDerivedParamValues(
            ParamFields paramFields,
            Map<String, ParamValueHolder> methodParamValues,
            Map<String, ParamValueHolder> headerParamValues,
            Map<String, ParamValueHolder> queryParamValues,
            Map<String, ParamValueHolder> bodyParamValues,
            Map<String, ParamValueHolder> predefinedParamValues) {

        ParamFields derivedParamFields = paramFields.withParamType(ParamType.Derived);
        if (derivedParamFields.all().isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, ParamValueHolder> paramValueHolders = new HashMap<>(
                methodParamValues.size() +
                        headerParamValues.size() +
                        queryParamValues.size() +
                        bodyParamValues.size() +
                        predefinedParamValues.size()
        );
        paramValueHolders.putAll(methodParamValues);
        paramValueHolders.putAll(headerParamValues);
        paramValueHolders.putAll(queryParamValues);
        paramValueHolders.putAll(bodyParamValues);
        paramValueHolders.putAll(predefinedParamValues);

        Map<String, ParamValueHolder> derived = new HashMap<>(derivedParamFields.all().size());
        for (ParamField paramField : derivedParamFields.all()) {
            Func<Object, ?> valueFunc = getValueFunc(paramField);
            Object value = valueFunc.apply(paramValueHolders);
            if (value != null) {
                ParamValueHolder paramValueHolder;
                if (paramField.isMultiValued()) {
                    paramValueHolder = ParamValueHolder
                            .multiValued(paramField, value)
                            .bindParamField(paramField);
                } else {
                    paramValueHolder = ParamValueHolder
                            .singleValued(paramField.getName(), paramField.getType(), value)
                            .bindParamField(paramField);
                }
                derived.put(paramField.getName(), paramValueHolder);
            }
        }
        return derived;
    }

    private Func<Object, Object> getValueFunc(ParamField paramField) {
        Directive directive = paramField.directives().first(d -> "TRANSFORM".equals(d.getCategory()));
        if (directive != null) {
            return directive.function();
        } else {
            Type valueType = paramField.getType();
            if (valueType == null) {
                valueType = ProtoTypes.STRING.create();
            }

            // io.aftersound.weave.value.CommonValueFuncFactory
            // must be initialized by MasterValueFuncFactory for
            // follow value func to work

            String valueFunc;
            switch (valueType.getName().toLowerCase()) {
                case "boolean":
                    valueFunc = "BOOL:FROM(String,true)";
                    break;
                case "double":
                    valueFunc = "DOUBLE:FROM(String)";
                    break;
                case "float":
                    valueFunc = "FLOAT:FROM(String)";
                    break;
                case "integer":
                    valueFunc = "INTEGER:FROM(String)";
                    break;
                case "long":
                    valueFunc = "LONG:FROM(String)";
                    break;
                case "short":
                    valueFunc = "SHORT:FROM(String)";
                    break;
                case "string":
                default:
                    valueFunc = "_()";
            }
            return valueFuncRegistry.getFunc(valueFunc);
        }
    }

    private ParamValueHolder parseParamValue(ParamField paramField, String[] rawValues) {
        Func<Object, Object> valueFunc = getValueFunc(paramField);

        List<String> paramValues = null;
        Object parsedValue;
        if (rawValues != null && rawValues.length > 0) {
            if (paramField.isMultiValued()) {
                if (paramField.getValueDelimiter() != null) {
                    List<String> expanded = new ArrayList<>();
                    for (String rawValue : rawValues) {
                        String[] splitted = rawValue.split(paramField.getValueDelimiter());
                        expanded.addAll(Arrays.asList(splitted));
                    }
                    paramValues = expanded;
                } else {
                    paramValues = Arrays.asList(rawValues);
                }
                List<Object> values = new ArrayList<>(paramValues.size());
                for (String paramValue : paramValues) {
                    Object value = valueFunc.apply(paramValue);
                    if (value != null) {
                        values.add(value);
                    }
                }

                parsedValue = values.size() > 0 ? values : null;
            } else {
                paramValues = Arrays.asList(rawValues);
                parsedValue = valueFunc.apply(rawValues[0]);
            }
        } else {
            // ParamField.valueFuncSpec might instruct to create some value out of null
            parsedValue = valueFunc.apply(null);
        }

        if (parsedValue != null) {
            if (paramField.isMultiValued()) {
                return ParamValueHolder.multiValued(paramField, parsedValue)
                        .bindRawValue(paramValues)
                        .bindParamField(paramField);
            } else {
                return ParamValueHolder
                        .singleValued(paramField.getName(), paramField.getType(), parsedValue)
                        .bindParamField(paramField);
            }
        } else {
            return null;
        }
    }

    private void validate(ParamValueHolder paramValueHolder, Map<String, ParamValueHolder> paramValueHolders, Messages messages) {
        ParamField paramField = paramValueHolder.getParamField();

        Constraint constraint = paramField.getConstraint();
        Constraint.Type constraintType = constraint != null ? constraint.getType() : Constraint.Type.Optional;

        // ParamField without name is considered optional
        if (paramField.getName() == null) {
            constraintType = Constraint.Type.Optional;
        }

        switch (constraintType) {
            case Required: {
                if (!paramValueHolders.containsKey(paramField.getName())) {
                    messages.addMessage(missingRequiredParamError(paramField));
                }
                break;
            }
            case SoftRequired: {
                // if there is value for this SoftRequired parameter
                if (paramValueHolders.containsKey(paramField.getName())) {
                    break;
                }

                Constraint.When when = paramField.getConstraint().getWhen();
                // required when is undefined, fine, treated same as Optional
                if (when == null || when.getCondition() == null) {
                    break;
                }

                Func<Map<String, ParamValueHolder>, Boolean> predicate = valueFuncRegistry.getFunc(when.getCondition());
                Boolean met = predicate.apply(paramValueHolders);
                if (met != null && met) {
                    messages.addMessage(
                            MessageRegistry.MISSING_REQUIRED_PARAMETER.error(
                                    paramField.getName(),
                                    paramField.getParamType().name()
                            )
                    );
                }
            }
        }

        if (messages.getMessagesWithSeverity(Severity.ERROR).size() > 0) {
            return;
        }

        List<Directive> validations = paramField.directives().filter(d -> "VALIDATION".equals(d.getCategory()));
        if (!validations.isEmpty()) {
            for (Directive validation : validations) {
                Func<Object, Boolean> validator = validation.function();
                Boolean result = validator.apply(paramValueHolder.getValue());
                if (result == null || !result) {
                    messages.addMessage(validation.getMessage());
                }
            }
        }
    }

    private static Message missingRequiredParamError(ParamField paramMetadata) {
        return MessageRegistry.MISSING_REQUIRED_PARAMETER.error(
                paramMetadata.getName(),
                paramMetadata.getParamType().name()
        );
    }

}
