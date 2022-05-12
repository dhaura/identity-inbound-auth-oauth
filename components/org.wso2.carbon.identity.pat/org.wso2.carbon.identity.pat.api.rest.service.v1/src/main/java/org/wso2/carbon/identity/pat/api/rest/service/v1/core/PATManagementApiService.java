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
import org.wso2.carbon.identity.pat.api.rest.service.v1.model.PATBasicMetadata;
import org.wso2.carbon.identity.pat.api.rest.service.v1.model.PATCreationRequest;
import org.wso2.carbon.identity.pat.api.rest.service.v1.model.PATCreationResponse;
import org.wso2.carbon.identity.pat.api.rest.service.v1.model.PATMetadata;
import org.wso2.carbon.identity.pat.core.service.common.PATConstants;
import org.wso2.carbon.identity.pat.core.service.common.PATUtil;
import org.wso2.carbon.identity.pat.core.service.exeptions.PATClientManagementException;
import org.wso2.carbon.identity.pat.core.service.exeptions.PATManagementException;
import org.wso2.carbon.identity.pat.core.service.model.PATCreationData;
import org.wso2.carbon.identity.pat.core.service.model.PATData;
import org.wso2.carbon.identity.pat.core.service.model.PATViewMetadata;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.ws.rs.core.Response;

/**
 * PAT management API service implementation.
 */
public class PATManagementApiService {

    private static final Log LOG = LogFactory.getLog(PATManagementApiService.class);

    private static final List<String> CONFLICT_ERROR_SCENARIOS = Arrays.asList();

    private static final List<String> NOT_FOUND_ERROR_SCENARIOS = Arrays.asList(
            PATConstants.ErrorMessage.ERROR_CODE_INVALID_TOKEN_ID.getCode()
    );

    /**
     * Creates Personal Access Token.
     *
     * @param patCreationRequest Data related to the PAT creation get request.
     * @return PATCreationResponse  Response containing the new Personal Access Token.
     */
    public PATCreationResponse issuePAT(PATCreationRequest patCreationRequest) {
        PATCreationData patCreationData = getPATCreationDataObject(patCreationRequest);
        try {
            return getPATCreationResponse(PATApiMgtDataHolder.getPatManagementService().issueToken(patCreationData));
        } catch (PATManagementException e) {
            throw handleException(e, Constants.ErrorMessages.ERROR_UNABLE_TO_CREATE_PAT, PATUtil.getUserNameFromContext());
        }
    }

    /**
     * Get metadata of the specified Personal Access Token.
     *
     * @param tokenId Token ID related to the specified PAT.
     * @return PATMetadata  Metadata related to the PAT.
     */
    public PATMetadata getPATMetadata(String tokenId) {
        try {
            PATViewMetadata patViewMetadata = PATApiMgtDataHolder.getPatManagementService().getTokenMetadata(tokenId);
            return getPATMetadata(patViewMetadata);
        } catch (PATManagementException e) {
            throw handleException(e, Constants.ErrorMessages.ERROR_UNABLE_TO_GET_PAT_METADATA, PATUtil.getUserNameFromContext());
        }

    }

    /**
     * Get Personal Access Token metadata list for the authorized user.
     *
     * @return List<PATMetadata>  List of PAT Metadata related the requesting user.
     */
    public List<PATBasicMetadata> getPATsMetadata() {
        try {
            List<PATViewMetadata> patViewMetadataList = PATApiMgtDataHolder
                    .getPatManagementService().getTokensMetadata();
            return getPATsMetadata(patViewMetadataList);
        } catch (PATManagementException e) {
            throw handleException(e, Constants.ErrorMessages.ERROR_UNABLE_TO_GET_PAT_METADATA, PATUtil.getUserNameFromContext());
        }
    }

    /**
     * Revoke the specified Personal Access Token.
     *
     * @param tokenId Token ID related to the specified PAT.
     */
    public void revokePAT(String tokenId) {
        try {
            PATApiMgtDataHolder.getPatManagementService().revokeToken(tokenId);
        } catch (PATManagementException e) {
            throw handleException(e, Constants.ErrorMessages.ERROR_UNABLE_TO_REVOKE_PAT, PATUtil.getUserNameFromContext());
        }
    }

    /**
     * Builds get resource location.
     *
     * @param tokenId Token ID related to the specified PAT.
     * @return URI     Get URI.
     */
    public URI getResourceLocation(String tokenId) {

        return buildURIForHeader("api/pat/v1/me/tokens/" + tokenId);
    }

