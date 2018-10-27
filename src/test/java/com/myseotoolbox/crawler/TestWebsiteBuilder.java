package com.myseotoolbox.crawler;

import com.myseotoolbox.crawler.http.HttpClient;
import com.myseotoolbox.crawler.http.HttpResponse;
import com.myseotoolbox.crawler.model.Page;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class TestWebsiteBuilder {

    private final Map<String, List<String>> links = new HashMap<>();
    private String currentPosition;


    public TestWebsiteBuilder whereTheRootPage() {
        currentPosition = "";
        return this;
    }

    public TestWebsiteBuilder hasLinkTo(String url) {
        getPageLinks(currentPosition).add(url);
        return this;
    }

    public TestWebsiteBuilder and(String s) {
        currentPosition = s;
        return this;
    }

    public HttpClient build() {
        HttpClient mockClient = uri -> {
            List<URI> links = getPageLinks(uri.getPath()).stream().map(URI::create).collect(Collectors.toList());
            Page page = new Page(links);
            return new HttpResponse(uri, 200, uri, page);
        };

        return mockClient;
    }

    private List<String> getPageLinks(String url) {
        return links.computeIfAbsent(url, s -> new ArrayList<>());
    }
}
