package com.myseotoolbox.crawler.http;

import com.myseotoolbox.crawler.model.HttpResponse;

import java.net.URI;

public interface HttpClient {
    HttpResponse get(URI uri);
}
