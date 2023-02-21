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
 * Creates required PATDAO.
 */
public class PATDAOFactory {

    // Implementation of DAO.
    private PATMgtDAO pATMgtDAOImpl;

    private PATDAOFactory() {

        // This factory creates instance of PAT DAOImplementation.
        pATMgtDAOImpl = new PATMgtDAOImpl();
    }

    private static PATDAOFactory pATDAOFactoryInstance = new PATDAOFactory();

    public static PATDAOFactory getInstance() {

        return pATDAOFactoryInstance;
    }

    /**
     * @return  PATMgtDAO.
     */
    public PATMgtDAO getPATMgtDAO() {

        return pATMgtDAOImpl;
    }
}

