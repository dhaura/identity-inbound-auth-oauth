package org.wso2.carbon.identity.pat.api.rest.service.v1.core;

import org.wso2.carbon.identity.pat.api.rest.commons.PATApiMgtDataHolder;
import org.wso2.carbon.identity.pat.api.rest.service.v1.model.PATCreationRequest;
import org.wso2.carbon.identity.pat.api.rest.service.v1.model.PATCreationResponse;
import org.wso2.carbon.identity.pat.core.service.model.PATCreationData;
import org.wso2.carbon.identity.pat.core.service.model.PATCreationResponseData;

public class TokenManagementApiService {

    public PATCreationResponse issuePAT(PATCreationRequest patCreationRequest){
        PATCreationData patCreationData = getPATCreationDataObject(patCreationRequest);
        return getPATCreationResponse(PATApiMgtDataHolder.getPatManagementService().issuePAT(patCreationData));
    }

    private PATCreationData getPATCreationDataObject(PATCreationRequest patCreationRequest){
        PATCreationData patCreationData = new PATCreationData();

        patCreationData.setAlias(patCreationRequest.getAlias());
        patCreationData.setDescription(patCreationRequest.getDescription());
        patCreationData.setValidityPeriod(patCreationRequest.getValidityPeriod());
        patCreationData.setScope(patCreationRequest.getScope());
        patCreationData.setIdTokenHint(patCreationRequest.getIdTokenHint());
        patCreationData.setClientID(patCreationRequest.getClientId());

        return patCreationData;
    }

    private PATCreationResponse getPATCreationResponse(PATCreationResponseData patCreationResponseData){
        PATCreationResponse patCreationResponse = new PATCreationResponse();
        patCreationResponse.setAccessToken(patCreationResponseData.getAccessToken());
        patCreationResponse.setScope(patCreationResponseData.getScope());
        patCreationResponse.setValidityPeriod((int) patCreationResponseData.getValidityPeriod());

        return patCreationResponse;
    }
}
