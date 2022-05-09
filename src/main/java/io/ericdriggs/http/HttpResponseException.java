package io.ericdriggs.http;

import java.net.http.HttpResponse;

@SuppressWarnings("unused")
public class HttpResponseException extends RuntimeException{
    private HttpResponse<String> httpResponse;

    public HttpResponseException(HttpResponse<String> httpResponse) {
        this.httpResponse = httpResponse;
    }

    public HttpResponseException(Exception ex, HttpResponse<String> httpResponse) {
        super(ex);
        this.httpResponse = httpResponse;
    }

    public HttpResponse<String> getHttpResponse() {
        return httpResponse;
    }
}
