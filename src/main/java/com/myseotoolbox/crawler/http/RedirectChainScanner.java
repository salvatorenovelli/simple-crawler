package com.myseotoolbox.crawler.http;


import com.myseotoolbox.crawler.model.RedirectChain;

import java.io.IOException;
import java.net.URI;

import static com.myseotoolbox.utils.HTTPUtils.isRedirect;

public class RedirectChainScanner {
    private final HttpClient client;

    public RedirectChainScanner(HttpClient client) {
        this.client = client;
    }

    public RedirectChain analyseRedirectChain(URI uri) throws RedirectLoopException, IOException {

        RedirectChain curRedirectChain = new RedirectChain();

        URI curUri = uri;
        HttpResponse curResponse;

        do {

            curResponse = client.get(curUri);
            if (isRedirectLoop(curRedirectChain, curUri, curResponse)) throw new RedirectLoopException(curRedirectChain);
            curRedirectChain.add(curResponse);

            curUri = curResponse.getLocationHeader();
        } while (isRedirect(curResponse.getHttpStatus()));

        return curRedirectChain;
    }

    private boolean isRedirectLoop(RedirectChain chain, URI sourceUri, HttpResponse response) {
        return isRedirect(response.getHttpStatus()) &&
                (
                        chain.contains(response.getLocationHeader()) || response.getLocationHeader().equals(sourceUri)
                );
    }


}
