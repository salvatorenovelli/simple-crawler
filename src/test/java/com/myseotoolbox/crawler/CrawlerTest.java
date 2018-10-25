package com.myseotoolbox.crawler;


import com.myseotoolbox.crawler.http.HttpClient;
import com.myseotoolbox.crawler.http.HttpResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.net.URI;
import java.util.function.Consumer;

import static junit.framework.TestCase.fail;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public class CrawlerTest implements CrawlerUnitTest {

    public static final String TEST_WEBSITE_ROOT = "http://somedomain";
    @Mock private Consumer<HttpResponse> listener;

    Crawler sut;

    @Test
    public void shouldDoBasicCrawling() {
        givenWebsite(TEST_WEBSITE_ROOT)
                .whereTheRootPage()
                .hasLinkTo("/page1")
                .hasLinkTo("/page2").build();

        sut.addSeed(URI.create(TEST_WEBSITE_ROOT));
        sut.run(listener);

        verify(listener).accept(aResponseForUri(TEST_WEBSITE_ROOT));
        verify(listener).accept(aResponseForUri(TEST_WEBSITE_ROOT + "/page1"));
        verify(listener).accept(aResponseForUri(TEST_WEBSITE_ROOT + "/page2"));
    }

    @Test
    public void shouldDoNestedCrawling() {
        givenWebsite(TEST_WEBSITE_ROOT)
                .whereTheRootPage()
                .hasLinkTo("/page1")
                .and("/page1")
                .hasLinkTo("/page2").build();

        sut.addSeed(URI.create(TEST_WEBSITE_ROOT));
        sut.run(listener);

        verify(listener).accept(aResponseForUri(TEST_WEBSITE_ROOT));
        verify(listener).accept(aResponseForUri(TEST_WEBSITE_ROOT + "/page1"));
        verify(listener).accept(aResponseForUri(TEST_WEBSITE_ROOT + "/page2"));
    }

    @Test
    public void shouldVisitExternalLinks() {
        givenWebsite(TEST_WEBSITE_ROOT)
                .whereTheRootPage()
                .hasLinkTo("http://another-domain").build();

        sut.addSeed(URI.create(TEST_WEBSITE_ROOT));
        sut.run(listener);

        verify(listener).accept(aResponseForUri(TEST_WEBSITE_ROOT));
        verify(listener).accept(aResponseForUri("http://another-domain"));
    }

    @Test
    public void shouldNotVisitSamePageMoreThanOnce() {
        givenWebsite(TEST_WEBSITE_ROOT)
                .whereTheRootPage()
                .hasLinkTo("/page1")
                .and("/page1")
                .hasLinkTo("/page1").build();

        sut.addSeed(URI.create(TEST_WEBSITE_ROOT));
        sut.run(listener);

        verify(listener, times(1)).accept(aResponseForUri(TEST_WEBSITE_ROOT + "/page1"));
    }

    @Test
    public void shouldNotConsiderFragment() {
        givenWebsite(TEST_WEBSITE_ROOT)
                .whereTheRootPage()
                .hasLinkTo("/page1#fragment1")
                .hasLinkTo("/page1#fragment2").build();

        sut.addSeed(URI.create(TEST_WEBSITE_ROOT));
        sut.run(listener);

        verify(listener).accept(aResponseForUri(TEST_WEBSITE_ROOT));
        verify(listener, times(1)).accept(aResponseForUri(TEST_WEBSITE_ROOT + "/page1"));
    }

    @Test
    public void shouldConsiderParametersAsDifferentPages() {
        givenWebsite(TEST_WEBSITE_ROOT)
                .whereTheRootPage()
                .hasLinkTo("/page1?param=A")
                .hasLinkTo("/page1?param=B").build();

        sut.addSeed(URI.create(TEST_WEBSITE_ROOT));
        sut.run(listener);

        verify(listener).accept(aResponseForUri(TEST_WEBSITE_ROOT));
        verify(listener).accept(aResponseForUri(TEST_WEBSITE_ROOT + "/page1?param=A"));
        verify(listener).accept(aResponseForUri(TEST_WEBSITE_ROOT + "/page1?param=B"));
    }

    @Override
    public void initializeCrawlerUnderTest(HttpClient client) {
        this.sut = new Crawler(client);
    }

    private HttpResponse aResponseForUri(String uri) {
        return ArgumentMatchers.argThat(argument -> argument.getLocation().equals(URI.create(uri)));
    }

    //parameter domainRoot ignored. Provided for readability
    private TestWebsiteBuilder givenWebsite(String domainRoor) {
        return new TestWebsiteBuilder(this);
    }

}