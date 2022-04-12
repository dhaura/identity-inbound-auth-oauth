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
  
    private String accessToken;
    private List<String> scope = new ArrayList<>();

    private Integer validityPeriod;

    /**
    **/
    public PATCreationResponse accessToken(String accessToken) {

        this.accessToken = accessToken;
        return this;
    }
    
    @ApiModelProperty(example = "6edb0049-0ea3-3b26-9aa3-152b1891d200", required = true, value = "")
    @JsonProperty("access_token")
    @Valid
    @NotNull(message = "Property accessToken cannot be null.")

    public String getAccessToken() {
        return accessToken;
    }
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
    **/
    public PATCreationResponse scope(List<String> scope) {

        this.scope = scope;
        return this;
    }
    
    @ApiModelProperty(example = "[\"openid\",\"address\"]", required = true, value = "")
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
    @JsonProperty("validity_period")
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
        return Objects.equals(this.accessToken, paTCreationResponse.accessToken) &&
            Objects.equals(this.scope, paTCreationResponse.scope) &&
            Objects.equals(this.validityPeriod, paTCreationResponse.validityPeriod);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accessToken, scope, validityPeriod);
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("class PATCreationResponse {\n");
        
        sb.append("    accessToken: ").append(toIndentedString(accessToken)).append("\n");
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

