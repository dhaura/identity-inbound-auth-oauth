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
import org.wso2.carbon.identity.pat.core.service.exeptions.PATManagementClientException;
import org.wso2.carbon.identity.pat.core.service.exeptions.PATManagementException;
import org.wso2.carbon.identity.pat.core.service.exeptions.PATManagementServerException;
import org.wso2.carbon.identity.pat.core.service.model.PATViewMetadata;

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
    public void insertPATData(String tokenID, String alias, String description) throws PATManagementServerException {

        try (Connection connection = IdentityDatabaseUtil.getDBConnection(true)) {
            try (PreparedStatement prepStmt = connection.prepareStatement(SQLQueries.
                    PATSQLQueries.STORE_PAT_DATA)) {

                prepStmt.setString(1, tokenID);
                prepStmt.setString(2, alias);
                prepStmt.setString(3, description);
                prepStmt.execute();
                IdentityDatabaseUtil.commitTransaction(connection);

            } catch (SQLException e) {
                IdentityDatabaseUtil.rollbackTransaction(connection);
                throw new PATManagementServerException(PATConstants.ErrorMessage.ERROR_CREATING_PAT);
            }
        } catch (SQLException e) {
            throw new PATManagementServerException(PATConstants.ErrorMessage.ERROR_CREATING_PAT);
        }
    }

    @Override
    public PATViewMetadata getPATMetadata(String tokenID, String userID) throws PATManagementException {

        try (Connection connection = IdentityDatabaseUtil.getDBConnection(true)) {
            try (PreparedStatement prepStmt = connection.prepareStatement(SQLQueries.
                    PATSQLQueries.GET_TOKEN_METADATA)) {

                prepStmt.setString(1, tokenID);
                prepStmt.setString(2, userID);
                prepStmt.setString(3, PATConstants.PAT);
                ResultSet resultSet = prepStmt.executeQuery();

                if (resultSet.next()) {
                    return getPATViewMetadata(resultSet);
                }
                throw new PATManagementClientException(PATConstants.ErrorMessage.ERROR_CODE_INVALID_TOKEN_ID);


            } catch (SQLException e) {
                IdentityDatabaseUtil.rollbackTransaction(connection);
                throw new PATManagementServerException(PATConstants.ErrorMessage.ERROR_RETRIEVING_TOKEN_METADATA);
            }
        } catch (SQLException e) {
            throw new PATManagementServerException(PATConstants.ErrorMessage.ERROR_RETRIEVING_TOKEN_METADATA);

        }
    }

    @Override
    public List<PATViewMetadata> getPATsMetadata(String userID) throws PATManagementException {

        try (Connection connection = IdentityDatabaseUtil.getDBConnection(true)) {
            try (PreparedStatement prepStmt = connection.prepareStatement(SQLQueries.
                    PATSQLQueries.GET_TOKENS_METADATA)) {

                prepStmt.setString(1, userID);
                prepStmt.setString(2, PATConstants.PAT);
                ResultSet resultSet = prepStmt.executeQuery();

                PATViewMetadata patViewMetadata;
                List<PATViewMetadata> patViewMetadataList = new ArrayList<>();

                while (resultSet.next()) {
                    patViewMetadata = getPATViewMetadata(resultSet);
                    patViewMetadataList.add(patViewMetadata);
                }

                return patViewMetadataList;

            } catch (SQLException e) {
                IdentityDatabaseUtil.rollbackTransaction(connection);
                throw new PATManagementServerException(PATConstants.ErrorMessage.ERROR_RETRIEVING_TOKEN_METADATA);
            }
        } catch (SQLException e) {
            throw new PATManagementServerException(PATConstants.ErrorMessage.ERROR_RETRIEVING_TOKEN_METADATA);
        }
    }

    @Override
    public List<String> getPATScopes(String tokenID) throws PATManagementException {

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
                throw new PATManagementServerException(PATConstants.ErrorMessage.ERROR_RETRIEVING_TOKEN_SCOPES);
            }
        } catch (SQLException e) {
            throw new PATManagementServerException(PATConstants.ErrorMessage.ERROR_RETRIEVING_TOKEN_SCOPES);
        }
    }

    @Override
    public String getPAT(String tokenID) throws PATManagementException {

        try (Connection connection = IdentityDatabaseUtil.getDBConnection(true)) {
            try (PreparedStatement prepStmt = connection.prepareStatement(SQLQueries.
                    PATSQLQueries.GET_ACCESS_TOKEN)) {

                prepStmt.setString(1, tokenID);
                prepStmt.setString(2, PATConstants.PAT);
                ResultSet resultSet = prepStmt.executeQuery();

                if (resultSet.next()) {
                    String accessToken = resultSet.getString(PATConstants.ACCESS_TOKEN);
                    return accessToken;
                } else {
                    throw new PATManagementClientException(PATConstants.ErrorMessage.ERROR_CODE_INVALID_TOKEN_ID);
                }


            } catch (SQLException e) {
                IdentityDatabaseUtil.rollbackTransaction(connection);
                throw new PATManagementServerException(PATConstants.ErrorMessage.ERROR_RETRIEVING_PAT);
            }
        } catch (SQLException e) {
            throw new PATManagementServerException(PATConstants.ErrorMessage.ERROR_RETRIEVING_PAT);
        }
    }

    @Override
    public String getClientIDFromTokenID(String tokenID) throws PATManagementException {

        try (Connection connection = IdentityDatabaseUtil.getDBConnection(true)) {
            try (PreparedStatement prepStmt = connection.prepareStatement(SQLQueries.
                    PATSQLQueries.GET_CLIENT_ID_FROM_TOKEN_ID)) {

                prepStmt.setString(1, tokenID);
                ResultSet resultSet = prepStmt.executeQuery();

                if (resultSet.next()) {
                    String clientID = resultSet.getString(PATConstants.CONSUMER_KEY);
                    return clientID;
                }
                throw new PATManagementClientException(PATConstants.ErrorMessage.ERROR_CODE_INVALID_TOKEN_ID);


            } catch (SQLException e) {
                IdentityDatabaseUtil.rollbackTransaction(connection);
                throw new PATManagementServerException(PATConstants.ErrorMessage.ERROR_RETRIEVING_CLIENT_ID);
            }
        } catch (SQLException e) {
            throw new PATManagementServerException(PATConstants.ErrorMessage.ERROR_RETRIEVING_CLIENT_ID);
        }
    }

    @Override
    public boolean isDuplicatedAlias(String userId, String alias) throws PATManagementException {

        try (Connection connection = IdentityDatabaseUtil.getDBConnection(true)) {
            try (PreparedStatement prepStmt = connection.prepareStatement(SQLQueries.
                    PATSQLQueries.GET_TOKEN_ID_FROM_ALIAS)) {

                prepStmt.setString(1, userId);
                prepStmt.setString(2, alias);
                prepStmt.setString(3, PATConstants.PAT);
                ResultSet resultSet = prepStmt.executeQuery();

                if (resultSet.next()) {
                    return true;
                }
                return false;

            } catch (SQLException e) {
                IdentityDatabaseUtil.rollbackTransaction(connection);
                throw new PATManagementServerException(PATConstants.ErrorMessage.ERROR_VALIDATING_DUPLICATED_ALIAS);
            }
        } catch (SQLException e) {
            throw new PATManagementServerException(PATConstants.ErrorMessage.ERROR_VALIDATING_DUPLICATED_ALIAS);
        }
    }

    private PATViewMetadata getPATViewMetadata(ResultSet resultSet)
            throws PATManagementServerException, SQLException {

        PATViewMetadata patViewMetadata = new PATViewMetadata();
        patViewMetadata.setTokenId(resultSet.getString(PATConstants.TOKEN_ID));
        patViewMetadata.setAlias(resultSet.getString(PATConstants.ALIAS));
        patViewMetadata.setDescription(resultSet.getString(PATConstants.DESCRIPTION));

        int validityPeriod = (int) TimeUnit.MILLISECONDS.toSeconds(resultSet
                .getInt(PATConstants.VALIDITY_PERIOD));
        String timeCreated = resultSet.getString(PATConstants.TIME_CREATED);
        String expiryTime = getExpiryTime(validityPeriod, timeCreated);
        patViewMetadata.setTimeCreated(getISOStandardTime(timeCreated));
        patViewMetadata.setExpiryTime(getISOStandardTime(expiryTime));

        return patViewMetadata;
    }

    private String getExpiryTime(int validityPeriod, String timeCreated) throws PATManagementServerException {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date timeCreatedObj = null;
        try {
            timeCreatedObj = formatter.parse(timeCreated);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(timeCreatedObj);
            calendar.add(Calendar.SECOND, validityPeriod);

            return formatter.format(calendar.getTime());
        } catch (ParseException e) {
            throw new PATManagementServerException(PATConstants.ErrorMessage.ERROR_RETRIEVING_TOKEN_METADATA);
        }
    }

    private String getISOStandardTime(String time) throws PATManagementServerException {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date;
        try {
            date = formatter.parse(time);
        } catch (ParseException e) {
            throw new PATManagementServerException(PATConstants.ErrorMessage.ERROR_RETRIEVING_TOKEN_METADATA);
        }

        formatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
        return formatter.format(date);
    }


}
