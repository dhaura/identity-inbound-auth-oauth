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
import org.wso2.carbon.identity.pat.core.service.exeptions.PATClientException;
import org.wso2.carbon.identity.pat.core.service.exeptions.PATException;
import org.wso2.carbon.identity.pat.core.service.internal.PATServiceComponentHolder;
import org.wso2.carbon.identity.pat.core.service.model.PATCreationReqDTO;
import org.wso2.carbon.identity.pat.core.service.model.PATCreationRespDTO;
import org.wso2.carbon.identity.pat.core.service.model.TokenMetadataDTO;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class PATManagementServiceImpl implements PATManagementService {

    private static final Log log = LogFactory.getLog(PATManagementServiceImpl.class);

    @Override
    public PATCreationRespDTO issuePAT(PATCreationReqDTO patCreationReqDTO) throws PATException {

        try {
            String userId = PATUtil.getUserID();
            String username = PATUtil.getUserName();

            PATUtil.startSuperTenantFlow();
            validateParams(patCreationReqDTO);
            // TODO: 5/9/2022 do we need to validate scopes? 

            OAuth2AccessTokenReqDTO tokenReqDTO = buildAccessTokenReqDTO(patCreationReqDTO, userId);
            OAuth2AccessTokenRespDTO oauth2AccessTokenResp = PATUtil.getOAuth2Service().issueAccessToken(tokenReqDTO);

            if (oauth2AccessTokenResp.getErrorMsg() != null) {
                // TODO: handle error
                return null;
            } else {
                PATCreationRespDTO patCreationRespDTO =
                        getPATCreationResponse(oauth2AccessTokenResp, patCreationReqDTO);
                try {
                    triggerEmail(username, patCreationRespDTO.
                            getAlias(), patCreationRespDTO.getDescription(), PATConstants.ASGARDEO_PAT_CREATION_EMAIL_TEMPLATE);
                } catch (IdentityEventException e) {
                    throw new RuntimeException(e);
                }
                return patCreationRespDTO;
            }
        } finally {
            PrivilegedCarbonContext.endTenantFlow();
        }

    }

    private void validateParams(PATCreationReqDTO patCreationReqDTO) throws PATException {
        if (StringUtils.isBlank(patCreationReqDTO.getAlias())) {
            throw new PATClientException(
                    PATConstants.ErrorMessage.ERROR_CODE_EMPTY_ALIAS.getCode(),
                    PATConstants.ErrorMessage.ERROR_CODE_EMPTY_ALIAS.getMessage());
        }
        if (patCreationReqDTO.getDescription() != null) {
            if (StringUtils.isBlank(patCreationReqDTO.getDescription())) {
                throw new PATClientException(
                        PATConstants.ErrorMessage.ERROR_CODE_EMPTY_DESCRIPTION.getCode(),
                        PATConstants.ErrorMessage.ERROR_CODE_EMPTY_DESCRIPTION.getMessage());
            }
        }
        if (patCreationReqDTO.getValidityPeriod() <= 0) {
            throw new PATClientException(
                    PATConstants.ErrorMessage.ERROR_CODE_INVALID_VALIDITY_PERIOD.getCode(),
                    PATConstants.ErrorMessage.ERROR_CODE_INVALID_VALIDITY_PERIOD.getMessage());
        }
        if (patCreationReqDTO.getScope().size() < 1) {
            throw new PATClientException(
                    PATConstants.ErrorMessage.ERROR_CODE_SCOPES_NOT_PRESENT.getCode(),
                    PATConstants.ErrorMessage.ERROR_CODE_SCOPES_NOT_PRESENT.getMessage());
        }
        if (StringUtils.isBlank(patCreationReqDTO.getClientID())) {
            throw new PATClientException(
                    PATConstants.ErrorMessage.ERROR_CODE_EMPTY_CLIENT_ID.getCode(),
                    PATConstants.ErrorMessage.ERROR_CODE_EMPTY_CLIENT_ID.getMessage());
        }
    }

    @Override
    public TokenMetadataDTO getTokenMetadata(String tokenId) throws PATException {
        String userId = PATUtil.getUserID();

        if (StringUtils.isNotBlank(tokenId)) {
            PATMgtDAO patMgtDAO = PATDAOFactory.getInstance().getPATMgtDAO();
            TokenMetadataDTO tokenMetadataDTO = patMgtDAO.getTokenMetadata(tokenId, userId);
            tokenMetadataDTO.setScope(patMgtDAO.getTokenScopes(tokenId));

            return tokenMetadataDTO;
        } else {
            throw new PATClientException(
                    PATConstants.ErrorMessage.ERROR_CODE_EMPTY_TOKEN_ID.getCode(),
                    PATConstants.ErrorMessage.ERROR_CODE_EMPTY_TOKEN_ID.getMessage());
        }
    }

    @Override
    public List<TokenMetadataDTO> getTokensMetadata() {

        String userId = PATUtil.getUserID();

        PATMgtDAO patMgtDAO = PATDAOFactory.getInstance().getPATMgtDAO();
        List<TokenMetadataDTO> tokenMetadataDTOList = patMgtDAO.getTokensMetadata(userId);

        return tokenMetadataDTOList;
    }

    @Override
    public void revokePAT(String tokenId) throws PATException {
        String userId = PATUtil.getUserID();
        String username = PATUtil.getUserName();

        PATMgtDAO patMgtDAO = PATDAOFactory.getInstance().getPATMgtDAO();
        TokenMetadataDTO tokenMetadataDTO = patMgtDAO.getTokenMetadata(tokenId, userId);

        if (tokenMetadataDTO != null) {
            try {
                PATUtil.startSuperTenantFlow();

                String accessToken = patMgtDAO.getAccessToken(tokenId);
                String clientID = patMgtDAO.getClientIDFromTokenID(tokenId);

                OAuthRevocationRequestDTO revokeRequest = buildOAuthRevocationRequest(accessToken, clientID);
                OAuthRevocationResponseDTO oauthRevokeResp = PATUtil.getOAuth2Service()
                        .revokeTokenByOAuthClient(revokeRequest);

                if (oauthRevokeResp.getErrorMsg() != null) {
                    // TODO: handle error
                } else {
                    triggerEmail(username, tokenMetadataDTO.getAlias(), tokenMetadataDTO.getDescription(),
                            PATConstants.ASGARDEO_PAT_REVOCATION_EMAIL_TEMPLATE);
                }

            } catch (IdentityEventException e) {
                // TODO: 5/10/2022 handle error
            } finally {
                PrivilegedCarbonContext.endTenantFlow();
            }
        } else {
            // TODO: handle error
        }


    }

    private OAuth2AccessTokenReqDTO buildAccessTokenReqDTO(PATCreationReqDTO patCreationReqDTO, String userId) {

        OAuth2AccessTokenReqDTO tokenReqDTO = new OAuth2AccessTokenReqDTO();

        OAuthClientAuthnContext oauthClientAuthnContext = new OAuthClientAuthnContext();
        oauthClientAuthnContext.setClientId(patCreationReqDTO.getClientID());
        oauthClientAuthnContext.setAuthenticated(true);
        oauthClientAuthnContext.addAuthenticator("BasicOAuthClientCredAuthenticator");
        tokenReqDTO.setoAuthClientAuthnContext(oauthClientAuthnContext);

        tokenReqDTO.setGrantType(PATConstants.PAT);
        tokenReqDTO.setClientId(patCreationReqDTO.getClientID());
        tokenReqDTO.setScope(patCreationReqDTO.getScope().toArray(new String[0]));
        // Set all request parameters to the OAuth2AccessTokenReqDTO
        tokenReqDTO.setRequestParameters(getRequestParameters(patCreationReqDTO, userId));

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

    private RequestParameter[] getRequestParameters(PATCreationReqDTO patCreationReqDTO, String userId) {

        RequestParameter[] requestParameters = new RequestParameter[5];
        requestParameters[0] = new RequestParameter(PATConstants.ALIAS, patCreationReqDTO.getAlias());
        requestParameters[1] = new RequestParameter(PATConstants.DESCRIPTION, patCreationReqDTO.getDescription());
        requestParameters[2] = new RequestParameter(PATConstants.VALIDITY_PERIOD, String.valueOf(patCreationReqDTO
                .getValidityPeriod()));
        requestParameters[3] = new RequestParameter(PATConstants.SCOPE, patCreationReqDTO.getScope()
                .toArray(new String[0]));
        requestParameters[4] = new RequestParameter(PATConstants.USER_ID, userId);

        return requestParameters;
    }

    private PATCreationRespDTO getPATCreationResponse(OAuth2AccessTokenRespDTO oauth2AccessTokenRespDTO,
                                                      PATCreationReqDTO patCreationReqDTO) {

        PATCreationRespDTO patCreationRespDTO = new PATCreationRespDTO();

        patCreationRespDTO.setTokenId(oauth2AccessTokenRespDTO.getTokenId());
        patCreationRespDTO.setAccessToken(oauth2AccessTokenRespDTO.getAccessToken());
        patCreationRespDTO.setAlias(patCreationReqDTO.getAlias());
        patCreationRespDTO.setDescription(patCreationReqDTO.getDescription());
        patCreationRespDTO.setValidityPeriod(oauth2AccessTokenRespDTO.getExpiresIn());
        patCreationRespDTO.setScope(Arrays.asList(oauth2AccessTokenRespDTO.getAuthorizedScopes().split(" ")));

        return patCreationRespDTO;
    }

    private void triggerEmail(String email, String  alias, String description, String templateType)
            throws IdentityEventException {

        // TODO: 5/10/2022 Try again the email flow with username
        // TODO: 5/10/2022 try out the new api def

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
