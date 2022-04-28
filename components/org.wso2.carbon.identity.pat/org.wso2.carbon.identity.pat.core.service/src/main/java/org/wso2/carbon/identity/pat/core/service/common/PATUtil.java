/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */
package org.wso2.carbon.identity.pat.core.service.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.identity.oauth.OAuthAdminService;
import org.wso2.carbon.identity.oauth2.OAuth2Service;
import org.wso2.carbon.user.core.service.RealmService;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;

/**
 * PAT util class.
 */
public class PATUtil {

    private static final Log log = LogFactory.getLog(PATUtil.class);

    /**
     * Get OAuthAdmin osgi service.
     *
     * @return OAuthAdminService
     */
    public static OAuthAdminService getOAuthServiceAdminService() {
        return (OAuthAdminService) PrivilegedCarbonContext.getThreadLocalCarbonContext()
                .getOSGiService(OAuthAdminService.class, null);
    }

    /**
     * Get realm service.
     *
     * @return RealmService
     */
    public static RealmService getRealmService() {
        return (RealmService) PrivilegedCarbonContext.getThreadLocalCarbonContext()
                .getOSGiService(RealmService.class, null);
    }

    /**
     * Get OAuth2 service.
     *
     * @return OAuth2Service
     */
    public static OAuth2Service getOAuth2Service() {
        return (OAuth2Service) PrivilegedCarbonContext.getThreadLocalCarbonContext()
                .getOSGiService(OAuth2Service.class, null);
    }

    /**
     * Get UserID.
     *
     * @return String
     */
    public static String getUserID() {
        return PrivilegedCarbonContext.getThreadLocalCarbonContext().getUserId();
    }

    /**
     * This method will start a super tenant flow
     */
    public static void startSuperTenantFlow() {

        PrivilegedCarbonContext.startTenantFlow();
        PrivilegedCarbonContext carbonContext = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        carbonContext.setTenantId(MultitenantConstants.SUPER_TENANT_ID);
        carbonContext.setTenantDomain(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
    }

}
