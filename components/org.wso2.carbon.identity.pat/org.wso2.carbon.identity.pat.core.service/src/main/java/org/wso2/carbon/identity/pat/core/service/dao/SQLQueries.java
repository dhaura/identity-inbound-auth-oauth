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
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
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

