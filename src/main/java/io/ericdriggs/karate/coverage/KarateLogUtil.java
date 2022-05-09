package io.ericdriggs.karate.coverage;

import io.ericdriggs.karate.coverage.model.HttpMethod;
import io.ericdriggs.karate.coverage.model.LogMethodUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Extracts (HttpMethod, url) from karate logs
 */
public class KarateLogUtil {

    private static Logger log = LoggerFactory.getLogger(KarateLogUtil.class);

    /**
     * Returns a map of all endpoints and their count from karate log
     * <p>
     * 1. Filters to log lines like: <code>1 &gt; POST http://host.foo.com/path1?query=val</code>
     * 2. Removes query strings
     *
     * @param karateLog the karate.log file contents
     * @return map of ( LogMethodUrl -&gt; Count)
     */
    public static Map<LogMethodUrl, Integer> fromKarateLog(String karateLog) {
        List<String> logLinesTrimmed = getTrimmedLogLines(karateLog);
        Map<LogMethodUrl, Integer> logUrlMap = getLogLineMap(logLinesTrimmed);
        return logUrlMap;
    }

    /**
     * Finds log lines like: <code>1 &gt; POST http://host.foo.com/path1?query=val</code>
     * and returns list of String like <code>POST http://host.foo.com/path1</code>
     *
     * @param karateLog string of karate.log
     * @return a list of trimmed log lines as string
     */
    protected static List<String> getTrimmedLogLines(String karateLog) {
        List<String> logLinesTrimmed;

        List<String> logLinesRaw = Arrays.asList(karateLog.split(System.lineSeparator()));

        String logLineFilter = "[ ]*[0-9][ ]*>[ ]*" + HttpMethod.getRegexMatch() + "[ ]*.*";
        List<String> filteredLogLines = logLinesRaw.stream().filter(l -> l.matches(logLineFilter)).collect(Collectors.toList());
        List<String> trimQueryParamLines = filteredLogLines.stream().map(l -> l.replaceAll("\\?.*", "")).collect(Collectors.toList());
        logLinesTrimmed = trimQueryParamLines.stream().map(l -> l.replaceAll("[ ]*[0-9][ ]*>[ ]*", "")).collect(Collectors.toList());

        return logLinesTrimmed;
    }

    protected static Map<LogMethodUrl, Integer> getLogLineMap(List<String> logLinesTrimmed) {
        Map<LogMethodUrl, Integer> logUrlMap = new TreeMap<>(LogMethodUrl.CASE_INSENSITIVE_ORDER);
        for (String logLine : logLinesTrimmed) {
            String[] split = logLine.split(" ", 2);
            if (split.length != 2) {
                log.error("Actual length: " + split.length + ", Expected length 2 for split for logLine: " + logLine);
            }
            try {
                HttpMethod httpMethod = HttpMethod.fromString(split[0]);
                String url = split[1];
                LogMethodUrl logUrl = new LogMethodUrl(httpMethod, url);
                if (logUrlMap.get(logUrl) == null) {

                    logUrlMap.put(logUrl, 0);
                }
                logUrlMap.put(logUrl, logUrlMap.get(logUrl) + 1);


            } catch (Exception ex) {
                log.error("failed to parse method for logLine: " + logLine + "\n " + ex.getStackTrace());
            }
        }
        return logUrlMap;
    }
}
