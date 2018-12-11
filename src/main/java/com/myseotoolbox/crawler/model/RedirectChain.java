package com.myseotoolbox.crawler.model;

import com.myseotoolbox.crawler.http.HttpResponse;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RedirectChain {

    private final List<HttpResponse> elements = new ArrayList<>();

    public void add(HttpResponse curResponse) {
        elements.add(curResponse);
    }

    public boolean contains(URI location) {
        return elements.stream()
                .anyMatch(element -> element.getLocationHeader().equals(location));
    }

    public List<HttpResponse> getResponses() {
        return Collections.unmodifiableList(elements);
    }


    public HttpResponse getLastResponse() {
        return elements.get(elements.size() - 1);
    }

}
