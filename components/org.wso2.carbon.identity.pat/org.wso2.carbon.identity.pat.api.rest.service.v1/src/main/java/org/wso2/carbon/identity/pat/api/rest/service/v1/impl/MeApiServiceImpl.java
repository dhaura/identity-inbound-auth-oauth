/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com).
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.identity.pat.api.rest.service.v1.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.wso2.carbon.identity.pat.api.rest.service.v1.*;
import org.wso2.carbon.identity.pat.api.rest.service.v1.core.TokenManagementApiService;
import org.wso2.carbon.identity.pat.api.rest.service.v1.model.*;
import java.util.List;
import javax.ws.rs.core.Response;

public class MeApiServiceImpl implements MeApiService {

    @Autowired
    private TokenManagementApiService tokenManagementApiService;

    @Override
    public Response meTokensGet() {

        // do some magic!
        return Response.ok().entity(tokenManagementApiService.getTokensMetadata()).build();
    }

    @Override
    public Response meTokensPost(PATCreationRequest paTCreationRequest) {

        PATCreationResponse patCreationResponse = tokenManagementApiService.issuePAT(paTCreationRequest);
        String tokenId = patCreationResponse.getId();

        // do some magic!
        return Response.created(tokenManagementApiService.getResourceLocation(tokenId)).entity(patCreationResponse).build();
    }

    @Override
    public Response meTokensTokenIdDelete(String tokenId) {
        tokenManagementApiService.revokePAT(tokenId);

        // do some magic!
        return Response.noContent().build();
    }

    @Override
    public Response meTokensTokenIdGet(String tokenId) {

        // do some magic!
        return Response.ok().entity(tokenManagementApiService.getTokenMetadata(tokenId)).build();
    }
}
