/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package org.wso2.carbon.identity.pat.api.rest.commons;

import org.wso2.carbon.identity.pat.core.service.PATManagementService;

public class PATApiMgtDataHolder {
    private static PATManagementService patManagementService ;

    public static PATManagementService getPatManagementService() {
        return patManagementService;
    }

    public static void setPatManagementService(PATManagementService patManagementService) {
        PATApiMgtDataHolder.patManagementService = patManagementService;
    }
}
