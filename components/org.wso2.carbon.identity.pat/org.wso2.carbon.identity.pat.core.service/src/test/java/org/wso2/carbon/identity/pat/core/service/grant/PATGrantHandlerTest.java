/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package org.wso2.carbon.identity.pat.core.service.grant;

import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.powermock.reflect.internal.WhiteboxImpl;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.wso2.carbon.identity.application.authentication.framework.util.FrameworkUtils;
import org.wso2.carbon.identity.common.testng.WithH2Database;
import org.wso2.carbon.identity.core.util.IdentityTenantUtil;
import org.wso2.carbon.identity.multi.attribute.login.mgt.ResolvedUserResult;
import org.wso2.carbon.identity.oauth.config.OAuthServerConfiguration;
import org.wso2.carbon.identity.oauth.internal.OAuthComponentServiceHolder;
import org.wso2.carbon.identity.oauth2.dto.OAuth2AccessTokenReqDTO;
import org.wso2.carbon.identity.oauth2.internal.OAuth2ServiceComponentHolder;
import org.wso2.carbon.identity.oauth2.model.RequestParameter;
import org.wso2.carbon.identity.oauth2.token.OAuthTokenReqMessageContext;
import org.wso2.carbon.identity.pat.core.service.common.PATConstants;
import org.wso2.carbon.identity.pat.core.service.common.PATUtil;
import org.wso2.carbon.identity.pat.core.service.dao.PATDAOFactory;
import org.wso2.carbon.identity.pat.core.service.dao.PATMgtDAO;
import org.wso2.carbon.identity.testutil.powermock.PowerMockIdentityBaseTest;
import org.wso2.carbon.user.core.UserRealm;
import org.wso2.carbon.user.core.common.AbstractUserStoreManager;
import org.wso2.carbon.user.core.common.User;
import org.wso2.carbon.user.core.common.UserUniqueIDManger;
import org.wso2.carbon.user.core.service.RealmService;
import org.wso2.carbon.user.core.util.UserCoreUtil;
import org.wso2.carbon.utils.multitenancy.MultitenantUtils;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.*;
import static org.powermock.api.mockito.PowerMockito.mock;

//@WithH2Database(files = {"dbScripts/identity.sql"})
@PrepareForTest({OAuthServerConfiguration.class, PATUtil.class, IdentityTenantUtil.class})
public class PATGrantHandlerTest extends PowerMockTestCase {

    @Mock
    OAuthServerConfiguration oAuthServerConfiguration;

    private OAuthTokenReqMessageContext tokReqMsgCtx;
    private OAuth2AccessTokenReqDTO oAuth2AccessTokenReqDTO;
    private RealmService realmService;
    private UserRealm userRealm;
    private AbstractUserStoreManager userStoreManager;

    private static final String USER_ID = "76d26bbe-9010-4fe2-bd76-a559cef192aa";


    @BeforeMethod
    public void setUp() throws Exception {
        mockStatic(OAuthServerConfiguration.class);
        when(OAuthServerConfiguration.getInstance()).thenReturn(oAuthServerConfiguration);

        tokReqMsgCtx = mock(OAuthTokenReqMessageContext.class);
        oAuth2AccessTokenReqDTO = mock(OAuth2AccessTokenReqDTO.class);
        realmService = mock(RealmService.class);
        userRealm = mock(UserRealm.class);
        userStoreManager = mock(AbstractUserStoreManager.class);
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

        RequestParameter[] parameters = new RequestParameter[4];
        parameters[0] = new RequestParameter(PATConstants.USER_ID, USER_ID);
        parameters[1] = new RequestParameter(PATConstants.ALIAS, "Test Alias");
        parameters[2] = new RequestParameter(PATConstants.DESCRIPTION, "Test Description");
        parameters[3] = new RequestParameter(PATConstants.VALIDITY_PERIOD, "2000");

        when(tokReqMsgCtx.getOauth2AccessTokenReqDTO().getRequestParameters()).thenReturn(parameters);
        when(tokReqMsgCtx.getOauth2AccessTokenReqDTO().getTenantDomain()).thenReturn("carbon.super");

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

        String[] scopes = {"internal_application_mgt_view"};
        when(tokReqMsgCtx.getOauth2AccessTokenReqDTO().getScope()).thenReturn(scopes);

        PATGrantHandler patGrantHandler = new PATGrantHandler();
        boolean actual = patGrantHandler.validateGrant(tokReqMsgCtx);
        Assert.assertTrue(actual, "PAT grant validation should be successful");
    }

}
