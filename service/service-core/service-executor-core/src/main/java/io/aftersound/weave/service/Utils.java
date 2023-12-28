package io.aftersound.weave.service;

import io.aftersound.weave.common.Message;
import io.aftersound.weave.common.Severity;

public class Utils {

    /**
     * Create a warning message with some details
     *
     * @param message some details on the warning
     * @return a {@link Message} as needed
     */
    public static Message warning(String message) {
        return Message.builder()
                .withSeverity(Severity.Warning)
                .withCategory("Service")
                .withContent(message)
                .build();
    }

    /**
     * Create an error message indicating it is a bad request
     *
     * @param message some details on what's wrong with given request
     * @return a {@link Message} as needed
     */
    public static Message badRequest(String message) {
        return Message.builder()
                .withSeverity(Severity.Error)
                .withCategory("Request")
                .withCode("400")
                .withContent(message)
                .build();
    }

    /**
     * Create an error message indicating there is some conflict
     *
     * @param message some details on the conflict
     * @return a {@link Message} as needed
     */
    public static Message conflict(String message) {
        return Message.builder()
                .withSeverity(Severity.Error)
                .withCategory("Request")
                .withCode("409")
                .withContent(message)
                .build();
    }

    /**
     * Create an error message indicating there is internal server error with some details
     *
     * @param message some details of the error
     * @return a {@link Message} as needed
     */
    public static Message internalServerError(String message) {
        return Message.builder()
                .withSeverity(Severity.Error)
                .withCategory("Service")
                .withCode("500")
                .withContent(message)
                .build();
    }

    /**
     * Create an error message indicating there is internal server error without any detail
     *
     * @return a {@link Message} as needed
     */
    public static Message internalServerError() {
        return internalServerError("Internal Server Error");
    }

    /**
     * Create an error message indicating there is internal server error related to given exception
     *
     * @param e exception which provides some details of the error
     * @return a {@link Message} as needed
     */
    public static Message internalServerError(Exception e) {
        return internalServerError(e.getMessage());
    }

    /**
     * Create an error message indicating required runtime dependency with specified type and id is missing
     *
     * @param type the type of required runtime dependency
     * @param id the identifier of required runtime dependency
     * @return a {@link Message} as needed
     */
    public static Message missingRequiredDependency(String type, String id) {
        String content = String.format(
                "Required runtime dependency with specified type '%s' and id '%s' is missing",
                type,
                id
        );
        return internalServerError(content);
    }

    /**
     * Create an error message indicating required runtime dependency with specified type and id is missing
     *
     * @param type the type of required runtime dependency
     * @param id   the identifier of required runtime dependency
     * @return a {@link Message} as needed
     */
    public static Message missingRequiredDependency(Class<?> type, String id) {
        return missingRequiredDependency(type.getSimpleName(), id);
    }

    /**
     * Create an error message indicating required runtime dependency with specified id is missing
     *
     * @param id   the identifier of required runtime dependency
     * @return a {@link Message} as needed
     */
    public static Message missingRequiredDependency(String id) {
        String message = String.format("Required runtime dependency with specified id '%s' is missing", id);
        return internalServerError(message);
    }

}
