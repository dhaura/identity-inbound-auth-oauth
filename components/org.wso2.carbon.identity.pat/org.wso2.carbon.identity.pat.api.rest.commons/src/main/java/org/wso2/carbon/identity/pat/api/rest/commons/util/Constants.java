/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package org.wso2.carbon.identity.pat.api.rest.commons.util;

/**
 * Constants to be used in PAT management API.
 */
public class Constants {

    public static final String CORRELATION_ID_MDC = "Correlation-ID";
    public static final String SUPER_TENANT = "carbon.super";
    private static final String ERROR_PREFIX = "IPM";


    /**
     * Enum for common server error messages.
     */
    public enum ErrorMessages {

        ERROR_COMMON_SERVER_ERROR(
                "96001",
                "Unable to complete operation.",
                "Error occurred while performing operation."),
        ERROR_UNABLE_TO_CREATE_PAT(
                "96002",
                "Unable to create personal access token.",
                "Error occurred while trying to create a personal access token for user: %s"),
        ERROR_UNABLE_TO_GET_PAT_METADATA(
                "96003",
                "Unable to retrieve personal access token metadata.",
                "Error occurred while trying to retrieve personal access token metadata for user: %s"),
        ERROR_UNABLE_TO_REVOKE_PAT(
                "96004",
                "Unable to revoke the personal access token.",
                "Error occurred while trying to revoke the personal access token for user: %s");



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
