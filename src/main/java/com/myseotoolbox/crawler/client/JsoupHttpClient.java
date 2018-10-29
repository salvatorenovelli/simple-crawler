package com.myseotoolbox.crawler.client;

import com.myseotoolbox.crawler.http.HttpClient;
import com.myseotoolbox.crawler.model.HttpResponse;

import java.net.URI;

public class JsoupHttpClient implements HttpClient<JsoupPage> {
    @Override
    public HttpResponse<JsoupPage> get(URI uri) {
        throw new UnsupportedOperationException("Not implemented yet!");
    }
}
