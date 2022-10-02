package com.igor101.tdd;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
We need to provide two functionalities for full/absolute and relative url paths.
* absolute: https://google.com/search
* relative: /search

First functionality is to parse query params from url path.
Examples:
https://google.com?a=1&a=12&b=33 -> { a: [1, 12], b: [2] }
/?a=101 -> { a : [101] }
https://google.com -> {}
/ -> {}

Second functionality is to parse path variables from url path and variables template.
Examples:
https://google.com/users/1, /users/:id/ -> { id: 1 }
/orders/12/list/87-add-ab-12, /orders/:orderId/list/:listId -> { orderId: 12, listId: 87-add-ab-12 }

In case of a non-compatible template, throw meaningful exception.
*/
public class UrlPathParser {

    public static final String QUERY_PARAMS_PREFIX = "\\?";
    public static final String PATH_VARIABLE_PREFIX = ":";

    public static Map<String, List<String>> queryParams(String path) {
        var pathQueryParams = path.split(QUERY_PARAMS_PREFIX);
        if (pathQueryParams.length < 2) {
            return Map.of();
        }

        var queryParams = pathQueryParams[1];
        var queryParamsPairs = queryParams.split("&");

        var queryParamsMap = new HashMap<String, List<String>>();

        for (var queryParamPair : queryParamsPairs) {
            var keyValue = queryParamPair.split("=");
            if (keyValue.length > 1) {
                var key = keyValue[0];
                var value = keyValue[1];
                queryParamsMap.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
            }
        }

        return queryParamsMap;
    }

    public static Map<String, String> pathVariables(String path, String template) {
        var pathParts = pathPartsFromUrlOrPath(path);
        var templateParts = template.split("/");

        validatePathVariablesTemplate(pathParts, templateParts);

        var pathVariablesMap = new HashMap<String, String>();

        for (int i = 0; i < templateParts.length; i++) {
            var part = templateParts[i];

            if (part.startsWith(PATH_VARIABLE_PREFIX)) {
                var variableName = part.substring(1);
                var variableValue = pathParts[i];

                pathVariablesMap.put(variableName, variableValue);
            }
        }

        return pathVariablesMap;
    }

    private static String[] pathPartsFromUrlOrPath(String path) {
        String sanitizedPath;
        if (path.contains("//")) {
            try {
                sanitizedPath = new URL(path).getPath();
            } catch (Exception e) {
                sanitizedPath = path;
            }
        } else {
            sanitizedPath = path;
        }
        return sanitizedPath.split("/");
    }

    private static void validatePathVariablesTemplate(String[] pathParts, String[] templateParts) {
        if (pathParts.length < templateParts.length) {
            throw new RuntimeException(
                    "Invalid variables template. Path has only %d parts, but more are expected from the template"
                            .formatted(pathParts.length));
        }
    }
}
