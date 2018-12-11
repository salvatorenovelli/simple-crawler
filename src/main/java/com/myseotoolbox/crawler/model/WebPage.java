package com.myseotoolbox.crawler.model;

import lombok.Getter;
import lombok.ToString;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@ToString
@Getter
public class WebPage {


    private final URI sourceUri;
    private final RedirectChain redirectChain;
    private final Document document;

    public WebPage(URI sourceUri, RedirectChain redirectChain, Document document) {
        this.sourceUri = sourceUri;
        this.redirectChain = redirectChain;
        this.document = document;
    }

    public Optional<Document> getDocument() {
        return Optional.ofNullable(document);
    }


    private URI toAbsoluteUri(URI requestUri, URI responseLocation) {
        if (responseLocation.isAbsolute()) return responseLocation;
        return requestUri.resolve(responseLocation);
    }

    public List<URI> getOutboundLinks() {

        if (document == null) return Collections.emptyList();
        URI baseUri = redirectChain.getLastResponse().getUri();

        return extractFromTag(document.body(), "a[href]", element -> element.attr("href"))
                .map(URI::create)
                .map(linkUri -> toAbsoluteUri(baseUri, linkUri))
                .collect(Collectors.toList());

    }

    private static Stream<String> extractFromTag(Element element, String filter, Function<Element, String> mapper) {
        return element
                .select(filter).stream()
                .map(mapper);
    }


}
