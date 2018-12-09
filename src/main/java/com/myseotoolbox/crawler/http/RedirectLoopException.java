package com.myseotoolbox.crawler.http;

import com.myseotoolbox.crawler.model.RedirectChain;

public class RedirectLoopException extends Exception {

    private final RedirectChain partialChain;

    public RedirectLoopException(RedirectChain partialChain) {
        super("ERROR: Redirect Loop");
        this.partialChain = partialChain;
    }

    public RedirectChain getPartialChain() {
        return partialChain;
    }
}
