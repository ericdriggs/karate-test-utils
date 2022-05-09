package io.ericdriggs.karate.coverage.exception;


import io.ericdriggs.karate.coverage.model.KarateOpenApiCoverageReport;

public class KarateCoverageException extends RuntimeException {

    public KarateCoverageException(KarateOpenApiCoverageReport karateOpenApiCoverageReport) {
        this.karateOpenApiCoverageReport = karateOpenApiCoverageReport;
    }
    private KarateOpenApiCoverageReport karateOpenApiCoverageReport;

    public KarateOpenApiCoverageReport getkarateOpenApiCoverageReport() {
        return karateOpenApiCoverageReport;
    }
}
