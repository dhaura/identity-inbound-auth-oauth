package org.wso2.carbon.identity.pat.api.rest.commons.util;

import org.apache.log4j.MDC;
import org.wso2.carbon.identity.pat.api.rest.commons.Constants;

import java.util.UUID;

public class Utils {

    /**
     * Get correlation id of current thread.
     *
     * @return Correlation-id.
     */
    public static String getCorrelation() {

        if (isCorrelationIDPresent()) {
            return MDC.get(Constants.CORRELATION_ID_MDC).toString();
        }
        return UUID.randomUUID().toString();
    }

    /**
     * Check whether correlation id present in the log MDC.
     *
     * @return Whether the correlation id is present.
     */
    public static boolean isCorrelationIDPresent() {

        return MDC.get(Constants.CORRELATION_ID_MDC) != null;
    }
}
