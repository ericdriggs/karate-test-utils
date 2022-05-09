package io.ericdriggs.karate.coverage.model;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class HttpMethodTest {

    @Test
    public void matchSupportedVerbs() {
        String httpMethodRegex = HttpMethod.getRegexMatch();

        assertTrue("CONNECT".matches(httpMethodRegex));
        assertTrue("DELETE".matches(httpMethodRegex));
        assertTrue("GET".matches(httpMethodRegex));
        assertTrue("HEAD".matches(httpMethodRegex));
        assertTrue("OPTIONS".matches(httpMethodRegex));
        assertTrue("PATCH".matches(httpMethodRegex));
        assertTrue("POST".matches(httpMethodRegex));
        assertTrue("PUT".matches(httpMethodRegex));
        assertTrue("TRACE".matches(httpMethodRegex));
    }

    @Test
    public void doesNotMatchUnsupportedVerbs() {
        String httpMethodRegex = HttpMethod.getRegexMatch();
        assertFalse("SMILE".matches(httpMethodRegex));
    }

    @Test
    public void filterMatchedTest() {
        Set<HttpMethodPath> paths = new TreeSet<>(HttpMethodPath.CASE_INSENSITIVE_ORDER);
        HttpMethodPath actuatorGet = new HttpMethodPath(HttpMethod.GET,"/actuator");
        HttpMethodPath actuatorCachesGet = new HttpMethodPath(HttpMethod.GET,"/actuator/caches");
        HttpMethodPath healthCheckGet = new HttpMethodPath(HttpMethod.GET,"/ping");
        HttpMethodPath v1FooGet = new HttpMethodPath(HttpMethod.GET,"/v1/foo/{id}");

        paths.add(actuatorGet);
        paths.add(actuatorCachesGet);
        paths.add(healthCheckGet);
        paths.add(v1FooGet);

        Collection<String> regexes = Arrays.asList(".*actuator.*", ".*internal.*", ".*ping.*");

        Set<HttpMethodPath> filtered = HttpMethodPath.filterMatched(paths, regexes);
        assertFalse(filtered.isEmpty());
        assertEquals(filtered.size(), 3);

        assertTrue(filtered.contains(actuatorGet));
        assertTrue(filtered.contains(actuatorCachesGet));
        assertTrue(filtered.contains(healthCheckGet));
    }

    @Test
    public void filterUnmatchedTest() {
        Set<HttpMethodPath> paths = new TreeSet<>(HttpMethodPath.CASE_INSENSITIVE_ORDER);
        paths.add(new HttpMethodPath(HttpMethod.GET,"/actuator/caches"));
        paths.add(new HttpMethodPath(HttpMethod.GET,"/v1/foo/{id}"));

        Collection<String> regexes = Collections.singletonList(".*/actuator/.*");

        Set<HttpMethodPath> filtered = HttpMethodPath.filterUnmatched(paths, regexes);
        assertFalse(filtered.isEmpty());
        assertEquals(filtered.size(), 1);
        for ( HttpMethodPath httpMethodPath : filtered){
            assertTrue(httpMethodPath.getPath().equals("/v1/foo/{id}"));
        }
    }

}
