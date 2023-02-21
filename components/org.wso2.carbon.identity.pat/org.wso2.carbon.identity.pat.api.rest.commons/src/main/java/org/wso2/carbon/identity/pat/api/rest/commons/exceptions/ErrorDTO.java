/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package org.wso2.carbon.identity.pat.api.rest.commons.exceptions;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Common DTO for all the API related error responses.
 */
@ApiModel(description = "")
public class ErrorDTO implements Serializable {

    private static final long serialVersionUID = 1984069451956875663L;

    @NotNull
    private String code = null;

    @NotNull
    private String message = null;

    private String description = null;

    private String ref = null;

    /**
     * Get code.
     **/
    @ApiModelProperty(required = true, value = "")
    @JsonProperty("code")
    public String getCode() {

        return code;
    }

    /**
     * Set code.
     **/
    public void setCode(String code) {

        this.code = code;
    }

    /**
     * Get Message.
     **/
    @ApiModelProperty(required = true, value = "")
    @JsonProperty("message")
    public String getMessage() {

        return message;
    }

    /**
     * Set Message.
     **/
    public void setMessage(String message) {

        this.message = message;
    }

    /**
     * Get description.
     **/
    @ApiModelProperty(value = "")
    @JsonProperty("description")
    public String getDescription() {

        return description;
    }

    /**
     * Set description.
     **/
    public void setDescription(String description) {

        this.description = description;
    }

    /**
     * Get traceId.
     **/
    @ApiModelProperty(value = "")
    @JsonProperty("traceId")
    public String getRef() {

        return ref;
    }

    /**
     * Set traceId.
     **/
    public void setRef(String ref) {

        this.ref = ref;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("class ErrorDTO {\n");

        sb.append("  code: ").append(code).append("\n");
        sb.append("  message: ").append(message).append("\n");
        sb.append("  description: ").append(description).append("\n");
        sb.append("  traceId: ").append(ref).append("\n");
        sb.append("}\n");
        return sb.toString();
    }
}
