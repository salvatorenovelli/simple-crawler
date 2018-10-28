package com.myseotoolbox.crawler;


import com.myseotoolbox.crawler.http.HttpClient;
import com.myseotoolbox.crawler.http.HttpResponse;
import com.myseotoolbox.utils.StreamUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.net.URI;
import java.util.function.Consumer;

import static org.mockito.AdditionalMatchers.not;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;


@RunWith(MockitoJUnitRunner.class)
public class CrawlerTest {

    public static final String TEST_WEBSITE_ROOT = "http://somedomain";
    public static final int ONCE = 1;
    @Mock private Consumer<HttpResponse> listener;


    @Test
    public void shouldDoBasicCrawling() {
        HttpClient mockClient = givenWebsite(TEST_WEBSITE_ROOT)
                .whereTheRootPage()
                .hasLinkTo("/page1")
                .hasLinkTo("/page2").build();

        Crawler sut = initCrawler(mockClient);
        sut.run();

        verify(listener).accept(aResponseForUri(TEST_WEBSITE_ROOT));
        verify(listener).accept(aResponseForUri(TEST_WEBSITE_ROOT + "/page1"));
        verify(listener).accept(aResponseForUri(TEST_WEBSITE_ROOT + "/page2"));
    }

    @Test
    public void shouldDoNestedCrawling() {
        HttpClient mockClient = givenWebsite(TEST_WEBSITE_ROOT)
                .whereTheRootPage()
                .hasLinkTo("/page1")
                .and("/page1")
                .hasLinkTo("/page2").build();

        Crawler sut = initCrawler(mockClient);
        sut.run();

        verify(listener).accept(aResponseForUri(TEST_WEBSITE_ROOT));
        verify(listener).accept(aResponseForUri(TEST_WEBSITE_ROOT + "/page1"));
        verify(listener).accept(aResponseForUri(TEST_WEBSITE_ROOT + "/page2"));
    }

    @Test
    public void shouldVisitExternalLinks() {
        HttpClient mockClient = givenWebsite(TEST_WEBSITE_ROOT)
                .whereTheRootPage()
                .hasLinkTo("http://another-domain").build();

        Crawler sut = initCrawler(mockClient);
        sut.run();

        verify(listener).accept(aResponseForUri(TEST_WEBSITE_ROOT));
        verify(listener).accept(aResponseForUri("http://another-domain"));
    }

    @Test
    public void shouldNotVisitSamePageMoreThanOnce() {
        HttpClient mockClient = givenWebsite(TEST_WEBSITE_ROOT)
                .whereTheRootPage()
                .hasLinkTo("/page1")
                .and("/page1")
                .hasLinkTo("/page1").build();

        Crawler sut = initCrawler(mockClient);
        sut.run();

        verify(listener, times(ONCE)).accept(aResponseForUri(TEST_WEBSITE_ROOT + "/page1"));
    }

    @Test
    public void shouldNotConsiderFragment() {
        HttpClient mockClient = givenWebsite(TEST_WEBSITE_ROOT)
                .whereTheRootPage()
                .hasLinkTo("/page1#fragment1")
                .hasLinkTo("/page1#fragment2").build();

        Crawler sut = initCrawler(mockClient);
        sut.run();

        verify(listener).accept(aResponseForUri(TEST_WEBSITE_ROOT));
        verify(listener, times(ONCE)).accept(aResponseForUri(TEST_WEBSITE_ROOT + "/page1"));
    }

    @Test
    public void shouldConsiderParametersAsDifferentPages() {
        HttpClient mockClient = givenWebsite(TEST_WEBSITE_ROOT)
                .whereTheRootPage()
                .hasLinkTo("/page1?param=A")
                .hasLinkTo("/page1?param=B").build();

        Crawler sut = initCrawler(mockClient);
        sut.run();

        verify(listener).accept(aResponseForUri(TEST_WEBSITE_ROOT));
        verify(listener).accept(aResponseForUri(TEST_WEBSITE_ROOT + "/page1?param=A"));
        verify(listener).accept(aResponseForUri(TEST_WEBSITE_ROOT + "/page1?param=B"));
    }


    @Test
    public void shouldFilter() {
        HttpClient mockClient = givenWebsite(TEST_WEBSITE_ROOT)
                .whereTheRootPage()
                .hasLinkTo("/page1")
                .hasLinkTo("/page2").build();

        Crawler sut = new Crawler(listener, mockClient, uri -> !uri.getPath().equals("/page1"));
        sut.addSeed(URI.create(TEST_WEBSITE_ROOT));
        sut.run();


        verify(listener).accept(aResponseForUri(TEST_WEBSITE_ROOT));
        verify(listener).accept(aResponseForUri(TEST_WEBSITE_ROOT + "/page2"));
        verifyNoMoreInteractions(listener);
    }

    private HttpResponse aResponseForUri(String uri) {
        return ArgumentMatchers.argThat(argument -> argument.getLocation().equals(URI.create(uri)));
    }


    private TestWebsiteBuilder givenWebsite(String domainRoot) {
        //domainRoot ignored by this implementation of HttpClient. Provided for readability
        return new TestWebsiteBuilder();
    }

    private Crawler initCrawler(HttpClient mockClient) {
        Crawler sut = new Crawler(listener, mockClient, uri -> true);
        sut.addSeed(URI.create(TEST_WEBSITE_ROOT));
        return sut;
    }
}