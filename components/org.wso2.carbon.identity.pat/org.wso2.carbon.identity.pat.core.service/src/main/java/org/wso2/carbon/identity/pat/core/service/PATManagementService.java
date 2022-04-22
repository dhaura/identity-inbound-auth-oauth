package org.wso2.carbon.identity.pat.core.service;


import org.wso2.carbon.identity.pat.core.service.model.PATCreationReqDTO;
import org.wso2.carbon.identity.pat.core.service.model.PATCreationRespDTO;
import org.wso2.carbon.identity.pat.core.service.model.TokenMetadataDTO;

import java.util.List;

public interface PATManagementService {

    public PATCreationRespDTO issuePAT(PATCreationReqDTO patCreationReqDTO);
    public TokenMetadataDTO getTokenMetadata(String tokenId);
    public List<TokenMetadataDTO> getTokensMetadata();
    public void revokePAT(String tokenId);

}
