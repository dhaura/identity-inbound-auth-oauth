/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package org.wso2.carbon.identity.pat.core.service;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.core.util.CryptoException;
import org.wso2.carbon.core.util.CryptoUtil;
import org.wso2.carbon.identity.event.IdentityEventConstants;
import org.wso2.carbon.identity.event.IdentityEventException;
import org.wso2.carbon.identity.event.event.Event;
import org.wso2.carbon.identity.oauth2.IdentityOAuth2Exception;
import org.wso2.carbon.identity.oauth2.bean.OAuthClientAuthnContext;
import org.wso2.carbon.identity.oauth2.dto.OAuth2AccessTokenReqDTO;
import org.wso2.carbon.identity.oauth2.dto.OAuth2AccessTokenRespDTO;
import org.wso2.carbon.identity.oauth2.dto.OAuthRevocationRequestDTO;
import org.wso2.carbon.identity.oauth2.dto.OAuthRevocationResponseDTO;
import org.wso2.carbon.identity.oauth2.model.RequestParameter;
import org.wso2.carbon.identity.pat.core.service.common.PATConstants;
import org.wso2.carbon.identity.pat.core.service.common.PATUtil;
import org.wso2.carbon.identity.pat.core.service.dao.PATDAOFactory;
import org.wso2.carbon.identity.pat.core.service.dao.PATMgtDAO;
import org.wso2.carbon.identity.pat.core.service.exeptions.PATManagementClientException;
import org.wso2.carbon.identity.pat.core.service.exeptions.PATManagementException;
import org.wso2.carbon.identity.pat.core.service.exeptions.PATManagementServerException;
import org.wso2.carbon.identity.pat.core.service.internal.PATServiceComponentHolder;
import org.wso2.carbon.identity.pat.core.service.model.PATCreationData;
import org.wso2.carbon.identity.pat.core.service.model.PATData;
import org.wso2.carbon.identity.pat.core.service.model.PATViewMetadata;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * PAT management service implementation.
 */
public class PATManagementServiceImpl implements PATManagementService {

    private static final Log log = LogFactory.getLog(PATManagementServiceImpl.class);

    @Override
    public PATData issueToken(PATCreationData patCreationData) throws PATManagementException {

        try {
            String userId = PATUtil.getUserIdFromContext();
            String username = PATUtil.getUserNameFromContext();
            String tenantDomain = PATUtil.getTenantDomainFromContext();

            PATUtil.startSuperTenantFlow();
            validatePATCreationData(patCreationData, userId);

            OAuth2AccessTokenReqDTO tokenReqDTO = buildAccessTokenReqDTO(patCreationData, userId);
            OAuth2AccessTokenRespDTO oauth2AccessTokenResp = PATServiceComponentHolder.getInstance()
                    .getOauth2Service().issueAccessToken(tokenReqDTO);

            if (oauth2AccessTokenResp.getErrorMsg() != null) {
                throw new PATManagementServerException(
                        PATConstants.ErrorMessage.ERROR_CREATING_PAT.getCode(),
                        oauth2AccessTokenResp.getErrorMsg());
            } else {
                PATData patData =
                        getPATCreationResponse(oauth2AccessTokenResp, patCreationData);
                try {
                    if (StringUtils.isNotBlank(tenantDomain)) {
                        String userStoreDomain = PATUtil.getAuthenticatedUser(userId, tenantDomain)
                                .getUserStoreDomain();

                        if (StringUtils.isNotBlank(userStoreDomain)) {
                            triggerEmail(username, patData.getAlias(), patData.getDescription(),
                                    userStoreDomain, tenantDomain,
                                    PATConstants.ASGARDEO_PAT_CREATION_EMAIL_TEMPLATE);
                        } else {
                            revokeToken(patData.getAccessToken(), patCreationData.getClientID());
                            handleEmailTriggeringError(true);
                        }
                    } else {
                        revokeToken(patData.getAccessToken(), patCreationData.getClientID());
                        handleEmailTriggeringError(true);
                    }
                } catch (IdentityOAuth2Exception e) {
                    throw new PATManagementServerException(PATConstants.ErrorMessage.ERROR_CREATING_PAT);
                } catch (IdentityEventException e) {
                    revokeToken(patData.getAccessToken(), patCreationData.getClientID());
                    handleEmailTriggeringError(true);
                }
                return patData;
            }
        } finally {
            PrivilegedCarbonContext.endTenantFlow();
        }

    }

