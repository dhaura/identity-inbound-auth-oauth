/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package org.wso2.carbon.identity.pat.api.rest.service.v1.core;

import org.wso2.carbon.identity.core.ServiceURLBuilder;
import org.wso2.carbon.identity.core.URLBuilderException;
import org.wso2.carbon.identity.pat.api.rest.commons.PATApiMgtDataHolder;
import org.wso2.carbon.identity.pat.api.rest.service.v1.model.PATCreationRequest;
import org.wso2.carbon.identity.pat.api.rest.service.v1.model.PATCreationResponse;
import org.wso2.carbon.identity.pat.api.rest.service.v1.model.TokenMetadataRetrievalResponse;
import org.wso2.carbon.identity.pat.core.service.model.PATCreationReqDTO;
import org.wso2.carbon.identity.pat.core.service.model.PATCreationRespDTO;
import org.wso2.carbon.identity.pat.core.service.model.TokenMetadataDTO;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class TokenManagementApiService {

    public PATCreationResponse issuePAT(PATCreationRequest patCreationRequest) {
        PATCreationReqDTO patCreationReqDTO = getPATCreationDataObject(patCreationRequest);
        return getPATCreationResponse(PATApiMgtDataHolder.getPatManagementService().issuePAT(patCreationReqDTO));
    }

    public TokenMetadataRetrievalResponse getTokenMetadata(String tokenId) {
        TokenMetadataDTO tokenMetadataDTO = PATApiMgtDataHolder.getPatManagementService().getTokenMetadata(tokenId);
        return getTokenMetadataRetrievalResponse(tokenMetadataDTO);
    }

    public List<TokenMetadataRetrievalResponse> getTokensMetadata() {
        List<TokenMetadataDTO> tokenMetadataDTOList = PATApiMgtDataHolder.getPatManagementService().getTokensMetadata();
        return getTokensMetadataRetrievalResponse(tokenMetadataDTOList);
    }

    public void revokePAT(String tokenId) {
        PATApiMgtDataHolder.getPatManagementService().revokePAT(tokenId);
    }

    public URI getResourceLocation(String tokenId) {

        return buildURIForHeader("api/pat/v1/tokens/" + tokenId);
    }

    private PATCreationReqDTO getPATCreationDataObject(PATCreationRequest patCreationRequest) {
        PATCreationReqDTO patCreationReqDTO = new PATCreationReqDTO();

        patCreationReqDTO.setAlias(patCreationRequest.getAlias());
        patCreationReqDTO.setDescription(patCreationRequest.getDescription());
        patCreationReqDTO.setValidityPeriod(patCreationRequest.getValidityPeriod());
        patCreationReqDTO.setScope(patCreationRequest.getScope());
        patCreationReqDTO.setClientID(patCreationRequest.getClientId());

        return patCreationReqDTO;
    }

    private PATCreationResponse getPATCreationResponse(PATCreationRespDTO patCreationRespDTO) {
        PATCreationResponse patCreationResponse = new PATCreationResponse();

        patCreationResponse.setTokenId(patCreationRespDTO.getTokenId());
        patCreationResponse.setAccessToken(patCreationRespDTO.getAccessToken());
        patCreationResponse.setAlias(patCreationRespDTO.getAlias());
        patCreationResponse.setDescription(patCreationRespDTO.getDescription());
        patCreationResponse.setScope(patCreationRespDTO.getScope());
        patCreationResponse.setValidityPeriod((int) patCreationRespDTO.getValidityPeriod());

        return patCreationResponse;
    }

    private TokenMetadataRetrievalResponse getTokenMetadataRetrievalResponse(TokenMetadataDTO tokenMetadataDTO) {
        TokenMetadataRetrievalResponse tokenMetadataRetrievalResponse = new TokenMetadataRetrievalResponse();
        tokenMetadataRetrievalResponse.setTokenId(tokenMetadataDTO.getTokenId());
        tokenMetadataRetrievalResponse.setAlias(tokenMetadataDTO.getAlias());
        tokenMetadataRetrievalResponse.setDescription(tokenMetadataDTO.getDescription());
        tokenMetadataRetrievalResponse.setTimeCreated(tokenMetadataDTO.getTimeCreated());
        tokenMetadataRetrievalResponse.setExpiryTime(tokenMetadataDTO.getExpiryTime());
        tokenMetadataRetrievalResponse.setScope(tokenMetadataDTO.getScope());

        return tokenMetadataRetrievalResponse;
    }

    private List<TokenMetadataRetrievalResponse> getTokensMetadataRetrievalResponse
            (List<TokenMetadataDTO> tokenMetadataDTOList) {

        List<TokenMetadataRetrievalResponse> tokenMetadataRetrievalResponseList = new ArrayList<>();

        for (TokenMetadataDTO tokenMetadataDTO : tokenMetadataDTOList) {
            TokenMetadataRetrievalResponse tokenMetadataRetrievalResponse
                    = getTokenMetadataRetrievalResponse(tokenMetadataDTO);

            tokenMetadataRetrievalResponseList.add(tokenMetadataRetrievalResponse);
        }

        return tokenMetadataRetrievalResponseList;
    }

    /**
     * Builds get url.
     *
     * @param endpoint Endpoint of the get request.
     * @return URI     Get URI.
     */
    public static URI buildURIForHeader(String endpoint) {

        URI loc = null;
        try {
            String url = ServiceURLBuilder.create().addPath(endpoint).build().getAbsolutePublicURL();
            loc = URI.create(url);
        } catch (URLBuilderException e) {
            String errorDescription = "Server encountered an error while building URL for response header.";
            //TODO: handle error
        }
        return loc;
    }
}
