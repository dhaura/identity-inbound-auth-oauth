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

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.base.MultitenantConstants;
import org.wso2.carbon.core.util.KeyStoreManager;
import org.wso2.carbon.identity.application.authentication.framework.model.AuthenticatedUser;
import org.wso2.carbon.identity.base.IdentityRuntimeException;
import org.wso2.carbon.identity.core.util.IdentityTenantUtil;
import org.wso2.carbon.identity.oauth.common.OAuthConstants;
import org.wso2.carbon.identity.oauth.common.exception.InvalidOAuthClientException;
import org.wso2.carbon.identity.oauth.config.OAuthServerConfiguration;
import org.wso2.carbon.identity.oauth.dao.OAuthAppDO;
import org.wso2.carbon.identity.oauth2.IdentityOAuth2Exception;
import org.wso2.carbon.identity.oauth2.dto.OAuth2AccessTokenRespDTO;
import org.wso2.carbon.identity.oauth2.model.RequestParameter;
import org.wso2.carbon.identity.oauth2.token.OAuthTokenReqMessageContext;
import org.wso2.carbon.identity.oauth2.token.bindings.TokenBinding;
import org.wso2.carbon.identity.oauth2.token.handlers.grant.AbstractAuthorizationGrantHandler;
import org.wso2.carbon.identity.oauth2.util.OAuth2Util;
import org.wso2.carbon.identity.oidc.session.OIDCSessionConstants;
import org.wso2.carbon.identity.oidc.session.util.OIDCSessionManagementUtil;
import org.wso2.carbon.identity.pat.core.service.bindings.impl.PATTokenBinder;
import org.wso2.carbon.identity.pat.core.service.common.PATUtil;
import org.wso2.carbon.identity.pat.core.service.dao.PATDAOFactory;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.core.common.AbstractUserStoreManager;
import org.wso2.carbon.user.core.common.User;
import org.wso2.carbon.user.core.common.UserUniqueIDManger;
import org.wso2.carbon.user.core.service.RealmService;
import org.wso2.carbon.utils.multitenancy.MultitenantUtils;

import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.util.Map;

public class PATHandler extends AbstractAuthorizationGrantHandler {
    private static final Log log = LogFactory.getLog(PATHandler.class);

    public static final String ALIAS = "alias";
    public static final String DESCRIPTION = "description";
    public static final String VALIDITY_PERIOD = "validity_period";
    public static final String ID_TOKEN_HINT = "id_token_hint";

