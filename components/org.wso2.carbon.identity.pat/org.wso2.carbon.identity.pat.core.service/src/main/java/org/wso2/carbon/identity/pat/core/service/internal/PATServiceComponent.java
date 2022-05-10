/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package org.wso2.carbon.identity.pat.core.service.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.*;
import org.wso2.carbon.identity.event.services.IdentityEventService;
import org.wso2.carbon.identity.oauth.common.token.bindings.TokenBinderInfo;
import org.wso2.carbon.identity.oauth2.OAuth2Service;
import org.wso2.carbon.identity.pat.core.service.PATManagementService;
import org.wso2.carbon.identity.pat.core.service.PATManagementServiceImpl;
import org.wso2.carbon.identity.pat.core.service.bindings.impl.PATTokenBinder;
import org.wso2.carbon.user.core.service.RealmService;


@Component(
        name = "identity.pat.component",
        immediate = true
)

public class PATServiceComponent {
    private static final Log log = LogFactory.getLog(PATServiceComponent.class);

    @Activate
    protected void activate(BundleContext bundleContext) {
        log.info("PAT Service Component Activation");

        PATTokenBinder patTokenBinder = new PATTokenBinder();
        bundleContext.registerService(TokenBinderInfo.class.getName(), patTokenBinder, null);

        bundleContext.registerService(PATManagementService.class.getName(), new PATManagementServiceImpl(), null);
    }

    protected void setRealmService(RealmService realmService) {

        if (log.isDebugEnabled()) {
            log.debug("Setting the Realm Service");
        }
        PATServiceComponentHolder.getInstance().setRealmService(realmService);
    }

    protected void unsetRealmService(RealmService realmService) {

        if (log.isDebugEnabled()) {
            log.debug("Unsetting the Realm Service");
        }
        PATServiceComponentHolder.getInstance().setRealmService(null);
    }

    protected void setOAuth2Service(OAuth2Service oAuth2Service) {

        if (log.isDebugEnabled()) {
            log.debug("Setting the OAuth2 Service");
        }
        PATServiceComponentHolder.getInstance().setOAuth2Service(oAuth2Service);
    }

    protected void unsetOAuth2Service(OAuth2Service oAuth2Service) {

        if (log.isDebugEnabled()) {
            log.debug("Unsetting the OAuth2 Service");
        }
        PATServiceComponentHolder.getInstance().setOAuth2Service(null);
    }

    @Reference(
            name = "IdentityEventService",
            service = org.wso2.carbon.identity.event.services.IdentityEventService.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetIdentityEventService")
    protected void setIdentityEventService(IdentityEventService identityEventService) {

        if (log.isDebugEnabled()) {
            log.debug("Setting IdentityEventService to UserInviteManagementService service.");
        }
        PATServiceComponentHolder.setIdentityEventService(identityEventService);
    }

    protected void unsetIdentityEventService(IdentityEventService identityEventService) {

        if (log.isDebugEnabled()) {
            log.debug("Unsetting IdentityEventService from UserInviteManagementService service.");
        }
        PATServiceComponentHolder.setIdentityEventService(null);
    }

}
