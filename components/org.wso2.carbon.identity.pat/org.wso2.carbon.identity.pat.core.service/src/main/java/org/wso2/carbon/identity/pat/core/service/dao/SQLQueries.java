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

        public static final String GET_TOKEN_METADATA = "SELECT IDN_OAUTH2_ACCESS_TOKEN.TOKEN_ID as TOKEN_ID, " +
                "ALIAS, DESCRIPTION, VALIDITY_PERIOD, TIME_CREATED FROM IDN_OAUTH2_ACCESS_TOKEN, IDN_PAT_DATA " +
                "WHERE IDN_OAUTH2_ACCESS_TOKEN.TOKEN_ID = IDN_PAT_DATA.TOKEN_ID AND " +
                "IDN_OAUTH2_ACCESS_TOKEN.TOKEN_ID = ? AND SUBJECT_IDENTIFIER = ? AND GRANT_TYPE = ?";

        public static final String GET_TOKEN_SCOPES = "SELECT TOKEN_SCOPE FROM IDN_OAUTH2_ACCESS_TOKEN_SCOPE " +
                "WHERE TOKEN_ID = ?";

        public static final String GET_ACCESS_TOKEN = "SELECT ACCESS_TOKEN FROM IDN_OAUTH2_ACCESS_TOKEN " +
                "WHERE TOKEN_ID = ? AND GRANT_TYPE = ?";

        public static final String GET_TOKENS_METADATA = "SELECT IDN_OAUTH2_ACCESS_TOKEN.TOKEN_ID as TOKEN_ID, " +
                "ALIAS, DESCRIPTION, VALIDITY_PERIOD, TIME_CREATED FROM IDN_OAUTH2_ACCESS_TOKEN, IDN_PAT_DATA " +
                "WHERE IDN_OAUTH2_ACCESS_TOKEN.TOKEN_ID = IDN_PAT_DATA.TOKEN_ID AND " +
                "SUBJECT_IDENTIFIER = ? AND GRANT_TYPE = ?";

        public static final String GET_CLIENT_ID_FROM_TOKEN_ID = "SELECT CONSUMER_KEY FROM " +
                "IDN_OAUTH2_ACCESS_TOKEN, IDN_OAUTH_CONSUMER_APPS " +
                "WHERE CONSUMER_KEY_ID = ID AND TOKEN_ID = ?";

//        public static final String GET_TOKEN_ID_FROM_ALIAS = "SELECT TOKEN_ID FROM IDN_OAUTH2_ACCESS_TOKEN " +
//                "NATURAL JOIN IDN_PAT_DATA WHERE SUBJECT_IDENTIFIER = ? AND ALIAS = ? AND GRANT_TYPE = ?";
        public static final String GET_TOKEN_ID_FROM_ALIAS = "SELECT IDN_OAUTH2_ACCESS_TOKEN.TOKEN_ID " +
                "FROM IDN_OAUTH2_ACCESS_TOKEN, IDN_PAT_DATA WHERE " +
                "IDN_OAUTH2_ACCESS_TOKEN.TOKEN_ID = IDN_PAT_DATA.TOKEN_ID AND SUBJECT_IDENTIFIER = ? AND ALIAS = ? " +
                "AND GRANT_TYPE = ?";
    }
}

