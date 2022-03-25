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

package org.wso2.carbon.identity.custom.grant;

import com.nimbusds.jwt.SignedJWT;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.application.authentication.framework.model.AuthenticatedUser;
import org.wso2.carbon.identity.oauth2.IdentityOAuth2Exception;
import org.wso2.carbon.identity.oauth2.model.RequestParameter;
import org.wso2.carbon.identity.oauth2.token.OAuthTokenReqMessageContext;
import org.wso2.carbon.identity.oauth2.token.handlers.grant.AbstractAuthorizationGrantHandler;

import java.text.ParseException;

public class MobileGrantHandler extends AbstractAuthorizationGrantHandler {
    private static final Log log = LogFactory.getLog(MobileGrantHandler.class);

    public static final String MOBILE_NUMBER = "mobile_number";
    public static final String ID_TOKEN_HINT = "id_token_hint";

    @Override
    public boolean validateGrant(OAuthTokenReqMessageContext tokReqMsgCtx)  throws IdentityOAuth2Exception {
        boolean validateGrant = super.validateGrant(tokReqMsgCtx);

        if (validateGrant){
            RequestParameter[] parameters = tokReqMsgCtx.getOauth2AccessTokenReqDTO().getRequestParameters();

            String mobileNumber = null;
            String userID = null;
            String idTokenHint = null;

            for (RequestParameter parameter : parameters) {
                if (MOBILE_NUMBER.equals(parameter.getKey())) {
                    if (parameter.getValue() != null && parameter.getValue().length > 0) {
                        mobileNumber = parameter.getValue()[0];
                    }
                }
                if (ID_TOKEN_HINT.equals(parameter.getKey())) {
                    if (parameter.getValue() != null && parameter.getValue().length > 0) {
                        idTokenHint = parameter.getValue()[0];
                    }
                }
            }

            if (mobileNumber != null && idTokenHint != null && isValidMobileNumber(mobileNumber)){
                AuthenticatedUser mobileUser = new AuthenticatedUser();
                try {
                    userID = SignedJWT.parse(idTokenHint).getJWTClaimsSet()
                            .getSubject();
                } catch (ParseException e) {
                    if (log.isDebugEnabled()) {
                        log.debug("Error occurred while retrieving user ID from ID token", e);
                    }
                }
                mobileUser.setUserId(userID);
                mobileUser.setUserName(mobileNumber);
                mobileUser.setAuthenticatedSubjectIdentifier(mobileNumber);
                mobileUser.setFederatedUser(false);
                mobileUser.setTenantDomain("carbon.super");
                mobileUser.setUserStoreDomain("PRIMARY");
                tokReqMsgCtx.setAuthorizedUser(mobileUser);
                tokReqMsgCtx.setScope(tokReqMsgCtx.getOauth2AccessTokenReqDTO().getScope());

                return true;
            }
        }

        return false;
    }

    /**
     *
     * This method should be implemented as per requirement
     *
     * @param mobileNumber
     * @return
     */
    private boolean isValidMobileNumber(String mobileNumber){

        // just demo validation

        if(mobileNumber.startsWith("011")){
            return true;
        }

        return false;
    }
}
