package org.wso2.carbon.identity.pat.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.oauth.OAuthAdminService;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;


@Component(
        name = "identity.pat.component",
        immediate = true
)

public class PATConsumerComponent {
    private static final Log log = LogFactory.getLog(PATConsumerComponent.class);

    OAuthAdminService oAuthAdminService = null;

    @Activate
    protected void activate(BundleContext bundleContext) {
        log.info("PAT Consumer Component Activation");
    }

    @Reference(
            name = "oAuthAdminService",
            service = OAuthAdminService.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unbindService"
    )
    protected void registerService(OAuthAdminService oAuthAdminService){
        this.oAuthAdminService = oAuthAdminService;
    }

    protected void unbindService(OAuthAdminService oAuthAdminService){

    }
}