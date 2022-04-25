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

package org.wso2.carbon.identity.pat.core.service.dao;

import org.wso2.carbon.identity.core.util.IdentityDatabaseUtil;
import org.wso2.carbon.identity.pat.core.service.common.PATConstants;
import org.wso2.carbon.identity.pat.core.service.model.TokenMetadataDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of abstract DAO layer.
 */
public class PATMgtDAOImpl implements PATMgtDAO {

    @Override
    public void insertPATData(String tokenID, String alias, String description) {

        try (Connection connection = IdentityDatabaseUtil.getDBConnection(true)) {
            try (PreparedStatement prepStmt = connection.prepareStatement(SQLQueries.
                    PATSQLQueries.STORE_PAT_DATA)) {

                prepStmt.setString(1, tokenID);
                prepStmt.setString(2, alias);
                prepStmt.setString(3, description);
                prepStmt.execute();
                IdentityDatabaseUtil.commitTransaction(connection);

            } catch (SQLException e) {
                // TODO: handle exception
                IdentityDatabaseUtil.rollbackTransaction(connection);
            }
        } catch (SQLException e) {
            // TODO: handle exception
        }
    }

    @Override
    public TokenMetadataDTO getTokenMetadata(String tokenID) {
        try (Connection connection = IdentityDatabaseUtil.getDBConnection(true)) {
            try (PreparedStatement prepStmt = connection.prepareStatement(SQLQueries.
                    PATSQLQueries.GET_TOKEN_METADATA)) {

                prepStmt.setString(1, tokenID);
                prepStmt.setString(2, PATConstants.PAT);
                ResultSet resultSet = prepStmt.executeQuery();

                if (resultSet.next()){
                    TokenMetadataDTO tokenMetadataDTO = new TokenMetadataDTO();
                    tokenMetadataDTO.setTokenId(resultSet.getString(PATConstants.TOKEN_ID));
                    tokenMetadataDTO.setAlias(resultSet.getString(PATConstants.ALIAS));
                    tokenMetadataDTO.setDescription(resultSet.getString(PATConstants.DESCRIPTION));
                    tokenMetadataDTO.setValidityPeriod((int) TimeUnit.MILLISECONDS.toSeconds(resultSet.getInt(PATConstants.VALIDITY_PERIOD)));
                    tokenMetadataDTO.setTimeCreated(resultSet.getString(PATConstants.TIME_CREATED));
                    return tokenMetadataDTO;
                }
                // TODO: handle exception
                return null;


            } catch (SQLException e) {
                // TODO: handle exception
                IdentityDatabaseUtil.rollbackTransaction(connection);
                return null;
            }
        } catch (SQLException e) {
            // TODO: handle exception
            return null;
        }
    }

