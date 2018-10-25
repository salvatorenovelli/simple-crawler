package com.myseotoolbox.crawler;


import com.myseotoolbox.crawler.http.HttpClient;
import com.myseotoolbox.crawler.http.HttpResponse;
import com.myseotoolbox.crawler.model.Page;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static junit.framework.TestCase.fail;
import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public class CrawlerTest {

    @Mock private Consumer<HttpResponse> listener;

    @Test
    public void shouldDoBasicCrawling() {

        HttpClient mockClient =
                givenAWebsite()
                        .whereTheRootPage()
                        .hasLinkTo("/link1")
                        .hasLinkTo("/link2").build();


        Crawler sut = new Crawler(mockClient);

        URI base = URI.create("http://somedomain/");
        sut.addSeed(base);

        sut.run(listener);

        System.out.println(Mockito.mockingDetails(listener).printInvocations());


        verify(listener).accept(aResponseForUri("http://somedomain/"));
        verify(listener).accept(aResponseForUri("http://somedomain/link1"));
        verify(listener).accept(aResponseForUri("http://somedomain/link2"));


    }

    @Test
    public void shouldNotConsiderFragment() {
        fail();
    }

    private HttpResponse aResponseForUri(String uri) {
        return ArgumentMatchers.argThat(argument -> argument.getLocation().equals(URI.create(uri)));
    }

    private TestWebsiteBuilder givenAWebsite() {
        return new TestWebsiteBuilder();
    }

    private static class TestWebsiteBuilder {

        private final Map<String, List<String>> links = new HashMap<>();
        private String currentPosition;

        public TestWebsiteBuilder whereTheRootPage() {
            currentPosition = "/";
            return this;
        }

        public TestWebsiteBuilder hasLinkTo(String url) {
            getPageLinks(currentPosition).add(url);
            return this;
        }

        public HttpClient build() {
            return uri -> {
                List<URI> collect = getPageLinks(uri.getPath()).stream().map(URI::create).collect(Collectors.toList());
                Page page = new Page(collect);
                return new HttpResponse(uri, 200, uri, page);
            };
        }

        private List<String> getPageLinks(String url) {
            return links.computeIfAbsent(url, s -> new ArrayList<>());
        }

    }
}