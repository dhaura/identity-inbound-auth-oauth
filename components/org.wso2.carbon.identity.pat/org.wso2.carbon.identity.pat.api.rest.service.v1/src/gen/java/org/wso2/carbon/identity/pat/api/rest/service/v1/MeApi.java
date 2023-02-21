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
import org.wso2.carbon.identity.pat.api.rest.service.v1.model.PATMetadata;
import org.wso2.carbon.identity.pat.api.rest.service.v1.MeApiService;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import io.swagger.annotations.*;

import javax.validation.constraints.*;

@Path("/me")
@Api(description = "The me API")

public class MeApi  {

    @Autowired
    private MeApiService delegate;

    @Valid
    @GET
    @Path("/tokens")
    
    @Produces({ "application/json" })
    @ApiOperation(value = "Retrieve metadata of PATs", notes = "Provides the capability to retrieve Metadata of all Personal Access Tokens of the authenticated user.", response = List.class, responseContainer = "List", authorizations = {
        @Authorization(value = "BasicAuth"),
        @Authorization(value = "OAuth2", scopes = {
            
        })
    }, tags={ "PAT Management", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "List of metadata of PATs successfully retrieved.", response = Object.class, responseContainer = "List"),
        @ApiResponse(code = 401, message = "Unauthorized", response = Void.class),
        @ApiResponse(code = 403, message = "Resources Forbidden", response = Void.class),
        @ApiResponse(code = 500, message = "Internal Server Error", response = Error.class)
    })
    public Response meTokensGet() {

        return delegate.meTokensGet();
    }

    @Valid
    @POST
    @Path("/tokens")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @ApiOperation(value = "PAT creation", notes = "Provides the capability to create a Personal Access Token.", response = PATCreationResponse.class, authorizations = {
        @Authorization(value = "BasicAuth"),
        @Authorization(value = "OAuth2", scopes = {
            
        })
    }, tags={ "PAT Management", })
    @ApiResponses(value = { 
        @ApiResponse(code = 201, message = "PAT successfully created.", response = PATCreationResponse.class),
        @ApiResponse(code = 400, message = "Invalid request input", response = Error.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = Void.class),
        @ApiResponse(code = 403, message = "Resources Forbidden", response = Void.class),
        @ApiResponse(code = 409, message = "Alias already exists for the authorized user.", response = Error.class),
        @ApiResponse(code = 500, message = "Internal Server Error", response = Error.class)
    })
    public Response meTokensPost(@ApiParam(value = "Represents the request to create a PAT." ,required=true) @Valid PATCreationRequest paTCreationRequest) {

        return delegate.meTokensPost(paTCreationRequest );
    }

    @Valid
    @DELETE
    @Path("/tokens/{tokenId}")
    
    @Produces({ "application/json" })
    @ApiOperation(value = "Revoke the speicifed PAT", notes = "Provides the capability to revoke a Personal Access Token.", response = Void.class, authorizations = {
        @Authorization(value = "BasicAuth"),
        @Authorization(value = "OAuth2", scopes = {
            
        })
    }, tags={ "PAT Management", })
    @ApiResponses(value = { 
        @ApiResponse(code = 204, message = "PAT successfully revoked.", response = Void.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = Void.class),
        @ApiResponse(code = 403, message = "Resources Forbidden", response = Void.class),
        @ApiResponse(code = 404, message = "PAT not found", response = Error.class),
        @ApiResponse(code = 500, message = "Internal Server Error", response = Error.class)
    })
    public Response meTokensTokenIdDelete(@ApiParam(value = "Token ID of a PAT",required=true) @PathParam("tokenId") String tokenId) {

        return delegate.meTokensTokenIdDelete(tokenId );
    }

    @Valid
    @GET
    @Path("/tokens/{tokenId}")
    
    @Produces({ "application/json" })
    @ApiOperation(value = "Retrieve metadata related to the specified PAT.", notes = "Provides the capability to retrieve the metadata for a specific PAT. ", response = PATMetadata.class, authorizations = {
        @Authorization(value = "BasicAuth"),
        @Authorization(value = "OAuth2", scopes = {
            
        })
    }, tags={ "PAT Management" })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Token metadata successfully retrieved.", response = PATMetadata.class),
        @ApiResponse(code = 401, message = "Unauthorized", response = Void.class),
        @ApiResponse(code = 403, message = "Resources Forbidden", response = Void.class),
        @ApiResponse(code = 404, message = "PAT not found", response = Error.class),
        @ApiResponse(code = 500, message = "Internal Server Error", response = Error.class)
    })
    public Response meTokensTokenIdGet(@ApiParam(value = "Token ID of a PAT",required=true) @PathParam("tokenId") String tokenId) {

        return delegate.meTokensTokenIdGet(tokenId );
    }

}
