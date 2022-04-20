package org.wso2.carbon.identity.pat.core.service;


import org.wso2.carbon.identity.pat.core.service.model.PATCreationData;
import org.wso2.carbon.identity.pat.core.service.model.PATCreationResponseData;

public interface PATManagementService {

    public PATCreationResponseData issuePAT(PATCreationData patCreationData);
}
