package org.wso2.carbon.identity.pat.api.rest.service.v1.core;

import org.wso2.carbon.identity.pat.api.rest.commons.PATApiMgtDataHolder;
import org.wso2.carbon.identity.pat.api.rest.service.v1.model.PATCreationRequest;
import org.wso2.carbon.identity.pat.core.service.model.PATCreationData;

public class TokenManagementApiService {

    public void issuePAT(PATCreationRequest patCreationRequest){
        PATApiMgtDataHolder.getPatManagementService().issuePAT(new PATCreationData());
    }
}
