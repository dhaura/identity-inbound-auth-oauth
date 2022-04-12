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
