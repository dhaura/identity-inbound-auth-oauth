/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package org.wso2.carbon.identity.pat.core.service.dao;

import org.wso2.carbon.identity.core.util.IdentityDatabaseUtil;
import org.wso2.carbon.identity.pat.core.service.common.PATConstants;
import org.wso2.carbon.identity.pat.core.service.exeptions.PATClientException;
import org.wso2.carbon.identity.pat.core.service.exeptions.PATException;
import org.wso2.carbon.identity.pat.core.service.exeptions.PATServerException;
import org.wso2.carbon.identity.pat.core.service.model.TokenMetadataDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
    public TokenMetadataDTO getTokenMetadata(String tokenID, String userID) throws PATException {
        try (Connection connection = IdentityDatabaseUtil.getDBConnection(true)) {
            try (PreparedStatement prepStmt = connection.prepareStatement(SQLQueries.
                    PATSQLQueries.GET_TOKEN_METADATA)) {

                prepStmt.setString(1, tokenID);
                prepStmt.setString(2, userID);
                prepStmt.setString(3, PATConstants.PAT);
                ResultSet resultSet = prepStmt.executeQuery();

                if (resultSet.next()) {
                    return getTokenMetadataDTO(resultSet);
                }
                throw new PATClientException(
                        PATConstants.ErrorMessage.ERROR_CODE_INVALID_TOKEN_ID.getCode(),
                        PATConstants.ErrorMessage.ERROR_CODE_INVALID_TOKEN_ID.getMessage());


            } catch (SQLException e) {
                IdentityDatabaseUtil.rollbackTransaction(connection);
                throw new PATServerException(
                        PATConstants.ErrorMessage.ERROR_RETRIEVING_TOKEN_METADATA.getCode(),
                        PATConstants.ErrorMessage.ERROR_RETRIEVING_TOKEN_METADATA.getMessage());
            }
        } catch (SQLException e) {
            throw new PATServerException(
                    PATConstants.ErrorMessage.ERROR_RETRIEVING_TOKEN_METADATA.getCode(),
                    PATConstants.ErrorMessage.ERROR_RETRIEVING_TOKEN_METADATA.getMessage());
        }
    }

    @Override
    public List<TokenMetadataDTO> getTokensMetadata(String userID) throws PATException {
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

                while (resultSet.next()) {
                    if (tokenID == null || !tokenID.equals(resultSet.getString(PATConstants.TOKEN_ID))) {
                        if (tokenID != null) {
                            tokenMetadataDTO.setScope(scopes);
                            tokenMetadataDTOList.add(tokenMetadataDTO);
                        }
                        tokenID = resultSet.getString(PATConstants.TOKEN_ID);

                        scopes = new ArrayList<>();
                        scopes.add(resultSet.getString(PATConstants.TOKEN_SCOPE));

                        tokenMetadataDTO = getTokenMetadataDTO(resultSet);
                    } else {
                        scopes.add(resultSet.getString(PATConstants.TOKEN_SCOPE));
                    }
                }

                if (tokenMetadataDTO != null) {
                    tokenMetadataDTO.setScope(scopes);
                    tokenMetadataDTOList.add(tokenMetadataDTO);
                }

                return tokenMetadataDTOList;

            } catch (SQLException e) {
                IdentityDatabaseUtil.rollbackTransaction(connection);
                throw new PATServerException(
                        PATConstants.ErrorMessage.ERROR_RETRIEVING_TOKEN_METADATA.getCode(),
                        PATConstants.ErrorMessage.ERROR_RETRIEVING_TOKEN_METADATA.getMessage());
            }
        } catch (SQLException e) {
            throw new PATServerException(
                    PATConstants.ErrorMessage.ERROR_RETRIEVING_TOKEN_METADATA.getCode(),
                    PATConstants.ErrorMessage.ERROR_RETRIEVING_TOKEN_METADATA.getMessage());
        }
    }

    @Override
    public List<String> getTokenScopes(String tokenID) throws PATException {
        try (Connection connection = IdentityDatabaseUtil.getDBConnection(true)) {
            try (PreparedStatement prepStmt = connection.prepareStatement(SQLQueries.
                    PATSQLQueries.GET_TOKEN_SCOPES)) {

                prepStmt.setString(1, tokenID);
                ResultSet resultSet = prepStmt.executeQuery();

                List<String> scopes = new ArrayList<>();

                while (resultSet.next()) {
                    scopes.add(resultSet.getString(PATConstants.TOKEN_SCOPE));
                }
                return scopes;


            } catch (SQLException e) {
                IdentityDatabaseUtil.rollbackTransaction(connection);
                throw new PATServerException(
                        PATConstants.ErrorMessage.ERROR_RETRIEVING_TOKEN_SCOPES.getCode(),
                        PATConstants.ErrorMessage.ERROR_RETRIEVING_TOKEN_SCOPES.getMessage());
            }
        } catch (SQLException e) {
            throw new PATServerException(
                    PATConstants.ErrorMessage.ERROR_RETRIEVING_TOKEN_SCOPES.getCode(),
                    PATConstants.ErrorMessage.ERROR_RETRIEVING_TOKEN_SCOPES.getMessage());
        }
    }

    @Override
    public String getAccessToken(String tokenID) throws PATException {
        try (Connection connection = IdentityDatabaseUtil.getDBConnection(true)) {
            try (PreparedStatement prepStmt = connection.prepareStatement(SQLQueries.
                    PATSQLQueries.GET_ACCESS_TOKEN)) {

                prepStmt.setString(1, tokenID);
                ResultSet resultSet = prepStmt.executeQuery();

                if (resultSet.next()) {
                    String accessToken = resultSet.getString(PATConstants.ACCESS_TOKEN);
                    return accessToken;
                } else {
                    throw new PATClientException(
                            PATConstants.ErrorMessage.ERROR_CODE_INVALID_TOKEN_ID.getCode(),
                            PATConstants.ErrorMessage.ERROR_CODE_INVALID_TOKEN_ID.getMessage());
                }


            } catch (SQLException e) {
                IdentityDatabaseUtil.rollbackTransaction(connection);
                throw new PATServerException(
                        PATConstants.ErrorMessage.ERROR_RETRIEVING_PAT.getCode(),
                        PATConstants.ErrorMessage.ERROR_RETRIEVING_PAT.getMessage());
            }
        } catch (SQLException e) {
            throw new PATServerException(
                    PATConstants.ErrorMessage.ERROR_RETRIEVING_PAT.getCode(),
                    PATConstants.ErrorMessage.ERROR_RETRIEVING_PAT.getMessage());
        }
    }

    @Override
    public String getClientIDFromTokenID(String tokenID) throws PATException {
        try (Connection connection = IdentityDatabaseUtil.getDBConnection(true)) {
            try (PreparedStatement prepStmt = connection.prepareStatement(SQLQueries.
                    PATSQLQueries.GET_CLIENT_ID_FROM_TOKEN_ID)) {

                prepStmt.setString(1, tokenID);
                ResultSet resultSet = prepStmt.executeQuery();

                if (resultSet.next()) {
                    String clientID = resultSet.getString(PATConstants.INBOUND_AUTH_KEY);
                    return clientID;
                }
                throw new PATClientException(
                        PATConstants.ErrorMessage.ERROR_CODE_INVALID_TOKEN_ID.getCode(),
                        PATConstants.ErrorMessage.ERROR_CODE_INVALID_TOKEN_ID.getMessage());


            } catch (SQLException e) {
                IdentityDatabaseUtil.rollbackTransaction(connection);
                throw new PATServerException(
                        PATConstants.ErrorMessage.ERROR_RETRIEVING_CLIENT_ID.getCode(),
                        PATConstants.ErrorMessage.ERROR_RETRIEVING_CLIENT_ID.getMessage());
            }
        } catch (SQLException e) {
            throw new PATServerException(
                    PATConstants.ErrorMessage.ERROR_RETRIEVING_CLIENT_ID.getCode(),
                    PATConstants.ErrorMessage.ERROR_RETRIEVING_CLIENT_ID.getMessage());
        }
    }

    private TokenMetadataDTO getTokenMetadataDTO(ResultSet resultSet) throws PATServerException, SQLException {

        TokenMetadataDTO tokenMetadataDTO = new TokenMetadataDTO();
        tokenMetadataDTO.setTokenId(resultSet.getString(PATConstants.TOKEN_ID));
        tokenMetadataDTO.setAlias(resultSet.getString(PATConstants.ALIAS));
        tokenMetadataDTO.setDescription(resultSet.getString(PATConstants.DESCRIPTION));

        int validityPeriod = (int) TimeUnit.MILLISECONDS.toSeconds(resultSet
                .getInt(PATConstants.VALIDITY_PERIOD));
        String timeCreated = resultSet.getString(PATConstants.TIME_CREATED);
        String expiryTime = getExpiryTime(validityPeriod, timeCreated);
        tokenMetadataDTO.setTimeCreated(getISOStandardTime(timeCreated));
        tokenMetadataDTO.setExpiryTime(getISOStandardTime(expiryTime));

        return tokenMetadataDTO;
    }

    private String getExpiryTime(int validityPeriod, String timeCreated) throws PATServerException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date timeCreatedObj = null;
        try {
            timeCreatedObj = formatter.parse(timeCreated);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(timeCreatedObj);
            calendar.add(Calendar.SECOND, validityPeriod);

            return formatter.format(calendar.getTime());
        } catch (ParseException e) {
            throw new PATServerException(
                    PATConstants.ErrorMessage.ERROR_RETRIEVING_TOKEN_METADATA.getCode(),
                    PATConstants.ErrorMessage.ERROR_RETRIEVING_TOKEN_METADATA.getMessage());
        }
    }

    private String getISOStandardTime(String time) throws PATServerException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date;
        try {
            date = formatter.parse(time);
        } catch (ParseException e) {
            throw new PATServerException(
                    PATConstants.ErrorMessage.ERROR_RETRIEVING_TOKEN_METADATA.getCode(),
                    PATConstants.ErrorMessage.ERROR_RETRIEVING_TOKEN_METADATA.getMessage());
        }

        formatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
        return formatter.format(date);
    }


}
