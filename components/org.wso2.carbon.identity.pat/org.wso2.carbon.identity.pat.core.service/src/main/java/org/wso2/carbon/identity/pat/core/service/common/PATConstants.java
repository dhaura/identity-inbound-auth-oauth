/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package org.wso2.carbon.identity.pat.core.service.common;

/**
 * Class for constants and error messages in PAT core service.
 */
public class PATConstants {

    public static final String PAT = "pat";
    public static final String TOKEN_ID = "token_id";
    public static final String ALIAS = "alias";
    public static final String DESCRIPTION = "description";
    public static final String VALIDITY_PERIOD = "validity_period";
    public static final String TIME_CREATED = "time_created";
    public static final String SCOPE = "scope";
    public static final String TOKEN_SCOPE = "token_scope";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String INBOUND_AUTH_KEY = "inbound_auth_key";
    public static final String USER_ID = "user_id";
    public static final String CLIENT_ID = "client_id";

    // constants used for sending an email
    public static final String EMAIL = "email";
    public static final String SEND_TO = "send-to";
    public static final String TEMPLATE_TYPE = "TEMPLATE_TYPE";
    public static final String ASGARDEO_PAT_CREATION_EMAIL_TEMPLATE = "AsgardeoPATCreation";
    public static final String ASGARDEO_PAT_REVOCATION_EMAIL_TEMPLATE = "AsgardeoPATRevocation";
    public static final String EMAIL_CHANNEL = "EMAIL";

    /**
     * Enum for PAT management related errors.
     * Error Code - code to identify the error.
     * Error Message - What went wrong.
     */
    public enum ErrorMessage {

        ERROR_CODE_EMPTY_ALIAS("IPM-95001", "Alias parameter cannot be empty."),
        ERROR_CODE_DUPLICATED_ALIAS("IPM-95002", "Alias already exists."),
        ERROR_CODE_INVALID_VALIDITY_PERIOD("IPM-95003",
                "Validity period cannot be empty and should contain a positive long value."),
        ERROR_CODE_SCOPES_NOT_PRESENT("IPM-95004", "At least one scope should be present."),
        ERROR_CODE_INVALID_SCOPES("IPM-95005", "Scopes should be valid."),

        ERROR_CODE_EMPTY_CLIENT_ID("IPM-95006", "Client ID parameter cannot be empty."),
        ERROR_CODE_EMPTY_TOKEN_ID("IPM-95007", "Token ID path parameter cannot be empty."),
        ERROR_CODE_INVALID_TOKEN_ID("IPM-95008", "Token ID path parameter should be valid."),
        ERROR_CREATING_PAT("IPM-95009", "Token ID path parameter should be valid."),
        ERROR_RETRIEVING_TOKEN_METADATA("IPM-95010", "Error occurred while retrieving token metadata."),
        ERROR_RETRIEVING_TOKEN_SCOPES("IPM-95011", "Error occurred while retrieving token scopes."),
        ERROR_RETRIEVING_CLIENT_ID("IPM-95012", "Error occurred while retrieving client ID."),

        ERROR_RETRIEVING_PAT("IPM-95013", "Error occurred while retrieving personal access token."),
        ERROR_VALIDATING_DUPLICATED_ALIAS("IPM-95014", "Error occurred while validating alias for duplication.");

        private final String code;
        private final String message;

        ErrorMessage(String code, String message) {

            this.code = code;
            this.message = message;
        }

        public String getCode() {

            return code;
        }

        public String getMessage() {

            return message;
        }

        @Override
        public String toString() {

            return code + " | " + message;
        }
    }
}
