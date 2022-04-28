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
import org.wso2.carbon.identity.oauth2.OAuth2Service;
import org.wso2.carbon.user.core.service.RealmService;

public class PATServiceComponentHolder {

    private static PATServiceComponent instance = new PATServiceComponent();
    private RealmService realmService;
    private OAuth2Service oAuth2Service;

    private static final Log log = LogFactory.getLog(PATServiceComponentHolder.class);

    public static PATServiceComponent getInstance() {

        return instance;

    }

    public RealmService getRealmService() {

        return realmService;
    }

    public void setRealmService(RealmService realmService) {

        this.realmService = realmService;
    }

    public OAuth2Service getOauth2Service() {

        return oAuth2Service;

    }

    public void setOauth2Service(OAuth2Service oAuth2Service) {

        this.oAuth2Service = oAuth2Service;

    }

}