    @Override
    public OAuth2AccessTokenRespDTO issue(OAuthTokenReqMessageContext tokReqMsgCtx) throws IdentityOAuth2Exception {
        RequestParameter[] parameters = tokReqMsgCtx.getOauth2AccessTokenReqDTO().getRequestParameters();

        String validityPeriod = getValueFromRequestParameters(parameters, VALIDITY_PERIOD);

        if (validityPeriod != null){
            tokReqMsgCtx.setValidityPeriod(Long.parseLong(validityPeriod));
        }

        addTokenBinding(tokReqMsgCtx);

        OAuth2AccessTokenRespDTO responseDTO = super.issue(tokReqMsgCtx);

        String alias;
        String description;

       alias = getValueFromRequestParameters(parameters, ALIAS);
       description = getValueFromRequestParameters(parameters, DESCRIPTION);

        if (alias != null && description != null) {
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
    public boolean validateGrant(OAuthTokenReqMessageContext tokReqMsgCtx)  throws IdentityOAuth2Exception {
        boolean validateGrant = super.validateGrant(tokReqMsgCtx);

        if (validateGrant){
            RequestParameter[] parameters = tokReqMsgCtx.getOauth2AccessTokenReqDTO().getRequestParameters();

            String idTokenHint;
            String tenantDomain;
            String userID;

            idTokenHint = getValueFromRequestParameters(parameters, ID_TOKEN_HINT);

            if (!OIDCSessionManagementUtil.isIDTokenEncrypted(idTokenHint)) {
                if (validateIdToken(idTokenHint)) {
                    try {
                        tenantDomain = extractTenantDomainFromIdToken(idTokenHint);
                        userID = SignedJWT.parse(idTokenHint).getJWTClaimsSet()
                                .getSubject();

                        AuthenticatedUser patAuthenticatedUser = getAuthenticatedUser(userID, tenantDomain);

                        if (patAuthenticatedUser != null) {
                            tokReqMsgCtx.setAuthorizedUser(patAuthenticatedUser);
                            tokReqMsgCtx.setScope(tokReqMsgCtx.getOauth2AccessTokenReqDTO().getScope());

                            return validateGrant;
                        }

                    } catch (ParseException e) {
                        if (log.isDebugEnabled()) {
                            log.debug("Error occurred while retrieving tenant domain from ID token", e);
                        }
                    }

                }else{
                    log.debug("ID token: "+idTokenHint+" is invalid.");
                }

            }else {
                log.debug("ID Token is encrypted.");
            }
        }
        return false;
    }

    private AuthenticatedUser getAuthenticatedUser(String userID, String tenantDomain){
        AbstractUserStoreManager userStoreManager = null;
        try {
            userStoreManager = getUserStoreManager(tenantDomain);
        } catch (IdentityOAuth2Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("Error occurred while retrieving user store manager:  " , e);
            }
        }

        if (userStoreManager != null){
            UserUniqueIDManger userUniqueIDManger = new UserUniqueIDManger();
            User user = null;
            try {
                user = userUniqueIDManger.getUser(userID, userStoreManager);
            } catch (UserStoreException e) {
                if (log.isDebugEnabled()) {
                    log.debug("Error occurred while extracting user from user id : " + userID, e);
                }
            }

            if (user != null){
                AuthenticatedUser patAuthenticatedUser = new AuthenticatedUser(user);
                patAuthenticatedUser.setTenantDomain(tenantDomain);

                return patAuthenticatedUser;
            }
        }

        return null;

    }

    private String extractClientFromIdToken(String idToken) throws ParseException {

        String clientId = (String) SignedJWT.parse(idToken).getJWTClaimsSet()
                .getClaims().get(OIDCSessionConstants.OIDC_ID_TOKEN_AZP_CLAIM);

        if (StringUtils.isBlank(clientId)) {
            clientId = SignedJWT.parse(idToken).getJWTClaimsSet().getAudience().get(0);
            log.info("Provided ID Token does not contain azp claim with client ID. " +
                    "Client ID is extracted from the aud claim in the ID Token.");
        }

        return clientId;
    }

    private String extractTenantDomainFromIdToken(String idToken) throws ParseException {

        String tenantDomain = null;
        Map realm = null;

        JWTClaimsSet claimsSet = SignedJWT.parse(idToken).getJWTClaimsSet();
        if (claimsSet.getClaims().get(OAuthConstants.OIDCClaims.REALM) instanceof Map) {
            realm = (Map) claimsSet.getClaims().get(OAuthConstants.OIDCClaims.REALM);
        }
        if (realm != null) {
            tenantDomain = (String) realm.get(OAuthConstants.OIDCClaims.TENANT);
        }
        if (StringUtils.isBlank(tenantDomain)) {
            if (log.isDebugEnabled()) {
                log.debug("Failed to retrieve tenant domain from 'realm' claim. Hence falling back to 'sub' claim.");
            }
            //It is not sending tenant domain with the subject in id_token by default, So to work this as
            //expected, need to enable the option "Use tenant domain in local subject identifier" in SP config
            tenantDomain = MultitenantUtils.getTenantDomain(claimsSet.getSubject());
            if (log.isDebugEnabled()) {
                log.debug("User tenant domain derived from 'sub' claim of JWT. Tenant domain : " + tenantDomain);
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("User tenant domain found in 'realm' claim of JWT. Tenant domain : " + tenantDomain);
            }
        }
        return tenantDomain;
    }

    private String getTenantDomainForSignatureValidation(String idToken) {

        boolean isJWTSignedWithSPKey = OAuthServerConfiguration.getInstance().isJWTSignedWithSPKey();
        if (log.isDebugEnabled()) {
            log.debug("'SignJWTWithSPKey' property is set to : " + isJWTSignedWithSPKey);
        }
        String tenantDomain;

        try {
            String clientId = extractClientFromIdToken(idToken);
            if (isJWTSignedWithSPKey) {
                OAuthAppDO oAuthAppDO = OAuth2Util.getAppInformationByClientId(clientId);
                tenantDomain = OAuth2Util.getTenantDomainOfOauthApp(oAuthAppDO);
                if (log.isDebugEnabled()) {
                    log.debug("JWT signature will be validated with the service provider's tenant domain : " +
                            tenantDomain);
                }
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("JWT signature will be validated with user tenant domain.");
                }
                tenantDomain = extractTenantDomainFromIdToken(idToken);
            }
        } catch (ParseException e) {
            if (log.isDebugEnabled()) {
                log.debug("Error occurred while extracting client id from id token: " + idToken, e);
            }
            return null;
        } catch (IdentityOAuth2Exception e) {
            log.error("Error occurred while getting oauth application information.", e);
            return null;
        } catch (InvalidOAuthClientException e) {
            if (log.isDebugEnabled()) {
                log.debug("Error occurred while getting tenant domain for signature validation with id token: "
                        + idToken, e);
            }
            return null;
        }
        return tenantDomain;
    }

