package com.myseotoolbox.crawler.http;

import com.google.common.base.Charsets;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestWebsiteBuilder {

    private final Map<String, Page> pages = new HashMap<>();
    private final URI domainRoot;
    private String currentPosition;


    private TestWebsiteBuilder(URI domainRoot) {
        this.domainRoot = domainRoot;
    }

    public static TestWebsiteBuilder givenAWebsite(URI domainRoot) {
        //domainRoot ignored by this implementation of HttpClient. Provided for readability
        return new TestWebsiteBuilder(domainRoot);
    }


    public TestWebsiteBuilder withRootPage() {
        currentPosition = "";
        return this;
    }

    public TestWebsiteBuilder and(String s) {
        currentPosition = s;
        return this;
    }


    public TestWebsiteBuilder havingLinkTo(String url) {
        getPage(currentPosition).addLink(url);
        createEmptyPage(url);
        return this;
    }

    private void createEmptyPage(String url) {
        getPage(url);
    }

    public TestWebsiteBuilder withPage(String s) {
        currentPosition = s;
        return this;
    }

    public HttpClient build() {
        HttpClient mockClient = uri -> {
            Page page = pages.get(uri.getPath());

            if (page != null) {

                String html = new HtmlPageBuilder().body().appendLink(page.links).build();
                InputStream inputStream = IOUtils.toInputStream(html, Charsets.UTF_8);
                return new HttpResponse(uri, page.status, page.location, inputStream);
            } else {
                return new HttpResponse(uri, 404, null, null);
            }
        };

        return mockClient;
    }

    private URI toAbsoluteUri(URI requestUri, URI responseLocation) {
        if (responseLocation.isAbsolute()) return responseLocation;
        return requestUri.resolve(responseLocation);
    }


    private Page getPage(String url) {
        return pages.computeIfAbsent(url, Page::new);
    }

    public TestWebsiteBuilder redirectingTo(int status, String location) {
        getPage(currentPosition).setRedirect(status, location);
        createEmptyPage(location);
        return this;
    }

    public TestWebsiteBuilder withStatus(int status) {
        getPage(currentPosition).setStatus(status);


        return this;
    }


    private class Page {
        public URI location;
        private int status = 200;
        private final List<String> links = new ArrayList<>();

        public Page(String location) {
            this.location = URI.create(location);
        }

        public void setRedirect(int status, String location) {
            this.status = status;
            this.location = URI.create(location);
        }

        public void addLink(String url) {
            this.links.add(url);
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }
}
