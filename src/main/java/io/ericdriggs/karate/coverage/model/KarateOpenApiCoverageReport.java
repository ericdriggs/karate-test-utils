package io.ericdriggs.karate.coverage.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.ericdriggs.file.SimpleFileReader;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

import static com.fasterxml.jackson.core.JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN;

/**
 * A report showing coverage from a karate test run log compared to an OpenApi spec.
 *
 * <dl>
 *
 *     <dt>actualCoveragePercentage</dt>
 *     <dd>Percent of paths covered in log</dd>
 *
 *     <dt>expectedCoveragePercentage</dt>
 *     <dd>Optional input percentage. Defaults to zero. If actual &lt; expected, util will throw</dd>
 *
 *     <dt>coveredPaths</dt>
 *     <dd>(Path, HttpMethod) from OpenApi which are matched in the log (url, HttpMethod)</dd>
 *
 *     <dt>uncoveredPaths</dt>
 *     <dd>(Path, HttpMethod) from OpenApi without a match in the log (url, HttpMethod)</dd>
 *
 *     <dt>unmatchedLogMethodUrls</dt>
 *     <dd>log (url, HttpMethod) without a match in OpenApi (Path, HttpMethod)</dd>
 *
 *     <dt>skippedLogMethodUrls</dt>
 *     <dd>log (url, HttpMethod) intentionally skipped since matching skip regex</dd>
 * </dl>
 */
@Data
public class KarateOpenApiCoverageReport {
    @JsonIgnore
    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.configure(WRITE_BIGDECIMAL_AS_PLAIN, true);
    }

    private BigDecimal actualCoveragePercentage;
    private BigDecimal expectedCoveragePercentage;
    private Map<HttpMethodPath, Map<String, Integer>> coveredPaths;
    private Set<HttpMethodPath> uncoveredPaths;
    private Set<LogMethodUrl> unmatchedLogMethodUrls;
    private Set<HttpMethodPath> skippedHttpMethodPaths;

    @JsonIgnore
    public String toJson() {
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    @JsonIgnore
    public void save() {
        SimpleFileReader.saveRelativePath("coverage", "karate-coverage.json", this.toJson());
    }
}
