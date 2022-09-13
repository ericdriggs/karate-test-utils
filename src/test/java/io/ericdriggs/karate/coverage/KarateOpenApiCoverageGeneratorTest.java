package io.ericdriggs.karate.coverage;

import io.ericdriggs.file.SimpleFileReader;
import io.ericdriggs.karate.coverage.exception.KarateCoverageException;
import io.ericdriggs.karate.coverage.model.HttpMethod;
import io.ericdriggs.karate.coverage.model.HttpMethodPath;
import io.ericdriggs.karate.coverage.model.KarateOpenApiCoverageReport;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


public class KarateOpenApiCoverageGeneratorTest extends KarateOpenApiCoverageGenerator {

    @Test
    public void generateKarateCoverageReportPathSwagger2Test() {

        KarateOpenApiCoverageGenerator karateOpenApiCoverageGenerator = new KarateOpenApiCoverageGenerator()
                .setOpenApiJsonFilePath("src/test/resources/coverage/user-swagger2.json")
                .setKarateLogPath("src/test/resources/coverage/user.log");

        KarateOpenApiCoverageReport karateOpenApiCoverageReport = karateOpenApiCoverageGenerator.generateAndSave();
        assertNotNull(karateOpenApiCoverageReport.getActualCoveragePercentage());
        assertEquals(new BigDecimal("54.5"), karateOpenApiCoverageReport.getActualCoveragePercentage());
        assertNotNull(karateOpenApiCoverageReport.getCoveredPaths());
        assertNotNull(karateOpenApiCoverageReport.getUncoveredPaths());
        assertNotNull(karateOpenApiCoverageReport.getUnmatchedLogMethodUrls());

        System.out.println(karateOpenApiCoverageReport.toJson());

    }


    @Test
    public void generateKarateCoverageReportString_Swagger2Test() {

        String karateLog = SimpleFileReader.fromRelativePath("src/test/resources/coverage/user.log");
        String swaggerJson = SimpleFileReader.fromRelativePath("src/test/resources/coverage/user-swagger2.json");
        assertNotNull(karateLog);
        assertNotNull(swaggerJson);
        assertTrue(karateLog.length() > 0);
        assertTrue(swaggerJson.length() > 0);

        KarateOpenApiCoverageReport karateOpenApiCoverageReport = KarateOpenApiCoverageGenerator.
                generateKarateOpenApiCoverageReportFromStrings(swaggerJson, karateLog, null, Collections.EMPTY_SET, BigDecimal.ZERO);
        assertNotNull(karateOpenApiCoverageReport.getActualCoveragePercentage());
        assertEquals(new BigDecimal("54.5"), karateOpenApiCoverageReport.getActualCoveragePercentage());
        assertNotNull(karateOpenApiCoverageReport.getCoveredPaths());
        assertNotNull(karateOpenApiCoverageReport.getUncoveredPaths());
        assertNotNull(karateOpenApiCoverageReport.getUnmatchedLogMethodUrls());

        System.out.println(karateOpenApiCoverageReport.toJson());
    }

    @Test
    public void generateKarateCoverageReportString_OAS3Test() {

        String karateLog = SimpleFileReader.fromRelativePath("src/test/resources/coverage/user.log");
        String swaggerJson = SimpleFileReader.fromRelativePath("src/test/resources/coverage/user-openapi3.json");
        assertNotNull(karateLog);
        assertNotNull(swaggerJson);
        assertTrue(karateLog.length() > 0);
        assertTrue(swaggerJson.length() > 0);

        KarateOpenApiCoverageReport karateOpenApiCoverageReport = KarateOpenApiCoverageGenerator.
                generateKarateOpenApiCoverageReportFromStrings(swaggerJson, karateLog, "/v1", Collections.EMPTY_SET, BigDecimal.ZERO);
        assertNotNull(karateOpenApiCoverageReport.getActualCoveragePercentage());
        assertEquals(new BigDecimal("54.5"), karateOpenApiCoverageReport.getActualCoveragePercentage());
        assertNotNull(karateOpenApiCoverageReport.getCoveredPaths());
        assertNotNull(karateOpenApiCoverageReport.getUncoveredPaths());
        assertNotNull(karateOpenApiCoverageReport.getUnmatchedLogMethodUrls());

        System.out.println(karateOpenApiCoverageReport.toJson());
    }
  
    @Test
    public void coveragePercentageTest() {

        Set<HttpMethodPath> paths = new TreeSet<>(HttpMethodPath.CASE_INSENSITIVE_ORDER);
        HttpMethodPath actuatorGet = new HttpMethodPath(HttpMethod.GET,"/actuator");
        HttpMethodPath actuatorCachesGet = new HttpMethodPath(HttpMethod.GET,"/actuator/caches");
        HttpMethodPath healthCheckGet = new HttpMethodPath(HttpMethod.GET,"/ping");
        HttpMethodPath v1FooGet = new HttpMethodPath(HttpMethod.GET,"/v1/foo/{id}");

        paths.add(actuatorGet);
        paths.add(actuatorCachesGet);
        paths.add(healthCheckGet);
        paths.add(v1FooGet);

        Map<HttpMethodPath, Map<String, Integer>> coveredPathsMap = new TreeMap<>(HttpMethodPath.CASE_INSENSITIVE_ORDER);
        coveredPathsMap.put(v1FooGet, Collections.emptyMap());

        BigDecimal coveredPercentage = KarateOpenApiCoverageGenerator.getCoveragePercentage(paths, coveredPathsMap);
        assertEquals(coveredPercentage, new BigDecimal("25"));

    }

    @Test
    public void validate_WhenCoverageTooLow_ThenThrows_Test() {
        KarateOpenApiCoverageReport karateOpenApiCoverageReport =
                new KarateOpenApiCoverageReport()
                        .setActualCoveragePercentage(new BigDecimal("50"))
                        .setExpectedCoveragePercentage(new BigDecimal("60"));

        Exception exception = assertThrows(KarateCoverageException.class, () -> {
            KarateOpenApiCoverageGenerator.validateCoverage(karateOpenApiCoverageReport);
        });

    }
}
