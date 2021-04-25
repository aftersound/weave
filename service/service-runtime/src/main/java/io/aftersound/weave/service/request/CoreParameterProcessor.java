package io.aftersound.weave.service.request;

import io.aftersound.weave.actor.ActorRegistry;
import io.aftersound.weave.common.ValueFuncFactory;
import io.aftersound.weave.service.ServiceContext;
import io.aftersound.weave.service.message.Message;
import io.aftersound.weave.service.message.MessageRegistry;
import io.aftersound.weave.service.message.Messages;
import io.aftersound.weave.service.message.Severity;
import io.aftersound.weave.service.metadata.param.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CoreParameterProcessor extends ParameterProcessor<HttpServletRequest> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoreParameterProcessor.class);

    private static final Pattern SLASH_SPLITTER = Pattern.compile("/");
    private static final Validator NULL_VALIDATOR = new NullValidator();

    private final ParamValueParser paramValueParser;

    public CoreParameterProcessor(
            ActorRegistry<ValueFuncFactory> valueFuncFactoryRegistry,
            ActorRegistry<Validator> paramValidatorRegistry) {
        super(paramValidatorRegistry);
        this.paramValueParser = new ParamValueParser(valueFuncFactoryRegistry);
    }

    @Override
    protected List<ParamValueHolder> process(HttpServletRequest request, ParamFields paramFields, ServiceContext context) {
        Map<String, ParamValueHolder> headerParamValues = extractHeaderParamValues(request, paramFields, context);
        Map<String, ParamValueHolder> pathParamValues = extractPathParamValues(request, paramFields, context);
        Map<String, ParamValueHolder> queryParamValues = extractQueryParamValues(request, paramFields, context);
        Map<String, ParamValueHolder> bodyParamValues = extractBodyParamValues(request, paramFields, context);

        Map<String, ParamValueHolder> predefinedParamValues = extractPredefinedParamValues(paramFields, context);

        List<ParamValueHolder> paramValueHolders = new ArrayList<>();
        paramValueHolders.addAll(headerParamValues.values());
        paramValueHolders.addAll(pathParamValues.values());
        paramValueHolders.addAll(queryParamValues.values());
        paramValueHolders.addAll(bodyParamValues.values());
        paramValueHolders.addAll(predefinedParamValues.values());
        return paramValueHolders;
    }

    private Map<String, ParamValueHolder> extractHeaderParamValues(
            HttpServletRequest request,
            ParamFields paramFields,
            ServiceContext context) {
        Map<String, List<String>> headers = new LinkedHashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();

            List<String> values = new ArrayList<>();
            Enumeration<String> headerValues = request.getHeaders(headerName);
            while (headerValues.hasMoreElements()) {
                values.add(headerValues.nextElement());
            }

            headers.put(headerName, values);
        }

        // Defined Header Parameters
        ParamFields headerParamFields = paramFields.withParamType(ParamType.Header);

        Map<String, ParamValueHolder> paramValueHolders = new LinkedHashMap<>();
        for (ParamField paramField : headerParamFields.all()) {
            ParamValueHolder paramValueHolder = extractAndParseParamValue(paramField.getName(), paramField, headers, context);
            if (paramValueHolder != null) {
                paramValueHolders.put(paramValueHolder.getParamName(), paramValueHolder);
                if (paramField.hasAlias()) {
                    paramValueHolders.put(paramField.getAlias(), paramValueHolder.copyWith(paramField.getAlias()));
                }
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
        List<String> orderedPathParamNames = new ArrayList<>(orderedPathParamFields.paramNames());

        if (path.length != orderedPathParamFields.all().size()) {
            String pathParams = String.join("/", orderedPathParamNames);
            context.getMessages().addMessage(MessageRegistry.RESOURCE_PATH_MISMATCH.error(pathParams, request.getRequestURI()));
            return Collections.emptyMap();
        }

        Map<String, List<String>> pathParameters = new LinkedHashMap<>();
        for (int index = 0; index < orderedPathParamNames.size(); index++) {
            String pathParamName = orderedPathParamNames.get(index);
            List<String> pathParamValues = Collections.singletonList(path[index]);
            pathParameters.put(pathParamName, pathParamValues);
        }

        Map<String, ParamValueHolder> paramValueHolders = new LinkedHashMap<>(orderedPathParamNames.size());
        for (ParamField paramField : orderedPathParamFields.all()) {
            ParamValueHolder paramValueHolder = extractAndParseParamValue(paramField.getName(), paramField, pathParameters, context);
            if (paramValueHolder != null) {
                paramValueHolders.put(paramValueHolder.getParamName(), paramValueHolder);
                if (paramField.hasAlias()) {
                    paramValueHolders.put(paramField.getAlias(), paramValueHolder.copyWith(paramField.getAlias()));
                }
            }
        }

        return paramValueHolders;
    }

    private Map<String, ParamValueHolder> extractQueryParamValues(
            HttpServletRequest request,
            ParamFields paramFields,
            ServiceContext context) {
        Map<String, String[]> paramMap = request.getParameterMap();
        Map<String, List<String>> parameters = new LinkedHashMap<>();
        for (Map.Entry<String, String[]> entry : paramMap.entrySet()) {
            String paramName = entry.getKey();
            String[] paramValues = entry.getValue();
            parameters.put(paramName, Arrays.asList(paramValues));
        }

        // Defined Query Parameters
        ParamFields queryParamFields = paramFields.withParamType(ParamType.Query);

        ParamFields namedParamFields = queryParamFields.named();
        ParamField dynamicParamField = queryParamFields.unnamed();

        Map<String, ParamValueHolder> paramValueHolders = new LinkedHashMap<>();

        // process named query parameters
        for (ParamField paramField : namedParamFields.all()) {
            ParamValueHolder paramValueHolder = extractAndParseParamValue(paramField.getName(), paramField, parameters, context);
            if (paramValueHolder != null) {
                paramValueHolders.put(paramValueHolder.getParamName(), paramValueHolder);
                if (paramField.hasAlias()) {
                    paramValueHolders.put(paramField.getAlias(), paramValueHolder.copyWith(paramField.getAlias()));
                }
            }
        }

        // process unnamed query parameters
        if (dynamicParamField != null) {
            for (Map.Entry<String, List<String>> entry : parameters.entrySet()) {
                if (!namedParamFields.contains(entry.getKey())) {
                    ParamValueHolder paramValueHolder = extractAndParseParamValue(entry.getKey(), dynamicParamField, parameters, context);
                    if (paramValueHolder != null) {
                        paramValueHolders.put(paramValueHolder.getParamName(), paramValueHolder);
                    }
                }
            }
        }

        return paramValueHolders;
    }

    private Map<String, ParamValueHolder> extractBodyParamValues(
            HttpServletRequest request,
            ParamFields paramFields,
            ServiceContext context) {
        Map<String, ParamValueHolder> paramValueHolders = new HashMap<>();
        ParamField paramField = paramFields.firstIfExists(ParamType.Body);
        if (paramField == null) {
            return paramValueHolders;
        }

        try {
            String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            if (body != null && body.length() > 0) {
                ParamValueHolder paramValueHolder = paramValueParser.parse(
                        paramField,
                        paramField.getName(),
                        Arrays.asList(body)
                );
                if (paramValueHolder == null || paramValueHolder.getValue() == null) {
                    context.getMessages().addMessage(missingRequiredParamError(paramField));
                    return null;
                }

                paramValueHolders.put(paramField.getName(), paramValueHolder);

            } else {
                if (paramField.getConstraint().getType() == Constraint.Type.Required) {
                    context.getMessages().addMessage(
                            MessageRegistry.MISSING_REQUIRED_PARAMETER.error(paramField.getName(), paramField.getParamType().name())
                    );
                }
            }
        } catch (Exception e) {
            context.getMessages().addMessage(unableToReadRequestBodyError());
        }

        return paramValueHolders;
    }

    private Map<String, ParamValueHolder> extractPredefinedParamValues(ParamFields paramFields, ServiceContext context) {
        // Predefined Parameters
        ParamFields predefinedParamFields = paramFields.withParamType(ParamType.Predefined);

        Map<String, ParamValueHolder> predefinedParamValueHolders = new LinkedHashMap<>();
        for (ParamField paramField : predefinedParamFields.all()) {
            ParamValueHolder valueHolder = paramValueParser.parse(paramField, paramField.getName(), null);
            if (valueHolder != null && valueHolder.getValue() != null) {
                predefinedParamValueHolders.put(paramField.getName(), valueHolder);
            } else {
                context.getMessages().addMessage(predefinedParamMissingValueError(paramField));
            }
        }
        return predefinedParamValueHolders;
    }

    private ParamValueHolder extractAndParseParamValue(
            String paramName,
            ParamField paramField,
            Map<String, List<String>> parameters,
            ServiceContext context) {
        Constraint.Type constraintType = paramField.getConstraint().getType();

        // ParamField without name is considered optional
        if (paramField.getName() == null) {
            constraintType = Constraint.Type.Optional;
        }
        switch (constraintType) {
            case Required:
                return extractAndParseRequiredParamValue(paramName, paramField, parameters, context);
            case SoftRequired:
                return extractAndParseSoftRequiredParamValue(paramName, paramField, parameters, context);
            case Optional:
                return extractAndParseOptionalParamValue(paramName, paramField, parameters, context);
            default:
                return null;
        }
    }

    private ParamValueHolder extractAndParseRequiredParamValue(
            String paramName,
            ParamField paramField,
            Map<String, List<String>> parameters,
            ServiceContext context) {
        List<String> rawValues = extractRawValues(paramName, paramField, parameters);

        // Basic/default validation: for Required parameter, some value must be
        // specified, even if the value is invalid
        if (rawValues == null || rawValues.isEmpty()) {
            context.getMessages().addMessage(missingRequiredParamError(paramField));
            return null;
        }

        // Customized validation: when ParamField.validation is explicitly specified
        Validator validator = getParamValidator(paramField);
        Messages messages = validator.validate(paramField, rawValues);
        if (messages.getMessagesWithSeverity(Severity.ERROR).size() > 0) {
            context.getMessages().acquire(messages);
            return null;
        }

        ParamValueHolder valueHolder = paramValueParser.parse(paramField, paramName, rawValues);
        if (valueHolder == null) {
            context.getMessages().addMessage(missingRequiredParamError(paramField));
            return null;
        } else {
            if (valueHolder.getValue() == null) {
                context.getMessages().addMessage(invalidParamValueError(paramField, parameters.get(paramField.getName())));
                return null;
            }
        }

        return valueHolder;
    }

    private ParamValueHolder extractAndParseSoftRequiredParamValue(
            String paramName,
            ParamField paramField,
            Map<String, List<String>> parameters,
            ServiceContext context) {
        List<String> rawValues = extractRawValues(paramName, paramField, parameters);

        Validator validator = getParamValidator(paramField);
        Messages messages = validator.validate(paramField, rawValues);
        if (messages.getMessagesWithSeverity(Severity.ERROR).size() > 0) {
            context.getMessages().acquire(messages);
            return null;
        }

        ParamValueHolder valueHolder = paramValueParser.parse(paramField, paramName, rawValues);
        if (valueHolder != null && valueHolder.getValue() != null) {
            return valueHolder;
        }
        // else validation is needed
        Constraint.When requiredWhen = paramField.getConstraint().getRequiredWhen();
        // required when is undefined, fine
        if (requiredWhen == null) {
            return valueHolder;
        }

        String[] otherParamNames = requiredWhen.getOtherParamNames();
        // does not involve other parameters, as if it is Optional
        if (otherParamNames == null || otherParamNames.length == 0) {
            return valueHolder;
        }

        Constraint.Condition condition = requiredWhen.getCondition();
        // condition is not specified, as if it's Optional
        if (condition == null) {
            return valueHolder;
        }

        // all of other parameters present
        if (condition == Constraint.Condition.AllOtherExist) {
            boolean allExist = true;
            for (String otherParamName : otherParamNames) {
                if (parameters.get(otherParamName) == null || parameters.get(otherParamName).isEmpty()) {
                    allExist = false;
                    break;
                }
            }
            if (allExist) {
                context.getMessages().addMessage(
                        MessageRegistry.MISSING_SOFT_REQUIRED_PARAMETER_ALL_OTHER_EXIST.error(
                                paramField.getName(),
                                paramField.getParamType().name(),
                                String.join("|", otherParamNames)
                        )
                );
            }
        }

        // any of other parameters presents
        if (condition == Constraint.Condition.AnyOtherExists) {
            boolean anyExists = false;
            for (String otherParamName : otherParamNames) {
                if (parameters.get(otherParamName) != null && !parameters.get(otherParamName).isEmpty()) {
                    anyExists = true;
                    break;
                }
            }
            if (anyExists) {
                context.getMessages().addMessage(
                        MessageRegistry.MISSING_SOFT_REQUIRED_PARAMETER_ANY_OTHER_EXISTS.error(
                                paramField.getName(),
                                paramField.getParamType().name(),
                                String.join("|", otherParamNames)
                        )
                );
            }
        }

        // all of other parameters don't present
        if (condition == Constraint.Condition.AllOtherNotExist) {
            boolean allNotExist = true;
            for (String otherParamName : otherParamNames) {
                if (parameters.get(otherParamName) != null && !parameters.get(otherParamName).isEmpty()) {
                    allNotExist = false;
                    break;
                }
            }
            if (allNotExist) {
                context.getMessages().addMessage(
                        MessageRegistry.MISSING_SOFT_REQUIRED_PARAMETER_ALL_OTHER_NOT_EXIST.error(
                                paramField.getName(),
                                paramField.getParamType().name(),
                                String.join("|", otherParamNames)
                        )
                );
            }
        }

        // any of other parameters doesn't present
        if (condition == Constraint.Condition.AnyOtherNotExist) {
            boolean anyNotExist = false;
            for (String otherParamName : otherParamNames) {
                if (parameters.get(otherParamName) == null || parameters.get(otherParamName).isEmpty()) {
                    anyNotExist = true;
                    break;
                }
            }
            if (anyNotExist) {
                context.getMessages().addMessage(
                        MessageRegistry.MISSING_SOFT_REQUIRED_PARAMETER_ANY_OTHER_NOT_EXIST.error(
                                paramField.getName(),
                                paramField.getParamType().name(),
                                String.join("|", otherParamNames)
                        )
                );
            }
        }

        return valueHolder;
    }

    private ParamValueHolder extractAndParseOptionalParamValue(
            String paramName,
            ParamField paramField,
            Map<String, List<String>> parameters,
            ServiceContext context) {
        List<String> rawValues = extractRawValues(paramName, paramField, parameters);

        Validator validator = getParamValidator(paramField);
        Messages messages = validator.validate(paramField, rawValues);
        if (messages.getMessagesWithSeverity(Severity.ERROR).size() > 0) {
            context.getMessages().acquire(messages);
            return null;
        }

        ParamValueHolder valueHolder = paramValueParser.parse(paramField, paramName, rawValues);
        if (valueHolder == null) {
            return null;
        }
        if (valueHolder.getValue() == null) {
            context.getMessages().addMessage(invalidParamValueWarning(paramField, parameters.get(paramField.getName())));
            return null;
        }
        return valueHolder;
    }

    private static List<String> extractRawValues(String paramName, ParamField paramField, Map<String, List<String>> parameters) {
        List<String> rawValues = parameters.get(paramName);
        if (rawValues == null || rawValues.isEmpty()) {
            return rawValues;
        }

        if (paramField.getValueDelimiter() == null) {
            return rawValues;
        }

        List<String> expanded = new ArrayList<>();
        for (String rawValue : rawValues) {
            String[] splitted = rawValue.split(paramField.getValueDelimiter());
            expanded.addAll(Arrays.asList(splitted));
        }
        return expanded;
    }

    private Validator getParamValidator(ParamField paramField) {
        Validation validation = paramField.getValidation();
        if (validation == null) {
            return NULL_VALIDATOR;
        }
        return paramValidatorRegistry.get(validation.getType(), NULL_VALIDATOR);
    }

    private static Message unableToReadRequestBodyError() {
        return MessageRegistry.UNABLE_TO_READ_REQUEST_BODY.error();
    }

    private static Message predefinedParamMissingValueError(ParamField paramMetadata) {
        return MessageRegistry.PREDEFINED_PARAMETER_MISSING_VALUE.error(
                paramMetadata.getName(),
                paramMetadata.getParamType().name()
        );
    }

    private static Message missingRequiredParamError(ParamField paramMetadata) {
        return MessageRegistry.MISSING_REQUIRED_PARAMETER.error(
                paramMetadata.getName(),
                paramMetadata.getParamType().name()
        );
    }

    private static Message invalidParamValueError(ParamField paramMetadata, List<String> values) {
        return MessageRegistry.INVALID_PARAMETER_VALUE.error(
                paramMetadata.getName(),
                paramMetadata.getParamType().name(),
                String.join("|", values));
    }

    private static Message invalidParamValueWarning(ParamField paramMetadata, List<String> values) {
        return MessageRegistry.INVALID_PARAMETER_VALUE.warning(
                paramMetadata.getName(),
                paramMetadata.getParamType().name(),
                String.join("|", values));
    }

}
