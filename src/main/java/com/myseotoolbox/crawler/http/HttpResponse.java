package com.myseotoolbox.crawler.http;

import lombok.Data;
import lombok.ToString;

import java.io.InputStream;
import java.net.URI;

@Data
@ToString
public class HttpResponse {

    private final URI sourceUri;
    private final int httpStatus;
    private final URI locationHeader;
    private final InputStream inputStream;

    public HttpResponse(URI sourceUri, int httpStatus, URI locationHeader, InputStream inputStream) {
        this.sourceUri = sourceUri;
        this.httpStatus = httpStatus;
        this.locationHeader = locationHeader;
        this.inputStream = inputStream;
    }

}
