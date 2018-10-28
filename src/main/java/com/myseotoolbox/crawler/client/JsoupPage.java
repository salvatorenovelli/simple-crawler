package com.myseotoolbox.crawler.client;

import com.myseotoolbox.crawler.model.Page;
import org.jsoup.nodes.Document;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JsoupPage implements Page {
    private final List<URI> outboundLinks = new ArrayList<>();

    public JsoupPage(List<URI> links) {
        this.outboundLinks.addAll(links);
    }

    public List<URI> getOutboundLinks() {
        return Collections.unmodifiableList(outboundLinks);
    }

    /**
     * Expose the underlying data structure
     */
    public Document getDocument() {
        throw new UnsupportedOperationException("Not implemented yet!");
    }
}