    private PATCreationData getPATCreationDataObject(PATCreationRequest patCreationRequest) {
        PATCreationData patCreationData = new PATCreationData();

        patCreationData.setAlias(patCreationRequest.getAlias());
        patCreationData.setDescription(patCreationRequest.getDescription());
        patCreationData.setValidityPeriod(patCreationRequest.getValidityPeriod());
        patCreationData.setScope(patCreationRequest.getScope());
        patCreationData.setClientID(patCreationRequest.getClientId());

        return patCreationData;
    }

    private PATCreationResponse getPATCreationResponse(PATData patData) {
        PATCreationResponse patCreationResponse = new PATCreationResponse();

        patCreationResponse.setId(patData.getTokenId());
        patCreationResponse.setToken(patData.getAccessToken());
        patCreationResponse.setAlias(patData.getAlias());
        patCreationResponse.setDescription(patData.getDescription());
        patCreationResponse.setScope(patData.getScope());
        patCreationResponse.setValidityPeriod((int) patData.getValidityPeriod());

        return patCreationResponse;
    }

    private PATMetadata getPATMetadata(PATViewMetadata patViewMetadata) {
        PATMetadata patMetadata = new PATMetadata();
        patMetadata.setId(patViewMetadata.getTokenId());
        patMetadata.setAlias(patViewMetadata.getAlias());
        patMetadata.setDescription(patViewMetadata.getDescription());
        patMetadata.setCreatedTime(patViewMetadata.getTimeCreated());
        patMetadata.setExpiryTime(patViewMetadata.getExpiryTime());
        patMetadata.setScope(patViewMetadata.getScope());


        return patMetadata;
    }

    private PATBasicMetadata getPATBasicMetadata(PATViewMetadata patViewMetadata) {
        PATBasicMetadata patBasicMetadata = new PATBasicMetadata();
        patBasicMetadata.setId(patViewMetadata.getTokenId());
        patBasicMetadata.setAlias(patViewMetadata.getAlias());
        patBasicMetadata.setDescription(patViewMetadata.getDescription());
        patBasicMetadata.setCreatedTime(patViewMetadata.getTimeCreated());
        patBasicMetadata.setExpiryTime(patViewMetadata.getExpiryTime());

        return patBasicMetadata;
    }

    private List<PATBasicMetadata> getPATsMetadata
            (List<PATViewMetadata> patViewMetadataList) {

        List<PATBasicMetadata> patMetadataList = new ArrayList<>();

        for (PATViewMetadata patViewMetadata : patViewMetadataList) {
            PATBasicMetadata patBasicMetadata
                    = getPATBasicMetadata(patViewMetadata);

            patMetadataList.add(patBasicMetadata);
        }

        return patMetadataList;
    }

    private static URI buildURIForHeader(String endpoint) {

        URI loc = null;
        try {
            String url = ServiceURLBuilder.create().addPath(endpoint).build().getAbsolutePublicURL();
            loc = URI.create(url);
        } catch (URLBuilderException e) {
            String errorDescription = "Server encountered an error while building URL for response header.";
            throw buildInternalServerError(e, errorDescription);
        }
        return loc;
    }

    private APIError handleException(PATManagementException exception, Constants.ErrorMessages errorEnum,
                                     String... data) {

        ErrorResponse errorResponse;
        Response.Status status;
        if (exception instanceof PATClientManagementException) {
            status = Response.Status.BAD_REQUEST;
            if (isConflictScenario(exception.getErrorCode())) {
                status = Response.Status.CONFLICT;
            } else if (isNotFoundScenario(exception.getErrorCode())) {
                status = Response.Status.NOT_FOUND;
            }

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

    private ErrorResponse.Builder getErrorBuilder(PATManagementException exception,
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

    private static APIError buildInternalServerError(Exception e, String errorDescription) {

        String errorCode = Constants.ErrorMessages.ERROR_COMMON_SERVER_ERROR.getCode();
        String errorMessage = "Error while building response.";

        ErrorResponse errorResponse = new ErrorResponse.Builder().
                withCode(errorCode)
                .withMessage(errorMessage)
                .withDescription(errorDescription)
                .build(LOG, e, errorDescription);
        Response.Status status = Response.Status.INTERNAL_SERVER_ERROR;
        return new APIError(status, errorResponse);
    }

    private boolean isConflictScenario(String errorCode) {

        return !StringUtils.isBlank(errorCode) && CONFLICT_ERROR_SCENARIOS.contains(errorCode);
    }

    private boolean isNotFoundScenario(String errorCode) {

        return !StringUtils.isBlank(errorCode) && NOT_FOUND_ERROR_SCENARIOS.contains(errorCode);
    }

}
