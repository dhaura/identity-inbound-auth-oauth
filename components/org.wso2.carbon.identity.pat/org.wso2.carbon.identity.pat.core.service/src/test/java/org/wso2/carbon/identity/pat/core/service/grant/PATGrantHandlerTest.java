/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package org.wso2.carbon.identity.pat.core.service.grant;

import org.apache.oltu.oauth2.as.issuer.OAuthIssuerImpl;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.wso2.carbon.identity.application.authentication.framework.model.AuthenticatedUser;
import org.wso2.carbon.identity.core.util.IdentityTenantUtil;
import org.wso2.carbon.identity.oauth.config.OAuthServerConfiguration;
import org.wso2.carbon.identity.oauth.dao.OAuthAppDO;
import org.wso2.carbon.identity.oauth2.dao.AccessTokenDAO;
import org.wso2.carbon.identity.oauth2.dao.AccessTokenDAOImpl;
import org.wso2.carbon.identity.oauth2.dao.OAuthTokenPersistenceFactory;
import org.wso2.carbon.identity.oauth2.dto.OAuth2AccessTokenReqDTO;
import org.wso2.carbon.identity.oauth2.dto.OAuth2AccessTokenRespDTO;
import org.wso2.carbon.identity.oauth2.model.AccessTokenDO;
import org.wso2.carbon.identity.oauth2.model.RequestParameter;
import org.wso2.carbon.identity.oauth2.token.OAuthTokenReqMessageContext;
import org.wso2.carbon.identity.oauth2.token.OauthTokenIssuerImpl;
import org.wso2.carbon.identity.oauth2.token.bindings.TokenBinding;
import org.wso2.carbon.identity.oauth2.util.OAuth2Util;
import org.wso2.carbon.identity.pat.core.service.common.PATConstants;
import org.wso2.carbon.identity.pat.core.service.common.PATUtil;
import org.wso2.carbon.identity.pat.core.service.dao.PATDAOFactory;
import org.wso2.carbon.identity.pat.core.service.dao.PATMgtDAOImpl;
import org.wso2.carbon.user.core.UserRealm;
import org.wso2.carbon.user.core.common.AbstractUserStoreManager;
import org.wso2.carbon.user.core.common.User;
import org.wso2.carbon.user.core.service.RealmService;

import static org.mockito.Matchers.*;
import static org.powermock.api.mockito.PowerMockito.*;
import static org.powermock.api.mockito.PowerMockito.mock;

@PrepareForTest({OAuthServerConfiguration.class, PATDAOFactory.class, PATUtil.class, IdentityTenantUtil.class, OAuth2Util.class, OAuthTokenPersistenceFactory.class})
public class PATGrantHandlerTest extends PowerMockTestCase {

    @Mock
    OAuthServerConfiguration oAuthServerConfiguration;

    private OAuthTokenReqMessageContext tokReqMsgCtx;
    private OAuth2AccessTokenReqDTO oAuth2AccessTokenReqDTO;
    private RealmService realmService;
    private UserRealm userRealm;
    private AbstractUserStoreManager userStoreManager;
    private PATDAOFactory patdaoFactory;
    private PATMgtDAOImpl patMgtDAO;
    private OAuthTokenPersistenceFactory oAuthTokenPersistenceFactory;

    private static final String[] scopes = {"internal_application_mgt_view", "internal_claim_meta_create"};
    private static final String USER_ID = "76d26bbe-9010-4fe2-bd76-a559cef192aa";
    private static final String CLIENT_ID = "JM6vFAVXAAHA1Jwhwnv7n4cNIP0a";
    private static final String TENANT_DOMAIN = "carbon.super";
    private static final String ACCESS_TOKEN = "4a34bf6d-783a-338f-900e-2d9e13c47526";
    private static final String ALIAS = "Test Alias";
    private static final String DESCRIPTION = "Test Description";


    @BeforeMethod
    public void setUp() throws Exception {
        mockStatic(OAuthServerConfiguration.class);
        when(OAuthServerConfiguration.getInstance()).thenReturn(oAuthServerConfiguration);

        tokReqMsgCtx = mock(OAuthTokenReqMessageContext.class);
        oAuth2AccessTokenReqDTO = mock(OAuth2AccessTokenReqDTO.class);
        realmService = mock(RealmService.class);
        userRealm = mock(UserRealm.class);
        userStoreManager = mock(AbstractUserStoreManager.class);

        mockStatic(OAuthTokenPersistenceFactory.class);
        oAuthTokenPersistenceFactory = mock(OAuthTokenPersistenceFactory.class);

        mockStatic(PATDAOFactory.class);
        patdaoFactory = mock(PATDAOFactory.class);
        patMgtDAO = mock(PATMgtDAOImpl.class);
    }

