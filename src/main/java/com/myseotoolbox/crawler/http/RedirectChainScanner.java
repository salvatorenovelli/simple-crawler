package com.myseotoolbox.crawler.http;


import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static com.myseotoolbox.crawler.http.HTTPUtils.isRedirect;

public class RedirectChainScanner {
    private final HttpClient client;

    public RedirectChainScanner(HttpClient client) {
        this.client = client;
    }

    public List<HttpResponse> analyseRedirectChain(URI uri) throws RedirectLoopException, IOException, URISyntaxException {

        List<HttpResponse> curRedirectChain = new ArrayList<>();

        URI curUri = uri;
        HttpResponse curResponse;

        do {
            curResponse = client.get(curUri);
            if (isRedirectLoop(curRedirectChain, curUri, curResponse)) throw new RedirectLoopException();
            curRedirectChain.add(curResponse);
            curUri = curResponse.getLocation();
        } while (isRedirect(curResponse.getHttpStatus()));

        return curRedirectChain;
    }

    private boolean isRedirectLoop(List<HttpResponse> chain, URI sourceUri, HttpResponse response) {
        return isRedirect(response.getHttpStatus()) &&
                (
                        alreadyExistInTheChain(chain, response) || response.getLocation().equals(sourceUri)
                );
    }

    private boolean alreadyExistInTheChain(List<HttpResponse> chain, HttpResponse response) {
        return chain.stream()
                .anyMatch(element -> element.getLocation().equals(response.getLocation()));
    }

}
