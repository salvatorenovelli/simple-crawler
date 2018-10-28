package com.myseotoolbox.crawler.client;

import com.myseotoolbox.crawler.HtmlPageBuilder;
import org.jsoup.Jsoup;
import org.junit.Test;

import java.net.URI;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;


public class JsoupPageTest {

    @Test
    public void shouldExtractOutboundLinks() {


        String html = new HtmlPageBuilder().body()
                .appendLink("/page1")
                .appendLink("/page2")
                .build();

        JsoupPage sut = new JsoupPage(Jsoup.parse(html));

        assertThat(sut.getOutboundLinks(), contains(URI.create("/page1"), URI.create("/page2")));

    }
}