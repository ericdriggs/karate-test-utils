package io.ericdriggs.karate.coverage;

import io.ericdriggs.file.SimpleFileReader;
import io.ericdriggs.karate.coverage.exception.KarateCoverageException;
import io.ericdriggs.karate.coverage.model.HttpMethodPath;
import io.ericdriggs.karate.coverage.model.KarateOpenApiCoverageReport;
import io.ericdriggs.karate.coverage.model.LogMethodUrl;
import lombok.Data;


import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;


@Data
public class KarateOpenApiCoverageGenerator {

    /**
     * The relative path from the project root to karate.log, e.g. /target/karate.log
     */
    private String karateLogPath;

    /**
     * The url to the openapi 3 or swagger 2 json spec, e.g. https://bar.foo.com/v2/api-docs
     */
    private String openApiJsonUrl;

    /**
     * The local path to an openapi json file, relative to project root. Prefer URL since it is more likely to stay current.
     */
    private String openApiJsonFilePath;

    /**
     * Optional basic auth credentials to the openapi url
     * e.g. Collections.singletonMap("user","pass")
     */
    private Map<String, String> openApiJsonUserPass = Collections.emptyMap();

    /**
     * Optional list of regex for openapi paths to skip/ignore for coverage purposes.
     * e.g. Arrays.asList(".*actuator.*", ".*internal.*", ".*health_check.*")
     */
    private Collection<String> skipPathRegexes = Collections.emptySet();

    /**
     *
     */
    private BigDecimal expectedCoveragePercentage = BigDecimal.ZERO;

    public KarateOpenApiCoverageGenerator() {

    }


    /**
     * Generates a json coverage report for using
     * 1. karate.log
     * 2. OpenApi/Swagger json from either file or url (url recommended since less stale)
     *
     * Recommended: use url for OpenApi json since that will be more current.
     * @return a report of type {@link io.ericdriggs.karate.coverage.model.KarateOpenApiCoverageReport}
     */

    public KarateOpenApiCoverageReport generateAndSave() {
        validate();
        KarateOpenApiCoverageReport karateOpenApiCoverageReport = generate();
        karateOpenApiCoverageReport.save();
        validateCoverage(karateOpenApiCoverageReport);
        return karateOpenApiCoverageReport;
    }


    protected static KarateOpenApiCoverageReport generateKarateOpenApiCoverageReportFromStrings(
            String swaggerJson,
            String karateLog,
            Collection<String> skipPathRegexes,
            BigDecimal expectedCoveragePercentage) {


        /***  Initialize report collections ****/
        Set<HttpMethodPath> httpMethodPaths = OpenApiJsonUtil.fromJsonString(swaggerJson);
        Map<LogMethodUrl, Integer> karateLogPathMap = KarateLogUtil.fromKarateLog(karateLog);

        Set<HttpMethodPath> skippedHttpMethodPaths = HttpMethodPath.filterMatched(httpMethodPaths, skipPathRegexes);
        httpMethodPaths.removeAll(skippedHttpMethodPaths);

        Set<HttpMethodPath> uncoveredPaths = new TreeSet<>(HttpMethodPath.CASE_INSENSITIVE_ORDER);
        uncoveredPaths.addAll(httpMethodPaths);

        Set<LogMethodUrl> unmatchedLogUrls = new TreeSet<>(LogMethodUrl.CASE_INSENSITIVE_ORDER);
        unmatchedLogUrls.addAll(karateLogPathMap.keySet());

        Map<HttpMethodPath, Map<String, Integer>> coveredPathsMap = new TreeMap<>(HttpMethodPath.CASE_INSENSITIVE_ORDER);

        /**** Determine coverage for each path specified in open api ****/
        for (HttpMethodPath httpMethodPath : httpMethodPaths) {
            //check each log line to see if it matches that path (log lines may match multiple paths due to regex imprecision)
            for (Map.Entry<LogMethodUrl, Integer> karateLogEntry : karateLogPathMap.entrySet()) {
                LogMethodUrl logMethodUrl = karateLogEntry.getKey();
                Integer logUrlCount = karateLogEntry.getValue();

              

                /**** If log method url matches, update report collections appropriately  ****/
                if (logMethodUrl.getUrl().matches(httpMethodPath.getPathRegex()) && logMethodUrl.getHttpMethod().equals(httpMethodPath.getHttpMethod())) {
                    uncoveredPaths.remove(httpMethodPath);
                    unmatchedLogUrls.remove(logMethodUrl);
                    if (coveredPathsMap.get(httpMethodPath) == null) {
                        coveredPathsMap.put(httpMethodPath, new TreeMap<>(String.CASE_INSENSITIVE_ORDER));
                    }
                    Map<String, Integer> coveredEndpointMap = coveredPathsMap.get(httpMethodPath);
                    if (coveredEndpointMap.get(logMethodUrl.getUrl()) == null) {
                        coveredEndpointMap.put(logMethodUrl.getUrl(), 0);
                    }
                    Integer newCount = coveredEndpointMap.get(logMethodUrl.getUrl()) + logUrlCount;
                    coveredEndpointMap.put(logMethodUrl.getUrl(), newCount);
                }
            }
        }

        BigDecimal coveredPercentage = getCoveragePercentage(httpMethodPaths, coveredPathsMap);
        KarateOpenApiCoverageReport karateOpenApiCoverageReport = 
                new KarateOpenApiCoverageReport() //
                        .setCoveredPaths(coveredPathsMap) //
                .setActualCoveragePercentage(coveredPercentage) //
                        .setExpectedCoveragePercentage(expectedCoveragePercentage) //
         .setUncoveredPaths(uncoveredPaths) //
                        .setUnmatchedLogMethodUrls(unmatchedLogUrls) //
                        .setSkippedHttpMethodPaths(skippedHttpMethodPaths);

        return karateOpenApiCoverageReport;

    }

