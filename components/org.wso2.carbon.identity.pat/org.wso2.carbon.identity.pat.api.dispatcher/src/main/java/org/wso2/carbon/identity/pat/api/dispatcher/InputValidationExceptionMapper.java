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
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.util.Set;

/**
 * Map input validation exceptions.
 */
public class InputValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    private static final Log log = LogFactory.getLog(InputValidationExceptionMapper.class);

    private static final String ERROR_CODE = "ELM-65000";
    private static final String ERROR_MESSAGE = "Invalid Request";
    private static final String ERROR_DESCRIPTION = "Provided request body content is not in the expected format";

    @Override
    public Response toResponse(ConstraintViolationException e) {

        StringBuilder description = new StringBuilder();
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        for (ConstraintViolation constraintViolation : constraintViolations) {
            if (StringUtils.isNotBlank(description)) {
                description.append(" ");
            }
            description.append(constraintViolation.getMessage());
        }
        if (StringUtils.isBlank(description)) {
            description = new StringBuilder(ERROR_DESCRIPTION);
        }

        ErrorDTO errorDTO = new ErrorResponse.Builder()
                .withCode(ERROR_CODE)
                .withMessage(ERROR_MESSAGE)
                .withDescription(description.toString())
                .build(log, e.getMessage(), true);
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(errorDTO)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON).build();
    }
}
