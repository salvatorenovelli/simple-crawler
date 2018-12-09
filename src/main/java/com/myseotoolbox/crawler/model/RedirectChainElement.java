package com.myseotoolbox.crawler.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.net.URI;

@Getter
@ToString
public class RedirectChainElement {

    private final int httpStatus;
    private final URI sourceURI;
    private final URI destinationURI;

    public RedirectChainElement(URI sourceUri, int httpStatus, URI destinationURI) {
        this.httpStatus = httpStatus;
        this.sourceURI = sourceUri;
        this.destinationURI = destinationURI;
    }
}
