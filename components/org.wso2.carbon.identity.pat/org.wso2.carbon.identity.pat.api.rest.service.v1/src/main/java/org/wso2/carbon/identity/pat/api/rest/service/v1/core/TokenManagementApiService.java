package org.wso2.carbon.identity.pat.api.rest.service.v1.core;

import org.wso2.carbon.identity.pat.api.rest.commons.PATApiMgtDataHolder;
import org.wso2.carbon.identity.pat.api.rest.service.v1.model.PATCreationRequest;
import org.wso2.carbon.identity.pat.api.rest.service.v1.model.PATCreationResponse;
import org.wso2.carbon.identity.pat.api.rest.service.v1.model.TokenMetadataRetrievalResponse;
import org.wso2.carbon.identity.pat.core.service.model.PATCreationReqDTO;
import org.wso2.carbon.identity.pat.core.service.model.PATCreationRespDTO;
import org.wso2.carbon.identity.pat.core.service.model.TokenMetadataDTO;

import java.util.ArrayList;
import java.util.List;

public class TokenManagementApiService {

    public PATCreationResponse issuePAT(PATCreationRequest patCreationRequest){
        PATCreationReqDTO patCreationReqDTO = getPATCreationDataObject(patCreationRequest);
        return getPATCreationResponse(PATApiMgtDataHolder.getPatManagementService().issuePAT(patCreationReqDTO));
    }

    public TokenMetadataRetrievalResponse getTokenMetadata(String tokenId){
        TokenMetadataDTO tokenMetadataDTO = PATApiMgtDataHolder.getPatManagementService().getTokenMetadata(tokenId);
        return getTokenMetadataRetrievalResponse(tokenMetadataDTO);
    }

    public List<TokenMetadataRetrievalResponse> getTokensMetadata(){
        List<TokenMetadataDTO> tokenMetadataDTOList = PATApiMgtDataHolder.getPatManagementService().getTokensMetadata();
        return getTokensMetadataRetrievalResponse(tokenMetadataDTOList);
    }

    public void revokePAT(String tokenId){
        PATApiMgtDataHolder.getPatManagementService().revokePAT(tokenId);
    }

    private PATCreationReqDTO getPATCreationDataObject(PATCreationRequest patCreationRequest){
        PATCreationReqDTO patCreationReqDTO = new PATCreationReqDTO();

        patCreationReqDTO.setAlias(patCreationRequest.getAlias());
        patCreationReqDTO.setDescription(patCreationRequest.getDescription());
        patCreationReqDTO.setValidityPeriod(patCreationRequest.getValidityPeriod());
        patCreationReqDTO.setScope(patCreationRequest.getScope());
        patCreationReqDTO.setIdTokenHint(patCreationRequest.getIdTokenHint());
        patCreationReqDTO.setClientID(patCreationRequest.getClientId());

        return patCreationReqDTO;
    }

    private PATCreationResponse getPATCreationResponse(PATCreationRespDTO patCreationRespDTO){
        PATCreationResponse patCreationResponse = new PATCreationResponse();
        patCreationResponse.setAccessToken(patCreationRespDTO.getAccessToken());
        patCreationResponse.setScope(patCreationRespDTO.getScope());
        patCreationResponse.setValidityPeriod((int) patCreationRespDTO.getValidityPeriod());

        return patCreationResponse;
    }

    private TokenMetadataRetrievalResponse getTokenMetadataRetrievalResponse(TokenMetadataDTO tokenMetadataDTO){
        TokenMetadataRetrievalResponse tokenMetadataRetrievalResponse = new TokenMetadataRetrievalResponse();
        tokenMetadataRetrievalResponse.setTokenId(tokenMetadataDTO.getTokenId());
        tokenMetadataRetrievalResponse.setAlias(tokenMetadataDTO.getAlias());
        tokenMetadataRetrievalResponse.setDescription(tokenMetadataDTO.getDescription());
        tokenMetadataRetrievalResponse.setValidityPeriod(tokenMetadataDTO.getValidityPeriod());
        tokenMetadataRetrievalResponse.setTimeCreated(tokenMetadataDTO.getTimeCreated());
        tokenMetadataRetrievalResponse.setScope(tokenMetadataDTO.getScope());

        return tokenMetadataRetrievalResponse;
    }

    private List<TokenMetadataRetrievalResponse> getTokensMetadataRetrievalResponse(List<TokenMetadataDTO> tokenMetadataDTOList){
        List<TokenMetadataRetrievalResponse> tokenMetadataRetrievalResponseList = new ArrayList<>();

        for (TokenMetadataDTO tokenMetadataDTO: tokenMetadataDTOList){
            TokenMetadataRetrievalResponse tokenMetadataRetrievalResponse = getTokenMetadataRetrievalResponse(tokenMetadataDTO);

            tokenMetadataRetrievalResponseList.add(tokenMetadataRetrievalResponse);
        }

        return tokenMetadataRetrievalResponseList;
    }
}
