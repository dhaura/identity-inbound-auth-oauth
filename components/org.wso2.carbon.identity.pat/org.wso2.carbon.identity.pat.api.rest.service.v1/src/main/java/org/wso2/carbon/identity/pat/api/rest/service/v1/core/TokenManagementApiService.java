/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package org.wso2.carbon.identity.pat.api.rest.service.v1.core;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.core.ServiceURLBuilder;
import org.wso2.carbon.identity.core.URLBuilderException;
import org.wso2.carbon.identity.pat.api.rest.commons.PATApiMgtDataHolder;
import org.wso2.carbon.identity.pat.api.rest.commons.exceptions.APIError;
import org.wso2.carbon.identity.pat.api.rest.commons.exceptions.ErrorResponse;
import org.wso2.carbon.identity.pat.api.rest.commons.util.Constants;
import org.wso2.carbon.identity.pat.api.rest.service.v1.model.PATCreationRequest;
import org.wso2.carbon.identity.pat.api.rest.service.v1.model.PATCreationResponse;
import org.wso2.carbon.identity.pat.api.rest.service.v1.model.PATMetadata;
import org.wso2.carbon.identity.pat.core.service.common.PATUtil;
import org.wso2.carbon.identity.pat.core.service.exeptions.PATClientException;
import org.wso2.carbon.identity.pat.core.service.exeptions.PATException;
import org.wso2.carbon.identity.pat.core.service.model.PATCreationReqDTO;
import org.wso2.carbon.identity.pat.core.service.model.PATCreationRespDTO;
import org.wso2.carbon.identity.pat.core.service.model.TokenMetadataDTO;

import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class TokenManagementApiService {

    private static final Log LOG = LogFactory.getLog(TokenManagementApiService.class);

    public PATCreationResponse issuePAT(PATCreationRequest patCreationRequest) {
        PATCreationReqDTO patCreationReqDTO = getPATCreationDataObject(patCreationRequest);
        try {
            return getPATCreationResponse(PATApiMgtDataHolder.getPatManagementService().issuePAT(patCreationReqDTO));
        } catch (PATException e) {
            throw handleException(e, Constants.ErrorMessages.ERROR_UNABLE_TO_CREATE_PAT, PATUtil.getUserID());
        }
    }

    public PATMetadata getTokenMetadata(String tokenId) {
        try {
            TokenMetadataDTO tokenMetadataDTO = PATApiMgtDataHolder.getPatManagementService().getTokenMetadata(tokenId);
            return getTokenMetadata(tokenMetadataDTO);
        } catch (PATException e) {
            throw new RuntimeException(e);
        }

    }

    public List<PATMetadata> getTokensMetadata() {
        try {
            List<TokenMetadataDTO> tokenMetadataDTOList = PATApiMgtDataHolder.getPatManagementService().getTokensMetadata();
            return getTokensMetadata(tokenMetadataDTOList);
        } catch (PATException e) {
            throw new RuntimeException(e);
        }
    }

    public void revokePAT(String tokenId) {
        try {
            PATApiMgtDataHolder.getPatManagementService().revokePAT(tokenId);
        } catch (PATException e) {
            throw new RuntimeException(e);
        }
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

        patCreationResponse.setId(patCreationRespDTO.getTokenId());
        patCreationResponse.setToken(patCreationRespDTO.getAccessToken());
        patCreationResponse.setAlias(patCreationRespDTO.getAlias());
        patCreationResponse.setDescription(patCreationRespDTO.getDescription());
        patCreationResponse.setScope(patCreationRespDTO.getScope());
        patCreationResponse.setValidityPeriod((int) patCreationRespDTO.getValidityPeriod());

        return patCreationResponse;
    }

    private PATMetadata getTokenMetadata(TokenMetadataDTO tokenMetadataDTO) {
        PATMetadata patMetadata = new PATMetadata();
        patMetadata.setId(tokenMetadataDTO.getTokenId());
        patMetadata.setAlias(tokenMetadataDTO.getAlias());
        patMetadata.setDescription(tokenMetadataDTO.getDescription());
        patMetadata.setCreatedTime(tokenMetadataDTO.getTimeCreated());
        patMetadata.setExpiryTime(tokenMetadataDTO.getExpiryTime());
        patMetadata.setScope(tokenMetadataDTO.getScope());

        return patMetadata;
    }

    private List<PATMetadata> getTokensMetadata
            (List<TokenMetadataDTO> tokenMetadataDTOList) {

        List<PATMetadata> patMetadataList = new ArrayList<>();

        for (TokenMetadataDTO tokenMetadataDTO : tokenMetadataDTOList) {
            PATMetadata patMetadata
                    = getTokenMetadata(tokenMetadataDTO);

            patMetadataList.add(patMetadata);
        }

        return patMetadataList;
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

    private APIError handleException(PATException exception, Constants.ErrorMessages errorEnum,
                                     String... data) {

        ErrorResponse errorResponse;
        Response.Status status;
        if (exception instanceof PATClientException) {
            status = Response.Status.BAD_REQUEST;
//            if (isConflictScenario(exception.getErrorCode())) {
//                status = Response.Status.CONFLICT;
//            } else if (isNotFoundScenario(exception.getErrorCode())) {
//                status = Response.Status.NOT_FOUND;
//            }

            errorResponse = getErrorBuilder(exception, errorEnum, data)
                    .build(LOG, exception, buildErrorDescription(errorEnum.getDescription(), data), true);

        } else {
            status = Response.Status.INTERNAL_SERVER_ERROR;
            errorResponse = getErrorBuilder(errorEnum, data).
                    build(LOG, exception, buildErrorDescription(errorEnum.getDescription(), data),
                            false);
        }
        return new APIError(status, errorResponse);
    }

    private ErrorResponse.Builder getErrorBuilder(PATException exception,
                                                  Constants.ErrorMessages errorEnum, String... data) {

        String errorCode = (StringUtils.isBlank(exception.getErrorCode())) ?
                errorEnum.getCode() : exception.getErrorCode();
        String description = (StringUtils.isBlank(exception.getMessage())) ?
                errorEnum.getDescription() : exception.getMessage();
        return new ErrorResponse.Builder()
                .withCode(errorCode)
                .withMessage(errorEnum.getMessage())
                .withDescription(buildErrorDescription(description, data));
    }

    private ErrorResponse.Builder getErrorBuilder(Constants.ErrorMessages errorEnum, String... data) {

        return new ErrorResponse.Builder()
                .withCode(errorEnum.getCode())
                .withMessage(errorEnum.getMessage())
                .withDescription(buildErrorDescription(errorEnum.getDescription(), data));
    }

    private String buildErrorDescription(String description, String... data) {

        if (ArrayUtils.isNotEmpty(data)) {
            return String.format(description, (Object[]) data);
        }
        return description;
    }

}
