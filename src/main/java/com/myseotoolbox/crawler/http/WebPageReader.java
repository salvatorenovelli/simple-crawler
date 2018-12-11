package com.myseotoolbox.crawler.http;

import com.myseotoolbox.crawler.model.RedirectChain;
import com.myseotoolbox.crawler.model.WebPage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;

import static java.nio.charset.StandardCharsets.UTF_8;

public class WebPageReader {

    private final RedirectChainScanner scanner;

    public WebPageReader(RedirectChainScanner scanner) {
        this.scanner = scanner;
    }

    public WebPage visit(URI curUri) throws RedirectLoopException, IOException {
        RedirectChain chain = scanner.analyseRedirectChain(curUri);
        Document document = null;

        if (chain.getLastResponse().getHttpStatus() == HttpURLConnection.HTTP_OK) {
            HttpResponse response = chain.getLastResponse();
            document = toJsoupDocument(response);
        }

        return new WebPage(curUri, chain, document);
    }

    private Document toJsoupDocument(HttpResponse response) throws IOException {
        return Jsoup.parse(response.getInputStream(), UTF_8.name(), response.getUri().toASCIIString());
    }


}
