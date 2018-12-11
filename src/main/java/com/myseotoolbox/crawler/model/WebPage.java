package com.myseotoolbox.crawler.model;

import com.myseotoolbox.crawler.http.HttpResponse;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;


@ToString
@Getter
public class WebPage {


    private final URI sourceUri;
    private final RedirectChain redirectChain;
    @Getter(AccessLevel.NONE) private final Document document;

    public WebPage(URI sourceUri, RedirectChain redirectChain) throws IOException {
        this.sourceUri = sourceUri;
        this.redirectChain = redirectChain;
        this.document = buildDocument(redirectChain);
    }

    private Document buildDocument(RedirectChain chain) throws IOException {

        if (chain.getLastResponse().getHttpStatus() != HttpURLConnection.HTTP_OK) {
            return null;
        }

        HttpResponse response = chain.getLastResponse();

        return Jsoup.parse(response.getInputStream(), UTF_8.name(), response.getSourceUri().toASCIIString());
    }

    public List<URI> getOutboundLinks() {
        if (document == null) return Collections.emptyList();

        URI baseUri = redirectChain.getLastResponse().getSourceUri();

        return extractFromTag(document.body(), "a[href]", element -> element.attr("href"))
                .map(URI::create)
                .map(linkUri -> toAbsoluteUri(baseUri, linkUri))
                .collect(Collectors.toList());
    }


    private URI toAbsoluteUri(URI requestUri, URI responseLocation) {
        if (responseLocation.isAbsolute()) return responseLocation;
        return requestUri.resolve(responseLocation);
    }

    private static Stream<String> extractFromTag(Element element, String filter, Function<Element, String> mapper) {
        return element
                .select(filter).stream()
                .map(mapper);
    }

}
