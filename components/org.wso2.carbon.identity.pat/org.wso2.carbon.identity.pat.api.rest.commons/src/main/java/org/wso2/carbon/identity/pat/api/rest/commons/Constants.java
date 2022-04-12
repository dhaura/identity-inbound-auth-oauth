package org.wso2.carbon.identity.pat.api.rest.commons;

/**
 * Constants to be used in Enterprise login management API.
 */
public class Constants {

    public static final String CORRELATION_ID_MDC = "Correlation-ID";
    public static final String SUPER_TENANT = "carbon.super";
    private static final String ERROR_PREFIX = "ELM";


    /**
     * Enum for common server error messages.
     */
    public enum ErrorMessages {

        ERROR_COMMON_SERVER_ERROR(
                "66001",
                "Unable to complete operation.",
                "Error occurred while performing operation."),
        ERROR_UNABLE_TO_ADD_CONFIG(
                "66002",
                "Unable to add enterprise login configurations.",
                "Error occurred while trying to add enterprise login management configurations for %s"),
        ERROR_UNABLE_TO_GET_CONFIGS(
                "66003",
                "Unable to retrieve enterprise login configurations.",
                "Error occurred while trying to retrieve enterprise login management configurations for %s"),
        ERROR_UNABLE_TO_DELETE_CONFIGS(
                "66004",
                "Unable to delete enterprise login configurations.",
                "Error occurred while trying to delete enterprise login management configurations for %s"),
        ERROR_UNABLE_TO_UPDATE_CONFIGS(
                "66005",
                "Unable to update enterprise login configurations.",
                "Error occurred while trying to update enterprise login management configurations for %s"),
        ERROR_NO_CONFIGS_TO_UPDATE(
                "66007",
                "No enterprise login configurations found.",
                "No enterprise login configurations found for %s");


        private final String code;
        private final String message;
        private final String description;

        ErrorMessages(String code, String message, String description) {

            this.code = code;
            this.message = message;
            this.description = description;
        }

        public String getCode() {

            return ERROR_PREFIX + code;
        }

        public String getMessage() {

            return message;
        }

        public String getDescription() {

            return description;
        }

        @Override
        public String toString() {

            return code + " | " + message;
        }
    }
}
