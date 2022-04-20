package org.wso2.carbon.identity.pat.core.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.identity.oauth2.bean.OAuthClientAuthnContext;
import org.wso2.carbon.identity.oauth2.dto.OAuth2AccessTokenReqDTO;
import org.wso2.carbon.identity.oauth2.dto.OAuth2AccessTokenRespDTO;
import org.wso2.carbon.identity.oauth2.model.RequestParameter;
import org.wso2.carbon.identity.pat.core.service.common.PATConstants;
import org.wso2.carbon.identity.pat.core.service.common.PATUtil;
import org.wso2.carbon.identity.pat.core.service.model.PATCreationData;
import org.wso2.carbon.identity.pat.core.service.model.PATCreationResponseData;


import java.util.Arrays;




public class PATManagementServiceImpl implements PATManagementService{

    private static final Log log = LogFactory.getLog(PATManagementServiceImpl.class);

    @Override
    public PATCreationResponseData issuePAT(PATCreationData patCreationData) {

        log.info("PAT issue service");

        try{
            PATUtil.startSuperTenantFlow();

            OAuth2AccessTokenReqDTO tokenReqDTO = buildAccessTokenReqDTO(patCreationData);
            OAuth2AccessTokenRespDTO oauth2AccessTokenResp = PATUtil.getOAuth2Service().issueAccessToken(tokenReqDTO);

            if (oauth2AccessTokenResp.getErrorMsg() != null) {
                // TODO: handle error
                return null;
            } else {
                return getPATCreationResponse(oauth2AccessTokenResp);
            }
        }finally {
            PrivilegedCarbonContext.endTenantFlow();

        }

    }

    private OAuth2AccessTokenReqDTO buildAccessTokenReqDTO(PATCreationData patCreationData) {

        OAuth2AccessTokenReqDTO tokenReqDTO = new OAuth2AccessTokenReqDTO();

        OAuthClientAuthnContext oauthClientAuthnContext = new OAuthClientAuthnContext();
        oauthClientAuthnContext.setClientId(patCreationData.getClientID());
        oauthClientAuthnContext.setAuthenticated(true);
        oauthClientAuthnContext.addAuthenticator("BasicOAuthClientCredAuthenticator");
        tokenReqDTO.setoAuthClientAuthnContext(oauthClientAuthnContext);

        String grantType = "pat";
        tokenReqDTO.setGrantType(grantType);
        tokenReqDTO.setClientId(patCreationData.getClientID());
        tokenReqDTO.setScope(patCreationData.getScope().toArray(new String[0]));
        // Set all request parameters to the OAuth2AccessTokenReqDTO
        tokenReqDTO.setRequestParameters(getRequestParameters(patCreationData));

        tokenReqDTO.addAuthenticationMethodReference(grantType);

        return tokenReqDTO;
    }

    private RequestParameter[] getRequestParameters(PATCreationData patCreationData){
        RequestParameter[] requestParameters = new RequestParameter[5];
        requestParameters[0] = new RequestParameter(PATConstants.ALIAS, patCreationData.getAlias());
        requestParameters[1] = new RequestParameter(PATConstants.DESCRIPTION, patCreationData.getDescription());
        requestParameters[2] = new RequestParameter(PATConstants.VALIDITY_PERIOD, String.valueOf(patCreationData.getValidityPeriod()));
        requestParameters[3] = new RequestParameter(PATConstants.ID_TOKEN_HINT, patCreationData.getIdTokenHint());
        requestParameters[4] = new RequestParameter(PATConstants.SCOPE, patCreationData.getScope().toArray(new String[0]));

        return requestParameters;
    }

    private PATCreationResponseData getPATCreationResponse(OAuth2AccessTokenRespDTO oauth2AccessTokenRespDTO){
        PATCreationResponseData patCreationResponseData = new PATCreationResponseData();
        patCreationResponseData.setAccessToken(oauth2AccessTokenRespDTO.getAccessToken());
        patCreationResponseData.setValidityPeriod(oauth2AccessTokenRespDTO.getExpiresIn());
        patCreationResponseData.setScope(Arrays.asList(oauth2AccessTokenRespDTO.getAuthorizedScopes().split(" ")));
        return patCreationResponseData;
    }
}
