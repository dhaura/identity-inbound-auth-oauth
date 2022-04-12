package org.wso2.carbon.identity.pat.core.service;


import org.wso2.carbon.identity.pat.core.service.model.PATCreationData;

public interface PATManagementService {

    public void issuePAT(PATCreationData patCreationData);
}
