package org.wso2.carbon.identity.pat.api.rest.service.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Objects;

public class PATBasicMetadata {
    private String id;
    private String alias;
    private String description;
    private String createdTime;
    private String expiryTime;

    /**
     **/
    public PATBasicMetadata id(String id) {

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
    public PATBasicMetadata alias(String alias) {

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
    public PATBasicMetadata description(String description) {

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
     * PAT creation time in ISO Date and Time format
     **/
    public PATBasicMetadata createdTime(String createdTime) {

        this.createdTime = createdTime;
        return this;
    }

    @ApiModelProperty(example = "2022-03-22T03:28:41Z", required = true, value = "PAT creation time in ISO Date and Time format")
    @JsonProperty("createdTime")
    @Valid
    @NotNull(message = "Property createdTime cannot be null.")

    public String getCreatedTime() {
        return createdTime;
    }
    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    /**
     * PAT expiry time in ISO Date and Time format
     **/
    public PATBasicMetadata expiryTime(String expiryTime) {

        this.expiryTime = expiryTime;
        return this;
    }

    @ApiModelProperty(example = "2022-03-22T03:58:41Z", required = true, value = "PAT expiry time in ISO Date and Time format")
    @JsonProperty("expiryTime")
    @Valid
    @NotNull(message = "Property expiryTime cannot be null.")

    public String getExpiryTime() {
        return expiryTime;
    }
    public void setExpiryTime(String expiryTime) {
        this.expiryTime = expiryTime;
    }


    @Override
    public boolean equals(java.lang.Object o) {

        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PATBasicMetadata patBasicMetadata = (PATBasicMetadata) o;
        return Objects.equals(this.id, patBasicMetadata.id) &&
                Objects.equals(this.alias, patBasicMetadata.alias) &&
                Objects.equals(this.description, patBasicMetadata.description) &&
                Objects.equals(this.createdTime, patBasicMetadata.createdTime) &&
                Objects.equals(this.expiryTime, patBasicMetadata.expiryTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, alias, description, createdTime, expiryTime);
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("class PATBasicMetadata {\n");

        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    alias: ").append(toIndentedString(alias)).append("\n");
        sb.append("    description: ").append(toIndentedString(description)).append("\n");
        sb.append("    createdTime: ").append(toIndentedString(createdTime)).append("\n");
        sb.append("    expiryTime: ").append(toIndentedString(expiryTime)).append("\n");
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
