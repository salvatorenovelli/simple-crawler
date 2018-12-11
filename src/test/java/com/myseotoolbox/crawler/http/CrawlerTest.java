package com.myseotoolbox.crawler.http;


import com.myseotoolbox.crawler.Crawler;
import com.myseotoolbox.crawler.model.WebPage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.net.URI;
import java.util.List;
import java.util.function.Consumer;

import static com.myseotoolbox.crawler.http.TestWebsiteBuilder.givenAWebsite;
import static com.myseotoolbox.crawler.http.RedirectChainScannerTest.el;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;


@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class CrawlerTest {

    public static final URI TEST_WEBSITE_ROOT = URI.create("http://somedomain");
    public static final int ONCE = 1;
    @Mock private Consumer<WebPage> listener;


    @Test
    public void shouldDoBasicCrawling() {

        HttpClient mockClient = givenAWebsite(TEST_WEBSITE_ROOT)
                .withRootPage()
                .havingLinkTo("/page1")
                .havingLinkTo("/page2").build();

        Crawler sut = initCrawler(mockClient);
        sut.run();

        verify(listener).accept(aPageForUri(TEST_WEBSITE_ROOT));
        verify(listener).accept(aPageForUri(TEST_WEBSITE_ROOT.resolve("/page1")));
        verify(listener).accept(aPageForUri(TEST_WEBSITE_ROOT.resolve("/page2")));
    }

    @Test
    public void shouldDoNestedCrawling() {
        HttpClient mockClient = givenAWebsite(TEST_WEBSITE_ROOT)
                .withRootPage()
                .havingLinkTo("/page1")
                .and("/page1")
                .havingLinkTo("/page2").build();

        Crawler sut = initCrawler(mockClient);
        sut.run();

        verify(listener).accept(aPageForUri(TEST_WEBSITE_ROOT));
        verify(listener).accept(aPageForUri(TEST_WEBSITE_ROOT.resolve("/page1")));
        verify(listener).accept(aPageForUri(TEST_WEBSITE_ROOT.resolve("/page2")));
    }

    @Test
    public void shouldVisitExternalLinks() {
        HttpClient mockClient = givenAWebsite(TEST_WEBSITE_ROOT)
                .withRootPage()
                .havingLinkTo("http://another-domain").build();

        Crawler sut = initCrawler(mockClient);
        sut.run();

        verify(listener).accept(aPageForUri(TEST_WEBSITE_ROOT));
        verify(listener).accept(aPageForUri(URI.create("http://another-domain")));
    }

    @Test
    public void shouldNotVisitSamePageMoreThanOnce() {
        HttpClient mockClient = givenAWebsite(TEST_WEBSITE_ROOT)
                .withRootPage()
                .havingLinkTo("/page1")
                .and("/page1")
                .havingLinkTo("/page1").build();

        Crawler sut = initCrawler(mockClient);
        sut.run();

        verify(listener, times(ONCE)).accept(aPageForUri(TEST_WEBSITE_ROOT.resolve("/page1")));
    }

    @Test
    public void shouldNotConsiderFragment() {
        HttpClient mockClient = givenAWebsite(TEST_WEBSITE_ROOT)
                .withRootPage()
                .havingLinkTo("/page1#fragment1")
                .havingLinkTo("/page1#fragment2").build();

        Crawler sut = initCrawler(mockClient);
        sut.run();

        verify(listener).accept(aPageForUri(TEST_WEBSITE_ROOT));
        verify(listener, times(ONCE)).accept(aPageForUri(TEST_WEBSITE_ROOT.resolve("/page1")));
    }

    @Test
    public void shouldConsiderParametersAsDifferentPages() {
        HttpClient mockClient = givenAWebsite(TEST_WEBSITE_ROOT)
                .withRootPage()
                .havingLinkTo("/page1?param=A")
                .havingLinkTo("/page1?param=B").build();

        Crawler sut = initCrawler(mockClient);
        sut.run();

        verify(listener).accept(aPageForUri(TEST_WEBSITE_ROOT));
        verify(listener).accept(aPageForUri(TEST_WEBSITE_ROOT.resolve("/page1?param=A")));
        verify(listener).accept(aPageForUri(TEST_WEBSITE_ROOT.resolve("/page1?param=B")));
    }


    @Test
    public void shouldFilter() {
        HttpClient mockClient = givenAWebsite(TEST_WEBSITE_ROOT)
                .withRootPage()
                .havingLinkTo("/page1")
                .havingLinkTo("/page2").build();


        Crawler sut = new Crawler(listener, new WebPageReader(new RedirectChainScanner(mockClient)), uri -> !uri.getPath().equals("/page1"));
        sut.addSeed(TEST_WEBSITE_ROOT);
        sut.run();


        verify(listener).accept(aPageForUri(TEST_WEBSITE_ROOT));
        verify(listener).accept(aPageForUri(TEST_WEBSITE_ROOT.resolve("/page2")));
        verifyNoMoreInteractions(listener);
    }


    @Test
    public void redirectChainShouldBePresent() {
        HttpClient mockClient = givenAWebsite(TEST_WEBSITE_ROOT)
                .withRootPage().redirectingTo(301, "/dst1")
                .build();

        Crawler sut = initCrawler(mockClient);
        sut.run();


        verify(listener).accept(argThat(webPage -> {
            List<HttpResponse> responses = webPage.getRedirectChain().getResponses();
            assertThat(responses, contains(el(301, "/dst1"), el(200, "/dst1")));
            return true;
        }));


    }

    private WebPage aPageForUri(URI uri) {

        return ArgumentMatchers.argThat(argument -> {
//                    System.out.println("Accepting: " + argument.getLocation());
                    return argument.getSourceUri().equals(uri);
                }
        );
    }


    private Crawler initCrawler(HttpClient mockClient) {

        RedirectChainScanner scanner = new RedirectChainScanner(mockClient);

        Crawler sut = new Crawler(listener, new WebPageReader(scanner), uri -> true);
        sut.addSeed(TEST_WEBSITE_ROOT);
        return sut;
    }
}