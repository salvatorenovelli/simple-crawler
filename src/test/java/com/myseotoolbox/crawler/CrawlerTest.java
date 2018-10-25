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
    public void shouldManageExternalLinks() {
        givenWebsite(TEST_WEBSITE_ROOT)
                .whereTheRootPage()
                .hasLinkTo("http://another-domain").build();

        sut.addSeed(URI.create(TEST_WEBSITE_ROOT));
        sut.run(listener);

        verify(listener).accept(aResponseForUri(TEST_WEBSITE_ROOT));
        verify(listener).accept(aResponseForUri("http://another-domain"));
    }

    @Test
    public void shouldVisitSamePageMoreThanOnce() {
        givenWebsite(TEST_WEBSITE_ROOT)
                .whereTheRootPage()
                .hasLinkTo("/page1")
                .and("/page1")
                .hasLinkTo("/page2").build();
    }

    @Test
    public void shouldNotConsiderFragment() {
        fail();
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