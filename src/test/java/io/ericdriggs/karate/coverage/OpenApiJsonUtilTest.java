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
        String swaggerJsonPathFilePath = "url goes here";
        String openApiJson = OpenApiJsonUtil.fromOpenApiJsonUrl(swaggerJsonPathFilePath, Collections.singletonMap("scsvc","test1"));
        Set<HttpMethodPath> httpMethodPaths = OpenApiJsonUtil.fromJsonString(openApiJson);
        assertNotNull(httpMethodPaths);
        assertTrue(httpMethodPaths.size() > 0);
    }

    @Test
    public void parseOpenApiJsonFromFile() {

        String relativePath = "src/test/resources/coverage/user-swagger.json";
        String openApiJson = OpenApiJsonUtil.fromRelativePath(relativePath);
        Set<HttpMethodPath> httpMethodPaths = OpenApiJsonUtil.fromJsonString(openApiJson);
        assertNotNull(httpMethodPaths);
        assertTrue(httpMethodPaths.size() > 0);

    }

}
