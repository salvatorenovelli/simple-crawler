package com.myseotoolbox.crawler.model;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Page {
    private final List<URI> outboundLinks = new ArrayList<>();

    public Page(List<URI> links) {
        this.outboundLinks.addAll(links);
    }

    public List<URI> getOutboundLinks() {
        return Collections.unmodifiableList(outboundLinks);
    }
}
