/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package org.wso2.carbon.identity.pat.api.dispatcher;

import org.wso2.carbon.identity.pat.api.rest.commons.exceptions.ErrorDTO;
import org.wso2.carbon.identity.pat.api.rest.commons.exceptions.ErrorResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * Handles all the unhandled server errors, (ex:NullPointer).
 * Sends a default error response.
 */
public class DefaultExceptionMapper implements ExceptionMapper<Throwable> {

    public static final String PROCESSING_ERROR_CODE = "ELM-65000";
    public static final String PROCESSING_ERROR_MESSAGE = "Unexpected Processing Error";
    public static final String PROCESSING_ERROR_DESCRIPTION = "Server encountered an error while serving the request";
    private static final Log log = LogFactory.getLog(DefaultExceptionMapper.class);

    @Override
    public Response toResponse(Throwable e) {

        log.error("Server encountered an error while serving the request", e);
        ErrorDTO errorDTO = new ErrorResponse.Builder().withCode(PROCESSING_ERROR_CODE)
                .withMessage(PROCESSING_ERROR_MESSAGE)
                .withDescription(PROCESSING_ERROR_DESCRIPTION).build();
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(errorDTO)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON).build();
    }
}
