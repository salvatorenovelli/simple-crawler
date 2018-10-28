package com.myseotoolbox.crawler.client;

import com.myseotoolbox.crawler.model.Page;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.net.URI;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class JsoupPage implements Page {

    private final Document document;

    public JsoupPage(Document document) {
        this.document = document;
    }

    public List<URI> getOutboundLinks() {
        return extractFromTag(document.body(), "a[href]", element -> element.attr("href"))
                .stream()
                .map(URI::create)
                .collect(Collectors.toList());
    }


    private static List<String> extractFromTag(Element element, String filter, Function<Element, String> mapper) {
        return element
                .select(filter).stream()
                .map(mapper)
                .collect(Collectors.toList());
    }

}