    protected static void validateCoverage(KarateOpenApiCoverageReport karateOpenApiCoverageReport) {
        BigDecimal expectedCoveragePercentage = karateOpenApiCoverageReport.getExpectedCoveragePercentage();
        BigDecimal actualCoveragePercentage = karateOpenApiCoverageReport.getActualCoveragePercentage();
        if (expectedCoveragePercentage != null) {
            if (actualCoveragePercentage.compareTo(expectedCoveragePercentage) < 0) {
                throw new KarateCoverageException(karateOpenApiCoverageReport);
            }
        }
    }

    protected static BigDecimal getCoveragePercentage(Set<HttpMethodPath> openApiMethodPaths, Map<HttpMethodPath, Map<String, Integer>> coveredPathsMap) {
        MathContext mathContext = new MathContext(3, RoundingMode.HALF_UP);
        BigDecimal swaggerPathsCount = new BigDecimal(openApiMethodPaths.size());
        BigDecimal coveredPathsCount = new BigDecimal(coveredPathsMap.size());
        return BigDecimal.valueOf(100).multiply(coveredPathsCount, mathContext).divide(swaggerPathsCount, mathContext);
    }

    protected void validate() {
        if (karateLogPath == null) {
            throw new NullPointerException("karateLogPath");
        }
        if (openApiJsonUrl == null && openApiJsonFilePath == null) {
            throw new NullPointerException("openApiJsonUrl == null && openApiJsonFilePath == null");
        }
        if (isEmpty(openApiJsonUrl) && isEmpty(openApiJsonFilePath)) {
            throw new IllegalArgumentException("StringUtils.isEmpty(openApiJsonUrl) && StringUtils.isEmpty(openApiJsonFilePath)");
        }
    }

    protected KarateOpenApiCoverageReport generate() {
        String karateLog = SimpleFileReader.fromRelativePath(karateLogPath);
        String openApiJson;
        if (openApiJsonFilePath != null) {
            openApiJson = SimpleFileReader.fromRelativePath(openApiJsonFilePath);
        } else {
            openApiJson = OpenApiJsonUtil.fromOpenApiJsonUrl(openApiJsonUrl, openApiJsonUserPass);
        }
        return generateKarateOpenApiCoverageReportFromStrings(openApiJson, karateLog, skipPathRegexes, expectedCoveragePercentage);
    }

    public static boolean isEmpty(String val) {
        if (val == null) {
            return true;
        }
        if (val.trim().equals("")) {
            return true;
        }
        return false;
    }
}

