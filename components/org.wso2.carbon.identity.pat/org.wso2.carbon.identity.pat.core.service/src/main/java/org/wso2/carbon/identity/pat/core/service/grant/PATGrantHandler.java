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
import org.wso2.carbon.identity.base.IdentityRuntimeException;
import org.wso2.carbon.identity.core.util.IdentityTenantUtil;
import org.wso2.carbon.identity.oauth2.IdentityOAuth2Exception;
import org.wso2.carbon.identity.oauth2.dto.OAuth2AccessTokenRespDTO;
import org.wso2.carbon.identity.oauth2.model.RequestParameter;
import org.wso2.carbon.identity.oauth2.token.OAuthTokenReqMessageContext;
import org.wso2.carbon.identity.oauth2.token.bindings.TokenBinding;
import org.wso2.carbon.identity.oauth2.token.handlers.grant.AbstractAuthorizationGrantHandler;
import org.wso2.carbon.identity.oauth2.util.OAuth2Util;
import org.wso2.carbon.identity.pat.core.service.bindings.impl.PATTokenBinder;
import org.wso2.carbon.identity.pat.core.service.common.PATConstants;
import org.wso2.carbon.identity.pat.core.service.common.PATUtil;
import org.wso2.carbon.identity.pat.core.service.dao.PATDAOFactory;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.core.common.AbstractUserStoreManager;
import org.wso2.carbon.user.core.common.User;
import org.wso2.carbon.user.core.common.UserUniqueIDManger;
import org.wso2.carbon.user.core.service.RealmService;

public class PATGrantHandler extends AbstractAuthorizationGrantHandler {

    private static final Log log = LogFactory.getLog(PATGrantHandler.class);

    @Override
    public OAuth2AccessTokenRespDTO issue(OAuthTokenReqMessageContext tokReqMsgCtx) throws IdentityOAuth2Exception {

        RequestParameter[] parameters = tokReqMsgCtx.getOauth2AccessTokenReqDTO().getRequestParameters();

        String validityPeriod = getValueFromRequestParameters(parameters, PATConstants.VALIDITY_PERIOD);
        if (StringUtils.isNotBlank(validityPeriod)) {
            tokReqMsgCtx.setValidityPeriod(Long.parseLong(validityPeriod));
        }
        addTokenBinding(tokReqMsgCtx);

        OAuth2AccessTokenRespDTO responseDTO = super.issue(tokReqMsgCtx);

        String alias = getValueFromRequestParameters(parameters, PATConstants.ALIAS);
        String description = getValueFromRequestParameters(parameters, PATConstants.DESCRIPTION);

        if (StringUtils.isNotBlank(alias) && StringUtils.isNotBlank(description)) {
            PATDAOFactory.getInstance().getPATMgtDAO()
                    .insertPATData(responseDTO.getTokenId(), alias, description);
        } else {
            log.info("Alias or Description is not provided for the PAT.");
        }

        return responseDTO;
    }

    @Override
    public boolean issueRefreshToken() throws IdentityOAuth2Exception {

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
                AuthenticatedUser patAuthenticatedUser = getAuthenticatedUser(userID, tenantDomain);

                if (patAuthenticatedUser != null) {
                    tokReqMsgCtx.setAuthorizedUser(patAuthenticatedUser);
                    tokReqMsgCtx.setScope(tokReqMsgCtx.getOauth2AccessTokenReqDTO().getScope());

                    return true;
                }
            } else {
                log.info("Tenant Domain or User ID is not provided for the PAT.");
            }


        }
        return false;
    }

    private AuthenticatedUser getAuthenticatedUser(String userID, String tenantDomain) {

        AbstractUserStoreManager userStoreManager = null;
        try {
            userStoreManager = getUserStoreManager(tenantDomain);
        } catch (IdentityOAuth2Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("Error occurred while retrieving user store manager:  ", e);
            }
        }

        if (userStoreManager != null) {
            UserUniqueIDManger userUniqueIDManger = new UserUniqueIDManger();
            User user = null;
            try {
                user = userUniqueIDManger.getUser(userID, userStoreManager);
            } catch (UserStoreException e) {
                if (log.isDebugEnabled()) {
                    log.debug("Error occurred while extracting user from user id : " + userID, e);
                }
            }

            if (user != null) {
                AuthenticatedUser patAuthenticatedUser = new AuthenticatedUser(user);
                patAuthenticatedUser.setTenantDomain(tenantDomain);

                return patAuthenticatedUser;
            }
        }

        return null;

    }

    private int getTenantId(String tenantDomain) throws IdentityOAuth2Exception {

        int tenantId;
        try {
            tenantId = IdentityTenantUtil.getTenantId(tenantDomain);
        } catch (IdentityRuntimeException e) {
            log.error("Token request with PAT Grant Type for an invalid tenant : " + tenantDomain);
            throw new IdentityOAuth2Exception(e.getMessage(), e);
        }
        return tenantId;
    }

    private AbstractUserStoreManager getUserStoreManager(String tenantDomain)
            throws IdentityOAuth2Exception {

        int tenantId = getTenantId(tenantDomain);
        RealmService realmService = PATUtil.getRealmService();
        AbstractUserStoreManager userStoreManager;
        try {
            userStoreManager
                    = (AbstractUserStoreManager) realmService.getTenantUserRealm(tenantId).getUserStoreManager();
        } catch (UserStoreException e) {
            throw new IdentityOAuth2Exception(e.getMessage(), e);
        }

        return userStoreManager;
    }

    private void addTokenBinding(OAuthTokenReqMessageContext tokReqMsgCtx) {

        PATTokenBinder patTokenBinder = new PATTokenBinder();
        TokenBinding tokenBinding = new TokenBinding();
        tokenBinding.setBindingValue(String.valueOf(patTokenBinder
                .getTokenBindingValue(tokReqMsgCtx.getOauth2AccessTokenReqDTO())));
        tokenBinding.setBindingReference(OAuth2Util.getTokenBindingReference(tokenBinding.getBindingValue()));
        tokenBinding.setBindingType(patTokenBinder.getBindingType());

        tokReqMsgCtx.setTokenBinding(tokenBinding);
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
}

