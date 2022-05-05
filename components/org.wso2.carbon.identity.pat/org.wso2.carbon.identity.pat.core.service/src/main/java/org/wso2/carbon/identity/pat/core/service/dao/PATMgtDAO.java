/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package org.wso2.carbon.identity.pat.core.service.dao;

import org.wso2.carbon.identity.pat.core.service.model.TokenMetadataDTO;

import java.util.List;

/**
 * DAO layer for PAT.
 */
public interface PATMgtDAO {

    public void insertPATData(String tokenID, String alias, String description);
    public TokenMetadataDTO getTokenMetadata(String tokenID, String userID);
    public List<TokenMetadataDTO> getTokensMetadata(String userID);
    public List<String> getTokenScopes(String tokenID);
    public String getAccessToken(String tokenID);
    public String getClientIDFromTokenID(String tokenID);

}
