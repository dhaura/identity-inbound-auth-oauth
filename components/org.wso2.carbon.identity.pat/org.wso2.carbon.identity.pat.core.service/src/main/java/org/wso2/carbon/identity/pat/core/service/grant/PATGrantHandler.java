/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package org.wso2.carbon.identity.pat.core.service.grant;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.application.authentication.framework.model.AuthenticatedUser;
import org.wso2.carbon.identity.event.IdentityEventConstants;
import org.wso2.carbon.identity.oauth2.IdentityOAuth2Exception;
import org.wso2.carbon.identity.oauth2.bean.OAuthClientAuthnContext;
import org.wso2.carbon.identity.oauth2.dto.OAuth2AccessTokenRespDTO;
import org.wso2.carbon.identity.oauth2.dto.OAuthRevocationRequestDTO;
import org.wso2.carbon.identity.oauth2.model.RequestParameter;
import org.wso2.carbon.identity.oauth2.token.OAuthTokenReqMessageContext;
import org.wso2.carbon.identity.oauth2.token.handlers.grant.AbstractAuthorizationGrantHandler;
import org.wso2.carbon.identity.pat.core.service.common.PATConstants;
import org.wso2.carbon.identity.pat.core.service.common.PATUtil;
import org.wso2.carbon.identity.pat.core.service.dao.PATDAOFactory;
import org.wso2.carbon.identity.pat.core.service.exeptions.PATManagementServerException;
import org.wso2.carbon.identity.pat.core.service.internal.PATServiceComponentHolder;

/**
 * PAT Grant Handler for the custom PAT grant type.
 */
public class PATGrantHandler extends AbstractAuthorizationGrantHandler {

    private static final Log log = LogFactory.getLog(PATGrantHandler.class);

    /**
     * Issues Personal Access Token and persists its alias and description.
     *
     * @param tokReqMsgCtx Data related to the PAT creation.
     * @return OAuth2AccessTokenRespDTO  Data containing the new Personal Access Token and its attributes.
     * @throws IdentityOAuth2Exception Error when generating or persisting the access token and its attributes
     */
    @Override
    public OAuth2AccessTokenRespDTO issue(OAuthTokenReqMessageContext tokReqMsgCtx) throws IdentityOAuth2Exception {

        RequestParameter[] parameters = tokReqMsgCtx.getOauth2AccessTokenReqDTO().getRequestParameters();

        String validityPeriod = getValueFromRequestParameters(parameters, PATConstants.VALIDITY_PERIOD);
        if (StringUtils.isNotBlank(validityPeriod)) {
            tokReqMsgCtx.setValidityPeriod(Long.parseLong(validityPeriod));
        }

        OAuth2AccessTokenRespDTO responseDTO = super.issue(tokReqMsgCtx);

        String alias = getValueFromRequestParameters(parameters, PATConstants.ALIAS);
        String description = getValueFromRequestParameters(parameters, PATConstants.DESCRIPTION);

        try {
            PATDAOFactory.getInstance().getPATMgtDAO()
                        .insertPATData(responseDTO.getTokenId(), alias, description);
        } catch (PATManagementServerException e) {
            String clientId = tokReqMsgCtx.getOauth2AccessTokenReqDTO().getClientId();
            OAuthClientAuthnContext oAuthClientAuthnContext = tokReqMsgCtx.getOauth2AccessTokenReqDTO()
                    .getoAuthClientAuthnContext();
            String accessToken = responseDTO.getAccessToken();

            OAuthRevocationRequestDTO oAuthRevocationRequestDTO
                    = buildOAuthRevocationRequest(accessToken, clientId, oAuthClientAuthnContext);
            PATServiceComponentHolder.getInstance().getOauth2Service()
                    .revokeTokenByOAuthClient(oAuthRevocationRequestDTO);

            throw new IdentityOAuth2Exception(e.getMessage(), e);
        }

        responseDTO.addParameter(IdentityEventConstants.EventProperty.USER_STORE_DOMAIN,
                tokReqMsgCtx.getAuthorizedUser().getUserStoreDomain());
        responseDTO.addParameter(IdentityEventConstants.EventProperty.TENANT_DOMAIN,
                tokReqMsgCtx.getAuthorizedUser().getTenantDomain());

        return responseDTO;
    }

    /**
     * Tells if the refresh tokens are issued for PAT.
     *
     * @return boolean  Returns false since refresh tokens are not needed for PATs.
     */
    @Override
    public boolean issueRefreshToken() {

        return false;
    }

    @Override
    public boolean validateGrant(OAuthTokenReqMessageContext tokReqMsgCtx) throws IdentityOAuth2Exception {

        boolean validateGrant = super.validateGrant(tokReqMsgCtx);

        if (validateGrant) {
            RequestParameter[] parameters = tokReqMsgCtx.getOauth2AccessTokenReqDTO().getRequestParameters();

            String tenantDomain = tokReqMsgCtx.getOauth2AccessTokenReqDTO().getTenantDomain();
            String userID = getValueFromRequestParameters(parameters, PATConstants.USER_ID);

            if (StringUtils.isNotBlank(tenantDomain) && StringUtils.isNotBlank(userID)) {
                AuthenticatedUser patAuthenticatedUser = PATUtil.getAuthenticatedUser(userID, tenantDomain);

                if (patAuthenticatedUser != null) {
                    tokReqMsgCtx.setAuthorizedUser(patAuthenticatedUser);
                    tokReqMsgCtx.setScope(tokReqMsgCtx.getOauth2AccessTokenReqDTO().getScope());

                    return true;
                }
            }
        }
        return false;
    }

    private String getValueFromRequestParameters(RequestParameter[] parameters, String key) {

        String value = null;
        for (RequestParameter parameter : parameters) {
            if (key.equals(parameter.getKey())) {
                if (parameter.getValue() != null && parameter.getValue().length > 0) {
                    value = parameter.getValue()[0];
                }
                break;
            }
        }
        return value;
    }

    private OAuthRevocationRequestDTO buildOAuthRevocationRequest
            (String accessToken, String clientID, OAuthClientAuthnContext oauthClientAuthnContext) {

        OAuthRevocationRequestDTO oAuthRevocationRequestDTO = new OAuthRevocationRequestDTO();

        oAuthRevocationRequestDTO.setOauthClientAuthnContext(oauthClientAuthnContext);
        oAuthRevocationRequestDTO.setConsumerKey(clientID);
        oAuthRevocationRequestDTO.setTokenType(PATConstants.PAT);
        oAuthRevocationRequestDTO.setToken(accessToken);

        return oAuthRevocationRequestDTO;
    }
}

