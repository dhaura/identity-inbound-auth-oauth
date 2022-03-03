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

package org.wso2.carbon.identity.pat.grant;

import org.wso2.carbon.identity.oauth2.IdentityOAuth2Exception;
import org.wso2.carbon.identity.oauth2.ResponseHeader;
import org.wso2.carbon.identity.oauth2.dto.OAuth2AccessTokenRespDTO;
import org.wso2.carbon.identity.oauth2.token.OAuthTokenReqMessageContext;
import org.wso2.carbon.identity.oauth2.token.handlers.grant.AbstractAuthorizationGrantHandler;
import org.wso2.carbon.identity.oauth2.model.RequestParameter;
import org.wso2.carbon.identity.pat.dao.PATDAOFactory;

import java.util.UUID;

public class PATHandler extends AbstractAuthorizationGrantHandler{
    public static final String ALIAS = "alias";
    public static final String DESCRIPTION = "description";

    @Override
    public OAuth2AccessTokenRespDTO issue(OAuthTokenReqMessageContext tokReqMsgCtx) throws IdentityOAuth2Exception {
        OAuth2AccessTokenRespDTO responseDTO = super.issue(tokReqMsgCtx);
        RequestParameter[] parameters = tokReqMsgCtx.getOauth2AccessTokenReqDTO().getRequestParameters();

        String alias = null;
        String description = null;

        for(RequestParameter parameter : parameters){
            if(ALIAS.equals(parameter.getKey())){
                if(parameter.getValue() != null && parameter.getValue().length > 0){
                    alias = parameter.getValue()[0];
                }
            }
            if(DESCRIPTION.equals(parameter.getKey())){
                if(parameter.getValue() != null && parameter.getValue().length > 0){
                    description = parameter.getValue()[0];
                }
            }
        }

        if (alias != null && description != null){
            PATDAOFactory.getInstance().getPATMgtDAO()
                    .insertPATData(responseDTO.getTokenId(), alias, description);
        }else{
           //if alias or description is not provided
        }

        return responseDTO;
    }

    @Override
    public boolean issueRefreshToken() throws IdentityOAuth2Exception {
        return false;
    }
}
