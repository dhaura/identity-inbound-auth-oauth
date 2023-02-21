/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package org.wso2.carbon.identity.pat.core.service.exeptions;

import org.wso2.carbon.identity.pat.core.service.common.PATConstants;

/**
 * PAT management server exception.
 */
public class PATManagementServerException extends PATManagementException {

    private String errorCode = null;

    /**
     * Constructor with message.
     *
     * @param message Error message.
     */
    public PATManagementServerException(String message) {

        super(message);
    }

    /**
     * Constructor with message and error code.
     *
     * @param message   Error message.
     * @param errorCode Error code.
     */
    public PATManagementServerException(String errorCode, String message) {

        super(message);
        this.errorCode = errorCode;
    }

    /**
     * Constructor with Enum.
     *
     * @param error   Enum that contains error code and message.
     */
    public PATManagementServerException(PATConstants.ErrorMessage error) {

        super(error.getMessage());
        this.errorCode = error.getCode();
    }

    /**
     * Constructor with message, error cause and error code.
     *
     * @param message   Error message.
     * @param errorCode Error code.
     * @param cause     If any error occurred when accessing the tenant.
     */
    public PATManagementServerException(String errorCode, String message, Throwable cause) {

        super(message, cause);
        this.errorCode = errorCode;
    }

    /**
     * Constructor with error cause and error message.
     *
     * @param message Error message.
     * @param cause   If any error occurred when deleting the tenant.
     */
    public PATManagementServerException(String message, Throwable cause) {

        super(message, cause);
    }

    /**
     * Constructor with error cause.
     *
     * @param cause If any error occurred when accessing the tenant.
     */
    public PATManagementServerException(Throwable cause) {

        super(cause);
    }

    public String getErrorCode() {

        return errorCode;
    }
}


