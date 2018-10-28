package com.myseotoolbox.crawler;

import com.myseotoolbox.crawler.http.HttpClient;
import com.myseotoolbox.crawler.http.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;

import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static com.myseotoolbox.utils.StreamUtils.not;


@Slf4j
public class Crawler {

    private final Queue<URI> toVisit = new LinkedList<>();
    private final Set<URI> visited = new HashSet<>();
    private final Consumer<HttpResponse> listener;
    private final HttpClient httpClient;
    private final Predicate<URI> shouldVisit;

    public Crawler(Consumer<HttpResponse> listener, HttpClient httpClient, Predicate<URI> shouldVisit) {
        this.listener = listener;
        this.httpClient = httpClient;
        this.shouldVisit = shouldVisit;
    }

    /**
     * Blocking!
     */
    public void run() {

        while (!toVisit.isEmpty()) {
            URI curUri = toVisit.poll();

            if (shouldVisit.test(curUri)) {
                HttpResponse response = visit(curUri);

                enqueueNewLinks(response);
                listener.accept(response);
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
        return httpClient.get(uri);
    }

    private void enqueueNewLinks(HttpResponse httpResponse) {

        if (httpResponse.getHttpStatus() == HttpStatus.SC_OK) {

            List<URI> pageLinks = getLinks(httpResponse);

            pageLinks.stream()
                    .map(uri -> toAbsoluteUri(httpResponse.getRequestUri(), uri))
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
            return httpResponse.getPage().getOutboundLinks();
        } catch (IOException e) {
            //As we have already checked getHttpStatus, we should not get here unless
            log.error("Error while getting links from {}", httpResponse);
            return Collections.emptyList();
        }
    }
}
