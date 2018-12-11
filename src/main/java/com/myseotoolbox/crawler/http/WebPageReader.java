package com.myseotoolbox.crawler.http;

import com.myseotoolbox.crawler.model.RedirectChain;
import com.myseotoolbox.crawler.model.WebPage;

import java.io.IOException;
import java.net.URI;

public class WebPageReader {

    private final RedirectChainScanner scanner;

    public WebPageReader(RedirectChainScanner scanner) {
        this.scanner = scanner;
    }

    public WebPage visit(URI curUri) throws RedirectLoopException, IOException {
        RedirectChain chain = scanner.analyseRedirectChain(curUri);
        return new WebPage(curUri, chain);
    }

}
