package io.ericdriggs.karate.coverage;

public enum OpenApiPathRegexUtil {
    ;

    private final static String schemeHostRegex = "[^/]*[/][/][^/]*";
    private final static String openApiVariableReplace = "[^/]*";
    private final static String openApiVariableFind = "\\{[^/]*}";

    /**
     * Replaces all characters inside of { } block with regex to match path segments.
     *
     * @param openApiPath
     * @return regex replaced text
     */
    public static String getRegexForOpenApiPath(String openApiPath) {
        //replace: everything in {} with segment ending at line end or next '/'
        String regex = schemeHostRegex + openApiPath.replaceAll(openApiVariableFind, openApiVariableReplace);

        //add optional trailing slash
        if (!regex.endsWith("/")) {
            regex += "[/]?";
        }
        return regex;
    }


}
