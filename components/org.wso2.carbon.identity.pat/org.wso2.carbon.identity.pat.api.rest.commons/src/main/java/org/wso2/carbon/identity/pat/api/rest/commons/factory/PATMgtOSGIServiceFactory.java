/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package org.wso2.carbon.identity.pat.api.rest.commons.factory;

import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.identity.pat.core.service.PATManagementService;

public class PATMgtOSGIServiceFactory extends AbstractFactoryBean<PATManagementService> {
    private PATManagementService patManagementService;

    @Override
    public Class<?> getObjectType() {

        return Object.class;
    }

    @Override
    protected PATManagementService createInstance() throws Exception {

        if (this.patManagementService == null) {
            PATManagementService patManagementService = (PATManagementService)
                    PrivilegedCarbonContext.getThreadLocalCarbonContext()
                            .getOSGiService(PATManagementService.class, null);
            if (patManagementService != null) {
                this.patManagementService = patManagementService;
            } else {
                throw new Exception("Unable to retrieve PATManagementService service.");
            }
        }
        return this.patManagementService;
    }
}
