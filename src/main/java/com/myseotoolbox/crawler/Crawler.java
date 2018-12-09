package com.myseotoolbox.crawler;

import com.myseotoolbox.crawler.http.HttpResponse;
import com.myseotoolbox.crawler.model.RedirectChain;
import com.myseotoolbox.crawler.http.RedirectChainScanner;
import com.myseotoolbox.crawler.http.RedirectLoopException;
import com.myseotoolbox.crawler.model.WebPage;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
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
    private final Consumer<WebPage> listener;
    private final RedirectChainScanner scanner;
    private final Predicate<URI> shouldVisit;

    public Crawler(Consumer<WebPage> listener, RedirectChainScanner scanner, Predicate<URI> uriFilter) {
        this.listener = listener;
        this.scanner = scanner;
        this.shouldVisit = uriFilter;
    }

    /**
     * Blocking
     */
    public void run() {

        while (!toVisit.isEmpty()) {
            URI curUri = toVisit.poll();

            if (shouldVisit.test(curUri)) {

                RedirectChain chain = visit(curUri);
                Document document = null;

                if (chain.getLastResponse().getHttpStatus() == HttpURLConnection.HTTP_OK) {
                    HttpResponse response = chain.getLastResponse();
                    document = toJsoupDocument(response.getInputStream(), response.getUri());
                    enqueueNewLinks(document);
                }

                listener.accept(new WebPage(curUri, chain, document));
            }
        }

    }

    public void addSeed(URI uri) {
        addUriToQueue(uri);
    }

    private void addUriToQueue(URI uri) {
        toVisit.add(removeFragment(uri));
    }

    private RedirectChain visit(URI uri) {
        try {
            visited.add(uri);
            return scanner.analyseRedirectChain(uri);
        } catch (IOException | RedirectLoopException e) {
            e.printStackTrace();
            //TODO:
            throw new UnsupportedOperationException("Not implemented yet!" + e);
        }
    }

    private void enqueueNewLinks(Document document) {


        List<URI> pageLinks = getOutboundLinks(document);

        pageLinks.stream()
                .map(uri -> toAbsoluteUri(document.baseUri(), uri))
                .map(this::removeFragment)
                .filter(not(this::duplicate))
                .filter(shouldVisit)
                .forEach(this::addUriToQueue);
    }


    private boolean duplicate(URI uri) {
        return visited.contains(uri) || toVisit.contains(uri);
    }

    private URI removeFragment(URI uri) {
        if (uri.getFragment() == null) return uri;
        return URI.create(uri.toASCIIString().split("#")[0]);
    }

    private URI toAbsoluteUri(String requestUri, URI responseLocation) {
        if (responseLocation.isAbsolute()) return responseLocation;
        return URI.create(requestUri).resolve(responseLocation);
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

    private Document toJsoupDocument(InputStream inputStream, URI baseUri) {
        try {
            return Jsoup.parse(inputStream, UTF_8.name(), baseUri.toASCIIString());
        } catch (IOException e) {
            //TODO
            throw new UnsupportedOperationException("Not implemented yet!");
        }
    }
}
