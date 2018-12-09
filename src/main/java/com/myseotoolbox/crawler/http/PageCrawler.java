package com.myseotoolbox.crawler.http;

import com.myseotoolbox.crawler.CrawledPage;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class PageCrawler {

    RedirectChainScanner scanner;


    public CrawledPage crawl(URI uri) throws RedirectLoopException, IOException, URISyntaxException {
        List<HttpResponse> redirectChain = scanner.analyseRedirectChain(uri);
        Document document = null;
        return new CrawledPage(uri, null, document);
    }
}
