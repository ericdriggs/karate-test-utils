package io.ericdriggs.karate.coverage.model;


import java.util.Set;
import java.util.TreeSet;

/**
 * Includes all http methods
 */
public enum HttpMethod {
    CONNECT,
    DELETE,
    GET,
    HEAD,
    OPTIONS,
    POST,
    PUT,
    PATCH,
    TRACE;

    public static HttpMethod fromString(String method) {
        return valueOf(method.toUpperCase());
    }

    public static String getRegexMatch() {
        Set<String> methods = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        for (HttpMethod httpMethod : HttpMethod.values()) {
            methods.add(httpMethod.name());
        }

        return "(" + String.join("|", methods) + ")";
    }
}