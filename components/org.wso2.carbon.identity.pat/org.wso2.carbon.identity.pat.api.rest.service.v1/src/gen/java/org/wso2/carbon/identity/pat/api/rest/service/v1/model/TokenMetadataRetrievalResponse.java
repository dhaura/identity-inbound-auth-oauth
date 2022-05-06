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

package org.wso2.carbon.identity.pat.api.rest.service.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.*;


import io.swagger.annotations.*;
import java.util.Objects;
import javax.validation.Valid;
import javax.xml.bind.annotation.*;

public class TokenMetadataRetrievalResponse  {
  
    private String tokenId;
    private String alias;
    private String description;
    private String timeCreated;
    private String expiryTime;
    private List<String> scope = new ArrayList<>();


    /**
    **/
    public TokenMetadataRetrievalResponse tokenId(String tokenId) {

        this.tokenId = tokenId;
        return this;
    }
    
    @ApiModelProperty(example = "00a12e21-64a1-4b60-9434-3d06b222291c", required = true, value = "")
    @JsonProperty("token_id")
    @Valid
    @NotNull(message = "Property tokenId cannot be null.")

    public String getTokenId() {
        return tokenId;
    }
    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    /**
    **/
    public TokenMetadataRetrievalResponse alias(String alias) {

        this.alias = alias;
        return this;
    }
    
    @ApiModelProperty(example = "Sample Alias", required = true, value = "")
    @JsonProperty("alias")
    @Valid
    @NotNull(message = "Property alias cannot be null.")

    public String getAlias() {
        return alias;
    }
    public void setAlias(String alias) {
        this.alias = alias;
    }

    /**
    **/
    public TokenMetadataRetrievalResponse description(String description) {

        this.description = description;
        return this;
    }
    
    @ApiModelProperty(example = "This is a description for Sample Alias", required = true, value = "")
    @JsonProperty("description")
    @Valid
    @NotNull(message = "Property description cannot be null.")

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    /**
    **/
    public TokenMetadataRetrievalResponse timeCreated(String timeCreated) {

        this.timeCreated = timeCreated;
        return this;
    }
    
    @ApiModelProperty(example = "2022-03-22 03:28:41", required = true, value = "")
    @JsonProperty("time_created")
    @Valid
    @NotNull(message = "Property timeCreated cannot be null.")

    public String getTimeCreated() {
        return timeCreated;
    }
    public void setTimeCreated(String timeCreated) {
        this.timeCreated = timeCreated;
    }

    /**
    **/
    public TokenMetadataRetrievalResponse expiryTime(String expiryTime) {

        this.expiryTime = expiryTime;
        return this;
    }
    
    @ApiModelProperty(example = "2022-03-22 03:58:41", required = true, value = "")
    @JsonProperty("expiry_time")
    @Valid
    @NotNull(message = "Property expiryTime cannot be null.")

    public String getExpiryTime() {
        return expiryTime;
    }
    public void setExpiryTime(String expiryTime) {
        this.expiryTime = expiryTime;
    }

    /**
    **/
    public TokenMetadataRetrievalResponse scope(List<String> scope) {

        this.scope = scope;
        return this;
    }
    
    @ApiModelProperty(example = "[\"internal_application_mgt_view\",\"internal_claim_meta_create\"]", required = true, value = "")
    @JsonProperty("scope")
    @Valid
    @NotNull(message = "Property scope cannot be null.")

    public List<String> getScope() {
        return scope;
    }
    public void setScope(List<String> scope) {
        this.scope = scope;
    }

    public TokenMetadataRetrievalResponse addScopeItem(String scopeItem) {
        this.scope.add(scopeItem);
        return this;
    }

    

    @Override
    public boolean equals(java.lang.Object o) {

        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TokenMetadataRetrievalResponse tokenMetadataRetrievalResponse = (TokenMetadataRetrievalResponse) o;
        return Objects.equals(this.tokenId, tokenMetadataRetrievalResponse.tokenId) &&
            Objects.equals(this.alias, tokenMetadataRetrievalResponse.alias) &&
            Objects.equals(this.description, tokenMetadataRetrievalResponse.description) &&
            Objects.equals(this.timeCreated, tokenMetadataRetrievalResponse.timeCreated) &&
            Objects.equals(this.expiryTime, tokenMetadataRetrievalResponse.expiryTime) &&
            Objects.equals(this.scope, tokenMetadataRetrievalResponse.scope);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tokenId, alias, description, timeCreated, expiryTime, scope);
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("class TokenMetadataRetrievalResponse {\n");
        
        sb.append("    tokenId: ").append(toIndentedString(tokenId)).append("\n");
        sb.append("    alias: ").append(toIndentedString(alias)).append("\n");
        sb.append("    description: ").append(toIndentedString(description)).append("\n");
        sb.append("    timeCreated: ").append(toIndentedString(timeCreated)).append("\n");
        sb.append("    expiryTime: ").append(toIndentedString(expiryTime)).append("\n");
        sb.append("    scope: ").append(toIndentedString(scope)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
    * Convert the given object to string with each line indented by 4 spaces
    * (except the first line).
    */
    private String toIndentedString(java.lang.Object o) {

        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n");
    }
}

