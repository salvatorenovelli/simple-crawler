package com.myseotoolbox.crawler.model;

import java.net.URI;
import java.util.List;

public interface Page {
    List<URI> getOutboundLinks();
}
