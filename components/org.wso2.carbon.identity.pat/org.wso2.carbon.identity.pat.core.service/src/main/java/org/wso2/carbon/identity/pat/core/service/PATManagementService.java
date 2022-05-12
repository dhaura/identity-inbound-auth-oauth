/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package org.wso2.carbon.identity.pat.core.service;


import org.wso2.carbon.identity.pat.core.service.exeptions.PATManagementException;
import org.wso2.carbon.identity.pat.core.service.model.PATCreationData;
import org.wso2.carbon.identity.pat.core.service.model.PATData;
import org.wso2.carbon.identity.pat.core.service.model.PATViewMetadata;

import java.util.List;

/**
 * Interface for the PAT management service .
 */
public interface PATManagementService {

    /**
     * Creates Personal Access Token.
     *
     * @param patCreationData Data related to the PAT creation.
     * @return PATData  Data containing the new Personal Access Token and its attributes.
     */
    PATData issueToken(PATCreationData patCreationData) throws PATManagementException;

    /**
     * Get metadata of the specified Personal Access Token.
     *
     * @param tokenId Token ID related to the specified PAT.
     * @return PATViewMetadata  Metadata related to the PAT.
     */
    PATViewMetadata getTokenMetadata(String tokenId) throws PATManagementException;

    /**
     * Get Personal Access Token metadata list for the authorized user.
     *
     * @return List<PATViewMetadata>  List of PAT Metadata related the requesting user.
     */
    List<PATViewMetadata> getTokensMetadata() throws PATManagementException;

    /**
     * Revoke the specified Personal Access Token.
     *
     * @param tokenId Token ID related to the specified PAT.
     */
    void revokeToken(String tokenId) throws PATManagementException;

}
