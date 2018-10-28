package com.myseotoolbox.crawler.model;

import org.jsoup.nodes.Document;

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


    /**
     * Expose the underlying data structure
     *
     * */
    public Document getDocument() {
        throw new UnsupportedOperationException("Not implemented yet!");
    }
}
