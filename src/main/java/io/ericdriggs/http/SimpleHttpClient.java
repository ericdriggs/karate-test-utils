package io.ericdriggs.http;


import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;

public class SimpleHttpClient {

    private HttpClient httpClient;

    public SimpleHttpClient() {
        httpClient = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build();
    }

    public String getJsonFromUrl(String url) {
        return getJsonFromUrl(url, Collections.EMPTY_MAP);
    }

    public String getJsonFromUrl(String url, Map<String, String> headers) {
        HttpResponse<String> response = doGetString(url, headers);
        return response.body();
    }

    protected static boolean shouldThrow(HttpResponse<String> response) {
        if (response.statusCode() >= 400) {
            return true;
        }
        return false;
    }

    protected HttpResponse<String> doGetString(String url, Map<String, String> headers) {
        try {
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("Accept", "*/*");

            for (Map.Entry<String, String> headerEntry : headers.entrySet()) {
                requestBuilder.header(headerEntry.getKey(), headerEntry.getValue());
            }

            HttpRequest httpRequest = requestBuilder.GET().build();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (shouldThrow(response)) {
                throw new HttpResponseException(response);
            }
            return response;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static Map<String,String> getBasicAuthHeader(Map<String,String> userPassMap) {
        if(userPassMap == null) {
            return Collections.EMPTY_MAP;
        }
        if (userPassMap.isEmpty()) {
            return Collections.EMPTY_MAP;
        }
        if (userPassMap.size() > 1) {
            throw new IllegalArgumentException("userPassMap size greater than 1: " + userPassMap.size());
        }
        for (Map.Entry<String, String> entry : userPassMap.entrySet()) {
            return getBasicAuthHeader(entry.getKey(), entry.getValue());
        }
        throw new IllegalStateException("Compiler doesn't know this is an impossible state. " +
                "If thrown, hug your ones since logic no longer applies.");
    }

    public static Map<String,String> getBasicAuthHeader(String user, String pass) {
        String auth = user + ":" + pass;
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.ISO_8859_1));
        return Collections.singletonMap("Authorization", "Basic " + new String(encodedAuth));
    }
}
