package org.wso2.carbon.identity.pat.core.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.pat.core.service.model.PATCreationData;

public class PATManagementServiceImpl implements PATManagementService{

    private static final Log log = LogFactory.getLog(PATManagementServiceImpl.class);

    @Override
    public void issuePAT(PATCreationData patCreationData) {
        log.info("PAT issue service");
    }
}
