package com.myseotoolbox.crawler.http;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static com.myseotoolbox.crawler.TestWebsiteBuilder.givenAWebsite;
import static junit.framework.TestCase.fail;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

@SuppressWarnings("unchecked")
public class RedirectChainScannerTest {


    private final URI TEST_WEBSITE_ROOT = URI.create("http://somehost");

    @Test
    public void shouldHandleSimpleRedirect() throws RedirectLoopException, IOException, URISyntaxException {


        HttpClient mockClient = givenAWebsite(TEST_WEBSITE_ROOT)
                .withRootPage().redirectingTo(301, "/dst1")
                .build();

        RedirectChainScanner sut = new RedirectChainScanner(mockClient);

        List<HttpResponse> responses = sut.analyseRedirectChain(TEST_WEBSITE_ROOT);

        assertThat(responses, contains(el(301, "/dst1"), el(200, "/dst1")));

    }

    @Test
    public void handleMultipleRedirect() throws Exception {

        HttpClient mockClient = givenAWebsite(TEST_WEBSITE_ROOT)
                .withRootPage().redirectingTo(301, "/dst1")
                .withPage("/dst1").redirectingTo(302, "/dst2")
                .withPage("/dst2").redirectingTo(301, "/dst3")
                .withPage("/dst3")
                .build();

        RedirectChainScanner sut = new RedirectChainScanner(mockClient);

        List<HttpResponse> responses = sut.analyseRedirectChain(TEST_WEBSITE_ROOT);

        assertThat(responses, contains(
                el(301, "/dst1"),
                el(302, "/dst2"),
                el(301, "/dst3"),
                el(200, "/dst3")));
    }


    @Test
    public void pageNotFound404ShouldBeHandled() throws Exception {

        HttpClient mockClient = givenAWebsite(TEST_WEBSITE_ROOT)
                .withPage("/page").redirectingTo(301, "/dst1")
                .withPage("/dst1").withStatus(404)
                .build();

        RedirectChainScanner sut = new RedirectChainScanner(mockClient);

        List<HttpResponse> responses = sut.analyseRedirectChain(TEST_WEBSITE_ROOT.resolve("/page"));

        assertThat(responses, contains(
                el(301, "/dst1"),
                el(404, "/dst1")));


    }

    @Test
    public void redirectLoopShouldBeHandledGracefully() throws Exception {

        HttpClient mockClient = givenAWebsite(TEST_WEBSITE_ROOT)
                .withPage("/start").redirectingTo(301, "/dst1")
                .withPage("/dst1").redirectingTo(301, "/dst2")
                .withPage("/dst2").redirectingTo(301, "/start")
                .build();

        RedirectChainScanner sut = new RedirectChainScanner(mockClient);

        try {
            sut.analyseRedirectChain(TEST_WEBSITE_ROOT.resolve("/start"));
            fail("Exception not thrown");
        } catch (RedirectLoopException e) {
            //success!
        }

    }

    private Matcher<HttpResponse> el(int status, String uri) {

        return new BaseMatcher<HttpResponse>() {
            @Override
            public boolean matches(Object item) {
                HttpResponse element = (HttpResponse) item;
                return element.getHttpStatus() == status && URI.create(uri).equals(element.getLocation());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("{ " + status + ", '" + uri + "' }");
            }
        };
    }
}