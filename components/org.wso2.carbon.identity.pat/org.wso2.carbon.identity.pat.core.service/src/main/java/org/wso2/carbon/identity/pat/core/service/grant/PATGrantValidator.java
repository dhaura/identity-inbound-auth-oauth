/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package org.wso2.carbon.identity.pat.core.service.grant;

import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.utils.OAuthUtils;
import org.apache.oltu.oauth2.common.validators.AbstractValidator;
import org.wso2.carbon.identity.pat.core.service.common.PATConstants;

import javax.servlet.http.HttpServletRequest;

/**
 * Grant validator for PAT Token Request.
 * For PAT Grant to be valid,
 * the required parameters are grant_type and expires_in.
 */
public class PATGrantValidator extends AbstractValidator {

    public PATGrantValidator() {
        requiredParams.add(PATConstants.ALIAS);
        requiredParams.add(PATConstants.VALIDITY_PERIOD);
        requiredParams.add(PATConstants.SCOPE);
        requiredParams.add(PATConstants.CLIENT_ID);
    }

    @Override
    public void validateContentType(HttpServletRequest request) throws OAuthProblemException {
        String contentType = request.getContentType();
        String expectedContentType = "application/json";
        if (!OAuthUtils.hasContentType(contentType, "application/json")) {
            throw OAuthUtils.handleBadContentTypeException("application/json");
        }
    }
}