    @Override
    public PATViewMetadata getTokenMetadata(String tokenId) throws PATManagementException {

        String userId = PATUtil.getUserIdFromContext();

        if (StringUtils.isNotBlank(tokenId)) {
            PATMgtDAO patMgtDAO = PATDAOFactory.getInstance().getPATMgtDAO();
            PATViewMetadata patViewMetadata = patMgtDAO.getPATMetadata(tokenId, userId);
            patViewMetadata.setScope(patMgtDAO.getPATScopes(tokenId));

            return patViewMetadata;
        } else {
            throw new PATManagementClientException(PATConstants.ErrorMessage.ERROR_CODE_EMPTY_TOKEN_ID);
        }
    }

    @Override
    public List<PATViewMetadata> getTokensMetadata() throws PATManagementException {

        String userId = PATUtil.getUserIdFromContext();

        PATMgtDAO patMgtDAO = PATDAOFactory.getInstance().getPATMgtDAO();
        List<PATViewMetadata> patViewMetadataList = patMgtDAO.getPATsMetadata(userId);

        return patViewMetadataList;
    }

    @Override
    public void revokeToken(String tokenId) throws PATManagementException {

        String userId = PATUtil.getUserIdFromContext();
        String username = PATUtil.getUserNameFromContext();
        String tenantDomain = PATUtil.getTenantDomainFromContext();

        if (StringUtils.isNotBlank(tokenId)) {
            PATMgtDAO patMgtDAO = PATDAOFactory.getInstance().getPATMgtDAO();
            PATViewMetadata patViewMetadata = patMgtDAO.getPATMetadata(tokenId, userId);

            if (patViewMetadata != null) {
                try {
                    PATUtil.startSuperTenantFlow();

                    String accessToken = new String(CryptoUtil.getDefaultCryptoUtil()
                            .base64DecodeAndDecrypt(patMgtDAO.getPAT(tokenId)));
                    String clientID = patMgtDAO.getClientIDFromTokenID(tokenId);

                    OAuthRevocationResponseDTO oauthRevokeResp = revokeToken(accessToken, clientID);

                    if (oauthRevokeResp.getErrorMsg() != null) {
                        throw new PATManagementServerException(
                                PATConstants.ErrorMessage.ERROR_REVOKING_PAT.getCode(),
                                oauthRevokeResp.getErrorMsg());
                    } else {
                        if (StringUtils.isNotBlank(tenantDomain)) {
                            String userStoreDomain = PATUtil.getAuthenticatedUser(userId, tenantDomain)
                                    .getUserStoreDomain();

                            if (StringUtils.isNotBlank(userStoreDomain)) {
                                triggerEmail(username, patViewMetadata.getAlias(), null,
                                        userStoreDomain, tenantDomain,
                                        PATConstants.ASGARDEO_PAT_REVOCATION_EMAIL_TEMPLATE);
                            } else {
                                handleEmailTriggeringError(false);
                            }
                        } else {
                            handleEmailTriggeringError(false);
                        }
                    }
                } catch (IdentityOAuth2Exception e) {
                    throw new PATManagementServerException(PATConstants.ErrorMessage.ERROR_CREATING_PAT);
                } catch (IdentityEventException e) {
                    handleEmailTriggeringError(false);
                } catch (CryptoException e) {
                    throw new RuntimeException(e);
                } finally {
                    PrivilegedCarbonContext.endTenantFlow();
                }
            } else {
                throw new PATManagementClientException(PATConstants.ErrorMessage.ERROR_CODE_INVALID_TOKEN_ID);
            }
        } else {
            throw new PATManagementClientException(PATConstants.ErrorMessage.ERROR_CODE_EMPTY_TOKEN_ID);
        }

    }

