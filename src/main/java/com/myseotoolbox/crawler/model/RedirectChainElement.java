package com.myseotoolbox.crawler.model;

import lombok.Data;
import lombok.ToString;

import java.net.URI;

@Data
@ToString
public class RedirectChainElement {

    private final URI uri;
    private final int httpStatus;
    private final URI locationHeader;

    public RedirectChainElement(URI uri, int httpStatus, URI locationHeader) {
        this.uri = uri;
        this.httpStatus = httpStatus;
        this.locationHeader = locationHeader;
    }
}