    @Test
    public void testIssueRefreshToken() throws Exception{
        PATGrantHandler patGrantHandler = new PATGrantHandler();
        boolean actual = patGrantHandler.issueRefreshToken();

        Assert.assertFalse(actual, "Should not issue a Refresh Token");

    }

    @Test
    public void testValidateGrant() throws Exception {
        when(tokReqMsgCtx.getOauth2AccessTokenReqDTO()).thenReturn(oAuth2AccessTokenReqDTO);

        RequestParameter[] parameters = new RequestParameter[1];
        parameters[0] = new RequestParameter(PATConstants.USER_ID, USER_ID);

        when(tokReqMsgCtx.getOauth2AccessTokenReqDTO().getRequestParameters()).thenReturn(parameters);
        when(tokReqMsgCtx.getOauth2AccessTokenReqDTO().getTenantDomain()).thenReturn(TENANT_DOMAIN);

        mockStatic(IdentityTenantUtil.class);
        when(IdentityTenantUtil.getTenantId(anyString())).thenReturn(-1234);

        mockStatic(PATUtil.class);
        when(PATUtil.getRealmService()).thenReturn(realmService);
        when(realmService.getTenantUserRealm(anyInt())).thenReturn(userRealm);
        when(realmService.getTenantUserRealm(anyInt()).getUserStoreManager()).thenReturn(userStoreManager);

        User user = new User();
        user.setUserID(USER_ID);
        user.setUsername("admin");
        user.setUserStoreDomain("PRIMARY");
        when(PATUtil.getUser(USER_ID, userStoreManager)).thenReturn(user);

        when(tokReqMsgCtx.getOauth2AccessTokenReqDTO().getScope()).thenReturn(scopes);

        PATGrantHandler patGrantHandler = new PATGrantHandler();
        boolean actual = patGrantHandler.validateGrant(tokReqMsgCtx);
        Assert.assertTrue(actual, "PAT grant validation should be successful");
    }

    @Test
    public void testIncorrectValidateGrant() throws Exception {
        when(tokReqMsgCtx.getOauth2AccessTokenReqDTO()).thenReturn(oAuth2AccessTokenReqDTO);

        RequestParameter[] parameters = new RequestParameter[1];
        parameters[0] = new RequestParameter(PATConstants.USER_ID, USER_ID);

        when(tokReqMsgCtx.getOauth2AccessTokenReqDTO().getRequestParameters()).thenReturn(parameters);
        when(tokReqMsgCtx.getOauth2AccessTokenReqDTO().getTenantDomain()).thenReturn(null);

        // check when tenant domain is unavailable
        PATGrantHandler patGrantHandler = new PATGrantHandler();
        boolean actual1 = patGrantHandler.validateGrant(tokReqMsgCtx);
        Assert.assertFalse(actual1, "PAT grant validation should be unsuccessful");

        // remove user id from request parameters
        parameters[0] = new RequestParameter("Test", "Test");

        // check when both user id and tenant domain are unavailable
        boolean actual2 = patGrantHandler.validateGrant(tokReqMsgCtx);
        Assert.assertFalse(actual2, "PAT grant validation should be unsuccessful");

        // add tenant domain to the request parameters
        when(tokReqMsgCtx.getOauth2AccessTokenReqDTO().getTenantDomain()).thenReturn(TENANT_DOMAIN);

        // check when user id is unavailable
        boolean actual3 = patGrantHandler.validateGrant(tokReqMsgCtx);
        Assert.assertFalse(actual3, "PAT grant validation should be unsuccessful");

        mockStatic(IdentityTenantUtil.class);
        when(IdentityTenantUtil.getTenantId(anyString())).thenReturn(-1234);

        mockStatic(PATUtil.class);
        when(PATUtil.getRealmService()).thenReturn(realmService);
        when(realmService.getTenantUserRealm(anyInt())).thenReturn(userRealm);
        when(realmService.getTenantUserRealm(anyInt()).getUserStoreManager()).thenReturn(null);

        // check when user store manager is unavailable
        boolean actual4 = patGrantHandler.validateGrant(tokReqMsgCtx);
        Assert.assertFalse(actual4, "PAT grant validation should be unsuccessful");

        when(realmService.getTenantUserRealm(anyInt()).getUserStoreManager()).thenReturn(null);
        when(PATUtil.getUser(USER_ID, userStoreManager)).thenReturn(null);

        // check when user is unavailable (user id is incorrect)
        boolean actual5 = patGrantHandler.validateGrant(tokReqMsgCtx);
        Assert.assertFalse(actual5, "PAT grant validation should be unsuccessful");
    }