    private OAuthRevocationResponseDTO revokeToken(String accessToken, String clientID) {
        OAuthRevocationRequestDTO revokeRequest = buildOAuthRevocationRequest(accessToken, clientID);
        OAuthRevocationResponseDTO oauthRevokeResp = PATServiceComponentHolder.getInstance()
                .getOauth2Service().revokeTokenByOAuthClient(revokeRequest);
        return oauthRevokeResp;
    }

    private void validatePATCreationData(PATCreationData patCreationData, String userId) throws PATManagementException {

        if (StringUtils.isNotBlank(patCreationData.getAlias())) {
            PATMgtDAO patMgtDAO = PATDAOFactory.getInstance().getPATMgtDAO();
            boolean isDuplicatedAlias = patMgtDAO.isDuplicatedAlias(userId, patCreationData.getAlias());
            if (isDuplicatedAlias) {
                throw new PATManagementClientException(PATConstants.ErrorMessage.ERROR_CODE_DUPLICATED_ALIAS);
            }
        } else {
            throw new PATManagementClientException(PATConstants.ErrorMessage.ERROR_CODE_EMPTY_ALIAS);
        }

        if (patCreationData.getValidityPeriod() <= 0) {
            throw new PATManagementClientException(PATConstants.ErrorMessage.
                    ERROR_CODE_INVALID_VALIDITY_PERIOD.getCode());
        }
        if (StringUtils.isBlank(patCreationData.getClientID())) {
            throw new PATManagementClientException(PATConstants.ErrorMessage.ERROR_CODE_EMPTY_CLIENT_ID);
        }
        if (patCreationData.getScope() != null && patCreationData.getScope().size() < 1) {
            throw new PATManagementClientException(PATConstants.ErrorMessage.ERROR_CODE_SCOPES_NOT_PRESENT);
        }
        validateScopes(patCreationData.getScope());
    }

    private void validateScopes(List<String> scopes) throws PATManagementClientException {

        Pattern pattern = Pattern.compile("^internal");
        Matcher matcher;
        for (String scope: scopes) {
            matcher = pattern.matcher(scope);
            if (!matcher.find()) {
                throw new PATManagementClientException(PATConstants.ErrorMessage.ERROR_CODE_INVALID_SCOPES);
            }
        }
    }

    private OAuth2AccessTokenReqDTO buildAccessTokenReqDTO(PATCreationData patCreationData, String userId) {

        OAuth2AccessTokenReqDTO tokenReqDTO = new OAuth2AccessTokenReqDTO();

        OAuthClientAuthnContext oauthClientAuthnContext = new OAuthClientAuthnContext();
        oauthClientAuthnContext.setClientId(patCreationData.getClientID());
        oauthClientAuthnContext.setAuthenticated(true);
        oauthClientAuthnContext.addAuthenticator("BasicOAuthClientCredAuthenticator");
        tokenReqDTO.setoAuthClientAuthnContext(oauthClientAuthnContext);

        tokenReqDTO.setGrantType(PATConstants.PAT);
        tokenReqDTO.setClientId(patCreationData.getClientID());
        tokenReqDTO.setScope(patCreationData.getScope().toArray(new String[0]));
        tokenReqDTO.setRequestParameters(getRequestParameters(patCreationData, userId));

        tokenReqDTO.addAuthenticationMethodReference(PATConstants.PAT);

        return tokenReqDTO;
    }

