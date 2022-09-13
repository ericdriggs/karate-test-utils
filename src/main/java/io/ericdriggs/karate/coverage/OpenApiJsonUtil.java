package io.ericdriggs.karate.coverage;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.ericdriggs.file.SimpleFileReader;
import io.ericdriggs.http.SimpleHttpClient;
import io.ericdriggs.karate.coverage.model.HttpMethod;
import io.ericdriggs.karate.coverage.model.HttpMethodPath;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Extracts the (HttpMethod, URL) from an OpenApi/Swagger spec.
 * <p>
 * Why write another OpenApi parser?
 * - couldn't find a parser which supported OpenApi 3 and Swagger 2
 * - only interested in a small section of spec, which barely changed from v2 and v3
 * - minimize external dependencies
 */
@Slf4j
public class OpenApiJsonUtil {
    private final static ObjectMapper mapper;
    private static final SimpleHttpClient jsonHttpClient = new SimpleHttpClient();

    static {
        mapper = new ObjectMapper();
    }

    /**
     * Extracts endpoints and paths from an openapi json spec
     *
     * @param openApiUrl  the url for the json openapi spec. version 2 and 3 supported. The url can be remote or on filesystem.
     * @param userPassMap a map with basic auth credentials
     * @return set of HttpMethodPath
     */
    protected static String fromOpenApiJsonUrl(String openApiUrl, Map<String, String> userPassMap) {
        return jsonHttpClient.getJsonFromUrl(openApiUrl, SimpleHttpClient.getBasicAuthHeader(userPassMap));
    }


    protected static String fromRelativePath(String relativePath) {

        return SimpleFileReader.fromRelativePath(relativePath);

    }

    @SneakyThrows(JsonProcessingException.class)
    protected static Set<HttpMethodPath> fromJsonString(String jsonBody, String basePath) {

        JsonNode jsonNode = mapper.readTree(jsonBody);
        return fromJsonNode(jsonNode, basePath);

    }


    /**
     * for OAS3 requires basePath to be set since no equivalent in OAS3
     *        reference: https://swagger.io/docs/specification/api-host-and-base-path/
     * @param jsonNode the parsed openapi spec
     * @param basePath the base path (if exists and oas3)
     * @return the set of http method paths to match against (host agnostic)
     */
    protected static Set<HttpMethodPath> fromJsonNode(JsonNode jsonNode, String basePath) {
        Set<HttpMethodPath> httpMethodPaths = new TreeSet<>(HttpMethodPath.CASE_INSENSITIVE_ORDER);


        JsonNode pathsNode = jsonNode.get("paths");
        if (pathsNode == null) {
            throw new IllegalStateException("Response json is missing key: 'paths':" + jsonNode.toPrettyString());
        }


        if (basePath == null || basePath.trim().equals("")) {
            JsonNode basePathNode = jsonNode.get("basePath");
            if (basePathNode == null) {
                basePath = "";

            } else {
                basePath = basePathNode.asText();
            }
        }

        Iterator<Map.Entry<String, JsonNode>> pathIterator = pathsNode.fields();
        while (pathIterator.hasNext()) {
            Map.Entry<String, JsonNode> pathNode = pathIterator.next();
            String path = basePath + pathNode.getKey();
            JsonNode methodsNode = pathNode.getValue();

            Iterator<Map.Entry<String, JsonNode>> methodIterator = methodsNode.fields();
            while (methodIterator.hasNext()) {
                Map.Entry<String, JsonNode> methodNode = methodIterator.next();
                String method = methodNode.getKey();
                HttpMethodPath httpMethodPath = new HttpMethodPath(HttpMethod.fromString(method), path);
                httpMethodPaths.add(httpMethodPath);
            }
        }
        return httpMethodPaths;
    }

}
