/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein in any form is strictly forbidden, unless permitted by WSO2 expressly.
 * You may not alter or remove any copyright or other notice from copies of this content.
 */

package org.wso2.carbon.identity.pat.core.service.dao;

/**
 * Class for SQL Queries.
 */

public class SQLQueries {

    /**
     * Constructor for SQL queries.
     */

    private SQLQueries() {

    }

    /**
     * SQL queries.
     */
    public static class PATSQLQueries {

        public static final String STORE_PAT_DATA = "INSERT INTO IDN_PAT_DATA " +
                "VALUES (?,?,?)";

        public static final String GET_TOKEN_METADATA = "SELECT TOKEN_ID, ALIAS, DESCRIPTION, VALIDITY_PERIOD, " +
                "TIME_CREATED FROM IDN_OAUTH2_ACCESS_TOKEN NATURAL JOIN IDN_PAT_DATA "+
                "WHERE TOKEN_ID = ? AND GRANT_TYPE = ?";

        public static final String GET_TOKEN_SCOPES = "SELECT TOKEN_SCOPE FROM IDN_OAUTH2_ACCESS_TOKEN_SCOPE " +
                "WHERE TOKEN_ID = ?";

        public static final String GET_ACCESS_TOKEN = "SELECT ACCESS_TOKEN FROM IDN_OAUTH2_ACCESS_TOKEN " +
                "WHERE TOKEN_ID = ?";

        public static final String GET_TOKENS_METADATA = "SELECT TOKEN_ID, ALIAS, DESCRIPTION, VALIDITY_PERIOD, " +
                "TIME_CREATED, TOKEN_SCOPE FROM IDN_OAUTH2_ACCESS_TOKEN NATURAL JOIN IDN_PAT_DATA NATURAL JOIN " +
                "IDN_OAUTH2_ACCESS_TOKEN_SCOPE WHERE SUBJECT_IDENTIFIER = ? AND GRANT_TYPE = ?  ORDER BY TOKEN_ID";

        public static final String GET_CLIENT_ID_FROM_TOKEN_ID = "SELECT INBOUND_AUTH_KEY FROM IDN_OAUTH2_ACCESS_TOKEN " +
                "JOIN SP_INBOUND_AUTH WHERE CONSUMER_KEY_ID = APP_ID AND TOKEN_ID = ?";
    }
}

