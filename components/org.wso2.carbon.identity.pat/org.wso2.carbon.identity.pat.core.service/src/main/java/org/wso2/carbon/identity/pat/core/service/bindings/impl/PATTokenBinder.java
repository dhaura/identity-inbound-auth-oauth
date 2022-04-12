/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
