package org.wso2.carbon.identity.pat.core.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.PrivilegedCarbonContext;
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
import org.wso2.carbon.identity.pat.core.service.model.PATCreationReqDTO;
import org.wso2.carbon.identity.pat.core.service.model.PATCreationRespDTO;
import org.wso2.carbon.identity.pat.core.service.model.TokenMetadataDTO;


import java.util.Arrays;
import java.util.List;


public class PATManagementServiceImpl implements PATManagementService{

    private static final Log log = LogFactory.getLog(PATManagementServiceImpl.class);

    @Override
    public PATCreationRespDTO issuePAT(PATCreationReqDTO patCreationReqDTO) {

        try{
            PATUtil.startSuperTenantFlow();

            OAuth2AccessTokenReqDTO tokenReqDTO = buildAccessTokenReqDTO(patCreationReqDTO);
            OAuth2AccessTokenRespDTO oauth2AccessTokenResp = PATUtil.getOAuth2Service().issueAccessToken(tokenReqDTO);

            if (oauth2AccessTokenResp.getErrorMsg() != null) {
                // TODO: handle error
                return null;
            } else {
                return getPATCreationResponse(oauth2AccessTokenResp, patCreationReqDTO);
            }
        }finally {
            PrivilegedCarbonContext.endTenantFlow();

        }

    }

    @Override
    public TokenMetadataDTO getTokenMetadata(String tokenId) {
        PATMgtDAO patMgtDAO = PATDAOFactory.getInstance().getPATMgtDAO();
        TokenMetadataDTO tokenMetadataDTO = patMgtDAO.getTokenMetadata(tokenId);
        tokenMetadataDTO.setScope(patMgtDAO.getTokenScopes(tokenId));

        return tokenMetadataDTO;
    }

    @Override
    public List<TokenMetadataDTO> getTokensMetadata() {
        String userId = PATUtil.getUserID();

        PATMgtDAO patMgtDAO = PATDAOFactory.getInstance().getPATMgtDAO();
        List<TokenMetadataDTO> tokenMetadataDTOList = patMgtDAO.getTokensMetadata(userId);

        return tokenMetadataDTOList;
    }

    @Override
    public void revokePAT(String tokenId) {
        try{
            PATUtil.startSuperTenantFlow();

            PATMgtDAO patMgtDAO = PATDAOFactory.getInstance().getPATMgtDAO();
            String accessToken = patMgtDAO.getAccessToken(tokenId);
            String clientID = patMgtDAO.getClientIDFromTokenID(tokenId);

            OAuthRevocationRequestDTO revokeRequest = buildOAuthRevocationRequest(accessToken, clientID);
            OAuthRevocationResponseDTO oauthRevokeResp = PATUtil.getOAuth2Service().revokeTokenByOAuthClient(revokeRequest);

            if (oauthRevokeResp.getErrorMsg() != null) {
                // TODO: handle error
            }

        }finally {
            PrivilegedCarbonContext.endTenantFlow();

        }
    }

    private OAuth2AccessTokenReqDTO buildAccessTokenReqDTO(PATCreationReqDTO patCreationReqDTO) {

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
        tokenReqDTO.setRequestParameters(getRequestParameters(patCreationReqDTO));

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


    private RequestParameter[] getRequestParameters(PATCreationReqDTO patCreationReqDTO){
        RequestParameter[] requestParameters = new RequestParameter[5];
        requestParameters[0] = new RequestParameter(PATConstants.ALIAS, patCreationReqDTO.getAlias());
        requestParameters[1] = new RequestParameter(PATConstants.DESCRIPTION, patCreationReqDTO.getDescription());
        requestParameters[2] = new RequestParameter(PATConstants.VALIDITY_PERIOD, String.valueOf(patCreationReqDTO.getValidityPeriod()));
        requestParameters[3] = new RequestParameter(PATConstants.ID_TOKEN_HINT, patCreationReqDTO.getIdTokenHint());
        requestParameters[4] = new RequestParameter(PATConstants.SCOPE, patCreationReqDTO.getScope().toArray(new String[0]));

        return requestParameters;
    }

    private PATCreationRespDTO getPATCreationResponse(OAuth2AccessTokenRespDTO oauth2AccessTokenRespDTO, PATCreationReqDTO patCreationReqDTO){
        PATCreationRespDTO patCreationRespDTO = new PATCreationRespDTO();

        patCreationRespDTO.setTokenId(oauth2AccessTokenRespDTO.getTokenId());
        patCreationRespDTO.setAccessToken(oauth2AccessTokenRespDTO.getAccessToken());
        patCreationRespDTO.setAlias(patCreationReqDTO.getAlias());
        patCreationRespDTO.setDescription(patCreationReqDTO.getDescription());
        patCreationRespDTO.setValidityPeriod(oauth2AccessTokenRespDTO.getExpiresIn());
        patCreationRespDTO.setScope(Arrays.asList(oauth2AccessTokenRespDTO.getAuthorizedScopes().split(" ")));

        return patCreationRespDTO;
    }
}
