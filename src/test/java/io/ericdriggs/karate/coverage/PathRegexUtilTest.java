package io.ericdriggs.karate.coverage;

import io.ericdriggs.karate.coverage.OpenApiPathRegexUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PathRegexUtilTest {

    @Test
    public void shortPathRegexTest() {

        final String path = "/email/{email}";
        final String expectedRegex = "[^/]*[/][/][^/]*/email/[^/]*[/]?";
        final String actualRegex = OpenApiPathRegexUtil.getRegexForOpenApiPath(path);
        assertEquals(expectedRegex, actualRegex);

        final String logUrl = "https://foo.com/email/1234";
        assertTrue(logUrl.matches(actualRegex));
    }


    @Test
    public void longPathRegexTest() {

        final String path = "/foo/{foo}/bar/{bar}";
        final String actualRegex = OpenApiPathRegexUtil.getRegexForOpenApiPath(path);
        {
            final String trailingSlashUrl = "https://foo.com/foo/123/bar/234/";
            assertTrue(trailingSlashUrl.matches(actualRegex));
        }
        {
            final String noTrailingSlashUrl = "https://foo.com/foo/123/bar/234";
            assertTrue(noTrailingSlashUrl.matches(actualRegex));
        }
    }

}
