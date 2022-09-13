package io.ericdriggs.karate.coverage;

import io.ericdriggs.karate.coverage.OpenApiJsonUtil;
import io.ericdriggs.karate.coverage.model.HttpMethodPath;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class OpenApiJsonUtilTest extends OpenApiJsonUtil {

    //TODO: move to integration test
    @Disabled //integration test
    @Test
    public void parseOpenApiJsonFromUrl() {
        final String swaggerJsonPathFilePath = "https://staging.foo.com/v1/api/json";
        final String openApiJson = OpenApiJsonUtil.fromOpenApiJsonUrl(swaggerJsonPathFilePath, Collections.singletonMap("user","pass"));
        Set<HttpMethodPath> httpMethodPaths = OpenApiJsonUtil.fromJsonString(openApiJson, null);
        assertNotNull(httpMethodPaths);
        assertTrue(httpMethodPaths.size() > 0);
    }

    @Test
    public void parseOpenApiJsonFromFile() {

        final String relativePath = "src/test/resources/coverage/user-swagger2.json";
        final String openApiJson = OpenApiJsonUtil.fromRelativePath(relativePath);
        Set<HttpMethodPath> httpMethodPaths = OpenApiJsonUtil.fromJsonString(openApiJson, null);
        assertNotNull(httpMethodPaths);
        assertTrue(httpMethodPaths.size() > 0);
    }

}
