package org.wso2.carbon.identity.pat.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.wso2.carbon.identity.pat.bindings.impl.PATTokenBinder;


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
    }

}