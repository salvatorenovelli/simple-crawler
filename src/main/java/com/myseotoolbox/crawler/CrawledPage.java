package com.myseotoolbox.crawler;

import com.myseotoolbox.crawler.http.RedirectChainElement;
import lombok.ToString;
import org.jsoup.nodes.Document;

import java.net.URI;
import java.util.List;


@ToString
public class CrawledPage {


    private final URI location;
    private final List<RedirectChainElement> redirectChain;
    private final Document document;

    public CrawledPage(URI location, List<RedirectChainElement> redirectChain, Document document) {
        this.location = location;
        this.redirectChain = redirectChain;
        this.document = document;
    }

    public URI getLocation() {
        return location;
    }
}
