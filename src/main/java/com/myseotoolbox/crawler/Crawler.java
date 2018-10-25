package com.myseotoolbox.crawler;

import com.myseotoolbox.crawler.http.HttpClient;
import com.myseotoolbox.crawler.http.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;

import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.function.Consumer;

import static com.myseotoolbox.utils.StreamUtils.not;


@Slf4j
public class Crawler {


    private final Queue<URI> queue = new LinkedList<>();
    private final Set<URI> visited = new HashSet<>();
    private final HttpClient httpClient;

    public Crawler(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * Blocking!
     */
    public void run(Consumer<HttpResponse> listener) {

        while (!queue.isEmpty()) {
            URI curUri = queue.poll();

            if (!alreadyVisited(curUri)) {
                HttpResponse response = visit(curUri);

                discoverNewLinks(response);
                listener.accept(response);
            }
        }

    }

    public void addSeed(URI uri) {
        addUriToQueue(uri);
    }

    private void addUriToQueue(URI uri) {
        queue.add(removeFragment(uri));
    }

    private HttpResponse visit(URI uri) {
        visited.add(uri);
        return httpClient.get(uri);
    }

    private void discoverNewLinks(HttpResponse httpResponse) {

        if (httpResponse.getHttpStatus() == HttpStatus.SC_OK) {

            List<URI> pageLinks = getLinks(httpResponse);

            pageLinks.stream()
                    .map(uri -> toAbsoluteUri(httpResponse.getRequestUri(), uri))
                    .map(this::removeFragment)
                    .filter(not(this::alreadyVisited))
                    .forEach(this::addUriToQueue);
        }

    }

    private boolean alreadyVisited(URI uri) {
        return visited.contains(uri);
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