    private boolean validateIdToken(String idToken) {

        String tenantDomain = getTenantDomainForSignatureValidation(idToken);
        if (StringUtils.isEmpty(tenantDomain)) {
            return false;
        }
        int tenantId = IdentityTenantUtil.getTenantId(tenantDomain);
        RSAPublicKey publicKey;

        try {
            KeyStoreManager keyStoreManager = KeyStoreManager.getInstance(tenantId);

            if (!tenantDomain.equals(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME)) {
                String ksName = tenantDomain.trim().replace(".", "-");
                String jksName = ksName + ".jks";
                publicKey = (RSAPublicKey) keyStoreManager.getKeyStore(jksName).getCertificate(tenantDomain)
                        .getPublicKey();
            } else {
                publicKey = (RSAPublicKey) keyStoreManager.getDefaultPublicKey();
            }
            SignedJWT signedJWT = SignedJWT.parse(idToken);
            JWSVerifier verifier = new RSASSAVerifier(publicKey);

            return signedJWT.verify(verifier);
        } catch (JOSEException | ParseException e) {
            if (log.isDebugEnabled()) {
                log.debug("Error occurred while validating id token signature for the id token: " + idToken);
            }
            return false;
        } catch (Exception e) {
            log.error("Error occurred while validating id token signature.");
            return false;
        }
    }

    private int getTenantId(String tenantDomain) throws IdentityOAuth2Exception {
        int tenantId;
        try {
            tenantId = IdentityTenantUtil.getTenantId(tenantDomain);
        } catch (IdentityRuntimeException e) {
            log.error("Token request with Password Grant Type for an invalid tenant : " + tenantDomain);
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
        tokenBinding.setBindingValue(String.valueOf(patTokenBinder.getTokenBindingValue(tokReqMsgCtx.getOauth2AccessTokenReqDTO())));
        tokenBinding.setBindingReference(OAuth2Util.getTokenBindingReference(tokenBinding.getBindingValue()));
        tokenBinding.setBindingType(patTokenBinder.getBindingType());

        tokReqMsgCtx.setTokenBinding(tokenBinding);
    }

    private String getValueFromRequestParameters(RequestParameter[] parameters, String key){
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

