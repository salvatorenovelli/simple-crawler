package com.myseotoolbox.crawler.http;

import lombok.Data;
import lombok.ToString;

import java.io.InputStream;
import java.net.URI;

@Data
@ToString
public class HttpResponse {

    private final URI uri;
    private final int httpStatus;
    private final URI location;
    private final InputStream inputStream;

    public HttpResponse(URI uri, int httpStatus, URI location, InputStream inputStream) {
        this.uri = uri;
        this.httpStatus = httpStatus;
        this.location = location;
        this.inputStream = inputStream;
    }

}
