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
import org.wso2.carbon.identity.application.authentication.framework.model.AuthenticatedUser;
import org.wso2.carbon.identity.base.IdentityRuntimeException;
import org.wso2.carbon.identity.core.util.IdentityTenantUtil;
import org.wso2.carbon.identity.oauth2.IdentityOAuth2Exception;
import org.wso2.carbon.identity.pat.core.service.internal.PATServiceComponentHolder;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.core.common.AbstractUserStoreManager;
import org.wso2.carbon.user.core.common.User;
import org.wso2.carbon.user.core.common.UserUniqueIDManger;
import org.wso2.carbon.user.core.service.RealmService;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;

/**
 * PAT util class.
 */
public class PATUtil {

    private static final Log log = LogFactory.getLog(PATUtil.class);

    /**
     * Get UserID.
     *
     * @return String
     */
    public static String getUserIdFromContext() {
        return PrivilegedCarbonContext.getThreadLocalCarbonContext().getUserId();
    }

    /**
     * Get UserName.
     *
     * @return String
     */
    public static String getUserNameFromContext() {
        return PrivilegedCarbonContext.getThreadLocalCarbonContext().getUsername();
    }

    /**
     * Get Tenant Domain.
     *
     * @return String
     */
    public static String getTenantDomainFromContext() {
        return PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantDomain();
    }


    /**
     * This method will start a super tenant flow.
     */
    public static void startSuperTenantFlow() {

        PrivilegedCarbonContext.startTenantFlow();
        PrivilegedCarbonContext carbonContext = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        carbonContext.setTenantId(MultitenantConstants.SUPER_TENANT_ID);
        carbonContext.setTenantDomain(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
    }

    /**
     * This method will return the relevant user from the UserUniqueStoreManager.
     *
     * @param userID           User ID of the user.
     * @param userStoreManager User Store Manager related to the user.
     *
     * @return User            Object related to the user ID.
     * @throws UserStoreException
     */
    public static User getUser(String userID, AbstractUserStoreManager userStoreManager) throws UserStoreException {
        UserUniqueIDManger userUniqueIDManger = new UserUniqueIDManger();
        User user = userUniqueIDManger.getUser(userID, userStoreManager);

        return user;
    }

    /**
     * This method will return the relevant authorized user from the UserStoreManager.
     *
     * @param userID       User ID of the user.
     * @param tenantDomain Tenant Domain.
     *
     * @return AuthenticatedUser   Object related to the authorized user.
     * @throws IdentityOAuth2Exception
     */
    public static AuthenticatedUser getAuthenticatedUser(String userID, String tenantDomain)
            throws IdentityOAuth2Exception {

        AbstractUserStoreManager userStoreManager = getUserStoreManager(tenantDomain);

        if (userStoreManager != null) {
            User user;
            try {
                user = getUser(userID, userStoreManager);
            } catch (UserStoreException e) {
                if (log.isDebugEnabled()) {
                    log.debug("Error occurred while extracting user from user id : " + userID, e);
                }
                throw new IdentityOAuth2Exception("Error occurred while extracting user from user id : " + userID, e);
            }

            if (user != null) {
                AuthenticatedUser patAuthenticatedUser = new AuthenticatedUser(user);
                patAuthenticatedUser.setTenantDomain(tenantDomain);

                return patAuthenticatedUser;
            }
        }
        return null;
    }

    private static int getTenantId(String tenantDomain) throws IdentityOAuth2Exception {

        int tenantId;
        try {
            tenantId = IdentityTenantUtil.getTenantId(tenantDomain);
        } catch (IdentityRuntimeException e) {
            log.error("Token request with PAT Grant Type for an invalid tenant : " + tenantDomain);
            throw new IdentityOAuth2Exception(e.getMessage(), e);
        }
        return tenantId;
    }

    private static AbstractUserStoreManager getUserStoreManager(String tenantDomain)
            throws IdentityOAuth2Exception {

        int tenantId = getTenantId(tenantDomain);
        RealmService realmService = PATServiceComponentHolder.getInstance().getRealmService();
        AbstractUserStoreManager userStoreManager;

        try {
            userStoreManager
                    = (AbstractUserStoreManager) realmService.getTenantUserRealm(tenantId).getUserStoreManager();
        } catch (UserStoreException e) {
            throw new IdentityOAuth2Exception(e.getMessage(), e);
        }

        return userStoreManager;
    }

}