    @Override
    public List<TokenMetadataDTO> getTokensMetadata(String userID) {
        try (Connection connection = IdentityDatabaseUtil.getDBConnection(true)) {
            try (PreparedStatement prepStmt = connection.prepareStatement(SQLQueries.
                    PATSQLQueries.GET_TOKENS_METADATA)) {

                prepStmt.setString(1, userID);
                prepStmt.setString(2, PATConstants.PAT);
                ResultSet resultSet = prepStmt.executeQuery();

                String tokenID = null;
                TokenMetadataDTO tokenMetadataDTO = null;
                List<TokenMetadataDTO> tokenMetadataDTOList = new ArrayList<>();
                List<String> scopes = null;

                while (resultSet.next()){
                    if (tokenID == null || !tokenID.equals(resultSet.getString(PATConstants.TOKEN_ID))){
                        if (tokenID != null){
                            tokenMetadataDTO.setScope(scopes);
                            tokenMetadataDTOList.add(tokenMetadataDTO);
                        }
                        tokenID = resultSet.getString(PATConstants.TOKEN_ID);

                        scopes = new ArrayList<>();
                        scopes.add(resultSet.getString(PATConstants.TOKEN_SCOPE));

                        tokenMetadataDTO = new TokenMetadataDTO();
                        tokenMetadataDTO.setTokenId(tokenID);
                        tokenMetadataDTO.setAlias(resultSet.getString(PATConstants.ALIAS));
                        tokenMetadataDTO.setDescription(resultSet.getString(PATConstants.DESCRIPTION));
                        tokenMetadataDTO.setValidityPeriod((int) TimeUnit.MILLISECONDS.toSeconds(resultSet.getInt(PATConstants.VALIDITY_PERIOD)));
                        tokenMetadataDTO.setTimeCreated(resultSet.getString(PATConstants.TIME_CREATED));
                    }else{
                        scopes.add(resultSet.getString(PATConstants.TOKEN_SCOPE));
                    }
                }

                if (tokenMetadataDTO != null){
                    tokenMetadataDTO.setScope(scopes);
                    tokenMetadataDTOList.add(tokenMetadataDTO);
                    return tokenMetadataDTOList;
                }

                // TODO: handle exception
                return null;


            } catch (SQLException e) {
                // TODO: handle exception
                IdentityDatabaseUtil.rollbackTransaction(connection);
                return null;
            }
        } catch (SQLException e) {
            // TODO: handle exception
            return null;
        }
    }

    @Override
    public List<String> getTokenScopes(String tokenID) {
        try (Connection connection = IdentityDatabaseUtil.getDBConnection(true)) {
            try (PreparedStatement prepStmt = connection.prepareStatement(SQLQueries.
                    PATSQLQueries.GET_TOKEN_SCOPES)) {

                prepStmt.setString(1, tokenID);
                ResultSet resultSet = prepStmt.executeQuery();

                List<String> scopes = new ArrayList<>();

                while (resultSet.next()){
                    scopes.add(resultSet.getString(PATConstants.TOKEN_SCOPE));
                }
                return scopes;


            } catch (SQLException e) {
                // TODO: handle exception
                IdentityDatabaseUtil.rollbackTransaction(connection);
                return null;
            }
        } catch (SQLException e) {
            // TODO: handle exception
            return null;
        }
    }

    @Override
    public String getAccessToken(String tokenID) {
        try (Connection connection = IdentityDatabaseUtil.getDBConnection(true)) {
            try (PreparedStatement prepStmt = connection.prepareStatement(SQLQueries.
                    PATSQLQueries.GET_ACCESS_TOKEN)) {

                prepStmt.setString(1, tokenID);
                ResultSet resultSet = prepStmt.executeQuery();

                if (resultSet.next()){
                    String accessToken = resultSet.getString(PATConstants.ACCESS_TOKEN);
                    return accessToken;
                }
                // TODO: handle exception
                return null;


            } catch (SQLException e) {
                // TODO: handle exception
                IdentityDatabaseUtil.rollbackTransaction(connection);
                return null;
            }
        } catch (SQLException e) {
            // TODO: handle exception
            return null;
        }
    }

    @Override
    public String getClientIDFromTokenID(String tokenID) {
        try (Connection connection = IdentityDatabaseUtil.getDBConnection(true)) {
            try (PreparedStatement prepStmt = connection.prepareStatement(SQLQueries.
                    PATSQLQueries.GET_CLIENT_ID_FROM_TOKEN_ID)) {

                prepStmt.setString(1, tokenID);
                ResultSet resultSet = prepStmt.executeQuery();

                if (resultSet.next()){
                    String clientID = resultSet.getString(PATConstants.INBOUND_AUTH_KEY);
                    return clientID;
                }
                // TODO: handle exception
                return null;


            } catch (SQLException e) {
                // TODO: handle exception
                IdentityDatabaseUtil.rollbackTransaction(connection);
                return null;
            }
        } catch (SQLException e) {
            // TODO: handle exception
            return null;
        }
    }


}
