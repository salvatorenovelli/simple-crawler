package com.myseotoolbox.crawler.http;

import com.myseotoolbox.crawler.model.Page;
import lombok.Getter;
import lombok.ToString;

import java.io.IOException;
import java.net.URI;

@Getter
@ToString(doNotUseGetters = true)
public class HttpResponse {

    private final URI requestUri;
    private final int httpStatus;
    private final URI location;
    private final Page page;
    private IOException possibleException;

    public HttpResponse(URI requestUri, int httpStatus, URI location, Page page) {
        this.requestUri = requestUri;
        this.httpStatus = httpStatus;
        this.location = location;
        this.page = page;
    }


    public int getHttpStatus() {
        return httpStatus;
    }

    public URI getLocation() {
        return location;
    }

    public Page getPage() throws IOException {
        if (possibleException != null) {
            throw possibleException;
        }
        return page;
    }
}