/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package org.wso2.carbon.identity.pat.core.service.dao;

import org.wso2.carbon.identity.pat.core.service.exeptions.PATManagementException;
import org.wso2.carbon.identity.pat.core.service.model.PATViewMetadata;

import java.util.List;

/**
 * DAO layer for PAT.
 */
public interface PATMgtDAO {

    public void insertPATData(String tokenID, String alias, String description);
    public PATViewMetadata getPATMetadata(String tokenID, String userID) throws PATManagementException;
    public List<PATViewMetadata> getPATsMetadata(String userID) throws PATManagementException;
    public List<String> getPATScopes(String tokenID) throws PATManagementException;
    public String getPAT(String tokenID) throws PATManagementException;
    public String getClientIDFromTokenID(String tokenID) throws PATManagementException;

}