    private OAuthRevocationRequestDTO buildOAuthRevocationRequest(String accessToken, String clientID) {

        OAuthRevocationRequestDTO oAuthRevocationRequestDTO = new OAuthRevocationRequestDTO();

        OAuthClientAuthnContext oauthClientAuthnContext = new OAuthClientAuthnContext();
        oauthClientAuthnContext.setClientId(clientID);
        oauthClientAuthnContext.setAuthenticated(true);
        oauthClientAuthnContext.addAuthenticator("BasicOAuthClientCredAuthenticator");

        oAuthRevocationRequestDTO.setOauthClientAuthnContext(oauthClientAuthnContext);
        oAuthRevocationRequestDTO.setConsumerKey(clientID);
        oAuthRevocationRequestDTO.setTokenType(PATConstants.PAT);
        oAuthRevocationRequestDTO.setToken(accessToken);

        return oAuthRevocationRequestDTO;
    }

    private RequestParameter[] getRequestParameters(PATCreationData patCreationData, String userId) {

        RequestParameter[] requestParameters = new RequestParameter[5];
        requestParameters[0] = new RequestParameter(PATConstants.ALIAS, patCreationData.getAlias());
        requestParameters[1] = new RequestParameter(PATConstants.DESCRIPTION, patCreationData.getDescription());
        requestParameters[2] = new RequestParameter(PATConstants.VALIDITY_PERIOD, String.valueOf(patCreationData
                .getValidityPeriod()));
        requestParameters[3] = new RequestParameter(PATConstants.SCOPE, patCreationData.getScope()
                .toArray(new String[0]));
        requestParameters[4] = new RequestParameter(PATConstants.USER_ID, userId);

        return requestParameters;
    }

    private PATData getPATCreationResponse(OAuth2AccessTokenRespDTO oauth2AccessTokenRespDTO,
                                           PATCreationData patCreationData) {

        PATData patData = new PATData();

        patData.setTokenId(oauth2AccessTokenRespDTO.getTokenId());
        patData.setAccessToken(oauth2AccessTokenRespDTO.getAccessToken());
        patData.setAlias(patCreationData.getAlias());
        patData.setDescription(patCreationData.getDescription());
        patData.setValidityPeriod(oauth2AccessTokenRespDTO.getExpiresIn());
        patData.setScope(Arrays.asList(oauth2AccessTokenRespDTO.getAuthorizedScopes().split(" ")));

        return patData;
    }

    private void triggerEmail(String username, String alias, String description,
                              String userStoreDomainName, String tenantDomain, String templateType)
            throws IdentityEventException {

        String eventName = IdentityEventConstants.Event.TRIGGER_NOTIFICATION;

        HashMap<String, Object> properties = new HashMap<>();
        properties.put(IdentityEventConstants.EventProperty.USER_NAME, username);
        properties.put(IdentityEventConstants.EventProperty.USER_STORE_DOMAIN, userStoreDomainName);
        properties.put(IdentityEventConstants.EventProperty.TENANT_DOMAIN, tenantDomain);
        properties.put(PATConstants.TEMPLATE_TYPE, templateType);
        properties.put(PATConstants.ALIAS, alias);
        if (templateType.equals(PATConstants.ASGARDEO_PAT_CREATION_EMAIL_TEMPLATE)) {
            if (StringUtils.isNotBlank(description)) {
                properties.put(PATConstants.DESCRIPTION, description);
            } else {
                properties.put(PATConstants.DESCRIPTION, "Can be used to access WSO2 REST APIs.");
            }
        }

        Event identityMgtEvent = new Event(eventName, properties);
        PATServiceComponentHolder.getInstance().getIdentityEventService().handleEvent(identityMgtEvent);
    }

    private void handleEmailTriggeringError(boolean isAtCreation)
            throws PATManagementServerException {

        if (isAtCreation) {
            throw new PATManagementServerException(PATConstants.ErrorMessage.ERROR_SENDING_CREATION_MAIL);
        } else {
            //We are not throwing any exception from here, because this event notification
            // should not break the main flow.
            String errorMsg =
                    "Error occurred while triggering mail notification after revocation of personal access token.";
            log.warn(errorMsg);
            if (log.isDebugEnabled()) {
                log.debug(errorMsg);
            }
        }
    }

}
