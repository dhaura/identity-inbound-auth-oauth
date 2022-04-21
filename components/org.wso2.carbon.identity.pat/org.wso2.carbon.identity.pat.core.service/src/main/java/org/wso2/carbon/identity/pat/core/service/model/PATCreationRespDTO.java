package org.wso2.carbon.identity.pat.core.service.model;

import java.util.ArrayList;
import java.util.List;

public class PATCreationRespDTO {
    private String accessToken;
    private List<String> scope = new ArrayList<>();

    private long validityPeriod;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public List<String> getScope() {
        return scope;
    }

    public void setScope(List<String> scope) {
        this.scope = scope;
    }

    public long getValidityPeriod() {
        return validityPeriod;
    }

    public void setValidityPeriod(long validityPeriod) {
        this.validityPeriod = validityPeriod;
    }
}
