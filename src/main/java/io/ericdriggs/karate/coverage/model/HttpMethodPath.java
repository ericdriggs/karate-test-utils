package io.ericdriggs.karate.coverage.model;

import io.ericdriggs.karate.coverage.OpenApiPathRegexUtil;
import lombok.Data;
import lombok.NonNull;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

/**
 * Represents a (HttpMethod, path) from an OpenApi spec.
 * If an endpoint supports multiple methods, they will be represented as separate HttpMethodPath entries.
 */
@Data
public class HttpMethodPath implements Comparable<HttpMethodPath> {
    @NonNull
    private final HttpMethod httpMethod;
    @NonNull
    private final String path;
    @NonNull
    private String pathRegex;

    public HttpMethodPath(HttpMethod httpMethod, String path) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.pathRegex = OpenApiPathRegexUtil.getRegexForOpenApiPath(path);
    }

    public static final Comparator<HttpMethodPath> CASE_INSENSITIVE_ORDER
            = new CaseInsensitiveComparator();

    @Override
    public int compareTo(HttpMethodPath that) {
        final int pathCompare = path.compareToIgnoreCase(that.path);
        if (pathCompare != 0) {
            return pathCompare;
        }
        return httpMethod.compareTo(that.httpMethod);
    }

    private static class CaseInsensitiveComparator
            implements Comparator<HttpMethodPath>, java.io.Serializable {
        private static final long serialVersionUID = -5293175194087492313L;

        public int compare(HttpMethodPath var1, HttpMethodPath var2) {
            return var1.compareTo(var2);
        }

        private Object readResolve() {
            return CASE_INSENSITIVE_ORDER;
        }
    }

    public static Set<HttpMethodPath> filterUnmatched(Collection<HttpMethodPath> httpMethodPaths, Collection<String> regexFilters) {
        Set<HttpMethodPath> unmatched = new TreeSet<>(HttpMethodPath.CASE_INSENSITIVE_ORDER);
        unmatched.addAll(httpMethodPaths);
        unmatched.removeAll(filterMatched(httpMethodPaths, regexFilters));
        return unmatched;
    }

    public static Set<HttpMethodPath> filterMatched(Collection<HttpMethodPath> httpMethodPaths, Collection<String> regexFilters) {
        Set<HttpMethodPath> matched = new TreeSet<HttpMethodPath>(HttpMethodPath.CASE_INSENSITIVE_ORDER);
        for (HttpMethodPath httpMethodPath : httpMethodPaths) {
            for ( String regex : regexFilters) {
                if (httpMethodPath.getPath().matches(regex)) {
                    matched.add(httpMethodPath);
                    break;
                }
            }
        }
        return matched;
    }

}
