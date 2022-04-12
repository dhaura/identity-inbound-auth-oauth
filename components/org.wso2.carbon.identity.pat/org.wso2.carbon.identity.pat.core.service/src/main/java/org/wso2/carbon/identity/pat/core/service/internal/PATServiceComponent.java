package org.wso2.carbon.identity.pat.core.service.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.wso2.carbon.identity.oauth.internal.OAuthComponentServiceHolder;
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
        bundleContext.registerService(PATTokenBinder.class.getName(), patTokenBinder, null);

        bundleContext.registerService(PATManagementService.class.getName(), new PATManagementServiceImpl(), null);
    }

    protected void setRealmService(RealmService realmService) {

        if (log.isDebugEnabled()) {
            log.debug("Setting the Realm Service");
        }
        OAuthComponentServiceHolder.getInstance().setRealmService(realmService);
    }

    protected void unsetRealmService(RealmService realmService) {

        if (log.isDebugEnabled()) {
            log.debug("Unsetting the Realm Service");
        }
        OAuthComponentServiceHolder.getInstance().setRealmService(null);
    }

}