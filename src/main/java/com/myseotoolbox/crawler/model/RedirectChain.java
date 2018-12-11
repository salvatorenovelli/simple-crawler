package com.myseotoolbox.crawler.model;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class RedirectChain implements Iterable<RedirectChainElement> {

    private final List<RedirectChainElement> elements = new ArrayList<>();

    public void add(RedirectChainElement element) {
        elements.add(element);
    }

    public boolean contains(URI location) {
        return elements.stream()
                .anyMatch(element -> element.getLocationHeader().equals(location) || element.getUri().equals(location));
    }

    public List<RedirectChainElement> getResponses() {
        return Collections.unmodifiableList(elements);
    }

    public int getLastStatus() {
        return getLastElement().getHttpStatus();
    }

    public URI getDestinationUri() {
        return getLastElement().getUri();
    }

    private RedirectChainElement getLastElement() {
        return elements.get(elements.size() - 1);
    }

    @Override
    public Iterator<RedirectChainElement> iterator() {
        return elements.iterator();
    }
}
