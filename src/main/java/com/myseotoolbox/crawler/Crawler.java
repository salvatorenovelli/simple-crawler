package com.myseotoolbox.crawler;

import com.myseotoolbox.crawler.http.HttpClient;
import com.myseotoolbox.crawler.http.HttpResponse;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.myseotoolbox.utils.StreamUtils.not;
import static java.nio.charset.StandardCharsets.UTF_8;


@Slf4j
public class Crawler {

    private final Queue<URI> toVisit = new LinkedList<>();
    private final Set<URI> visited = new HashSet<>();
    private final Consumer<CrawledPage> listener;
    private final HttpClient httpClient;
    private final Predicate<URI> shouldVisit;

    public Crawler(Consumer<CrawledPage> listener, HttpClient httpClient, Predicate<URI> uriFilter) {
        this.listener = listener;
        this.httpClient = httpClient;
        this.shouldVisit = uriFilter;
    }

    /**
     * Blocking!
     */
    public void run() {

        while (!toVisit.isEmpty()) {
            URI curUri = toVisit.poll();

            if (shouldVisit.test(curUri)) {
                HttpResponse response = visit(curUri);
                enqueueNewLinks(curUri, response);
                listener.accept(new CrawledPage(curUri, null, null));
            }
        }

    }

    public void addSeed(URI uri) {
        addUriToQueue(uri);
    }

    private void addUriToQueue(URI uri) {
        toVisit.add(removeFragment(uri));
    }

    private HttpResponse visit(URI uri) {
        visited.add(uri);
        try {
            return httpClient.get(uri);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            throw new UnsupportedOperationException("Not implemented yet!");
        }
    }

    private void enqueueNewLinks(URI curUri, HttpResponse httpResponse) {

        if (httpResponse.getHttpStatus() == HttpStatus.SC_OK) {

            List<URI> pageLinks = getLinks(httpResponse);

            pageLinks.stream()
                    .map(uri -> toAbsoluteUri(curUri, uri))
                    .map(this::removeFragment)
                    .filter(not(this::duplicate))
                    .filter(shouldVisit)
                    .forEach(this::addUriToQueue);
        }

    }

    private boolean duplicate(URI uri) {
        return visited.contains(uri) || toVisit.contains(uri);
    }

    private URI removeFragment(URI uri) {
        if (uri.getFragment() == null) return uri;
        return URI.create(uri.toASCIIString().split("#")[0]);
    }

    private URI toAbsoluteUri(URI requestUri, URI responseLocation) {
        if (responseLocation.isAbsolute()) return responseLocation;
        return requestUri.resolve(responseLocation);
    }

    private List<URI> getLinks(HttpResponse httpResponse) {

        try {
            Document document = toJsoupDocument(httpResponse.getInputStream(), httpResponse.getLocation().toASCIIString());
            return getOutboundLinks(document);

        } catch (IOException e) {
            //As we have already checked getHttpStatus, we should not get here
            log.error("Error while getting links from {}", httpResponse);
            return Collections.emptyList();
        }
    }


    private List<URI> getOutboundLinks(Document document) {
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

    private Document toJsoupDocument(InputStream inputStream, String baseUri) throws IOException {
        return Jsoup.parse(inputStream, UTF_8.name(), baseUri);
    }
}
