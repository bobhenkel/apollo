package io.logz.apollo.common;

import com.google.common.base.Splitter;

import java.util.Map;

/**
 * Created by roiravhon on 5/29/17.
 */
public class QueryStringParser {

    public static int getIntFromQueryString(String queryString, String key) {
        Map<String, String> queryStringParams = Splitter.on('&').trimResults().withKeyValueSeparator('=').split(queryString);
        return Integer.parseInt(queryStringParams.get(key));
    }
}
