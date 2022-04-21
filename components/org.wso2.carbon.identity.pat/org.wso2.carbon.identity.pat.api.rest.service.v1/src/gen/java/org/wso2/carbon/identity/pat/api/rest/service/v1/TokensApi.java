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

package org.wso2.carbon.identity.pat.api.rest.service.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import java.io.InputStream;
import java.util.List;

import org.wso2.carbon.identity.pat.api.rest.service.v1.model.Error;
import org.wso2.carbon.identity.pat.api.rest.service.v1.model.PATCreationRequest;
import org.wso2.carbon.identity.pat.api.rest.service.v1.model.PATCreationResponse;
import org.wso2.carbon.identity.pat.api.rest.service.v1.model.TokenMetadataRetrievalResponse;
import org.wso2.carbon.identity.pat.api.rest.service.v1.TokensApiService;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import io.swagger.annotations.*;

import javax.validation.constraints.*;

@Path("/tokens")
@Api(description = "The tokens API")

public class TokensApi  {

    @Autowired
    private TokensApiService delegate;

    @Valid
    @GET
    
    
    @Produces({ "application/json" })
    @ApiOperation(value = "Retrieve PATs", notes = "Provides the capability to retrieve Personal Access Tokens for a specific user.", response = TokenMetadataRetrievalResponse.class, responseContainer = "List", authorizations = {
        @Authorization(value = "BasicAuth"),
        @Authorization(value = "OAuth2", scopes = {
            
        })
    }, tags={ "PAT Management", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "List of PATs successfully Retrieved.", response = TokenMetadataRetrievalResponse.class, responseContainer = "List"),
        @ApiResponse(code = 400, message = "Invalid Request Input", response = Error.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = Error.class),
        @ApiResponse(code = 403, message = "Resources Forbidden", response = Error.class),
        @ApiResponse(code = 404, message = "PAT not found", response = Error.class),
        @ApiResponse(code = 500, message = "Internal Server Error", response = Error.class)
    })
    public Response tokensGet() {

        return delegate.tokensGet();
    }

    @Valid
    @POST
    
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @ApiOperation(value = "PAT generation", notes = "Provides the capability to create a Personal Access Token.", response = PATCreationResponse.class, authorizations = {
        @Authorization(value = "BasicAuth"),
        @Authorization(value = "OAuth2", scopes = {
            
        })
    }, tags={ "PAT Management", })
    @ApiResponses(value = { 
        @ApiResponse(code = 201, message = "PAT successfully created.", response = PATCreationResponse.class),
        @ApiResponse(code = 400, message = "Invalid Request Input", response = Error.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = Error.class),
        @ApiResponse(code = 403, message = "Resources Forbidden", response = Error.class),
        @ApiResponse(code = 500, message = "Internal Server Error", response = Error.class)
    })
    public Response tokensPost(@ApiParam(value = "Represents the request to create a PAT." ,required=true) @Valid PATCreationRequest paTCreationRequest) {

        return delegate.tokensPost(paTCreationRequest );
    }

    @Valid
    @DELETE
    @Path("/{token_id}")
    
    @Produces({ "application/json" })
    @ApiOperation(value = "Revoke a PAT", notes = "Provides the capability to revoke a Personal Access Token.", response = Void.class, authorizations = {
        @Authorization(value = "BasicAuth"),
        @Authorization(value = "OAuth2", scopes = {
            
        })
    }, tags={ "PAT Management", })
    @ApiResponses(value = { 
        @ApiResponse(code = 204, message = "PAT successfully Revoked.", response = Void.class),
        @ApiResponse(code = 400, message = "Invalid Request Input", response = Error.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = Error.class),
        @ApiResponse(code = 403, message = "Resources Forbidden", response = Error.class),
        @ApiResponse(code = 404, message = "PAT not found", response = Error.class),
        @ApiResponse(code = 500, message = "Internal Server Error", response = Error.class)
    })
    public Response tokensTokenIdDelete(@ApiParam(value = "Token ID of a PAT",required=true) @PathParam("token_id") String tokenId) {

        return delegate.tokensTokenIdDelete(tokenId );
    }

    @Valid
    @GET
    @Path("/{token_id}")
    
    @Produces({ "application/json" })
    @ApiOperation(value = "Retrieve info related to a PAT.", notes = "Provides the capability to reteive all the metadata for a specific PAT. ", response = TokenMetadataRetrievalResponse.class, authorizations = {
        @Authorization(value = "BasicAuth"),
        @Authorization(value = "OAuth2", scopes = {
            
        })
    }, tags={ "PAT Management" })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Token Metadata successfully Retrieved.", response = TokenMetadataRetrievalResponse.class),
        @ApiResponse(code = 400, message = "Invalid Request Input", response = Error.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = Error.class),
        @ApiResponse(code = 403, message = "Resources Forbidden", response = Error.class),
        @ApiResponse(code = 404, message = "PAT not found", response = Error.class),
        @ApiResponse(code = 500, message = "Internal Server Error", response = Error.class)
    })
    public Response tokensTokenIdGet(@ApiParam(value = "Token ID of a PAT",required=true) @PathParam("token_id") String tokenId) {

        return delegate.tokensTokenIdGet(tokenId );
    }

}
