package io.ericdriggs.karate.coverage;

import io.ericdriggs.karate.coverage.model.HttpMethod;
import io.ericdriggs.karate.coverage.model.LogMethodUrl;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class KarateLogUtilTest {

    public String getSampleKarateLog() {
        return " 1 > GET http://bar.foo.com/api/v1/find?happy=true&sad=false\n" +
                " 1 > POST http://bar.foo.com/api/v1/user_search\n" +
                " 1 > POST http://bar.foo.com/api/v1/user_search\n" +
                "dummy line";
    }

    @Test
    public void testLogImport() {
        Map<LogMethodUrl, Integer> logUrlMap = KarateLogUtil.fromKarateLog(getSampleKarateLog());
        assertNotNull(logUrlMap);
        assertEquals(Integer.valueOf(2), logUrlMap.get(new LogMethodUrl(HttpMethod.POST, "http://bar.foo.com/api/v1/user_search")));
        assertEquals(Integer.valueOf(1), logUrlMap.get(new LogMethodUrl(HttpMethod.GET, "http://bar.foo.com/api/v1/find")));

    }
}
