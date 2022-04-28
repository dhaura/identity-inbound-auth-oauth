/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package org.wso2.carbon.identity.pat.core.service.bindings.impl;

import org.wso2.carbon.identity.oauth2.dto.OAuth2AccessTokenReqDTO;
import org.wso2.carbon.identity.oauth2.token.bindings.impl.AbstractTokenBinder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Token Binding for PAT
 */
public class PATTokenBinder extends AbstractTokenBinder {
    private static String PAT_GRANT_TYPE = "pat";

    @Override
    public Optional<String> getTokenBindingValue(OAuth2AccessTokenReqDTO oAuth2AccessTokenReqDTO) {

        if (PAT_GRANT_TYPE.equals(oAuth2AccessTokenReqDTO.getGrantType())) {
            String binding_value = UUID.randomUUID().toString();
            return Optional.ofNullable(binding_value);

        } else {
            return super.getTokenBindingValue(oAuth2AccessTokenReqDTO);
        }
    }

    @Override
    public String getDisplayName() {

        return "UUID Based";
    }

    @Override
    public String getDescription() {

        return "Bind token to a unique uuid. Supported grant type : " + PAT_GRANT_TYPE;
    }

    @Override
    public String getBindingType() {

        return "pat";
    }

    @Override
    public List<String> getSupportedGrantTypes() {

        return Collections.singletonList(PAT_GRANT_TYPE);
    }

    @Override
    public String getOrGenerateTokenBindingValue(HttpServletRequest request) {

        return null;
    }

    @Override
    public void setTokenBindingValueForResponse(HttpServletResponse response, String bindingValue) {

    }

    @Override
    public void clearTokenBindingElements(HttpServletRequest request, HttpServletResponse response) {

    }

    @Override
    public boolean isValidTokenBinding(Object request, String bindingReference) {

        return true;
    }

    @Override
    public boolean isValidTokenBinding(OAuth2AccessTokenReqDTO oAuth2AccessTokenReqDTO, String bindingReference) {

        return true;
    }
}
