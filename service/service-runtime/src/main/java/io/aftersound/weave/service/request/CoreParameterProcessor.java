package io.aftersound.weave.service.request;

import io.aftersound.weave.common.ValueFunc;
import io.aftersound.weave.common.ValueFuncRegistry;
import io.aftersound.weave.component.ComponentRepository;
import io.aftersound.weave.service.ServiceContext;
import io.aftersound.weave.service.message.Message;
import io.aftersound.weave.service.message.MessageRegistry;
import io.aftersound.weave.service.message.Messages;
import io.aftersound.weave.service.message.Severity;
import io.aftersound.weave.service.metadata.param.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Pattern;

public class CoreParameterProcessor extends ParameterProcessor<HttpServletRequest> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoreParameterProcessor.class);

    private static final Pattern SLASH_SPLITTER = Pattern.compile("/");

    private final ValueFuncRegistry valueFuncRegistry;

    public CoreParameterProcessor(ComponentRepository componentRepository) {
        super(componentRepository);
        this.valueFuncRegistry = new ValueFuncRegistry();
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
            ValueFunc<Object, Object> valueFunc = getValueFunc(paramField);
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
            ValueFunc<Object, Object> valueFunc = getValueFunc(paramField);
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
            ValueFunc<Object, Object> valueFunc = getValueFunc(paramField);
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

        ValueFunc<Object, Object> valueFunc = getValueFunc(paramField);
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
            ValueFunc<Object, Object> valueFunc = getValueFunc(paramField);
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
            ValueFunc<Object, ?> valueFunc = getValueFunc(paramField);
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

    private ValueFunc<Object, Object> getValueFunc(ParamField paramField) {
        final String valueFunc;
        if (paramField.getValueFunc() != null) {
            valueFunc = paramField.getValueFunc();
        } else {
            String valueType = paramField.getType();
            if (valueType == null) {
                valueType = "string";
            }

            // io.aftersound.weave.value.CommonValueFuncFactory
            // must be initialized by MasterValueFuncFactory for
            // follow value func to work
            switch (valueType.toLowerCase()) {
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
        }
        return valueFuncRegistry.getValueFunc(valueFunc);
    }

    private ParamValueHolder parseParamValue(ParamField paramField, String[] rawValues) {
        ValueFunc<Object, Object> valueFunc = getValueFunc(paramField);

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
                Constraint.When requiredWhen = paramField.getConstraint().getRequiredWhen();
                // required when is undefined, fine, treated same as Optional
                if (requiredWhen == null) {
                    break;
                }

                String[] otherParamNames = requiredWhen.getOtherParamNames();
                // does not involve other parameters, as if it is Optional
                if (otherParamNames == null || otherParamNames.length == 0) {
                    break;
                }

                Constraint.Condition condition = requiredWhen.getCondition();
                // condition is not specified, as if it's Optional
                if (condition == null) {
                    break;
                }

                if (paramValueHolders.containsKey(paramField.getName())) {
                    break;
                }

                // required when all other parameters exist
                if (condition == Constraint.Condition.AllOtherExist) {
                    boolean allExist = true;
                    for (String otherParamName : otherParamNames) {
                        if (!paramValueHolders.containsKey(otherParamName)) {
                            allExist = false;
                            break;
                        }
                    }
                    if (allExist) {
                        messages.addMessage(
                                MessageRegistry.MISSING_SOFT_REQUIRED_PARAMETER_ALL_OTHER_EXIST.error(
                                        paramField.getName(),
                                        paramField.getParamType().name(),
                                        String.join("|", otherParamNames)
                                )
                        );
                    }
                }

                // required when any of other parameters presents
                if (condition == Constraint.Condition.AnyOtherExists) {
                    boolean anyExists = false;
                    for (String otherParamName : otherParamNames) {
                        if (paramValueHolders.containsKey(otherParamName)) {
                            anyExists = true;
                            break;
                        }
                    }
                    if (anyExists) {
                        messages.addMessage(
                                MessageRegistry.MISSING_SOFT_REQUIRED_PARAMETER_ANY_OTHER_EXISTS.error(
                                        paramField.getName(),
                                        paramField.getParamType().name(),
                                        String.join("|", otherParamNames)
                                )
                        );
                    }
                }

                // required when all of other parameters don't present
                if (condition == Constraint.Condition.AllOtherNotExist) {
                    boolean allNotExist = true;
                    for (String otherParamName : otherParamNames) {
                        if (!paramValueHolders.containsKey(otherParamName)) {
                            allNotExist = false;
                            break;
                        }
                    }
                    if (allNotExist) {
                        messages.addMessage(
                                MessageRegistry.MISSING_SOFT_REQUIRED_PARAMETER_ALL_OTHER_NOT_EXIST.error(
                                        paramField.getName(),
                                        paramField.getParamType().name(),
                                        String.join("|", otherParamNames)
                                )
                        );
                    }
                }

                // required when any of other parameters doesn't present
                if (condition == Constraint.Condition.AnyOtherNotExist) {
                    boolean anyNotExist = false;
                    for (String otherParamName : otherParamNames) {
                        if (!paramValueHolders.containsKey(otherParamName)) {
                            anyNotExist = true;
                            break;
                        }
                    }
                    if (anyNotExist) {
                        messages.addMessage(
                                MessageRegistry.MISSING_SOFT_REQUIRED_PARAMETER_ANY_OTHER_NOT_EXIST.error(
                                        paramField.getName(),
                                        paramField.getParamType().name(),
                                        String.join("|", otherParamNames)
                                )
                        );
                    }
                }
            }
        }

        if (messages.getMessagesWithSeverity(Severity.ERROR).size() > 0) {
            return;
        }

        Validation validation = paramField.getValidation();
        if (validation != null) {
            ValueFunc<Object, Message> validator = valueFuncRegistry.getValueFunc(validation.getSpec());
            Message validationResult = validator.apply(paramValueHolder.getValue());
            messages.addMessage(validationResult);
        }
    }

    private static Message missingRequiredParamError(ParamField paramMetadata) {
        return MessageRegistry.MISSING_REQUIRED_PARAMETER.error(
                paramMetadata.getName(),
                paramMetadata.getParamType().name()
        );
    }

}
