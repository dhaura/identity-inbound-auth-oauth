/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package org.wso2.carbon.identity.pat.core.service;


import org.wso2.carbon.identity.pat.core.service.exeptions.PATException;
import org.wso2.carbon.identity.pat.core.service.model.PATCreationReqDTO;
import org.wso2.carbon.identity.pat.core.service.model.PATCreationRespDTO;
import org.wso2.carbon.identity.pat.core.service.model.TokenMetadataDTO;

import java.util.List;

public interface PATManagementService {

    public PATCreationRespDTO issuePAT(PATCreationReqDTO patCreationReqDTO) throws PATException;
    public TokenMetadataDTO getTokenMetadata(String tokenId);
    public List<TokenMetadataDTO> getTokensMetadata();
    public void revokePAT(String tokenId);

}
