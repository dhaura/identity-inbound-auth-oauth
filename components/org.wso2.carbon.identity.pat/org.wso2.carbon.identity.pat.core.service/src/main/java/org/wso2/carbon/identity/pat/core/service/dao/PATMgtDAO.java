/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package org.wso2.carbon.identity.pat.core.service.dao;

import org.wso2.carbon.identity.oauth2.IdentityOAuth2Exception;
import org.wso2.carbon.identity.pat.core.service.exeptions.PATManagementException;
import org.wso2.carbon.identity.pat.core.service.exeptions.PATManagementServerException;
import org.wso2.carbon.identity.pat.core.service.model.PATViewMetadata;

import java.util.List;

/**
 * DAO layer for PAT.
 */
public interface PATMgtDAO {

    /**
     * Persists Personal Access Token alias and description in the database.
     *
     * @param tokenID     Token ID of the newly created PAT.
     * @param alias       Alias of the newly created PAT.
     * @param description Description of the newly created PAT.
     */
    void insertPATData(String tokenID, String alias, String description) throws PATManagementServerException;

    /**
     * Get metadata of the specified Personal Access Token from the database.
     *
     * @param tokenID Token ID related to the specified PAT.
     * @param userID  User ID of the authorized user.
     * @return PATViewMetadata  Metadata related to the PAT.
     */
    PATViewMetadata getPATMetadata(String tokenID, String userID) throws PATManagementException;

    /**
     * Get Personal Access Token metadata list for the authorized user.
     *
     * @param userID User ID of the authorized user.
     * @return List<PATViewMetadata>  List of PAT Metadata related the requesting user.
     */
    List<PATViewMetadata> getPATsMetadata(String userID) throws PATManagementException;

    /**
     * Get scopes list for the specified Personal Access Token.
     *
     * @param tokenID Token ID related to the specified PAT.
     * @return List<String>  List of scopes related the specified PAT.
     */
    List<String> getPATScopes(String tokenID) throws PATManagementException;

    /**
     * Get the Token for the specified Personal Access Token ID.
     *
     * @param tokenID Token ID related to the specified PAT.
     * @return String  Token for the specified PAT ID.
     */
    String getPAT(String tokenID) throws PATManagementException;

    /**
     * Get the Client ID for the specified Personal Access Token.
     *
     * @param tokenID Token ID related to the specified PAT.
     * @return String  Client ID for the specified PAT.
     */
    String getClientIDFromTokenID(String tokenID) throws PATManagementException;

    /**
     * Check if the alias already exists.
     *
     * @param userId User ID related to the authorized user.
     * @param alias alias of the new PAT.
     * @return boolean  Client ID for the specified PAT.
     */
    boolean isDuplicatedAlias(String userId, String alias) throws PATManagementException;

}
