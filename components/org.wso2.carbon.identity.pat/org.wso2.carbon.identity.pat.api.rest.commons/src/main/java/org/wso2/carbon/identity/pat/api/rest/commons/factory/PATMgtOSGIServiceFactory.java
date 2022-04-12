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
