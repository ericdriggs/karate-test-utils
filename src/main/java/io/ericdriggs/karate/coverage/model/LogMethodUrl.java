package io.ericdriggs.karate.coverage.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.util.Comparator;

@Data
@AllArgsConstructor
public class LogMethodUrl implements Comparable<LogMethodUrl> {
    @NonNull
    private final HttpMethod httpMethod;
    @NonNull
    private final String url;

    public static final Comparator<LogMethodUrl> CASE_INSENSITIVE_ORDER
            = new CaseInsensitiveComparator();

    @Override
    public int compareTo(LogMethodUrl that) {
        final int urlCompare = url.compareToIgnoreCase(that.url);
        if (urlCompare != 0) {
            return urlCompare;
        }
        return httpMethod.compareTo(that.httpMethod);
    }

    private static class CaseInsensitiveComparator
            implements Comparator<LogMethodUrl>, java.io.Serializable {
        private static final long serialVersionUID = 6623906061286761449L;

        public int compare(LogMethodUrl var1, LogMethodUrl var2) {
            return var1.compareTo(var2);
        }

        private Object readResolve() {
            return CASE_INSENSITIVE_ORDER;
        }
    }

}
