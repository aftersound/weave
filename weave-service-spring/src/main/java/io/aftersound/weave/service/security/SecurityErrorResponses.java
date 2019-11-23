package io.aftersound.weave.service.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.aftersound.weave.jackson.ObjectMapperBuilder;
import io.aftersound.weave.service.message.MessageRegistry;
import io.aftersound.weave.service.response.ServiceResponse;

import java.util.Arrays;

class SecurityErrorResponses {

    public static final String MISSING_TOKEN_OR_CREDENTIAL;
    public static final String BAD_TOKEN;
    public static final String BAD_CREDENTIAL;
    public static final String TOKEN_EXPIRED;
    public static final String CREDENTIAL_EXPIRED;
    public static final String AUTHENTICATION_SERVICE_ERROR;
    public static final String ACCESS_DENIED;
    public static final String UNCLASSIFIED_SECURITY_ERROR;


    static {
        ObjectMapper mapper = ObjectMapperBuilder.forJson().build();
        ServiceResponse serviceResponse = new ServiceResponse();

        serviceResponse.setMessages(
                Arrays.asList(
                        MessageRegistry.UNAUTHORIZED.error("Missing Token or Credential")
                )
        );
        MISSING_TOKEN_OR_CREDENTIAL = toString(mapper, serviceResponse);

        serviceResponse.setMessages(
                Arrays.asList(
                        MessageRegistry.UNAUTHORIZED.error("Bad Token")
                )
        );
        BAD_TOKEN = toString(mapper, serviceResponse);

        serviceResponse.setMessages(
                Arrays.asList(
                        MessageRegistry.UNAUTHORIZED.error("Bad Credential")
                )
        );
        BAD_CREDENTIAL = toString(mapper, serviceResponse);

        serviceResponse.setMessages(
                Arrays.asList(
                        MessageRegistry.UNAUTHORIZED.error("Token Expired")
                )
        );
        TOKEN_EXPIRED = toString(mapper, serviceResponse);

        serviceResponse.setMessages(
                Arrays.asList(
                        MessageRegistry.UNAUTHORIZED.error("Credential Expired")
                )
        );
        CREDENTIAL_EXPIRED = toString(mapper, serviceResponse);

        serviceResponse.setMessages(
                Arrays.asList(
                        MessageRegistry.UNAUTHORIZED.error("Authentication Service Error")
                )
        );
        AUTHENTICATION_SERVICE_ERROR = toString(mapper, serviceResponse);

        serviceResponse.setMessages(
                Arrays.asList(
                        MessageRegistry.UNAUTHORIZED.error("Access Denied")
                )
        );
        ACCESS_DENIED = toString(mapper, serviceResponse);

        serviceResponse.setMessages(
                Arrays.asList(
                        MessageRegistry.UNAUTHORIZED.error("Unclassified Security Error")
                )
        );
        UNCLASSIFIED_SECURITY_ERROR = toString(mapper, serviceResponse);
    }

    private static String toString(ObjectMapper mapper, ServiceResponse response) {
        try {
            return mapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }
}
