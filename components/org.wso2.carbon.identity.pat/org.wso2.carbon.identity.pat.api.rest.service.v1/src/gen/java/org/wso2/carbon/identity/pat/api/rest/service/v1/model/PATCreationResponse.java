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

public class PATCreationResponse  {
  
    private String id;
    private String token;
    private String alias;
    private String description;
    private List<String> scope = new ArrayList<>();

    private Integer validityPeriod;

    /**
    **/
    public PATCreationResponse id(String id) {

        this.id = id;
        return this;
    }
    
    @ApiModelProperty(example = "00a12e21-64a1-4b60-9434-3d06b222291c", required = true, value = "")
    @JsonProperty("id")
    @Valid
    @NotNull(message = "Property id cannot be null.")

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    /**
    **/
    public PATCreationResponse token(String token) {

        this.token = token;
        return this;
    }
    
    @ApiModelProperty(example = "6edb0049-0ea3-3b26-9aa3-152b1891d200", required = true, value = "")
    @JsonProperty("token")
    @Valid
    @NotNull(message = "Property token cannot be null.")

    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }

    /**
    **/
    public PATCreationResponse alias(String alias) {

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
    public PATCreationResponse description(String description) {

        this.description = description;
        return this;
    }
    
    @ApiModelProperty(example = "This is a description for Sample Alias", value = "")
    @JsonProperty("description")
    @Valid
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    /**
    **/
    public PATCreationResponse scope(List<String> scope) {

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

    public PATCreationResponse addScopeItem(String scopeItem) {
        this.scope.add(scopeItem);
        return this;
    }

        /**
    **/
    public PATCreationResponse validityPeriod(Integer validityPeriod) {

        this.validityPeriod = validityPeriod;
        return this;
    }
    
    @ApiModelProperty(example = "2000", required = true, value = "")
    @JsonProperty("validityPeriod")
    @Valid
    @NotNull(message = "Property validityPeriod cannot be null.")

    public Integer getValidityPeriod() {
        return validityPeriod;
    }
    public void setValidityPeriod(Integer validityPeriod) {
        this.validityPeriod = validityPeriod;
    }



    @Override
    public boolean equals(java.lang.Object o) {

        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PATCreationResponse paTCreationResponse = (PATCreationResponse) o;
        return Objects.equals(this.id, paTCreationResponse.id) &&
            Objects.equals(this.token, paTCreationResponse.token) &&
            Objects.equals(this.alias, paTCreationResponse.alias) &&
            Objects.equals(this.description, paTCreationResponse.description) &&
            Objects.equals(this.scope, paTCreationResponse.scope) &&
            Objects.equals(this.validityPeriod, paTCreationResponse.validityPeriod);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, token, alias, description, scope, validityPeriod);
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("class PATCreationResponse {\n");
        
        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    token: ").append(toIndentedString(token)).append("\n");
        sb.append("    alias: ").append(toIndentedString(alias)).append("\n");
        sb.append("    description: ").append(toIndentedString(description)).append("\n");
        sb.append("    scope: ").append(toIndentedString(scope)).append("\n");
        sb.append("    validityPeriod: ").append(toIndentedString(validityPeriod)).append("\n");
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

