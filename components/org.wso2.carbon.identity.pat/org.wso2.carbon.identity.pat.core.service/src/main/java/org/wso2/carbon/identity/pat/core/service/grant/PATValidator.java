/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.identity.pat.core.service.grant;

import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.utils.OAuthUtils;
import org.apache.oltu.oauth2.common.validators.AbstractValidator;

import javax.servlet.http.HttpServletRequest;

/**
 * Grant validator for PAT Token Request.
 * For PAT Grant to be valid,
 * the required parameters are grant_type and expires_in.
 */
public class PATValidator extends AbstractValidator {

    public PATValidator() {
        requiredParams.add(PATHandler.ALIAS);
        requiredParams.add(PATHandler.DESCRIPTION);
        requiredParams.add(PATHandler.VALIDITY_PERIOD);
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
