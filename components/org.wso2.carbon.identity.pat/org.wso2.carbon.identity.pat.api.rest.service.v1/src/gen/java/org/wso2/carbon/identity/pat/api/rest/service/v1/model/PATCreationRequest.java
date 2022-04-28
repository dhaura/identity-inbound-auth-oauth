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

public class PATCreationRequest  {
  
    private String alias;
    private String description;
    private Integer validityPeriod;
    private List<String> scope = new ArrayList<>();

    private String clientId;

    /**
    **/
    public PATCreationRequest alias(String alias) {

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
    public PATCreationRequest description(String description) {

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
    public PATCreationRequest validityPeriod(Integer validityPeriod) {

        this.validityPeriod = validityPeriod;
        return this;
    }
    
    @ApiModelProperty(example = "2000", required = true, value = "")
    @JsonProperty("validity_period")
    @Valid
    @NotNull(message = "Property validityPeriod cannot be null.")

    public Integer getValidityPeriod() {
        return validityPeriod;
    }
    public void setValidityPeriod(Integer validityPeriod) {
        this.validityPeriod = validityPeriod;
    }

    /**
    **/
    public PATCreationRequest scope(List<String> scope) {

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

    public PATCreationRequest addScopeItem(String scopeItem) {
        this.scope.add(scopeItem);
        return this;
    }

        /**
    **/
    public PATCreationRequest clientId(String clientId) {

        this.clientId = clientId;
        return this;
    }
    
    @ApiModelProperty(example = "JM6vFAVXAAHA1Jwhwnv7n4cNIP0a", required = true, value = "")
    @JsonProperty("client_id")
    @Valid
    @NotNull(message = "Property clientId cannot be null.")

    public String getClientId() {
        return clientId;
    }
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }



    @Override
    public boolean equals(java.lang.Object o) {

        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PATCreationRequest paTCreationRequest = (PATCreationRequest) o;
        return Objects.equals(this.alias, paTCreationRequest.alias) &&
            Objects.equals(this.description, paTCreationRequest.description) &&
            Objects.equals(this.validityPeriod, paTCreationRequest.validityPeriod) &&
            Objects.equals(this.scope, paTCreationRequest.scope) &&
            Objects.equals(this.clientId, paTCreationRequest.clientId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(alias, description, validityPeriod, scope, clientId);
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("class PATCreationRequest {\n");
        
        sb.append("    alias: ").append(toIndentedString(alias)).append("\n");
        sb.append("    description: ").append(toIndentedString(description)).append("\n");
        sb.append("    validityPeriod: ").append(toIndentedString(validityPeriod)).append("\n");
        sb.append("    scope: ").append(toIndentedString(scope)).append("\n");
        sb.append("    clientId: ").append(toIndentedString(clientId)).append("\n");
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