    @Test
    public void testIssue() throws Exception{
        when(tokReqMsgCtx.getOauth2AccessTokenReqDTO()).thenReturn(oAuth2AccessTokenReqDTO);

        RequestParameter[] parameters = new RequestParameter[4];
        parameters[0] = new RequestParameter(PATConstants.USER_ID, USER_ID);
        parameters[1] = new RequestParameter(PATConstants.ALIAS, ALIAS);
        parameters[2] = new RequestParameter(PATConstants.DESCRIPTION, DESCRIPTION);
        parameters[3] = new RequestParameter(PATConstants.VALIDITY_PERIOD, "2000");
        when(tokReqMsgCtx.getOauth2AccessTokenReqDTO().getRequestParameters()).thenReturn(parameters);

        when(oAuth2AccessTokenReqDTO.getGrantType()).thenReturn("pat");
        when(tokReqMsgCtx.getOauth2AccessTokenReqDTO().getClientId()).thenReturn(CLIENT_ID);

        AuthenticatedUser authenticatedUser = new AuthenticatedUser();
        authenticatedUser.setUserId(USER_ID);
        when(tokReqMsgCtx.getAuthorizedUser()).thenReturn(authenticatedUser);
        mockStatic(OAuth2Util.class);
        when(OAuth2Util.getAuthenticatedIDP(tokReqMsgCtx.getAuthorizedUser())).thenReturn("LOCAL");

        TokenBinding tokenBinding = mock(TokenBinding.class);
        when(tokReqMsgCtx.getTokenBinding()).thenReturn(tokenBinding);
        when(tokReqMsgCtx.getTokenBinding().getBindingReference()).thenReturn("4035c2fecfeb2a8687ae4526580e1e37");

        when(OAuthServerConfiguration.getInstance().getOAuthTokenGenerator()).thenReturn(mock(OAuthIssuerImpl.class));
        OauthTokenIssuerImpl oauthTokenIssuer = mock(OauthTokenIssuerImpl.class);
        when(OAuth2Util.getOAuthTokenIssuerForOAuthApp(anyString())).thenReturn(oauthTokenIssuer);
        when(OAuth2Util.isHashDisabled()).thenReturn(false);

        OAuthAppDO oAuthAppBean = mock(OAuthAppDO.class);
        when(oAuthAppBean.getUserAccessTokenExpiryTime()).thenReturn(3600L);
        when(tokReqMsgCtx.getValidityPeriod()).thenReturn(2000L);
        when(tokReqMsgCtx.getOauth2AccessTokenReqDTO().getTenantDomain()).thenReturn(TENANT_DOMAIN);
        when(oAuth2AccessTokenReqDTO.getClientId()).thenReturn(CLIENT_ID);

        when(tokReqMsgCtx.getScope()).thenReturn(scopes);
        when(OAuth2Util.getTenantId(anyString())).thenReturn(-1234);
        when(oauthTokenIssuer.accessToken(tokReqMsgCtx)).thenReturn(ACCESS_TOKEN);
        when(OAuth2Util.checkUserNameAssertionEnabled()).thenReturn(false);

        when(OAuthServerConfiguration.getInstance().isTokenRenewalPerRequestEnabled()).thenReturn(false);
        when(tokReqMsgCtx.getRefreshTokenvalidityPeriod()).thenReturn(-1L);
        when(oAuthAppBean.getRefreshTokenExpiryTime()).thenReturn(8400L);
        when(oauthTokenIssuer.refreshToken(tokReqMsgCtx)).thenReturn("aa4f7279-3a8d-34db-a724-fb096d69af0e");

        when(OAuthTokenPersistenceFactory.getInstance()).thenReturn(oAuthTokenPersistenceFactory);
        AccessTokenDAO accessTokenDAO = mock(AccessTokenDAOImpl.class);
        when(OAuthTokenPersistenceFactory.getInstance().getAccessTokenDAO()).thenReturn(accessTokenDAO);
        when(OAuthTokenPersistenceFactory.getInstance().getAccessTokenDAO()
                .insertAccessToken(ACCESS_TOKEN, oAuth2AccessTokenReqDTO.getClientId(), mock(AccessTokenDO.class),
                        null, null)).thenReturn(true);
        when(OAuth2Util.getAppInformationByClientId(anyString())).thenReturn(oAuthAppBean);

        when(PATDAOFactory.getInstance()).thenReturn(patdaoFactory);
        when(PATDAOFactory.getInstance().getPATMgtDAO()).thenReturn(patMgtDAO);
        doNothing().when(patMgtDAO).insertPATData(anyString(), eq(ALIAS), eq(DESCRIPTION));

        PATGrantHandler patGrantHandler = new PATGrantHandler();
        OAuth2AccessTokenRespDTO actual = patGrantHandler.issue(tokReqMsgCtx);
        Assert.assertNotNull(actual);
        Assert.assertNotNull(actual.getAccessToken());
    }

}
