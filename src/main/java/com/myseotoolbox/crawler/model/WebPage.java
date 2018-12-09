package com.myseotoolbox.crawler.model;

import lombok.Getter;
import lombok.ToString;
import org.jsoup.nodes.Document;

import java.net.URI;
import java.util.Optional;


@ToString
@Getter
public class WebPage {


    private final URI uri;
    private final RedirectChain redirectChain;
    private final Document document;

    public WebPage(URI uri, RedirectChain redirectChain, Document document) {
        this.uri = uri;
        this.redirectChain = redirectChain;
        this.document = document;
    }


    public Optional<Document> getDocument() {
        return Optional.ofNullable(document);
    }
}
