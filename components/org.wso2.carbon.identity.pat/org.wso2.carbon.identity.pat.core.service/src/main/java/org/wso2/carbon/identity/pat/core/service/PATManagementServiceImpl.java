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
import org.wso2.carbon.identity.event.IdentityEventConstants;
import org.wso2.carbon.identity.event.IdentityEventException;
import org.wso2.carbon.identity.event.event.Event;
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
import org.wso2.carbon.identity.pat.core.service.exeptions.PATClientManagementException;
import org.wso2.carbon.identity.pat.core.service.exeptions.PATManagementException;
import org.wso2.carbon.identity.pat.core.service.exeptions.PATServerManagementException;
import org.wso2.carbon.identity.pat.core.service.internal.PATServiceComponentHolder;
import org.wso2.carbon.identity.pat.core.service.model.PATCreationData;
import org.wso2.carbon.identity.pat.core.service.model.PATData;
import org.wso2.carbon.identity.pat.core.service.model.PATViewMetadata;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

            PATUtil.startSuperTenantFlow();
            validatePATCreationData(patCreationData, userId);

            OAuth2AccessTokenReqDTO tokenReqDTO = buildAccessTokenReqDTO(patCreationData, userId);
            OAuth2AccessTokenRespDTO oauth2AccessTokenResp = PATUtil.getOAuth2Service().issueAccessToken(tokenReqDTO);

            if (oauth2AccessTokenResp.getErrorMsg() != null) {
                throw new PATServerManagementException(
                        PATConstants.ErrorMessage.ERROR_CREATING_PAT.getCode(),
                        oauth2AccessTokenResp.getErrorMsg());
            } else {
                PATData patData =
                        getPATCreationResponse(oauth2AccessTokenResp, patCreationData);
                try {
                    triggerEmail(username, patData.
                                    getAlias(), patData.getDescription(),
                            PATConstants.ASGARDEO_PAT_CREATION_EMAIL_TEMPLATE);
                } catch (IdentityEventException e) {
                    throw new PATServerManagementException(PATConstants.ErrorMessage.ERROR_CREATING_PAT);
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
            throw new PATClientManagementException(PATConstants.ErrorMessage.ERROR_CODE_EMPTY_TOKEN_ID);
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

        if (StringUtils.isNotBlank(tokenId)) {
            PATMgtDAO patMgtDAO = PATDAOFactory.getInstance().getPATMgtDAO();
            PATViewMetadata patViewMetadata = patMgtDAO.getPATMetadata(tokenId, userId);

            if (patViewMetadata != null) {
                try {
                    PATUtil.startSuperTenantFlow();

                    String accessToken = patMgtDAO.getPAT(tokenId);
                    String clientID = patMgtDAO.getClientIDFromTokenID(tokenId);

                    OAuthRevocationRequestDTO revokeRequest = buildOAuthRevocationRequest(accessToken, clientID);
                    OAuthRevocationResponseDTO oauthRevokeResp = PATUtil.getOAuth2Service()
                            .revokeTokenByOAuthClient(revokeRequest);

                    if (oauthRevokeResp.getErrorMsg() != null) {
                        throw new PATServerManagementException(
                                PATConstants.ErrorMessage.ERROR_CREATING_PAT.getCode(),
                                oauthRevokeResp.getErrorMsg());
                    } else {
                        triggerEmail(username, patViewMetadata.getAlias(), patViewMetadata.getDescription(),
                                PATConstants.ASGARDEO_PAT_REVOCATION_EMAIL_TEMPLATE);
                    }

                } catch (IdentityEventException e) {
                    throw new PATServerManagementException(PATConstants.ErrorMessage.ERROR_CREATING_PAT);
                } finally {
                    PrivilegedCarbonContext.endTenantFlow();
                }
            } else {
                throw new PATClientManagementException(PATConstants.ErrorMessage.ERROR_CODE_INVALID_TOKEN_ID);
            }
        } else {
            throw new PATClientManagementException(PATConstants.ErrorMessage.ERROR_CODE_EMPTY_TOKEN_ID);
        }

    }

    private void validatePATCreationData(PATCreationData patCreationData, String userId) throws PATManagementException {

        if (StringUtils.isNotBlank(patCreationData.getAlias())) {
            PATMgtDAO patMgtDAO = PATDAOFactory.getInstance().getPATMgtDAO();
            boolean isDuplicatedAlias = patMgtDAO.isDuplicatedAlias(userId, patCreationData.getAlias());
            if (isDuplicatedAlias) {
                throw new PATClientManagementException(PATConstants.ErrorMessage.ERROR_CODE_DUPLICATED_ALIAS);
            }
        } else {
            throw new PATClientManagementException(PATConstants.ErrorMessage.ERROR_CODE_EMPTY_ALIAS);
        }

        if (patCreationData.getValidityPeriod() <= 0) {
            throw new PATClientManagementException(PATConstants.ErrorMessage.
                    ERROR_CODE_INVALID_VALIDITY_PERIOD.getCode());
        }
        if (StringUtils.isBlank(patCreationData.getClientID())) {
            throw new PATClientManagementException(PATConstants.ErrorMessage.ERROR_CODE_EMPTY_CLIENT_ID);
        }
        if (patCreationData.getScope() != null && patCreationData.getScope().size() < 1) {
            throw new PATClientManagementException(PATConstants.ErrorMessage.ERROR_CODE_SCOPES_NOT_PRESENT);
        }
        validateScopes(patCreationData.getScope());
    }

    private void validateScopes(List<String> scopes) throws PATClientManagementException {

        Pattern pattern = Pattern.compile("^internal");
        Matcher matcher;
        for (String scope: scopes) {
            matcher = pattern.matcher(scope);
            if (!matcher.find()) {
                throw new PATClientManagementException(PATConstants.ErrorMessage.ERROR_CODE_INVALID_SCOPES);
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

    private void triggerEmail(String email, String alias, String description, String templateType)
            throws IdentityEventException {

        // TODO: 5/10/2022 Try again the email flow with username

        HashMap<String, Object> properties = new HashMap<>();
        String encodedEmail;
        try {
            encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new IdentityEventException("Error occurred while encoding email.", e);
        }
        properties.put(PATConstants.EMAIL, encodedEmail);
        properties.put(PATConstants.SEND_TO, email);
        // Email is always sent from the super tenant.
        properties.put(IdentityEventConstants.EventProperty.TENANT_DOMAIN,
                MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
        properties.put(PATConstants.ALIAS, alias);
        properties.put(PATConstants.DESCRIPTION, description);
        properties.put(IdentityEventConstants.EventProperty.NOTIFICATION_CHANNEL, PATConstants.EMAIL_CHANNEL);
        properties.put(PATConstants.TEMPLATE_TYPE, templateType);

        Event identityMgtEvent = new Event(IdentityEventConstants.Event.TRIGGER_NOTIFICATION, properties);
        PATServiceComponentHolder.getIdentityEventService().handleEvent(identityMgtEvent);
        if (log.isDebugEnabled()) {
            log.debug("PAT Creation email notification triggered for email: " + email);
        }
    }
}
